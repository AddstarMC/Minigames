package au.com.mineauz.minigamesregions.conditions;

import java.util.Arrays;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class PlayerHasItemCondition extends ConditionInterface {
	
	private StringFlag type = new StringFlag("STONE", "type");
	private BooleanFlag useData = new BooleanFlag(false, "usedata");
	private IntegerFlag data = new IntegerFlag(0, "data");
	private StringFlag where = new StringFlag("ANYWHERE", "where");
	private IntegerFlag slot = new IntegerFlag(0, "slot");

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
		PositionType checkType = PositionType.valueOf(where.getFlag().toUpperCase());
		if (checkType == null) {
			checkType = PositionType.ANYWHERE;
		}
		
		PlayerInventory inventory = player.getPlayer().getInventory();
		ItemStack[] searchItems;
		int startSlot;
		int endSlot;
		
		if (checkType == PositionType.ARMOR) {
			searchItems = inventory.getArmorContents();
			startSlot = 0;
			endSlot = searchItems.length;
		} else {
			searchItems = inventory.getContents();
			
			if (checkType == PositionType.HOTBAR) {
				startSlot = 0;
				endSlot = 9;
			} else if (checkType == PositionType.MAIN) {
				startSlot = 9;
				endSlot = 36;
			} else if (checkType == PositionType.SLOT) {
				startSlot = slot.getFlag();
				endSlot = startSlot + 1;
			} else {
				startSlot = 0;
				endSlot = searchItems.length;
			}
		}
		
		Material material = Material.getMaterial(type.getFlag());
		
		for (int i = startSlot; i < endSlot && i < searchItems.length; ++i) {
			ItemStack itemInSlot = searchItems[i];
			if (itemInSlot == null) {
				continue;
			}
			
			if (itemInSlot.getType() == material) {
				if (!useData.getFlag() || itemInSlot.getDurability() == data.getFlag()) {
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		type.saveValue(path, config);
		useData.saveValue(path, config);
		data.saveValue(path, config);
		where.saveValue(path, config);
		slot.saveValue(path, config);
		saveInvert(config, path);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		type.loadValue(path, config);
		useData.loadValue(path, config);
		data.loadValue(path, config);
		where.loadValue(path, config);
		slot.loadValue(path, config);
		loadInvert(config, path);
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
		m.addItem(new MenuItemList("Search Where", Material.COMPASS, new Callback<String>() {
			@Override
			public void setValue(String value) {
				where.setFlag(value.toUpperCase());
			}
			
			@Override
			public String getValue() {
				return WordUtils.capitalizeFully(where.getFlag());
			}
		}, Arrays.asList("Anywhere", "Hotbar", "Main", "Armor", "Slot")));
		m.addItem(slot.getMenuItem("Slot", Material.DIAMOND, 0, 35));
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}
	
	private enum PositionType {
		ANYWHERE,
		HOTBAR,
		MAIN,
		ARMOR,
		SLOT
	}

}
