package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.properties.types.StringProperty;
import au.com.mineauz.minigamesregions.TriggerArea;

public class MessageAction extends ActionInterface {
	
	private final StringProperty msg = new StringProperty("Hello World", "message");
	
	public MessageAction() {
		properties.addProperty(msg);
	}

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
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		player.sendMessage(msg.getValue(), MessageType.Normal);
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Options");
		m.addItem(new MenuItemString("Message", Material.PAPER, msg));
		m.displayMenu(player);
		return true;
	}
}
