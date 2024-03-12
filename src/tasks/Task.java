package tasks;

import java.util.Objects;

public class Task {
    protected String title;
    protected int id;
    protected String description;
    protected TaskStatus status;

    public Task(String title, int id, String description, TaskStatus status) {
        this.title = title;
        this.id = id;
        this.description = description;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Простая задача (Task){" +
                "Название: '" + title + '\'' +
                ". Описание: '" + description + '\'' +
                ". ID: '" + id +
                "'. Статус: '" + status + '\'' +
                '}' + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return getId() == task.getId() && getTitle().equals(task.getTitle())
                && getDescription().equals(task.getDescription()) && getStatus().equals(task.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getId(), getDescription(), getStatus());
    }
}