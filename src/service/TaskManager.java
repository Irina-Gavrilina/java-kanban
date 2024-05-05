package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int counterId = 0;
    public final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    // Получение списка всех задач/подзадач/эпиков:

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // Удаление всех задач/подзадач/эпиков:

    public void clearTasks() {
        tasks.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
        for (Epic subtaskInEpics : epics.values()) {
            subtaskInEpics.clearSubtaskId();
        }
    }

    public void clearEpics() {
        epics.clear();
        clearSubtasks();
    }

    // Получение задач/подзадач/эпиков по идентификатору:

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }

    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        return null;
    }

    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    // Создание задачи/подзадачи/эпика:

    public void createTask(Task task) {
        task.setId(++counterId);
        tasks.put(task.getId(), task);
    }

    public void createSubtask (Subtask subtask) {
        subtask.setId(++counterId);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask);
        }
        updateStatus(subtask.getEpicId());
    }

    public void createEpic(Epic epic) {
        epic.setId(++counterId);
        epics.put(epic.getId(), epic);
    }

    // Обновление задачи/подзадачи/эпика:

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);

    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateStatus(subtask.getEpicId());
        }
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    // Удаление задачи/подзадачи/эпика по идентификатору:

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtaskId(id);
        subtasks.remove(id);
        updateStatus(epic.getId());
    }

    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.getSubtaskId()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    // Получение списка всех подзадач определённого эпика:

    public ArrayList<Subtask> getAllSubtasksFromEpic(Epic epic) {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskId()) {
            subtaskList.add(subtasks.get(subtaskId));
        }
        return subtaskList;
    }

    // Обновление статуса эпика:

    private void updateStatus(int id) {
        Epic epic = epics.get(id);
        ArrayList<Integer> subtaskIds = epic.getSubtaskId();
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
}