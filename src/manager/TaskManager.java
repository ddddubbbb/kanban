package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {

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


    int createTask(Task task);

    int createEpic(Epic epic);

    Integer createSubTask(SubTask subTask);


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

    void updateEpicStatus(int id);
}
