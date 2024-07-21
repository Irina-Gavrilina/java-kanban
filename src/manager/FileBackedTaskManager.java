package manager;

import exceptions.ManagerSaveException;
import exceptions.NoEpicException;
import exceptions.TimeConflictException;
import model.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;
    private static final String title = "id,type,title,status,description,startTime,duration,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        save();
        return super.getTaskById(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        save();
        return super.getSubtaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        save();
        return super.getEpicById(id);
    }

    @Override
    public Integer createTask(Task task) throws TimeConflictException {
        int taskId = super.createTask(task);
        save();
        return taskId;
    }

    @Override
    public Integer createSubtask(Subtask subtask) throws NoEpicException, TimeConflictException {
        int subtaskId = super.createSubtask(subtask);
        save();
        return subtaskId;
    }

    @Override
    public Integer createEpic(Epic epic) {
        int epicId = super.createEpic(epic);
        save();
        return epicId;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        save();
        return super.getSubtasksByEpicId(epicId);
    }

    @Override
    public List<Task> getHistory() {
        save();
        return super.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    @Override
    public List<Task> getAllTasks() {
        save();
        return super.getAllTasks();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        save();
        return super.getAllSubtasks();
    }

    @Override
    public List<Epic> getAllEpics() {
        save();
        return super.getAllEpics();
    }

    private void save() {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            fileWriter.write(title);

            for (Task task : tasks.values()) {
                fileWriter.write(String.format("\n%s", toString(task)));
            }
            for (Epic epic : epics.values()) {
                fileWriter.write(String.format("\n%s", toString(epic)));
            }
            for (Subtask subtask : subtasks.values()) {
                fileWriter.write(String.format("\n%s", toString(subtask)));
            }
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("%s\n", "Не удалось сохранить данные"), e.getCause());
        }
    }

    private String toString(Task currentTask) {
        TaskType taskType = getTaskType(currentTask);

        switch (taskType) {

            case TASK, EPIC -> {
                return String.format("%d,%s,%s,%s,%s,%s,%d", currentTask.getId(), taskType, currentTask.getTitle(),
                        currentTask.getTaskStatus(), currentTask.getDescription(),
                        currentTask.getStartTime() != null ? currentTask.getStartTime().format(DATE_TIME_FORMATTER) : "",
                        currentTask.getDuration().toMinutes());
            }
            case SUBTASK -> {
                Subtask subtask = (Subtask) currentTask;
                return String.format("%d,%s,%s,%s,%s,%s,%d,%d", subtask.getId(), taskType, subtask.getTitle(),
                        subtask.getTaskStatus(), subtask.getDescription(),
                        subtask.getStartTime().format(DATE_TIME_FORMATTER), subtask.getDuration().toMinutes(),
                        subtask.getEpicId());
            }
        }
        throw new IllegalStateException(String.format("%s %s", "Введено неверное значение:", taskType));
    }

    private static Task fromString(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        TaskType taskType = TaskType.valueOf(split[1]);
        String title = split[2];
        TaskStatus status = TaskStatus.valueOf(split[3]);
        String description = split[4];
        LocalDateTime startTime = null;
        if (!split[5].isBlank()) {
            startTime = LocalDateTime.parse(split[5], DATE_TIME_FORMATTER);
        }
        Duration duration = Duration.ofMinutes(Integer.parseInt(split[6]));

        switch (taskType) {

            case TASK -> {
                Task task = new Task(title, description, startTime, duration);
                task.setId(id);
                task.setTaskStatus(status);
                return task;
            }
            case EPIC -> {
                Epic epic = new Epic(title, description);
                epic.setId(id);
                epic.setTaskStatus(status);
                return epic;
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(split[7]);
                Subtask subtask = new Subtask(title, description, startTime, duration, epicId);
                subtask.setId(id);
                subtask.setTaskStatus(status);
                return subtask;
            }
            default -> throw new IllegalStateException(String.format("%s %s", "Введено неверное значение:", taskType));
        }
    }

    private static TaskType getTaskType(Task currentType) {
        if (currentType instanceof Subtask) {
            return TaskType.SUBTASK;
        } else if (currentType instanceof Epic) {
            return TaskType.EPIC;
        }
        return TaskType.TASK;
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        try {
            String[] fileLines = Files.readString(file.toPath()).split("\n");
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

            for (String line : fileLines) {
                if (line.equals(title) || line.isBlank()) {
                    continue;
                }
                Task currentTask = fromString(line);
                TaskType taskType = getTaskType(currentTask);

                switch (taskType) {
                    case TASK -> fileBackedTaskManager.createTask(currentTask);

                    case EPIC -> fileBackedTaskManager.createEpic((Epic) currentTask);

                    case SUBTASK -> fileBackedTaskManager.createSubtask((Subtask) currentTask);

                    default -> throw new IllegalStateException(String.format("%s %s", "Введено неверное значение:", taskType));
                }
            }
            return fileBackedTaskManager;
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("%s\n", "Не удалось восcтановить данные"), e.getCause());
        }
    }
}