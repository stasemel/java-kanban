package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void shouldCreateTwoEpicsWithEqualsIdIsEquals(){
        Subtask subtask1=new Subtask("Первый", "Описание",TaskStatus.NEW);
        subtask1.setId(10);
        Subtask subtask2=new Subtask("Второй", "Описание второго", TaskStatus.NEW);
        subtask2.setId(10);
        assertEquals(subtask1,subtask2,"Задачи с одинаковым id не совпадают");
    }

}