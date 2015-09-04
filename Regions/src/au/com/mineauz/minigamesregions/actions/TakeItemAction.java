package au.com.mineauz.minigamesregions.actions;

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

public class TakeItemAction extends ActionInterface{
	
	private final StringProperty type = new StringProperty("STONE", "type"); // TODO: Make this an enum property
	private final BooleanProperty matchDamage = new BooleanProperty(true, "matchdamage");
	private final IntegerProperty damage = new IntegerProperty(0, "damage");
	private final IntegerProperty count = new IntegerProperty(1, "count");
	
	public TakeItemAction() {
		properties.addProperty(type);
		properties.addProperty(matchDamage);
		properties.addProperty(damage);
		properties.addProperty(count);
	}

	@Override
	public String getName() {
		return "TAKE_ITEM";
	}

	@Override
	public String getCategory() {
		return "Player Actions";
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
	public void executeRegionAction(MinigamePlayer player, Region region) {
		execute(player);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node) {
		execute(player);
	}
	
	private void execute(MinigamePlayer player){
		ItemStack match = new ItemStack(Material.getMaterial(type.getValue()), count.getValue(), damage.getValue().shortValue());
		ItemStack matched = null;
		boolean remove = false;
		int slot = 0;
		
		for(ItemStack i : player.getPlayer().getInventory().getContents()){
			if(i != null && i.getType() == match.getType()){
				if(!matchDamage.getValue() || match.getDurability() == i.getDurability()){
					if(match.getAmount() >= i.getAmount()){
						matched = i.clone();
						remove = true;
					}
					else{
						matched = i.clone();
						matched.setAmount(matched.getAmount() - match.getAmount());
					}
					break;
				}
			}
			slot++;
		}
		
		if(remove)
			player.getPlayer().getInventory().removeItem(matched);
		else{
			player.getPlayer().getInventory().getItem(slot).setAmount(matched.getAmount());
		}
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(final MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Give Item");
		
		MenuItemString typeItem = new MenuItemString("Type", Material.STONE, type);
		typeItem.setChangeHandler(new IMenuItemChange<String>() {
			@Override
			public void onChange(MenuItemValue<String> menuItem, MinigamePlayer player, String previous, String current) {
				if (Material.getMaterial(current.toUpperCase()) == null) {
					player.sendMessage("Invalid item type!", MessageType.Error);
					type.setValue(previous);
				}
			}
		});
		
		m.addItem(typeItem);
		m.addItem(new MenuItemInteger("Count", Material.STEP, count, 1, 64));
		m.addItem(new MenuItemInteger("Damage", Material.COBBLESTONE, damage, 0, Integer.MAX_VALUE));
		m.addItem(new MenuItemBoolean("Match Damage", Material.ENDER_PEARL, matchDamage));
		
		m.displayMenu(player);
		return true;
	}

}
