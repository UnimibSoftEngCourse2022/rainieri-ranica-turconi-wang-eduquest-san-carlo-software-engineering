package it.bicocca.eduquest.services.ranking;

import java.util.List;

import org.springframework.stereotype.Component;

import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.repository.StudentsRepository;

@Component
public class AverageQuizzesScoreStrategy extends AbstractRankingStrategy {
	public AverageQuizzesScoreStrategy(StudentsRepository studentsRepository) {
		super(studentsRepository);
	}
	
	public List<Student> getSortedStudents() {
		return studentsRepository.getRankingByAverageScore();
	}
	
	public double extractValue(Student student) {
		return student.getStats().getAverageQuizzesScore();
	}
	
	public String getRankingType() {
		return "quizzesScore";
	}
}
