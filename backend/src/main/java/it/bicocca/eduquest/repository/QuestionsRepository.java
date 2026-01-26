package it.bicocca.eduquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.bicocca.eduquest.domain.quiz.Question;
import java.util.List;

public interface QuestionsRepository extends JpaRepository<Question, Long>{
	
}
