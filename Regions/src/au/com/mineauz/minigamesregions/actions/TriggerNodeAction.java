package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.properties.types.StringProperty;
import au.com.mineauz.minigamesregions.RegionModule;
import au.com.mineauz.minigamesregions.TriggerArea;
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
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		Minigame mg = player.getMinigame();
		if (mg != null) {
			RegionModule rmod = mg.getModule(RegionModule.class);
			if (rmod.hasNode(node.getValue())) {
				rmod.getNode(node.getValue()).execute(Triggers.getTrigger("REMOTE"), player);
			}
		}
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Trigger Node");
		m.addItem(new MenuItemString("Node Name", Material.EYE_OF_ENDER, node));
		m.displayMenu(player);
		return true;
	}

}
