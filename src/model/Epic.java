package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtaskId = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public List<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void addSubtaskId(Subtask subtask) {
        subtaskId.add(subtask.getId());
    }

    public void clearSubtaskId() {
        subtaskId.clear();
    }

    public void removeSubtaskId(Integer id) {
        subtaskId.remove(id);
    }

    @Override
    public String toString() {
        return "model.Epic{" +
                "id='" + getId() + '\'' +
                ", subtaskId=" + subtaskId +
                ", title='" + getTitle() +
                ", description='" + getDescription() + '\'' +
                ", status=" + getTaskStatus() +
                '}';
    }
}