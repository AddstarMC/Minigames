package com.pauldavdesign.mineauz.minigamesregions.actions;

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
import com.pauldavdesign.mineauz.minigamesregions.menuitems.MenuItemAction;
import com.pauldavdesign.mineauz.minigamesregions.menuitems.MenuItemActionAdd;

public class Actions {
	private static Map<String, ActionInterface> actions = new HashMap<String, ActionInterface>();
	
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
		addAction(new SpawnEntityAction());
		addAction(new TriggerNodeAction());
		addAction(new PulseRedstoneAction());
	}
	
	public static void addAction(ActionInterface action){
		actions.put(action.getName().toUpperCase(), action);
	}
	
	public static ActionInterface getActionByName(String name){
		if(actions.containsKey(name.toUpperCase()))
			return actions.get(name.toUpperCase());
		return null;
	}
	
	public static Collection<ActionInterface> getAllActions(){
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
		for(ActionInterface act : exec.getActions()){
			m.addItem(new MenuItemAction(MinigameUtils.capitalize(act.getName()), Material.PAPER, exec, act));
		}
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		m.addItem(new MenuItemActionAdd("Add Action", Material.ITEM_FRAME, exec), m.getSize() - 1);
		m.displayMenu(player);
	}
	
	public static void displayMenu(MinigamePlayer player, NodeExecutor exec, Menu prev){
		Menu m = new Menu(3, "Actions", player);
		m.setPreviousPage(prev);
		for(ActionInterface act : exec.getActions()){
			m.addItem(new MenuItemAction(MinigameUtils.capitalize(act.getName()), Material.PAPER, exec, act));
		}
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		m.addItem(new MenuItemActionAdd("Add Action", Material.ITEM_FRAME, exec), m.getSize() - 1);
		m.displayMenu(player);
	}
}
