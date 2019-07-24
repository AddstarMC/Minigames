package au.com.mineauz.minigames.minigame;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.stats.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreboardDisplay {
    public static final int defaultWidth = 3;
    public static final int defaultHeight = 3;
    private final Location rootBlock;
    private MinigameStat stat;
    private StatValueField field;
    private ScoreboardOrder order;
    private final Minigame minigame;
    private final int width;
    private final int height;
    private final BlockFace facing;

    private StatSettings settings;

    private List<StoredStat> stats;

    private boolean needsLoad;

    public ScoreboardDisplay(Minigame minigame, int width, int height, Location rootBlock, BlockFace facing) {
        this.minigame = minigame;
        this.width = width;
        this.height = height;
        this.rootBlock = rootBlock;
        this.facing = facing;

        // Default values
        stat = MinigameStats.Wins;
        field = StatValueField.Total;
        order = ScoreboardOrder.DESCENDING;

        stats = Lists.newArrayListWithCapacity(width * height * 2);
        needsLoad = true;
    }

    public static ScoreboardDisplay load(Minigame minigame, ConfigurationSection section) {
        int width = section.getInt("width");
        int height = section.getInt("height");
        Location location = MinigameUtils.loadShortLocation(section.getConfigurationSection("location"));
        BlockFace facing = BlockFace.valueOf(section.getString("dir"));

        // from invalid world
        if (location == null) {
            return null;
        }

        ScoreboardDisplay display = new ScoreboardDisplay(minigame, width, height, location, facing);
        display.setOrder(ScoreboardOrder.valueOf(section.getString("order")));
        MinigameStat stat = MinigameStats.getStat(section.getString("stat", "wins"));
        StatValueField field = StatValueField.valueOf(section.getString("field", "Total"));
        display.setStat(stat, field);
        Block block = location.getBlock();
        block.setMetadata("MGScoreboardSign", new FixedMetadataValue(Minigames.getPlugin(), true));
        block.setMetadata("Minigame", new FixedMetadataValue(Minigames.getPlugin(), minigame));

        return display;
    }

    public Location getRoot() {
        return rootBlock;
    }

    public MinigameStat getStat() {
        return stat;
    }

    public StatValueField getField() {
        return field;
    }

    public void setStat(MinigameStat stat, StatValueField field) {
        this.stat = stat;
        this.field = field;
    }

    public ScoreboardOrder getOrder() {
        return order;
    }

    public void setOrder(ScoreboardOrder order) {
        this.order = order;
        stats.clear();
        needsLoad = true;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Minigame getMinigame() {
        return minigame;
    }

    public BlockFace getFacing() {
        return facing;
    }

    public boolean needsLoad() {
        return needsLoad;
    }

    private List<Block> getSignBlocks(boolean onlySigns) {
        // Find the horizontal direction (going across the the signs, left to right)
        BlockFace horizontal;

        switch (facing) {
            case NORTH:
                horizontal = BlockFace.WEST;
                break;
            case SOUTH:
                horizontal = BlockFace.EAST;
                break;
            case WEST:
                horizontal = BlockFace.SOUTH;
                break;
            case EAST:
                horizontal = BlockFace.NORTH;
                break;
            default:
                throw new AssertionError("Invalid facing " + facing);
        }

        List<Block> blocks = Lists.newArrayListWithCapacity(width * height);

        // Find the corner that is the top left part of the scoreboard
        Location min = rootBlock.clone();
        min.add(-horizontal.getModX() * (width / 2), -1, -horizontal.getModZ() * (width / 2));

        // Grab each sign of the scoreboards in order
        Block block = min.getBlock();

        for (int y = 0; y < height; ++y) {
            Block start = block;
            for (int x = 0; x < width; ++x) {
                // Only add signs
                if (block.getType() == Material.OAK_WALL_SIGN || (!onlySigns && block.getType() == Material.AIR)) {
                    blocks.add(block);
                }

                block = block.getRelative(horizontal);
            }
            block = start.getRelative(BlockFace.DOWN);
        }

        return blocks;
    }

    /**
     * Updates all signs with the current values of the stats
     */
    public void updateSigns() {
        settings = minigame.getSettings(stat);

        placeRootSign();

        List<Block> signs = getSignBlocks(true);

        int nextIndex = 0;
        for (Block sign : signs) {
            if (nextIndex <= stats.size() - 2) {
                updateSign(sign, nextIndex + 1, stats.get(nextIndex++), stats.get(nextIndex++));
            } else if (nextIndex <= stats.size() - 1) {
                updateSign(sign, nextIndex + 1, stats.get(nextIndex++));
            } else {
                clearSign(sign);
            }
        }
    }

    private void updateSign(Block block, int place, StoredStat... stats) {
        Preconditions.checkArgument(stats.length >= 1 && stats.length <= 2);

        Sign sign = (Sign) block.getState();
        sign.setLine(0, MinigameUtils.limitIgnoreCodes(ChatColor.GREEN + String.valueOf(place) + ". " + ChatColor.BLACK + stats[0].getPlayerDisplayName(), 15));
        sign.setLine(1, MinigameUtils.limitIgnoreCodes(ChatColor.BLUE + stat.displayValueSign(stats[0].getValue(), settings), 15));

        if (stats.length == 2) {
            ++place;
            sign.setLine(2, MinigameUtils.limitIgnoreCodes(ChatColor.GREEN + String.valueOf(place) + ". " + ChatColor.BLACK + stats[1].getPlayerDisplayName(), 15));
            sign.setLine(3, MinigameUtils.limitIgnoreCodes(ChatColor.BLUE + stat.displayValueSign(stats[1].getValue(), settings), 15));
        } else {
            sign.setLine(2, "");
            sign.setLine(3, "");
        }

        sign.update();
    }

    public void displayMenu(MinigamePlayer player) {
        final Menu setupMenu = new Menu(3, "Setup Scoreboard", player);

        StatSettings settings = minigame.getSettings(stat);
        final MenuItemCustom statisticChoice = new MenuItemCustom("Statistic", Material.WRITABLE_BOOK);
        statisticChoice.setDescription(Collections.singletonList(ChatColor.GREEN + settings.getDisplayName()));

        final MenuItemCustom fieldChoice = new MenuItemCustom("Statistic Field", Material.PAPER);
        fieldChoice.setDescription(Collections.singletonList(ChatColor.GREEN + field.getTitle()));

        statisticChoice.setClick(object -> {
            Menu childMenu = MinigameStats.createStatSelectMenu(setupMenu, new Callback<MinigameStat>() {
                @Override
                public MinigameStat getValue() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void setValue(MinigameStat value) {
                    stat = value;
                    StatSettings settings12 = minigame.getSettings(stat);
                    statisticChoice.setDescription(Collections.singletonList(ChatColor.GREEN + settings12.getDisplayName()));

                    // Check that the field is valid
                    StatValueField first = null;
                    boolean valid = false;
                    for (StatValueField sfield : settings12.getFormat().getFields()) {
                        if (first == null) {
                            first = sfield;
                        }

                        if (sfield == field) {
                            valid = true;
                            break;
                        }
                    }

                    // Update the field
                    if (!valid) {
                        field = first;
                        fieldChoice.setDescription(Collections.singletonList(ChatColor.GREEN + value.toString()));
                    }
                }
            });

            childMenu.displayMenu(setupMenu.getViewer());
            return null;
        });

        fieldChoice.setClick(object -> {
            StatSettings settings1 = minigame.getSettings(stat);
            Menu childMenu = MinigameStats.createStatFieldSelectMenu(setupMenu, settings1.getFormat(), new Callback<StatValueField>() {
                @Override
                public StatValueField getValue() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void setValue(StatValueField value) {
                    field = value;
                    fieldChoice.setDescription(Collections.singletonList(ChatColor.GREEN + value.getTitle()));
                }
            });

            childMenu.displayMenu(setupMenu.getViewer());
            return null;
        });

        setupMenu.addItem(statisticChoice);
        setupMenu.addItem(fieldChoice);

        List<String> sbotypes = new ArrayList<>();
        for (ScoreboardOrder o : ScoreboardOrder.values()) {
            sbotypes.add(o.toString().toLowerCase());
        }
        setupMenu.addItem(new MenuItemList("Scoreboard Order", Material.ENDER_PEARL, new Callback<String>() {

            @Override
            public String getValue() {
                return order.toString().toLowerCase();
            }            @Override
            public void setValue(String value) {
                order = ScoreboardOrder.valueOf(value.toUpperCase());
            }


        }, sbotypes));
        setupMenu.addItem(new MenuItemScoreboardSave("Create Scoreboard", MenuUtility.getCreateMaterial(), this), setupMenu.getSize() - 1);
        setupMenu.displayMenu(player);
    }

    private void clearSign(Block block) {
        Sign sign = (Sign) block.getState();
        sign.setLine(0, "");
        sign.setLine(1, "");
        sign.setLine(2, "");
        sign.setLine(3, "");
        sign.update();
    }

    public void deleteSigns() {
        List<Block> blocks = getSignBlocks(true);

        for (Block block : blocks) {
            block.setType(Material.AIR);
        }
    }

    public void placeSigns() {
        List<Block> blocks = getSignBlocks(false);

        for (Block block : blocks) {
            block.setType(Material.OAK_WALL_SIGN);
            Directional d = (Directional) block.getBlockData();
            d.setFacing(facing);
            block.setBlockData(d);
        }
    }

    public void save(ConfigurationSection section) {
        section.set("height", height);
        section.set("width", width);
        section.set("dir", facing.name());
        section.set("stat", stat.getName());
        section.set("field", field.name());
        section.set("order", order.name());
        MinigameUtils.saveShortLocation(section.createSection("location"), rootBlock);
    }

    public void placeRootSign() {
        // For external calls
        if (settings == null) {
            settings = minigame.getSettings(stat);
        }

        Block root = rootBlock.getBlock();
        if (root.getType() == Material.OAK_WALL_SIGN || root.getType() == Material.OAK_SIGN) {
            BlockState state = root.getState();
            if (state instanceof Sign) {
                Sign sign = (Sign) state;

                sign.setLine(0, ChatColor.BLUE + minigame.getName(true));
                sign.setLine(1, ChatColor.GREEN + settings.getDisplayName());
                sign.setLine(2, ChatColor.GREEN + field.getTitle());
                sign.setLine(3, "(" + WordUtils.capitalize(order.toString()) + ")");
                sign.update();

                sign.setMetadata("MGScoreboardSign", new FixedMetadataValue(Minigames.getPlugin(), true));
                sign.setMetadata("Minigame", new FixedMetadataValue(Minigames.getPlugin(), minigame));
            } else {
                Minigames.getPlugin().getLogger().warning("No Root Sign Block at: " + root.getLocation().toString());
            }
        } else {
            Minigames.getPlugin().getLogger().warning("No Root Sign Block at: " + root.getLocation().toString());
        }
    }

    public void reload() {
        needsLoad = false;
        ListenableFuture<List<StoredStat>> future = Minigames.getPlugin().getBackend().loadStats(minigame, stat, field, order, 0, width * height * 2);
        Minigames.getPlugin().getBackend().addServerThreadCallback(future, getUpdateCallback());
    }

    // The update callback to be provided to the future. MUST be executed on the bukkit server thread
    private FutureCallback<List<StoredStat>> getUpdateCallback() {
        return new FutureCallback<List<StoredStat>>() {
            @Override
            public void onSuccess(List<StoredStat> result) {
                stats = result;
                needsLoad = false;
                updateSigns();
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                stats = Collections.emptyList();
                needsLoad = true;
            }
        };
    }
}
