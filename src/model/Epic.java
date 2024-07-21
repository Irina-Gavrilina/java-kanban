package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtaskId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description, null, Duration.ofMinutes(0));
    }

    public Epic(Epic epic) {
        super(epic.getTitle(), epic.getDescription(), null, Duration.ofMinutes(0));
        this.id = epic.getId();
        this.endTime = null;
    }

    public List<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void addSubtaskId(Subtask subtask) {
        subtaskId.add(subtask.getId());
    }

    public void clearSubtaskId() {
        subtaskId.clear();
        setStartTime(null);
        setDuration(Duration.ofMinutes(0));
        setEndTime(null);
    }

    public void removeSubtaskId(Integer id) {
        subtaskId.remove(id);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        String startTimeToString = "";
        String endTimeToString = "";
        if (startTime != null) {
            startTimeToString = startTime.format(formatter);
        }
        if (endTime != null) {
            endTimeToString = endTime.format(formatter);
        }
        return "model.Epic{" +
                "id='" + getId() + '\'' +
                ", subtaskId=" + subtaskId +
                ", title='" + getTitle() +
                ", description='" + getDescription() + '\'' +
                ", status=" + getTaskStatus() +
                ", startTime=" + startTimeToString +
                ", duration=" + duration.toMinutes() + " мин" +
                ", endTime=" + endTimeToString +
                '}';
    }
}