package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.Managers;
import managers.task.FileBackedTasksManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TasksHandler implements HttpHandler {
    private final FileBackedTasksManager fBTasksManager;
    private final Gson gson = Managers.getGson();

    public TasksHandler(FileBackedTasksManager fBTasksManager) {
        this.fBTasksManager = fBTasksManager;
    }

    @Override
    public void handle(HttpExchange exchange) {

        String method = exchange.getRequestMethod();
        System.out.println("Началась обработка " + method + " /hello запроса от клиента.");

        String endpoint = exchange.getRequestURI().getPath().replaceFirst("/tasks/", "");
        try {
            switch (endpoint) {
                case "task": {
                    handleTask(exchange);
                    break;
                }
                case "subtask": {
                    handleSubTask(exchange);
                    break;
                }
                case "epic": {
                    handleEpic(exchange);
                    break;
                }
                case "history": {
                    handleHistory(exchange);
                    break;
                }
                case "subtasksinepic": {
                    handleSubTasksInEpic(exchange);
                    break;
                }
                case "alltasks": {
                    handleAllTasks(exchange);
                    break;
                }
                case "tasksbytime": {
                    handleTasksByTime(exchange);
                    break;
                }
                default:
                    exchange.sendResponseHeaders(404, 0);
                    break;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void handleTasksByTime(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String path = String.valueOf(exchange.getRequestURI());

        System.out.println("Обрабатывается запрос: " + path + "\n с методом: " + requestMethod);
        if (requestMethod.equals("GET")) {

            String response = gson.toJson(fBTasksManager.getTasksStartTimeTree());
            sendText(exchange, response);
            exchange.sendResponseHeaders(200, 0);

        } else {
            System.out.println("Некорректный метод! Должен быть GET, а в запросе = " + requestMethod);
            exchange.sendResponseHeaders(405, 0);
        }
    }

    private void handleAllTasks(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String path = String.valueOf(exchange.getRequestURI());

        System.out.println("Обрабатывается запрос: " + path + "\n с методом: " + requestMethod);
        if (requestMethod.equals("GET")) {

            String response = gson.toJson(fBTasksManager.getFileBackedTasksMap());
            sendText(exchange, response);
            exchange.sendResponseHeaders(200, 0);

        } else if (requestMethod.equals("DELETE")) {
            fBTasksManager.clearAll();
            String response = "Все задачи очищены.";

            sendText(exchange, response);
            exchange.sendResponseHeaders(200, 0);

        } else {
            System.out.println("Некорректный метод! Должен быть GET, а в запросе = " + requestMethod);
            exchange.sendResponseHeaders(405, 0);
        }
    }

    private void handleSubTasksInEpic(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String path = String.valueOf(exchange.getRequestURI());
        String query = exchange.getRequestURI().getQuery();

        System.out.println("Обрабатывается запрос: " + path + "\n с методом: " + requestMethod);
        if (requestMethod.equals("GET")) {
            if (Objects.nonNull(query)) {
                String epId = query.substring(3);
                int id = parseTaskId(epId);
                if (id != -1) {
                    String response = gson.toJson(fBTasksManager.getSubTaskInEpic(id));
                    sendText(exchange, response);
                    exchange.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("Получен некорректный ID = " + id);
                    exchange.sendResponseHeaders(405, 0);
                }
            }
        } else {
            System.out.println("Некорректный метод! Должен быть GET, а в запросе = " + requestMethod);
            exchange.sendResponseHeaders(405, 0);
        }
    }

    private void handleTask(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        String path = String.valueOf(exchange.getRequestURI());

        System.out.println("Обрабатывается запрос: " + path + "\n с методом: " + requestMethod);

        switch (requestMethod) {
            case "GET": {
                if (Objects.nonNull(query)) {
                    String taskId = query.substring(3);
                    int id = parseTaskId(taskId);
                    if (id != -1) {
                        String response = gson.toJson(fBTasksManager.getTaskForId(id));
                        sendText(exchange, response);
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        System.out.println("Получен некорректный ID = " + id);
                        exchange.sendResponseHeaders(405, 0);
                    }
                } else {
                    String response = gson.toJson(fBTasksManager.getTasks());
                    sendText(exchange, response);
                    exchange.sendResponseHeaders(200, 0);
                }
            }
            case "POST": {
                try {
                    Task task = gson.fromJson(body, Task.class);

                    if (fBTasksManager.getTaskForId(task.getId()) != null) {
                        fBTasksManager.updateTask(task);
                        exchange.sendResponseHeaders(200, 0);
                        String response = task + " \n ОБНОВЛЕН";
                        sendText(exchange, response);
                    } else {
                        fBTasksManager.createTask(task);
                        exchange.sendResponseHeaders(200, 0);
                        String response = task + " \n СОЗДАН";
                        sendText(exchange, response);
                    }
                } catch (Exception exception) {
                    exchange.sendResponseHeaders(405, 0);
                    exception.printStackTrace();
                }
            }
            case "DELETE": {
                if (Objects.nonNull(query)) {
                    String taskId = query.substring(3);
                    int id = parseTaskId(taskId);
                    if (id != -1) {
                        fBTasksManager.taskRemoveForId(id);
                        exchange.sendResponseHeaders(200, 0);
                        System.out.println("удалили задачу Task ID:" + id);
                    } else {
                        System.out.println("Получен некорректный ID = " + id);
                        exchange.sendResponseHeaders(405, 0);
                    }

                } else {
                    assert fBTasksManager != null;
                    fBTasksManager.deleteTasks();
                    exchange.sendResponseHeaders(200, 0);
                    System.out.println("Все Task`и очищены!");
                }
            }
        }
    }

    private void handleSubTask(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        String path = String.valueOf(exchange.getRequestURI());

        System.out.println("Обрабатывается запрос: " + path + "\n с методом: " + requestMethod);

        switch (requestMethod) {
            case "GET": {
                if (Objects.nonNull(query)) {
                    String taskId = query.substring(3);
                    int id = parseTaskId(taskId);
                    if (id != -1) {
                        String response = gson.toJson(fBTasksManager.getSubTaskForId(id));
                        sendText(exchange, response);
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        System.out.println("Получен некорректный ID = " + id);
                        exchange.sendResponseHeaders(405, 0);
                    }
                } else {
                    String response = gson.toJson(fBTasksManager.getSubTasks());
                    sendText(exchange, response);
                    exchange.sendResponseHeaders(200, 0);
                }
            }
            case "POST": {
                try {
                    SubTask subTask = gson.fromJson(body, SubTask.class);

                    if (fBTasksManager.getSubTaskForId(subTask.getId()) != null) {
                        fBTasksManager.updateSubTask(subTask);
                        exchange.sendResponseHeaders(200, 0);
                        String response = subTask + " \n ОБНОВЛЕН";
                        sendText(exchange, response);
                    } else {
                        fBTasksManager.createSubTask(subTask);
                        exchange.sendResponseHeaders(200, 0);
                        String response = subTask + " \n СОЗДАН";
                        sendText(exchange, response);
                    }
                } catch (Exception exception) {
                    exchange.sendResponseHeaders(405, 0);
                    exception.printStackTrace();
                }
            }
            case "DELETE": {
                if (Objects.nonNull(query)) {
                    String taskId = query.substring(3);
                    int id = parseTaskId(taskId);
                    if (id != -1) {
                        fBTasksManager.subTaskRemoveForId(id);
                        exchange.sendResponseHeaders(200, 0);
                        System.out.println("удалили задачу SubTask ID:" + id);
                    } else {
                        System.out.println("Получен некорректный ID = " + id);
                        exchange.sendResponseHeaders(405, 0);
                    }

                } else {
                    assert fBTasksManager != null;
                    fBTasksManager.deleteSubTasks();
                    exchange.sendResponseHeaders(200, 0);
                    System.out.println("Все SubTask`и очищены!");
                }
            }
        }
    }

    private void handleEpic(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        String path = String.valueOf(exchange.getRequestURI());

        System.out.println("Обрабатывается запрос: " + path + "\n с методом: " + requestMethod);

        switch (requestMethod) {
            case "GET": {
                if (Objects.nonNull(query)) {
                    String taskId = query.substring(3);
                    int id = parseTaskId(taskId);
                    if (id != -1) {
                        String response = gson.toJson(fBTasksManager.getEpicForId(id));
                        sendText(exchange, response);
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        System.out.println("Получен некорректный ID = " + id);
                        exchange.sendResponseHeaders(405, 0);
                    }
                } else {
                    String response = gson.toJson(fBTasksManager.getEpics());
                    sendText(exchange, response);
                    exchange.sendResponseHeaders(200, 0);
                }
            }
            case "POST": {
                try {
                    Epic epic = gson.fromJson(body, Epic.class);

                    if (fBTasksManager.getEpicForId(epic.getId()) != null) {
                        fBTasksManager.updateEpic(epic);
                        exchange.sendResponseHeaders(200, 0);
                        String response = epic + " \n ОБНОВЛЕН";
                        sendText(exchange, response);
                    } else {
                        fBTasksManager.createEpic(epic);
                        exchange.sendResponseHeaders(200, 0);
                        String response = epic + " \n СОЗДАН";
                        sendText(exchange, response);
                    }
                } catch (Exception exception) {
                    exchange.sendResponseHeaders(405, 0);
                    exception.printStackTrace();
                }
            }
            case "DELETE": {
                if (Objects.nonNull(query)) {
                    String taskId = query.substring(3);
                    int id = parseTaskId(taskId);
                    if (id != -1) {
                        fBTasksManager.epicRemoveForId(id);
                        exchange.sendResponseHeaders(200, 0);
                        System.out.println("удалили задачу Epic ID:" + id);
                    } else {
                        System.out.println("Получен некорректный ID = " + id);
                        exchange.sendResponseHeaders(405, 0);
                    }

                } else {
                    assert fBTasksManager != null;
                    fBTasksManager.deleteEpics();
                    exchange.sendResponseHeaders(200, 0);
                    System.out.println("Все Epic`и очищены!");
                }
            }
        }
    }

    private void handleHistory(HttpExchange exchange) throws IOException {
        String path = String.valueOf(exchange.getRequestURI());
        String requestMethod = exchange.getRequestMethod();
        System.out.println("Обрабатывается запрос: " + path + "\n с методом: " + requestMethod);

        if (requestMethod.equals("GET")) {
            List<Task> history = fBTasksManager.delegate.getHistory();
            String response = gson.toJson(history);
            sendText(exchange, response);
            exchange.sendResponseHeaders(200, 0);
        } else {
            System.out.println("Получен некорректный запрос");//ЧЕРЕЗ sendText()
            exchange.sendResponseHeaders(405, 0);

        }
    }

    private int parseTaskId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }


    protected String readText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, resp.length);
        exchange.getResponseBody().write(resp);
    }
}
