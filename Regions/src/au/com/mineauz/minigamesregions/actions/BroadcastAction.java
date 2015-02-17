package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class BroadcastAction extends ActionInterface{
	
	private StringFlag message = new StringFlag("Hello World", "message");
	private BooleanFlag excludeExecutor = new BooleanFlag(false, "exludeExecutor");
	private BooleanFlag redText = new BooleanFlag(false, "redText");

	@Override
	public String getName() {
		return "BROADCAST";
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
	public void executeRegionAction(MinigamePlayer player, Region region) {
		execute(player);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node) {
		execute(player);
	}
	
	private void execute(MinigamePlayer player){
		String type = "info";
		if(redText.getFlag())
			type = "error";
		MinigamePlayer exclude = null;
		if(excludeExecutor.getFlag())
			exclude = player;
		
		Minigames.plugin.mdata.sendMinigameMessage(player.getMinigame(), message.getFlag().replaceAll("%player%", player.getDisplayName()), type, exclude);
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		message.saveValue(path, config);
		excludeExecutor.saveValue(path, config);
		redText.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		message.loadValue(path, config);
		excludeExecutor.loadValue(path, config);
		redText.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Broadcast");
		m.addItem(new MenuItemBack(previous), m.getSize() - 9);
		
		m.addItem(message.getMenuItem("Message", Material.NAME_TAG));
		m.addItem(excludeExecutor.getMenuItem("Don't Send to Executor", Material.ENDER_PEARL));
		m.addItem(redText.getMenuItem("Red Message", Material.ENDER_PEARL));
		
		m.displayMenu(player);
		return true;
	}

}
