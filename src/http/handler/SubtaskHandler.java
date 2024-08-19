package http.handler;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TimeConflictException;
import http.Endpoint;
import http.HttpTaskServer;
import manager.TaskManager;
import model.Subtask;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {

    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {

            case GET_SUBTASKS -> handleGetSubtasks(exchange);
            case GET_SUBTASK_BY_ID -> handleGetSubtaskById(exchange);
            case POST_SUBTASK -> handleCreateOrUpdateSubtask(exchange);
            case DELETE_SUBTASK_BY_ID -> handleDeleteSubtaskById(exchange);
            case UNKNOWN -> sendText(exchange, 400, "К сожалению, такой команды не существует");
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> listOfAllSubtasks = taskManager.getAllSubtasks();
        String response = HttpTaskServer.getGson().toJson(listOfAllSubtasks);
        sendText(exchange, 200, response);
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> subtaskIdOpt = getId(exchange);

            if (subtaskIdOpt.isEmpty()) {
                sendText(exchange, 400, "Некорректный идентификатор");
                return;
            }
            int subtaskId = subtaskIdOpt.get();
            Subtask subtask = taskManager.getSubtaskById(subtaskId);

            if (subtask == null) {
                sendText(exchange, 404, String.format("Подзадачи с идентификатором %d не существует", subtaskId));
                return;
            }
            String response = HttpTaskServer.getGson().toJson(subtask);
            sendText(exchange, 200, response);
        } catch (NotFoundException e) {
            sendText(exchange, 404, e.getMessage());
        }
    }

    void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            Optional<Subtask> optionalSubtask = parseSubtask(inputStream);
            if (optionalSubtask.isEmpty()) {
                sendText(exchange, 400, "Поля подзадачи не могут быть пустыми");
                return;
            }
            Subtask subtask = optionalSubtask.get();
            try {
                if (subtask.getId() != 0) {
                    taskManager.updateSubtask(subtask);
                    sendText(exchange, 200, "Подзадача была успешно обновлена");
                    return;
                }
                taskManager.createSubtask(subtask);
                sendText(exchange, 201, "Подзадача была успешно создана");
            } catch (TimeConflictException e) {
                sendText(exchange, 406, "Подзадача не может быть добавлена, так как пересекается с текущей");
            }
        }
    }

    private void handleDeleteSubtaskById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> subtaskIdOpt = getId(exchange);

            if (subtaskIdOpt.isEmpty()) {
                sendText(exchange, 400, "Некорректный идентификатор");
                return;
            }
            int subtaskId = subtaskIdOpt.get();
            Subtask subtask = taskManager.getSubtaskById(subtaskId);

            if (subtask == null) {
                sendText(exchange, 404, String.format("Подзадачи с идентификатором %d не существует", subtaskId));
                return;
            }
            taskManager.removeSubtaskById(subtaskId);
            sendText(exchange, 200, "Подзадача была успешно удалена");
        } catch (NotFoundException e) {
            sendText(exchange, 404, e.getMessage());
        }
    }

    private Optional<Subtask> parseSubtask(InputStream bodyInputStream) throws IOException {
        String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
        if (!body.contains("title") || !body.contains("description") || !body.contains("startTime")
                || !body.contains("duration") || !body.contains("epicId")) {
            return Optional.empty();
        }
        Subtask subtask = HttpTaskServer.getGson().fromJson(body, Subtask.class);
        return Optional.of(subtask);
    }
}