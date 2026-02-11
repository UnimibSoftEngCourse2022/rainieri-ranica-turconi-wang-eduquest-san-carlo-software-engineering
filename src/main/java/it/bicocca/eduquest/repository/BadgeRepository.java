package it.bicocca.eduquest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import it.bicocca.eduquest.domain.gamification.Badge;
import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    List<Badge> findByStudentId(Long studentId);
}