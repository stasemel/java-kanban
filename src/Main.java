import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        populate(taskManager);
        testViews(taskManager);
        printAllTasks(taskManager);
    }

    private static void populate(TaskManager manager) {
        final int taskNumber = 5;
        final int epicNumber = 4;
        final int subtaskNamber = 3;
        for (int i = 0; i < taskNumber; i++) {
            Task task = new Task("Task No " + (i + 1), "Описание таска No " + (i + 1), TaskStatus.NEW);
            manager.addTask(task);
        }
        for (int i = 0; i < epicNumber; i++) {
            Epic epic = new Epic("Epic No " + (i + 1), "Описание эпика No " + (i + 1), TaskStatus.NEW);
            manager.addEpic(epic);
            for (int j = 0; j < subtaskNamber; j++) {
                Subtask subtask = new Subtask("Subtask No " + (j + 1) + " эпика " + epic.getId(),
                        "Описание сабтаска " + (j + 1) + " эпика " + epic.getId(),
                        TaskStatus.NEW);
                manager.addSubtask(subtask, epic);
            }
        }
    }

    private static void testViews(TaskManager manager) {
        //просмотр четных тасков
        for (Task task : manager.getAllTasks()) {
            if (task.getId() % 2 == 0) {
                manager.getTaskById(task.getId());
            }
        }
        //просмотр четных эпиков
        for (Epic epic : manager.getAllEpics()) {
            if (epic.getId() % 2 == 0) {
                manager.getEpicById(epic.getId());
            }
        }
        //просмотр каждого 3-го сабтасков
        for (Subtask subtask : manager.getAllSubtasks()) {
            if (subtask.getId() % 3 == 0) {
                manager.getSubtaskById(subtask.getId());
            }
        }
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Subtask task : manager.getSubtaskByEpic(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
