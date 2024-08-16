package http.handler;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TimeConflictException;
import http.Endpoint;
import http.HttpTaskServer;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {

    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {

            case GET_EPICS -> handleGetEpics(exchange);
            case GET_EPIC_BY_ID -> handleGetEpicById(exchange);
            case GET_EPIC_SUBTASKS_BY_ID -> handleGetEpicSubtasksById(exchange);
            case POST_EPIC -> handleCreateOrUpdateEpic(exchange);
            case DELETE_EPIC_BY_ID -> handleDeleteEpicById(exchange);
            case UNKNOWN -> sendText(exchange, 400, "К сожалению, такой команды не существует");
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        List<Epic> listOfAllEpics = taskManager.getAllEpics();
        String response = HttpTaskServer.getGson().toJson(listOfAllEpics);
        sendText(exchange, 200, response);
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> epicIdOpt = getId(exchange);

            if (epicIdOpt.isEmpty()) {
                sendText(exchange, 400, "Некорректный идентификатор");
                return;
            }
            int epicId = epicIdOpt.get();
            Epic epic = taskManager.getEpicById(epicId);

            if (epic == null) {
                sendText(exchange, 404, String.format("Эпика с идентификатором %d не существует", epicId));
                return;
            }
            String response = HttpTaskServer.getGson().toJson(epic);
            sendText(exchange, 200, response);
        } catch (NotFoundException e) {
            sendText(exchange, 404, e.getMessage());
        }
    }

    private void handleGetEpicSubtasksById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> epicIdOpt = getId(exchange);

            if (epicIdOpt.isEmpty()) {
                sendText(exchange, 400, "Некорректный идентификатор");
                return;
            }
            int epicId = epicIdOpt.get();
            Epic epic = taskManager.getEpicById(epicId);

            if (epic == null) {
                sendText(exchange, 404, String.format("Эпика с идентификатором %d не существует", epicId));
                return;
            }
            List<Subtask> listOfAllSubtasks = taskManager.getSubtasksByEpicId(epicId);
            String response = HttpTaskServer.getGson().toJson(listOfAllSubtasks);
            sendText(exchange, 200, response);
        } catch (NotFoundException e) {
            sendText(exchange, 404, e.getMessage());
        }
    }

    void handleCreateOrUpdateEpic(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            Optional<Epic> optionalEpic = parseEpic(inputStream);
            if (optionalEpic.isEmpty()) {
                sendText(exchange, 400, "Поля эпика не могут быть пустыми");
                return;
            }
            Epic epic = optionalEpic.get();
            try {
                if (epic.getId() != 0) {
                    taskManager.updateEpic(epic);
                    sendText(exchange, 200, "Эпик был успешно обновлен");
                    return;
                }
                taskManager.createEpic(epic);
                sendText(exchange, 201, "Эпик был успешно создан");
            } catch (TimeConflictException e) {
                sendText(exchange, 406, "Эпик не может быть добавлен, так как пересекается с текущмим");
            }
        }
    }

    private void handleDeleteEpicById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> epicIdOpt = getId(exchange);

            if (epicIdOpt.isEmpty()) {
                sendText(exchange, 400, "Некорректный идентификатор");
                return;
            }
            int epicId = epicIdOpt.get();
            Epic epic = taskManager.getEpicById(epicId);

            if (epic == null) {
                sendText(exchange, 404, String.format("Эпика с идентификатором %d не существует", epicId));
                return;
            }
            taskManager.removeEpicById(epicId);
            sendText(exchange, 200, "Эпик был успешно удален");
        } catch (NotFoundException e) {
            sendText(exchange, 404, e.getMessage());
        }
    }

    private Optional<Epic> parseEpic(InputStream bodyInputStream) throws IOException {
        String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
        if (!body.contains("title") || !body.contains("description")) {
            return Optional.empty();
        }
        Epic epic = HttpTaskServer.getGson().fromJson(body, Epic.class);
        return Optional.of(epic);
    }
}