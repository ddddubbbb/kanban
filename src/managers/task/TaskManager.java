package managers.task;

import enums.TaskStatus;
import managers.history.HistoryManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface TaskManager {


    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask);


    HashMap<Integer, SubTask> getSubTasksMap();

    HashMap<Integer, Epic> getEpicsMap();

    HashMap<Integer, Task> getTasksMap();


    List<Task> getTasks();

    List<SubTask> getSubTasks();

    public List<Epic> getEpics();


    Task getTaskForId(Integer id);

    Epic getEpicForId(Integer id);

    SubTask getSubTaskForId(Integer id);

    List<SubTask> getSubTaskInEpic(Integer epicId);


    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);


    void taskRemoveForId(Integer id);

    void epicRemoveForId(Integer id);

    void subTaskRemoveForId(Integer id);

    void deleteTasks();

    void deleteSubTasks();

    void deleteEpics();


    void clearAll();


    TaskStatus getTaskStatusById(Integer id);

    TaskStatus getEpicStatusById(Integer id);

    TaskStatus getSubTaskStatusById(Integer id);


    int getSubIdById(int id);

    Boolean isEmpty();

    List<Task> getHistory();

    Map<Integer, Task> getFileBackedTasksMap();


    void updateEpicStatus(int id);

    Set<Task> getTasksStartTimeTree();

    int getNextId();

    HistoryManager getHistoryManager();
}