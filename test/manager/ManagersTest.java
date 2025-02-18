package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ManagersTest {
    @Test
    void shouldGetDefaultReturnsTaskManagerObject() {
        TaskManager taskManager = Managers.getDefault();
        assertInstanceOf(InMemoryTaskManager.class, taskManager, "Получен не корректный класс getDefault()");
    }

    @Test
    void shouldGetDefaultHistoryReturnsHistoryManagerObject() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertInstanceOf(InMemoryHistoryManager.class, historyManager, "Получен не корректный класс getDefaultHistory()");
    }
}