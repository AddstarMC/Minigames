package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.properties.types.StringProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionModule;
import au.com.mineauz.minigamesregions.triggers.Triggers;

public class TriggerNodeAction extends ActionInterface {
	
	private final StringProperty node = new StringProperty("None", "node");
	
	public TriggerNodeAction() {
		properties.addProperty(node);
	}

	@Override
	public String getName() {
		return "TRIGGER_NODE";
	}

	@Override
	public String getCategory() {
		return "Remote Trigger Actions";
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
			Region region) {
		if(player == null || !player.isInMinigame()) return;
		Minigame mg = player.getMinigame();
		if(mg != null){
			RegionModule rmod = mg.getModule(RegionModule.class);
			if(rmod.hasNode(node.getValue()))
				rmod.getNode(node.getValue()).execute(Triggers.getTrigger("REMOTE"), player);
		}
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node) {
		if(player == null || !player.isInMinigame()) return;
		Minigame mg = player.getMinigame();
		if(mg != null){
			RegionModule rmod = mg.getModule(RegionModule.class);
			if(rmod.hasNode(this.node.getValue()))
				rmod.getNode(this.node.getValue()).execute(Triggers.getTrigger("REMOTE"), player);
		}
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Trigger Node");
		m.addItem(new MenuItemString("Node Name", Material.EYE_OF_ENDER, node));
		m.displayMenu(player);
		return true;
	}

}
