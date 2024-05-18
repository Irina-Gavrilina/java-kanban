package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void shouldBeEqualTasksWhenSameId() {
        Task task1 = new Task("task", "description");
        task1.setId(1);
        Task task2 = new Task("task", "description");
        task2.setId(1);
        assertEquals(task1, task2);
    }
}