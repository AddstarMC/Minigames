package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.menu.MenuItemValue;
import au.com.mineauz.minigames.menu.MenuItemValue.IMenuItemChange;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigames.properties.types.StringProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class PlayerHasItemCondition extends ConditionInterface {
	
	private final StringProperty type = new StringProperty("STONE", "type"); // TODO: Change to enum property of Material
	private final BooleanProperty useData = new BooleanProperty(false, "usedata");
	private final IntegerProperty data = new IntegerProperty(0, "data");
	
	public PlayerHasItemCondition() {
		properties.addProperty(type);
		properties.addProperty(useData);
		properties.addProperty(data);
	}

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
		if(player.getPlayer().getInventory().contains(Material.getMaterial(type.getValue()))){
			if(useData.getValue()){
				short dam = data.getValue().shortValue();
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
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Player Has Item");
		MenuItemString typeItem = new MenuItemString("Item", Material.STONE, type);
		
		typeItem.setChangeHandler(new IMenuItemChange<String>() {
			@Override
			public void onChange(MenuItemValue<String> menuItem, MinigamePlayer player, String previous, String current) {
				if (Material.getMaterial(current.toUpperCase()) == null) {
					type.setValue(previous);
					player.sendMessage("Invalid Item!", MessageType.Error);
				}
			}
		});
		
		m.addItem(typeItem);
		m.addItem(new MenuItemBoolean("Match Item Data", Material.ENDER_PEARL, useData));
		m.addItem(new MenuItemInteger("Data Value", Material.EYE_OF_ENDER, data, 0, Integer.MAX_VALUE));
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
