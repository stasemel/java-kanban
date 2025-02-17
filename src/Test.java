import manager.InMemoryTaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;

public class Test {
    public static void testTaskManager() {
        System.out.println("Поехали!");

        //Создаём TaskManager, добавляем в него несколько задач типа Task
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Первый таск", "Создать новую задачу", TaskStatus.NEW);
        Task task2 = new Task("Второй таск", "Создать вторую новую задачу", TaskStatus.IN_PROGRESS);
        Task task3 = new Task("Третий таск", "Создать третью новую задачу", TaskStatus.DONE);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        //Получаем список Task
        ArrayList<Task> listTasks = manager.getAllTasks();
        printTestResult("Создание менеджера, добавление тасков, получение списка тасков", Integer.toString(listTasks.size()), Integer.toString(3));

        //Поиск Task по существующему id
        Task foundTask = manager.getTask(task1.getId());
        printTestResult("Поиск Task по существующему id ", foundTask.getId(), task1.getId());

        //Поиск Task по несуществующему id
        Task notFoundTask = manager.getTask(0);
        printTestResult("Поиск Task по существующему id", notFoundTask, null);

        //Проверка обновления Task:
        task1.setName("Новое имя первого таска");
        task1.setDescription("Проверить обновление данных первой задачи");
        task1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task1);
        printTestResult("Проверить обновление данных таска. Свойство name", manager.getTask(task1.getId()).getName(), task1.getName());
        printTestResult("Проверить обновление данных таска. Свойство description",
                manager.getTask(task1.getId()).getDescription(),
                task1.getDescription());
        printTestResult("Проверить обновление данных таска. Свойство status", manager.getTask(task1.getId()).getStatus(), task1.getStatus());

        //Удаление по идентификатору
        int deleteId = task2.getId();
        manager.deleteTaskById(deleteId);
        printTestResult("Удаление по идентификатору: ", manager.getTask(deleteId), null);

        //Удаление по несуществующему идентификатору
        manager.deleteTaskById(0);
        printTestResult("Удаление по несуществующему идентификатору: ", manager.getAllTasks().size(), 2);

        //Удаление всех задач
        manager.deleteAllTasks();
        printTestResult("Удаление всех задач: ", manager.getAllTasks().size(), 0);

        //Создаем эпики и проверяем установку статусов
        Epic epic1 = new Epic("Первый эпик", "Проверяем создание первого эпика", TaskStatus.DONE);
        Epic epic2 = new Epic("Второй эпик", "Проверяем создание второго эпика", TaskStatus.IN_PROGRESS);
        Epic epic3 = new Epic("Третий эпик", "Проверяем создание третьего эпика", TaskStatus.NEW);
        manager.addEpic(epic1);
        printTestResult("Создание эпика со статусом DONE. Проверка установки статуса NEW ", manager.getEpic(epic1.getId()).getStatus(), TaskStatus.NEW);
        manager.addEpic(epic2);
        printTestResult("Создание эпика со статусом IN_PROGRESS. Проверка установки статуса NEW ", manager.getEpic(epic2.getId()).getStatus(), TaskStatus.NEW);
        manager.addEpic(epic3);
        printTestResult("Создание эпика со статусом NEW. Проверка установки статуса NEW ", manager.getEpic(epic3.getId()).getStatus(), TaskStatus.NEW);

        //Получаем список эпиков
        ArrayList<Epic> epics = manager.getAllEpics();
        printTestResult("Получение списка эпиков", epics.size(), 3);

        //Получаем эпик по id
        printTestResult("Получаем эпик по id", manager.getEpic(epic1.getId()).getId(), epic1.getId());

        //Добавляем эпику сабтаски
        Subtask subtask1 = new Subtask("Первый сабтаск первого эпика", "Добавить первый сабтаск первому эпику", TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Второй сабтаск первого эпика", "Добавить второй сабтаск первому эпику", TaskStatus.NEW);
        Subtask subtask3 = new Subtask("Третий сабтаск первого эпика", "Добавить третий сабтаск первому эпику", TaskStatus.NEW);
        Subtask subtask21 = new Subtask("Первый сабтаск второго эпика", "Добавить первый сабтаск второму эпику", TaskStatus.IN_PROGRESS);
        Subtask subtask22 = new Subtask("Второй сабтаск второго эпика", "Добавить второй сабтаск второму эпику", TaskStatus.DONE);
        Subtask subtask23 = new Subtask("Третий сабтаск второго эпика", "Добавить третий сабтаск второму эпику", TaskStatus.NEW);
        manager.addSubtask(subtask1, epic1);
        printTestResult("Проверка статуса эпика при  добавлении 1-го сабтаска со статусом NEW", epic1.getStatus(), TaskStatus.NEW);
        manager.addSubtask(subtask2, epic1);
        printTestResult("Проверка статуса эпика при  добавлении 2-го сабтаска со статусом NEW", epic1.getStatus(), TaskStatus.NEW);
        manager.addSubtask(subtask3, epic1);
        printTestResult("Проверка статуса эпика при  добавлении 3-го сабтаска со статусом NEW", epic1.getStatus(), TaskStatus.NEW);
        manager.addSubtask(subtask21, epic2);
        printTestResult("Проверка статуса эпика при  добавлении 1-го сабтаска со статусом IN_PROGRESS", epic2.getStatus(), TaskStatus.IN_PROGRESS);
        manager.addSubtask(subtask22, epic2);
        printTestResult("Проверка статуса эпика при  добавлении 2-го сабтаска со статусом DONE", epic2.getStatus(), TaskStatus.IN_PROGRESS);
        manager.addSubtask(subtask23, epic2);
        printTestResult("Проверка статуса эпика при  добавлении 3-го сабтаска со статусом NEW", epic2.getStatus(), TaskStatus.IN_PROGRESS);

        //Меняем статусы сабтасков первого эпика
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        printTestResult("Проверка статуса эпика при изменении статуса сабтаска на IN_PROGRESS", epic1.getStatus(), TaskStatus.IN_PROGRESS);
        subtask1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);
        printTestResult("Проверка статуса эпика при изменении статуса одного сабтаска на DONE", epic1.getStatus(), TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask2);
        printTestResult("Проверка статуса эпика при изменении статуса двух сабтасков на DONE", epic1.getStatus(), TaskStatus.IN_PROGRESS);
        subtask3.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask3);
        printTestResult("Проверка статуса эпика при изменении статуса всех сабтасков на DONE", epic1.getStatus(), TaskStatus.DONE);
        //Добавляем первому эпику новый сабтаск
        Subtask subtask4 = new Subtask("Четвертый сабтаск первого эпика", "Отработать смену статусов", TaskStatus.NEW);
        manager.addSubtask(subtask4, epic1);
        printTestResult("Проверка статуса эпика со статусом DONE при добавлении нового сабтаска со статусом NEW", epic1.getStatus(), TaskStatus.IN_PROGRESS);
        printTestResult("Проверка получения списка всех сабтасков", manager.getAllSubtasks().size(), 7);
        printTestResult("Проверка получения списка всех сабтасков по эпику", manager.getSubtaskByEpic(epic1).size(), 4);
        //Удаляем четвертый сабтаск
        int deleteSubtackId = subtask4.getId();
        manager.deleteSubtaskById(deleteSubtackId);
        printTestResult("Удаление сабтаска по id", manager.getSubtask(deleteSubtackId), null);
        printTestResult("Смена статуса epic при удалении сабтаска", manager.getEpic(epic1.getId()).getStatus(), TaskStatus.DONE);

        //Проверка изменения эпика
        epic1.setName("Новое имя первого эпика");
        manager.updateEpic(epic1);
        printTestResult("Проверка изменения имени эпика", manager.getEpic(epic1.getId()).getName(), epic1.getName());

        //Удаляем второй эпик
        int deleteEpicId = epic2.getId();
        manager.deleteEpicById(deleteEpicId);
        printTestResult("Удаление эпика по id", manager.getEpic(deleteEpicId), null);
        printTestResult("Проверка удаления сабтаска при удалении эпика", manager.getSubtask(subtask21.getId()), null);

        //Удаляем все сабтаски
        manager.deleteAllSubtasks();
        printTestResult("Смена статуса epic при удалении всех сабтасков", manager.getEpic(epic1.getId()).getStatus(), TaskStatus.NEW);

        //Удаляем все эпики
        manager.deleteAllEpics();
        printTestResult("Проверка удаления всех эпиков", manager.getAllEpics().size(), 0);
    }

    private static void printTestResult(String name, Object result, Object expectedResult) {
        if (isTestOk(result, expectedResult)) {
            printTestOK(name, result);
        } else {
            printTestError(name, result, expectedResult);
        }
    }

    private static boolean isTestOk(Object result, Object expectedResult) {
        if (result == null) {
            return (expectedResult == null);
        }
        return (result.equals(expectedResult));

    }

    private static void printTestError(String name, Object result, Object expectedResult) {
        System.out.println("--- TEST ERROR: " + name);
        System.out.println("Result:");
        System.out.println(result);
        System.out.println("Expected result:");
        System.out.println(expectedResult);
    }

    private static void printTestOK(String name, Object result) {
        System.out.println("--- TEST PASS: " + name + ". result: " + result);
    }
}
