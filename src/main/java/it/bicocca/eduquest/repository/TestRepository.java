package it.bicocca.eduquest.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import it.bicocca.eduquest.domain.quiz.Test;

public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByQuizId(Long quizId);
}