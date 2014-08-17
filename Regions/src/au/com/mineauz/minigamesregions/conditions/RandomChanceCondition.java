package au.com.mineauz.minigamesregions.conditions;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class RandomChanceCondition extends ConditionInterface {
	
	private IntegerFlag chance = new IntegerFlag(50, "chance");

	@Override
	public String getName() {
		return "RANDOM_CHANCE";
	}
	
	@Override
	public String getCategory(){
		return "Misc Conditions";
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
	public boolean checkRegionCondition(MinigamePlayer player, Region region, Event event) {
		return check();
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player, Node node, Event event) {
		return check();
	}
	
	private boolean check(){
		double chance = this.chance.getFlag().doubleValue() / 100d;
		Random rand = new Random();
		if(rand.nextDouble() <= chance)
			return true;
		return false;
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		chance.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		chance.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Random Chance", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		m.addItem(chance.getMenuItem("Percentage Chance", Material.EYE_OF_ENDER, 1, 99));
		return true;
	}

}
