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
	private List<RegionActionInterface> actions = new ArrayList<RegionActionInterface>();
	private Map<String, Object> arguments = new HashMap<String, Object>();
	
	public RegionExecutor(RegionTrigger trigger){
		this.trigger = trigger;
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
	
	public List<RegionActionInterface> getActions(){
		return actions;
	}
	
	public void addAction(RegionActionInterface action){
		actions.add(action);
		if(action.getRequiredArguments() != null)
			arguments.putAll(action.getRequiredArguments());
	}
	
	public void removeAction(RegionActionInterface action){
		actions.remove(action);
		if(action.getRequiredArguments() != null)
			for(String key : action.getRequiredArguments().keySet())
				arguments.remove(key);
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
