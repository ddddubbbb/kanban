package Tasks;

import java.util.Objects;

public class SubEpic extends Task {
    protected int epicId;
    protected Boolean status;

    public SubEpic(String title, int id, String description, int epicId, Boolean status) {
        super(title, id, description);
        this.epicId = epicId;
        this.status = status;
    }

    public Boolean isStatus() {
        return status;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Подзадача (SubEpic){" +
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
        if (!(o instanceof SubEpic)) return false;
        if (!super.equals(o)) return false;
        SubEpic subEpic = (SubEpic) o;
        return getEpicId() == subEpic.getEpicId() && status.equals(subEpic.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEpicId(), status);
    }
}