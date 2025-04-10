package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {

    InMemoryHistoryManager manager;

    @Test
    void addTask() {
        Task task = new Task("Первый таск", "Описание", TaskStatus.NEW);
        task.setId(10);
        manager.add(task);
        final ArrayList<Task> history = manager.getHistory();
        assertNotNull(history, "Не создалась история");
        assertEquals(1, history.size(), "Не правильное количество записей в истории");
        assertEquals(task, history.getFirst(), "Задачи не совпадают");
    }

    @Test
    void addEpic() {
        Epic epic = new Epic("Первый эпик", "Описание", TaskStatus.NEW);
        epic.setId(10);
        Subtask subtask = new Subtask("Субтаск 1", "Описание к субтаску", TaskStatus.IN_PROGRESS);
        subtask.setId(11);
        epic.addSubtask(subtask);
        manager.add(epic);
        final ArrayList<Task> history = manager.getHistory();
        Epic first = (Epic) history.getFirst();
        assertNotNull(history, "Не создалась история");
        assertEquals(1, history.size(), "Неправильное количество записей в истории");
        assertEquals(epic, first, "Задачи не совпадают");
        assertEquals(epic.getSubtasks().get(11), first.getSubtasks().get(11), "Сабтаски не совпадают");
    }

    @Test
    void changeTaskAfterGetHistory() {
        Task task = new Task("Первый таск", "Описание", TaskStatus.NEW);
        manager.add(task);
        task.setName("Измененное имя");
        final ArrayList<Task> history = manager.getHistory();
        assertNotEquals(task.getName(), history.getFirst().getName(), "Изменились данные в истории");
    }

    @Test
    void addElevenDifferentTasks() {
        for (int i = 1; i < 12; i++) {
            Task tasknext = new Task("Таск " + i, "Описание", TaskStatus.NEW);
            tasknext.setId(i);
            manager.add(tasknext);
        }
        final ArrayList<Task> history = manager.getHistory();
        assertNotNull(history, "Не создалась история");
        assertEquals(11, history.size(), "Неправильное количество записей в истории");
    }

    @Test
    void shouldBeLastWhenAdded() {
        Task task = new Task("Таск " + 0, "Описание", TaskStatus.NEW);
        task.setId(0);
        manager.add(task);
        for (int i = 1; i < 10; i++) {
            Task tasknext = new Task("Таск " + i, "Описание", TaskStatus.NEW);
            tasknext.setId(i);
            manager.add(tasknext);
        }
        manager.add(task);
        final ArrayList<Task> history = manager.getHistory();
        assertNotNull(history, "Не создалась история");
        assertEquals(10, history.size(), "Неправильное количество записей в истории");
        assertEquals(history.get(9), task, "Не добавился в последнюю позицию");
    }

    @Test
    void removeFromHistory() {
        Task task = new Task("Таск " + 0, "Описание", TaskStatus.NEW);
        task.setId(0);
        manager.add(task);
        manager.remove(0);
        final ArrayList<Task> history = manager.getHistory();
        assertTrue(history.isEmpty(), "Не удалилась запись");
    }

    @BeforeEach
    public void createManager() {
        manager = new InMemoryHistoryManager();
    }
}