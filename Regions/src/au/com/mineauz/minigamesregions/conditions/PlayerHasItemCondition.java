package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class PlayerHasItemCondition extends ConditionInterface {
	
	private StringFlag type = new StringFlag("STONE", "type");
	private BooleanFlag useData = new BooleanFlag(false, "usedata");
	private IntegerFlag data = new IntegerFlag(0, "data");

	@Override
	public String getName() {
		return "PLAYER_HAS_ITEM";
	}

	@Override
	public String getCategory() {
		return "Player Conditions";
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
	public boolean checkRegionCondition(MinigamePlayer player, Region region) {
		return check(player);
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player, Node node) {
		return check(player);
	}
	
	private boolean check(MinigamePlayer player){
		if(player.getPlayer().getInventory().contains(Material.getMaterial(type.getFlag()))){
			if(useData.getFlag()){
				short dam = data.getFlag().shortValue();
				for(ItemStack i : player.getPlayer().getInventory().getContents()){
					if(i != null && i.getDurability() == dam){
						return true;
					}
				}
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		type.saveValue(path, config);
		useData.saveValue(path, config);
		data.saveValue(path, config);
		saveInvert(config, path);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		type.loadValue(path, config);
		useData.loadValue(path, config);
		data.loadValue(path, config);
		loadInvert(config, path);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Player Has Item");
		final MinigamePlayer fply = player;
		m.addItem(new MenuItemString("Item", Material.STONE, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				if(Material.getMaterial(value.toUpperCase()) != null)
					type.setFlag(value.toUpperCase());
				else
					fply.sendMessage("Invalid Item!", "error");
			}
			
			@Override
			public String getValue() {
				return type.getFlag();
			}
		}));
		m.addItem(useData.getMenuItem("Match Item Data", Material.ENDER_PEARL));
		m.addItem(data.getMenuItem("Data Value", Material.EYE_OF_ENDER, 0, null));
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
