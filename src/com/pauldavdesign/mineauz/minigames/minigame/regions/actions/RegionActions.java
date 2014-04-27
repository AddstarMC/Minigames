package com.pauldavdesign.mineauz.minigames.minigame.regions.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.minigame.regions.MenuItemAction;
import com.pauldavdesign.mineauz.minigames.minigame.regions.MenuItemActionAdd;
import com.pauldavdesign.mineauz.minigames.minigame.regions.RegionExecutor;

public class RegionActions {
	private static Map<String, RegionActionInterface> actions = new HashMap<String, RegionActionInterface>();
	
	static{
		addAction(new KillAction());
		addAction(new RevertAction());
		addAction(new QuitAction());
		addAction(new EndAction());
		addAction(new MessageAction());
		addAction(new AddScoreAction());
		addAction(new ReequipLoadoutAction());
		addAction(new EquipLoadoutAction());
		addAction(new HealAction());
		addAction(new BarrierAction());
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
	
	public static void displayMenu(MinigamePlayer player, RegionExecutor exec, Menu prev){
		Menu m = new Menu(3, "Actions", player);
		m.setPreviousPage(prev);
		for(RegionActionInterface act : exec.getActions()){
			m.addItem(new MenuItemAction(MinigameUtils.capitalize(act.getName()), Material.PAPER, exec, act));
		}
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		m.addItem(new MenuItemActionAdd("Add Action", Material.ITEM_FRAME, exec), m.getSize() - 1);
		m.displayMenu(player);
	}
}
