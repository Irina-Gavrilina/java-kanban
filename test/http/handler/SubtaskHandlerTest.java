package http.handler;

import http.HttpTaskServerTest;
import model.Epic;
import model.Subtask;
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

class SubtaskHandlerTest extends HttpTaskServerTest {

    protected SubtaskHandlerTest() throws IOException {
    }

    @Test
    public void testGetListOfAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 10, 30), Duration.ofMinutes(5),
                epic.getId());
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 11, 30), Duration.ofMinutes(10),
                epic.getId());
        manager.createSubtask(subtask2);
        List<Subtask> listOfAllSubtasks = manager.getAllSubtasks();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasksFromRequest = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        assertEquals(200, response.statusCode(), "Коды ответа не совпадают");
        assertEquals(listOfAllSubtasks, subtasksFromRequest, "Списки подзадач не совпадают");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask", "description",
                LocalDateTime.of(2024, Month.JULY, 17, 10, 30), Duration.ofMinutes(5),
                epic.getId());
        manager.createSubtask(subtask);
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtaskFromRequest = gson.fromJson(response.body(), Subtask.class);
        assertEquals(200, response.statusCode(), "Коды ответа не совпадают");
        assertEquals(subtask, subtaskFromRequest, "Подзадачи не совпадают");
    }

    @Test
    public void testCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask", "description",
                LocalDateTime.of(2024, Month.JULY, 17, 10, 30), Duration.ofMinutes(5),
                epic.getId());
        String subtaskJson = gson.toJson(subtask);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Коды ответа не совпадают");
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подззадач");
        assertEquals("subtask", subtasksFromManager.getFirst().getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(15), epic.getId());
        manager.createSubtask(subtask);
        subtask = new Subtask("subtask2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 14, 30), Duration.ofMinutes(15), epic.getId());
        subtask.setId(subtask.getId());
        String subtaskJson = gson.toJson(subtask);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Коды ответа не совпадают");
        assertNotEquals("subtask1", subtask.getTitle(), "Некорректное имя подзадачи");
        assertNotEquals("description1", subtask.getDescription(), "Некорректное описание подзадачи");
        assertEquals(subtask.getTitle(), "subtask2", "Некорректное имя подзадачи");
        assertEquals(subtask.getDescription(), "description2", "Некорректное описание подзадачи");
    }

    @Test
    public void shouldDeleteSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 30), Duration.ofMinutes(15), epic.getId());
        manager.createSubtask(subtask);
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> listOfAllSubtasks = manager.getAllSubtasks();
        assertEquals(200, response.statusCode(), "Коды ответа не совпадают");
        assertEquals(listOfAllSubtasks.size(), 0, "Подзадача не удалена");
    }

    @Test
    public void shouldNotAddNewSubtaskIfItIntersectsWithExistingSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks");
        Epic epic = new Epic("epic", "description");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 10, 30), Duration.ofMinutes(25),
                epic.getId());
        Subtask subtask2 = new Subtask("subtask2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 10, 35), Duration.ofMinutes(10),
                epic.getId());
        manager.createSubtask(subtask2);
        String subtaskJson = gson.toJson(subtask1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Коды ответа не совпадают");
    }

    @Test
    public void shouldReturn404IfRequestedSubtaskDoesNotExist() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Коды ответа не совпадают");
    }

    static class SubtaskListTypeToken extends TypeToken<List<Subtask>> {
    }
}