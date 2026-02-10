package it.bicocca.eduquest.services.ranking;

import java.util.List;

import org.springframework.stereotype.Component;

import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.repository.StudentsRepository;

@Component
public class CorrectAnswersStrategy extends AbstractRankingStrategy {
	public CorrectAnswersStrategy(StudentsRepository studentsRepository) {
		super(studentsRepository);
	}
	
	public List<Student> getSortedStudents() {
		return studentsRepository.getRankingByCorrectAnswers();
	}
	
	public double extractValue(Student student) {
		return student.getStats().getTotalCorrectAnswers();
	}
	
	public String getRankingType() {
		return "correctAnswers";
	}
}
