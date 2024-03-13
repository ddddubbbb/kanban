package managers.task;

import managers.Managers;
import managers.history.InMemoryHistoryManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import enums.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryTaskManager implements TaskManager {
    public static InMemoryHistoryManager historyManager = (InMemoryHistoryManager) Managers.getHistoryDefault();
    private int nextId = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Task> fileBackedTasksMap = new HashMap<>();


    @Override
    public Task createTask(Task task) {
        if (task.getId() != 0) {
            nextId = task.getId();
        }
        task.setId(nextId);
        nextId++;
        tasks.put(task.getId(), task);
        fileBackedTasksMap.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic.getId() != 0) {
            nextId = epic.getId();
        }
        epic.setId(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
        if (!epic.getSubTaskIds().isEmpty()) {
            epic.getSubTaskIds().clear();
        }
        fileBackedTasksMap.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        if (subTask.getId() != 0) {
            nextId = subTask.getId();
        }
        int epId = subTask.getEpicId();
        subTask.setId(nextId);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(epId);

        if (epic.getSubTaskIds().contains(nextId)) {
            return subTask;
        } else {
            epic.getSubTaskIds().add(nextId);
        }
        nextId++;
        fileBackedTasksMap.put(subTask.getId(), subTask);
        return subTask;
    }

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
    public void updateTask(Task task) {
        fileBackedTasksMap.remove(task.getId());
        tasks.put(task.getId(), task);
        fileBackedTasksMap.put(task.getId(), getTasksMap().get(task.getId()));
    }

    @Override
    public void updateEpic(Epic epic) {
        fileBackedTasksMap.remove(epic.getId());
        epics.put(epic.getId(), epic);
        fileBackedTasksMap.put(epic.getId(), getEpicsMap().get(epic.getId()));
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int epId = subTask.getEpicId();
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(epId);
        fileBackedTasksMap.remove(subTask.getId());
        fileBackedTasksMap.put(subTask.getId(), getSubTasksMap().get(subTask.getId()));
        fileBackedTasksMap.remove(epId);
        fileBackedTasksMap.put(epId, getEpicsMap().get(epId));
    }

    @Override
    public void taskRemoveForId(Integer id) {
        tasks.remove(id);
        historyManager.remove(id);
        fileBackedTasksMap.remove(id);
    }

    @Override
    public void epicRemoveForId(Integer id) {
        fileBackedTasksMap.remove(id);
        for (Integer subTaskId : getEpicsMap().get(id).getSubTaskIds()) {
            fileBackedTasksMap.remove(subTaskId);
        }
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
        fileBackedTasksMap.remove(id);
        fileBackedTasksMap.remove(epId);
        fileBackedTasksMap.put(epId, getEpicsMap().get(epId));
    }

    @Override
    public void deleteTasks() {
        for (int id : getTasksMap().keySet()) {
            fileBackedTasksMap.remove(id);
        }
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        List<Integer> listEpId = new ArrayList<>();
        for (SubTask subTask : getSubTasksMap().values()) {
            int subId = subTask.getId();
            fileBackedTasksMap.remove(subId);
            int epId = subTask.getEpicId();
            listEpId.add(epId);
            fileBackedTasksMap.remove(epId);
        }

        for (Epic epic : epics.values()) {

            epic.getSubTaskIds().clear();
            updateEpicStatus(epic.getId());
        }
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();

        for (Integer id : listEpId) {
            fileBackedTasksMap.put(id, getEpicsMap().get(id));
        }
    }

    @Override
    public void deleteEpics() {
        for (int id : getSubTasksMap().keySet()) {
            fileBackedTasksMap.remove(id);
        }
        for (int id : getEpicsMap().keySet()) {
            fileBackedTasksMap.remove(id);

        }

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
        fileBackedTasksMap.clear();
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

    //private void updateEpicStatus(int id) {
    @Override
    public void updateEpicStatus(int id) { // main:178
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
        } else if ((d == 0) && (n == 0) && (p == 0)) {
            epics.get(id).setStatus(TaskStatus.NEW);
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
    public Map<Integer, Task> getFileBackedTasksMap() {
        return fileBackedTasksMap;
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