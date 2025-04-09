package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class TaskManagerTest {
    public TaskManager manager;

    @BeforeEach
    public abstract void createManager();

    protected void addNewEpicsForTests(int count) throws ManagerAddTaskException {
        for (int i = 0; i < count; i++) {
            Epic epic = new Epic(String.format("Эпик No %d", i + 1), String.format("Описание эпика No %d", i + 1),
                    TaskStatus.NEW);
            manager.addEpic(epic);
        }
    }

    protected void addSubtasksForTests(int count, Epic epic, TaskStatus status) throws ManagerAddTaskException {
        for (int i = 0; i < count; i++) {
            Subtask subtask = new Subtask(String.format("Subtask No %d", i + 1),
                    String.format("Описание сабтаска No %d", i + 1),
                    status);
            manager.addSubtask(subtask, epic);
        }
    }

    protected void addThreeNewDifferentTasksForTests() throws ManagerAddTaskException {
        Task task1 = new Task("Первый таск", "Создать новую задачу", TaskStatus.NEW);
        Task task2 = new Task("Второй таск", "Создать вторую новую задачу", TaskStatus.IN_PROGRESS);
        Task task3 = new Task("Третий таск", "Создать третью новую задачу", TaskStatus.DONE);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
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
    void testEpicStatusChanges() throws ManagerAddTaskException {
        addNewEpicsForTests(1);
        Epic epic = manager.getAllEpics().getFirst();
        TaskStatus status1 = epic.getStatus();
        addSubtasksForTests(2, epic, TaskStatus.NEW);
        TaskStatus status2 = epic.getStatus();
        addSubtasksForTests(2, epic, TaskStatus.DONE);
        TaskStatus status3 = epic.getStatus();
        manager.deleteAllSubtasks();
        epic = manager.getAllEpics().getFirst(); //нужно перечитать после удаления всех
        TaskStatus status4 = epic.getStatus();
        addSubtasksForTests(2, epic, TaskStatus.DONE);
        TaskStatus status5 = epic.getStatus();
        addSubtasksForTests(2, epic, TaskStatus.IN_PROGRESS);
        TaskStatus status6 = epic.getStatus();
        manager.deleteAllSubtasks();
        epic = manager.getAllEpics().getFirst(); //нужно перечитать после удаления всех сабтасков
        addSubtasksForTests(2, epic, TaskStatus.IN_PROGRESS);
        TaskStatus status7 = epic.getStatus();
        assertEquals(TaskStatus.NEW, status1, "Статус при создании эпика не NEW");
        assertEquals(TaskStatus.NEW, status2, "Статус эпика не NEW при добавлении сабтасков со статусом NEW");
        assertEquals(TaskStatus.IN_PROGRESS, status3, "Статус эпика не IN_PROGRESS при добавлении сабтасков со статусом NEW и DONE");
        assertEquals(TaskStatus.NEW, status4, "Статус эпика не NEW после удаления всех сабтасков");
        assertEquals(TaskStatus.DONE, status5, "Статус эпика не DONE при добавлении сабтасков со статусом DONE");
        assertEquals(TaskStatus.IN_PROGRESS, status6, "Статус эпика не IN_PROGRESS при добавлении сабтасков со статусом DONE и IN_PROGRESS");
        assertEquals(TaskStatus.IN_PROGRESS, status7, "Статус эпика не IN_PROGRESS при добавлении сабтасков со статусом IN_PROGRESS");
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
        assertTrue(historyBeforeDelete.size() > history.size(), "Удаление элемента не влияет на историю");
    }

    @Test
    void setTaskStartTimeAndDuration() throws ManagerAddTaskException {
        LocalDateTime startTime = LocalDateTime.parse("2025-04-08 12:00", FileBackedTaskManager.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);
        Task task = new Task("Таск", "Описание таска", TaskStatus.NEW, startTime, duration);
        manager.addTask(task);
        assertEquals("2025-04-08 13:00", manager.getTaskById(task.getId()).getEndTime()
                        .format(FileBackedTaskManager.DATE_TIME_FORMATTER)
                , "Неправильно посчитался endTime ");
    }

    private Epic createEpicsWithDuration() throws ManagerAddTaskException {
        LocalDateTime startTime = LocalDateTime.parse("2025-04-08 12:00", FileBackedTaskManager.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);
        LocalDateTime startTime2 = LocalDateTime.parse("2025-04-08 14:00", FileBackedTaskManager.DATE_TIME_FORMATTER);
        Duration duration2 = Duration.ofMinutes(120);
        Epic epic = new Epic("Таск", "Описание таска", TaskStatus.NEW);
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Сабтаск", "Описание сабтаска", TaskStatus.NEW, startTime2, duration);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Описание сабтаска 2", TaskStatus.NEW, startTime, duration2);
        manager.addSubtask(subtask, epic);
        manager.addSubtask(subtask2, epic);
        return epic;
    }

    @Test
    void setEpicStartTimeAndDuration() throws ManagerAddTaskException {
        Epic epic = createEpicsWithDuration();
        assertEquals("2025-04-08 15:00", manager.getEpicById(epic.getId()).getEndTime()
                        .format(FileBackedTaskManager.DATE_TIME_FORMATTER)
                , "Неправильно посчитался endTime у Эпика");
    }

    @Test
    void testGetPrioritizedTasks() throws ManagerAddTaskException {
        Epic epic = createEpicsWithDuration();
        List<Task> times = manager.getPrioritizedTasks();
        assertEquals(2, times.size(), "Метод getPrioritizedTasks вернул неправильное количество элементов");
        assertTrue(times.getFirst().getStartTime().isBefore(times.getLast().getStartTime())
                , "Неправильная сортировка задач в методе getPrioritizedTasks");
        assertTrue(epic.getSubtasks().get(2).getStartTime().isAfter(epic.getSubtasks().get(3).getStartTime())
                , "В эпике хранятся сабтаски не в порядке добавления");
    }

    @Test
    void testTimeIntersections() throws ManagerAddTaskException {
        LocalDateTime startTime = LocalDateTime.parse("2025-04-08 12:30", FileBackedTaskManager.DATE_TIME_FORMATTER);
        LocalDateTime startTime2 = LocalDateTime.parse("2025-04-08 16:00", FileBackedTaskManager.DATE_TIME_FORMATTER);
        LocalDateTime startTime3 = LocalDateTime.parse("2025-04-08 11:00", FileBackedTaskManager.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);
        Epic epic = createEpicsWithDuration();
        Task task = new Task("Таск", "Описание", TaskStatus.NEW, startTime, duration);
        Task task2 = new Task("Таск", "Описание", TaskStatus.NEW, startTime2, duration);
        Task task3 = new Task("Таск", "Описание", TaskStatus.NEW, startTime3, duration);
        boolean isIntersections = manager.isTimeIntersections(task);
        boolean isIntersections2 = manager.isTimeIntersections(task2);
        boolean isIntersections3 = manager.isTimeIntersections(task3);
        assertTrue(isIntersections
                , "Некорректная работа проверки пересечений по времени. Время старта после другой задачи и до её окончания.");
        assertFalse(isIntersections2
                , "Некорректная работа проверки пересечений по времени. Дата старта равна дате окончания.");
        assertFalse(isIntersections3
                , "Некорректная работа проверки пересечений по времени. Время окочания равна дате старта другой задачи.");
    }

    @Test
    void addTaskShouldNotIntersectWithOthers() throws ManagerAddTaskException {
        LocalDateTime startTime = LocalDateTime.parse("2025-04-08 12:30", FileBackedTaskManager.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);
        Epic epic = createEpicsWithDuration();
        Task task = new Task("Таск", "Описание", TaskStatus.NEW, startTime, duration);
        assertThrows(ManagerAddTaskException.class, () -> manager.addTask(task)
                , "Разрешили пересечение по времени с другой задачей");
    }

    @Test
    void addSubtaskShouldNotIntersectWithOthers() throws ManagerAddTaskException {
        LocalDateTime startTime = LocalDateTime.parse("2025-04-08 12:30", FileBackedTaskManager.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);
        Epic epic = createEpicsWithDuration();
        Subtask subtask = new Subtask("SubТаск", "Описание подзадачи", TaskStatus.NEW, startTime, duration);
        assertThrows(ManagerAddTaskException.class, () -> manager.addSubtask(subtask, epic)
                , "Разрешили пересечение по времени с другой задачей");
    }

    @Test
    void updateTaskShouldNotIntersectWithOthers() throws ManagerAddTaskException {
        LocalDateTime startTime = LocalDateTime.parse("2025-04-08 10:00", FileBackedTaskManager.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);
        Epic epic = createEpicsWithDuration();
        Task task = new Task("Таск", "Описание", TaskStatus.NEW, startTime, duration);
        manager.addTask(task);
        task.setStartTime(startTime.plusMinutes(120));
        assertThrows(ManagerAddTaskException.class, () -> manager.updateTask(task)
                , "Разрешили пересечение задачи по времени с другой задачей при изменении даты старта");
        task.setStartTime(startTime.plusMinutes(10));
        assertDoesNotThrow(() -> manager.updateTask(task)
                , "Не разрешили изменить задачу без пересечения по времени с другой задачей при изменении даты старта");
    }

    @Test
    void updateSubaskShouldNotIntersectWithOthers() throws ManagerAddTaskException {
        LocalDateTime startTime = LocalDateTime.parse("2025-04-08 10:00", FileBackedTaskManager.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);
        Epic epic = createEpicsWithDuration();
        Subtask subtask = new Subtask("SubТаск", "Описание подзадачи", TaskStatus.NEW, startTime, duration);
        manager.addSubtask(subtask, epic);
        subtask.setStartTime(startTime.plusMinutes(120));
        assertThrows(ManagerAddTaskException.class, () -> manager.updateSubtask(subtask)
                , "Разрешили пересечение подзадачи по времени с другой задачей при изменении даты старта");
        subtask.setStartTime(startTime.minusHours(3));
        assertDoesNotThrow(() -> manager.updateSubtask(subtask)
                , "Не разрешили изменить подзадачу без пересечения по времени с другой задачей при изменении даты старта");
    }
}
