package it.bicocca.eduquest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.domain.users.StudentStats;
import it.bicocca.eduquest.dto.gamification.StudentInfoForRankingDTO;
import it.bicocca.eduquest.repository.StudentsRepository;
import it.bicocca.eduquest.services.ranking.*;

@ExtendWith(MockitoExtension.class)
class RankingServicesTest {

    @Mock
    private StudentsRepository studentsRepository;

    private RankingServices rankingServices;

    private List<Student> mockStudents;

    private CompletedQuizzesStrategy quizzesStrategy;
    private AverageQuizzesScoreStrategy averageStrategy;
    private CorrectAnswersStrategy answersStrategy;
    
    @BeforeEach
    void setUp() {
    	quizzesStrategy = new CompletedQuizzesStrategy(studentsRepository);
    	averageStrategy = new AverageQuizzesScoreStrategy(studentsRepository);
    	answersStrategy = new CorrectAnswersStrategy(studentsRepository);
        
        mockStudents = new ArrayList<>();
        mockStudents.add(createStudent(1L, "Mario", "Rossi", 10, 8.5, 5));
        mockStudents.add(createStudent(2L, "Luigi", "Verdi", 5, 6.0, 4));
        mockStudents.add(createStudent(3L, "Peach", "Toadstool", 20, 9.5, 3));
        
        List<RankingStrategy> strategies = List.of(quizzesStrategy, averageStrategy, answersStrategy);
        rankingServices = new RankingServices(strategies);
    }

    @Test
    void getRankingByNumberOfQuizzesCompletedReturnMappedDTOs() throws StrategyNotFoundException {
        when(studentsRepository.getRankingByCompletedQuizzes()).thenReturn(mockStudents);

        List<StudentInfoForRankingDTO> result = rankingServices.getRanking("completedQuizzes");

        assertEquals(3, result.size());
        
        StudentInfoForRankingDTO dto1 = result.get(0);
        assertEquals("Mario", dto1.getName());
        assertEquals(10.0, dto1.getValue());
        
        verify(studentsRepository).getRankingByCompletedQuizzes();
    }

    @Test
    void getRankingByAverageQuizzesScoreReturnMappedDTOs() throws StrategyNotFoundException {
        when(studentsRepository.getRankingByAverageScore()).thenReturn(mockStudents);

        List<StudentInfoForRankingDTO> result = rankingServices.getRanking("quizzesScore");

        assertEquals(3, result.size());

        StudentInfoForRankingDTO dto1 = result.get(0);
        assertEquals("Mario", dto1.getName());
        assertEquals(8.5, dto1.getValue()); 

        verify(studentsRepository).getRankingByAverageScore();
    }
    
    @Test
    void getRankingByCorrectAnswersReturnMappedDTOs() throws StrategyNotFoundException {
        when(studentsRepository.getRankingByCorrectAnswers()).thenReturn(mockStudents);

        List<StudentInfoForRankingDTO> result = rankingServices.getRanking("correctAnswers");

        assertEquals(3, result.size());

        StudentInfoForRankingDTO dto1 = result.get(0);
        assertEquals("Mario", dto1.getName());
        assertEquals(5, dto1.getValue()); 

        verify(studentsRepository).getRankingByCorrectAnswers();
    }

    @Test
    void buildRankingDTOLimitTo10Entries() throws StrategyNotFoundException {
        List<Student> manyStudents = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            manyStudents.add(createStudent((long)i, "S" + i, "Cognome", 5, 5.0, 5));
        }

        when(studentsRepository.getRankingByCompletedQuizzes()).thenReturn(manyStudents);

        List<StudentInfoForRankingDTO> result = rankingServices.getRanking("completedQuizzes");

        assertEquals(10, result.size(), "La classifica deve essere limitata a 10 elementi");
        assertEquals("S0", result.get(0).getName()); 
        assertEquals("S9", result.get(9).getName()); 
    }

    @Test
    void getRankingHandleEmptyList() throws StrategyNotFoundException {
        when(studentsRepository.getRankingByCompletedQuizzes()).thenReturn(Collections.emptyList());

        List<StudentInfoForRankingDTO> result = rankingServices.getRanking("completedQuizzes");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private Student createStudent(Long id, String name, String surname, int quizzes, double avgScore, int correctAnswers) {
        Student s = new Student();
        s.setId(id);
        s.setName(name);
        s.setSurname(surname);
        
        StudentStats stats = new StudentStats();
        stats.setQuizzesCompleted(quizzes);
        stats.setAverageQuizzesScore(avgScore);
        stats.setTotalCorrectAnswers(correctAnswers);
        s.setStats(stats);
        
        return s;
    }
}