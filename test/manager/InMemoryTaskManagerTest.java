package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


class InMemoryTaskManagerTest {

    public InMemoryTaskManager manager;

    private void addNewEpicsForTests(int count) {
        for (int i = 0; i < count; i++) {
            Epic epic = new Epic("Эпик No " + (i + 1), "Описение эпика No" + (i + 1), TaskStatus.NEW);
            manager.addEpic(epic);
        }
    }

    private void addSubtasksForTests(int count, Epic epic, TaskStatus status) {
        for (int i = 0; i < count; i++) {
            Subtask subtask = new Subtask("Subtask No " + (i + 1), "Описение сабтаска No" + (i + 1), status);
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

    @BeforeEach
    public void createManager() {
        manager = (InMemoryTaskManager) Managers.getDefault();
    }

    @Test
    void shouldAddTaskDoNotChangeFieldsExceptId() {
        Task task = new Task("Первый", "Описание первого", TaskStatus.IN_PROGRESS);
        String name = task.getName();
        String description = task.getDescription();
        TaskStatus status = task.getStatus();
        manager.addTask(task);
        Task addedTask = manager.getTaskById(1);
        assertEquals(name, addedTask.getName(), "Изменилось имя при добавлении Task в менеджер");
        assertEquals(description, addedTask.getDescription(), "Изменилось описание при добавлении Task в менеджер");
        assertEquals(status, addedTask.getStatus(), "Изменился статус при добавлении Task в менеджер");
    }

    @Test
    void shouldAddTaskWithSameStatus() {
        Task task1 = new Task("Первый таск", "Создать новую задачу", TaskStatus.IN_PROGRESS);
        manager.addTask(task1);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getTaskById(1).getStatus(), "Не создалась задача типа Task с тем же статусом");
    }

    @Test
    void shouldAddThreeTasksAndReturnsGetAllTasks() {
        addThreeNewDifferentTasksForTests();
        assertEquals(3, manager.getAllTasks().size(), "Не создались задачи типа Task");
    }


    @Test
    void ahouldAddEpicAndStatusMustSetNew() {
        Epic epic1 = new Epic("Первый эпик", "Проверяем создание первого эпика", TaskStatus.DONE);
        manager.addEpic(epic1);
        assertEquals(TaskStatus.NEW, manager.getEpicById(1).getStatus(), "Не создалась задача типа Epic со статусом NEW");
    }

    @Test
    void shouldAddOneSubtaskWithStatusNew() {
        addNewEpicsForTests(1);
        Subtask subtask = new Subtask("Subtask", "Описание Subtask", TaskStatus.NEW);
        manager.addSubtask(subtask, manager.getEpicById(1));
        assertEquals(TaskStatus.NEW, manager.getSubtaskById(2).getStatus(), "Не создался Subtask со статусом NEW");
    }

    @Test
    void shouldAddOneSubtaskWithStatusInProgress() {
        addNewEpicsForTests(1);
        Subtask subtask = new Subtask("Subtask", "Описание Subtask", TaskStatus.IN_PROGRESS);
        manager.addSubtask(subtask, manager.getEpicById(1));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getSubtaskById(2).getStatus(), "Не создался Subtask со статусом IN_PROGRESS");
    }

    @Test
    void shouldEpicChangeStatusWhenAddSubtaskWithStatusInPorgress() {
        addNewEpicsForTests(1);
        Subtask subtask = new Subtask("Subtask", "Описание Subtask", TaskStatus.IN_PROGRESS);
        manager.addSubtask(subtask, manager.getEpicById(1));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(1).getStatus(), "Не изменился статус Epic при доабвлении Subtask со статусом IN_PROGRESS");
    }

    @Test
    void shouldEpicChangeStatusWhenAddSubtaskWithStatusDone() {
        addNewEpicsForTests(1);
        Subtask subtask = new Subtask("Subtask", "Описание Subtask", TaskStatus.DONE);
        manager.addSubtask(subtask, manager.getEpicById(1));
        assertEquals(TaskStatus.DONE, manager.getEpicById(1).getStatus(), "Не изменился статус Epic при доабвлении Subtask со статусом DONE");
    }

    @Test
    void getTaskByIdMustReturnsTask() {
        addThreeNewDifferentTasksForTests();
        assertInstanceOf(Task.class, manager.getTaskById(2), "Не удалось получить Task по id");
    }

    @Test
    void getEpicByIdMustReturnsEpic() {
        addNewEpicsForTests(2);
        assertInstanceOf(Epic.class, manager.getEpicById(2), "Не удалось получить Task по id");
    }

    @Test
    void getSubtaskByIdMustReturnsSubtask() {
        addNewEpicsForTests(1);
        Subtask subtask = new Subtask("Subtask", "Описание Subtask", TaskStatus.IN_PROGRESS);
        manager.addSubtask(subtask, manager.getEpicById(1));
        assertInstanceOf(Subtask.class, manager.getSubtaskById(2), "Не удалось получить Task по id");
    }


    @Test
    void shouldReturnAllEpics() {
        addNewEpicsForTests(3);
        assertEquals(3, manager.getAllEpics().size(), "Не корректная работа getAllEpics");
    }

    @Test
    void shouldReturnsAllSubtasks() {
        addNewEpicsForTests(1);
        addSubtasksForTests(3, manager.getEpicById(1), TaskStatus.NEW);
        assertEquals(3, manager.getAllSubtasks().size(), "Не корректная работа getAllSubtasks");
    }

    @Test
    void deleteTaskById() {
        addThreeNewDifferentTasksForTests();
        manager.deleteTaskById(1);
        assertEquals(2, manager.getAllTasks().size(), "Не корректная работа deleteTaskById");
    }

    @Test
    void deleteEpicById() {
        addNewEpicsForTests(3);
        manager.deleteEpicById(1);
        assertEquals(2, manager.getAllEpics().size(), "Не корреектная работа deleteEpicsById");
    }

    @Test
    void deleteEpicByIdMustDeleteSubtasks() {
        addNewEpicsForTests(1);
        addSubtasksForTests(2, manager.getEpicById(1), TaskStatus.NEW);
        manager.deleteEpicById(1);
        assertEquals(0, manager.getAllSubtasks().size(), "Проверка удаления Subtasks при удалении эпика");
    }

    @Test
    void deleteSubtaskById() {
        addNewEpicsForTests(1);
        addSubtasksForTests(3, manager.getEpicById(1), TaskStatus.NEW);
        manager.deleteSubtaskById(3);
        assertEquals(2, manager.getAllSubtasks().size(), "Не корректное удаление сабтаска по deleteSubtaskById");
    }

    @Test
    void deleteAllTasks() {
        addThreeNewDifferentTasksForTests();
        manager.deleteAllTasks();
        assertEquals(0, manager.getAllTasks().size(), "Проверка удаления всех Tasks");
    }

    @Test
    void deleteAllEpics() {
        addNewEpicsForTests(3);
        manager.deleteAllEpics();
        assertEquals(0, manager.getAllEpics().size(), "Проверка удаления всех Epics");
    }

    @Test
    void deleteAllEpicsMustDeleteAllSubtasks() {
        addNewEpicsForTests(2);
        addSubtasksForTests(2, manager.getEpicById(1), TaskStatus.NEW);
        addSubtasksForTests(2, manager.getEpicById(2), TaskStatus.NEW);
        manager.deleteAllEpics();
        assertEquals(0, manager.getAllSubtasks().size(), "Проверка удаления всех Subtasks при удалении всех эпиков");
    }

    @Test
    void deleteAllSubtasks() {
        addNewEpicsForTests(2);
        addSubtasksForTests(2, manager.getEpicById(1), TaskStatus.NEW);
        addSubtasksForTests(2, manager.getEpicById(2), TaskStatus.NEW);
        manager.deleteAllSubtasks();
        assertEquals(0, manager.getAllSubtasks().size(), "Проверка удаления всех Subtasks");
    }

    @Test
    void deleteAllSubtasksMustChangeEpicStatusToNew() {
        addNewEpicsForTests(1);
        addSubtasksForTests(2, manager.getEpicById(1), TaskStatus.IN_PROGRESS);
        manager.deleteAllSubtasks();
        assertEquals(TaskStatus.NEW, manager.getEpicById(1).getStatus(), "Проверка удаления всех Subtasks");
    }

    @Test
    void updateTaskNameMustChangeSavedTask() {
        addThreeNewDifferentTasksForTests();
        Task task = manager.getTaskById(1);
        task.setName("Новое имя таска");
        manager.updateTask(task);
        assertEquals("Новое имя таска", manager.getTaskById(1).getName(), "Обновление имени таска по updateTask");
    }

    @Test
    void updateEpicNameMustChangeSavedEpic() {
        addNewEpicsForTests(1);
        Epic epic = manager.getEpicById(1);
        epic.setName("Новое имя эпика");
        manager.updateEpic(epic);
        assertEquals("Новое имя эпика", manager.getEpicById(1).getName(), "Обновление имени эпика по updateEpic");

    }

    @Test
    void updateSubtaskNameMustChangeSavedSubtask() {
        addNewEpicsForTests(1);
        addSubtasksForTests(1, manager.getEpicById(1), TaskStatus.NEW);
        Subtask subtask = manager.getSubtaskById(2);
        subtask.setName("Новое имя сабтаска");
        manager.updateSubtask(subtask);
        assertEquals("Новое имя сабтаска", manager.getSubtaskById(2).getName(), "Обновление имени сабтаска при updateSubtask");
    }

    @Test
    void updateSubtaskStatusDoneMustChangeEpicStatus() {
        addNewEpicsForTests(1);
        addSubtasksForTests(1, manager.getEpicById(1), TaskStatus.NEW);
        Subtask subtask = manager.getSubtaskById(2);
        subtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask);
        assertEquals(TaskStatus.DONE, manager.getEpicById(1).getStatus(), "Обновление статуса эпика при смене статуса сабтаска при updateSubtask не произошло");
    }

    @Test
    void getSubtaskByEpicMustReturnAllEpicSuntasks() {
        addNewEpicsForTests(1);
        addSubtasksForTests(5, manager.getEpicById(1), TaskStatus.NEW);
        assertEquals(5, manager.getSubtaskByEpic(manager.getEpicById(1)).size(), "Вернулось неправильное количество сабтасков эпика");

    }

    @Test
    void shouldReturnThreeTasksOneEpicAndTwoSubtaskAtGetHistory() {
        addThreeNewDifferentTasksForTests();
        addNewEpicsForTests(1);
        addSubtasksForTests(2, manager.getAllEpics().getFirst(), TaskStatus.NEW);
        for (Task task : manager.getAllTasks()) {
            manager.getTaskById(task.getId());
        }
        for (Epic epic : manager.getAllEpics()) {
            manager.getEpicById(epic.getId());
        }
        for (Subtask subtask : manager.getAllSubtasks()) {
            manager.getSubtaskById(subtask.getId());
        }
        ArrayList<Task> history = manager.getHistory();
        assertEquals(6, history.size(), "Не совпало количество просмотров");
    }

    @Test
    void shouldHistorySizeCannotBeGreaterThanTen() {
        addNewEpicsForTests(11);
        for (Epic epic : manager.getAllEpics()) {
            manager.getEpicById(epic.getId());
        }
        ArrayList<Task> history = manager.getHistory();
        assertEquals(10, history.size(), "Количество просмотров не должно быть больше 10");
    }

    @Test
    void addElevenTaskInHistoryMustRemoveFirst() {
        addNewEpicsForTests(11);
        for (Epic epic : manager.getAllEpics()) {
            manager.getEpicById(epic.getId());
        }
        ArrayList<Task> history = manager.getHistory();
        assertEquals(2, history.getFirst().getId(), "Количество просмотров не должно быть больше 10");
    }
}