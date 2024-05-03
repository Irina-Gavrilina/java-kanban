package task;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "task.Subtask{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", epicId=" + epicId +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}