package au.com.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.NodeExecutor;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionExecutor;

public class TriggerRandomAction extends ActionInterface{
	
	private final IntegerProperty timesTriggered = new IntegerProperty(1, "timesTriggered");
	private final BooleanProperty randomPerTrigger = new BooleanProperty(false, "randomPerTrigger");
	
	public TriggerRandomAction() {
		properties.addProperty(timesTriggered);
		properties.addProperty(randomPerTrigger);
	}

	@Override
	public String getName() {
		return "TRIGGER_RANDOM";
	}

	@Override
	public String getCategory() {
		return "Region/Node Actions";
	}

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Region region) {
		List<RegionExecutor> exs = new ArrayList<RegionExecutor>();
		for(RegionExecutor ex : region.getExecutors()){
			if(ex.getTrigger().getName().equalsIgnoreCase("RANDOM"))
				exs.add(ex);
		}
		Collections.shuffle(exs);
		if(timesTriggered.getValue() == 1){
			if(region.checkConditions(exs.get(0), player) && exs.get(0).canBeTriggered(player))
				region.execute(exs.get(0), player);
		}
		else{
			for(int i = 0; i < timesTriggered.getValue(); i++){
				if(!randomPerTrigger.getValue()){
					if(i == timesTriggered.getValue()) break;
					if(region.checkConditions(exs.get(i), player) && exs.get(i).canBeTriggered(player))
						region.execute(exs.get(i), player);
				}
				else{
					if(region.checkConditions(exs.get(0), player) && exs.get(0).canBeTriggered(player))
						region.execute(exs.get(0), player);
					Collections.shuffle(exs);
				}
			}
		}
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node) {
		List<NodeExecutor> exs = new ArrayList<NodeExecutor>();
		for(NodeExecutor ex : node.getExecutors()){
			if(ex.getTrigger().getName().equalsIgnoreCase("RANDOM"))
				exs.add(ex);
		}
		Collections.shuffle(exs);
		if(timesTriggered.getValue() == 1){
			if(node.checkConditions(exs.get(0), player) && exs.get(0).canBeTriggered(player))
				node.execute(exs.get(0), player);
		}
		else{
			for(int i = 0; i < timesTriggered.getValue(); i++){
				if(!randomPerTrigger.getValue()){
					if(i == timesTriggered.getValue()) break;
					if(node.checkConditions(exs.get(i), player) && exs.get(i).canBeTriggered(player))
						node.execute(exs.get(i), player);
				}
				else{
					if(node.checkConditions(exs.get(0), player) && exs.get(0).canBeTriggered(player))
						node.execute(exs.get(0), player);
					Collections.shuffle(exs);
				}
			}
		}
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Trigger Random");
		m.addItem(new MenuItemInteger("Times to Trigger Random", Material.COMMAND, timesTriggered, 1, Integer.MAX_VALUE));
		m.addItem(new MenuItemBoolean("Allow Same Executor", "Should there be a chance;that the same execeutor;can be triggered more?", Material.ENDER_PEARL, randomPerTrigger));
		m.displayMenu(player);
		return true;
	}

}
