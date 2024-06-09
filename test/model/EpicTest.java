package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    void shouldBeEqualEpicsWhenSameId() {
        Epic epic1 = new Epic("epic", "description");
        epic1.setId(1);
        Epic epic2 = new Epic("epic", "description");
        epic2.setId(1);
        assertEquals(epic1, epic2);
    }
}