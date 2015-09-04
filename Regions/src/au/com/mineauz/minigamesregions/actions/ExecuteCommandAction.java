package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.properties.ChangeListener;
import au.com.mineauz.minigames.properties.ObservableValue;
import au.com.mineauz.minigames.properties.types.StringProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class ExecuteCommandAction extends ActionInterface {
	
	private final StringProperty comd = new StringProperty("say Hello World!", "command");
	
	public ExecuteCommandAction() {
		properties.addProperty(comd);
		
		// Adds in the translation of ./ into /
		comd.addListener(new ChangeListener<String>() {
			@Override
			public void onValueChange( ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue.startsWith("./")) {
					((ObservableValue<String>)observable).setValue(newValue.replaceFirst("./", "/"));
				}
			}
		});
	}

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
		String command = replacePlayerTags(player, comd.getValue());
		command = command.replace("{region}", region.getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node) {
		String command = replacePlayerTags(player, comd.getValue());
		command = command
			.replace("{x}", String.valueOf(node.getLocation().getBlockX()))
			.replace("{y}", String.valueOf(node.getLocation().getBlockY()))
			.replace("{z}", String.valueOf(node.getLocation().getBlockZ()))
			.replace("{node}", node.getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
	}
	
	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Execute Command");
		m.addItem(new MenuItemString("Command", "Do not include '/';If '//' command, start with './'", Material.COMMAND, comd));
		m.displayMenu(player);
		return true;
	}

}
