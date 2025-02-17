package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private static int idCount = 0;
    HistoryManager historyManager = Managers.getDefaultHistory();

    public static int getNewId() {
        idCount++;
        return idCount;
    }

    @Override
    public void addTask(Task task) {
        int id = getNewId();
        task.setId(id);
        Task saveTask = task.cloneTask();
        tasks.put(id, saveTask);
    }

    @Override
    public void addEpic(Epic epic) {
        int id = getNewId();
        epic.setId(id);
        Epic saveEpic = epic.cloneTask();
        epics.put(id, saveEpic);
        updateEpicStatus(saveEpic);
    }

    @Override
    public void addSubtask(Subtask subtask, Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic != null) {
            int id = getNewId();
            subtask.setId(id);
            subtask.setEpic(epic);
            epic.addSubtask(subtask);
            updateEpicStatus(epic);
            Subtask saveSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus());
            saveSubtask.setId(id);
            saveSubtask.setEpic(savedEpic);
            savedEpic.addSubtask(saveSubtask);
            subtasks.put(id, saveSubtask);
            updateEpicStatus(savedEpic);
        }
    }

    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.add(task);
            return task.cloneTask();
        }
        return null;
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic=epics.get(id);
            historyManager.add(epic);
            return epic.cloneTask();
        }
        return null;
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask=subtasks.get(id);
            historyManager.add(subtask);
            return subtask.cloneTask();
        }
        return null;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> list = new ArrayList<>();
        for (Integer id : tasks.keySet()) {
            list.add(getTask(id)); //возращаем копию объекта
        }
        return list;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> list = new ArrayList<>();
        for (Integer id : epics.keySet()) {
            list.add(getEpic(id)); //возращаем копию объекта
        }
        return list;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> list = new ArrayList<>();
        for (Integer id : subtasks.keySet()) {
            list.add(getSubtask(id)); //возращаем копию объекта
        }
        return list;
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        final Epic epic = epics.get(id);
        if (epic == null) return;
        epics.remove(id);
        final HashMap<Integer, Subtask> epicSubtasks = epic.getSubtasks();
        for (int key : epicSubtasks.keySet()) {
            subtasks.remove(key);
        }

    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = subtask.getEpic();
        subtasks.remove(id);
        if (epic == null) {
            return;
        }
        epic.getSubtasks().remove(id);
        updateEpicStatus(epic);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (int key : epics.keySet()) {
            epics.get(key).getSubtasks().clear();
            updateEpicStatus(epics.get(key));
        }
    }

    @Override
    public void updateTask(Task task) {
        final int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.get(id).updateFrom(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        final int id = epic.getId();
        if (epics.containsKey(id)) {
            epics.get(id).updateFrom(epic);
            updateEpicStatus(epics.get(id));
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        final int id = subtask.getId();
        if (subtasks.containsKey(id)) {
            subtasks.get(id).updateFrom(subtask);
            updateEpicStatus(subtasks.get(id).getEpic());
            updateEpicStatus(subtask.getEpic());
        }
    }

    private void updateEpicStatus(Epic epic) {
        if (epic == null) return;
        HashMap<Integer, Subtask> subtasks = epic.getSubtasks();
        if (subtasks.size() == 0) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        int doneCount = 0;
        int newCount = 0;
        for (int key : subtasks.keySet()) {
            switch (subtasks.get(key).getStatus()) {
                case NEW:
                    newCount++;
                    break;
                case DONE:
                    doneCount++;
                    break;
                default:
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                    return;
            }
        }
        if (doneCount == subtasks.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else if (newCount == subtasks.size()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                "tasks=" + tasks +
                ", epics=" + epics +
                ", subtasks=" + subtasks +
                '}';
    }

    @Override
    public ArrayList<Subtask> getSubtaskByEpic(Epic epic) {
        ArrayList<Subtask> list = new ArrayList<>();
        if (epic == null) return list;
        for (int key : epic.getSubtasks().keySet()) {
            list.add(getSubtask(key));
        }
        return list;
    }

}
