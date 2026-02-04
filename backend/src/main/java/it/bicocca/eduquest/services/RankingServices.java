package it.bicocca.eduquest.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import it.bicocca.eduquest.repository.StudentsRepository;
import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.dto.gamification.*;

@Service
public class RankingServices {
	private StudentsRepository studentsRepository;
	private final int RANKING_DIMENSION = 10;
	
	public RankingServices(StudentsRepository studentsRepository) {
		this.studentsRepository = studentsRepository;
	}
	
	public List<StudentInfoForRankingDTO> getRankingByNumberOfQuizzesCompleted() {
		List<StudentInfoForRankingDTO> ranking = new ArrayList<StudentInfoForRankingDTO>();
		List<Student> sortedStudents = studentsRepository.getRanking();
		int currentPosition = 0;
		for (Student student : sortedStudents) {
			if (currentPosition >= RANKING_DIMENSION) {
				break;
			}
			StudentInfoForRankingDTO userInfo = new StudentInfoForRankingDTO(student.getId(), student.getName(), student.getSurname(), student.getStats().getQuizzesCompleted());
			ranking.add(userInfo);
			currentPosition++;
		}
		return ranking;
	}
}
