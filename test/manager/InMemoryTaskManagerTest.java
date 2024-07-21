package manager;

import exceptions.NoEpicException;
import exceptions.TimeConflictException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    protected TaskManager taskManager;

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @BeforeEach
    public void beforeEachTaskManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void epicStatusShouldReturnNewWhenAllSubtasksHaveStatusNew() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "description1",
                LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "description2",
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(10), epic.getId());
        taskManager.createSubtask(subtask2);
        assertEquals(epic.getTaskStatus(), TaskStatus.NEW);
    }

    @Test
    public void epicStatusShouldReturnDoneWhenAllSubtasksHaveStatusDone() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "description1",
                LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());
        taskManager.createSubtask(subtask1);
        subtask1.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "description2",
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(10), epic.getId());
        taskManager.createSubtask(subtask2);
        subtask2.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);
        assertEquals(epic.getTaskStatus(), TaskStatus.DONE);
    }

    @Test
    public void epicStatusShouldReturnInProgressWhenSubtasksHaveStatusNewAndDone() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "description1",
                LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());
        taskManager.createSubtask(subtask1);
        subtask1.setTaskStatus(TaskStatus.NEW);
        taskManager.updateSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "description2",
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(10), epic.getId());
        taskManager.createSubtask(subtask2);
        subtask2.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);
        assertEquals(epic.getTaskStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    public void epicStatusShouldReturnInProgressWhenSubtasksHaveStatusInProgress() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "description1",
                LocalDateTime.now(), Duration.ofMinutes(5), epic.getId());
        taskManager.createSubtask(subtask1);
        subtask1.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "description2",
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(10), epic.getId());
        taskManager.createSubtask(subtask2);
        subtask2.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);
        assertEquals(epic.getTaskStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    public void shouldThrowExceptionWhenTryingToCreateSubtaskInTheAbsenceOfEpic() {
        int epicId =  1;
        assertThrows(NoEpicException.class, () -> {
            Subtask subtask = new Subtask("subtask", "description",
                    LocalDateTime.now(), Duration.ofMinutes(5), epicId);
            taskManager.createSubtask(subtask);
        }, String.format("%s %d %s", "К сожалению, Epic с таким", epicId, "не существует"));
    }

    @Test
    public void shouldThrowExceptionIfStartTimeOfTasksIntersects() {
        assertThrows(TimeConflictException.class, () -> {
            Task task1 = new Task("task1", "description1",
                    LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(15));
            taskManager.createTask(task1);
            Task task2 = new Task("task2", "description2",
                    LocalDateTime.of(2024, Month.JULY, 17, 12, 35), Duration.ofMinutes(30));
            taskManager.createTask(task2);
        }, "На это время уже запланирована другая задача");
    }

    @Test
    public void shouldNotThrowExceptionIfStartTimeOfTasksDoesNotIntersect() {
        assertDoesNotThrow(() -> {
            Task task1 = new Task("task1", "description1",
                    LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(15));
            taskManager.createTask(task1);
            Task task2 = new Task("task2", "description2",
                    LocalDateTime.of(2024, Month.JULY, 17, 12, 50), Duration.ofMinutes(30));
            taskManager.createTask(task2);
        });
    }
}