package managers;

import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import managers.task.FileBackedTasksManager;
import managers.task.InMemoryTaskManager;
import managers.task.TaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getHistoryDefault() {

        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getBackedDefault() throws IOException {
        Path path = Paths.get("storage.csv");
        File file = path.getFileName().toFile();

        if (!Files.exists(path.getFileName())) {
            Files.createFile(path);
        }
        return new FileBackedTasksManager(file);

    }
}