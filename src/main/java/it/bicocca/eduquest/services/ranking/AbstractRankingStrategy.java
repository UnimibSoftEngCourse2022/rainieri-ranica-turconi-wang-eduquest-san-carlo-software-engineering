package it.bicocca.eduquest.services.ranking;

import java.util.ArrayList;
import java.util.List;

import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.dto.gamification.StudentInfoForRankingDTO;
import it.bicocca.eduquest.repository.StudentsRepository;

public abstract class AbstractRankingStrategy implements RankingStrategy {
	protected StudentsRepository studentsRepository;
	protected static final int RANKING_DIMENSION = 10;
	
	protected AbstractRankingStrategy(StudentsRepository studentsRepository) {
		this.studentsRepository = studentsRepository;
	}
	
	public List<StudentInfoForRankingDTO> getRanking() {
		List<Student> students = getSortedStudents();
		
		List<StudentInfoForRankingDTO> ranking = new ArrayList<>();
		int currentPosition = 0;
		for (Student student : students) {
			if (currentPosition >= RANKING_DIMENSION) {
				break;
			}
			
			double value = extractValue(student);

			StudentInfoForRankingDTO userInfo = new StudentInfoForRankingDTO(student.getId(), student.getName(), student.getSurname(), value);
			ranking.add(userInfo);
			currentPosition++;
		}
		return ranking;
	}
	
	protected abstract List<Student> getSortedStudents();
	protected abstract double extractValue(Student student);
}
