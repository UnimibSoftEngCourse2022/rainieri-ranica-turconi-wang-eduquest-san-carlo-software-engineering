package it.bicocca.eduquest.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Collections;

import it.bicocca.eduquest.repository.MissionsProgressesRepository;
import it.bicocca.eduquest.repository.MissionsRepository;
import it.bicocca.eduquest.dto.gamification.MissionProgressDTO;
import it.bicocca.eduquest.domain.gamification.Mission;
import it.bicocca.eduquest.domain.gamification.MissionProgress;
import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.domain.answers.QuizAttempt;

@ExtendWith(MockitoExtension.class)
class GamificationServicesTest {

    @Mock
    private MissionsProgressesRepository missionsProgressesRepository;

    @Mock
    private MissionsRepository missionsRepository;

    @InjectMocks
    private GamificationServices gamificationServices;

    @Test
    void getAllMissionsProgresses_ShouldReturnCorrectData_AndFilterCompleted() {
        long userId = 1L;
        Student student = new Student(); student.setId(userId);

        Mission m1 = createMission(11L, "Study", 11);
        MissionProgress p1 = new MissionProgress(m1, student, 11);
        p1.setId(111L); 
        p1.setCurrentCount(5);
        p1.setCompleted(false);

        Mission m2 = createMission(22L, "Win", 1);
        MissionProgress p2 = new MissionProgress(m2, student, 1);
        p2.setId(222L); 
        p2.setCurrentCount(1);
        p2.setCompleted(true);

        when(missionsProgressesRepository.findByStudentId(userId))
            .thenReturn(List.of(p1, p2));

        List<MissionProgressDTO> allResults = gamificationServices.getAllMissionsProgressesByUserId(userId, false);
        List<MissionProgressDTO> completedResults = gamificationServices.getAllMissionsProgressesByUserId(userId, true);

        assertEquals(2, allResults.size());
        assertEquals(1, completedResults.size());
        assertEquals("Win", completedResults.get(0).getMission().getTitle()); 
    }
    
    @Test
    void updateMissionsProgresses_ShouldCreateMissingProgress_IfNotExist() {
                Student student = new Student(); student.setId(1L);
        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudent(student);

        Mission mission = createMission(11L, "New mission", 5);
        when(missionsRepository.findAll()).thenReturn(List.of(mission));

        when(missionsProgressesRepository.findByMissionIdAndStudentId(mission.getId(), student.getId()))
            .thenReturn(Collections.emptyList());
        
        when(missionsProgressesRepository.findByStudentId(student.getId()))
            .thenReturn(Collections.emptyList());

        gamificationServices.updateMissionsProgresses(attempt);

        verify(missionsProgressesRepository).save(any(MissionProgress.class));
    }

    @Test
    void updateMissionsProgresses_ShouldUpdateCount_AndMarkCompleted_WhenGoalReached() {
        Student student = new Student(); student.setId(1L);
        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudent(student);

        Mission mission = mock(Mission.class);
        lenient().when(mission.getId()).thenReturn(10L);   
        lenient().when(mission.getGoal()).thenReturn(10); 
        
        lenient().when(mission.getProgress(anyInt(), any(QuizAttempt.class))).thenReturn(10); 

        MissionProgress progress = new MissionProgress(mission, student, 10);
        progress.setId(500L); 
        progress.setCurrentCount(9); 
        progress.setCompleted(false);

        when(missionsRepository.findAll()).thenReturn(List.of(mission));
        
        lenient().when(missionsProgressesRepository.findByMissionIdAndStudentId(anyLong(), anyLong()))
            .thenReturn(List.of(progress));
        
        when(missionsProgressesRepository.findByStudentId(student.getId()))
            .thenReturn(List.of(progress));

        gamificationServices.updateMissionsProgresses(attempt);

        ArgumentCaptor<MissionProgress> captor = ArgumentCaptor.forClass(MissionProgress.class);
        verify(missionsProgressesRepository).save(captor.capture());
        
        MissionProgress savedProgress = captor.getValue();
        
        assertEquals(10, savedProgress.getCurrentCount(), "Count to 10");
        assertTrue(savedProgress.isCompleted(), "Mission completed");
    }

    private Mission createMission(Long id, String title, int goal) {
        return new TestMission(id, title, goal);
    }

    static class TestMission extends Mission {
        private int goalValue; 

        public TestMission(Long id, String title, int goal) {
            this.id = id;
            this.title = title;
            this.goalValue = goal;
            this.description = "Fake description";
        }

        @Override
        public int getGoal() {
            return this.goalValue; 
        }

        @Override
        public int getProgress(int currentProgress, QuizAttempt attempt) {
            return currentProgress + 1;
        }
    }
    
    @Test
    void updateMissionsProgressesAlreadyCompleted() {
        Student student = new Student(); student.setId(1L);
        QuizAttempt attempt = new QuizAttempt(); attempt.setStudent(student);

        Mission mission = createMission(10L, "Done", 10);
        MissionProgress progress = new MissionProgress(mission, student, 10);
        progress.setCompleted(true); 

        when(missionsRepository.findAll()).thenReturn(List.of(mission));
        
        lenient().when(missionsProgressesRepository.findByMissionIdAndStudentId(10L, 1L))
            .thenReturn(List.of(progress));
        
        when(missionsProgressesRepository.findByStudentId(1L))
            .thenReturn(List.of(progress));

        gamificationServices.updateMissionsProgresses(attempt);

        verify(missionsProgressesRepository, never()).save(progress);
    }

    @Test
    void updateMissionsProgressesGoalNotReached() {
        Student student = new Student(); student.setId(1L);
        QuizAttempt attempt = new QuizAttempt(); attempt.setStudent(student);

        Mission mission = createMission(10L, "Ongoing", 10); 
        
        MissionProgress progress = new MissionProgress(mission, student, 10);
        progress.setCurrentCount(2);
        progress.setCompleted(false);

        when(missionsRepository.findAll()).thenReturn(List.of(mission));
        lenient().when(missionsProgressesRepository.findByMissionIdAndStudentId(10L, 1L))
            .thenReturn(List.of(progress));
        when(missionsProgressesRepository.findByStudentId(1L))
            .thenReturn(List.of(progress));

        gamificationServices.updateMissionsProgresses(attempt);

        ArgumentCaptor<MissionProgress> captor = ArgumentCaptor.forClass(MissionProgress.class);
        verify(missionsProgressesRepository).save(captor.capture());

        MissionProgress saved = captor.getValue();
        assertEquals(3, saved.getCurrentCount(), "Dovrebbe incrementare da 2 a 3");
        assertFalse(saved.isCompleted(), "Non dovrebbe essere completata (3 < 10)");
    }

    @Test
    void refreshWeeklyMissionsSelect4NewRandomly() {
        long userId = 1L;

        List<Mission> allMissions = new java.util.ArrayList<>();
        for (long i = 1; i <= 6; i++) {
            allMissions.add(createMission(i, "Mission " + i, 10));
        }

        when(missionsRepository.findAll()).thenReturn(allMissions);

        gamificationServices.refreshWeeklyMissions(userId);

        verify(missionsProgressesRepository).deleteByStudentId(userId);

        verify(missionsProgressesRepository, times(4)).save(any(MissionProgress.class));
    }
    
    @Test
    void updateMissionsProgressesSkipCompleted() {
        Student student = new Student(); student.setId(1L);
        QuizAttempt attempt = new QuizAttempt(); attempt.setStudent(student);

        Mission mission = mock(Mission.class);
        lenient().when(mission.getId()).thenReturn(10L); 
        
        MissionProgress progress = new MissionProgress(mission, student, 10);
        progress.setCompleted(true); 
        when(missionsRepository.findAll()).thenReturn(Collections.emptyList());
        
        when(missionsProgressesRepository.findByStudentId(1L))
            .thenReturn(List.of(progress));

        gamificationServices.updateMissionsProgresses(attempt);

        verify(missionsProgressesRepository, never()).save(progress);
        verify(mission, never()).getProgress(anyInt(), any());
    }

    @Test
    void updateMissionsProgressesButNotComplete() {
        Student student = new Student(); student.setId(1L);
        QuizAttempt attempt = new QuizAttempt(); attempt.setStudent(student);

        Mission mission = mock(Mission.class);
        lenient().when(mission.getId()).thenReturn(10L);
        lenient().when(mission.getGoal()).thenReturn(100); 
        
        when(mission.getProgress(eq(0), any(QuizAttempt.class))).thenReturn(50);

        MissionProgress progress = new MissionProgress(mission, student, 100);
        progress.setCurrentCount(0);
        progress.setCompleted(false);

        when(missionsRepository.findAll()).thenReturn(Collections.emptyList());
        when(missionsProgressesRepository.findByStudentId(1L)).thenReturn(List.of(progress));

        gamificationServices.updateMissionsProgresses(attempt);

        ArgumentCaptor<MissionProgress> captor = ArgumentCaptor.forClass(MissionProgress.class);
        verify(missionsProgressesRepository).save(captor.capture());
        
        MissionProgress saved = captor.getValue();
        assertEquals(50, saved.getCurrentCount()); 
        assertFalse(saved.isCompleted()); 
    }
}