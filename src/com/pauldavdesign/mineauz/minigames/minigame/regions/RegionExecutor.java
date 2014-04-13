package com.pauldavdesign.mineauz.minigames.minigame.regions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pauldavdesign.mineauz.minigames.minigame.regions.actions.RegionActionInterface;
import com.pauldavdesign.mineauz.minigames.minigame.regions.conditions.RegionConditionInterface;

public class RegionExecutor {
	private RegionTrigger trigger;
	private List<RegionConditionInterface> conditions = new ArrayList<RegionConditionInterface>();
	private RegionActionInterface action;
	private Map<String, Object> arguments = new HashMap<String, Object>();
	
	public RegionExecutor(RegionTrigger trigger, RegionActionInterface action){
		this.trigger = trigger;
		this.action = action;
		if(action.getRequiredArguments() != null)
			arguments.putAll(action.getRequiredArguments());
	}
	
	public RegionExecutor(RegionTrigger trigger, RegionConditionInterface condition, RegionActionInterface action){
		this.trigger = trigger;
		this.action = action;
		conditions.add(condition);
		if(action.getRequiredArguments() != null)
			arguments.putAll(action.getRequiredArguments());
		if(condition.getRequiredArguments() != null)
			arguments.putAll(condition.getRequiredArguments());
	}
	
	public RegionTrigger getTrigger(){
		return trigger;
	}
	
	public List<RegionConditionInterface> getConditions(){
		return conditions;
	}
	
	public void addCondition(RegionConditionInterface condition){
		if(condition == null || conditions.contains(condition)) return;
		conditions.add(condition);
		if(condition.getRequiredArguments() != null)
			arguments.putAll(condition.getRequiredArguments());
	}
	
	public void removeCondition(RegionConditionInterface condition){
		conditions.remove(condition);
		if(condition.getRequiredArguments() != null){
			for(String arg : condition.getRequiredArguments().keySet()){
				arguments.remove(arg);
			}
		}
	}
	
	public RegionActionInterface getAction(){
		return action;
	}
	
	public Map<String, Object> getArguments(){
		return arguments;
	}
	
	public void addArguments(Map<String, Object> args){
		if(args == null) return;
		for(String arg : args.keySet())
			arguments.remove(arg);
		arguments.putAll(args);
	}
}
