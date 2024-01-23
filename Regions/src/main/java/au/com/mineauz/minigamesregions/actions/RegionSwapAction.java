package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.recorder.RecorderData;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.RegionModule;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import au.com.mineauz.minigamesregions.language.RegionPlaceHolderKey;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;

/**
 * This class provides the ability to swap the blocks in two regions that have the same size. If
 * swap region is set to true, it will switch these two regions blocks, if it is set to false, it
 * will replace the TO regions blocks with the FROM regions block.
 * <p>
 * It allows to have template regions that can be copied into game or two switch two regions.
 */
public class RegionSwapAction extends AbstractAction {
    private final StringFlag fromRegion = new StringFlag("", "fromRegion");
    private final StringFlag toRegion = new StringFlag("", "toRegion");
    private final BooleanFlag swapRegion = new BooleanFlag(true, "swapRegion");

    @Override
    public String getName() {
        return "REGION_SWAP_ACTION";
    }

    @Override
    public String getCategory() {
        return "Block Actions";
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("From: ", fromRegion.getFlag());
        out.put("To: ", toRegion.getFlag());
        out.put("Swap: ", swapRegion.getFlag());

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
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer, @NotNull Region region) {
        debug(mgPlayer, region);

    }

    /**
     * This method loops through all blocks in either region and saves their BlockState. If both
     * regions are the same size, it will switch the regions blocks or replace the To (target) regions
     * block with the From (start) regions block.
     */
    @Override
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer, @NotNull Node node) {
        debug(mgPlayer, node);

        Region startRegion = null;
        Region targetRegion = null;
        ArrayList<BlockState> startRegionBlocks = new ArrayList<>();
        ArrayList<BlockState> targetRegionBlocks = new ArrayList<>();

        if (mgPlayer == null || !mgPlayer.isInMinigame()) {
            return;
        }
        Minigame mgm = mgPlayer.getMinigame();

        if (mgm != null) {
            RegionModule rmod = RegionModule.getMinigameModule(mgm);

            if (rmod.hasRegion(fromRegion.getFlag())) {
                startRegion = rmod.getRegion(fromRegion.getFlag());
            } else {
                MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.ERROR, RegionMessageManager.getBundleKey(),
                        RegionLangKey.ACTION_ERROR_NOREGION,
                        Placeholder.unparsed(RegionPlaceHolderKey.REGION.getKey(), fromRegion.getFlag()));
            }

            if (rmod.hasRegion(toRegion.getFlag())) {
                targetRegion = rmod.getRegion(toRegion.getFlag());
            } else {
                MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.ERROR, RegionMessageManager.getBundleKey(),
                        RegionLangKey.ACTION_ERROR_NOREGION,
                        Placeholder.unparsed(RegionPlaceHolderKey.REGION.getKey(), toRegion.getFlag()));
            }
        }

        if (startRegion != null && targetRegion != null) {

            fillRegionBlockList(startRegion, startRegionBlocks);

            fillRegionBlockList(targetRegion, targetRegionBlocks);

            if (startRegionBlocks.size() == targetRegionBlocks.size() && swapRegion.getFlag()) {
                for (int i = 0; i < targetRegionBlocks.size(); i++) {
                    RecorderData data = mgPlayer.getMinigame().getRecorderData();
                    data.addBlock(targetRegionBlocks.get(i).getBlock(), null);
                    data.addBlock(startRegionBlocks.get(i).getBlock(), null);

                    Material tempType = targetRegionBlocks.get(i).getType();
                    BlockData tempData = targetRegionBlocks.get(i).getBlockData();

                    targetRegionBlocks.get(i).setType(startRegionBlocks.get(i).getType());
                    targetRegionBlocks.get(i).setBlockData(startRegionBlocks.get(i).getBlockData());
                    targetRegionBlocks.get(i).update(true, false);

                    startRegionBlocks.get(i).setType(tempType);
                    startRegionBlocks.get(i).setBlockData(tempData);
                    startRegionBlocks.get(i).update(true, false);
                }

            } else if (startRegionBlocks.size() == targetRegionBlocks.size()) {
                for (int i = 0; i < targetRegionBlocks.size(); i++) {
                    RecorderData data = mgPlayer.getMinigame().getRecorderData();
                    data.addBlock(targetRegionBlocks.get(i).getBlock(), null);
                    targetRegionBlocks.get(i).setType(startRegionBlocks.get(i).getType());
                    targetRegionBlocks.get(i).setBlockData(startRegionBlocks.get(i).getBlockData());
                    targetRegionBlocks.get(i).update(true, false);
                }
            } else {
                MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.ERROR, RegionMessageManager.getBundleKey(),
                        RegionLangKey.ACTION_REGIONSWAP_ERROR_SIZE);
            }
        }

    }

    private void fillRegionBlockList(Region targetRegion, ArrayList<BlockState> targetRegionBlocks) {
        for (int y = targetRegion.getFirstPoint().getBlockY(); y <= targetRegion.getSecondPoint().getBlockY(); y++) {
            for (int x = targetRegion.getFirstPoint().getBlockX(); x <= targetRegion.getSecondPoint().getBlockX(); x++) {
                for (int z = targetRegion.getFirstPoint().getBlockZ(); z <= targetRegion.getSecondPoint().getBlockZ(); z++) {
                    targetRegionBlocks.add(targetRegion.getFirstPoint().getWorld().getBlockAt(x, y, z).getState());
                }
            }
        }
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        fromRegion.saveValue(path, config);
        toRegion.saveValue(path, config);
        swapRegion.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        fromRegion.loadValue(path, config);
        toRegion.loadValue(path, config);
        swapRegion.loadValue(path, config);

    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, "Trigger Node", mgPlayer);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.addItem(fromRegion.getMenuItem("From Region Name", Material.ENDER_EYE));
        m.addItem(swapRegion.getMenuItem("Swap Regions?", Material.ENDER_PEARL));

        m.addItem(new MenuItemNewLine());
        m.addItem(toRegion.getMenuItem("To Region Name", Material.ENDER_EYE));

        m.displayMenu(mgPlayer);
        return true;
    }

}
