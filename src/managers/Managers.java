package managers;

import adapters.LocalDateTimeAdapter;
import adapters.DurationAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HTTPTaskManager;
import http.server.KVServer;
import managers.history.HistoryManager;
import managers.task.FileBackedTasksManager;
import managers.task.InMemoryTaskManager;
import managers.task.TaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {

    public static TaskManager getDefault(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static FileBackedTasksManager getBackedDefault() throws IOException {
        Path path = Paths.get("storage.csv");
        File file = path.getFileName().toFile();

        if (!Files.exists(path.getFileName())) {
            Files.createFile(path);
        }
        return new FileBackedTasksManager(file);
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }


    public static KVServer getDefaultKVServer() throws IOException {
        return new KVServer();
    }

//
//    public static HTTPTaskManager getDefaultHttpTaskManager() throws IOException, InterruptedException {
//        return new HTTPTaskManager(KVServer.PORT);

    public static HTTPTaskManager getDefaultHTTPTM(HistoryManager historyManager) throws IOException, InterruptedException {
        return new HTTPTaskManager(historyManager, "http://localhost:" + KVServer.PORT);


    }
}
