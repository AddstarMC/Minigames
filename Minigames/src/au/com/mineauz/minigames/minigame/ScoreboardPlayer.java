package au.com.mineauz.minigames.minigame;

import au.com.mineauz.minigames.stats.MinigameStats;
import au.com.mineauz.minigames.stats.StoredStats;

public class ScoreboardPlayer {
	private String playerName;
	private String uuid;
	private int completions;
	private int failures;
	private int bestKills;
	private int bestScore;
	private int leastDeaths;
	private long bestTime;
	private int leastReverts;
	private int totalKills;
	private int totalDeaths;
	private int totalScore;
	private int totalReverts;
	private long totalTime;
	
	public ScoreboardPlayer(StoredStats saveData) {
		this(saveData.getPlayer().getName(),
				saveData.getPlayer().getUUID().toString(), 
				(int)saveData.getStat(MinigameStats.Wins),
				(int)(saveData.getStat(MinigameStats.Attempts) - saveData.getStat(MinigameStats.Wins)),
				(int)saveData.getStat(MinigameStats.Kills),
				(int)saveData.getStat(MinigameStats.Deaths),
				(int)saveData.getStat(MinigameStats.Score),
				saveData.getStat(MinigameStats.CompletionTime),
				(int)saveData.getStat(MinigameStats.Reverts),
				(int)saveData.getStat(MinigameStats.Kills),
				(int)saveData.getStat(MinigameStats.Deaths),
				(int)saveData.getStat(MinigameStats.Score),
				(int)saveData.getStat(MinigameStats.Reverts),
				saveData.getStat(MinigameStats.CompletionTime)
				);
	}
	public ScoreboardPlayer(String playerName, String uuid, int completions, int failures, int bestKills, 
			int leastDeaths, int bestScore, long bestTime, int leastReverts, int totalKills, int totalDeaths, int totalScore, int totalReverts, long totalTime){
		this.playerName = playerName;
		this.uuid = uuid;
		this.completions = completions;
		this.failures = failures;
		this.bestKills = bestKills;
		this.leastDeaths = leastDeaths;
		this.bestScore = bestScore;
		this.bestTime = bestTime;
		this.leastReverts = leastReverts;
		this.totalKills = totalKills;
		this.totalDeaths = totalDeaths;
		this.totalScore = totalScore;
		this.totalReverts = totalReverts;
		this.totalTime = totalTime;
	}

	public int getCompletions() {
		return completions;
	}

	public void setCompletions(int completions) {
		this.completions = completions;
	}

	public int getFailures() {
		return failures;
	}

	public void setFailures(int failures) {
		this.failures = failures;
	}

	public int getBestKills() {
		return bestKills;
	}

	public void setBestKills(int bestKills) {
		this.bestKills = bestKills;
	}
	
	public int getBestScore() {
		return bestScore;
	}
	
	public void setBestScore(int bestScore) {
		this.bestScore = bestScore;
	}

	public int getLeastDeaths() {
		return leastDeaths;
	}

	public void setLeastDeaths(int leastDeaths) {
		this.leastDeaths = leastDeaths;
	}

	public long getBestTime() {
		return bestTime;
	}

	public void setBestTime(long bestTime) {
		this.bestTime = bestTime;
	}

	public int getLeastReverts() {
		return leastReverts;
	}

	public void setLeastReverts(int leastReverts) {
		this.leastReverts = leastReverts;
	}

	public int getTotalKills() {
		return totalKills;
	}

	public void setTotalKills(int totalKills) {
		this.totalKills = totalKills;
	}

	public int getTotalDeaths() {
		return totalDeaths;
	}

	public void setTotalDeaths(int totalDeaths) {
		this.totalDeaths = totalDeaths;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public int getTotalReverts() {
		return totalReverts;
	}

	public void setTotalReverts(int totalReverts) {
		this.totalReverts = totalReverts;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public String getPlayerName() {
		return playerName;
	}
	
	public String getUUID(){
		return uuid;
	}
	
	public void update(StoredStats data) {
		completions += (int)data.getStat(MinigameStats.Wins);
		failures += (int)(data.getStat(MinigameStats.Attempts) - data.getStat(MinigameStats.Wins));
		bestKills = Math.max(bestKills, (int)data.getStat(MinigameStats.Kills));
		leastDeaths = Math.min(leastDeaths, (int)data.getStat(MinigameStats.Deaths));
		bestScore = Math.max(bestScore, (int)data.getStat(MinigameStats.Score));
		bestTime = Math.min(bestTime, data.getStat(MinigameStats.CompletionTime));
		leastReverts = Math.min(leastReverts, (int)data.getStat(MinigameStats.Reverts));
		totalKills += (int)data.getStat(MinigameStats.Kills);
		totalDeaths += (int)data.getStat(MinigameStats.Deaths);
		totalScore += (int)data.getStat(MinigameStats.Score);
		totalReverts += (int)data.getStat(MinigameStats.Reverts);
		totalTime += data.getStat(MinigameStats.CompletionTime);
	}
	
	public Object getByType(ScoreboardType type){
		Object obj = null;
		switch(type){
		case BEST_KILLS : obj = getBestKills();
		break;
		case BEST_SCORE : obj = getBestScore();
		break;
		case COMPLETIONS : obj = getCompletions();
		break;
		case FAILURES : obj = getFailures();
		break;
		case LEAST_DEATHS : obj = getLeastDeaths();
		break;
		case LEAST_REVERTS : obj = getLeastReverts();
		break;
		case LEAST_TIME : obj = getBestTime();
		break;
		case TOTAL_DEATHS : obj = getTotalDeaths();
		break;
		case TOTAL_KILLS : obj = getTotalKills();
		break;
		case TOTAL_REVERTS : obj = getTotalReverts();
		break;
		case TOTAL_SCORE : obj = getTotalScore();
		break;
		case TOTAL_TIME : obj = getTotalTime();
		break;
		}
		return obj;
	}
}
