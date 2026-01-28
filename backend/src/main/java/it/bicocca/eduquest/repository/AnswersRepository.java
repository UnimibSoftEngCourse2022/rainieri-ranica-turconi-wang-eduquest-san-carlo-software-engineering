package it.bicocca.eduquest.repository;

import it.bicocca.eduquest.domain.answers.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswersRepository extends JpaRepository<Answer, Long> {

}
