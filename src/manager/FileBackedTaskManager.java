package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;
    public static final String DELIMITER = ",";
    private static final String[] HEADER = {"id", "type", "name", "status", "description", "epic"};

    public static final Charset CS = Charset.forName("UTF8");

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public static void main(String[] args) {
        System.out.println(String.join(DELIMITER, HEADER));
    }

    private void save() {

        try (FileWriter fileWriter = new FileWriter(file, CS)) {
            fileWriter.write(String.format("%s\n", String.join(DELIMITER, HEADER))); //header

            for (Task task : getAllTasks()) {
                fileWriter.write(String.format("%s\n", getFieldsForRow(task)));
            }
            for (Epic epic:getAllEpics()){
                fileWriter.write(String.format("%s\n",getFieldsForRow(epic)));
            }
            for(Subtask subtask:getAllSubtasks()){
                fileWriter.write(String.format("%s\n",getFieldsForRow(subtask)));
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
            }
            fields[count] = value;
            count++;
        }
        return String.join(DELIMITER, fields);
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask, Epic epic) {
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
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }
}
