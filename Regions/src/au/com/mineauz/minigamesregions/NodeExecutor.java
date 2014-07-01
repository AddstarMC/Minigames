package au.com.mineauz.minigamesregions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.mineauz.minigamesregions.actions.ActionInterface;
import au.com.mineauz.minigamesregions.conditions.ConditionInterface;


public class NodeExecutor {
	
	private NodeTrigger trigger;
	private List<ConditionInterface> conditions = new ArrayList<ConditionInterface>();
	private List<ActionInterface> actions = new ArrayList<ActionInterface>();
	private Map<String, Object> arguments = new HashMap<String, Object>();
	
	public NodeExecutor(NodeTrigger trigger){
		this.trigger = trigger;
	}
	
	public NodeTrigger getTrigger(){
		return trigger;
	}
	
	public List<ConditionInterface> getConditions(){
		return conditions;
	}
	
	public void addCondition(ConditionInterface condition){
		if(condition == null || conditions.contains(condition)) return;
		conditions.add(condition);
		if(condition.getRequiredArguments() != null)
			arguments.putAll(condition.getRequiredArguments());
	}
	
	public void removeCondition(ConditionInterface condition){
		conditions.remove(condition);
		if(condition.getRequiredArguments() != null){
			for(String arg : condition.getRequiredArguments().keySet()){
				arguments.remove(arg);
			}
		}
	}
	
	public List<ActionInterface> getActions(){
		return actions;
	}
	
	public void addAction(ActionInterface action){
		actions.add(action);
		if(action.getRequiredArguments() != null)
			arguments.putAll(action.getRequiredArguments());
	}
	
	public void removeAction(ActionInterface action){
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
