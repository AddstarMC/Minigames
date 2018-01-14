package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.script.ExpressionParser;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class MessageAction extends AbstractAction {
	
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
	public void describe(Map<String, Object> out) {
		out.put("Message", msg.getFlag());
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
	public void executeNodeAction(final MinigamePlayer player, final Node node) {
		debug(player,node);
		if(player == null || !player.isInMinigame()) return;
		execute(player, node);
	}

	@Override
	public void executeRegionAction(final MinigamePlayer player, final Region region) {
		debug(player,region);
		if(player == null || !player.isInMinigame()) return;
		player.sendMessage(msg.getFlag(), null);
		execute(player,region);
	}
	
	private void execute(MinigamePlayer player,ScriptObject script ) {
		ScriptObject base = createScriptObject(player,script);
		String message = msg.getFlag();
		message = ExpressionParser.stringResolve(message, base, true, true);
		player.sendMessage(message, null);
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
