package au.com.mineauz.minigames.minigame;

/**
 * Deprecated since 1.10 see commit log
 */
@Deprecated  //todo remove in 1.13
public enum ScoreboardType {
	COMPLETIONS("Wins"),
	BEST_KILLS("Kills"),
	LEAST_DEATHS("Deaths"),
	BEST_SCORE("Points"),
	LEAST_TIME(""),
	LEAST_REVERTS("Reverts"),
	TOTAL_KILLS("Kills"),
	TOTAL_DEATHS("Deaths"),
	TOTAL_SCORE("Points"),
	TOTAL_REVERTS("Reverts"),
	TOTAL_TIME(""),
	FAILURES("Losses");
	
	private String typeName;
	
	ScoreboardType(String type){
		typeName = type;
	}
	
	public String getTypeName(){
		return typeName;
	}
}
