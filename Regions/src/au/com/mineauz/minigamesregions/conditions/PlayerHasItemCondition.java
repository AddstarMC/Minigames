package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class PlayerHasItemCondition extends ConditionInterface {
	
	private StringFlag type = new StringFlag("STONE", "type");
	private BooleanFlag useData = new BooleanFlag(false, "usedata");
	private IntegerFlag data = new IntegerFlag(0, "data");
	private BooleanFlag invert = new BooleanFlag(false, "invert");

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
	public boolean checkRegionCondition(MinigamePlayer player, Region region, Event event) {
		return check(player);
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player, Node node, Event event) {
		return check(player);
	}
	
	private boolean check(MinigamePlayer player){
		boolean inv = invert.getFlag();
		if(player.getPlayer().getInventory().contains(Material.getMaterial(type.getFlag()))){
			if(useData.getFlag()){
				short dam = data.getFlag().shortValue();
				for(ItemStack i : player.getPlayer().getInventory().getContents()){
					if(i != null && i.getDurability() == dam){
						if(!inv) return true;
						else return false;
					}
				}
				if(!inv) return false;
				else return true;
			}
			if(!inv) return true;
			else return false;
		}
		if(!inv) return false;
		else return true;
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		type.saveValue(path, config);
		useData.saveValue(path, config);
		data.saveValue(path, config);
		invert.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		type.loadValue(path, config);
		useData.loadValue(path, config);
		data.loadValue(path, config);
		invert.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Player Has Item", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
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
		m.addItem(invert.getMenuItem("Invert Result", Material.ENDER_PEARL));
		m.displayMenu(player);
		return true;
	}

}
