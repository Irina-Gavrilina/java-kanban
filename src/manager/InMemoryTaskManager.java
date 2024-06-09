package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int counterId = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    // Получение списка всех задач/подзадач/эпиков:

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // Удаление всех задач/подзадач/эпиков:

    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        for (Epic epic : epics.values()) {
            epic.clearSubtaskId();
        }
        subtasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
        clearSubtasks();
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
    public Integer createTask(Task task) {
        task.setId(++counterId);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return null;
        }
        subtask.setId(++counterId);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask);
        updateStatus(subtask.getEpicId());
        return subtask.getId();
    }

    @Override
    public Integer createEpic(Epic epic) {
        epic.setId(++counterId);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    // Обновление задачи/подзадачи/эпика:

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateStatus(subtask.getEpicId());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    // Удаление задачи/подзадачи/эпика по идентификатору:

    @Override
    public void removeTaskById(int id) {
            tasks.remove(id);
            historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtaskId(id);
        subtasks.remove(id);
        historyManager.remove(id);
        updateStatus(epic.getId());
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
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        List<Subtask> subtaskList = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskId()) {
            subtaskList.add(subtasks.get(subtaskId));
        }
        return subtaskList;
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
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }
}