package it.bicocca.eduquest.domain.gamification;

import it.bicocca.eduquest.domain.users.Student;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GamificationDomainTest {

    @Test
    void testBadgeInitialization() {
        Badge badge = new Badge("Master Quiz", "http://icon.url");
        badge.setId(1L);

        assertEquals(1L, badge.getId());
        assertEquals("Master Quiz", badge.getName());
        assertEquals("http://icon.url", badge.getIconUrl());

        Badge emptyBadge = new Badge();
        emptyBadge.setName("Novice");
        emptyBadge.setIconUrl("novice.png");

        assertEquals("Novice", emptyBadge.getName());
        assertEquals("novice.png", emptyBadge.getIconUrl());
    }

    @Test
    void testMissionInheritanceAndLogic() {
        ChallengeNumberMission mission = new ChallengeNumberMission();
        mission.setId(10L);
        mission.setTitle("Sfida Accettata");
        mission.setDescription("Completa 5 sfide");
        
        Badge badge = new Badge("Challenger", "challenger.png");
        mission.setBadge(badge);

        assertEquals(10L, mission.getId());
        assertEquals("Sfida Accettata", mission.getTitle());
        assertEquals("Completa 5 sfide", mission.getDescription());
        assertEquals(badge, mission.getBadge());
        assertEquals("Challenger", mission.getBadge().getName());
    }

    @Test
    void testMissionProgress() {
        MissionProgress progress = new MissionProgress();
        progress.setId(50L);
        
        ChallengeNumberMission mission = new ChallengeNumberMission();
        mission.setTitle("Target Mission");
        progress.setMission(mission);

        Student student = new Student();
        student.setName("Marco");
        progress.setStudent(student);

        progress.setGoal(10);
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