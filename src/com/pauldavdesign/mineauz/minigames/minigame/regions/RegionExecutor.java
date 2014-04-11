package com.pauldavdesign.mineauz.minigames.minigame.regions;

import java.util.HashMap;
import java.util.Map;

import com.pauldavdesign.mineauz.minigames.minigame.regions.actions.RegionActionInterface;

public class RegionExecutor {
	private RegionTrigger trigger;
	private RegionActionInterface action;
	private Map<String, Object> arguments = new HashMap<String, Object>();
	
	public RegionExecutor(RegionTrigger trigger, RegionActionInterface action){
		this.trigger = trigger;
		this.action = action;
		if(action.getRequiredArguments() != null){
			arguments = action.getRequiredArguments();
		}
	}
	
	public RegionTrigger getTrigger(){
		return trigger;
	}
	
	public RegionActionInterface getAction(){
		return action;
	}
	
	public Map<String, Object> getArguments(){
		return arguments;
	}
	
	public void setArguments(Map<String, Object> args){
		arguments.putAll(args);
	}
}
