package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.MaterialFlag;
import au.com.mineauz.minigames.config.MaterialListFlag;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.recorder.RecorderData;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

;

/**
 * This class provides the methods necessary to fill a gameboard with pairs of randomly placed
 * blocks. It's a region action and can as such only run inside a region.
 * <p>
 * The user can define two options in the menu. A) the matchBlock, the block that is the placeholder
 * in the game and which will be replaced by the random blocks B) the white/blacklist, which restricts or removes
 * blocks from the given blockPool to provide a free choice in gameboard design. Removed blocks will
 * not appear on the game board.
 */
public class MemorySwapBlockAction extends AbstractAction {
    /*
     * Building a blockPool to provide the blocks that could be used in the game.
     */
    private static final ArrayList<Material> blockPool = new ArrayList<>();

    /*
     * Filling the block pool with blocks than can be pulled and pushed by pistons manually
     */
    static {
        /* TODO Maybe an automatic way of dealing with this. Problem: some curation is necessary
         * to prevent blocks that are to visual similar to appear, for example quartz and white
         * concrete. Letting the user sort this out with the blacklist results in a very long
         * blacklist string, which is annoying for the user.
         */

        //Resource blocks
        blockPool.addAll(Tag.BEACON_BASE_BLOCKS.getValues());
        blockPool.add(Material.COAL_BLOCK);
        blockPool.add(Material.WAXED_CUT_COPPER);
        blockPool.add(Material.WAXED_EXPOSED_COPPER);
        blockPool.add(Material.WAXED_WEATHERED_CUT_COPPER);
        blockPool.add(Material.WAXED_OXIDIZED_COPPER);

        //Concrete
        blockPool.add(Material.WHITE_CONCRETE);
        blockPool.add(Material.ORANGE_CONCRETE);
        blockPool.add(Material.MAGENTA_CONCRETE);
        blockPool.add(Material.LIGHT_BLUE_CONCRETE);
        blockPool.add(Material.YELLOW_CONCRETE);
        blockPool.add(Material.LIME_CONCRETE);
        blockPool.add(Material.PINK_CONCRETE);
        blockPool.add(Material.GRAY_CONCRETE);
        blockPool.add(Material.LIGHT_GRAY_CONCRETE);
        blockPool.add(Material.CYAN_CONCRETE);
        blockPool.add(Material.PURPLE_CONCRETE);
        blockPool.add(Material.BLUE_CONCRETE);
        blockPool.add(Material.BROWN_CONCRETE);
        blockPool.add(Material.GREEN_CONCRETE);
        blockPool.add(Material.RED_CONCRETE);
        blockPool.add(Material.BLACK_CONCRETE);

        //Ore blocks
        blockPool.addAll(Tag.DIAMOND_ORES.getValues());
        blockPool.addAll(Tag.IRON_ORES.getValues());
        blockPool.addAll(Tag.REDSTONE_ORES.getValues());
        blockPool.addAll(Tag.EMERALD_ORES.getValues());
        blockPool.addAll(Tag.GOLD_ORES.getValues());
        blockPool.addAll(Tag.LAPIS_ORES.getValues());
        blockPool.add(Material.NETHER_QUARTZ_ORE);
        blockPool.add(Material.ANCIENT_DEBRIS);

        //Wool blocks
        blockPool.addAll(Tag.WOOL.getValues());

        //Logs
        blockPool.add(Material.OAK_LOG);
        blockPool.add(Material.STRIPPED_OAK_LOG);
        blockPool.add(Material.SPRUCE_LOG);
        blockPool.add(Material.STRIPPED_SPRUCE_LOG);
        blockPool.add(Material.BIRCH_LOG);
        blockPool.add(Material.STRIPPED_BIRCH_LOG);
        blockPool.add(Material.JUNGLE_LOG);
        blockPool.add(Material.STRIPPED_JUNGLE_LOG);
        blockPool.add(Material.ACACIA_LOG);
        blockPool.add(Material.STRIPPED_ACACIA_LOG);
        blockPool.add(Material.MANGROVE_LOG);
        blockPool.add(Material.STRIPPED_MANGROVE_LOG);
        blockPool.add(Material.DARK_OAK_LOG);
        blockPool.add(Material.STRIPPED_DARK_OAK_LOG);
        blockPool.add(Material.CRIMSON_STEM);
        blockPool.add(Material.STRIPPED_CRIMSON_STEM);
        blockPool.add(Material.WARPED_STEM);
        blockPool.add(Material.STRIPPED_WARPED_STEM);

        //Planks
        blockPool.addAll(Tag.PLANKS.getValues());

        //Stone-alike
        blockPool.add(Material.STONE);
        blockPool.add(Material.SMOOTH_STONE);
        blockPool.add(Material.CHISELED_STONE_BRICKS);
        blockPool.add(Material.COBBLESTONE);
        blockPool.add(Material.MOSSY_COBBLESTONE);
        blockPool.add(Material.STONE_BRICKS);
        blockPool.add(Material.BRICKS);
        blockPool.add(Material.BASALT);
        blockPool.add(Material.CALCITE);
        blockPool.add(Material.TUFF);
        blockPool.add(Material.DRIPSTONE_BLOCK);
        blockPool.add(Material.SMOOTH_BASALT);
        blockPool.add(Material.POLISHED_BASALT);
        blockPool.add(Material.POLISHED_ANDESITE);
        blockPool.add(Material.CHISELED_DEEPSLATE);
        blockPool.add(Material.POLISHED_DEEPSLATE);
        blockPool.add(Material.DEEPSLATE_BRICKS);
        blockPool.add(Material.DEEPSLATE);
        blockPool.add(Material.DEEPSLATE_TILES);
        blockPool.add(Material.POLISHED_BLACKSTONE);
        blockPool.add(Material.GILDED_BLACKSTONE);
        blockPool.add(Material.CHISELED_POLISHED_BLACKSTONE);
        blockPool.add(Material.NETHERRACK);
        blockPool.add(Material.NETHER_BRICKS);
        blockPool.add(Material.RED_NETHER_BRICKS);
        blockPool.add(Material.SMOOTH_QUARTZ);
        blockPool.add(Material.CHISELED_QUARTZ_BLOCK);
        blockPool.add(Material.QUARTZ_BRICKS);
        blockPool.add(Material.QUARTZ_PILLAR);
        blockPool.add(Material.PURPUR_BLOCK);
        blockPool.add(Material.PURPUR_PILLAR);
        blockPool.add(Material.END_STONE_BRICKS);

        //dirt alike
        blockPool.add(Material.DIRT);
        blockPool.add(Material.MUD);
        blockPool.add(Material.PODZOL);
        blockPool.add(Material.CLAY);
        blockPool.add(Material.SOUL_SAND);
        blockPool.add(Material.SOUL_SOIL);
        blockPool.add(Material.PACKED_MUD);
        blockPool.add(Material.MUD_BRICKS);
        blockPool.add(Material.SANDSTONE);
        blockPool.add(Material.RED_SANDSTONE);
        blockPool.add(Material.AMETHYST_BLOCK);

        //kinda living
        blockPool.add(Material.SCULK);
        blockPool.add(Material.BONE_BLOCK);
        blockPool.add(Material.NETHER_WART_BLOCK);
        blockPool.add(Material.WARPED_WART_BLOCK);
        blockPool.add(Material.SHROOMLIGHT);
        blockPool.add(Material.DRIED_KELP_BLOCK);
        blockPool.add(Material.DEAD_BRAIN_CORAL_BLOCK);
        blockPool.add(Material.SPONGE);
        blockPool.add(Material.HONEYCOMB_BLOCK);
        blockPool.add(Material.OCHRE_FROGLIGHT);
        blockPool.add(Material.VERDANT_FROGLIGHT);
        blockPool.add(Material.PEARLESCENT_FROGLIGHT);

        //elements
        blockPool.add(Material.PACKED_ICE);
        blockPool.add(Material.BLUE_ICE);
        blockPool.add(Material.SNOW_BLOCK);
        blockPool.add(Material.MAGMA_BLOCK);
        blockPool.add(Material.PRISMARINE_BRICKS);
        blockPool.add(Material.DARK_PRISMARINE);
        blockPool.add(Material.SEA_LANTERN);

        //usage blocks
        blockPool.add(Material.CRAFTING_TABLE);
        blockPool.add(Material.FLETCHING_TABLE);
        blockPool.add(Material.SMITHING_TABLE);
        blockPool.add(Material.BOOKSHELF);

        //todo config for this, also move this standard list into a ressource file
    }

    private final MaterialFlag matchType = new MaterialFlag(Material.COBBLESTONE, "matchtype");
    private final MaterialListFlag wbList = new MaterialListFlag(new ArrayList<>(), "config.blacklist");
    // is it a white or a blacklist?
    private final BooleanFlag whitelistMode = new BooleanFlag(false, "whitelistmode");

    /**
     * Returns an array of Material that will consist of all blocks of the block pool minus the
     * ones on the blacklist. The blacklist string format is block1,block2,block4.
     *
     * @return ArrayList<PhantomBlock>
     */
    private ArrayList<Material> cleanUpBlockPool() {
        if (wbList.getFlag().isEmpty()) {
            return blockPool;
        }

        ArrayList<Material> output;
        if (whitelistMode.getFlag()) {
            output = blockPool.stream().filter(m -> wbList.getFlag().contains(m)).collect(Collectors.toCollection(ArrayList::new));
        } else {
            output = new ArrayList<>(blockPool);
            output.removeAll(wbList.getFlag());
        }

        return output;
    }

    @Override
    public @NotNull String getName() {
        return "MEMORY_SWAP_BLOCK";
    }

    @Override
    public @NotNull String getCategory() {
        return "Block Actions";
    }

    @Override
    public void describe(@NotNull Map<@NotNull String, @NotNull Object> out) {
        out.put("From: ", matchType.getFlag());
        out.put("Block pool size", blockPool.size());
        out.put("Whitelist mode", whitelistMode.getFlag()); //todo this can be quite long, maybe cut it off
        out.put("White/Blacklist", wbList.getFlag());
    }

    @Override
    public boolean useInRegions() {
        return true;
    }

    @Override
    public boolean useInNodes() {
        return false;
    }

    /**
     * This will search for a certain type of block (user definable over the menu) and replaced with a
     * random block from the block pool minus the blacklisted blocks (also user definable over the
     * menu).
     * <p>
     * The block will always have a pair unless there is an odd number of blocks to replace. If there
     * is an odd numbered amount of blocks there will be one unmatched block and player will be
     * warned. If there are more blocks to replace than it is possible, the surplus blocks will be
     * skipped and the player will be warned.
     */
    @Override
    public void executeRegionAction(MinigamePlayer mgPlayer, @NotNull Region region) {
        debug(mgPlayer, region);
        ArrayList<Material> localMatPool = cleanUpBlockPool();

        ArrayList<Block> blocksToSwap = new ArrayList<>();

        //Collects all blocks to be swapped
        for (int y = (int) region.getMinY(); y <= region.getMaxY(); y++) {
            for (int x = (int) region.getMinX(); x <= region.getMaxX(); x++) {
                for (int z = (int) region.getMinZ(); z <= region.getMaxZ(); z++) {
                    Block block = region.getFirstPoint().getWorld().getBlockAt(x, y, z);

                    if (block.getType() == matchType.getFlag()) {
                        blocksToSwap.add(block);
                    }
                }
            }
        }

        //Sanity checks that can be handled without throwing an exception but need a warning to player
        if (blocksToSwap.size() % 2 != 0) {
            if (mgPlayer != null) {
                MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.ERROR, RegionMessageManager.getBundleKey(),
                        RegionLangKey.ACTION_MEMORYSWAPBLOCK_ERROR_ODD);
            } else {
                RegionMessageManager.debugMessage("This game board of \"" + region.getName() + "\" has an odd amount of playing fields, there will be unmatched blocks!");
            }
        }
        if (blocksToSwap.size() > 2 * localMatPool.size()) {
            if (mgPlayer != null) {
                MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.ERROR, RegionMessageManager.getBundleKey(),
                        RegionLangKey.ACTION_MEMORYSWAPBLOCK_ERROR_TOOBIG,
                        Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(localMatPool.size())));
            }
        }


        // make a random collection of used materials
        if ((2 * localMatPool.size()) > blocksToSwap.size())
            Collections.shuffle(localMatPool);

        //shuffle blocks to swap, to make picking 2 random ones easy.
        Collections.shuffle(blocksToSwap);

        //iterator to iterate through without an extra loop
        Iterator<Material> matIt = localMatPool.iterator();

        // to stop in case of uneven size
        final int max = blocksToSwap.size() - 1;

        // for every 2 blocks of the list, set a random material
        for (int i = 0; i < max; i += 2) {
            if (matIt.hasNext()) {
                //save block data in recorder
                RecorderData data = mgPlayer.getMinigame().getRecorderData();
                data.addBlock(blocksToSwap.get(i), null);
                data.addBlock(blocksToSwap.get(i + 1), null);

                Material newMat = matIt.next();
                blocksToSwap.get(i).setType(newMat);
                blocksToSwap.get(i + 1).setType(newMat);
            } else {
                break;
            }
        }
    }

    @Override
    public void executeNodeAction(@NotNull MinigamePlayer mgPlayer,
                                  @NotNull Node node) {
        debug(mgPlayer, node);
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config, @NotNull String path) {
        matchType.saveValue(path, config);
        wbList.saveValue(path, config);
        whitelistMode.saveValue(path, config);

    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config, @NotNull String path) {
        matchType.loadValue(path, config);
        wbList.loadValue(path, config);
        whitelistMode.loadValue(path, config);

    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, "Memory Swap Block", mgPlayer);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);

        //The menu entry for the from-block, aka the block that will be replaced
        m.addItem(matchType.getMenuItem("Match Block"));

        //Menu entry for the white/blacklist entry, aka the blocks that will be only accounted for / removed from the block pool
        m.addItem(new MenuItemNewLine());
        m.addItem(new MenuItemDisplayWhitelist("Block Whitelist/Blacklist", List.of("Blocks that can/can't", "used as memory."),
                Material.BOOK, wbList.getFlag(), new Callback<>() {

            @Override
            public Boolean getValue() {
                return whitelistMode.getFlag();
            }

            @Override
            public void setValue(Boolean value) {
                whitelistMode.setFlag(value);
            }
        }, List.of("If whitelist mode only", "added items can get", "used as memory.")));

        m.displayMenu(mgPlayer);

        return false;
    }
}