package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.TriggerArea;

public class HasRequiredFlagsCondition extends ConditionInterface {

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
	public boolean checkCondition(MinigamePlayer player, TriggerArea area) {
		if (Minigames.plugin.pdata.checkRequiredFlags(player, player.getMinigame().getName(false)).isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Required Flags");
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
