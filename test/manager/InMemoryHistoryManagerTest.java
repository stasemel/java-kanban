package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {

    InMemoryHistoryManager historyManager;

    @BeforeEach
    void initializeHistory() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addTask() {
        Task task = new Task("Первый таск", "Описание", TaskStatus.NEW);
        task.setId(10);
        historyManager.add(task);
        final ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "Не создалась история");
        assertEquals(1, history.size(), "Не правильное количество записей в истории");
        assertEquals(task, history.getFirst(), "Задачи не совпадают");
    }

    @Test
    void changeTaskDoNotChangeTaskInHistory() {
        Task task = new Task("Первый таск", "Описание", TaskStatus.NEW);
        historyManager.add(task);
        task.setName("Измененное имя");
        final ArrayList<Task> history = historyManager.getHistory();
        assertNotEquals(task.getName(), history.getFirst().getName(), "Изменились данные в истории");
    }
}