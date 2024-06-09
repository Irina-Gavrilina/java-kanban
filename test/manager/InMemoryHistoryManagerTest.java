package manager;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Task task = new Task("task", "description");
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

        Task task = new Task(title, description);
        task.setId(id);
        historyManager.addToHistory(task);
        task.setTitle("newTitle");
        task.setDescription("newDescription");
        historyManager.addToHistory(task);
        List<Task> historyList = historyManager.getHistory();
        assertEquals(1, historyList.size());
        assertEquals(historyList.getFirst().getId(), id);
        assertNotEquals(historyList.getFirst().getTitle(), title);
        assertNotEquals(historyList.getFirst().getDescription(), description);
    }

    @Test
    void shouldBeRemovedFromHistoryManagerWhenDeleted() {

        Task task1 = new Task("task1", "description1");
        task1.setId(1);
        historyManager.addToHistory(task1);
        Task task2 = new Task("task2", "description2");
        task2.setId(2);
        historyManager.addToHistory(task2);
        Task task3 = new Task("task3", "description3");
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
        Task task = new Task("task", "description");
        task.setId(1);
        historyManager.remove(task.getId());
        List<Task> historyList = historyManager.getHistory();
        assertEquals(0, historyList.size());
    }
}