package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int nextId = 1;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    public HashMap<Integer, SubTask> getSubTasksMap() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getEpicsMap() {
        return epics;
    }

    public HashMap<Integer, Task> getTasksMap() {
        return tasks;
    }

    public Task getTaskForId(Integer id) {
        return tasks.get(id);
    }

    public Epic getEpicForId(Integer id) {
        return epics.get(id);
    }

    public SubTask getSubTaskForId(Integer id) {
        return subTasks.get(id);
    }

    public ArrayList<SubTask> getSubTaskInEpic(Integer epicId) {
        ArrayList<SubTask> SubTaskInEpic = new ArrayList<>();
        for (Integer i : epics.get(epicId).getSubTaskIds()) {
            SubTaskInEpic.add(subTasks.get(i));
        }
        return SubTaskInEpic;
    }

    public int createTask(Task task) {
        task.setId(nextId);
        nextId++;
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public int createEpic(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    public Integer createSubTask(SubTask subTask) {
        int epId = subTask.getEpicId();
        subTask.setId(nextId);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(epId);
        epic.getSubTaskIds().add(nextId);
        nextId++;
        return subTask.getId();
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubTask(SubTask subTask) {
        int epId = subTask.getEpicId();
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(epId);
    }

    public void taskRemoveForId(Integer id) {
        tasks.remove(id);
    }

    public void epicRemoveForId(Integer id) {
        Epic epic = epics.remove(id);
        for (Integer subTaskId : epic.getSubTaskIds()) {
            subTasks.remove(subTaskId);
        }
    }

    public void subTaskRemoveForId(Integer id) {
        SubTask subTask = subTasks.get(id);
        int epId = subTask.getEpicId();
        subTasks.remove(id);
        Epic epic = epics.get(epId);
        epic.getSubTaskIds().remove(id);
        epic.setSubTaskIds(epic.getSubTaskIds());
        updateEpicStatus(epic.getId());
    }

    public void clearAll() {
        tasks.clear();
        epics.clear();
        subTasks.clear();

        nextId = 1;
    }

    public String getTaskStatusById(Integer id) {
        return tasks.get(id).getStatus();
    }

    public String getEpicStatusById(Integer id) {
        return epics.get(id).getStatus();
    }

    public String getSubTaskStatusById(Integer id) {
        return subTasks.get(id).getStatus();
    }

    public void updateEpicStatus(int id) {
        int n = 0; //new
        int p = 0;//in progress
        int d = 0;//done
        ArrayList<Integer> subId = epics.get(id).getSubTaskIds();
        for (int idS : subId) {
            if (subTasks.get(idS).getStatus().equals("NEW")) {
                n++;
            } else if (subTasks.get(idS).getStatus().equals("IN_PROGRESS")) {
                p++;
            } else if (subTasks.get(idS).getStatus().equals("DONE")) {
                d++;
            }
        }
        if ((n > 0) && (p == 0) && (d == 0)) {
            epics.get(id).setStatus("NEW");
        } else if ((d > 0) && (n == 0) && (p == 0)) {
            epics.get(id).setStatus("DONE");
        } else {
            epics.get(id).setStatus("IN_PROGRESS");
        }
    }

    public int getSubIdById(int id) {
        return subTasks.get(id).getEpicId();
    }

    public Boolean isEmpty() {
        return getTasksMap().isEmpty() && getSubTasksMap().isEmpty() && getEpicsMap().isEmpty();
    }

    @Override
    public String toString() {
        return "manager.Manager{" +
                "tasks=" + tasks +
                ", subTasks=" + subTasks +
                ", epics=" + epics +
                '}';
    }
}