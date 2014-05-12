package com.pauldavdesign.mineauz.minigames.minigame.reward;

public enum RewardRarity {
	VERY_COMMON(0.5),
	COMMON(0.25),
	NORMAL(0.1),
	RARE(0.02),
	VERY_RARE(0);
	
	private double rarity;
	
	private RewardRarity(double r){
		rarity = r;
	}
	
	public double getRarity(){
		return rarity;
	}
	
	public RewardRarity getPreviousRarity(){
		if(this == VERY_COMMON)
			return COMMON;
		else if(this == COMMON)
			return NORMAL;
		else if(this == NORMAL)
			return RARE;
		
		return VERY_RARE;
	}
	
	public RewardRarity getNextRarity(){
		if(this == VERY_RARE)
			return RARE;
		else if(this == RARE)
			return NORMAL;
		else if(this == NORMAL)
			return COMMON;
		
		return VERY_COMMON;
	}
}
