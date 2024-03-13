package http;

import enums.TaskStatus;
import http.server.KVServer;
import managers.Managers;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import managers.task.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HTTPTaskManagerTest {
    private KVServer server;
    private TaskManager manager;
    Instant instant = Instant.now();

    @BeforeEach
    public void createManager() {
        try {
            server = new KVServer();
            server.start();
            HistoryManager historyManager = new InMemoryHistoryManager();
            manager = Managers.getDefault(historyManager);
        } catch (IOException e) {
            System.out.println("Ошибка при создании менеджера");
        }
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void shouldLoadTasks() {
        Task task1 = new Task("taskHTTPTM", 0, "description", TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(10), Duration.ofMinutes(1));
        manager.createTask(task1);
        Task task2 = new Task("taskHTTPTM", 0, "description", TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(15), Duration.ofMinutes(1));
        manager.createTask(task2);

        manager.getTaskForId(task1.getId());
        manager.getTaskForId(task2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getTasks(), list);
    }

    @Test
    public void shouldLoadEpics() {
        Epic epic1 = new Epic("epicHTTPTM", 0, "description", null, null);
        manager.createEpic(epic1);
        Epic epic2 = new Epic("epicHTTPTM", 0, "description", null, null);
        manager.createEpic(epic2);

        manager.getEpicForId(epic1.getId());
        manager.getEpicForId(epic2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getEpics(), list);
    }

    @Test
    public void shouldLoadSubtasks() {
        Epic epic1 = new Epic("epicHTTPTM", 0, "description", null, null);
        manager.createEpic(epic1);
        SubTask subtask1 = new SubTask("subTaskHTTPTM1", 0, "description", epic1.getId(), TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime(), Duration.ofMinutes(3));
        SubTask subtask2 = new SubTask("subTaskHTTPTM2", 0, "description", epic1.getId(), TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(10), Duration.ofMinutes(33));

        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);
        manager.getSubTaskForId(subtask1.getId());
        manager.getSubTaskForId(subtask2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getSubTasks(), list);
    }
}