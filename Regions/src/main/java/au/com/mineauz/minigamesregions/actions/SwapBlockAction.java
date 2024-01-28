package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.BlockDataFlag;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.recorder.RecorderData;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.Directional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class SwapBlockAction extends AbstractAction {
    private final BlockDataFlag matchType = new BlockDataFlag(Material.STONE.createBlockData(), "matchtype");
    private final BlockDataFlag toType = new BlockDataFlag(Material.COBBLESTONE.createBlockData(), "totype");
    private final BooleanFlag keepAttachment = new BooleanFlag(false, "keepattachment");

    @Override
    public @NotNull String getName() {
        return "SWAP_BLOCK";
    }

    @Override
    public @NotNull String getCategory() {
        return "Block Actions";
    }

    @Override
    public void describe(@NotNull Map<@NotNull String, @NotNull Object> out) {
        out.put("From", matchType.getFlag());

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
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer, @NotNull Region region) {
        debug(mgPlayer, region);
        for (int y = region.getFirstPoint().getBlockY(); y <= region.getSecondPoint().getBlockY(); y++) {
            for (int x = region.getFirstPoint().getBlockX(); x <= region.getSecondPoint().getBlockX(); x++) {
                for (int z = region.getFirstPoint().getBlockZ(); z <= region.getSecondPoint().getBlockZ(); z++) {
                    Block block = region.getFirstPoint().getWorld().getBlockAt(x, y, z);

                    if (block.getBlockData().getMaterial() == matchType.getFlag().getMaterial()) {

                        // Block matches, now replace it
                        // create a new instance of the  Materials default blockdata
                        BlockData newBlockData = toType.getFlag().getMaterial().createBlockData();
                        BlockFace facing = null;

                        if (keepAttachment.getFlag()) {
                            // Keep attachments if possible
                            BlockData data = block.getBlockData();
                            if (data instanceof Directional) {
                                facing = ((Directional) data).getFacing();
                            }
                        }
                        if (newBlockData instanceof Directional) {
                            ((Directional) newBlockData).setFacingDirection(facing);
                        }

                        RecorderData data = mgPlayer.getMinigame().getRecorderData();
                        data.addBlock(block, null);

                        // Update block type
                        block.setBlockData(newBlockData);
                    }
                }
            }
        }
    }

    @Override
    public void executeNodeAction(@NotNull MinigamePlayer mgPlayer,
                                  @NotNull Node node) {
        debug(mgPlayer, node);
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        matchType.saveValue(path, config);
        toType.saveValue(path, config);
        keepAttachment.saveValue(path, config);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        matchType.loadValue(path, config);
        toType.loadValue(path, config);
        keepAttachment.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, "Swap Block", mgPlayer);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.addItem(new MenuItemBlockData("Match Block", matchType.getFlag().getMaterial(), new Callback<>() {

            @Override
            public BlockData getValue() {
                return matchType.getFlag();
            }

            @Override
            public void setValue(BlockData value) {
                matchType.setFlag(value);
            }


        }));
        m.addItem(new MenuItemNewLine());
        m.addItem(new MenuItemBlockData("To Block", toType.getFlag().getMaterial(), new Callback<>() {

            @Override
            public BlockData getValue() {
                return toType.getFlag();
            }

            @Override
            public void setValue(BlockData value) {
                toType.setFlag(value);
            }


        }));
        m.addItem(keepAttachment.getMenuItem("Keep Attachment", Material.PISTON, List.of("When on, and To Block Use Data is off", "If the source and target block", "types are both blocks that", "attach to surfaces, this", "attachment will be preserved")));
        m.displayMenu(mgPlayer);
        return true;
    }

}
