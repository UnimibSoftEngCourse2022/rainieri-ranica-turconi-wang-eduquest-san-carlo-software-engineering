package it.bicocca.eduquest.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import it.bicocca.eduquest.dto.gamification.*;
import it.bicocca.eduquest.services.ranking.*;

enum RankingType {
	QUIZZES_NUMBER,
	AVERAGE_SCORE,
	CORRECT_ANSWERS
}

@Service
public class RankingServices {
	private Map<String, RankingStrategy> rankingStrategies;
	
	public RankingServices(List<RankingStrategy> strategiesList) {
		this.rankingStrategies = new HashMap<>();
		for (RankingStrategy strategy : strategiesList) {
			rankingStrategies.put(strategy.getRankingType(), strategy);
		}
	}
	
	public List<StudentInfoForRankingDTO> getRanking(String rankingType) throws StrategyNotFoundException {
		if (!rankingStrategies.containsKey(rankingType)) {
			throw new StrategyNotFoundException("Cannot find the ranking strategy '" + rankingType + "'");
		}
		return rankingStrategies.get(rankingType).getRanking();
	}
}
