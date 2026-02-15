package it.bicocca.eduquest.services.ranking;

import java.util.List;

import org.springframework.stereotype.Component;

import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.repository.StudentsRepository;

@Component
public class CompletedQuizzesStrategy extends AbstractRankingStrategy {
	public CompletedQuizzesStrategy(StudentsRepository studentsRepository) {
		super(studentsRepository);
	}
	
	protected List<Student> getSortedStudents() {
		return studentsRepository.getRankingByCompletedQuizzes();
	}
	
	protected double extractValue(Student student) {
		return student.getStats().getQuizzesCompleted();
	}
	
	public String getRankingType() {
		return "completedQuizzes";
	}
}
