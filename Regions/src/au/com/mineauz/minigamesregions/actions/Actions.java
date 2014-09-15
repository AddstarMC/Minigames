package au.com.mineauz.minigamesregions.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigamesregions.NodeExecutor;
import au.com.mineauz.minigamesregions.RegionExecutor;
import au.com.mineauz.minigamesregions.menuitems.MenuItemAction;
import au.com.mineauz.minigamesregions.menuitems.MenuItemActionAdd;

public class Actions {
	private static Map<String, Class<? extends ActionInterface>> actions = new HashMap<String, Class<? extends ActionInterface>>();
	
	static{
		addAction("KILL", KillAction.class);
		addAction("REVERT", RevertAction.class);
		addAction("QUIT", QuitAction.class);
		addAction("END", EndAction.class);
		addAction("MESSAGE", MessageAction.class);
		addAction("ADD_SCORE", AddScoreAction.class);
		addAction("SET_SCORE", SetScoreAction.class);
		addAction("REEQUIP_LOADOUT", ReequipLoadoutAction.class);
		addAction("EQUIP_LOADOUT", EquipLoadoutAction.class);
		addAction("HEAL", HealAction.class);
		addAction("BARRIER", BarrierAction.class);
		addAction("SPAWN_ENTITY", SpawnEntityAction.class);
		addAction("TRIGGER_NODE", TriggerNodeAction.class);
		addAction("TRIGGER_REGION", TriggerRegionAction.class);
		addAction("PULSE_REDSTONE", PulseRedstoneAction.class);
		addAction("EXECUTE_COMMAND", ExecuteCommandAction.class);
		addAction("SET_BLOCK", SetBlockAction.class);
		addAction("EXPLODE", ExplodeAction.class);
		addAction("PLAY_SOUND", PlaySoundAction.class);
		addAction("CHECKPOINT", CheckpointAction.class);
		addAction("SWAP_BLOCK", SwapBlockAction.class);
		addAction("APPLY_POTION", ApplyPotionAction.class);
		addAction("FALLING_BLOCK", FallingBlockAction.class);
		addAction("ADD_TEAM_SCORE", AddTeamScoreAction.class);
		addAction("SET_TEAM_SCORE", SetTeamScoreAction.class);
		addAction("FLIGHT", FlightAction.class);
	}
	
	public static void addAction(String name, Class<? extends ActionInterface> action){
		actions.put(name, action);
	}
	
	public static ActionInterface getActionByName(String name){
		if(actions.containsKey(name.toUpperCase()))
			try {
				return actions.get(name.toUpperCase()).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		return null;
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
