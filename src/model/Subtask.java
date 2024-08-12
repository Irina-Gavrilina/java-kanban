package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String title, String description, LocalDateTime startTime, Duration duration, int epicId) {
        super(title, description, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        String startTimeToString = "";
        if (startTime != null) {
            startTimeToString = startTime.format(formatter);
        }
        return "model.Subtask{" +
                "id='" + getId() + '\'' +
                ", epicId=" + epicId +
                ", title='" + getTitle() +
                ", description='" + getDescription() + '\'' +
                ", status=" + getTaskStatus() +
                ", startTime=" + startTimeToString +
                ", duration=" + duration.toMinutes() + " мин" +
                '}';
    }
}