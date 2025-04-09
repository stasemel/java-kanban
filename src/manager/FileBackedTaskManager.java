package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    public static final String DELIMITER = ",";
    private static final String[] HEADER = {"id", "type", "name", "status", "description", "epic", "startTime", "duration"};

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static final Charset CHARSET = StandardCharsets.UTF_8; // как вариант - Charset.forName("UTF8");

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public static void main(String[] args) throws ManagerAddTaskException, IOException {
        File file = File.createTempFile("main", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW);
        Task task3 = new Task("Задача 3", "Описание 3", TaskStatus.NEW);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", TaskStatus.NEW);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", TaskStatus.NEW);
        Subtask subtask11 = new Subtask("Сабтаск 1 эпика 1", "Описание сабтаска 11", TaskStatus.NEW);
        Subtask subtask12 = new Subtask("Сабтаск 1 эпика 2", "Описание сабтаска 12", TaskStatus.NEW);
        Subtask subtask21 = new Subtask("Сабтаск 2 эпика 1", "Описание сабтаска 21", TaskStatus.NEW);
        Subtask subtask22 = new Subtask("Сабтаск 2 эпика 2", "Описание сабтаска 22", TaskStatus.NEW);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask11, epic1);
        manager.addSubtask(subtask21, epic1);
        manager.addSubtask(subtask12, epic2);
        manager.addSubtask(subtask22, epic2);
        FileBackedTaskManager loadedManager = loadFromFile(file);
        System.out.println(manager);
        System.out.println(loadedManager);
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerAddTaskException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        String[] lines;
        HashMap<Integer, ArrayList<Subtask>> addSubtasks = new HashMap<>();
        try {
            String str = Files.readString(file.toPath());
            lines = str.split("\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (lines.length < 1) return fileBackedTaskManager; //нет записей в файле
        String[] headers = lines[0].split(DELIMITER);
        for (int i = 1; i < lines.length; i++) {
            String[] fields = lines[i].split(DELIMITER);
            HashMap<String, String> properties = new HashMap<>();
            for (int j = 0; j < headers.length; j++) {
                String value;
                if (j < fields.length) {
                    value = fields[j];
                } else {
                    value = "";
                }
                properties.put(headers[j], value);
            }
            switch (properties.get("type")) {
                case "TASK":
                    Task task = new Task(properties.get("name"), properties.get("description"),
                            TaskStatus.valueOf(properties.get("status")));
                    if ((properties.get("startTme") != null) && (!properties.get("startTme").isBlank())) {
                        task.setStartTime(LocalDateTime.parse(properties.get("startTime"), DATE_TIME_FORMATTER));
                    }
                    if ((properties.get("duration") != null) && (!properties.get("duration").isBlank())) {
                        task.setDuration(Duration.ofMinutes(Long.parseLong(properties.get("duration"))));
                    }
                    task.setId(Integer.valueOf(properties.get("id")));
                    fileBackedTaskManager.addTask(task);
                    break;

                case "EPIC":
                    Epic epic = new Epic(properties.get("name"), properties.get("description"),
                            TaskStatus.valueOf(properties.get("status")));
                    epic.setId(Integer.valueOf(properties.get("id")));
                    fileBackedTaskManager.addEpic(epic);
                    break;

                case "SUBTASK":
                    Integer epicid;
                    try {
                        epicid = Integer.valueOf(properties.get("epic"));
                    } catch (NumberFormatException e) {
                        break; //нет эпика, пропускаем сабтаск, как некорректный
                    }
                    Epic subtaskEpic = fileBackedTaskManager.getEpicById(epicid);
                    Subtask subtask = new Subtask(properties.get("name"), properties.get("description"),
                            TaskStatus.valueOf(properties.get("status"))
                    );
                    if ((properties.get("startTme") != null) && (!properties.get("startTme").isBlank())) {
                        subtask.setStartTime(LocalDateTime.parse(properties.get("startTime"), DATE_TIME_FORMATTER));
                    }
                    if ((properties.get("duration") != null) && (!properties.get("duration").isBlank())) {
                        subtask.setDuration(Duration.ofMinutes(Long.parseLong(properties.get("duration"))));
                    }
                    subtask.setId(Integer.valueOf(properties.get("id")));
                    if (subtaskEpic != null) {
                        fileBackedTaskManager.addSubtask(subtask, subtaskEpic);
                    } else {
                        if (!addSubtasks.containsKey(epicid)) {
                            addSubtasks.put(epicid, new ArrayList<>());
                        }
                        addSubtasks.get(epicid).add(subtask);
                    }
                    break;
            }
            for (Integer epicid : addSubtasks.keySet()) {
                Epic epic = fileBackedTaskManager.getEpicById(epicid);
                if (epic != null) {
                    for (Subtask subtask : addSubtasks.get(epicid)) {
                        fileBackedTaskManager.addSubtask(subtask, epic);
                    }
                }
            }
        }
        return fileBackedTaskManager;
    }

    private void save() {

        try (FileWriter fileWriter = new FileWriter(file, CHARSET)) {
            fileWriter.write(String.format("%s\n", String.join(DELIMITER, HEADER))); //header

            for (Task task : getAllTasks()) {
                fileWriter.write(String.format("%s\n", getFieldsForRow(task)));
            }
            for (Epic epic : getAllEpics()) {
                fileWriter.write(String.format("%s\n", getFieldsForRow(epic)));
            }
            for (Subtask subtask : getAllSubtasks()) {
                fileWriter.write(String.format("%s\n", getFieldsForRow(subtask)));
            }

        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private <T extends Task> String getFieldsForRow(T task) {
        String[] fields = new String[HEADER.length];
        int count = 0;
        for (String field : HEADER) {
            String value = "";
            switch (field) {
                case "id":
                    value = task.getId().toString();
                    break;
                case "type":
                    if (task instanceof Subtask) {
                        value = TaskType.SUBTASK.toString();
                    } else if (task instanceof Epic) {
                        value = TaskType.EPIC.toString();
                    } else {
                        value = TaskType.TASK.toString();
                    }
                    break;
                case "name":
                    value = task.getName();
                    break;
                case "description":
                    value = task.getDescription();
                    break;
                case "status":
                    value = task.getStatus().toString();
                    break;
                case "epic":
                    if ((task instanceof Subtask) && (((Subtask) task).getEpic() != null)) {
                        value = ((Subtask) task).getEpic().getId().toString();
                    } else {
                        value = "";
                    }
                    break;
                case "startTime":
                    if (task.getStartTime() == null) {
                        value = "";
                    } else {
                        value = task.getStartTime().format(DATE_TIME_FORMATTER);
                    }
                    break;
                case "duration":
                    if (task.getDuration() == null) {
                        value = "";
                    } else {
                        value = String.valueOf(task.getDuration().toMinutes());
                    }
            }
            fields[count] = value;
            count++;
        }
        return String.join(DELIMITER, fields);
    }

    @Override
    public void addTask(Task task) throws ManagerAddTaskException {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) throws ManagerAddTaskException {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask, Epic epic) throws ManagerAddTaskException {
        super.addSubtask(subtask, epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void updateTask(Task task) throws ManagerAddTaskException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerAddTaskException {
        super.updateSubtask(subtask);
        save();
    }
}
