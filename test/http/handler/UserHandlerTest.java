package http.handler;

import http.HttpTaskServerTest;
import model.Task;
import org.junit.jupiter.api.Test;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserHandlerTest extends HttpTaskServerTest {

    protected UserHandlerTest() throws IOException {
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(15));
        manager.createTask(task1);
        Task task2 = new Task("task2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 14, 30), Duration.ofMinutes(5));
        manager.createTask(task2);
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromRequest = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(200, response.statusCode());
        assertNotNull(tasksFromRequest);
        assertEquals(2, tasksFromRequest.size());
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(15));
        manager.createTask(task1);
        Task task2 = new Task("task2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 14, 30), Duration.ofMinutes(5));
        manager.createTask(task2);
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromRequest = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(200, response.statusCode());
        assertNotNull(tasksFromRequest);
        assertEquals(2, tasksFromRequest.size());
    }

    static class TaskListTypeToken extends TypeToken<List<Task>> {
    }
}