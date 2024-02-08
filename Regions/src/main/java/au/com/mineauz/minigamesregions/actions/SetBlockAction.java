package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.BlockDataFlag;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemBlockData;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

//todo
public class SetBlockAction extends AAction {
    private final BlockDataFlag type = new BlockDataFlag(Material.STONE.createBlockData(), "type");
    private final BooleanFlag useBlockData = new BooleanFlag(false, "usedur");//todo rename flag
    private final IntegerFlag dur = new IntegerFlag(0, "dur");

    protected SetBlockAction(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayname() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_ACTION_SETBLOCK_NAME);
    }

    @Override
    public @NotNull IActionCategory getCategory() {
        return RegionActionCategories.BLOCK;
    }

    @Override
    public void describe(@NotNull Map<@NotNull String, @NotNull Object> out) {
        if (useBlockData.getFlag()) {
            out.put("Block", type.getFlag() + ":" + dur.getFlag());
        } else {
            out.put("Block", type.getFlag());
        }
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
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer,
                                    @NotNull Region region) {
        debug(mgPlayer, region);
        Location temp = region.getFirstPoint();
        for (int y = region.getFirstPoint().getBlockY(); y <= region.getSecondPoint().getBlockY(); y++) {
            temp.setY(y);
            for (int x = region.getFirstPoint().getBlockX(); x <= region.getSecondPoint().getBlockX(); x++) {
                temp.setX(x);
                for (int z = region.getFirstPoint().getBlockZ(); z <= region.getSecondPoint().getBlockZ(); z++) {
                    temp.setZ(z);

                    BlockState bs = temp.getBlock().getState();
                    if (useBlockData.getFlag()) {
                        bs.setBlockData(type.getFlag());
                    } else {
                        bs.setBlockData(type.getFlag().getMaterial().createBlockData());
                    }
                    bs.update(true);
                }
            }
        }
    }

    @Override
    public void executeNodeAction(@NotNull MinigamePlayer mgPlayer,
                                  @NotNull Node node) {
        debug(mgPlayer, node);
        BlockState bs = node.getLocation().getBlock().getState();
        if (useBlockData.getFlag()) {
            bs.setBlockData(type.getFlag());
        } else {
            bs.setBlockData(type.getFlag().getMaterial().createBlockData());
        }
        bs.update(true);
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        type.saveValue(path, config);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        type.loadValue(path, config);
        useBlockData.loadValue(path, config);
        dur.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, getDisplayname(), mgPlayer);
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);
        m.addItem(new MenuItemBlockData(Material.STONE, "Type", new Callback<>() {
            @Override
            public BlockData getValue() {
                return type.getFlag();
            }

            @Override
            public void setValue(BlockData value) {
                type.setFlag(value);
            }
        }));
        m.addItem(useBlockData.getMenuItem("Use Specific BlockData", Material.ENDER_PEARL));
        m.displayMenu(mgPlayer);
        return true;
    }

}
