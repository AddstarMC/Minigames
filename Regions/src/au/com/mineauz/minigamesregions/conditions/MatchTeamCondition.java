package au.com.mineauz.minigamesregions.conditions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class MatchTeamCondition extends ConditionInterface {
	
	private StringFlag team = new StringFlag("RED", "team");
	private BooleanFlag invert = new BooleanFlag(false, "invert");

	@Override
	public String getName() {
		return "MATCH_TEAM";
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
		return true;
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player, Node node, Event event) {
		boolean inv = invert.getFlag();
		if(player.getTeam() != null && player.getTeam().getColor().toString().equals(team.getFlag())){
			if(!inv)
				return true;
			else
				return false;
		}
		if(inv)
			return true;
		return false;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player, Region region, Event event) {
		if(player == null || !player.isInMinigame()) return false;
		boolean inv = invert.getFlag();
		if(player.getTeam() != null && player.getTeam().getColor().toString().equals(team.getFlag())){
			if(!inv)
				return true;
			else
				return false;
		}
		if(inv)
			return true;
		return false;
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		team.saveValue(path, config);
		invert.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		team.loadValue(path, config);
		invert.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Match Team", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		List<String> teams = new ArrayList<String>();
		for(TeamColor t : TeamColor.values())
			teams.add(MinigameUtils.capitalize(t.toString().replace("_", " ")));
		m.addItem(new MenuItemList("Team Color", Material.WOOL, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				team.setFlag(value.toUpperCase().replace(" ", "_"));
			}
			
			@Override
			public String getValue() {
				return MinigameUtils.capitalize(team.getFlag().replace("_", " "));
			}
		}, teams));
		m.addItem(invert.getMenuItem("Invert Match", Material.ENDER_PEARL));
		m.displayMenu(player);
		return true;
	}

}
