package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    FileBackedTaskManager manager;
    File file;

    private void addNewEpicsForTests(int count) {
        for (int i = 0; i < count; i++) {
            Epic epic = new Epic(String.format("Эпик No %d", i + 1), String.format("Описание эпика No %d", i + 1),
                    TaskStatus.NEW);
            manager.addEpic(epic);
        }
    }

    private void addSubtasksForTests(int count, Epic epic, TaskStatus status) {
        for (int i = 0; i < count; i++) {
            Subtask subtask = new Subtask(String.format("Subtask No %d", i + 1),
                    String.format("Описание сабтаска No %d", i + 1),
                    status);
            manager.addSubtask(subtask, epic);
        }
    }

    private void addThreeNewDifferentTasksForTests() {
        Task task1 = new Task("Первый таск", "Создать новую задачу", TaskStatus.NEW);
        Task task2 = new Task("Второй таск", "Создать вторую новую задачу", TaskStatus.IN_PROGRESS);
        Task task3 = new Task("Третий таск", "Создать третью новую задачу", TaskStatus.DONE);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
    }

    private ArrayList<String> readFile() {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file, FileBackedTaskManager.CS))) {
            while (br.ready()) {
                String line = br.readLine();
                if ((!line.isEmpty()) && (!line.isBlank())) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("manager", ".csv");
        manager = new FileBackedTaskManager(file);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addTask() {
        Task task = new Task("Таск 1", "Описание 1", TaskStatus.NEW);
        manager.addTask(task);
        ArrayList<String> lines = readFile();
        assertEquals(task, manager.getAllTasks().getFirst(), "Не добавлен таск");
        assertTrue(file.exists(), "Файл не создался");
        assertEquals(2, lines.size(), "Не все строки с таск добавлены в файл");
    }

    @Test
    void addEpic() {
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.NEW);
        manager.addEpic(epic);
        ArrayList<String> lines = readFile();
        assertEquals(epic, manager.getAllEpics().getFirst(), "Не добавлен эпик");
        assertTrue(file.exists(), "Файл не создался");
        assertEquals(2, lines.size(), "Не все строки с эпик добавлены в файл");
    }

    @Test
    void addSubtask() {
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.NEW);
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Сабтаск 1", "Описание сабтаска 1", TaskStatus.NEW);
        manager.addSubtask(subtask1, epic);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Описание сабтаска 2", TaskStatus.NEW);
        manager.addSubtask(subtask2, epic);
        ArrayList<String> lines = readFile();
        //System.out.println(file.getAbsolutePath());
        assertEquals(epic, manager.getAllEpics().getFirst(), "Не добавлен эпик");
        assertEquals(subtask1, manager.getAllSubtasks().getFirst(), "Не добавлен сабтаск 1");
        assertEquals(2, epic.getSubtasks().size(), "Добавлены не все сабтаски к эпику");
        assertEquals(2, manager.getAllSubtasks().size(), "Не добавлены все сабтаски в менеджер");
        assertTrue(file.exists(), "Файл не создался");
        assertEquals(4, lines.size(), "Не все строки с эпиком и сабтасками добавлены в файл");
    }

    @Test
    void deleteTaskById() {
        addThreeNewDifferentTasksForTests();
        ArrayList<String> lines = readFile();
        manager.deleteTaskById(1);
        ArrayList<String> linesAfterDelete = readFile();
        assertEquals(2, manager.getAllTasks().size(), "Не корректная работа deleteTaskById");
        assertEquals(linesAfterDelete.size() + 1, lines.size(), "Не удален таск из файла");
    }

    @Test
    void deleteEpicById() {
        addNewEpicsForTests(3);
        ArrayList<String> lines = readFile();
        manager.deleteEpicById(1);
        ArrayList<String> linesAfterDelete = readFile();
        assertEquals(2, manager.getAllEpics().size(), "Не корреектная работа deleteEpicsById");
        assertEquals(linesAfterDelete.size() + 1, lines.size(), "Не удален эпик из файла");
    }

    @Test
    void deleteSubtaskById() {
        addNewEpicsForTests(1);
        addSubtasksForTests(3, manager.getEpicById(1), TaskStatus.NEW);
        ArrayList<String> lines = readFile();
        manager.deleteSubtaskById(3);
        ArrayList<String> linesAfterDelete = readFile();
        assertEquals(2, manager.getAllSubtasks().size(),
                "Не корректное удаление сабтаска по deleteSubtaskById");
        assertEquals(linesAfterDelete.size() + 1, lines.size(), "Не удален сабтаск из файла");
    }

    @Test
    void deleteAllTasks() {
        addThreeNewDifferentTasksForTests();
        manager.deleteAllTasks();
        ArrayList<String> lines = readFile();
        assertEquals(0, manager.getAllTasks().size(), "Проверка удаления всех Tasks");
        assertEquals(1, lines.size(), "Не все строки таски удалены из файла");
    }

    @Test
    void deleteAllEpics() {
        addNewEpicsForTests(3);
        manager.deleteAllEpics();
        ArrayList<String> lines = readFile();
        assertEquals(0, manager.getAllEpics().size(), "Проверка удаления всех Epics");
        assertEquals(1, lines.size(), "Не все строки эпик удалены из файла");
    }

    @Test
    void deleteAllEpicsMustDeleteAllSubtasks() {
        addNewEpicsForTests(2);
        addSubtasksForTests(2, manager.getEpicById(1), TaskStatus.NEW);
        addSubtasksForTests(2, manager.getEpicById(2), TaskStatus.NEW);
        manager.deleteAllEpics();
        ArrayList<String> lines = readFile();
        assertEquals(0, manager.getAllSubtasks().size(),
                "Проверка удаления всех Subtasks при удалении всех эпиков");
        assertEquals(1, lines.size(), "Не все строки сабтаски удалены из файла при удалении эпиков");
    }

    @Test
    void deleteAllSubtasks() {
        addNewEpicsForTests(2);
        addSubtasksForTests(2, manager.getEpicById(1), TaskStatus.NEW);
        addSubtasksForTests(2, manager.getEpicById(2), TaskStatus.NEW);
        manager.deleteAllSubtasks();
        ArrayList<String> lines = readFile();
        assertEquals(0, manager.getAllSubtasks().size(), "Проверка удаления всех Subtasks");
        assertEquals(3, lines.size(),
                "Не все строки сабтаски удалены из файла при удалении всех сабтасков");
    }
    @Test
    void updateTaskNameMustChangeSavedTask() {
        addThreeNewDifferentTasksForTests();
        Task task = manager.getTaskById(1);
        String newName="Новое имя таска";
        task.setName(newName);
        manager.updateTask(task);
        ArrayList<String> lines = readFile();
        assertEquals(newName, manager.getTaskById(1).getName(),
                "Обновление имени таска по updateTask");
        assertEquals(newName,lines.get(1).split(FileBackedTaskManager.DELIMITER)[2],
                "Не изменилось имя таска в файле");
    }

    @Test
    void updateEpicNameMustChangeSavedEpic() {
        addNewEpicsForTests(1);
        Epic epic = manager.getEpicById(1);
        String newName="Новое имя эпика";
        epic.setName(newName);
        manager.updateEpic(epic);
        ArrayList<String> lines = readFile();
        assertEquals(newName, manager.getEpicById(1).getName(),
                "Обновление имени эпика по updateEpic");
        assertEquals(newName,lines.get(1).split(FileBackedTaskManager.DELIMITER)[2],
                "Не изменилось имя эпика в файле");

    }

    @Test
    void updateSubtaskNameMustChangeSavedSubtask() {
        addNewEpicsForTests(1);
        addSubtasksForTests(1, manager.getEpicById(1), TaskStatus.NEW);
        Subtask subtask = manager.getSubtaskById(2);
        String newName="Новое имя сабтаска";
        subtask.setName(newName);
        manager.updateSubtask(subtask);
        ArrayList<String> lines = readFile();
        assertEquals(newName, manager.getSubtaskById(2).getName(),
                "Обновление имени сабтаска при updateSubtask");
        assertEquals(newName,lines.get(2).split(FileBackedTaskManager.DELIMITER)[2],
                "Не изменилось имя сабтаска в файле");
    }


}