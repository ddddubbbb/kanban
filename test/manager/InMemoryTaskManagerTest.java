package manager;

import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import managers.task.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        manager = new InMemoryTaskManager(historyManager);
    }
}