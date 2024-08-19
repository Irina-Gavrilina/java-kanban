package http.handler;

import com.sun.net.httpserver.HttpExchange;
import http.Endpoint;
import http.HttpTaskServer;
import manager.TaskManager;
import java.io.IOException;

public class UserHandler extends BaseHttpHandler {

    private final TaskManager taskManager;

    public UserHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {

            case GET_HISTORY -> handleGetHistory(exchange);
            case GET_PRIORITIZED -> handleGetPrioritized(exchange);
            case UNKNOWN -> sendText(exchange, 400, "К сожалению, такой команды не существует");
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        String response = HttpTaskServer.getGson().toJson(taskManager.getHistory());
        sendText(exchange, 200, response);
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        String response = HttpTaskServer.getGson().toJson(taskManager.getPrioritizedTasks());
        sendText(exchange, 200, response);
    }
}