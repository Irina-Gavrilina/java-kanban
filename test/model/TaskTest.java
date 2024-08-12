package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void shouldBeEqualTasksWhenSameId() {
        Task task1 = new Task("task", "description", LocalDateTime.now(), Duration.ofMinutes(20));
        task1.setId(1);
        Task task2 = new Task("task", "description", LocalDateTime.now().plusHours(2), Duration.ofMinutes(20));
        task2.setId(1);
        assertEquals(task1, task2);
    }
}