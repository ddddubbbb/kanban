package http.server;

import com.sun.net.httpserver.HttpServer;
import http.TasksHandler;
import managers.Managers;
import managers.task.FileBackedTasksManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final static int PORT = 8080;
    private static HttpServer taskServer;

    public HttpTaskServer() throws IOException {
        this(Managers.getBackedDefault());
    }

    public HttpTaskServer(FileBackedTasksManager fBTasksManager) throws IOException {
        taskServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        taskServer.createContext("/tasks/", new TasksHandler(fBTasksManager));
    }

    public void start() {
        System.out.println("Запускаем сервер Задач " + PORT);
        System.out.println("http://localhost:" + PORT + "/tasks/");
        taskServer.start();
    }

    public void stop() {
        taskServer.stop(1);
        System.out.println("Остановили сервер на порту " + PORT);
    }
}


