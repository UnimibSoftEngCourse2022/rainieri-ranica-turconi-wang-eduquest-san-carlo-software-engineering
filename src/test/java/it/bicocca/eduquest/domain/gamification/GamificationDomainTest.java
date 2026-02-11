package it.bicocca.eduquest.domain.gamification;

import it.bicocca.eduquest.domain.users.Student;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GamificationDomainTest {

    @Test
    void testBadgeInitialization() {
        Student student = new Student();
        student.setId(1L);
        student.setName("Test Student");

        Badge badge = new Badge("Master Quiz", student);
        badge.setId(1L);
        badge.setDescription("Awarded for mastery");

        assertEquals(1L, badge.getId());
        assertEquals("Master Quiz", badge.getName());
        assertEquals("Awarded for mastery", badge.getDescription());
        assertEquals(student, badge.getStudent());
        assertNotNull(badge.getObtainedDate());

        Badge manualBadge = new Badge();
        manualBadge.setName("Novice");
        manualBadge.setStudent(student);

        assertEquals("Novice", manualBadge.getName());
        assertEquals(student, manualBadge.getStudent());
    }

    @Test
    void testMissionInheritanceAndLogic() {
        ChallengeNumberMission mission = new ChallengeNumberMission(5);
        mission.setId(10L);
        mission.setTitle("Sfida Accettata");
        mission.setDescription("Completa 5 sfide");
        
        assertEquals(10L, mission.getId());
        assertEquals("Sfida Accettata", mission.getTitle());
        assertEquals("Completa 5 sfide", mission.getDescription());
        
        assertEquals(5, mission.getGoal());
    }

    @Test
    void testMissionProgress() {
        MissionProgress progress = new MissionProgress();
        progress.setId(50L);
        
        ChallengeNumberMission mission = new ChallengeNumberMission(10);
        mission.setTitle("Target Mission");
        progress.setMission(mission);

        Student student = new Student();
        student.setName("Marco");
        progress.setStudent(student);

        progress.setGoal(mission.getGoal());
        progress.setCurrentCount(3);
        progress.setCompleted(false);

        assertEquals(50L, progress.getId());
        assertEquals(mission, progress.getMission());
        assertEquals(student, progress.getStudent());
        assertEquals(10, progress.getGoal());
        assertEquals(3, progress.getCurrentCount());
        assertFalse(progress.isCompleted());

        progress.setCurrentCount(10);
        progress.setCompleted(true);
        
        assertEquals(10, progress.getCurrentCount());
        assertTrue(progress.isCompleted());
    }
}