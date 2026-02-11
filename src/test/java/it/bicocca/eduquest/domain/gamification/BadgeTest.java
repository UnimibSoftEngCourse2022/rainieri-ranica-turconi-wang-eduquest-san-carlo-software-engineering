package it.bicocca.eduquest.domain.gamification;

import it.bicocca.eduquest.domain.users.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadgeTest {

    @Mock
    private Student student;

    @Mock
    private Mission mission;

    @Test
    void testNoArgsConstructorAndSetters() {
        Badge badge = new Badge();
        
        Long id = 1L;
        String name = "Test Badge";
        String description = "Test Description";
        LocalDate date = LocalDate.of(2023, 1, 1);

        badge.setId(id);
        badge.setName(name);
        badge.setDescription(description);
        badge.setObtainedDate(date);
        badge.setStudent(student);

        assertEquals(id, badge.getId());
        assertEquals(name, badge.getName());
        assertEquals(description, badge.getDescription());
        assertEquals(date, badge.getObtainedDate());
        assertEquals(student, badge.getStudent());
    }

    @Test
    void testConstructorWithMissionAndStudent() {
        when(mission.getTitle()).thenReturn("Mission Title");
        when(mission.getDescription()).thenReturn("Mission Description");

        Badge badge = new Badge(mission, student);

        assertEquals("Mission Title", badge.getName());
        assertEquals("Mission Description", badge.getDescription());
        assertEquals(student, badge.getStudent());
        assertEquals(LocalDate.now(), badge.getObtainedDate(), "La data deve essere quella odierna");
    }

    @Test
    void testConstructorWithNameAndStudent() {
        String name = "Simple Badge";

        Badge badge = new Badge(name, student);

        assertEquals(name, badge.getName());
        assertEquals(student, badge.getStudent());
        assertEquals(LocalDate.now(), badge.getObtainedDate(), "La data deve essere quella odierna");
        assertNull(badge.getDescription(), "La descrizione dovrebbe essere null se non impostata");
    }
}