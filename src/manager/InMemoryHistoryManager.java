package manager;

import model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> tasksHistory = new HashMap<>();
    private Node<Task> head = null;
    private Node<Task> tail = null;

    @Override
    public void addToHistory(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(tasksHistory.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();
        Node<Task> curNode = head;

        while (curNode != null) {
            tasksList.add(curNode.data);
            curNode = curNode.next;
        }
        return tasksList;
    }

    private void linkLast(Task data) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, data, null);
        tail = newNode;
        tasksHistory.put(data.getId(), newNode);
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    private void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        } else {
            final Node<Task> next = node.next;
            final Node<Task> prev = node.prev;
            node.data = null;
            if (node == head && node == tail) {
                head = null;
                tail = null;
            } else if (node == head) {
                head = head.next;
                head.prev = null;
            } else if (node == tail) {
                tail = tail.prev;
                tail.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }
        }
    }

    private static class Node<T> {
        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }
}