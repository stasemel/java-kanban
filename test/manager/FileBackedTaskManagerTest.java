package manager;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends TaskManagerTest {

    File file;

    @Override
    @BeforeEach
    public void createManager() {
        try {
            setUp();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private ArrayList<String> readFile() {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file, FileBackedTaskManager.CHARSET))) {
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

    void setUp() throws IOException {
        file = File.createTempFile("manager", ".csv");
        manager = new FileBackedTaskManager(file);
    }

    @Test
    void addTaskToFile() throws ManagerAddTaskException {
        Task task = new Task("Таск 1", "Описание 1", TaskStatus.NEW);
        manager.addTask(task);
        ArrayList<String> lines = readFile();
        assertEquals(task, manager.getAllTasks().getFirst(), "Не добавлен таск");
        assertTrue(file.exists(), "Файл не создался");
        assertEquals(2, lines.size(), "Не все строки с таск добавлены в файл");
    }

    @Test
    void addEpicToFile() throws ManagerAddTaskException {
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.NEW);
        manager.addEpic(epic);
        ArrayList<String> lines = readFile();
        assertEquals(epic, manager.getAllEpics().getFirst(), "Не добавлен эпик");
        assertTrue(file.exists(), "Файл не создался");
        assertEquals(2, lines.size(), "Не все строки с эпик добавлены в файл");
    }

    @Test
    void addSubtaskToFile() throws ManagerAddTaskException {
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.NEW);
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Сабтаск 1", "Описание сабтаска 1", TaskStatus.NEW);
        manager.addSubtask(subtask1, epic);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Описание сабтаска 2", TaskStatus.NEW);
        manager.addSubtask(subtask2, epic);
        ArrayList<String> lines = readFile();
        assertEquals(epic, manager.getAllEpics().getFirst(), "Не добавлен эпик");
        assertEquals(subtask1, manager.getAllSubtasks().getFirst(), "Не добавлен сабтаск 1");
        assertEquals(2, epic.getSubtasks().size(), "Добавлены не все сабтаски к эпику");
        assertEquals(2, manager.getAllSubtasks().size(), "Не добавлены все сабтаски в менеджер");
        assertTrue(file.exists(), "Файл не создался");
        assertEquals(4, lines.size(), "Не все строки с эпиком и сабтасками добавлены в файл");
    }

    @Override
    @Test
    void deleteTaskById() throws ManagerAddTaskException {
        super.deleteTaskById();
        ArrayList<String> linesAfterDelete = readFile();
        assertEquals(linesAfterDelete.size() - 1, manager.getAllTasks().size(), "Не удален таск из файла");
    }

    @Override
    @Test
    void deleteEpicById() throws ManagerAddTaskException {
        super.deleteEpicById();
        ArrayList<String> linesAfterDelete = readFile();
        assertEquals(linesAfterDelete.size() - 1, manager.getAllEpics().size(), "Не удален эпик из файла");
    }

    @Test
    void deleteSubtaskById() throws ManagerAddTaskException {
        addNewEpicsForTests(1);
        addSubtasksForTests(3, manager.getEpicById(1), TaskStatus.NEW);
        ArrayList<String> lines = readFile();
        manager.deleteSubtaskById(3);
        ArrayList<String> linesAfterDelete = readFile();
        assertEquals(2, manager.getAllSubtasks().size(),
                "Некорректное удаление сабтаска по deleteSubtaskById");
        assertEquals(linesAfterDelete.size() + 1, lines.size(), "Не удален сабтаск из файла");
    }

    @Test
    void deleteAllTasks() throws ManagerAddTaskException {
        addThreeNewDifferentTasksForTests();
        manager.deleteAllTasks();
        ArrayList<String> lines = readFile();
        assertEquals(0, manager.getAllTasks().size(), "Проверка удаления всех Tasks");
        assertEquals(1, lines.size(), "Не все строки таски удалены из файла");
    }

    @Test
    void deleteAllEpics() throws ManagerAddTaskException {
        addNewEpicsForTests(3);
        manager.deleteAllEpics();
        ArrayList<String> lines = readFile();
        assertEquals(0, manager.getAllEpics().size(), "Проверка удаления всех Epics");
        assertEquals(1, lines.size(), "Не все строки эпик удалены из файла");
    }

    @Test
    void deleteAllEpicsMustDeleteAllSubtasks() throws ManagerAddTaskException {
        super.deleteAllEpicsMustDeleteAllSubtasks();
        ArrayList<String> lines = readFile();
        assertEquals(1, lines.size(), "Не все строки сабтаски удалены из файла при удалении эпиков");
    }

    @Test
    void deleteAllSubtasks() throws ManagerAddTaskException {
        super.deleteAllSubtasks();
        ArrayList<String> lines = readFile();
        assertEquals(3, lines.size(),
                "Не все строки сабтаски удалены из файла при удалении всех сабтасков");
    }

    @Test
    void updateTaskNameMustChangeSavedTask() throws ManagerAddTaskException {
        super.updateTaskNameMustChangeSavedTask();
        String newName = "Новое имя таска";
        ArrayList<String> lines = readFile();
        assertEquals(newName, lines.get(1).split(FileBackedTaskManager.DELIMITER)[2],
                "Не изменилось имя таска в файле");
    }

    @Test
    void updateEpicNameMustChangeSavedEpic() throws ManagerAddTaskException {
        addNewEpicsForTests(1);
        Epic epic = manager.getEpicById(1);
        String newName = "Новое имя эпика";
        epic.setName(newName);
        manager.updateEpic(epic);
        ArrayList<String> lines = readFile();
        assertEquals(newName, manager.getEpicById(1).getName(),
                "Обновление имени эпика по updateEpic");
        assertEquals(newName, lines.get(1).split(FileBackedTaskManager.DELIMITER)[2],
                "Не изменилось имя эпика в файле");

    }

    @Test
    void updateSubtaskNameMustChangeSavedSubtask() throws ManagerAddTaskException {
        super.updateSubtaskNameMustChangeSavedSubtask();
        String newName = "Новое имя сабтаска";
        ArrayList<String> lines = readFile();
        assertEquals(newName, lines.get(2).split(FileBackedTaskManager.DELIMITER)[2],
                "Не изменилось имя сабтаска в файле");
    }

    @Test
    void loadEmptyFile() throws ManagerAddTaskException {
        FileBackedTaskManager fileManager = FileBackedTaskManager.loadFromFile(file);
        System.out.println(fileManager.getAllTasks().size());
        assertEquals(0, fileManager.getAllTasks().size(), "Некорректная обработка пустого файла");
    }

    @Test
    void tryToLoadNonExistsFile() {
        File nonExistsFile = new File("nonexists.csv");
        assertThrows(RuntimeException.class, () -> FileBackedTaskManager.loadFromFile(nonExistsFile));
    }

    @Test
    void addTasksMustSaveFileThenLoadFileMustAddTasks() throws ManagerAddTaskException {
        Task task = new Task("Таск 1", "Описание 1", TaskStatus.NEW);
        manager.addTask(task);
        Task task2 = new Task("Таск 2", "Описание 2", TaskStatus.NEW);
        manager.addTask(task2);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(manager.getAllTasks(), loadedManager.getAllTasks(),
                "Не совпадают загруженные задачи с сохраненными");
        assertEquals(manager.getTaskById(1), loadedManager.getTaskById(1),
                "Не совпадают загруженные задачи с одинаковыми id");
    }

    @Test
    void testExpectedExceptionWhenAddTaskWithSameId() throws ManagerAddTaskException {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        task.setId(1);
        manager.addTask(task);
        assertThrows(ManagerAddTaskException.class, () -> manager.addTask(task),
                "Не выкинуло исключение ManagerAddTaskException при добавлении задачи с существующим id");
    }
}