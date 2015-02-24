package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class EquipLoadoutAction extends ActionInterface {
	
	private StringFlag loadout = new StringFlag("default", "loadout");

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
	public void executeNodeAction(MinigamePlayer player,
			Node node) {
		if(player == null || !player.isInMinigame()) return;
		LoadoutModule lmod = player.getMinigame().getModule(LoadoutModule.class);
		if(lmod.hasLoadout(loadout.getFlag())){
			player.setLoadout(lmod.getLoadout(loadout.getFlag()));
		}
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Region region) {
		if(player == null || !player.isInMinigame()) return;
		LoadoutModule lmod = player.getMinigame().getModule(LoadoutModule.class);
		if(lmod.hasLoadout(loadout.getFlag())){
			player.setLoadout(lmod.getLoadout(loadout.getFlag()));
		}
	}

	@Override
	public void saveArguments(FileConfiguration config,
			String path) {
		loadout.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		loadout.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Equip Loadout");
		m.addItem(new MenuItemString("Loadout Name", Material.DIAMOND_SWORD, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				loadout.setFlag(value);
			}
			
			@Override
			public String getValue() {
				return loadout.getFlag();
			}
		}));
		m.displayMenu(player);
		return true;
	}

}
