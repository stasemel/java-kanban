package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    @Test
    void shouldCreateTwoEpicsWithEqualsIdIsEquals(){
        Epic epic1=new Epic("Первый", "Описание",TaskStatus.NEW);
        epic1.setId(10);
        Epic epic2=new Epic("Второй", "Описание второго", TaskStatus.NEW);
        epic2.setId(10);
        assertEquals(epic1,epic2,"Задачи с одинаковым id не совпадают");
    }
}