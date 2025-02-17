package manager;

import task.Task;

import java.util.ArrayList;

public interface HistoryManager {
    public <T extends Task> void add(T task);

    public ArrayList<Task> getHistory();
}
