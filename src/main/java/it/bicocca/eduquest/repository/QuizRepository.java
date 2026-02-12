package it.bicocca.eduquest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.bicocca.eduquest.domain.quiz.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long>{
	List<Quiz> findByAuthorId(long authorId);
	List<Quiz> findByIsPublicTrue();
}