package tasks;

import enums.TaskStatus;
import enums.TaskTypes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;


public class Task {
    protected String title;
    protected int id;
    protected String description;
    protected TaskStatus status;
    protected TaskTypes type;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;

    protected Duration duration;

    public Task(String title, int id, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.id = id;
        this.description = description;
        this.status = status;
        this.type = TaskTypes.TASK;

        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {

        return startTime.plusMinutes(duration.toMinutes());
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
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

    public TaskTypes getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Простая задача (Task){" +
                "Название: '" + title + '\'' +
                ". Описание: '" + description + '\'' +
                ". ID: '" + id +
                "'. Статус: '" + status + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                ", duration='" + duration +
                '}' + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return getId() == task.getId() && getTitle().equals(task.getTitle())
                && getDescription().equals(task.getDescription()) && getStatus().equals(task.getStatus())
                && getStartTime().equals(task.getStartTime()) && getDuration().equals(task.getDuration());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getId(), getDescription(), getStatus(), getStartTime(), getDuration());
    }

    public String toStringFileBacked() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                id, type, title, status, description, startTime, duration, endTime, " " + "\n");
    }
}