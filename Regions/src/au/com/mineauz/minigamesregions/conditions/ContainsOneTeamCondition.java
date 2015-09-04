package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.TriggerArea;

public class ContainsOneTeamCondition extends ConditionInterface {

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
	public boolean requiresPlayer() {
		return false;
	}

	@Override
	public boolean checkCondition(MinigamePlayer player, TriggerArea area) {
		if (area instanceof Region) {
			Team last = null;
			
			for (MinigamePlayer p : ((Region)area).getPlayers()) {
				if (last == null) {
					last = p.getTeam();
				} else if (last != p.getTeam()) {
					return false;
				}
			}
			return true;
		}
		
		return false;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Contains One Team");
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
