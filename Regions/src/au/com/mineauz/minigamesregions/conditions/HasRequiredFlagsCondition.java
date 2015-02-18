package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

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
	public boolean checkNodeCondition(MinigamePlayer player, Node node) {
		if(player == null || !player.isInMinigame()) return false;
		if(Minigames.plugin.pdata.checkRequiredFlags(player, player.getMinigame().getName(false)).isEmpty()){
			return true;
		}
		return false;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player, Region region) {
		if(player == null || !player.isInMinigame()) return false;
		if(Minigames.plugin.pdata.checkRequiredFlags(player, player.getMinigame().getName(false)).isEmpty()){
			return true;
		}
		return false;
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		saveInvert(config, path);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		loadInvert(config, path);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Required Flags");
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
