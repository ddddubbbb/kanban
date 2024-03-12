package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected HistoryManager historyManager = Managers.getHistoryDefault();

    private int nextId = 1;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();


    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }


    @Override
    public HashMap<Integer, SubTask> getSubTasksMap() {
        return (HashMap<Integer, SubTask>) subTasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpicsMap() {
        return (HashMap<Integer, Epic>) epics;
    }

    @Override
    public HashMap<Integer, Task> getTasksMap() {
        return (HashMap<Integer, Task>) tasks;
    }


    @Override
    public Task getTaskForId(Integer id) {

        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicForId(Integer id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public SubTask getSubTaskForId(Integer id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public List<SubTask> getSubTaskInEpic(Integer epicId) {
        List<SubTask> SubTaskInEpic = new ArrayList<>();
        for (Integer i : epics.get(epicId).getSubTaskIds()) {
            SubTaskInEpic.add(subTasks.get(i));
        }
        return SubTaskInEpic;
    }

    @Override
    public int createTask(Task task) {
        task.setId(nextId);
        nextId++;
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public Integer createSubTask(SubTask subTask) {
        int epId = subTask.getEpicId();
        subTask.setId(nextId);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(epId);
        epic.getSubTaskIds().add(nextId);
        nextId++;
        return subTask.getId();
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int epId = subTask.getEpicId();
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(epId);
    }

    @Override
    public void taskRemoveForId(Integer id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void epicRemoveForId(Integer id) {
        Epic epic = epics.remove(id);
        historyManager.remove(id);
        for (Integer subTaskId : epic.getSubTaskIds()) {
            historyManager.remove(subTaskId);
            subTasks.remove(subTaskId);
        }
    }

    @Override
    public void subTaskRemoveForId(Integer id) {
        SubTask subTask = subTasks.get(id);
        int epId = subTask.getEpicId();
        subTasks.remove(id);
        historyManager.remove(id);
        Epic epic = epics.get(epId);
        epic.getSubTaskIds().remove(id);
        epic.setSubTaskIds(epic.getSubTaskIds());
        updateEpicStatus(epic.getId());
    }

    @Override
    public void deleteTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        for (Epic epic : epics.values()) {

            epic.getSubTaskIds().clear();
            updateEpicStatus(epic.getId());
        }
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();
    }

    @Override
    public void clearAll() {

        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }

        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }

        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }

        tasks.clear();
        epics.clear();
        subTasks.clear();

        nextId = 1;
    }

    @Override
    public TaskStatus getTaskStatusById(Integer id) {
        return tasks.get(id).getStatus();
    }

    @Override
    public TaskStatus getEpicStatusById(Integer id) {
        return epics.get(id).getStatus();
    }

    @Override
    public TaskStatus getSubTaskStatusById(Integer id) {
        return subTasks.get(id).getStatus();
    }


    public void updateEpicStatus(int id) {// @Override - public void updateEpicStatus/ main:178
        int n = 0; //new
        int p = 0;//in progress
        int d = 0;//done
        ArrayList<Integer> subId = epics.get(id).getSubTaskIds();
        for (int idS : subId) {
            if (subTasks.get(idS).getStatus().equals(TaskStatus.NEW)) {
                n++;
            } else if (subTasks.get(idS).getStatus().equals(TaskStatus.IN_PROGRESS)) {
                p++;
            } else if (subTasks.get(idS).getStatus().equals(TaskStatus.DONE)) {
                d++;
            }
        }
        if ((n > 0) && (p == 0) && (d == 0)) {
            epics.get(id).setStatus(TaskStatus.NEW);
        } else if ((d > 0) && (n == 0) && (p == 0)) {
            epics.get(id).setStatus(TaskStatus.DONE);
        } else {
            epics.get(id).setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public int getSubIdById(int id) {
        return subTasks.get(id).getEpicId();
    }

    @Override
    public Boolean isEmpty() {
        return getTasksMap().isEmpty() && getSubTasksMap().isEmpty() && getEpicsMap().isEmpty();
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public String toString() {
        return "manager.InMemoryTaskManager{" +
                "tasks=" + tasks +
                ", subTasks=" + subTasks +
                ", epics=" + epics +
                '}';
    }
}