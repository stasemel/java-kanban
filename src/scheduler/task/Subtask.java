package scheduler.task;

public class Subtask extends Task {

    private Epic epic;

    public Subtask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public Subtask cloneTask() {
        Subtask outSubtask = new Subtask(getName(), getDescription(), getStatus());
        outSubtask.setId(getId());
        return outSubtask;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epic=" + epic.getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                '}';
    }
}
