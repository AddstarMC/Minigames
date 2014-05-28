package com.pauldavdesign.mineauz.minigamesregions.conditions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigamesregions.NodeExecutor;
import com.pauldavdesign.mineauz.minigamesregions.RegionExecutor;
import com.pauldavdesign.mineauz.minigamesregions.menuitems.MenuItemCondition;
import com.pauldavdesign.mineauz.minigamesregions.menuitems.MenuItemConditionAdd;

public class Conditions {
	private static Map<String, ConditionInterface> conditions = new HashMap<String, ConditionInterface>();
	
	static{
		addCondition(new PlayerHealthRangeCondition());
		addCondition(new HasRequiredFlagsCondition());
		addCondition(new PlayerScoreRangeCondition());
		addCondition(new MatchTeamCondition());
		addCondition(new ContainsOneTeamCondition());
		addCondition(new RandomChanceCondition());
		addCondition(new MatchBlockCondition());
		addCondition(new ContainsEntireTeamCondition());
		addCondition(new PlayerCountCondition());
		addCondition(new PlayerHasItemCondition());
	}
	
	public static void addCondition(ConditionInterface condition){
		conditions.put(condition.getName().toUpperCase(), condition);
	}
	
	public static boolean hasCondition(String condition){
		return conditions.containsKey(condition.toUpperCase());
	}
	
	public static ConditionInterface getConditionByName(String name){
		if(hasCondition(name.toUpperCase()))
			return conditions.get(name.toUpperCase());
		return null;
	}
	
	public static Set<String> getAllConditionNames(){
		return conditions.keySet();
	}
	
	public static Collection<ConditionInterface> getAllConditions(){
		return conditions.values();
	}
	
	public static void displayMenu(MinigamePlayer player, RegionExecutor exec, Menu prev){
		Menu m = new Menu(3, "Conditions", player);
		m.setPreviousPage(prev);
		for(ConditionInterface con : exec.getConditions()){
			m.addItem(new MenuItemCondition(MinigameUtils.capitalize(con.getName()), Material.PAPER, exec, con));
		}
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		m.addItem(new MenuItemConditionAdd("Add Condition", Material.ITEM_FRAME, exec), m.getSize() - 1);
		m.displayMenu(player);
	}
	
	public static void displayMenu(MinigamePlayer player, NodeExecutor exec, Menu prev){
		Menu m = new Menu(3, "Conditions", player);
		m.setPreviousPage(prev);
		for(ConditionInterface con : exec.getConditions()){
			m.addItem(new MenuItemCondition(MinigameUtils.capitalize(con.getName()), Material.PAPER, exec, con));
		}
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		m.addItem(new MenuItemConditionAdd("Add Condition", Material.ITEM_FRAME, exec), m.getSize() - 1);
		m.displayMenu(player);
	}
}
