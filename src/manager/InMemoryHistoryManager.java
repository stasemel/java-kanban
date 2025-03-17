package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY = 10;
    final private ArrayList<Task> historyOfViews = new ArrayList<>();
    final private HashMap<Integer, Node> hiMap = new HashMap<>();
    private Node first;
    private Node last;
    private int count = 0;

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> history = new ArrayList<>();
        Node node = first;
        while (node != null) {
            history.add(node.getTask().cloneTask());
            node = node.getNext();
        }
        return history;
    }

    @Override
    public <T extends Task> void add(T task) {
        linkLast(task);
        if (historyOfViews.size() == MAX_HISTORY) {
            historyOfViews.removeFirst();
        }
        historyOfViews.add(task.cloneTask()); //запоминаем в истории текущее состояние задачи
    }

    @Override
    public void remove(int id) {
        Node node = hiMap.get(id);
        if (node == null) return;
        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        } else {
            first = node.getNext();
        }
        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());
        } else {
            last = node.getPrev();
        }
        if (count > 0) {
            hiMap.remove(id);
            count--;
        }
    }

    private void linkLast(Task task) {
        if (hiMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        Node node = new Node(last, null, task.cloneTask());
        if (first == null) {
            first = node;
        }
        if (last == null) {
            last = node;
        } else {
            last.setNext(node);
            last = node;
        }
        hiMap.put(task.getId(), node);
        count++;
    }

}
