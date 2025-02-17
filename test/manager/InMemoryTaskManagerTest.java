package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    public InMemoryTaskManager manager;

    private void createAndAddEpics(int count) {
        for (int i = 0; i < count; i++) {
            Epic epic = new Epic("Эпик No " + (i + 1), "Описение эпика No" + (i + 1), TaskStatus.NEW);
            manager.addEpic(epic);
        }
    }

    private void createAndAddSubtasks(int count, Epic epic, TaskStatus status) {
        for (int i = 0; i < count; i++) {
            Subtask subtask = new Subtask("Subtask No " + (i + 1), "Описение сабтаска No" + (i + 1), status);
            manager.addSubtask(subtask, epic);
        }
    }

    private void createThreeDifferentTasks() {
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
    void shouldAddTaskWithSameStatus() {
        Task task1 = new Task("Первый таск", "Создать новую задачу", TaskStatus.IN_PROGRESS);
        manager.addTask(task1);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, manager.getTask(1).getStatus(), "Не создалась задача типа Task с тем же статусом");
    }

    @Test
    void shouldAddThreeTasksAndReturnsGetAllTasks() {
        createThreeDifferentTasks();
        Assertions.assertEquals(3, manager.getAllTasks().size(), "Не создались задачи типа Task");
    }


    @Test
    void ahouldAddEpicAndStatusMustSetNew() {
        Epic epic1 = new Epic("Первый эпик", "Проверяем создание первого эпика", TaskStatus.DONE);
        manager.addEpic(epic1);
        Assertions.assertEquals(TaskStatus.NEW, manager.getEpic(1).getStatus(), "Не создалась задача типа Epic со статусом NEW");
    }

    @Test
    void shouldAddOneSubtaskWithStatusNew() {
        createAndAddEpics(1);
        Subtask subtask = new Subtask("Subtask", "Описание Subtask", TaskStatus.NEW);
        manager.addSubtask(subtask, manager.getEpic(1));
        assertEquals(TaskStatus.NEW, manager.getSubtask(2).getStatus(), "Не создался Subtask со статусом NEW");
    }

    @Test
    void shouldAddOneSubtaskWithStatusInProgress() {
        createAndAddEpics(1);
        Subtask subtask = new Subtask("Subtask", "Описание Subtask", TaskStatus.IN_PROGRESS);
        manager.addSubtask(subtask, manager.getEpic(1));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getSubtask(2).getStatus(), "Не создался Subtask со статусом IN_PROGRESS");
    }

    @Test
    void shouldEpicChangeStatusWhenAddSubtaskWithStatusInPorgress() {
        createAndAddEpics(1);
        Subtask subtask = new Subtask("Subtask", "Описание Subtask", TaskStatus.IN_PROGRESS);
        manager.addSubtask(subtask, manager.getEpic(1));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(1).getStatus(), "Не изменился статус Epic при доабвлении Subtask со статусом IN_PROGRESS");
    }

    @Test
    void shouldEpicChangeStatusWhenAddSubtaskWithStatusDone() {
        createAndAddEpics(1);
        Subtask subtask = new Subtask("Subtask", "Описание Subtask", TaskStatus.DONE);
        manager.addSubtask(subtask, manager.getEpic(1));
        assertEquals(TaskStatus.DONE, manager.getEpic(1).getStatus(), "Не изменился статус Epic при доабвлении Subtask со статусом DONE");
    }

    @Test
    void getTaskByIdMustReturnsTask() {
        createThreeDifferentTasks();
        assertTrue(manager.getTask(2) instanceof Task, "Не удалось получить Task по id");
    }

    @Test
    void getEpicByIdMustReturnsEpic() {
        createAndAddEpics(2);
        assertTrue(manager.getEpic(2) instanceof Epic, "Не удалось получить Task по id");
    }

    @Test
    void getSubtaskByIdMustReturnsSubtask() {
        createAndAddEpics(1);
        Subtask subtask = new Subtask("Subtask", "Описание Subtask", TaskStatus.IN_PROGRESS);
        manager.addSubtask(subtask, manager.getEpic(1));
        assertTrue(manager.getSubtask(2) instanceof Subtask, "Не удалось получить Task по id");
    }


    @Test
    void shouldReturnAllEpics() {
        createAndAddEpics(3);
        assertEquals(3, manager.getAllEpics().size(), "Не корректная работа getAllEpics");
    }

    @Test
    void shouldReturnsAllSubtasks() {
        createAndAddEpics(1);
        createAndAddSubtasks(3, manager.getEpic(1), TaskStatus.NEW);
        assertEquals(3, manager.getAllSubtasks().size(), "Не корректная работа getAllSubtasks");
    }

    @Test
    void deleteTaskById() {
        createThreeDifferentTasks();
        manager.deleteTaskById(1);
        assertEquals(2, manager.getAllTasks(), "Не корректная работа deleteTaskById");
    }

    @Test
    void deleteEpicById() {
        createAndAddEpics(3);
        assertEquals(2, manager.getAllEpics().size(), "Не корреектная работа deleteEpicsById");
    }

    @Test
    void deleteEpicByIdMustDeleteSubtasks() {
        createAndAddEpics(1);
        createAndAddSubtasks(2, manager.getEpic(1), TaskStatus.NEW);
        manager.deleteEpicById(1);
        assertEquals(0, manager.getAllSubtasks().size(), "Проверка удаления Subtasks при удалении эпика");
    }

    @Test
    void deleteSubtaskById() {
        createAndAddEpics(1);
        createAndAddSubtasks(3, manager.getEpic(1), TaskStatus.NEW);
        manager.deleteSubtaskById(3);
        assertEquals(2, manager.getAllSubtasks().size(), "Не корректное удаление сабтаска по deleteSubtaskById");
    }

    @Test
    void deleteAllTasks() {
        createThreeDifferentTasks();
        manager.deleteAllTasks();
        assertEquals(0, manager.getAllTasks().size(), "Проверка удаления всех Tasks");
    }

    @Test
    void deleteAllEpics() {
        createAndAddEpics(3);
        manager.deleteAllEpics();
        assertEquals(0, manager.getAllEpics().size(), "Проверка удаления всех Epics");
    }

    @Test
    void deleteAllEpicsMustDeleteAllSubtasks() {
        createAndAddEpics(2);
        createAndAddSubtasks(2, manager.getEpic(1), TaskStatus.NEW);
        createAndAddSubtasks(2, manager.getEpic(2), TaskStatus.NEW);
        manager.deleteAllEpics();
        assertEquals(0, manager.getAllSubtasks().size(), "Проверка удаления всех Subtasks при удалении всех эпиков");
    }

    @Test
    void deleteAllSubtasks() {
        createAndAddEpics(2);
        createAndAddSubtasks(2, manager.getEpic(1), TaskStatus.NEW);
        createAndAddSubtasks(2, manager.getEpic(2), TaskStatus.NEW);
        manager.deleteAllSubtasks();
        assertEquals(0, manager.getAllSubtasks().size(), "Проверка удаления всех Subtasks");
    }

    @Test
    void updateTask() {
        createThreeDifferentTasks();
        Task task= manager.getTask(1);
        task.setName("Новое имя таска");
        manager.updateTask(task);
        assertEquals("Новое имя таска", manager.getTask(1).getName(),"Обновление имени таска по updateTask");
    }

    @Test
    void updateEpic() {
        createAndAddEpics(1);
        Epic epic=manager.getEpic(1);
        epic.setName("Новое имя эпика");
        manager.updateEpic(epic);
        assertEquals("Новое имя эпика", manager.getEpic(1).getName(),"Обновление имени эпика по updateEpic");

    }

    @Test
    void updateSubtask() {
        createAndAddEpics(1);
        createAndAddSubtasks(1,manager.getEpic(1),TaskStatus.NEW);
        Subtask subtask=manager.getSubtask(2);
        subtask.setName("Новое имя сабтаска");
        manager.updateSubtask(subtask);
        assertEquals("Новое имя сабтаска",manager.getSubtask(2).getName(),"Обновление имени сабтаска при updateSubtask");
    }

    @Test
    void getSubtaskByEpic() {
    }

    @Test
    void getHistory() {
    }
}