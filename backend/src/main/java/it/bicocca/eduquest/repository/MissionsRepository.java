package it.bicocca.eduquest.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import it.bicocca.eduquest.domain.gamification.Mission;

public interface MissionsRepository extends JpaRepository<Mission, Long> {
	
}
