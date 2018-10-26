package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

import java.util.Map;

@SuppressWarnings("deprecation")
public class SwapBlockAction extends AbstractAction {
	
	private StringFlag matchType = new StringFlag("STONE", "matchtype");
	private BooleanFlag matchData = new BooleanFlag(false, "matchdata");
	private IntegerFlag matchDataValue = new IntegerFlag(0, "matchdatavalue");
	private StringFlag toType = new StringFlag("COBBLESTONE", "totype");
	private BooleanFlag keepAttachment = new BooleanFlag(false, "keepattachment");
	private BooleanFlag toData = new BooleanFlag(false, "todata");
	private IntegerFlag toDataValue = new IntegerFlag(0, "todatavalue");

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
		if (matchData.getFlag()) {
			out.put("From", matchType.getFlag() + ":" + matchDataValue.getFlag());
		} else {
			out.put("From", matchType.getFlag() + ":all");
		}
		
		if (toData.getFlag()) {
			out.put("To", toType.getFlag() + ":" + toDataValue.getFlag());
		} else {
			out.put("To", toType.getFlag());
		}
		
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
						if (matchData.getFlag() && block.getData() != matchDataValue.getFlag().byteValue()) {
							continue;
						}
						
						// Block matches, now replace it
						BlockData data = null;
						BlockFace facing = null;
						if (keepAttachment.getFlag()) {
							// Keep attachments if possible
							BlockData mat = (BlockData) block.getState().getData();
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
						} else {
							block.setBlockData(data, false);
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
		matchData.saveValue(path, config);
		matchDataValue.saveValue(path, config);
		toType.saveValue(path, config);
		toData.saveValue(path, config);
		toDataValue.saveValue(path, config);
		keepAttachment.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		matchType.loadValue(path, config);
		matchData.loadValue(path, config);
		matchDataValue.loadValue(path, config);
		toType.loadValue(path, config);
		toData.loadValue(path, config);
		toDataValue.loadValue(path, config);
		keepAttachment.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Swap Block", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH, previous), m.getSize() - 9);
		final MinigamePlayer fply = player;
		m.addItem(new MenuItemString("Match Block", Material.COBBLESTONE, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				if(Material.matchMaterial(value.toUpperCase()) != null)
					matchType.setFlag(value.toUpperCase());
				else
                    fply.sendMessage("Invalid block type!", MinigameMessageType.ERROR);
			}
			
			@Override
			public String getValue() {
				return matchType.getFlag();
			}
		}));
		m.addItem(matchData.getMenuItem("Match Block Use Data?", Material.ENDER_PEARL));
		m.addItem(matchDataValue.getMenuItem("Match Block Data Value", Material.ENDER_EYE, 0, 15));
		
		m.addItem(new MenuItemNewLine());
		
		m.addItem(new MenuItemString("To Block", Material.STONE, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				if(Material.matchMaterial(value.toUpperCase()) != null)
					toType.setFlag(value.toUpperCase());
				else
                    fply.sendMessage("Invalid block type!", MinigameMessageType.ERROR);
			}
			
			@Override
			public String getValue() {
				return toType.getFlag();
			}
		}));
		m.addItem(toData.getMenuItem("To Block Use Data?", Material.ENDER_PEARL));
		m.addItem(toDataValue.getMenuItem("To Block Data Value", Material.ENDER_EYE, 0, 15));
		m.addItem(keepAttachment.getMenuItem("Keep Attachment", Material.PISTON, MinigameUtils.stringToList("When on, and To Block Use Data is off;If the source and target block;types are both blocks that;attach to surfaces, this;attachment will be preserved")));
		m.displayMenu(player);
		return true;
	}

}
