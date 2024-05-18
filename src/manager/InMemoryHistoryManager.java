package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyList = new ArrayList<>();
    private final static int MAX_HISTORY_SIZE = 10;

    @Override
    public void addToHistory(Task task) {
        if (historyList.size() >= MAX_HISTORY_SIZE) {
            historyList.removeFirst();
        }
        Task taskToAdd = new Task(task);
        historyList.add(taskToAdd);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyList);
    }
}