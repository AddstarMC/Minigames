package au.com.mineauz.minigamesregions.conditions;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.InteractionInterface;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class MatchBlockCondition extends ConditionInterface {
	
	private StringFlag type = new StringFlag("STONE", "type");

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
			out.put("Type", type.getFlag() + ":all");
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
	
	private boolean check(Location location){
		Block block = location.getBlock();
        return block.getType() == Material.getMaterial(type.getFlag());
    }

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		type.saveValue(path, config);
		saveInvert(config, path);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		type.loadValue(path, config);
		loadInvert(config, path);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Match Block", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
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
					ply.sendMessage("No block found by that name!", "error");
			}

			@Override
			public String getValue() {
				return type.getFlag();
			}
		});
		m.addItem(btype);
		c.setClickItem(object -> {
            ItemStack i = (ItemStack) object;
            type.setFlag(i.getType().toString());
            btype.updateDescription();
            return c.getItem();
        });
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}
