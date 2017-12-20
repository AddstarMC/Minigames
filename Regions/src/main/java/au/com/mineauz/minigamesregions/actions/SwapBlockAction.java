package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class SwapBlockAction extends ActionInterface {
	
	private StringFlag matchType = new StringFlag("STONE", "matchtype");
	private StringFlag toType = new StringFlag("COBBLESTONE", "totype");
	private BooleanFlag keepAttachment = new BooleanFlag(false, "keepattachment");


	@Override
	public String getName() {
		return "SWAP_BLOCK";
	}

	@Override
	public String getCategory() {
		return "Block Actions";
	}
	
	@Override
	public void describe(Map<String, Object> out) {
		out.put("From", matchType.getFlag() + ":all");
		out.put("To", toType.getFlag());
		out.put("Keep Attachment", keepAttachment.getFlag());
	}

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return false;
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Region region) {
		debug(player,region);
		for (int y = region.getFirstPoint().getBlockY(); y <= region.getSecondPoint().getBlockY(); y++) {
			for (int x = region.getFirstPoint().getBlockX(); x <= region.getSecondPoint().getBlockX(); x++) {
				for (int z = region.getFirstPoint().getBlockZ(); z <= region.getSecondPoint().getBlockZ(); z++) {
					Block block = region.getFirstPoint().getWorld().getBlockAt(x, y, z);
					
					if (block.getType() == Material.getMaterial(matchType.getFlag())) {
						// Block matches, now replace it
						BlockFace facing = null;
						 if (keepAttachment.getFlag()) {
							// Keep attachments if possible
							MaterialData mat = block.getState().getData();
							if (mat instanceof Directional) {
								facing = ((Directional)mat).getFacing();
							}
						}
						
						// Update block type
						block.setType(Material.getMaterial(toType.getFlag()), false);
						if (facing != null) {
							BlockState state = block.getState();
							MaterialData mat = block.getState().getData();
							if (mat instanceof Directional) {
								((Directional)mat).setFacingDirection(facing);
							}
							state.setData(mat);
							state.update(true, false);
						}
					}
				}
			}
		}
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Node node) {
		debug(player,node);
	}
	
	@Override
	public void saveArguments(FileConfiguration config,
			String path) {
		matchType.saveValue(path, config);
		toType.saveValue(path, config);
		keepAttachment.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		matchType.loadValue(path, config);
		toType.loadValue(path, config);
		keepAttachment.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Swap Block", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		final MinigamePlayer fply = player;
		m.addItem(new MenuItemString("Match Block", Material.COBBLESTONE, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				if(Material.matchMaterial(value.toUpperCase()) != null)
					matchType.setFlag(value.toUpperCase());
				else
					fply.sendMessage("Invalid block type!", "error");
			}
			
			@Override
			public String getValue() {
				return matchType.getFlag();
			}
		}));
		
		m.addItem(new MenuItemNewLine());
		
		m.addItem(new MenuItemString("To Block", Material.STONE, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				if(Material.matchMaterial(value.toUpperCase()) != null)
					toType.setFlag(value.toUpperCase());
				else
					fply.sendMessage("Invalid block type!", "error");
			}
			
			@Override
			public String getValue() {
				return toType.getFlag();
			}
		}));
		m.addItem(keepAttachment.getMenuItem("Keep Attachment", Material.PISTON_BASE, MinigameUtils.stringToList("When on, and To Block Use Data is off;If the source and target block;types are both blocks that;attach to surfaces, this;attachment will be preserved")));
		m.displayMenu(player);
		return true;
	}

}
