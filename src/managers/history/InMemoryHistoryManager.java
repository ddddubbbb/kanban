package managers.history;

import node.Node;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> customHistoryTask = new CustomLinkedList<>();
    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();


    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        customHistoryTask.linkLast(task);
        historyMap.put(task.getId(), customHistoryTask.tail);

    }

    @Override
    public List<Task> getHistory() {
        return customHistoryTask.getTasks();
    }

    private static class CustomLinkedList<Task> {
        private Node<Task> head;

        private Node<Task> tail;

        private int size;

        private void linkLast(Task task) {

            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node<>(oldTail, task, null);
            tail = newNode;
            if (oldTail == null)
                head = newNode;
            else
                oldTail.next = newNode;
            size++;
        }

        private List<Task> getTasks() {
            List<Task> historyList = new ArrayList<>();

            Node<Task> element = head;

            for (int i = 0; i < size; i++) {
                if (element != null) {
                    historyList.add(element.task);
                    element = element.next;
                }
            }
            return historyList;
        }

        private void removeNode(Node<Task> node) {
            final Node<Task> next = node.next;
            final Node<Task> prev = node.prev;

            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                node.prev = null;
            }
            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }
            node.task = null;
            size--;
        }
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            customHistoryTask.removeNode(historyMap.get(id));
            historyMap.remove(id);
        }
    }
}