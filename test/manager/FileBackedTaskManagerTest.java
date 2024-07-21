package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(new File("test.csv"));
    }

    @Test
    void savingAnEmptyFileTest() {
        try {
            File tmpFile = File.createTempFile("test", "csv");
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tmpFile);
            String[] strings = Files.readString(tmpFile.toPath()).split("\n");
            assertEquals(strings.length, 1);
            assertEquals(strings[0], "id,type,title,status,description,startTime,duration,epic");
            tmpFile.deleteOnExit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void savingNotEmptyFileTest() {
        try {
            File tmpFile = File.createTempFile("test", "csv");
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tmpFile);
            Task task = new Task("Task1", "Task1_Description",
                    LocalDateTime.of(2024, Month.JULY, 16, 13, 30), Duration.ofMinutes(2));
            fileBackedTaskManager.createTask(task);
            Epic epic = new Epic("Epic1", "Epic1_Description");
            fileBackedTaskManager.createEpic(epic);
            Subtask subtask1 = new Subtask("Subtask1", "Subtask1_Description",
                    LocalDateTime.of(2024, Month.JULY, 16, 14, 10),
                    Duration.ofMinutes(1),epic.getId());
            fileBackedTaskManager.createSubtask(subtask1);
            Subtask subtask2 = new Subtask("Subtask2", "Subtask2_Description",
                    LocalDateTime.of(2024, Month.JULY, 16, 14, 30), Duration.ofMinutes(5),
                    epic.getId());
            fileBackedTaskManager.createSubtask(subtask2);
            String[] strings = Files.readString(tmpFile.toPath()).split("\n");
            assertEquals(strings.length, 5);
            assertEquals(strings[0], "id,type,title,status,description,startTime,duration,epic");
            assertEquals(strings[1], "1,TASK,Task1,NEW,Task1_Description,16.07.24 13:30,2");
            assertEquals(strings[2], "2,EPIC,Epic1,NEW,Epic1_Description,16.07.24 14:10,6");
            assertEquals(strings[3], "3,SUBTASK,Subtask1,NEW,Subtask1_Description,16.07.24 14:10,1,2");
            assertEquals(strings[4], "4,SUBTASK,Subtask2,NEW,Subtask2_Description,16.07.24 14:30,5,2");
            tmpFile.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void loadingAnEmptyFileTest() {
        try {
            File tmpFile = File.createTempFile("test", "csv");
            FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
            assertEquals(fileBackedTaskManager.getAllTasks().size(), 0);
            assertEquals(fileBackedTaskManager.getAllSubtasks().size(), 0);
            assertEquals(fileBackedTaskManager.getAllEpics().size(), 0);
            tmpFile.deleteOnExit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
//    id,type,title,status,description,startTime,duration,epic
    @Test
    void loadingNotEmptyFileTest() {
        try {
            File tmpFile = File.createTempFile("test", "csv");
            try (Writer fileWriter = new FileWriter(tmpFile)) {
                fileWriter.write("id,type,title,status,description,startTime,duration,epic\n");
                fileWriter.write("1,TASK,Task1,NEW,Task1_Description,16.07.24 13:30,2\n");
                fileWriter.write("2,EPIC,Epic1,NEW,Epic1_Description,16.07.24 14:10,6\n");
                fileWriter.write("3,SUBTASK,Subtask1,NEW,Subtask1_Description,16.07.24 14:10,1,2\n");
                fileWriter.write("4,SUBTASK,Subtask2,NEW,Subtask2_Description,16.07.24 14:30,5,2\n");
            }
            FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
            assertEquals(fileBackedTaskManager.getAllTasks().size(), 1);
            assertEquals(fileBackedTaskManager.getAllEpics().size(), 1);
            assertEquals(fileBackedTaskManager.getAllSubtasks().size(), 2);
            tmpFile.deleteOnExit();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}