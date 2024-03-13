package tasks;

import enums.TaskStatus;
import enums.TaskTypes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;


public class Epic extends Task {
    private ArrayList<Integer> subTaskIds = new ArrayList<>();
    private final TaskTypes type;


    public Epic(String title, int id, String description, LocalDateTime startTime, Duration duration) {
        super(title, id, description, TaskStatus.NEW, startTime, duration);
        this.type = TaskTypes.EPIC;
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public String toString() {
        return "Задача (Epic){" +
                "Название: '" + title + '\'' +
                ". Описание: '" + description + '\'' +
                ". ID: '" + id +
                "'. ID подзадач: '" + subTaskIds +
                "'. Статус: '" + status + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", duration='" + duration +
                '}' + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return getSubTaskIds().equals(epic.getSubTaskIds());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSubTaskIds());
    }

    @Override
    public String toStringFileBacked() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                id, type, title, status, description, startTime, duration, endTime, subTaskIds.toString()
                        .replace("[", "").replace("]", "").replace(" ", "") + "\n");
    }
}