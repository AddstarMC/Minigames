package com.pauldavdesign.mineauz.minigames.minigame.regions;

public enum RegionTrigger {
	
	ENTER("Enter"),
	LEAVE("Leave"),
	TIMER_ENTER("Timer_Enter"),
	TIMER_LEAVE("Timer_Leave");
	
	private String name;
	
	private RegionTrigger(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public static RegionTrigger getByName(String name){
		for(RegionTrigger t : RegionTrigger.values()){
			if(name.equalsIgnoreCase(t.getName())){
				return t;
			}
		}
		return null;
	}
}
