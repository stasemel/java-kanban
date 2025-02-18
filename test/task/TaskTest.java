package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    @Test
    void shouldCreateTwoTaskWithEqualsIdIsEquals() {
        Task task1 = new Task("Первый таск", "Описание первого таска", TaskStatus.NEW);
        task1.setId(10);
        Task task2 = new Task("Второй таск", "Описание второго таска", TaskStatus.NEW);
        task2.setId(10);
        assertEquals(task1, task2, "Задачи с одинаковым id не совпадают");
    }
}