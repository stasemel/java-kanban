package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    void addTask(Task task) throws ManagerAddTaskException;

    void addEpic(Epic epic) throws ManagerAddTaskException;

    void addSubtask(Subtask subtask, Epic epic) throws ManagerAddTaskException;

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    void updateTask(Task task) throws ManagerAddTaskException;

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask) throws ManagerAddTaskException;

    List<Subtask> getSubtaskByEpic(Epic epic);

    ArrayList<Task> getHistory();

    List<Task> getPrioritizedTasks();
    <T extends Task> boolean isTimeIntersections(T task);
}
