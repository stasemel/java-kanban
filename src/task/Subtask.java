package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private Epic epic;

    public Subtask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Subtask(String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public Subtask cloneTask() {
        Subtask outSubtask;
        Epic epic = getEpic();
        if (epic == null) {
            outSubtask = new Subtask(getName(), getDescription(), getStatus(), getStartTime(), getDuration());
        } else {
            outSubtask = epic.cloneTask().getSubtasks().get(getId()); //через клонирование эпика, так можно избежать рекурсии
        }
        outSubtask.setId(getId());
        return outSubtask;
    }

    public String toString() {
        return "Subtask{" +
                ((epic == null) ? "epic=null" : "epic=" + epic.getId()) +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                '}';
    }
}
