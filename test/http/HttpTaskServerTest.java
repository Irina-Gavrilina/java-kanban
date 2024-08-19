package http;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.io.IOException;
import java.net.http.HttpClient;

public abstract class HttpTaskServerTest {
    protected TaskManager manager = new InMemoryTaskManager();
    protected HttpTaskServer taskServer = new HttpTaskServer(manager);
    protected Gson gson = HttpTaskServer.getGson();
    protected HttpClient client = HttpClient.newHttpClient();

    protected HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    void setUp() {
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }
}