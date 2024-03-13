package http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import managers.Managers;
import managers.history.HistoryManager;
import managers.task.FileBackedTasksManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.util.stream.Collectors;

public class HTTPTaskManager extends FileBackedTasksManager {

    final static String KEY_TASKS = "tasks";
    final static String KEY_SUBTASKS = "subtasks";
    final static String KEY_EPICS = "epics";
    final static String KEY_HISTORY = "history";
    final KVTaskClient client;
    private static final Gson gson = Managers.getGson();

    public HTTPTaskManager(HistoryManager historyManager, String path) throws IOException, InterruptedException {
        super(historyManager);
        client = new KVTaskClient(path);

        JsonElement jsonTasks = JsonParser.parseString(client.load(KEY_TASKS));
        if (!jsonTasks.isJsonNull()) {
            JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
            for (JsonElement jsonTask : jsonTasksArray) {
                Task task = gson.fromJson(jsonTask, Task.class);
                this.createTask(task);
            }
        }

        JsonElement jsonEpics = JsonParser.parseString(client.load(KEY_EPICS));
        if (!jsonEpics.isJsonNull()) {
            JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
            for (JsonElement jsonEpic : jsonEpicsArray) {
                Epic task = gson.fromJson(jsonEpic, Epic.class);
                this.createEpic(task);
            }
        }

        JsonElement jsonSubtasks = JsonParser.parseString(client.load(KEY_SUBTASKS));
        if (!jsonSubtasks.isJsonNull()) {
            JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
            for (JsonElement jsonSubtask : jsonSubtasksArray) {
                SubTask task = gson.fromJson(jsonSubtask, SubTask.class);
                this.createSubTask(task);
            }
        }

        JsonElement jsonHistoryList = JsonParser.parseString(client.load(KEY_HISTORY));
        if (!jsonHistoryList.isJsonNull()) {
            JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
            for (JsonElement jsonTaskId : jsonHistoryArray) {
                int taskId = jsonTaskId.getAsInt();
                if (this.getSubTasksMap().containsKey(taskId)) {
                    this.getSubTaskForId(taskId);
                } else if (this.getEpicsMap().containsKey(taskId)) {
                    this.getEpicForId(taskId);
                } else if (this.getTasksMap().containsKey(taskId)) {
                    this.getTaskForId(taskId);
                }
            }
        }
    }

    @Override
    public void save() {
        client.put(KEY_TASKS, gson.toJson(getTasksMap().values()));
        client.put(KEY_SUBTASKS, gson.toJson(getSubTasksMap().values()));
        client.put(KEY_EPICS, gson.toJson(getEpicsMap().values()));
        client.put(KEY_HISTORY, gson.toJson(this.getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList())));
    }
}