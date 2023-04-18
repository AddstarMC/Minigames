package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.blockRecorder.RecorderData;
import au.com.mineauz.minigames.config.MaterialFlag;
import au.com.mineauz.minigames.config.MaterialListFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides the methods necessary to fill a gameboard with pairs of randomly placed
 * blocks. It's a region action and can as such only run inside a region.
 * <p>
 * The user can define two options in the menu. A) the matchBlock, the block that is the placeholder
 * in the game and which will be replaced by the random blocks B) the blacklist, which removes
 * blocks from the given blockPool to provide a free choice in gameboard design. Removed blocks will
 * not appear on the gameboard. The blacklist follows this format:
 * block:data,block2,block3,block4:data4 (no spaces) If the block data is provided, only precise
 * matches with that block will be removed. If the block data is not provided, all blocks with that
 * name will be removed. For example, if the entry is WOOL, all WOOL blocks, no matter the color
 * will be removed, but if the entry is WOOL:1, only orange WOOL will be removed.
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
    blockPool.add(Material.COPPER_BLOCK);
    blockPool.add(Material.COAL_BLOCK);
    blockPool.add(Material.WEATHERED_COPPER);

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
    blockPool.add(Material.DARK_OAK_LOG);
    blockPool.add(Material.STRIPPED_DARK_OAK_LOG);
    blockPool.add(Material.CRIMSON_STEM);
    blockPool.add(Material.STRIPPED_CRIMSON_STEM);
    blockPool.add(Material.WARPED_STEM);
    blockPool.add(Material.STRIPPED_WARPED_STEM);

    //Planks
    blockPool.addAll(Tag.PLANKS.getValues());

    //Misc
    blockPool.add(Material.BRICKS);
    blockPool.add(Material.PRISMARINE);
    blockPool.add(Material.SEA_LANTERN);
    blockPool.add(Material.SANDSTONE);
    blockPool.add(Material.RED_SANDSTONE);
    blockPool.add(Material.STONE_BRICKS);
    blockPool.add(Material.NETHER_BRICKS);
    blockPool.add(Material.RED_NETHER_BRICKS);
    blockPool.add(Material.STONE);
    blockPool.add(Material.DIRT);
    blockPool.add(Material.SCULK);

    //todo more block types
  }

  private final MaterialFlag matchType = new MaterialFlag(Material.COBBLESTONE, "matchtype");
  private final MaterialListFlag blacklist = new MaterialListFlag(new ArrayList<>(), "blacklist");

  /**
   * Returns an array of Material that will consist of all blocks of the block pool minus the
   * ones on the blacklist. The blacklist string format is block1,block2,block4.
   *
   * @return ArrayList<PhantomBlock>
   */
  private ArrayList<Material> cleanUpBlockPool() {
    if (blacklist.getFlag().isEmpty()) {
      return blockPool;
    }

    ArrayList<Material> output = (new ArrayList<>(blockPool));
    output.removeAll(blacklist.getFlag());

    return output;
  }


  @Override
  public String getName() {
    return "MEMORY_SWAP_BLOCK";
  }

  @Override
  public String getCategory() {
    return "Block Actions";
  }

  @Override
  public void describe(Map<String, Object> out) {
    out.put("From: ", matchType.getFlag());
    out.put("Block pool size", blockPool.size());
    out.put("Blacklist", blacklist.getFlag());
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
  public void executeRegionAction(MinigamePlayer player, Region region) {
    debug(player, region);
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
      player.sendMessage(
              "This gameboard has an odd amount of playing fields, there will be unmatched blocks",
              MinigameMessageType.ERROR);
    }
    if (blocksToSwap.size() > 2 * localMatPool.size()) {
      player.sendMessage(
              "This gameboard has more fields then supported by the pool of available blocks (2 * "
                      + localMatPool.size() + "), there will remain not swapped blocks",
              MinigameMessageType.ERROR);
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
        RecorderData data = player.getMinigame().getRecorderData();
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
  public void executeNodeAction(MinigamePlayer player,
                                Node node) {
    debug(player, node);
  }


  @Override
  public void saveArguments(FileConfiguration config, String path) {
    matchType.saveValue(path, config);
    blacklist.saveValue(path, config);

  }

  @Override
  public void loadArguments(FileConfiguration config, String path) {
    matchType.loadValue(path, config);
    blacklist.loadValue(path, config);

  }

  @Override
  public boolean displayMenu(MinigamePlayer player, Menu previous) {
    Menu m = new Menu(3, "Memory Swap Block", player);
    m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);

    //The menu entry for the from-block, aka the block that will be replaced
    m.addItem(new MenuItemMaterial("Match Block", matchType.getFlag(), new Callback<>() {

      @Override
      public Material getValue() {
        return matchType.getFlag();
      }

      @Override
      public void setValue(Material value) {
        matchType.setFlag(value);
      }


    }) {
      @Override
      public ItemStack getItem() {
        ItemStack stack = super.getItem();
        stack.setType(Objects.requireNonNullElse(matchType.getFlag(), Material.COBBLESTONE));
        return stack;
      }
    });

    //todo make a adder, getter and setter, writing the whole list down for every little change is not very user friendly
    //Menu entry for the blacklist entry, aka the blocks that will be removed from the block pool
    m.addItem(new MenuItemNewLine());
    m.addItem(new MenuItemString("Blacklist",
            MinigameUtils.stringToList("Format: WHITE_WOOL,OAK_LOG"), Material.BOOK, new Callback<>() {
      @Override
      public String getValue() {
        return blacklist.getFlag().stream().map(Enum::name).collect(Collectors.joining(","));
      }

      @Override
      public void setValue(String value) {
        if (value != null) {
          blacklist.setFlag(Arrays.stream(value.split(",")).map(String::trim).map(str -> {
            Material mat = Material.matchMaterial(str);

            //warn player if the given string was not a material.
            if (mat == null) {
              player.sendMessage("string '" + str + "' is not a valid Material. Skipping.", MinigameMessageType.ERROR);
            }

            return mat;
            //actually filter out broken Materials and finish to list
          }).filter(Objects::nonNull).toList());
        }
      }
    }));

    m.displayMenu(player);

    return false;
  }
}