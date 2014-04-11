package com.pauldavdesign.mineauz.minigames.minigame.regions.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RegionActions {
	private static Map<String, RegionActionInterface> actions = new HashMap<String, RegionActionInterface>();
	
	static{
		addAction(new KillAction());
		addAction(new RevertAction());
		addAction(new QuitAction());
		addAction(new EndAction());
		addAction(new MessageAction());
	}
	
	public static void addAction(RegionActionInterface action){
		actions.put(action.getName().toUpperCase(), action);
	}
	
	public static RegionActionInterface getActionByName(String name){
		if(actions.containsKey(name.toUpperCase()))
			return actions.get(name.toUpperCase());
		return null;
	}
	
	public static Collection<RegionActionInterface> getAllActions(){
		return actions.values();
	}
	
	public static Set<String> getAllActionNames(){
		return actions.keySet();
	}
	
	public static boolean hasAction(String name){
		return actions.containsKey(name.toUpperCase());
	}
}
