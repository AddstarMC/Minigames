package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigames.properties.types.StringProperty;
import au.com.mineauz.minigamesregions.TriggerArea;

public class BroadcastAction extends ActionInterface{
	
	private final StringProperty message = new StringProperty("Hello World", "message");
	private final BooleanProperty excludeExecutor = new BooleanProperty(false, "exludeExecutor");
	private final BooleanProperty redText = new BooleanProperty(false, "redText");
	
	public BroadcastAction() {
		properties.addProperty(message);
		properties.addProperty(excludeExecutor);
		properties.addProperty(redText);
	}

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
	public boolean requiresPlayer() {
		return false;
	}

	@Override
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		MessageType type = MessageType.Normal;
		if(redText.getValue())
			type = MessageType.Error;
		MinigamePlayer exclude = null;
		if(excludeExecutor.getValue())
			exclude = player;
		
		player.getMinigame().broadcastExcept(message.getValue().replaceAll("%player%", player.getDisplayName()), type, exclude);
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Broadcast");
		
		m.addItem(new MenuItemString("Message", Material.NAME_TAG, message));
		m.addItem(new MenuItemBoolean("Don't Send to Executor", Material.ENDER_PEARL, excludeExecutor));
		m.addItem(new MenuItemBoolean("Red Message", Material.ENDER_PEARL, redText));
		
		m.displayMenu(player);
		return true;
	}

}
