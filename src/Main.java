import manager.Managers;
import manager.TaskManager;
import model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Task1", "Description", LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "Description", LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(5));
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic1", "Description");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask1", "Description",
                LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(5), epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Description",
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(2), epic1.getId());
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Subtask3", "Description",
                LocalDateTime.now().plusHours(2), Duration.ofMinutes(10), epic1.getId());
        taskManager.createSubtask(subtask3);

        Epic epic2 = new Epic("Epic2", "Description");
        taskManager.createEpic(epic2);

        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getEpicById(epic1.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.removeTaskById(task1.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.removeEpicById(epic1.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();
    }
}