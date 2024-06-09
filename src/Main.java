import manager.Managers;
import manager.TaskManager;
import model.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Таск 1", "Описание таск 1");
        taskManager.createTask(task1);
        Task task2 = new Task("Таск 2", "Описание таск 2");
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Субтаск 1", "Описание субтаск 1", epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Субтаск 2", "Описание субтаск 2", epic1.getId());
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Субтаск 3", "Описание субтаск 3", epic1.getId());
        taskManager.createSubtask(subtask3);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
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