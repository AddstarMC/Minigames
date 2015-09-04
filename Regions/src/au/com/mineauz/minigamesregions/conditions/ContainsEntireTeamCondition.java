package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.TriggerArea;

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
	public boolean checkCondition(MinigamePlayer player, TriggerArea area) {
		if (area instanceof Region) {
			return ((Region)area).getPlayers().containsAll(player.getTeam().getPlayers());
		}
		return false;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu menu = new Menu(3, "Contains Entire Team");
		addInvertMenuItem(menu);
		menu.displayMenu(player);
		return true;
	}

}
