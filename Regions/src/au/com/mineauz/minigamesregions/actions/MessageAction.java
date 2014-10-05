package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class MessageAction extends ActionInterface {
	
	private StringFlag msg = new StringFlag("Hello World", "message");

	@Override
	public String getName() {
		return "MESSAGE";
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
	public void executeNodeAction(MinigamePlayer player,
			Node node) {
		if(player == null || !player.isInMinigame()) return;
		player.sendMessage(msg.getFlag(), null);
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Region region) {
		if(player == null || !player.isInMinigame()) return;
		player.sendMessage(msg.getFlag(), null);
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		msg.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		msg.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Options", player);
		m.setPreviousPage(previous);
		m.addItem(msg.getMenuItem("Message", Material.PAPER));
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, m.getPreviousPage()), m.getSize() - 9);
		m.displayMenu(player);
		return true;
	}
}
