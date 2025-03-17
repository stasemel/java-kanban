package task;

import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        subtasks = new HashMap<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public Epic cloneTask() {
        Epic outEpic = new Epic(getName(), getDescription(), getStatus());
        outEpic.setId(getId());
        outEpic.setId(this.getId());
        for (Subtask subtask : subtasks.values()) {
            Subtask outSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus());
            outSubtask.setId(subtask.getId());
            outSubtask.setEpic(outEpic);
            outEpic.subtasks.put(subtask.getId(), outSubtask);
        }
        return outEpic;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                '}';
    }
}
