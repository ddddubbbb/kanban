package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyTask = new ArrayList<>();
    private static final int VALUE_NUMBERS = 10;


    @Override
    public void add(Task task) {
        if (!(task == null)) {
            historyTask.add(task);
            if (historyTask.size() > VALUE_NUMBERS) {
                historyTask.remove(0);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyTask;
    }
}
