package it.bicocca.eduquest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.bicocca.eduquest.domain.users.Student;

@Repository
public interface StudentsRepository extends JpaRepository<Student, Long> {
	@Query("SELECT s FROM Student s ORDER BY s.stats.quizzesCompleted DESC")
    List<Student> getRankingByCompletedQuizzes();
	
	@Query("SELECT s FROM Student s ORDER BY s.stats.averageQuizzesScore DESC")
    List<Student> getRankingByAverageScore();
}