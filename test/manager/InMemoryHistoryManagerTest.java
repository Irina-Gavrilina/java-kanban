package manager;

import model.Task;
import org.junit.jupiter.api.Assertions;
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
    void shouldSaveInHistoryManagerPreviousVersionsOfTask() {
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
        assertEquals(2, historyList.size());
        assertEquals(historyList.getFirst().getId(), id);
        assertEquals(historyList.getFirst().getTitle(), title);
        assertEquals(historyList.getFirst().getDescription(), description);
    }
}