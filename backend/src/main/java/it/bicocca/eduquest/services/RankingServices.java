package it.bicocca.eduquest.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import it.bicocca.eduquest.repository.StudentsRepository;
import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.dto.gamification.*;

enum RankingType {
	QUIZZES_NUMBER,
	AVERAGE_SCORE,
	CORRECT_ANSWERS
}

@Service
public class RankingServices {
	private StudentsRepository studentsRepository;
	private static final int RANKING_DIMENSION = 10;
	
	public RankingServices(StudentsRepository studentsRepository) {
		this.studentsRepository = studentsRepository;
	}
	
	public List<StudentInfoForRankingDTO> getRankingByNumberOfQuizzesCompleted() {
		List<Student> sortedStudents = studentsRepository.getRankingByCompletedQuizzes();
		return buildRankingDTO(sortedStudents, RankingType.QUIZZES_NUMBER);
	}
	
	public List<StudentInfoForRankingDTO> getRankingByAverageQuizzesScore() {
		List<Student> sortedStudents = studentsRepository.getRankingByAverageScore();
		return buildRankingDTO(sortedStudents, RankingType.AVERAGE_SCORE);
	}

	public List<StudentInfoForRankingDTO> getRankingByCorrectAnswers() {
		List<Student> sortedStudents = studentsRepository.getRankingByCorrectAnswers();
		return buildRankingDTO(sortedStudents, RankingType.CORRECT_ANSWERS);
	}
	
	public List<StudentInfoForRankingDTO> buildRankingDTO(List<Student> students, RankingType rankingType) {
		List<StudentInfoForRankingDTO> ranking = new ArrayList<>();
		int currentPosition = 0;
		for (Student student : students) {
			if (currentPosition >= RANKING_DIMENSION) {
				break;
			}
			
			double value = 0;
			if (rankingType == RankingType.QUIZZES_NUMBER) {
				value = student.getStats().getQuizzesCompleted();
			} else if (rankingType == RankingType.AVERAGE_SCORE) {
				value = student.getStats().getAverageQuizzesScore();
			} else if (rankingType == RankingType.CORRECT_ANSWERS) {
				value = student.getStats().getTotalCorrectAnswers();
			} else {
				throw new RuntimeException("Sorting type " + rankingType + " is not supported");
			}

			StudentInfoForRankingDTO userInfo = new StudentInfoForRankingDTO(student.getId(), student.getName(), student.getSurname(), value);
			ranking.add(userInfo);
			currentPosition++;
		}
		return ranking;
	}
}
