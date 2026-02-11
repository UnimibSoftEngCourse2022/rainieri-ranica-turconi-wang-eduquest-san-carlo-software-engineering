package it.bicocca.eduquest.services.ranking;

import java.util.List;

import it.bicocca.eduquest.dto.gamification.*;

public interface RankingStrategy {
	public List<StudentInfoForRankingDTO> getRanking();
	String getRankingType();
}
