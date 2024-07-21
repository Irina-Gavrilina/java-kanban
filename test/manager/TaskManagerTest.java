package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void Manager() {
        taskManager = createTaskManager();
    }

    @Test
    void checkTaskCreation() {
        Task task = new Task("task", "description", LocalDateTime.now(), Duration.ofMinutes(1));
        taskManager.createTask(task);
        assertNotNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void checkSubtaskCreation() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        epic.setId(1);
        Subtask subtask = new Subtask("subtask", "description",
                LocalDateTime.now(), Duration.ofMinutes(1), 1);
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
        Task task = new Task("task", "description", LocalDateTime.now(), Duration.ofMinutes(1));
        taskManager.createTask(task);
        Task taskValue = taskManager.getTaskById(task.getId());
        assertEquals(taskValue, task);
    }

    @Test
    void shouldReturnSubtaskById() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        epic.setId(1);
        Subtask subtask = new Subtask("subtask", "description", LocalDateTime.now(), Duration.ofMinutes(1), 1);
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
        Task task1 = new Task("task1", "description1", LocalDateTime.now(), Duration.ofMinutes(1));
        Task task2 = new Task("task2", "description2",
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(2));
        taskManager.createTask(task1);
        task1.setId(4);
        taskManager.createTask(task2);
        List<Task> allTasks = taskManager.getAllTasks();
        assertEquals(2, allTasks.size());
    }

    @Test
    void shouldBeEqualTaskFieldsBeforeAndAfterCreation() {
        Task firstTask = new Task("task", "description", LocalDateTime.now(), Duration.ofMinutes(5));
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
        Subtask subtask1 = new Subtask("subtask1", "description1",
                LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "description2",
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(10), epic.getId());
        taskManager.createSubtask(subtask2);
        List<Integer> subtaskIds = epic.getSubtaskId();
        assertEquals(subtaskIds.size(), 2);
        taskManager.removeSubtaskById(subtask1.getId());
        subtaskIds = epic.getSubtaskId();
        assertEquals(subtaskIds.size(), 1);
        assertEquals(subtaskIds.getFirst(), subtask2.getId());
    }

    @Test
    public void shouldReturnAllTasks() {
        Task task1 = new Task("task1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(15));
        taskManager.createTask(task1);
        Task task2 = new Task("task2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 50), Duration.ofMinutes(20));
        taskManager.createTask(task2);
        List<Task> firstListOfAllTasks = new ArrayList<>();
        firstListOfAllTasks.add(task1);
        firstListOfAllTasks.add(task2);
        List<Task> secondListOfAllTasks = taskManager.getAllTasks();
        assertEquals(firstListOfAllTasks, secondListOfAllTasks);
    }

    @Test
    public void shouldReturnAllSubtasks() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "description1",
                LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "description2",
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(10), epic.getId());
        taskManager.createSubtask(subtask2);
        List<Subtask> firstListOfAllSubtasks = new ArrayList<>();
        firstListOfAllSubtasks.add(subtask1);
        firstListOfAllSubtasks.add(subtask2);
        List<Subtask> secondListOfAllSubtasks = taskManager.getAllSubtasks();
        assertEquals(firstListOfAllSubtasks, secondListOfAllSubtasks);
    }

    @Test
    public void shouldReturnAllEpics() {
        Epic epic1 = new Epic("epic1", "description1");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("epic2", "description2");
        taskManager.createEpic(epic2);
        List<Epic> firstListOfAllEpics = new ArrayList<>();
        firstListOfAllEpics.add(epic1);
        firstListOfAllEpics.add(epic2);
        List<Epic> secondListOfAllEpics = taskManager.getAllEpics();
        assertEquals(firstListOfAllEpics, secondListOfAllEpics);
    }

    @Test
    public void shouldClearAllTasks() {
        Task task1 = new Task("task1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(15));
        taskManager.createTask(task1);
        Task task2 = new Task("task2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 50), Duration.ofMinutes(20));
        taskManager.createTask(task2);
        taskManager.clearTasks();
        assertEquals(taskManager.getAllTasks().size(), 0);
    }

    @Test
    public void shouldClearAllSubtasks() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 10, 30), Duration.ofMinutes(5),
                epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 11, 30), Duration.ofMinutes(10),
                epic.getId());
        taskManager.createSubtask(subtask2);
        assertEquals(subtask1.getStartTime(), epic.getStartTime());
        assertEquals(subtask2.getStartTime().plus(subtask2.getDuration()), epic.getEndTime());
        taskManager.clearSubtasks();
        assertEquals(taskManager.getAllSubtasks().size(), 0);
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertEquals(epic.getDuration(), Duration.ZERO);
    }

    @Test
    public void shouldClearAllEpics() {
        Epic epic1 = new Epic("epic1", "description1");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("epic2", "description2");
        taskManager.createEpic(epic2);
        taskManager.clearEpics();
        assertEquals(taskManager.getAllEpics().size(),0);
    }

    @Test
    public void shouldRemoveTaskById() {
        Task task1 = new Task("task1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(15));
        taskManager.createTask(task1);
        Task task2 = new Task("task2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 50), Duration.ofMinutes(20));
        taskManager.createTask(task2);
        assertEquals(taskManager.getAllTasks().size(), 2);
        taskManager.removeTaskById(task1.getId());
        assertEquals(taskManager.getAllTasks().size(), 1);
        assertNull(taskManager.getTaskById(task1.getId()));
    }

    @Test
    public void shouldRemoveSubtaskById() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 10, 30), Duration.ofMinutes(5),
                epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 11, 30), Duration.ofMinutes(10),
                epic.getId());
        taskManager.createSubtask(subtask2);
        assertTrue(epic.getSubtaskId().contains(subtask1.getId()) && epic.getSubtaskId().contains(subtask2.getId()));
        assertEquals(subtask1.getStartTime(), epic.getStartTime());
        assertEquals(subtask2.getStartTime().plus(subtask2.getDuration()), epic.getEndTime());
        taskManager.removeSubtaskById(subtask2.getId());
        assertTrue(epic.getSubtaskId().contains(subtask1.getId()));
        assertFalse(epic.getSubtaskId().contains(subtask2.getId()));
        assertEquals(subtask1.getStartTime().plus(subtask1.getDuration()), epic.getEndTime());
        assertEquals(subtask1.getDuration(), epic.getDuration());
    }

    @Test
    public void shouldRemoveEpicById() {
        Epic epic1 = new Epic("epic1", "description1");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("epic2", "description2");
        taskManager.createEpic(epic2);
        assertEquals(taskManager.getAllEpics().size(),2);
        taskManager.removeEpicById(epic2.getId());
        assertEquals(taskManager.getAllEpics().size(), 1);
        assertNull(taskManager.getEpicById(epic2.getId()));
    }

    @Test
    public void shouldUpdateTask() {
        Task task = new Task("task1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(15));
        taskManager.createTask(task);
        task = new Task("task2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 14, 50), Duration.ofMinutes(20));
        taskManager.updateTask(task);
        assertNotEquals("task1", task.getTitle());
        assertNotEquals("description1", task.getDescription());
        assertEquals(task.getTitle(), "task2");
        assertEquals(task.getDescription(), "description2");
    }

    @Test
    public void shouldUpdateSubtask() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtask", "description",
                LocalDateTime.of(2024, Month.JULY, 17, 10, 30), Duration.ofMinutes(5),
                epic.getId());
        taskManager.createSubtask(subtask);
        assertNotNull(subtask);
        assertEquals(TaskStatus.NEW, subtask.getTaskStatus());
        assertEquals(subtask.getStartTime().format(formatter), "17.07.24 10:30");
        assertEquals(subtask.getEndTime().format(formatter), "17.07.24 10:35");
        assertEquals(subtask.getDuration().toMinutes(), 5);
        assertEquals(epic.getStartTime().format(formatter), "17.07.24 10:30");
        assertEquals(epic.getEndTime().format(formatter), "17.07.24 10:35");
        assertEquals(epic.getDuration().toMinutes(), 5);
        subtask.setStartTime(LocalDateTime.of(2024, Month.JULY,  20, 11, 45));
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);
        assertNotNull(subtask);
        assertEquals(TaskStatus.DONE, subtask.getTaskStatus());
        assertEquals(subtask.getStartTime().format(formatter), "20.07.24 11:45");
        assertEquals(subtask.getEndTime().format(formatter), "20.07.24 12:00");
        assertEquals(subtask.getDuration().toMinutes(), 15);
        assertEquals(epic.getStartTime().format(formatter), "20.07.24 11:45");
        assertEquals(epic.getEndTime().format(formatter), "20.07.24 12:00");
        assertEquals(epic.getDuration().toMinutes(), 15);
    }

    @Test
    public void shouldUpdateEpic() {
        Epic epic = new Epic("epic1", "description1");
        taskManager.createEpic(epic);
        epic = new Epic("epic2", "description2");
        taskManager.updateEpic(epic);
        assertNotEquals("epic1", epic.getTitle());
        assertNotEquals("description1", epic.getDescription());
        assertEquals(epic.getTitle(), "epic2");
        assertEquals(epic.getDescription(), "description2");
    }

    @Test
    public void shouldReturnSubtasksByEpicId() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 10, 30), Duration.ofMinutes(5),
                epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 11, 30), Duration.ofMinutes(10),
                epic.getId());
        taskManager.createSubtask(subtask2);
        List<Subtask> subtaskList = taskManager.getSubtasksByEpicId(epic.getId());
        assertEquals(subtaskList.size(), 2);
        assertEquals(subtaskList.getFirst().getId(), subtask1.getId());
        assertEquals(subtaskList.get(1).getId(), subtask2.getId());
        taskManager.removeSubtaskById(subtask1.getId());
        subtaskList = taskManager.getSubtasksByEpicId(epic.getId());
        assertEquals(subtaskList.size(), 1);
        assertEquals(subtaskList.getFirst().getId(), subtask2.getId());
    }

    @Test
    public void checkGetHistory() {
        List<Task> listOfTasks = new ArrayList<>();

        Task task = new Task("task", "description",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(15));
        taskManager.createTask(task);
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtask", "description",
                LocalDateTime.of(2024, Month.JULY, 17, 10, 30), Duration.ofMinutes(5),
                epic.getId());
        taskManager.createSubtask(subtask);
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());
        listOfTasks.add(task);
        listOfTasks.add(epic);
        listOfTasks.add(subtask);
        assertEquals(listOfTasks, taskManager.getHistory());
    }

    @Test
    public void checkGetPrioritizedTasks() {
        List<Task> firstListOfTasks = new ArrayList<>();
        List<Task> secondListOfTasks;

        Task task1 = new Task("task1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(15));
        taskManager.createTask(task1);
        Task task2 = new Task("task2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 50), Duration.ofMinutes(20));
        taskManager.createTask(task2);
        Epic epic1 = new Epic("epic1", "description1");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("epic2", "description2");
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask("subtask1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 18, 15), Duration.ofMinutes(5),
                epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 20, 30), Duration.ofMinutes(10),
                epic1.getId());
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("subtask3", "description3",
                LocalDateTime.of(2024, Month.JULY, 17, 21, 30), Duration.ofMinutes(30),
                epic2.getId());
        taskManager.createSubtask(subtask3);
        firstListOfTasks.add(task1);
        firstListOfTasks.add(task2);
        firstListOfTasks.add(subtask1);
        firstListOfTasks.add(subtask2);
        firstListOfTasks.add(subtask3);
        secondListOfTasks = taskManager.getPrioritizedTasks();
        assertEquals(firstListOfTasks.size(), 5);
        assertEquals(secondListOfTasks.size(), 5);
        assertEquals(firstListOfTasks, secondListOfTasks);
        taskManager.removeTaskById(task2.getId());
        secondListOfTasks = taskManager.getPrioritizedTasks();
        firstListOfTasks.remove(1);
        assertEquals(firstListOfTasks, secondListOfTasks);
        taskManager.clearTasks();
        secondListOfTasks = taskManager.getPrioritizedTasks();
        firstListOfTasks.removeFirst();
        assertEquals(firstListOfTasks, secondListOfTasks);
        taskManager.removeEpicById(epic1.getId());
        secondListOfTasks = taskManager.getPrioritizedTasks();
        firstListOfTasks.removeFirst();
        firstListOfTasks.removeFirst();
        assertEquals(firstListOfTasks, secondListOfTasks);
        taskManager.clearEpics();
        secondListOfTasks = taskManager.getPrioritizedTasks();
        firstListOfTasks.removeFirst();
        assertEquals(firstListOfTasks, secondListOfTasks);
        assertEquals(firstListOfTasks.size(), 0);
        assertEquals(secondListOfTasks.size(), 0);
    }
}