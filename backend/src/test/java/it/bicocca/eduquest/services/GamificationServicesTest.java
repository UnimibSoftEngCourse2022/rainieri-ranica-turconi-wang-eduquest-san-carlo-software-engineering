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
import java.util.ArrayList;
import java.util.Collections;
import java.time.LocalDate;

import it.bicocca.eduquest.repository.MissionsProgressesRepository;
import it.bicocca.eduquest.repository.MissionsRepository;
import it.bicocca.eduquest.dto.gamification.MissionProgressDTO;
import it.bicocca.eduquest.domain.gamification.*;
import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.domain.answers.QuizAttempt;
import it.bicocca.eduquest.domain.answers.QuizAttemptStatus;
import it.bicocca.eduquest.repository.BadgeRepository;

@ExtendWith(MockitoExtension.class)
class GamificationServicesTest {

    @Mock
    private MissionsProgressesRepository missionsProgressesRepository;

    @Mock
    private MissionsRepository missionsRepository;
    
    @Mock
    private BadgeRepository badgeRepository;

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
        p1.setAssignmentDate(LocalDate.now());

        Mission m2 = createMission(22L, "Win", 1);
        MissionProgress p2 = new MissionProgress(m2, student, 1);
        p2.setId(222L); 
        p2.setCurrentCount(1);
        p2.setCompleted(true);
        p2.setAssignmentDate(LocalDate.now());

        when(missionsProgressesRepository.findByStudentId(userId))
            .thenReturn(List.of(p1, p2));

        List<MissionProgressDTO> allResults = gamificationServices.getAllMissionsProgressesByUserId(userId, false);
        List<MissionProgressDTO> completedResults = gamificationServices.getAllMissionsProgressesByUserId(userId, true);

        assertEquals(2, allResults.size());
        assertEquals(1, completedResults.size());
        assertEquals("Win", completedResults.get(0).getMission().getTitle()); 
    }
    
    @Test
    void getAllMissionsProgresses_ShouldRefresh_WhenListEmpty() {
        long userId = 1L;
        when(missionsProgressesRepository.findByStudentId(userId))
            .thenReturn(Collections.emptyList()) 
            .thenReturn(new ArrayList<>()); 

        gamificationServices.getAllMissionsProgressesByUserId(userId, false);

        verify(missionsProgressesRepository).deleteByStudentId(userId);
    }

    @Test
    void getAllMissionsProgresses_ShouldRefresh_WhenDateNull() {
        long userId = 1L;
        Student student = new Student(); student.setId(userId);
        
        Mission m = createMission(1L, "T", 1);
        MissionProgress p = new MissionProgress(m, student, 1);
        p.setId(100L);
        p.setAssignmentDate(null);

        when(missionsProgressesRepository.findByStudentId(userId))
            .thenReturn(List.of(p)); 

        gamificationServices.getAllMissionsProgressesByUserId(userId, false);

        verify(missionsProgressesRepository).deleteByStudentId(userId);
    }
    
    @Test
    void getAllMissionsProgresses_ShouldRefresh_WhenDateIsOld() {
        long userId = 1L;
        Student student = new Student(); student.setId(userId);
        
        Mission m = createMission(1L, "T", 1);
        MissionProgress p = new MissionProgress(m, student, 1);
        p.setId(101L);
        p.setAssignmentDate(LocalDate.now().minusWeeks(2));

        when(missionsProgressesRepository.findByStudentId(userId))
            .thenReturn(List.of(p)); 

        gamificationServices.getAllMissionsProgressesByUserId(userId, false);

        verify(missionsProgressesRepository).deleteByStudentId(userId);
    }

    @Test
    void updateMissionsProgresses_ShouldDoNothing_IfNoMissionsExist() {
        Student student = new Student(); 
        student.setId(1L);
        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudent(student);

        when(missionsProgressesRepository.findByStudentId(student.getId()))
            .thenReturn(Collections.emptyList());

        gamificationServices.updateMissionsProgresses(attempt);

        verify(missionsProgressesRepository, never()).save(any(MissionProgress.class));
    }

    @Test
    void updateMissionsProgresses_ShouldUpdateCount_AndMarkCompleted_WhenGoalReached() {
        Student student = new Student(); student.setId(1L);
        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudent(student);

        QuizzesNumberMission mission = mock(QuizzesNumberMission.class);
        lenient().when(mission.getId()).thenReturn(10L);   
        lenient().when(mission.getGoal()).thenReturn(10); 
        
        lenient().when(mission.getProgress(anyInt(), any(QuizAttempt.class))).thenReturn(10); 

        MissionProgress progress = new MissionProgress(mission, student, 10);
        progress.setId(500L); 
        progress.setCurrentCount(9); 
        progress.setCompleted(false);
        
        when(missionsProgressesRepository.findByStudentId(student.getId()))
            .thenReturn(List.of(progress));

        gamificationServices.updateMissionsProgresses(attempt);

        ArgumentCaptor<MissionProgress> captor = ArgumentCaptor.forClass(MissionProgress.class);
        verify(missionsProgressesRepository).save(captor.capture());
        
        MissionProgress savedProgress = captor.getValue();
        
        assertEquals(10, savedProgress.getCurrentCount());
        assertTrue(savedProgress.isCompleted());
        verify(badgeRepository).save(any(Badge.class));
    }
    
    @Test
    void updateMissionsProgresses_ShouldSkip_ChallengeNumberMission() {
        Student student = new Student(); student.setId(1L);
        QuizAttempt attempt = new QuizAttempt(); attempt.setStudent(student);

        ChallengeNumberMission challengeMission = mock(ChallengeNumberMission.class);
        MissionProgress progress = new MissionProgress(challengeMission, student, 5);
        
        when(missionsProgressesRepository.findByStudentId(1L))
            .thenReturn(List.of(progress));

        gamificationServices.updateMissionsProgresses(attempt);

        verify(missionsProgressesRepository, never()).save(any());
    }

    @Test
    void updateMissionsProgresses_Victory_ShouldSkip_NonChallengeMission() {
        long studentId = 1L;
        Student student = new Student(); student.setId(studentId);
        
        Mission normalMission = createMission(1L, "Normal", 10);
        MissionProgress progress = new MissionProgress(normalMission, student, 10);
        
        when(missionsProgressesRepository.findByStudentId(studentId))
            .thenReturn(List.of(progress));
            
        gamificationServices.updateMissionsProgresses(studentId, true);
        
        verify(missionsProgressesRepository, never()).save(any());
    }

    @Test
    void updateMissionsProgresses_Victory_ShouldUpdate_ChallengeMission() {
        long studentId = 1L;
        Student student = new Student(); student.setId(studentId);
        
        ChallengeNumberMission challengeMission = mock(ChallengeNumberMission.class);
         
        MissionProgress progress = new MissionProgress(challengeMission, student, 5);
        progress.setCurrentCount(0);
        
        when(missionsProgressesRepository.findByStudentId(studentId))
            .thenReturn(List.of(progress));
            
        gamificationServices.updateMissionsProgresses(studentId, true);
        
        verify(missionsProgressesRepository).save(progress);
        assertEquals(1, progress.getCurrentCount());
    }
    
    @Test
    void updateMissionsProgresses_Victory_ShouldSkip_CompletedChallengeMission() {
        long studentId = 1L;
        Student student = new Student(); student.setId(studentId);
        
        ChallengeNumberMission challengeMission = mock(ChallengeNumberMission.class);
        MissionProgress progress = new MissionProgress(challengeMission, student, 5);
        progress.setCompleted(true);
        
        when(missionsProgressesRepository.findByStudentId(studentId))
            .thenReturn(List.of(progress));
            
        gamificationServices.updateMissionsProgresses(studentId, true);
        
        verify(missionsProgressesRepository, never()).save(any());
    }

    @Test
    void updateMissionsProgresses_Victory_False_ShouldNotUpdate() {
        long studentId = 1L;
        Student student = new Student(); student.setId(studentId);
        
        ChallengeNumberMission challengeMission = mock(ChallengeNumberMission.class);
        MissionProgress progress = new MissionProgress(challengeMission, student, 5);
        
        when(missionsProgressesRepository.findByStudentId(studentId))
            .thenReturn(List.of(progress));
            
        gamificationServices.updateMissionsProgresses(studentId, false);
        
        verify(missionsProgressesRepository, never()).save(any());
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

        QuizzesNumberMission mission = new QuizzesNumberMission(10); 
        MissionProgress progress = new MissionProgress(mission, student, 10);
        progress.setCompleted(true); 
        
        when(missionsProgressesRepository.findByStudentId(1L))
            .thenReturn(List.of(progress));

        gamificationServices.updateMissionsProgresses(attempt);

        verify(missionsProgressesRepository, never()).save(progress);
    }

    @Test
    void updateMissionsProgressesGoalNotReached() {
        Student student = new Student(); student.setId(1L);
        QuizAttempt attempt = new QuizAttempt(); attempt.setStudent(student);
        attempt.setStatus(QuizAttemptStatus.COMPLETED);

        QuizzesNumberMission mission = new QuizzesNumberMission(10); 
        
        MissionProgress progress = new MissionProgress(mission, student, 10);
        progress.setCurrentCount(2);
        progress.setCompleted(false);
        
        when(missionsProgressesRepository.findByStudentId(1L))
            .thenReturn(List.of(progress));

        gamificationServices.updateMissionsProgresses(attempt);

        ArgumentCaptor<MissionProgress> captor = ArgumentCaptor.forClass(MissionProgress.class);
        verify(missionsProgressesRepository).save(captor.capture());

        MissionProgress saved = captor.getValue();
        assertEquals(3, saved.getCurrentCount());
        assertFalse(saved.isCompleted());
        verify(badgeRepository, never()).save(any());
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
    void getStudentBadges_ShouldMapCorrectly() {
        long studentId = 1L;
        Student student = new Student(); student.setId(studentId);
        Mission mission = createMission(10L, "BadgeMission", 5);
        Badge badge = new Badge(mission, student);
        badge.setId(100L);
        
        when(badgeRepository.findByStudentId(studentId)).thenReturn(List.of(badge));
        
        var results = gamificationServices.getStudentBadges(studentId);
        
        assertEquals(1, results.size());
        assertEquals("BadgeMission", results.get(0).getName());
    }
    
    @Test
    void updateMissionsProgressesSkipCompleted() {
        Student student = new Student(); student.setId(1L);
        QuizAttempt attempt = new QuizAttempt(); attempt.setStudent(student);

        Mission mission = mock(Mission.class);
        lenient().when(mission.getId()).thenReturn(10L); 
        
        MissionProgress progress = new MissionProgress(mission, student, 10);
        progress.setCompleted(true); 
        
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

        lenient().when(missionsRepository.findAll()).thenReturn(Collections.emptyList());
        when(missionsProgressesRepository.findByStudentId(1L)).thenReturn(List.of(progress));

        gamificationServices.updateMissionsProgresses(attempt);

        ArgumentCaptor<MissionProgress> captor = ArgumentCaptor.forClass(MissionProgress.class);
        verify(missionsProgressesRepository).save(captor.capture());
        
        MissionProgress saved = captor.getValue();
        assertEquals(50, saved.getCurrentCount()); 
        assertFalse(saved.isCompleted()); 
    }
}