package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    void shouldBeEqualSubtasksWhenSameId() {
        Subtask subtask1 = new Subtask("subtask", "description", 1);
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("subtask", "description",  1);
        subtask2.setId(1);
        assertEquals(subtask1, subtask2);
    }
}