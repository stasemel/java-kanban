package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private static int idCount = 0;

    public static int getNewId() {
        idCount++;
        return idCount;
    }

    public void addTask(Task task) {
        int id = getNewId();
        task.setId(id);
        Task saveTask = task.cloneTask();
        tasks.put(id, saveTask);
    }

    public void addEpic(Epic epic) {
        int id = getNewId();
        epic.setId(id);
        Epic saveEpic = epic.cloneTask();
        epics.put(id, saveEpic);
        updateEpicStatus(saveEpic);
    }

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

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id).cloneTask();
        }
        return null;
    }

    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id).cloneTask();
        }
        return null;
    }

    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id).cloneTask();
        }
        return null;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> list = new ArrayList<>();
        for (Integer id : tasks.keySet()) {
            list.add(tasks.get(id).cloneTask()); //возращаем копию объекта
        }
        return list;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> list = new ArrayList<>();
        for (Integer id : epics.keySet()) {
            list.add(epics.get(id).cloneTask()); //возращаем копию объекта
        }
        return list;
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> list = new ArrayList<>();
        for (Integer id : subtasks.keySet()) {
            list.add(subtasks.get(id).cloneTask()); //возращаем копию объекта
        }
        return list;
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        final Epic epic = epics.get(id);
        if (epic == null) return;
        epics.remove(id);
        final HashMap<Integer, Subtask> epicSubtasks = epic.getSubtasks();
        for (int key : epicSubtasks.keySet()) {
            subtasks.remove(key);
        }

    }

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

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (int key : epics.keySet()) {
            epics.get(key).getSubtasks().clear();
            updateEpicStatus(epics.get(key));
        }
    }

    public void updateTask(Task task) {
        final int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.get(id).updateFrom(task);
        }
    }

    public void updateEpic(Epic epic) {
        final int id = epic.getId();
        if (epics.containsKey(id)) {
            epics.get(id).updateFrom(epic);
            updateEpicStatus(epics.get(id));
        }
    }

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

    public ArrayList<Subtask> getSubtaskByEpic(Epic epic) {
        ArrayList<Subtask> list = new ArrayList<>();
        if (epic == null) return list;
        for (int key : epic.getSubtasks().keySet()) {
            list.add(epic.getSubtasks().get(key).cloneTask());
        }
        return list;
    }
}
