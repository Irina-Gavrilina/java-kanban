import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
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


        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Субтаск 3", "Описание субтаск 3", epic2.getId());
        taskManager.createSubtask(subtask3);

        System.out.println();
        System.out.println("Таск:");
        System.out.println(taskManager.getAllTasks());
        System.out.println();
        System.out.println("Субтаск:");
        System.out.println(taskManager.getAllSubtasks());
        System.out.println();
        System.out.println("Эпик:");
        System.out.println(taskManager.getAllEpics());
        System.out.println();

        // Меняем статусы и обновляем таск/субтаск/эпик:

        taskManager.getTaskById(1).setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(taskManager.getTaskById(1));
        taskManager.getTaskById(2).setDescription("Новое описание таск 2");
        taskManager.updateTask(taskManager.getTaskById(2));
        taskManager.getSubtaskById(5).setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(taskManager.getSubtaskById(5));
        taskManager.getSubtaskById(7).setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(taskManager.getSubtaskById(7));

        System.out.println("Меняем статусы:");
        System.out.println();
        System.out.println("Таск:");
        System.out.println(taskManager.getAllTasks());
        System.out.println();
        System.out.println("Субтаск:");
        System.out.println(taskManager.getAllSubtasks());
        System.out.println();
        System.out.println("Эпик:");
        System.out.println(taskManager.getAllEpics());
        System.out.println();

        // Удаляем таск/эпик по id:

        System.out.println("Удаляем таск/эпик по id:");
        System.out.println();
        taskManager.removeTaskById(1);
        taskManager.removeEpicById(3);
        System.out.println("Таск:");
        System.out.println(taskManager.getAllTasks());
        System.out.println();
        System.out.println("Субтаск:");
        System.out.println(taskManager.getAllSubtasks());
        System.out.println();
        System.out.println("Эпик:");
        System.out.println(taskManager.getAllEpics());
        System.out.println();
        System.out.println("Вывод истории:");
        System.out.println(taskManager.getHistory());
    }
}