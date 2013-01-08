package com.pauldavdesign.mineauz.minigames.scoring;

import java.util.HashMap;
import java.util.Map;

public class ScoreTypes {
	private static Map<String, ScoreType> scoreTypes = new HashMap<String, ScoreType>();
	
	static{
		addScoreType(new PlayerKillsType());
	}
	
	public static void addScoreType(ScoreType type){
		scoreTypes.put(type.getType(), type);
	}
	
	public Map<String, ScoreType> getScoreTypes(){
		return scoreTypes;
	}
}
