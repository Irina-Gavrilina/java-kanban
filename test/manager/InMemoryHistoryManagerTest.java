package manager;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void shouldAddToHistory() {
        Task task = new Task("task", "description", LocalDateTime.now(), Duration.ofMinutes(2));
        historyManager.addToHistory(task);
        List<Task> historyList = historyManager.getHistory();
        assertEquals(1, historyList.size());
        assertEquals(task, historyList.getFirst());
    }

    @Test
    void shouldSaveInHistoryManagerOnlyTheLastVersionOfTask() {
        int id = 1;
        String title = "title";
        String description = "description";
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(15);
        Task task = new Task(title, description, startTime, duration);
        task.setId(id);
        historyManager.addToHistory(task);
        task.setTitle("newTitle");
        task.setDescription("newDescription");
        task.setStartTime(LocalDateTime.of(2024, Month.JULY, 16, 19, 45));
        task.setDuration(Duration.ofMinutes(20));
        historyManager.addToHistory(task);
        List<Task> historyList = historyManager.getHistory();
        assertEquals(1, historyList.size());
        assertEquals(historyList.getFirst().getId(), id);
        assertNotEquals(historyList.getFirst().getTitle(), title);
        assertNotEquals(historyList.getFirst().getDescription(), description);
        assertNotEquals(historyList.getFirst().getStartTime(), startTime);
        assertNotEquals(historyList.getFirst().getDuration(), duration);
    }

    @Test
    void shouldBeRemovedFromHistoryManagerWhenDeleted() {

        Task task1 = new Task("task1", "description1", LocalDateTime.now(), Duration.ofMinutes(5));
        task1.setId(1);
        historyManager.addToHistory(task1);
        Task task2 = new Task("task2", "description2", LocalDateTime.now().plusHours(1),
                Duration.ofMinutes(10));
        task2.setId(2);
        historyManager.addToHistory(task2);
        Task task3 = new Task("task3", "description3", LocalDateTime.now().plusHours(2),
                Duration.ofMinutes(30));
        task3.setId(3);
        historyManager.addToHistory(task3);
        List<Task> historyList = historyManager.getHistory();
        assertEquals(3, historyList.size());
        historyManager.remove(task2.getId());
        historyList = historyManager.getHistory();
        assertEquals(2, historyList.size());

        for (Task task : historyList) {
            assertNotEquals(task2, task);
        }
    }

    @Test
    void shouldBeEmptyIfSingleTaskWasRemoved() {
        Task task = new Task("task", "description", LocalDateTime.now(), Duration.ofMinutes(5));
        task.setId(1);
        historyManager.remove(task.getId());
        List<Task> historyList = historyManager.getHistory();
        assertEquals(0, historyList.size());
    }
}