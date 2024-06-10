import manager.Managers;
import manager.TaskManager;
import model.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Task1", "Description");
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "Description");
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic1", "Description");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask1", "Description", epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Description", epic1.getId());
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Subtask3", "Description", epic1.getId());
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