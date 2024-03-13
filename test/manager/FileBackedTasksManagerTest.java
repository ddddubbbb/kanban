package manager;

import enums.TaskStatus;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import managers.task.FileBackedTasksManager;
import managers.task.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    public static final Path path = Path.of("test.csv");
    File file = new File(String.valueOf(path));

    @BeforeEach
    public void beforeEach() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        manager = new InMemoryTaskManager(historyManager);

    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(path);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    public void shouldSaveAndLoadTasks() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        Instant instant = Instant.now();

        Task task = new Task("taskFB", 0, "description", TaskStatus.NEW,
                (instant.plusSeconds(100000)).atZone(ZoneId.systemDefault()).toLocalDateTime(), Duration.ofMinutes(1));
        fileManager.createTask(task);

        Epic epic = new Epic("epicFB", 0, "description", null, null);
        fileManager.createEpic(epic);

        SubTask subTask = new SubTask("SubTaskFB", 0, "description", epic.getId(), TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime(), Duration.ofMinutes(33));
        fileManager.createSubTask(subTask);

        FileBackedTasksManager fileManager1 = new FileBackedTasksManager(file);

        fileManager1.loadFromFile();
        assertEquals(List.of(task), fileManager1.getTasks());
        assertEquals(List.of(epic), fileManager1.getEpics());
        assertEquals(List.of(subTask), fileManager1.getSubTasks());
    }

    @Test
    public void shouldSaveAndLoadEmptyTasksEpicsSubtasks() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);

        fileManager.save();
        fileManager.loadFromFile();

        assertEquals(Collections.EMPTY_LIST, fileManager.getTasks());
        assertEquals(Collections.EMPTY_LIST, fileManager.getEpics());
        assertEquals(Collections.EMPTY_LIST, fileManager.getSubTasks());
        assertEquals(Collections.EMPTY_LIST, new ArrayList<>(fileManager.getFileBackedTasksMap().values()));
    }

    @Test
    public void shouldSaveAndLoadEmptyHistory() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);

        fileManager.save();
        fileManager.loadFromFile();

        assertEquals(Collections.EMPTY_LIST, fileManager.getHistory());
    }
}