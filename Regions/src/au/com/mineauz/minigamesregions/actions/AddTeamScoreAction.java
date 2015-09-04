package au.com.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigames.properties.types.StringProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class AddTeamScoreAction extends ActionInterface {
	
	private final IntegerProperty score = new IntegerProperty(1, "amount");
	private final StringProperty team = new StringProperty("NONE", "team"); // TODO: Make this an enum property on TeamSelection
	
	public AddTeamScoreAction() {
		properties.addProperty(score);
		properties.addProperty(team);
	}

	@Override
	public String getName() {
		return "ADD_TEAM_SCORE";
	}

	@Override
	public String getCategory() {
		return "Team Actions";
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
	public void executeRegionAction(MinigamePlayer player,
			Region region) {
		executeAction(player);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Node node) {
		executeAction(player);
	}
	
	private void executeAction(MinigamePlayer player){
		if(player == null || !player.isInMinigame()) return;
		if(player.getTeam() != null && team.getValue().equals("NONE")){
			player.getTeam().addScore(score.getValue());
		}
		else if(!team.getValue().equals("NONE")){
			TeamsModule tm = player.getMinigame().getModule(TeamsModule.class);
			if(tm.hasTeam(TeamColor.valueOf(team.getValue()))){
				tm.getTeam(TeamColor.valueOf(team.getValue())).addScore(score.getValue());
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
		Menu m = new Menu(3, "Add Team Score");
		m.addItem(new MenuItemInteger("Add Score Amount", Material.STONE, score, Integer.MIN_VALUE, Integer.MAX_VALUE));
		
		List<String> teams = new ArrayList<String>();
		teams.add("None");
		for(TeamColor team : TeamColor.values()){
			teams.add(MinigameUtils.capitalize(team.toString()));
		}
		m.addItem(new MenuItemList("Specific Team", "If 'None', the players;team will be used", Material.PAPER, team, teams));
		m.displayMenu(player);
		return true;
	}

}
