package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigamesregions.TriggerArea;

public class TeamScoreRangeCondition extends ConditionInterface {
	
	private final IntegerProperty min = new IntegerProperty(5, "min");
	private final IntegerProperty max = new IntegerProperty(10, "max");
	
	public TeamScoreRangeCondition() {
		properties.addProperty(min);
		properties.addProperty(max);
	}

	@Override
	public String getName() {
		return "TEAM_SCORE_RANGE";
	}

	@Override
	public String getCategory() {
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
	public boolean checkCondition(MinigamePlayer player, TriggerArea area) {
		if(player.getTeam() != null){
			Team t = player.getTeam();
			if(t.getScore() >= min.getValue() && 
					t.getScore() <= max.getValue()){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Team Score Range");
		m.addItem(new MenuItemInteger("Minimum Score", Material.STEP, min, 0, Integer.MAX_VALUE));
		m.addItem(new MenuItemInteger("Maximum Score", Material.STONE, max, 0, Integer.MAX_VALUE));
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
