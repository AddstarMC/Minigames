package com.pauldavdesign.mineauz.minigames.minigame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardData {
	public Map<String, ScoreboardPlayer> scoreboards = new HashMap<String, ScoreboardPlayer>();
	
	public void addPlayer(ScoreboardPlayer player){
		scoreboards.put(player.getPlayerName(), player);
	}
	
	public ScoreboardPlayer getPlayer(String name){
		return scoreboards.get(name);
	}
	
	public boolean hasPlayer(String name){
		return scoreboards.containsKey(name);
	}
	
	public List<ScoreboardPlayer> getPlayers(){
		return new ArrayList<ScoreboardPlayer>(scoreboards.values());
	}
}
