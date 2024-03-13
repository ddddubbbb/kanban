package managers.task;

import enums.TaskStatus;
import exeptions.ManagerValidateException;
import managers.history.HistoryManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager;
    private int nextId = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Task> fileBackedTasksMap = new HashMap<>();
    protected Set<Task> tasksStartTimeTree = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    public static ZoneId zoneId = ZoneId.systemDefault();

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public int getNextId() {
        return nextId;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }


    @Override
    public Task createTask(Task task) {
        if (task == null) return null;
        task.setId(nextId);
        LocalDateTime endTime = (task.getEndTime());
        task.setEndTime(endTime);
        nextId++;

        tasksStartTimeTree.add(task);
        taskTimeValidator(task);
        tasks.put(task.getId(), task);
        fileBackedTasksMap.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null) return null;
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
        if (subTask == null) return null;
        int epId = subTask.getEpicId();
        subTask.setId(nextId);

        Epic epic = epics.get(epId);

        if (epic != null) {
            epic.getSubTaskIds().add(nextId);

            nextId++;

            tasksStartTimeTree.add(subTask);
            taskTimeValidator(subTask);
            subTasks.put(subTask.getId(), subTask);
            fileBackedTasksMap.put(subTask.getId(), subTask);
            updateEpicTime((Epic) fileBackedTasksMap.get(subTask.getEpicId()));
            return subTask;
        }
        return null;
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
    public Set<Task> getTasksStartTimeTree() {
        return tasksStartTimeTree;
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
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public Epic getEpicForId(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public SubTask getSubTaskForId(Integer id) {
        SubTask subtask = subTasks.get(id);
        if (subtask != null) {
            historyManager.add(subTasks.get(id));
        }
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
        if (task != null && tasks.containsKey(task.getId())) {
            tasksStartTimeTree.remove(fileBackedTasksMap.get(task.getId()));
            fileBackedTasksMap.remove(task.getId());
            tasks.put(task.getId(), task);
            fileBackedTasksMap.put(task.getId(), getTasksMap().get(task.getId()));
            tasksStartTimeTree.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            fileBackedTasksMap.remove(epic.getId());
            epics.put(epic.getId(), epic);
            fileBackedTasksMap.put(epic.getId(), getEpicsMap().get(epic.getId()));
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask != null && subTasks.containsKey(subTask.getId())) {
            int epId = subTask.getEpicId();
            subTasks.put(subTask.getId(), subTask);
            updateEpicStatus(epId);

            tasksStartTimeTree.remove(fileBackedTasksMap.get(subTask.getId()));

            fileBackedTasksMap.remove(subTask.getId());
            fileBackedTasksMap.put(subTask.getId(), getSubTasksMap().get(subTask.getId()));
            fileBackedTasksMap.remove(epId);

            fileBackedTasksMap.put(epId, getEpicsMap().get(epId));
            updateEpicTime((Epic) fileBackedTasksMap.get(epId));
        }
    }

    @Override
    public void taskRemoveForId(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
            fileBackedTasksMap.remove(id);
            tasksStartTimeTree.removeIf(task -> Objects.equals(task.getId(), id));
        }
    }

    @Override
    public void epicRemoveForId(Integer id) {
        Epic epic = epics.get(id);
        epics.remove(id);
        if (epic != null) {
            fileBackedTasksMap.remove(id);
            historyManager.remove(id);

            if (epic.getSubTaskIds() != null) {
                for (Integer subTaskId : epic.getSubTaskIds()) {
                    fileBackedTasksMap.remove(subTaskId);
                    historyManager.remove(subTaskId);
                    subTasks.remove(subTaskId);
                }
            }
        }
    }

    @Override
    public void subTaskRemoveForId(Integer id) {
        SubTask subtask = subTasks.get(id);
        if (subtask != null) {
            SubTask subTask = subTasks.get(id);
            int epId = subTask.getEpicId();
            subTasks.remove(id);
            historyManager.remove(id);
            tasksStartTimeTree.removeIf(task -> Objects.equals(task.getId(), id));

            Epic epic = epics.get(epId);
            epic.getSubTaskIds().remove(id);
            epic.setSubTaskIds(epic.getSubTaskIds());
            updateEpicStatus(epic.getId());
            fileBackedTasksMap.remove(id);
            fileBackedTasksMap.remove(epId);
            fileBackedTasksMap.put(epId, getEpicsMap().get(epId));
        }
    }

    @Override
    public void deleteTasks() {
        for (int id : getTasksMap().keySet()) {
            tasksStartTimeTree.remove(fileBackedTasksMap.get(id));
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
            tasksStartTimeTree.remove(subTask);
            int epId = subTask.getEpicId();
            listEpId.add(epId);

            tasksStartTimeTree.remove(fileBackedTasksMap.get(epId));
            fileBackedTasksMap.remove(epId);
        }

        for (Epic epic : epics.values()) {

            epic.getSubTaskIds().clear();
            updateEpicStatus(epic.getId());
            updateEpicTime(epic);
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
            tasksStartTimeTree.remove(fileBackedTasksMap.get(id));
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
        tasksStartTimeTree.clear();

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

    @Override
    public void updateEpicStatus(int id) { // main:264

        int n = 0; //new
        int p = 0;//in progress
        int d = 0;//done
        ArrayList<Integer> subId = epics.get(id).getSubTaskIds();
        if (epics.containsKey(id)) {
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
    }

    public void updateEpicTime(Epic epic) {
        List<SubTask> subtasks = getSubTaskInEpic(epic.getId());
        if (subtasks.size() == 0) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
        } else {

            LocalDateTime startTime = subtasks.get(0).getStartTime();
            LocalDateTime endTime = subtasks.get(0).getEndTime();

            for (SubTask subtask : subtasks) {
                if (subtask.getStartTime().isBefore(startTime)) startTime = subtask.getStartTime();
                if (subtask.getEndTime().isAfter(endTime)) endTime = subtask.getEndTime();
            }

            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
            Duration duration = Duration.ofSeconds(endTime.atZone(zoneId).toEpochSecond()
                    - startTime.atZone(zoneId).toEpochSecond());
            epic.setDuration(duration);
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

    private void taskTimeValidator(Task task) {

        for (Task intersectionTask : tasksStartTimeTree) {
            if ((tasks.size() == 1) || ((task.getStartTime() == null) && (task.getEndTime() == null))) {
                return;
            }
            if (!Objects.equals(task.getId(), intersectionTask.getId())) {

                if ((task.getStartTime().isEqual(intersectionTask.getStartTime()))
                        || (task.getEndTime().isEqual(intersectionTask.getEndTime()))) {
                    throw new ManagerValidateException(
                            task.getTitle() + " ID:" + task.getId() + " и " + intersectionTask.getTitle()
                                    + " ID:" + intersectionTask.getId() + " пересекаются");
                } else if ((task.getStartTime().isBefore(intersectionTask.getStartTime())
                        && task.getEndTime().isAfter(intersectionTask.getEndTime()))
                        || (task.getStartTime().isBefore(intersectionTask.getStartTime())
                        && task.getEndTime().isBefore(intersectionTask.getEndTime())
                        && intersectionTask.getStartTime().isBefore(task.getEndTime()))
                        || (task.getStartTime().isAfter(intersectionTask.getStartTime())
                        && (task.getEndTime().isAfter(intersectionTask.getEndTime()))
                        && intersectionTask.getEndTime().isAfter(task.getStartTime()))
                        || (task.getStartTime().isAfter(intersectionTask.getStartTime())
                        && task.getEndTime().isBefore(intersectionTask.getEndTime()))) {
                    throw new ManagerValidateException(
                            task.getTitle() + " ID:" + task.getId() + " и " + intersectionTask.getTitle()
                                    + " ID:" + intersectionTask.getId() + " пересекаются");
                }
            }
        }
    }
}