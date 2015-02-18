package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
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
	
	private String replacePlayerTags(MinigamePlayer player, String string) {
		return string
			.replace("{player}", player.getName())
			.replace("{dispplayer}", player.getName())
			.replace("{px}", String.valueOf(player.getLocation().getX()))
			.replace("{py}", String.valueOf(player.getLocation().getY()))
			.replace("{pz}", String.valueOf(player.getLocation().getZ()))
			.replace("{yaw}", String.valueOf(player.getLocation().getYaw()))
			.replace("{pitch}", String.valueOf(player.getLocation().getPitch()))
			.replace("{minigame}", player.getMinigame().getName(false))
			.replace("{dispminigame}", player.getMinigame().getName(true))
			.replace("{deaths}", String.valueOf(player.getDeaths()))
			.replace("{kills}", String.valueOf(player.getKills()))
			.replace("{reverts}", String.valueOf(player.getReverts()))
			.replace("{score}", String.valueOf(player.getScore()))
			.replace("{team}", (player.getTeam() != null ? player.getTeam().getDisplayName() : ""));
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Region region) {
		String command = replacePlayerTags(player, comd.getFlag());
		command = command.replace("{region}", region.getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node) {
		String command = replacePlayerTags(player, comd.getFlag());
		command = command
			.replace("{x}", String.valueOf(node.getLocation().getBlockX()))
			.replace("{y}", String.valueOf(node.getLocation().getBlockY()))
			.replace("{z}", String.valueOf(node.getLocation().getBlockZ()))
			.replace("{node}", node.getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
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
		Menu m = new Menu(3, "Execute Command");
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
