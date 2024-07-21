package manager;

import exceptions.NoEpicException;
import exceptions.TimeConflictException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int counterId = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    // Получение списка всех задач/подзадач/эпиков:

    @Override
    public List<Task> getAllTasks() {
        return tasks.values().stream().toList();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return subtasks.values().stream().toList();
    }

    @Override
    public List<Epic> getAllEpics() {
        return epics.values().stream().toList();
    }

    @Override
    public void clearTasks() {
        tasks.values().stream()
                .map(Task::getId)
                .forEach(historyManager::remove);
        tasks.clear();
        updatePrioritizedTasks();
    }

    @Override
    public void clearSubtasks() {
        subtasks.values().stream()
                .map(Subtask::getId)
                .forEach(historyManager::remove);
        subtasks.clear();
        epics.values().forEach(Epic::clearSubtaskId);
        updatePrioritizedTasks();
    }

    @Override
    public void clearEpics() {
        epics.values().stream()
                .map(Epic::getId)
                .forEach(historyManager::remove);
        epics.clear();
        clearSubtasks();
        updatePrioritizedTasks();
    }

    // Получение задач/подзадач/эпиков по идентификатору:

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.addToHistory(tasks.get(id));
            return tasks.get(id);
        }
        return null;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.addToHistory(subtasks.get(id));
            return subtasks.get(id);
        }
        return null;
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.addToHistory(epics.get(id));
            return epics.get(id);
        }
        return null;
    }

    // Создание задачи/подзадачи/эпика:

    @Override
    public Integer createTask(Task task) throws TimeConflictException {
        if (isTimeConflict(task)) {
            throw new TimeConflictException("На это время уже запланирована другая задача");
        }
        task.setId(++counterId);
        tasks.put(task.getId(), task);
        updatePrioritizedTasks();
        return task.getId();
    }

    @Override
    public Integer createSubtask(Subtask subtask) throws NoEpicException, TimeConflictException {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new NoEpicException(String.format("%s %d %s", "К сожалению, Epic с таким", subtask.getEpicId(), "не существует"));
        }
        if (isTimeConflict(subtask)) {
            throw new TimeConflictException("На это время уже запланирована другая задача");
        }
        subtask.setId(++counterId);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask);
        if (epic.getStartTime() == null || epic.getStartTime().isAfter(subtask.getStartTime())) {
            epic.setStartTime(subtask.getStartTime());
        }
        if (epic.getEndTime() == null || epic.getEndTime().isBefore(subtask.getEndTime())) {
            epic.setEndTime(subtask.getEndTime());
        }
        epic.setDuration(epic.getDuration().plus(subtask.getDuration()));
        updateStatus(subtask.getEpicId());
        updatePrioritizedTasks();
        return subtask.getId();
    }

    @Override
    public Integer createEpic(Epic epic) {
        epic.setId(++counterId);
        epics.put(epic.getId(), epic);
        updatePrioritizedTasks();
        return epic.getId();
    }

    // Обновление задачи/подзадачи/эпика:

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        updatePrioritizedTasks();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateStatus(subtask.getEpicId());
            updateStartTimeAndEndTimeForEpic(epic.getId());
        }
        updatePrioritizedTasks();
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updatePrioritizedTasks();
    }

    // Удаление задачи/подзадачи/эпика по идентификатору:

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
        updatePrioritizedTasks();
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtaskId(id);
        subtasks.remove(id);
        historyManager.remove(id);
        updateStatus(epic.getId());
        updateStartTimeAndEndTimeForEpic(epic.getId());
        updatePrioritizedTasks();
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.getSubtaskId()) {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
        updatePrioritizedTasks();
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        return epics.get(epicId).getSubtaskId().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    // Обновление статуса эпика:

    private void updateStatus(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> subtaskIds = epic.getSubtaskId();
        TaskStatus status;
        boolean isAllNew = true;
        boolean isAllDone = true;

        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getTaskStatus() != TaskStatus.NEW) {
                isAllNew = false;
            }
            if (subtask.getTaskStatus() != TaskStatus.DONE) {
                isAllDone = false;
            }
        }

        if (isAllNew) {
            status = TaskStatus.NEW;
        } else if (isAllDone) {
            status = TaskStatus.DONE;
        } else {
            status = TaskStatus.IN_PROGRESS;
        }
        epic.setTaskStatus(status);
        epics.put(epic.getId(), epic);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    private boolean isTimeConflict(Task newTask) {
        return getPrioritizedTasks().stream()
                .anyMatch(currentTask -> !currentTask.getEndTime().isBefore(newTask.getStartTime()) &&
                        !newTask.getEndTime().isBefore(currentTask.getStartTime()));
    }

    private void updatePrioritizedTasks() {
        prioritizedTasks.clear();
        prioritizedTasks.addAll(tasks.values().stream()
                .filter(task -> task.getStartTime().toLocalDate() != null)
                .toList());
        prioritizedTasks.addAll(subtasks.values().stream()
                .filter(subtask -> subtask.getStartTime().toLocalDate() != null)
                .toList());
    }

    private void updateStartTimeAndEndTimeForEpic(int idEpic) {
        Epic epic = epics.get(idEpic);
        if (epic != null) {
            List<Subtask> subtaskIdList = epic.getSubtaskId().stream()
                    .map(subtasks::get)
                    .toList();
            LocalDateTime startTime = subtaskIdList.stream()
                    .map(Subtask::getStartTime)
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);
            LocalDateTime endTime = subtaskIdList.stream()
                    .map(subtask -> subtask.getStartTime().plus(subtask.getDuration()))
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
            Duration duration = subtaskIdList.stream()
                    .map(Subtask::getDuration)
                    .filter(Objects::nonNull)
                    .reduce(Duration.ZERO, Duration::plus);
            epic.setDuration(duration);
        }
    }
}