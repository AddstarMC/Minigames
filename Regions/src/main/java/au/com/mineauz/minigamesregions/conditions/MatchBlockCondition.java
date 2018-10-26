package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

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
	public void describe(Map<String, Object> out) {
		if (useDur.getFlag()) {
			out.put("Type", type.getFlag() + ":" + dur.getFlag());
		} else {
			out.put("Type", type.getFlag() + ":all");
		}
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
        return block.getType() == Material.getMaterial(type.getFlag()) &&
                (!useDur.getFlag() ||
                        block.getData() == dur.getFlag().byteValue());
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
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Match Block", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH, prev), m.getSize() - 9);
		final MenuItemCustom c = new MenuItemCustom("Auto Set Block", 
				MinigameUtils.stringToList("Click here with a;block you wish to;match to."), Material.ITEM_FRAME);
		m.addItem(c, m.getSize() - 1);
		final MinigamePlayer ply = m.getViewer();
		
		final MenuItemString btype = new MenuItemString("Block Type", Material.STONE, new Callback<String>() {

			@Override
			public void setValue(String value) {
				if(Material.matchMaterial(value.toUpperCase()) != null)
					type.setFlag(value.toUpperCase());
				else
                    ply.sendMessage("No block found by that name!", MinigameMessageType.ERROR);
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
		
		c.setClickItem(new InteractionInterface() {
			
			@SuppressWarnings("deprecation")
			@Override
			public Object interact(Object object) {
				ItemStack i = (ItemStack) object;
				type.setFlag(i.getType().toString());
				useDur.setFlag(true);
				dur.setFlag(((Short)i.getDurability()).intValue());
				bdur.updateDescription();
				busedur.updateDescription();
				btype.updateDescription();
				return c.getItem();
			}
		});
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
