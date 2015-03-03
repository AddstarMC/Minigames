package au.com.mineauz.minigamesregions.conditions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItem.IMenuItemClickItem;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class MatchBlockCondition extends ConditionInterface {
	
	private StringFlag type = new StringFlag("STONE", "type");
	private BooleanFlag useDur = new BooleanFlag(false, "usedur");
	private IntegerFlag dur = new IntegerFlag(0, "dur");

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
	public boolean checkRegionCondition(MinigamePlayer player, Region region) {
		return false;
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player, Node node) {
		return check(node.getLocation());
	}
	
	@SuppressWarnings("deprecation")
	private boolean check(Location location){
		Block block = location.getBlock();
		if(block.getType() == Material.getMaterial(type.getFlag()) &&
				(!useDur.getFlag() || 
						block.getData() == dur.getFlag().byteValue())){
			return true;
		}
		return false;
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		type.saveValue(path, config);
		useDur.saveValue(path, config);
		dur.saveValue(path, config);
		saveInvert(config, path);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		type.loadValue(path, config);
		useDur.loadValue(path, config);
		dur.loadValue(path, config);
		loadInvert(config, path);
	}

	@Override
	public boolean displayMenu(final MinigamePlayer player, Menu prev) {
		final Menu m = new Menu(3, "Match Block");
		final MenuItem c = new MenuItem("Auto Set Block", "Click here with a;block you wish to;match to.", Material.ITEM_FRAME);
		m.setControlItem(c, 4);
		
		final MenuItemString btype = new MenuItemString("Block Type", Material.STONE, new Callback<String>() {

			@Override
			public void setValue(String value) {
				if(Material.matchMaterial(value.toUpperCase()) != null)
					type.setFlag(value.toUpperCase());
				else
					player.sendMessage("No block found by that name!", MessageType.Error);
			}

			@Override
			public String getValue() {
				return type.getFlag();
			}
		});
		m.addItem(btype);
		final MenuItemBoolean busedur = (MenuItemBoolean) useDur.getMenuItem("Use Data Values", Material.ENDER_PEARL);
		m.addItem(busedur);
		final MenuItemInteger bdur = (MenuItemInteger) dur.getMenuItem("Data Value", Material.PAPER, 0, 16);
		m.addItem(bdur);
		
		c.setClickWithItemHandler(new IMenuItemClickItem() {
			@Override
			public void onClickWithItem(MenuItem menuItem, MinigamePlayer player, ItemStack item) {
				type.setFlag(item.getType().toString());
				useDur.setFlag(true);
				dur.setFlag(((Short)item.getDurability()).intValue());
				m.refresh();
			}
		});
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
