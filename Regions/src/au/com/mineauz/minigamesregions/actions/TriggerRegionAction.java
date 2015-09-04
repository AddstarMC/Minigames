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

public class TriggerRegionAction extends ActionInterface {
	
	private final StringProperty region = new StringProperty("None", "region");
	
	public TriggerRegionAction() {
		properties.addProperty(region);
	}

	@Override
	public String getName() {
		return "TRIGGER_REGION";
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
			if (rmod.hasRegion(this.region.getValue())) {
				rmod.getRegion(this.region.getValue()).execute(Triggers.getTrigger("REMOTE"), player);
			}
		}
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Trigger Node");
		m.addItem(new MenuItemString("Region Name", Material.EYE_OF_ENDER, region));
		m.displayMenu(player);
		return true;
	}

}
