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
	
	public RankingServices(StudentsRepository studentsRepository) {
		this.studentsRepository = studentsRepository;
	}
	
	public List<StudentInfoForRankingDTO> getRankingByNumberOfQuizzesCompleted() {
		List<StudentInfoForRankingDTO> ranking = new ArrayList<StudentInfoForRankingDTO>();
		List<Student> sortedStudents = studentsRepository.getRanking();
		for (Student student : sortedStudents) {
			StudentInfoForRankingDTO userInfo = new StudentInfoForRankingDTO(student.getId(), student.getName(), student.getSurname(), student.getStats().getQuizzesCompleted());
			ranking.add(userInfo);
		}
		return ranking;
	}
}
