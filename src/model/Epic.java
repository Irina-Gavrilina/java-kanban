package model;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskId = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void addSubtaskId(Subtask subtask) {
        subtaskId.add(subtask.getId());
    }

    public void clearSubtaskId() {
        subtaskId.clear();
    }

    public void removeSubtaskId(int id) {
        subtaskId.remove(id);
    }

    @Override
    public String toString() {
        return "task.Epic{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", subtaskId=" + subtaskId +
                ", status=" + status +
                '}';
    }
}