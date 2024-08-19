package http.handler;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TimeConflictException;
import http.Endpoint;
import http.HttpTaskServer;
import manager.TaskManager;
import model.Task;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {

    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {

            case GET_TASKS -> handleGetTasks(exchange);
            case GET_TASK_BY_ID -> handleGetTaskById(exchange);
            case POST_TASK -> handleCreateOrUpdateTask(exchange);
            case DELETE_TASK_BY_ID -> handleDeleteTaskById(exchange);
            case UNKNOWN -> sendText(exchange, 400, "К сожалению, такой команды не существует");
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<Task> listOfAllTasks = taskManager.getAllTasks();
        String response = HttpTaskServer.getGson().toJson(listOfAllTasks);
        sendText(exchange, 200, response);
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> taskIdOpt = getId(exchange);

            if (taskIdOpt.isEmpty()) {
                sendText(exchange, 400, "Некорректный идентификатор");
                return;
            }
            int taskId = taskIdOpt.get();
            Task task = taskManager.getTaskById(taskId);

            if (task == null) {
                sendText(exchange, 404, String.format("Задачи с идентификатором %d не существует", taskId));
                return;
            }
            String response = HttpTaskServer.getGson().toJson(task);
            sendText(exchange, 200, response);
        } catch (NotFoundException e) {
            sendText(exchange, 404, e.getMessage());
        }
    }

    void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            Optional<Task> optionalTask = parseTask(inputStream);
            if (optionalTask.isEmpty()) {
                sendText(exchange, 400, "Поля задачи не могут быть пустыми");
                return;
            }
            Task task = optionalTask.get();
            try {
                if (task.getId() != 0) {
                    taskManager.updateTask(task);
                    sendText(exchange, 200, "Задача была успешно обновлена");
                    return;
                }
                taskManager.createTask(task);
                sendText(exchange, 201, "Задача была успешно создана");
            } catch (TimeConflictException e) {
                sendText(exchange, 406, "Задача не может быть добавлена, так как пересекается с текущей");
            }
        }
    }

    private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> taskIdOpt = getId(exchange);

            if (taskIdOpt.isEmpty()) {
                sendText(exchange, 400, "Некорректный идентификатор");
                return;
            }
            int taskId = taskIdOpt.get();
            Task task = taskManager.getTaskById(taskId);

            if (task == null) {
                sendText(exchange, 404, String.format("Задачи с идентификатором %d не существует", taskId));
                return;
            }
            taskManager.removeTaskById(taskId);
            sendText(exchange, 200, "Задача была успешно удалена");
        } catch (NotFoundException e) {
            sendText(exchange, 404, e.getMessage());
        }
    }

    private Optional<Task> parseTask(InputStream bodyInputStream) throws IOException {
        String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
        if (!body.contains("title") || !body.contains("description") || !body.contains("startTime") || !body.contains("duration")) {
            return Optional.empty();
        }
        Task task = HttpTaskServer.getGson().fromJson(body, Task.class);
        return Optional.of(task);
    }
}