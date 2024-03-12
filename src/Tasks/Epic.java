package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String title, int id, String description) {
        super(title, id, description, "NEW");
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    @Override
    public String toString() {
        return "Задача (Epic){" +
                "Название: '" + title + '\'' +
                ". Описание: '" + description + '\'' +
                ". ID: '" + id +
                "'. ID подзадач: '" + subTaskIds +
                "'. Статус: '" + status + '\'' +
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
}