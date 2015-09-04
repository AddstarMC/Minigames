package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigamesregions.TriggerArea;

public class AddScoreAction extends ActionInterface {
	
	private final IntegerProperty amount = new IntegerProperty(1, "amount");
	
	public AddScoreAction() {
		properties.addProperty(amount);
	}

	@Override
	public String getName() {
		return "ADD_SCORE";
	}

	@Override
	public String getCategory() {
		return "Minigame Actions";
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
		player.addScore(amount.getValue());
		player.getMinigame().setScore(player, player.getScore());
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Add Score");
		m.addItem(new MenuItemInteger("Add Score Amount", Material.ENDER_PEARL, amount, Integer.MIN_VALUE, Integer.MAX_VALUE));
		m.displayMenu(player);
		return true;
	}

}
