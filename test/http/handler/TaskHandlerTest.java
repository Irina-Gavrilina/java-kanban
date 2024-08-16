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

class TaskHandlerTest extends HttpTaskServerTest {

    protected TaskHandlerTest() throws IOException {
    }

    @Test
    public void testGetListOfAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(15));
        manager.createTask(task1);
        Task task2 = new Task("task2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 14, 30), Duration.ofMinutes(5));
        manager.createTask(task2);
        URI url = URI.create("http://localhost:8080/tasks");
        List<Task> listOfAllTasks = manager.getAllTasks();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromRequest = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(200, response.statusCode(), "Коды ответа не совпадают");
        assertEquals(listOfAllTasks, tasksFromRequest, "Списки задач не совпадают");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("task", "description",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(15));
        manager.createTask(task);
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskFromRequest = gson.fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode(), "Коды ответа не совпадают");
        assertEquals(task, taskFromRequest, "Задачи не совпадают");
    }

    @Test
    public void testCreateTask() throws IOException, InterruptedException {
        Task task = new Task("task", "description", LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Коды ответа не совпадают");
        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("task", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("task1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(15));
        manager.createTask(task);
        task = new Task("task2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 14, 30), Duration.ofMinutes(15));
        task.setId(task.getId());
        String taskJson = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Коды ответа не совпадают");
        assertNotEquals("task1", task.getTitle(), "Некорректное имя задачи");
        assertNotEquals("description1", task.getDescription(), "Некорректное описание задачи");
        assertEquals(task.getTitle(), "task2", "Некорректное имя задачи");
        assertEquals(task.getDescription(), "description2", "Некорректное описание задачи");
    }

    @Test
    public void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("task", "description",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(15));
        manager.createTask(task);
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> listOfAllTasks = manager.getAllTasks();
        assertEquals(200, response.statusCode(), "Коды ответа не совпадают");
        assertEquals(listOfAllTasks.size(), 0, "Задача не удалена");
    }

    @Test
    public void shouldNotAddNewTaskIfItIntersectsWithExistingTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        Task task1 = new Task("task1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(45));
        Task task2 = new Task(task1);
        manager.createTask(task2);
        String taskJson = gson.toJson(task1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Коды ответа не совпадают");
    }

    @Test
    public void shouldReturn404IfRequestedTaskDoesNotExist() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Коды ответа не совпадают");
    }

    static class TaskListTypeToken extends TypeToken<List<Task>> {
    }
}