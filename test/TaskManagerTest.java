import enums.TaskStatus;
import managers.task.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected Task createTask() {
        Instant instant = Instant.now();
        return new Task("taskTM", 0, "description", TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime(), Duration.ofMinutes(0));
    }

    protected Epic createEpic() {
        return new Epic("epicTM", 0, "description", null, null);
    }

    protected SubTask createSubTask(Epic epic) {
        Instant instant = Instant.now();
        return new SubTask("SubTaskTM", 0, "description", epic.getId(), TaskStatus.NEW,
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime(), Duration.ofMinutes(33));
    }

    @Test
    public void shouldCreateTask() {
        Task task = createTask();
        manager.createTask(task);
        List<Task> tasks = manager.getTasks();
        assertNotNull(task.getStatus());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(List.of(task), tasks);
    }

    @Test
    public void shouldCreateEpic() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        List<Epic> epics = manager.getEpics();
        assertNotNull(epic.getStatus());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(Collections.EMPTY_LIST, epic.getSubTaskIds());
        assertEquals(List.of(epic), epics);
    }

    @Test
    public void shouldCreateSubTask() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask = createSubTask(epic);
        manager.createSubTask(subtask);
        List<SubTask> subtasks = manager.getSubTasks();
        assertNotNull(subtask.getStatus());
        assertEquals(epic.getId(), subtask.getEpicId());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(List.of(subtask), subtasks);
        assertEquals(List.of(subtask.getId()), epic.getSubTaskIds());
    }

    @Test
    void shouldReturnNullIfCreateTaskNull() {
        Task task = manager.createTask(null);
        assertNull(task);
    }

    @Test
    void shouldReturnNullIfCreateEpicNull() {
        Epic epic = manager.createEpic(null);
        assertNull(epic);
    }

    @Test
    void shouldReturnNullIfCreateSubtaskNull() {
        SubTask subtask = manager.createSubTask(null);
        assertNull(subtask);
    }

    @Test
    public void shouldUpdateTaskStatusToInProgress() {
        Task task = createTask();
        manager.createTask(task);
        task.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getTaskForId(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToInProgress() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        epic.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicForId(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubtaskStatusToInProgress() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask = createSubTask(epic);
        manager.createSubTask(subtask);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(subtask);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getSubTaskForId(subtask.getId()).getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicForId(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateTaskStatusToDone() {
        Task task = createTask();
        manager.createTask(task);
        task.setStatus(TaskStatus.DONE);
        manager.updateTask(task);
        assertEquals(TaskStatus.DONE, manager.getTaskForId(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToDone() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask = createSubTask(epic);
        manager.createSubTask(subtask);
        subtask.setStatus(TaskStatus.DONE);
        manager.updateEpicStatus(epic.getId());
        assertEquals(TaskStatus.DONE, manager.getEpicForId(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubtaskStatusToDone() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask = createSubTask(epic);
        manager.createSubTask(subtask);
        subtask.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subtask);
        assertEquals(TaskStatus.DONE, manager.getSubTaskForId(subtask.getId()).getStatus());
        assertEquals(TaskStatus.DONE, manager.getEpicForId(epic.getId()).getStatus());
    }

    @Test
    public void shouldNotUpdateTaskIfTaskNull() {
        Task task = createTask();
        manager.createTask(task);
        manager.updateTask(null);
        assertEquals(task, manager.getTaskForId(task.getId()));
    }

    @Test
    public void shouldNotUpdateEpicIfEpicNull() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.updateEpic(null);
        assertEquals(epic, manager.getEpicForId(epic.getId()));
    }

    @Test
    public void shouldNotUpdateSubTaskIfSubTaskNull() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask = createSubTask(epic);
        manager.createSubTask(subtask);
        manager.updateSubTask(null);
        assertEquals(subtask, manager.getSubTaskForId(subtask.getId()));
    }

    @Test
    public void shouldDeleteTasks() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteTasks();
        assertEquals(Collections.EMPTY_LIST, manager.getTasks());
    }

    @Test
    public void shouldDeleteEpics() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.deleteEpics();
        assertEquals(Collections.EMPTY_LIST, manager.getEpics());
    }

    @Test
    public void shouldDeleteSubTasks() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask = createSubTask(epic);
        manager.createSubTask(subtask);
        manager.deleteSubTasks();
        assertTrue(epic.getSubTaskIds().isEmpty());
        assertTrue(manager.getSubTasks().isEmpty());
    }

    @Test
    public void shouldDeleteAllSubTasksByEpic() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask = createSubTask(epic);
        manager.createSubTask(subtask);
        manager.deleteEpics();
        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getSubTasks().isEmpty());
    }

    @Test
    public void shouldDeleteTaskById() {
        Task task = createTask();
        manager.createTask(task);
        manager.taskRemoveForId(task.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getTasks());
    }

    @Test
    public void shouldDeleteEpicById() {
        Epic epicTM = createEpic();
        Epic epic = manager.createEpic(epicTM);
        manager.epicRemoveForId(epic.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getEpics());
    }

    @Test
    public void shouldReturnEmptyHistory() {
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void shouldNotDeleteTaskIfIdNotExist() {
        Task task = createTask();
        manager.createTask(task);
        manager.taskRemoveForId(8);
        assertEquals(List.of(task), manager.getTasks());
    }

    @Test
    public void shouldNotDeleteEpicIfIdNotExist() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.epicRemoveForId(8);
        assertEquals(List.of(epic), manager.getEpics());
    }

    @Test
    public void shouldNotDeleteSubTaskIfIdNotExist() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask = createSubTask(epic);
        manager.createSubTask(subtask);
        manager.epicRemoveForId(8);
        assertEquals(List.of(subtask), manager.getSubTasks());
        assertEquals(List.of(subtask), manager.getSubTaskInEpic(epic.getId()));
    }

    @Test
    public void shouldDoNothingIfTaskMapEmpty() {
        manager.deleteTasks();
        manager.taskRemoveForId(8);
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    public void shouldDoNothingIfEpicMapEmpty() {
        manager.deleteEpics();
        manager.epicRemoveForId(8);
        assertTrue(manager.getEpics().isEmpty());
    }

    @Test
    public void shouldDoNothingIfSubTaskMapEmpty() {
        manager.deleteEpics();
        manager.subTaskRemoveForId(8);
        assertEquals(0, manager.getSubTasks().size());
    }

    @Test
    void shouldReturnEmptySubTaskListIfGetSubTaskInEpicEmpty() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        List<SubTask> subtasks = manager.getSubTaskInEpic(epic.getId());
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void shouldReturnEmptyTaskListIfTasksAbsent() {
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    public void shouldReturnEmptyEpicListIfEpicsAbsent() {
        assertTrue(manager.getEpics().isEmpty());
    }

    @Test
    public void shouldReturnEmptySubTaskListIfSubTasksAbsent() {
        assertTrue(manager.getSubTasks().isEmpty());
    }

    @Test
    public void shouldReturnNullIfTaskNotExist() {
        assertNull(manager.getTaskForId(8));
    }

    @Test
    public void shouldReturnNullIfEpicNotExist() {
        assertNull(manager.getEpicForId(9));
    }

    @Test
    public void shouldReturnNullIfSubTaskNotExist() {
        assertNull(manager.getSubTaskForId(10));
    }


    @Test
    public void shouldReturnEmptyHistoryIfTasksNotExist() {
        manager.getTaskForId(8);
        manager.getSubTaskForId(9);
        manager.getEpicForId(10);
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldReturnTasksInHistory() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask = createSubTask(epic);
        manager.createSubTask(subtask);
        manager.getEpicForId(epic.getId());
        manager.getSubTaskForId(subtask.getId());
        List<Task> list = manager.getHistory();
        assertEquals(2, list.size());
        assertTrue(list.contains(subtask));
        assertTrue(list.contains(epic));
    }
}