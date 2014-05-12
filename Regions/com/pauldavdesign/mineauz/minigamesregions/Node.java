package com.pauldavdesign.mineauz.minigamesregions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.event.Event;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigamesregions.actions.ActionInterface;
import com.pauldavdesign.mineauz.minigamesregions.conditions.ConditionInterface;

public class Node {
	
	private String name;
	private Location loc;
	private List<NodeExecutor> executors = new ArrayList<NodeExecutor>();
	
	public Node(String name, Location loc){
		this.name = name;
		this.loc = loc;
	}
	
	public String getName(){
		return name;
	}
	
	public Location getLocation(){
		return loc.clone();
	}
	
	public int addExecutor(NodeTrigger trigger){
		executors.add(new NodeExecutor(trigger));
		return executors.size();
	}
	
	public int addExecutor(NodeExecutor exec){
		executors.add(exec);
		return executors.size();
	}
	
	public List<NodeExecutor> getExecutors(){
		return executors;
	}
	
	public void removeExecutor(int id){
		if(executors.size() <= id){
			executors.remove(id - 1);
		}
	}
	
	public void removeExecutor(NodeExecutor executor){
		if(executors.contains(executor)){
			executors.remove(executor);
		}
	}
	
	public void execute(NodeTrigger trigger, MinigamePlayer player, Event event){
		List<NodeExecutor> toExecute = new ArrayList<NodeExecutor>();
		for(NodeExecutor exec : executors){
			if(exec.getTrigger() == trigger){
				boolean cont = true;
				for(ConditionInterface con : exec.getConditions()){
					if(!con.checkNodeCondition(player, exec.getArguments(), this)){
						cont = false;
						break;
					}
				}
				if(cont)
					toExecute.add(exec);
			}
		}
		for(NodeExecutor exec : toExecute)
			for(ActionInterface act : exec.getActions())
				act.executeNodeAction(player, exec.getArguments(), this, event);
	}
}
