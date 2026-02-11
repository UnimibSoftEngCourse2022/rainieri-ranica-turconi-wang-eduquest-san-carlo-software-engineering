package it.bicocca.eduquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.bicocca.eduquest.domain.multimedia.MultimediaSupport;

@Repository
public interface MultimediaRepository extends JpaRepository<MultimediaSupport, Long> {
    
}