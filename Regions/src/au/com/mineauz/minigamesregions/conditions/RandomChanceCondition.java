package au.com.mineauz.minigamesregions.conditions;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class RandomChanceCondition extends ConditionInterface {
	
	private final IntegerProperty chance = new IntegerProperty(50, "chance");
	
	public RandomChanceCondition() {
		properties.addProperty(chance);
	}

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
	public boolean checkRegionCondition(MinigamePlayer player, Region region) {
		return check();
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player, Node node) {
		return check();
	}
	
	private boolean check(){
		double chance = this.chance.getValue().doubleValue() / 100d;
		Random rand = new Random();
		if(rand.nextDouble() <= chance)
			return true;
		return false;
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Random Chance");
		m.addItem(new MenuItemInteger("Percentage Chance", Material.EYE_OF_ENDER, chance, 1, 99));
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
