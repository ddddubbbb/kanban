package Tasks;

import java.util.Objects;

public class Task {
    protected String title;
    protected int id;
    protected String description;

    public Task(String title, int id, String description) {
        this.title = title;
        this.id = id;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return getId() == task.getId() && getTitle().equals(task.getTitle()) && getDescription().equals(task.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getId(), getDescription());
    }
}

