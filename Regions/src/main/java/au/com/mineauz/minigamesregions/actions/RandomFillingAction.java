package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.recorder.RecorderData;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/***
 * This action fills a region randomly with a new block. There are two modes. Either "replace all",
 * where every block in that region is either replaced by air or the chosen block, 
 * or "replace selective" where blocks in the region are only replaced by the chosen block. 
 *
 */
public class RandomFillingAction extends AbstractAction {
    private final StringFlag toType = new StringFlag("WOOL", "totype");
    private final IntegerFlag percentageChance = new IntegerFlag(50, "percentagechance");
    private final BooleanFlag replaceAll = new BooleanFlag(true, "replaceAll");

    @Override
    public @NotNull String getName() {
        return "RANDOM_FILLING";
    }

    @Override
    public @NotNull String getCategory() {
        return "Block Actions";
    }

    @Override
    public void describe(@NotNull Map<@NotNull String, @NotNull Object> out) {
        out.put("To", toType.getFlag());
        out.put("Chance", percentageChance.getFlag());
        out.put("Replace misses with air", replaceAll.getFlag());
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
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer,
                                    @NotNull Region region) {
        debug(mgPlayer, region);
        Location temp = region.getFirstPoint();
        Random rndGen = new Random();
        for (int y = region.getFirstPoint().getBlockY(); y <= region.getSecondPoint().getBlockY(); y++) {
            temp.setY(y);
            for (int x = region.getFirstPoint().getBlockX(); x <= region.getSecondPoint().getBlockX(); x++) {
                temp.setX(x);
                for (int z = region.getFirstPoint().getBlockZ(); z <= region.getSecondPoint().getBlockZ(); z++) {
                    temp.setZ(z);
                    int randomDraw = rndGen.nextInt(100);  //Generating a number between [0-99]
                    randomDraw++;                //Adding one to handle edge cases (0 %, 100 %) correctly.

                    RecorderData data = mgPlayer.getMinigame().getRecorderData();
                    data.addBlock(temp.getBlock(), null);

                    if (randomDraw <= percentageChance.getFlag()) {
                        temp.getBlock().setType(Material.getMaterial(toType.getFlag()), false);
                    } else if (replaceAll.getFlag()) {
                        temp.getBlock().setType(Material.AIR);
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
        toType.saveValue(path, config);
        percentageChance.saveValue(path, config);
        replaceAll.saveValue(path, config);

    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        toType.loadValue(path, config);
        percentageChance.loadValue(path, config);
        replaceAll.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(final @NotNull MinigamePlayer mgPlayer, Menu previous) {

        Menu m = new Menu(4, "Random Filling", mgPlayer);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);

        //The menu entry for the block that will be placed
        m.addItem(new MenuItemString("To Block", Material.COBBLESTONE, new Callback<>() {

            @Override
            public String getValue() {
                return toType.getFlag();
            }

            @Override
            public void setValue(@NotNull String value) {
                if (Material.matchMaterial(value) != null) {
                    toType.setFlag(value.toUpperCase());
                } else {
                    MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.ERROR, RegionMessageManager.getBundleKey(),
                            RegionLangKey.ACTION_ERROR_INVALIDMATERIAL);
                }
            }

        }) {
            @Override
            public @NotNull ItemStack getDisplayItem() {
                ItemStack stack = super.getDisplayItem();
                Material m = Material.getMaterial(toType.getFlag());
                stack.setType(Objects.requireNonNullElse(m, Material.COBBLESTONE));
                return stack;
            }
        });

        //Percentage of blocks that will replaced
        m.addItem(new MenuItemNewLine());
        m.addItem(
                new MenuItemInteger("Chance in integer percentage (0-100)", List.of(""),
                        Material.BOOK, new Callback<>() {

                    @Override
                    public Integer getValue() {
                        return percentageChance.getFlag();
                    }

                    @Override
                    public void setValue(Integer value) {
                        percentageChance.setFlag(value);
                    }

                }, 0, 100));

        //Replace all or replace selectively
        m.addItem(new MenuItemNewLine());
        m.addItem(replaceAll.getMenuItem("Replace misses with air?", Material.ENDER_PEARL));

        m.displayMenu(mgPlayer);

        return false;
    }

}
