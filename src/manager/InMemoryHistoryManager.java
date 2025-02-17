package manager;

import task.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    private static final int MAX_HISTORY = 10;
    private ArrayList<Task> historyOfViews = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> history=new ArrayList<>();
        for (Task task:historyOfViews){
            history.add(task.cloneTask());
        }
        return history;
    }

    @Override
    public  <T extends Task> void add(T task) {
        if(historyOfViews.size()==MAX_HISTORY){
            historyOfViews.remove(0);
        }
        historyOfViews.add(task);
    }
}
