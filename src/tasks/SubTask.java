package tasks;

import java.util.Objects;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String title, int id, String description, int epicId, TaskStatus status) {
        super(title, id, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Подзадача (SubTask){" +
                "Название: '" + title + '\'' +
                ". Описание: '" + description + '\'' +
                ". ID Подзадачи: '" + id +
                "'. ID Задачи: '" + epicId +
                "'. Статус: '" + status + '\'' +
                '}' + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return getEpicId() == subTask.getEpicId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEpicId());
    }
}