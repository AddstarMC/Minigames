package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemValue;
import au.com.mineauz.minigames.menu.MenuItem.IMenuItemClickItem;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.menu.MenuItemValue.IMenuItemChange;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigames.properties.types.StringProperty;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.TriggerArea;

public class MatchBlockCondition extends ConditionInterface {
	
	private final StringProperty type = new StringProperty("STONE", "type");
	private final BooleanProperty useDur = new BooleanProperty(false, "usedur");
	private final IntegerProperty dur = new IntegerProperty(0, "dur");
	
	public MatchBlockCondition() {
		properties.addProperty(type);
		properties.addProperty(useDur);
		properties.addProperty(dur);
	}

	@Override
	public String getName() {
		return "MATCH_BLOCK";
	}
	
	@Override
	public String getCategory(){
		return "World Conditions";
	}

	@Override
	public boolean useInRegions() {
		return false;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}
	
	@Override
	public boolean requiresPlayer() {
		return false;
	}
	
	@Override
	public boolean checkCondition(MinigamePlayer player, TriggerArea area) {
		if (area instanceof Node) {
			Block block = ((Node)area).getLocation().getBlock();
			if(block.getType() == Material.getMaterial(type.getValue()) &&
					(!useDur.getValue() || 
							block.getData() == dur.getValue().byteValue())){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean displayMenu(final MinigamePlayer player, Menu prev) {
		final Menu m = new Menu(3, "Match Block");
		final MenuItem c = new MenuItem("Auto Set Block", "Click here with a;block you wish to;match to.", Material.ITEM_FRAME);
		m.setControlItem(c, 4);
		
		MenuItemString btype = new MenuItemString("Block Type", Material.STONE, type);
		
		btype.setChangeHandler(new IMenuItemChange<String>() {
			@Override
			public void onChange(MenuItemValue<String> menuItem, MinigamePlayer player, String previous, String current) {
				if (Material.matchMaterial(current.toUpperCase()) == null) {
					type.setValue(previous);
					player.sendMessage("No block found by that name!", MessageType.Error);
				}
			}
		});
		m.addItem(btype);
		m.addItem(new MenuItemBoolean("Use Data Values", Material.ENDER_PEARL, useDur));
		m.addItem(new MenuItemInteger("Data Value", Material.PAPER, dur, 0, 16));
		
		c.setClickWithItemHandler(new IMenuItemClickItem() {
			@Override
			public void onClickWithItem(MenuItem menuItem, MinigamePlayer player, ItemStack item) {
				type.setValue(item.getType().toString());
				useDur.setValue(true);
				dur.setValue(((Short)item.getDurability()).intValue());
				m.refresh();
			}
		});
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
