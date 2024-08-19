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

class EpicHandlerTest extends HttpTaskServerTest {

    protected EpicHandlerTest() throws IOException {
    }

    @Test
    public void testGetListOfAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "description1");
        manager.createEpic(epic1);
        Epic epic2 = new Epic("epic2", "description2");
        manager.createEpic(epic2);
        List<Epic> listOfAllEpics = manager.getAllEpics();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicsFromRequest = gson.fromJson(response.body(), new EpicListTypeToken().getType());
        assertEquals(200, response.statusCode());
        assertEquals(listOfAllEpics, epicsFromRequest);
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description");
        manager.createEpic(epic);
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicFromRequest = gson.fromJson(response.body(), Epic.class);
        assertEquals(200, response.statusCode());
        assertEquals(epic, epicFromRequest);
    }

    @Test
    public void testCreateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description");
        String epicJson = gson.toJson(epic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Epic> epicsFromManager = manager.getAllEpics();
        assertNotNull(epicsFromManager);
        assertEquals(1, epicsFromManager.size());
        assertEquals("epic", epicsFromManager.getFirst().getTitle());
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("epic1", "description1");
        manager.createEpic(epic);
        epic = new Epic("epic2", "description2");
        epic.setId(epic.getId());
        String epicJson = gson.toJson(epic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertNotEquals("epic1", epic.getTitle());
        assertNotEquals("description1", epic.getDescription());
        assertEquals(epic.getTitle(), "epic2");
        assertEquals(epic.getDescription(), "description2");
    }

    @Test
    public void testGetEpicSubtasksById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "description1",
                LocalDateTime.of(2024, Month.JULY, 17, 10, 30), Duration.ofMinutes(25),
                epic.getId());
        Subtask subtask2 = new Subtask("subtask2", "description2",
                LocalDateTime.of(2024, Month.JULY, 17, 12, 35), Duration.ofMinutes(10),
                epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        List<Subtask> listOfAllSubtasks = manager.getAllSubtasks();
        URI url = URI.create(String.format("http://localhost:8080/epics/%s/subtasks", epic.getId()));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasksFromRequest = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        assertEquals(200, response.statusCode());
        assertEquals(listOfAllSubtasks, subtasksFromRequest);
    }

    @Test
    public void shouldDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic1", "description1");
        manager.createEpic(epic);
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> listOfAllEpics = manager.getAllEpics();
        assertEquals(200, response.statusCode());
        assertEquals(listOfAllEpics.size(), 0);
    }

    @Test
    public void shouldReturn404IfRequestedEpicDoesNotExist() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    static class EpicListTypeToken extends TypeToken<List<Epic>> {
    }

    static class SubtaskListTypeToken extends TypeToken<List<Subtask>> {
    }
}