package com.pauldavdesign.mineauz.minigames.minigame.regions;

public class RegionExecutor {
	private RegionTrigger trigger;
	private RegionAction action;
	
	public RegionExecutor(RegionTrigger trigger, RegionAction action){
		this.trigger = trigger;
		this.action = action;
	}
	
	public RegionTrigger getTrigger(){
		return trigger;
	}
	
	public RegionAction getAction(){
		return action;
	}
}
