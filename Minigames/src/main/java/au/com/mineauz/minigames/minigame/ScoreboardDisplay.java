package au.com.mineauz.minigames.minigame;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.stats.*;
import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ScoreboardDisplay {
    public static final int defaultWidth = 3;
    public static final int defaultHeight = 3;
    private final Location rootBlock;
    private final Minigame minigame;
    private final int width;
    private final int height;
    private final BlockFace facing;
    private MinigameStat stat;
    private StatisticValueField field;
    private ScoreboardOrder order;
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
        stat = MinigameStatistics.Wins;
        field = StatisticValueField.Total;
        order = ScoreboardOrder.DESCENDING;

        stats = new ArrayList<>(width * height * 2);
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
        MinigameStat stat = MinigameStatistics.getStat(section.getString("stat", "wins"));
        StatisticValueField field = StatisticValueField.valueOf(section.getString("field", "Total"));
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

    public StatisticValueField getField() {
        return field;
    }

    public void setStat(MinigameStat stat, StatisticValueField field) {
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
        // Find the horizontal direction (going across the signs, left to right)
        BlockFace horizontal = switch (facing) {
            case NORTH -> BlockFace.WEST;
            case SOUTH -> BlockFace.EAST;
            case WEST -> BlockFace.SOUTH;
            case EAST -> BlockFace.NORTH;
            default -> throw new AssertionError("Invalid facing " + facing);
        };

        List<Block> blocks = new ArrayList<>(width * height);

        // Find the corner that is the top left part of the scoreboard
        Location min = rootBlock.clone();
        min.add(-horizontal.getModX() * (width / 2), -1, -horizontal.getModZ() * (width / 2));

        // Grab each sign of the scoreboards in order
        Block block = min.getBlock();

        for (int y = 0; y < height; ++y) {
            Block start = block;
            for (int x = 0; x < width; ++x) {
                // Only add signs
                if (Tag.WALL_SIGNS.isTagged(block.getType()) || (!onlySigns && block.getType() == Material.AIR)) {
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
        sign.getSide(Side.FRONT).line(0, MinigameUtils.limitIgnoreFormat(Component.text(place + ". ").color(NamedTextColor.GREEN).append(stats[0].getPlayerDisplayName().color(NamedTextColor.BLACK)), 15));
        sign.getSide(Side.FRONT).line(1, MinigameUtils.limitIgnoreFormat(stat.displayValueSign(stats[0].getValue(), settings).color(NamedTextColor.BLUE), 15));

        if (stats.length == 2) {
            ++place;
            sign.getSide(Side.FRONT).line(2, MinigameUtils.limitIgnoreFormat(Component.text(place + ". ").color(NamedTextColor.GREEN).append(stats[1].getPlayerDisplayName().color(NamedTextColor.BLACK)), 15));
            sign.getSide(Side.FRONT).line(3, MinigameUtils.limitIgnoreFormat(stat.displayValueSign(stats[1].getValue(), settings).color(NamedTextColor.BLUE), 15));
        } else {
            sign.getSide(Side.FRONT).setLine(2, "");
            sign.getSide(Side.FRONT).setLine(3, "");
        }

        sign.update();
    }

    public void displayMenu(MinigamePlayer player) {
        final Menu setupMenu = new Menu(3, MgMenuLangKey.MENU_SCOREBOARD_SETUP_NAME, player);

        StatSettings settings = minigame.getSettings(stat);
        final MenuItemCustom statisticChoice = new MenuItemCustom(Material.WRITABLE_BOOK, MgMenuLangKey.MENU_SCOREBOARD_STATISTIC_NAME,
                List.of(settings.getDisplayName().color(NamedTextColor.GREEN)));

        final MenuItemCustom fieldChoice = new MenuItemCustom(Material.PAPER, MgMenuLangKey.MENU_SCOREBOARD_STATISTIC_FIELD_NAME,
                List.of(field.getTitle().color(NamedTextColor.GREEN)));

        statisticChoice.setClick(() -> {
            Menu childMenu = MinigameStatistics.createStatSelectMenu(setupMenu, new Callback<>() {
                @Override
                public MinigameStat getValue() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void setValue(MinigameStat value) {
                    stat = value;
                    StatSettings settings12 = minigame.getSettings(stat);
                    statisticChoice.setBaseDescriptionPart(List.of(settings12.getDisplayName().color(NamedTextColor.GREEN)));

                    // Check that the field is valid
                    StatisticValueField first = null;
                    boolean valid = false;
                    for (StatisticValueField sfield : settings12.getFormat().getFields()) {
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
                        fieldChoice.setBaseDescriptionPart(List.of(value.getDisplayName().color(NamedTextColor.GREEN)));
                    }
                }
            });

            childMenu.displayMenu(setupMenu.getViewer());
            return null;
        });

        fieldChoice.setClick(() -> {
            StatSettings settings1 = minigame.getSettings(stat);
            Menu childMenu = MinigameStatistics.createStatFieldSelectMenu(setupMenu, settings1.getFormat(), new Callback<>() {
                @Override
                public StatisticValueField getValue() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void setValue(StatisticValueField value) {
                    field = value;
                    fieldChoice.setBaseDescriptionPart(List.of(value.getTitle().color(NamedTextColor.GREEN)));
                }
            });

            childMenu.displayMenu(setupMenu.getViewer());
            return null;
        });

        setupMenu.addItem(statisticChoice);
        setupMenu.addItem(fieldChoice);

        setupMenu.addItem(new MenuItemEnum<>(Material.ENDER_PEARL, MgMenuLangKey.MENU_SCOREBOARD_ORDER_NAME, new Callback<>() {

            @Override
            public ScoreboardOrder getValue() {
                return order;
            }

            @Override
            public void setValue(ScoreboardOrder value) {
                order = value;
            }
        }, ScoreboardOrder.class));

        setupMenu.addItem(new MenuItemScoreboardSave(MenuUtility.getCreateMaterial(), MgMenuLangKey.MENU_SCOREBOARD_CREATE_NAME, this),
                setupMenu.getSize() - 1);
        setupMenu.displayMenu(player);
    }

    private void clearSign(Block block) {
        Sign sign = (Sign) block.getState();
        sign.getSide(Side.FRONT).setLine(0, "");
        sign.getSide(Side.FRONT).setLine(1, "");
        sign.getSide(Side.FRONT).setLine(2, "");
        sign.getSide(Side.FRONT).setLine(3, "");
        sign.update();
    }

    public void deleteSigns() {
        List<Block> blocks = getSignBlocks(true);

        for (Block block : blocks) {
            block.setType(Material.AIR);
        }
    }

    public void placeSigns(Material material) {
        List<Block> blocks = getSignBlocks(false);

        for (Block block : blocks) {
            block.setType(material);
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
        if (Tag.ALL_SIGNS.isTagged(root.getType())) {
            BlockState state = root.getState();
            if (state instanceof Sign sign) {
                sign.getSide(Side.FRONT).line(0, minigame.getDisplayName().color(NamedTextColor.BLUE));
                sign.getSide(Side.FRONT).line(1, settings.getDisplayName().color(NamedTextColor.GREEN));
                sign.getSide(Side.FRONT).line(2, field.getTitle().color(NamedTextColor.GREEN));
                sign.getSide(Side.FRONT).line(3, Component.text("(" + WordUtils.capitalizeFully(order.toString()) + ")"));
                sign.update();

                sign.setMetadata("MGScoreboardSign", new FixedMetadataValue(Minigames.getPlugin(), true));
                sign.setMetadata("Minigame", new FixedMetadataValue(Minigames.getPlugin(), minigame));
            } else {
                Minigames.getCmpnntLogger().warn("No Root Sign Block at: " + root.getLocation());
            }
        } else {
            Minigames.getCmpnntLogger().warn("No Root Sign Block at: " + root.getLocation());
        }
    }

    public void reload() {
        needsLoad = false;
        CompletableFuture<List<StoredStat>> future = Minigames.getPlugin().getBackend().loadStats(minigame, stat, field, order, 0, width * height * 2);

        // The update callback to be provided to the future. MUST be executed on the bukkit server thread
        future.handle((result, exp) -> Bukkit.getScheduler().runTask(Minigames.getPlugin(), () -> {
            if (exp == null) {
                stats = result;
                needsLoad = false;
                updateSigns();
            } else {
                Minigames.getCmpnntLogger().error("Error when loading scoreboard " + stat.getDisplayName() + " for minigame " + minigame.getName(), exp);
                stats = List.of();
                needsLoad = true;
            }
        }));
    }
}
