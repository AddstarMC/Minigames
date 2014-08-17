package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class HasRequiredFlagsCondition extends ConditionInterface {
	
	private BooleanFlag invert = new BooleanFlag(false, "invert");

	@Override
	public String getName() {
		return "HAS_REQUIRED_FLAGS";
	}
	
	@Override
	public String getCategory(){
		return "Player Conditions";
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
	public boolean checkNodeCondition(MinigamePlayer player, Node node, Event event) {
		if(player == null || !player.isInMinigame()) return false;
		if(Minigames.plugin.pdata.checkRequiredFlags(player, player.getMinigame().getName(false)).isEmpty()){
			if(!invert.getFlag())
				return true;
		}
		else
			if(invert.getFlag())
				return true;
		return false;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player, Region region, Event event) {
		if(player == null || !player.isInMinigame()) return false;
		if(Minigames.plugin.pdata.checkRequiredFlags(player, player.getMinigame().getName(false)).isEmpty()){
			if(!invert.getFlag())
				return true;
		}
		else
			if(invert.getFlag())
				return true;
		return false;
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		invert.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		invert.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Required Flags", player);
		m.addItem(invert.getMenuItem("Invert Require Flags?", Material.ENDER_PEARL));
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		m.displayMenu(player);
		return true;
	}

}
