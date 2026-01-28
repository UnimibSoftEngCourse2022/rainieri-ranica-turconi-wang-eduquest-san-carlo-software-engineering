package it.bicocca.eduquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.bicocca.eduquest.domain.quiz.Quiz;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long>{
	List<Quiz> findByAuthorId(long authorId);
}