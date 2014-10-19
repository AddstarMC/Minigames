package au.com.mineauz.minigames.sql;

public class SQLPlayer {
	private final String playerName;
	private final String uuid;
	private final int completionChange;
	private final int failureChange;
	private final int kills;
	private final int deaths;
	private final int score;
	private final int reverts;
	private final long time;
	private final String minigame;
	
	public SQLPlayer(String minigame, String name, String uuid, int completionChange, int failureChange, 
			int kills, int deaths, int score, int reverts, long time){
		this.minigame = minigame;
		this.uuid = uuid;
		playerName = name;
		this.completionChange = completionChange;
		this.failureChange = failureChange;
		this.kills = kills;
		this.deaths = deaths;
		this.reverts = reverts;
		this.score = score;
		this.time = time;
	}
	
	public String getMinigame() {
		return minigame;
	}

	public String getPlayerName() {
		return playerName;
	}
	
	public String getUUID(){
		return uuid;
	}

	public int getCompletionChange() {
		return completionChange;
	}


	public int getFailureChange() {
		return failureChange;
	}


	public int getKills() {
		return kills;
	}


	public int getDeaths() {
		return deaths;
	}


	public int getScore() {
		return score;
	}


	public int getReverts() {
		return reverts;
	}


	public long getTime() {
		return time;
	}
}
