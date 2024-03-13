package managers.task;

import enums.TaskStatus;
import enums.TaskTypes;
import exeptions.ManagerSaveException;
import managers.Managers;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class FileBackedTasksManager implements TaskManager {
    private File file;
    public HistoryManager historyManager = new InMemoryHistoryManager();

    public TaskManager delegate = Managers.getDefault(historyManager);

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    @Override
    public Task createTask(Task task) {
        delegate.createTask(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        delegate.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        delegate.createSubTask(subTask);
        save();
        return subTask;
    }

    @Override
    public HashMap<Integer, SubTask> getSubTasksMap() {
        return delegate.getSubTasksMap();
    }

    @Override
    public HashMap<Integer, Epic> getEpicsMap() {
        return delegate.getEpicsMap();
    }

    @Override
    public HashMap<Integer, Task> getTasksMap() {
        return delegate.getTasksMap();
    }

    @Override
    public Set<Task> getTasksStartTimeTree() {
        return delegate.getTasksStartTimeTree();
    }

    @Override
    public int getNextId() {
        return delegate.getNextId();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return delegate.getHistoryManager();
    }

    @Override
    public Task getTaskForId(Integer id) {
        Task task = delegate.getTaskForId(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicForId(Integer id) {
        Epic epic = delegate.getEpicForId(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTaskForId(Integer id) {
        SubTask subTask = delegate.getSubTaskForId(id);
        save();
        return subTask;
    }

    @Override
    public List<SubTask> getSubTaskInEpic(Integer epicId) {
        return delegate.getSubTaskInEpic(epicId);
    }

    @Override
    public void updateTask(Task task) {
        delegate.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        delegate.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        delegate.updateSubTask(subTask);
        delegate.updateEpicStatus(subTask.getEpicId());
        save();
    }

    @Override
    public void taskRemoveForId(Integer id) {
        delegate.taskRemoveForId(id);
        save();
    }

    @Override
    public void epicRemoveForId(Integer id) {
        delegate.epicRemoveForId(id);
        save();
    }

    @Override
    public void subTaskRemoveForId(Integer id) {

        delegate.subTaskRemoveForId(id);
        save();
    }

    @Override
    public void deleteTasks() {
        delegate.deleteTasks();
        save();
    }

    @Override
    public void deleteSubTasks() {
        delegate.deleteSubTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        delegate.deleteEpics();
        save();
    }

    @Override
    public void clearAll() {
        delegate.clearAll();
        save();
    }

    @Override
    public Boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Map<Integer, Task> getFileBackedTasksMap() {
        return delegate.getFileBackedTasksMap();
    }

    @Override
    public void updateEpicStatus(int id) {
        delegate.updateEpicStatus(id);
    }

    public void save() {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id, type, title, status, description, startTime, duration, endTime, epicID-subtasksID \n");
            for (Integer id : getFileBackedTasksMap().keySet()) {
                writer.write(taskToString(getFileBackedTasksMap().get(id)));
            }
            writer.write("\n");
            writer.write(historyToString(getHistoryManager()));
        } catch (IOException exception) {
            throw new ManagerSaveException("Не удалось сохранить в файл");
        }
    }

    public void loadFromFile() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader((file)))) {

            String value;
            while (bufferedReader.ready()) {
                value = bufferedReader.readLine();
                if (value.isBlank()) {
                    break;
                }
                if (value.contains("id")) {
                    continue;
                }

                Task task = fromString(value);
                if (value.contains("EPIC")) {
                    delegate.createEpic((Epic) task);
                } else if (value.contains("SUBTASK")) {
                    delegate.createSubTask((SubTask) task);
                } else {
                    delegate.createTask(task);
                }
            }
            String valueHistory = bufferedReader.readLine();
            for (int id : historyFromString(valueHistory)) {
                if (getFileBackedTasksMap().containsKey(id)) {
                    getHistoryManager().add(getFileBackedTasksMap().get(id));
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Не удалось прочитать файл");
        }
    }

    public static String taskToString(Task task) {
        return task.toStringFileBacked();
    }

    public static String historyToString(HistoryManager historyManager) {
        List<String> idHistory = new ArrayList<>();
        List<Task> historyList = historyManager.getHistory();

        if (historyList != null) {
            for (Task history : historyList) {
                idHistory.add(history.getId().toString());
            }
        }
        return String.join(",", idHistory);
    }

    public static Task fromString(String value) {

        String[] lines = value.split(",");
        int id = Integer.parseInt(lines[0]);
        String title = lines[2];
        TaskStatus status = TaskStatus.valueOf(lines[3]);
        String description = lines[4];
        TaskTypes type = TaskTypes.valueOf(lines[1]);
        LocalDateTime startTime;
        Duration duration;
        if (lines[5].equals("null")) {
            startTime = null;
            duration = null;
        } else {
            startTime = LocalDateTime.parse(lines[5]);
            duration = Duration.parse(lines[6]);
        }

        if (type.equals(TaskTypes.TASK)) {
            return new Task(title, id, description, status, startTime, duration);

        } else if (type.equals(TaskTypes.EPIC)) {
            return new Epic(title, id, description, startTime, duration);

        } else {
            return new SubTask(title, id, description, Integer.parseInt(lines[8]), status, startTime, duration);
        }
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> tasksIds = new ArrayList<>();
        if (value != null && !value.isBlank()) {
            String[] ids = value.split(",");
            for (String id : ids) {
                tasksIds.add(Integer.valueOf(id));
            }
        } else {
            return tasksIds;
        }
        return tasksIds;
    }

    @Override
    public List<Task> getTasks() {
        return delegate.getTasks();
    }

    @Override
    public List<SubTask> getSubTasks() {
        return delegate.getSubTasks();
    }

    @Override
    public List<Epic> getEpics() {
        return delegate.getEpics();
    }

    @Override
    public List<Task> getHistory() {
        return delegate.getHistory();
    }

    @Override
    public TaskStatus getTaskStatusById(Integer id) {
        return delegate.getTaskStatusById(id);
    }

    @Override
    public TaskStatus getEpicStatusById(Integer id) {
        return delegate.getEpicStatusById(id);
    }

    @Override
    public TaskStatus getSubTaskStatusById(Integer id) {
        return delegate.getSubTaskStatusById(id);
    }

    @Override
    public int getSubIdById(int id) {
        return delegate.getSubIdById(id);
    }
}