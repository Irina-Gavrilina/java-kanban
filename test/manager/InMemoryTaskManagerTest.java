package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void checkTaskCreation() {
        Task task = new Task("task", "description");
        taskManager.createTask(task);
        assertNotNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void checkSubtaskCreation() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        epic.setId(1);
        Subtask subtask = new Subtask("subtask", "description", 1);
        taskManager.createSubtask(subtask);
        assertNotNull(taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void checkEpicCreation() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        assertNotNull(taskManager.getEpicById(epic.getId()));
    }

    @Test
    void shouldReturnTaskById() {
        Task task = new Task("task", "description");
        taskManager.createTask(task);
        Task taskValue = taskManager.getTaskById(task.getId());
        assertEquals(taskValue, task);
    }

    @Test
    void shouldReturnSubtaskById() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        epic.setId(1);
        Subtask subtask = new Subtask("subtask", "description", 1);
        taskManager.createSubtask(subtask);
        Subtask subtaskValue = taskManager.getSubtaskById(subtask.getId());
        assertEquals(subtaskValue, subtask);
    }

    @Test
    void shouldReturnEpicById() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Epic epicValue = taskManager.getEpicById(epic.getId());
        assertEquals(epicValue, epic);
    }

    @Test
    void shouldNotBeConflictBetweenGenerateIdAndSetId() {
        Task task1 = new Task("task1", "description1");
        Task task2 = new Task("task2", "description2");
        taskManager.createTask(task1);
        task1.setId(4);
        taskManager.createTask(task2);
        List<Task> allTasks = taskManager.getAllTasks();
        assertEquals(2, allTasks.size());
    }

    @Test
    void shouldBeEqualTaskFieldsBeforeAndAfterCreation() {
        Task firstTask = new Task("task", "description");
        taskManager.createTask(firstTask);
        final int firstTaskId = firstTask.getId();
        Task secondTask = taskManager.getTaskById(firstTaskId);
        assertEquals(firstTask.getTitle(), secondTask.getTitle());
        assertEquals(firstTask.getDescription(), secondTask.getDescription());
    }

    @Test
    void shouldNotContainRemovedSubtasksIdsInEpic() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "description1", epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "description2", epic.getId());
        taskManager.createSubtask(subtask2);
        List<Integer> subtaskIds = epic.getSubtaskId();
        assertEquals(subtaskIds.size(), 2);
        taskManager.removeSubtaskById(subtask1.getId());
        subtaskIds = epic.getSubtaskId();
        assertEquals(subtaskIds.size(), 1);
        assertEquals(subtaskIds.getFirst(), subtask2.getId());
    }
}