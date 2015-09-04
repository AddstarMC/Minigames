package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemEnum;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.minigame.TeamSelection;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.properties.types.EnumProperty;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigamesregions.TriggerArea;

public class SetTeamScoreAction extends ActionInterface {
	
	private final IntegerProperty score = new IntegerProperty(1, "amount");
	private final EnumProperty<TeamSelection> team = new EnumProperty<TeamSelection>(TeamSelection.NONE, "team");
	
	public SetTeamScoreAction() {
		properties.addProperty(score);
		properties.addProperty(team);
	}

	@Override
	public String getName() {
		return "SET_TEAM_SCORE";
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
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		if (team.getValue() == TeamSelection.NONE) {
			if (player.getTeam() != null) {
				player.getTeam().setScore(score.getValue());
			}
		} else {
			TeamsModule tm = player.getMinigame().getModule(TeamsModule.class);
			if (tm.hasTeam(team.getValue().getTeam())) {
				tm.getTeam(team.getValue().getTeam()).setScore(score.getValue());
			}
		}
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Set Team Score");
		m.addItem(new MenuItemInteger("Set Score Amount", Material.STONE, score, Integer.MIN_VALUE, Integer.MAX_VALUE));
		
		m.addItem(new MenuItemEnum<TeamSelection>("Specific Team", "If 'None', the players;team will be used", Material.PAPER, team, TeamSelection.class));
		m.displayMenu(player);
		return true;
	}

}
