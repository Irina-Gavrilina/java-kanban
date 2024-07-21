package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    void shouldBeEqualSubtasksWhenSameId() {
        Subtask subtask1 = new Subtask("subtask", "description", LocalDateTime.now(), Duration.ofMinutes(5), 1);
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("subtask", "description", LocalDateTime.of(2024, Month.JULY, 16, 18, 30), Duration.ofMinutes(10),1);
        subtask2.setId(1);
        assertEquals(subtask1, subtask2);
    }
}