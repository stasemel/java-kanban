package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        subtasks = new HashMap<>();
    }

    public void calcEndTimeEpic() {
        if (subtasks.isEmpty()) {
            return;
        }
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        Duration duration = null;
        List<Subtask> subtaskWithStartTime = subtasks.values().stream()
                .filter(task -> task.getStartTime() != null)
                .sorted(Comparator.comparing(Task::getStartTime))
                .toList();
        if (!subtaskWithStartTime.isEmpty()) {
            startTime = subtaskWithStartTime.getFirst().getStartTime();
            endTime = subtaskWithStartTime.getLast().getEndTime();
        }
        if ((startTime != null) && (endTime != null)) {
            duration = Duration.between(startTime, endTime);
        }
        this.setStartTime(startTime);
        this.setDuration(duration);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        calcEndTimeEpic();
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
            Subtask outSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getStartTime(), subtask.getDuration());
            outSubtask.setId(subtask.getId());
            outSubtask.setEpic(outEpic);
            outEpic.subtasks.put(subtask.getId(), outSubtask);
        }
        outEpic.calcEndTimeEpic();
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
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                '}';
    }
}
