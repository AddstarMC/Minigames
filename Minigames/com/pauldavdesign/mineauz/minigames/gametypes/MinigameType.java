package com.pauldavdesign.mineauz.minigames.gametypes;

public enum MinigameType {
	SINGLEPLAYER("Singleplayer"),
	MULTIPLAYER("Multiplayer"),
	TREASURE_HUNT("Treasure Hunt");
	
	private String name;
	
	private MinigameType(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public static boolean hasValue(String value){
		for(MinigameType type : values()){
			if(type.toString().equalsIgnoreCase(value))
				return true;
		}
		return false;
	}
}
