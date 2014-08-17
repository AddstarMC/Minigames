package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class ExecuteCommandAction extends ActionInterface {
	
	private StringFlag comd = new StringFlag("say Hello World!", "command");

	@Override
	public String getName() {
		return "EXECUTE_COMMAND";
	}

	@Override
	public String getCategory() {
		return "Server Actions";
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
	public void executeRegionAction(MinigamePlayer player,
			Region region, Event event) {
		execute();
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Node node, Event event) {
		execute();
	}
	
	private void execute(){
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), comd.getFlag());
	}

	@Override
	public void saveArguments(FileConfiguration config,
			String path) {
		comd.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		comd.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Execute Command", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		m.addItem(new MenuItemString("Command", MinigameUtils.stringToList("Do not include '/';If '//' command, start with './'"), 
				Material.COMMAND, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				if(value.startsWith("./"))
					value = value.replaceFirst("./", "/");
				comd.setFlag(value);
			}
			
			@Override
			public String getValue() {
				return comd.getFlag();
			}
		}));
		m.displayMenu(player);
		return true;
	}

}
