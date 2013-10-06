package com.pauldavdesign.mineauz.minigames.scoring;

import java.util.HashMap;
import java.util.Map;

public class ScoreTypes {
	private static Map<String, ScoreType> scoreTypes = new HashMap<String, ScoreType>();
	
	static{
		addScoreType(new PlayerKillsType());
		addScoreType(new CTFType());
		addScoreType(new InfectionType());
		addScoreType(new CustomType());
	}
	
	public static void addScoreType(ScoreType type){
		scoreTypes.put(type.getType(), type);
	}
	
	public static ScoreType getScoreType(String type){
		if(scoreTypes.containsKey(type)){
			return scoreTypes.get(type);
		}
		return null;
	}
	
	public Map<String, ScoreType> getScoreTypes(){
		return scoreTypes;
	}
}
