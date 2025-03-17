package manager;

import task.Task;

import java.util.Objects;

class Node {
    private Node prev;
    private Node next;
    private Task task;

    public Node(Node prev, Node next, Task task) {
        this.prev = prev;
        this.next = next;
        this.task = task;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(prev, node.prev) && Objects.equals(next, node.next) && Objects.equals(task, node.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prev, next, task);
    }
}
