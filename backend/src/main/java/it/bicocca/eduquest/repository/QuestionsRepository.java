package it.bicocca.eduquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.bicocca.eduquest.domain.quiz.Question;

@Repository
public interface QuestionsRepository extends JpaRepository<Question, Long>{
	
}
