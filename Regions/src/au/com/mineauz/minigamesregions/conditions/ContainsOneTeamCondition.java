package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class ContainsOneTeamCondition extends ConditionInterface {
	
	private BooleanFlag invert = new BooleanFlag(false, "invert");

	@Override
	public String getName() {
		return "CONTAINS_ONE_TEAM";
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
	public boolean checkNodeCondition(MinigamePlayer player, Node node, Event event) {
		return false;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player, Region region, Event event) {
		boolean ret = true;
		Team last = player.getTeam();
		if(last == null) return true;
		for(MinigamePlayer p : region.getPlayers()){
			if(last != p.getTeam()){
				ret = false;
				break;
			}
		}
		if(ret && invert.getFlag())
			return false;
		return ret;
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
		Menu m = new Menu(3, "Contains One Team", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		m.addItem(invert.getMenuItem("Invert", Material.ENDER_PEARL));
		m.displayMenu(player);
		return true;
	}

}
