package Tasks;

import java.util.Objects;

public class SimpleTask extends Task {
    protected Boolean status;

    public SimpleTask(String title, int id, String description, Boolean status) {
        super(title, id, description);
        this.status = status;
    }

    public Boolean isStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Простая задача (Simple Task){" +
                "Название: '" + title + '\'' +
                ". Описание: '" + description + '\'' +
                ". ID: '" + id +
                "'. Статус: '" + status + '\'' +
                '}' + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleTask)) return false;
        SimpleTask that = (SimpleTask) o;
        return status.equals(that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }
}