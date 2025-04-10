package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idCount = 0;
    HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private final boolean canStartAndEndTimesMatch = true; //true - если старт одной задачи и финиш другой могут совпадать

    public <T extends Task> int getNewId(T task) throws ManagerAddTaskException {
        if ((task != null) && (task.getId() != null)) {
            if (isExistsId(task.getId())) {
                //надо прервать, не должны подавать таск с существующим id
                throw new ManagerAddTaskException("Задача с таким id уже существует");
            }
        }
        idCount++;
        while (isExistsId(idCount)) {
            idCount++;
        }
        return idCount;
    }

    private boolean isExistsId(int id) {
        return subtasks.containsKey(id) || epics.containsKey(id) || tasks.containsKey(id);
    }

    @Override
    public void addTask(Task task) throws ManagerAddTaskException {
        if (isTimeIntersections(task)) {
            throw new ManagerAddTaskException(String.format("С %s по %s уже есть задача для исполнения",
                    task.getStartTime(),
                    task.getEndTime()));
        }
        int id = getNewId(task);
        task.setId(id);
        Task saveTask = task.cloneTask();
        tasks.put(id, saveTask);
        addToPrioritizedTask(saveTask);
    }

    @Override
    public void addEpic(Epic epic) throws ManagerAddTaskException {
        int id = getNewId(epic);
        epic.setId(id);
        Epic saveEpic = epic.cloneTask();
        epics.put(id, saveEpic);
        updateEpicStatus(saveEpic);
    }

    @Override
    public void addSubtask(Subtask subtask, Epic epic) throws ManagerAddTaskException {
        if (isTimeIntersections(subtask)) {
            throw new ManagerAddTaskException(String.format("С %s по %s уже есть задача для исполнения",
                    subtask.getStartTime(),
                    subtask.getEndTime()));
        }
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic != null) {
            int id = getNewId(subtask);
            subtask.setId(id);
            subtask.setEpic(epic);
            epic.addSubtask(subtask);
            updateEpicStatus(epic);
            Subtask saveSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getStartTime(), subtask.getDuration());
            saveSubtask.setId(id);
            saveSubtask.setEpic(savedEpic);
            savedEpic.addSubtask(saveSubtask);
            subtasks.put(id, saveSubtask);
            updateEpicStatus(savedEpic);
            addToPrioritizedTask(saveSubtask);
        }
    }

    private void addToPrioritizedTask(Task task) {
        if (task.getStartTime() != null) prioritizedTasks.add(task);
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.add(task);
            return task.cloneTask();
        }
        return null;
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.add(epic);
            return epic.cloneTask();
        }
        return null;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            historyManager.add(subtask);
            return cloneEpicBySubtask(subtask).getSubtasks().get(id);
        }
        return null;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> list = new ArrayList<>();
        for (Integer id : tasks.keySet()) {
            list.add(tasks.get(id).cloneTask()); //возращаем копию объекта
        }
        return list;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> list = new ArrayList<>();
        for (Integer id : epics.keySet()) {
            list.add(epics.get(id).cloneTask()); //возращаем копию объекта
        }
        return list;
    }

    private Epic cloneEpicBySubtask(Subtask subtask) {
        Integer epicId = subtask.getEpic().getId();
        return epics.get(epicId).cloneTask();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return subtasks.values().stream()
                .map(subtask ->
                        cloneEpicBySubtask(subtask).getSubtasks().get(subtask.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.get(id);
        removeFromPrioritizedTasks(task);
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        historyManager.remove(id);
        final Epic epic = epics.get(id);
        if (epic == null) return;
        epics.remove(id);
        final HashMap<Integer, Subtask> epicSubtasks = epic.getSubtasks();
        for (int key : epicSubtasks.keySet()) {
            historyManager.remove(key);
            subtasks.remove(key);
        }

    }

    @Override
    public void deleteSubtaskById(int id) {
        historyManager.remove(id);
        Subtask subtask = subtasks.get(id);
        removeFromPrioritizedTasks(subtask);
        Epic epic = subtask.getEpic();
        subtasks.remove(id);
        if (epic == null) {
            return;
        }
        epic.getSubtasks().remove(id);
        updateEpicStatus(epic);
    }

    private void removeFromPrioritizedTasks(Task task) {
        if (task == null) return;
        if (task.getStartTime() != null) prioritizedTasks.remove(task);
    }

    @Override
    public void deleteAllTasks() {
        removeAllFromPrioritizedTask(tasks);
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
    }

    @Override
    public void deleteAllSubtasks() {
        removeAllFromPrioritizedTask(subtasks);
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        }
    }

    private <T extends Task> void removeAllFromPrioritizedTask(HashMap<Integer, T> taskMap) {
        for (Task task : taskMap.values()) {
            removeFromPrioritizedTasks(task);
        }
    }

    @Override
    public void updateTask(Task task) throws ManagerAddTaskException {
        if (isTimeIntersections(task)) {
            throw new ManagerAddTaskException(String.format("С %s по %s уже есть задача для исполнения",
                    task.getStartTime(),
                    task.getEndTime()));
        }
        final int id = task.getId();
        if (tasks.containsKey(id)) {
            removeFromPrioritizedTasks(tasks.get(id));
            tasks.get(id).updateFrom(task);
            addToPrioritizedTask(tasks.get(id));
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
    public void updateSubtask(Subtask subtask) throws ManagerAddTaskException {
        if (isTimeIntersections(subtask)) {
            throw new ManagerAddTaskException(String.format("С %s по %s уже есть задача для исполнения",
                    subtask.getStartTime(),
                    subtask.getEndTime()));
        }
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
        if (subtasks.isEmpty()) {
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
    public List<Subtask> getSubtaskByEpic(Epic epic) {
        if (epic == null) return new ArrayList<>();
        return epic.cloneTask().getSubtasks()
                .values()
                .stream()
                .toList();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream()
                .map(Task::cloneTask)
                .toList();
    }

    public <T extends Task> boolean isTimeIntersections(T task) {
        LocalDateTime startTime = task.getStartTime();
        if (startTime == null) return false;
        LocalDateTime endTime = task.getEndTime();
        return (getPrioritizedTasks().stream().filter(t -> {
            if (t.equals(task)) return false; //самого себя не проверяем
            if (t.getStartTime() == null) return false;
            if (t.getStartTime().isAfter(endTime)) return false;
            if ((canStartAndEndTimesMatch) && (t.getStartTime().equals(endTime)))
                return false;
            if (t.getEndTime().isBefore(startTime)) return false;
            if ((canStartAndEndTimesMatch) && (t.getEndTime().equals(startTime)))
                return false;
            return true;
        }).count() != 0);
    }
}
