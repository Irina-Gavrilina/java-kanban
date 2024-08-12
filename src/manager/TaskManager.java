package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    void clearTasks();

    void clearSubtasks();

    void clearEpics();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    Integer createTask(Task task);

    Integer createSubtask(Subtask subtask);

    Integer createEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}