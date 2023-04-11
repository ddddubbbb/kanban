package Tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected ArrayList<Integer> subEpicIds = new ArrayList<>();
    protected Boolean status;

    public Epic(String title, int id, String description, Boolean status) {
        super(title, id, description);
        this.status = status;
    }

    public ArrayList<Integer> getSubEpicIds() {
        return subEpicIds;
    }

    public ArrayList<Integer> setSubEpicIds(ArrayList<Integer> subEpicIds) {
        this.subEpicIds = subEpicIds;
        return subEpicIds;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean isStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Задача (Epic){" +
                "Название: '" + title + '\'' +
                ". Описание: '" + description + '\'' +
                ". ID: '" + id +
                "'. ID подзадач: '" + subEpicIds +
                "'. Статус: '" + status + '\'' +
                '}' + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return getSubEpicIds().equals(epic.getSubEpicIds()) && status.equals(epic.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSubEpicIds(), status);
    }
}