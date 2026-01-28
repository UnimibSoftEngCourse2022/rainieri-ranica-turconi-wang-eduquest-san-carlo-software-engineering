package it.bicocca.eduquest.domain.users;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsersDomainTest {

    @Test
    void testStudentInitializationAndLogic() {
        Student student = new Student("Luca", "Bianchi", "luca@student.it", "password123");
        
        assertEquals("Luca", student.getName());
        assertEquals("Bianchi", student.getSurname());
        assertEquals("luca@student.it", student.getEmail());
        assertEquals("password123", student.getPassword());
        assertEquals(Role.STUDENT, student.getRole());
        assertEquals(0.0, student.getScore());
        assertNotNull(student.getStats());

        student.updateTotalScore(10.5);
        assertEquals(10.5, student.getScore());

        student.updateTotalScore(5.0);
        assertEquals(15.5, student.getScore());
    }

    @Test
    void testStudentEmptyConstructor() {
        Student student = new Student();
        student.setId(1L);
        student.setName("Test");
        student.setScore(100.0);
        
        assertEquals(1L, student.getId());
        assertEquals("Test", student.getName());
        assertEquals(Role.STUDENT, student.getRole());
        assertEquals(100.0, student.getScore());
        assertNotNull(student.getStats());
    }

    @Test
    void testTeacherInitialization() {
        Teacher teacher = new Teacher("Maria", "Verdi", "maria@prof.it", "securePass");
        teacher.setId(5L);

        assertEquals("Maria", teacher.getName());
        assertEquals(Role.TEACHER, teacher.getRole());
        assertEquals(5L, teacher.getId());

        Teacher teacherEmpty = new Teacher();
        assertEquals(Role.TEACHER, teacherEmpty.getRole());
    }

    @Test
    void testStudentStats() {
        StudentStats stats = new StudentStats();
        
        assertEquals(0, stats.getNumberOfCompletedQuizzes());
        assertEquals(0, stats.getNumberOfCompletedMissions());

        stats.setNumberOfCompletedQuizzes(5);
        stats.setNumberOfCompletedMissions(3);

        assertEquals(5, stats.getNumberOfCompletedQuizzes());
        assertEquals(3, stats.getNumberOfCompletedMissions());
    }

    @Test
    void testStudentStatsIntegration() {
        Student student = new Student();
        StudentStats newStats = new StudentStats();
        newStats.setNumberOfCompletedQuizzes(10);
        
        student.setStats(newStats);
        
        assertEquals(10, student.getStats().getNumberOfCompletedQuizzes());
    }

    @Test
    void testRoleEnum() {
        assertEquals(Role.STUDENT, Role.valueOf("STUDENT"));
        assertEquals(Role.TEACHER, Role.valueOf("TEACHER"));
        assertEquals(2, Role.values().length);
    }
}