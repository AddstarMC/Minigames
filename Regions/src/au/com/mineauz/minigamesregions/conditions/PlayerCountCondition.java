package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.TriggerArea;

public class PlayerCountCondition extends ConditionInterface {
	
	private final IntegerProperty min = new IntegerProperty(1, "min");
	private final IntegerProperty max = new IntegerProperty(5, "max");
	
	public PlayerCountCondition() {
		properties.addProperty(min);
		properties.addProperty(max);
	}

	@Override
	public String getName() {
		return "PLAYER_COUNT";
	}

	@Override
	public String getCategory() {
		return "Player Conditions";
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
			Region region = (Region)area;
			return (region.getPlayers().size() >= min.getValue() && region.getPlayers().size() <= max.getValue());
		} else {
			return false;
		}
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Player Count");
		m.addItem(new MenuItemInteger("Min Player Count", Material.STEP, min, 1, Integer.MAX_VALUE));
		m.addItem(new MenuItemInteger("Max Player Count", Material.STONE, max, 1, Integer.MAX_VALUE));
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
