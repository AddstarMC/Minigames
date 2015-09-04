package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.properties.types.StringProperty;
import au.com.mineauz.minigamesregions.TriggerArea;

public class EquipLoadoutAction extends ActionInterface {
	
	private final StringProperty loadout = new StringProperty("default", "loadout");
	
	public EquipLoadoutAction() {
		properties.addProperty(loadout);
	}

	@Override
	public String getName() {
		return "EQUIP_LOADOUT";
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
		LoadoutModule lmod = player.getMinigame().getModule(LoadoutModule.class);
		if(lmod.hasLoadout(loadout.getValue())){
			player.setLoadout(lmod.getLoadout(loadout.getValue()));
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
		Menu m = new Menu(3, "Equip Loadout");
		m.addItem(new MenuItemString("Loadout Name", Material.DIAMOND_SWORD, loadout));
		m.displayMenu(player);
		return true;
	}

}
