package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public abstract class ConditionInterface {
	
	private BooleanFlag invert = new BooleanFlag(false, "invert");
	protected void addInvertMenuItem(Menu m){
		m.addItem(invert.getMenuItem("Invert", Material.ENDER_PEARL), m.getSize() - 1);
	}
	protected void saveInvert(FileConfiguration config, String path){
		invert.saveValue(path, config);
	}
	protected void loadInvert(FileConfiguration config, String path){
		invert.loadValue(path, config);
	}
	public boolean isInverted(){
		return invert.getFlag();
	}
	
	public abstract String getName();
	public abstract String getCategory();
	public abstract boolean useInRegions();
	public abstract boolean useInNodes();
	public abstract boolean checkRegionCondition(MinigamePlayer player, Region region, Event event);
	public abstract boolean checkNodeCondition(MinigamePlayer player, Node node, Event event);
	public abstract void saveArguments(FileConfiguration config, String path);
	public abstract void loadArguments(FileConfiguration config, String path);
	public abstract boolean displayMenu(MinigamePlayer player, Menu prev);
}
