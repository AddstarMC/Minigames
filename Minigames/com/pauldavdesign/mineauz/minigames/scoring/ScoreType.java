package com.pauldavdesign.mineauz.minigames.scoring;

import java.util.HashMap;
import java.util.Map;

public class ScoreType {
	private static Map<String, ScoreTypeBase> scoreTypes = new HashMap<String, ScoreTypeBase>();
	
	static{
		addScoreType(new PlayerKillsType());
		addScoreType(new CTFType());
		addScoreType(new InfectionType());
		addScoreType(new CustomType());
	}
	
	public static void addScoreType(ScoreTypeBase type){
		scoreTypes.put(type.getType(), type);
	}
	
	public static ScoreTypeBase getScoreType(String type){
		if(scoreTypes.containsKey(type)){
			return scoreTypes.get(type);
		}
		return null;
	}
	
	public Map<String, ScoreTypeBase> getScoreTypes(){
		return scoreTypes;
	}
}
