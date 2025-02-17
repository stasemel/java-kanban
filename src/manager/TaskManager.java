package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;

public interface TaskManager {
    public void addTask(Task task);

    public void addEpic(Epic epic);

    public void addSubtask(Subtask subtask, Epic epic);

    public Task getTask(int id);

    public Epic getEpic(int id);

    public Subtask getSubtask(int id);

    public ArrayList<Task> getAllTasks();

    public ArrayList<Epic> getAllEpics();

    public ArrayList<Subtask> getAllSubtasks();

    public void deleteTaskById(int id);

    public void deleteEpicById(int id);

    public void deleteSubtaskById(int id);

    public void deleteAllTasks();

    public void deleteAllEpics();

    public void deleteAllSubtasks();

    public void updateTask(Task task);

    public void updateEpic(Epic epic);

    public void updateSubtask(Subtask subtask);

    public ArrayList<Subtask> getSubtaskByEpic(Epic epic);

}
