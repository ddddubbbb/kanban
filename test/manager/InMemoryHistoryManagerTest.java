package manager;

import enums.TaskStatus;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import managers.task.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    HistoryManager manager;
    Instant instant = Instant.now();

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldAddTasksToHistory() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(manager);
        Task task1 = taskManager.createTask(new Task("taskH", 0, "description", TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(10), Duration.ofMinutes(1)));
        manager.add(task1);
        Task task2 = taskManager.createTask(new Task("taskH", 0, "description", TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(20), Duration.ofMinutes(2)));
        manager.add(task2);
        Task task3 = taskManager.createTask(new Task("taskH", 0, "description", TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(30), Duration.ofMinutes(3)));
        manager.add(task3);

        assertEquals(List.of(task1, task2, task3), manager.getHistory());
    }

    @Test
    public void shouldRemoveTaskAndGetHistory() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(manager);
        Task task1 = taskManager.createTask(new Task("taskH", 0, "description", TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(10), Duration.ofMinutes(1)));
        manager.add(task1);
        Task task2 = taskManager.createTask(new Task("taskH", 0, "description", TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(20), Duration.ofMinutes(2)));
        manager.add(task2);
        Task task3 = taskManager.createTask(new Task("taskH", 0, "description", TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(30), Duration.ofMinutes(3)));
        manager.add(task3);
        manager.remove(task2.getId());
        assertEquals(List.of(task1, task3), manager.getHistory());
    }

    @Test
    public void shouldRemoveOneTaskAndGetEmptyHistory() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(manager);
        Task task = taskManager.createTask(new Task("taskH", 0, "description", TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(10), Duration.ofMinutes(1)));
        manager.add(task);
        manager.remove(task.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void shouldGetEmptyHistory() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(manager);
        Task task1 = taskManager.createTask(new Task("taskH", 0, "description", TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(10), Duration.ofMinutes(1)));
        manager.add(task1);
        Task task2 = taskManager.createTask(new Task("taskH", 0, "description", TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(20), Duration.ofMinutes(2)));
        manager.add(task2);
        Task task3 = taskManager.createTask(new Task("taskH", 0, "description", TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(30), Duration.ofMinutes(3)));
        manager.add(task3);
        manager.remove(task1.getId());
        manager.remove(task2.getId());
        manager.remove(task3.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void shouldNotRemoveTaskWithoutID() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(manager);
        Task task = taskManager.createTask(new Task("taskH", 0, "description", TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(10), Duration.ofMinutes(1)));
        manager.add(task);
        manager.remove(0);
        assertEquals(List.of(task), manager.getHistory());
    }
}