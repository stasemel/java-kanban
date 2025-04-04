package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryTaskManagerTest {

    public InMemoryTaskManager manager;

    private void addNewEpicsForTests(int count) throws ManagerAddTaskException {
        for (int i = 0; i < count; i++) {
            Epic epic = new Epic(String.format("Эпик No %d", i + 1), String.format("Описание эпика No %d", i + 1),
                    TaskStatus.NEW);
            manager.addEpic(epic);
        }
    }

    private void addSubtasksForTests(int count, Epic epic, TaskStatus status) throws ManagerAddTaskException {
        for (int i = 0; i < count; i++) {
            Subtask subtask = new Subtask(String.format("Subtask No %d", i + 1),
                    String.format("Описание сабтаска No %d", i + 1),
                    status);
            manager.addSubtask(subtask, epic);
        }
    }

    private void addThreeNewDifferentTasksForTests() throws ManagerAddTaskException {
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
    void shouldAddTaskDoNotChangeFieldsExceptId() throws ManagerAddTaskException {
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
    void shouldAddTaskWithSameStatus() throws ManagerAddTaskException {
        Task task1 = new Task("Первый таск", "Создать новую задачу", TaskStatus.IN_PROGRESS);
        manager.addTask(task1);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getTaskById(1).getStatus(),
                "Не создалась задача типа Task с тем же статусом");
    }

    @Test
    void shouldAddThreeTasksAndReturnsGetAllTasks() throws ManagerAddTaskException {
        addThreeNewDifferentTasksForTests();
        assertEquals(3, manager.getAllTasks().size(), "Не создались задачи типа Task");
    }


    @Test
    void shouldAddEpicAndStatusMustSetNew() throws ManagerAddTaskException {
        Epic epic1 = new Epic("Первый эпик", "Проверяем создание первого эпика", TaskStatus.DONE);
        manager.addEpic(epic1);
        assertEquals(TaskStatus.NEW, manager.getEpicById(1).getStatus(),
                "Не создалась задача типа Epic со статусом NEW");
    }

    @Test
    void shouldAddOneSubtaskWithStatusNew() throws ManagerAddTaskException {
        addNewEpicsForTests(1);
        Subtask subtask = new Subtask("Subtask", "Описание Subtask", TaskStatus.NEW);
        manager.addSubtask(subtask, manager.getEpicById(1));
        assertEquals(TaskStatus.NEW, manager.getSubtaskById(2).getStatus(),
                "Не создался Subtask со статусом NEW");
    }

    @Test
    void shouldAddOneSubtaskWithStatusInProgress() throws ManagerAddTaskException {
        addNewEpicsForTests(1);
        Subtask subtask = new Subtask("Subtask", "Описание Subtask", TaskStatus.IN_PROGRESS);
        manager.addSubtask(subtask, manager.getEpicById(1));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getSubtaskById(2).getStatus(),
                "Не создался Subtask со статусом IN_PROGRESS");
    }

    @Test
    void changeEpicStatusWhenAddSubtaskInProgress() throws ManagerAddTaskException {
        addNewEpicsForTests(1);
        Subtask subtask = new Subtask("Subtask", "Описание Subtask", TaskStatus.IN_PROGRESS);
        manager.addSubtask(subtask, manager.getEpicById(1));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(1).getStatus(),
                "Не изменился статус Epic при добавлении Subtask со статусом IN_PROGRESS");
    }

    @Test
    void changeEpicStatusWhenAddSubtaskDone() throws ManagerAddTaskException {
        addNewEpicsForTests(1);
        Subtask subtask = new Subtask("Subtask", "Описание Subtask", TaskStatus.DONE);
        manager.addSubtask(subtask, manager.getEpicById(1));
        assertEquals(TaskStatus.DONE, manager.getEpicById(1).getStatus(),
                "Не изменился статус Epic при доабвлении Subtask со статусом DONE");
    }

    @Test
    void getTaskByIdMustReturnsTask() throws ManagerAddTaskException {
        addThreeNewDifferentTasksForTests();
        assertInstanceOf(Task.class, manager.getTaskById(2), "Не удалось получить Task по id");
    }

    @Test
    void getEpicByIdMustReturnsEpic() throws ManagerAddTaskException {
        addNewEpicsForTests(2);
        assertInstanceOf(Epic.class, manager.getEpicById(2), "Не удалось получить Task по id");
    }

    @Test
    void getSubtaskByIdMustReturnsSubtask() throws ManagerAddTaskException {
        addNewEpicsForTests(1);
        Subtask subtask = new Subtask("Subtask", "Описание Subtask", TaskStatus.IN_PROGRESS);
        manager.addSubtask(subtask, manager.getEpicById(1));
        assertInstanceOf(Subtask.class, manager.getSubtaskById(2), "Не удалось получить Task по id");
    }


    @Test
    void shouldReturnAllEpics() throws ManagerAddTaskException {
        addNewEpicsForTests(3);
        assertEquals(3, manager.getAllEpics().size(), "Некорректная работа getAllEpics");
    }

    @Test
    void shouldReturnsAllSubtasks() throws ManagerAddTaskException {
        addNewEpicsForTests(1);
        addSubtasksForTests(3, manager.getEpicById(1), TaskStatus.NEW);
        assertEquals(3, manager.getAllSubtasks().size(), "Некорректная работа getAllSubtasks");
    }

    @Test
    void deleteTaskById() throws ManagerAddTaskException {
        addThreeNewDifferentTasksForTests();
        manager.deleteTaskById(1);
        assertEquals(2, manager.getAllTasks().size(), "Некорректная работа deleteTaskById");
    }

    @Test
    void deleteEpicById() throws ManagerAddTaskException {
        addNewEpicsForTests(3);
        manager.deleteEpicById(1);
        assertEquals(2, manager.getAllEpics().size(), "Некорректная работа deleteEpicsById");
    }

    @Test
    void deleteEpicByIdMustDeleteSubtasks() throws ManagerAddTaskException {
        addNewEpicsForTests(1);
        addSubtasksForTests(2, manager.getEpicById(1), TaskStatus.NEW);
        manager.deleteEpicById(1);
        assertEquals(0, manager.getAllSubtasks().size(), "Проверка удаления Subtasks при удалении эпика");
    }

    @Test
    void deleteSubtaskById() throws ManagerAddTaskException {
        addNewEpicsForTests(1);
        addSubtasksForTests(3, manager.getEpicById(1), TaskStatus.NEW);
        manager.deleteSubtaskById(3);
        assertEquals(2, manager.getAllSubtasks().size(),
                "Некорректное удаление сабтаска по deleteSubtaskById");
    }

    @Test
    void deleteAllTasks() throws ManagerAddTaskException {
        addThreeNewDifferentTasksForTests();
        manager.deleteAllTasks();
        assertEquals(0, manager.getAllTasks().size(), "Проверка удаления всех Tasks");
    }

    @Test
    void deleteAllEpics() throws ManagerAddTaskException {
        addNewEpicsForTests(3);
        manager.deleteAllEpics();
        assertEquals(0, manager.getAllEpics().size(), "Проверка удаления всех Epics");
    }

    @Test
    void deleteAllEpicsMustDeleteAllSubtasks() throws ManagerAddTaskException {
        addNewEpicsForTests(2);
        addSubtasksForTests(2, manager.getEpicById(1), TaskStatus.NEW);
        addSubtasksForTests(2, manager.getEpicById(2), TaskStatus.NEW);
        manager.deleteAllEpics();
        assertEquals(0, manager.getAllSubtasks().size(),
                "Проверка удаления всех Subtasks при удалении всех эпиков");
    }

    @Test
    void deleteAllSubtasks() throws ManagerAddTaskException {
        addNewEpicsForTests(2);
        addSubtasksForTests(2, manager.getEpicById(1), TaskStatus.NEW);
        addSubtasksForTests(2, manager.getEpicById(2), TaskStatus.NEW);
        manager.deleteAllSubtasks();
        assertEquals(0, manager.getAllSubtasks().size(), "Проверка удаления всех Subtasks");
    }

    @Test
    void deleteAllSubtasksMustChangeEpicStatusToNew() throws ManagerAddTaskException {
        addNewEpicsForTests(1);
        addSubtasksForTests(2, manager.getEpicById(1), TaskStatus.IN_PROGRESS);
        manager.deleteAllSubtasks();
        assertEquals(TaskStatus.NEW, manager.getEpicById(1).getStatus(), "Проверка удаления всех Subtasks");
    }

    @Test
    void updateTaskNameMustChangeSavedTask() throws ManagerAddTaskException {
        addThreeNewDifferentTasksForTests();
        Task task = manager.getTaskById(1);
        task.setName("Новое имя таска");
        manager.updateTask(task);
        assertEquals("Новое имя таска", manager.getTaskById(1).getName(),
                "Обновление имени таска по updateTask");
    }

    @Test
    void updateEpicNameMustChangeSavedEpic() throws ManagerAddTaskException {
        addNewEpicsForTests(1);
        Epic epic = manager.getEpicById(1);
        epic.setName("Новое имя эпика");
        manager.updateEpic(epic);
        assertEquals("Новое имя эпика", manager.getEpicById(1).getName(),
                "Обновление имени эпика по updateEpic");

    }

    @Test
    void updateSubtaskNameMustChangeSavedSubtask() throws ManagerAddTaskException {
        addNewEpicsForTests(1);
        addSubtasksForTests(1, manager.getEpicById(1), TaskStatus.NEW);
        Subtask subtask = manager.getSubtaskById(2);
        subtask.setName("Новое имя сабтаска");
        manager.updateSubtask(subtask);
        assertEquals("Новое имя сабтаска", manager.getSubtaskById(2).getName(),
                "Обновление имени сабтаска при updateSubtask");
    }

    @Test
    void updateSubtaskStatusDoneMustChangeEpicStatus() throws ManagerAddTaskException {
        addNewEpicsForTests(1);
        addSubtasksForTests(1, manager.getEpicById(1), TaskStatus.NEW);
        Subtask subtask = manager.getSubtaskById(2);
        subtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask);
        assertEquals(TaskStatus.DONE, manager.getEpicById(1).getStatus(),
                "Обновление статуса эпика при смене статуса сабтаска при updateSubtask не произошло");
    }

    @Test
    void getSubtaskByEpicMustReturnAllEpicSubtasks() throws ManagerAddTaskException {
        addNewEpicsForTests(1);
        addSubtasksForTests(5, manager.getEpicById(1), TaskStatus.NEW);
        assertEquals(5, manager.getSubtaskByEpic(manager.getEpicById(1)).size(),
                "Вернулось неправильное количество сабтасков эпика");

    }

    @Test
    void shouldReturnThreeTasksOneEpicAndTwoSubtaskAtGetHistory() throws ManagerAddTaskException {
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
    void shouldHistoryMustContainAllViews() throws ManagerAddTaskException {
        addNewEpicsForTests(11);
        for (Epic epic : manager.getAllEpics()) {
            manager.getEpicById(epic.getId());
        }
        ArrayList<Task> history = manager.getHistory();
        assertEquals(11, history.size(),
                String.format("Количество просмотров должно быть равно %d, количеству уникальных id", history.size()));
    }

    @Test
    void shouldDeleteTaskMustRemoveFromHistory() throws ManagerAddTaskException {
        addNewEpicsForTests(11);
        for (Epic epic : manager.getAllEpics()) {
            manager.getEpicById(epic.getId());
        }
        ArrayList<Task> historyBeforeDelete = manager.getHistory();
        Epic epic = manager.getAllEpics().getFirst();
        manager.deleteEpicById(epic.getId());
        ArrayList<Task> history = manager.getHistory();
        assertEquals(10, history.size(), "Удаление таска должно удалять его из истории просмотров");
        assertTrue(historyBeforeDelete.size() > history.size(), "Удаление не элемента влияет на историю");
    }
}