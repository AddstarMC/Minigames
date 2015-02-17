package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class ContainsEntireTeamCondition extends ConditionInterface {

	@Override
	public String getName() {
		return "CONTAINS_ENTIRE_TEAM";
	}
	
	@Override
	public String getCategory(){
		return "Team Conditions";
	}

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return false;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player, Region region) {
		if(region.getPlayers().containsAll(player.getTeam().getPlayers()))
			return true;
		return false;
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player, Node node) {
		return false;
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		saveInvert(config, path);
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		loadInvert(config, path);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu menu = new Menu(3, "Contains Entire Team");
		menu.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), menu.getSize() - 9);
		addInvertMenuItem(menu);
		menu.displayMenu(player);
		return true;
	}

}
