package au.com.mineauz.minigames.blockRecorder;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.papermc.lib.PaperLib;
import io.papermc.lib.features.blockstatesnapshot.BlockStateSnapshotResult;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class RecorderData implements Listener {
    private static Minigames plugin;
    private static List<Material> physBlocks = new ArrayList<>();

    static {
        physBlocks.add(Material.TORCH);
        physBlocks.add(Material.WALL_TORCH);
        physBlocks.add(Material.OAK_SIGN);
        physBlocks.add(Material.OAK_WALL_SIGN);
        physBlocks.add(Material.TRIPWIRE);
        physBlocks.add(Material.RAIL);
        physBlocks.add(Material.POWERED_RAIL);
        physBlocks.add(Material.ACTIVATOR_RAIL);
        physBlocks.add(Material.DETECTOR_RAIL);
        physBlocks.add(Material.REDSTONE_WIRE);
        physBlocks.add(Material.REDSTONE_TORCH);
        physBlocks.add(Material.REDSTONE_WALL_TORCH);
        physBlocks.add(Material.ACACIA_SAPLING);
        physBlocks.add(Material.JUNGLE_SAPLING);
        physBlocks.add(Material.OAK_SAPLING);
        physBlocks.add(Material.BIRCH_SAPLING);
        physBlocks.add(Material.DARK_OAK_SAPLING);
        physBlocks.add(Material.ROSE_BUSH);
        physBlocks.add(Material.SUNFLOWER);
        physBlocks.add(Material.NETHER_WART);
        physBlocks.add(Material.BOWL);
        physBlocks.add(Material.ACACIA_PRESSURE_PLATE);
        physBlocks.add(Material.OAK_PRESSURE_PLATE);
        physBlocks.add(Material.JUNGLE_PRESSURE_PLATE);
        physBlocks.add(Material.DARK_OAK_PRESSURE_PLATE);
        physBlocks.add(Material.BIRCH_PRESSURE_PLATE);
        physBlocks.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        physBlocks.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        physBlocks.add(Material.STONE_PRESSURE_PLATE);
        physBlocks.add(Material.STONE_BUTTON);
        physBlocks.add(Material.OAK_BUTTON);
        physBlocks.add(Material.DARK_OAK_BUTTON);
        physBlocks.add(Material.JUNGLE_BUTTON);
        physBlocks.add(Material.ACACIA_BUTTON);
        physBlocks.add(Material.BIRCH_BUTTON);
        physBlocks.add(Material.LEVER);
        physBlocks.add(Material.LADDER);
        physBlocks.add(Material.IRON_DOOR);
        physBlocks.add(Material.OAK_DOOR);
        physBlocks.add(Material.JUNGLE_DOOR);
        physBlocks.add(Material.DARK_OAK_DOOR);
        physBlocks.add(Material.BIRCH_DOOR);
        physBlocks.add(Material.ACACIA_DOOR);
        physBlocks.add(Material.RED_MUSHROOM);
        physBlocks.add(Material.BROWN_MUSHROOM);
        physBlocks.add(Material.FLOWER_POT);
        physBlocks.add(Material.LILY_PAD);
        physBlocks.add(Material.TRIPWIRE_HOOK);
        physBlocks.add(Material.OAK_TRAPDOOR);
        physBlocks.add(Material.BIRCH_TRAPDOOR);
        physBlocks.add(Material.ACACIA_TRAPDOOR);
        physBlocks.add(Material.DARK_OAK_TRAPDOOR);
        physBlocks.add(Material.JUNGLE_TRAPDOOR);
        physBlocks.add(Material.RED_CARPET);
        physBlocks.add(Material.BLUE_CARPET);
        physBlocks.add(Material.CYAN_CARPET);
        physBlocks.add(Material.GREEN_CARPET);
        physBlocks.add(Material.PINK_CARPET);
        physBlocks.add(Material.LIGHT_BLUE_CARPET);
        physBlocks.add(Material.LIME_CARPET);
        physBlocks.add(Material.WHITE_CARPET);
        physBlocks.add(Material.GRAY_CARPET);
        physBlocks.add(Material.LIGHT_GRAY_CARPET);
        physBlocks.add(Material.ORANGE_CARPET);
        physBlocks.add(Material.MAGENTA_CARPET);
        physBlocks.add(Material.BLACK_CARPET);
        physBlocks.add(Material.PURPLE_CARPET);
        physBlocks.add(Material.BROWN_CARPET);
        physBlocks.add(Material.TALL_GRASS);
        physBlocks.add(Material.TALL_SEAGRASS);
        physBlocks.add(Material.DEAD_BUSH);
        physBlocks.add(Material.COMPARATOR);
        physBlocks.add(Material.REPEATER);
        physBlocks.add(Material.WATER);
        physBlocks.add(Material.LAVA);
        physBlocks.add(Material.ANVIL);
        physBlocks.add(Material.DRAGON_EGG);
        physBlocks.add(Material.ZOMBIE_HEAD);
        physBlocks.add(Material.ZOMBIE_WALL_HEAD);
        physBlocks.add(Material.WITHER_SKELETON_WALL_SKULL);
        physBlocks.add(Material.WITHER_SKELETON_SKULL);
        physBlocks.add(Material.CREEPER_HEAD);
        physBlocks.add(Material.PLAYER_HEAD);
        physBlocks.add(Material.SKELETON_SKULL);
        physBlocks.add(Material.SKELETON_WALL_SKULL);
        physBlocks.add(Material.SNOW);
        physBlocks.add(Material.VINE);
        physBlocks.add(Material.NETHER_PORTAL);
        physBlocks.add(Material.COCOA);
        physBlocks.add(Material.CARROT);
        physBlocks.add(Material.POTATO);
        physBlocks.add(Material.BLACK_BANNER);
        physBlocks.add(Material.WHITE_BANNER);
        physBlocks.add(Material.RED_BANNER);
        physBlocks.add(Material.BLUE_BANNER);
        physBlocks.add(Material.CYAN_BANNER);
        physBlocks.add(Material.PINK_BANNER);
        physBlocks.add(Material.YELLOW_BANNER);
        physBlocks.add(Material.GREEN_BANNER);
        physBlocks.add(Material.ORANGE_BANNER);
        physBlocks.add(Material.LIME_BANNER);
        physBlocks.add(Material.GRAY_BANNER);
        physBlocks.add(Material.LIGHT_BLUE_BANNER);
        physBlocks.add(Material.LIGHT_GRAY_BANNER);
        physBlocks.add(Material.BROWN_BANNER);
        physBlocks.add(Material.MAGENTA_BANNER);
        physBlocks.add(Material.PISTON_HEAD);
        physBlocks.add(Material.MOVING_PISTON);
    }

    private Minigame minigame;
    private boolean whitelistMode = false;
    private boolean hasCreatedRegenBlocks = false;
    private Map<String, MgBlockData> blockdata;
    private Map<Integer, EntityData> entdata;
    private List<Material> wbBlocks = new ArrayList<>();

    public RecorderData(Minigame minigame) {
        plugin = Minigames.getPlugin();

        this.minigame = minigame;
        blockdata = new HashMap<>();
        entdata = new HashMap<>();

//        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public boolean getWhitelistMode() {
        return whitelistMode;
    }

    public void setWhitelistMode(boolean bool) {
        whitelistMode = bool;
    }

    public Callback<Boolean> getWhitelistModeCallback() {
        return new Callback<Boolean>() {

            @Override
            public Boolean getValue() {
                return whitelistMode;
            }

            @Override
            public void setValue(Boolean value) {
                whitelistMode = value;
            }
        };
    }

    public void addWBBlock(Material mat) {
        wbBlocks.add(mat);
    }

    public List<Material> getWBBlocks() {
        return wbBlocks;
    }

    public boolean removeWBBlock(Material mat) {
        if (wbBlocks.contains(mat)) {
            wbBlocks.remove(mat);
            return true;
        }
        return false;
    }

    public boolean hasCreatedRegenBlocks() {
        return hasCreatedRegenBlocks;
    }

    public void setCreatedRegenBlocks(boolean bool) {
        hasCreatedRegenBlocks = bool;
    }

    public Minigame getMinigame() {
        return minigame;
    }

    public MgBlockData addBlock(Block block, MinigamePlayer modifier) {
        BlockStateSnapshotResult blockstate = PaperLib.getBlockState(block, true);
        return addBlock(blockstate.getState(), modifier);
    }

    public MgBlockData addBlock(BlockState block, MinigamePlayer modifier) {
        MgBlockData bdata = new MgBlockData(block, modifier);
        String sloc = bdata.getLocation().getBlockX() + ":" + bdata.getLocation().getBlockY() + ":" + bdata.getLocation().getBlockZ();
        if (!blockdata.containsKey(sloc)) {
            if (block instanceof InventoryHolder) {
                InventoryHolder inv = (InventoryHolder) block;
                if (inv instanceof DoubleChest) {
                    Location left = ((DoubleChest) inv).getLeftSide().getInventory().getLocation().clone();
                    Location right = ((DoubleChest) inv).getRightSide().getInventory().getLocation().clone();
                    if (bdata.getLocation() == left) {

                        addInventory(bdata, ((DoubleChest) inv).getLeftSide());
                        if (minigame.isRandomizeChests())
                            bdata.randomizeContents(minigame.getMinChestRandom(), minigame.getMaxChestRandom());
                    }
                    MgBlockData secondChest = addBlock(right.getBlock(), modifier);
                    if (secondChest.getItems() == null) {
                        addInventory(secondChest, ((DoubleChest) inv).getRightSide());
                        if (minigame.isRandomizeChests())
                            secondChest.randomizeContents(minigame.getMinChestRandom(), minigame.getMaxChestRandom());
                    } else if (inv instanceof Chest) {
                        addInventory(bdata, inv);
                        if (minigame.isRandomizeChests())
                            bdata.randomizeContents(minigame.getMinChestRandom(), minigame.getMaxChestRandom());
                    }
                } else {
                    addInventory(bdata, inv);
                }
            } else if (block.getType() == Material.FLOWER_POT) {
                bdata.setSpecialData("contents", block.getData());
            }

            blockdata.put(sloc, bdata);
            return bdata;
        } else {
            if (block.getType() != Material.CHEST || !blockdata.get(sloc).hasRandomized())
                blockdata.get(sloc).setModifier(modifier);
            return blockdata.get(sloc);
        }
    }

    public void addInventory(MgBlockData bdata, InventoryHolder ih) {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack item : ih.getInventory()) {
            if (item != null) {
                items.add(item.clone());
            }
        }
        ItemStack[] inventory = new ItemStack[items.size()];
        items.toArray(inventory);
        bdata.setItems(inventory);
    }


    public void addEntity(Entity ent, MinigamePlayer player, boolean created) {
        EntityData edata = new EntityData(ent, player, created);
        entdata.put(ent.getEntityId(), edata);
    }

    public boolean hasEntity(Entity ent) {
        return entdata.containsKey(ent.getEntityId());
    }

    public boolean hasBlock(Block block) {
        String sloc = block.getLocation().getBlockX() + ":" + block.getLocation().getBlockY() + ":" + block.getLocation().getBlockZ();
        return blockdata.containsKey(sloc);
    }

    public void restoreAll(MinigamePlayer modifier) {
        if (!blockdata.isEmpty()) {
            restoreBlocks(modifier);
        }

        if (!entdata.isEmpty()) {
            restoreEntities(modifier);
        }
    }

    public void restoreBlocks() {
//        saveAllBlockData();
        restoreBlocks(null);
    }

    public void restoreEntities() {
        restoreEntities(null);
        entdata.clear();
    }

    public void restoreBlocks(final MinigamePlayer modifier) {
        // When rolling back a single player's changes dont change the overall games state
        if (modifier == null) {
            minigame.setState(MinigameState.REGENERATING);
        }

        Iterator<MgBlockData> it = blockdata.values().iterator();
        final List<MgBlockData> baseBlocks = Lists.newArrayList();
        final List<MgBlockData> gravityBlocks = Lists.newArrayList();
        final List<MgBlockData> attachableBlocks = Lists.newArrayList();

        while (it.hasNext()) {
            MgBlockData data = it.next();
            boolean gravity = false;
            boolean attachable = false;
            boolean inventoryholder = false;
            if (modifier == null || modifier.equals(data.getModifier())) {
                it.remove();

                // Clear inventories
                if (data.getLocation().getBlock().getState() instanceof InventoryHolder) {
                    InventoryHolder block = (InventoryHolder) data.getLocation().getBlock().getState();
                    block.getInventory().clear();
                }
                if (data.getBukkitBlockData().getMaterial().hasGravity()) gravity = true;
                if (physBlocks.contains(data.getBlockState().getType()) || data.getBlockState().getBlockData() instanceof
                        Attachable) attachable = true;
                if (data.getItems() != null) inventoryholder = true;
                if (attachable) {
                    attachableBlocks.add(data);
                } else if (gravity) {
                    gravityBlocks.add(data);
                } else {
                    baseBlocks.add(data);
                }
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            customblockComparator(baseBlocks);
            customblockComparator(attachableBlocks);
            customblockComparator(gravityBlocks);
            baseBlocks.addAll(gravityBlocks);

            new RollbackScheduler(baseBlocks, attachableBlocks, minigame, modifier);
        });
    }

    private void customblockComparator(List<MgBlockData> baseBlocks) {
        baseBlocks.sort((o1, o2) -> {
            int comp = Integer.compare(o1.getBlockState().getChunk().getX(), o2.getBlockState().getChunk().getX());
            if (comp != 0)
                return comp;
            comp = Integer.compare(o1.getBlockState().getChunk().getZ(), o2.getBlockState().getChunk().getZ());
            if (comp != 0)
                return comp;
            return Integer.compare(o1.getBlockState().getY(), o2.getBlockState().getY());
        });
    }

    public void restoreEntities(MinigamePlayer player) {
        Iterator<EntityData> it = entdata.values().iterator();
        while (it.hasNext()) {
            EntityData entdata = it.next();
            if (player == null || player.equals(entdata.getModifier())) {
                if (entdata.wasCreated()) {
                    Entity ent = entdata.getEntity();
                    // Entity needs to be removed
                    if (ent.isValid()) {
                        ent.remove();
                    }
                } else {
                    // Entity needs to be spawned
                    Location location = entdata.getEntityLocation();
                    location.getWorld().spawnEntity(location, entdata.getEntityType());
                }

                it.remove();
            }
        }
    }

    public void clearRestoreData() {
        entdata.clear();
        blockdata.clear();
    }

    public boolean hasData() {
        return !(blockdata.isEmpty() && entdata.isEmpty());
    }

    public boolean checkBlockSides(Location location) {
        Location temp = location.clone();
        temp.setX(temp.getX() - 1);
        temp.setY(temp.getY() - 1);
        temp.setZ(temp.getZ() - 1);

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                for (int z = 0; z < 2; z++) {
                    if (hasBlock(temp.getBlock())) {
                        return true;
                    }
                    temp.setZ(temp.getZ() + 1);
                }
                if (hasBlock(temp.getBlock())) {
                    return true;
                }
                temp.setZ(temp.getZ() - 2);
                temp.setX(temp.getX() + 1);
            }
            temp.setX(temp.getX() - 2);
            temp.setY(temp.getY() + 1);
        }
        return false;
    }

    public boolean hasRegenArea() {
        return minigame.getRegenArea1() != null && minigame.getRegenArea2() != null;
    }

    public double getRegenMinX() {
        if (minigame.getRegenArea1().getX() > minigame.getRegenArea2().getX()) {
            return minigame.getRegenArea2().getX();
        }
        return minigame.getRegenArea1().getX();
    }

    public double getRegenMaxX() {
        if (minigame.getRegenArea1().getX() < minigame.getRegenArea2().getX()) {
            return minigame.getRegenArea2().getX();
        }
        return minigame.getRegenArea1().getX();
    }

    public double getRegenMinY() {
        if (minigame.getRegenArea1().getY() > minigame.getRegenArea2().getY()) {
            return minigame.getRegenArea2().getY();
        }
        return minigame.getRegenArea1().getY();
    }

    public double getRegenMaxY() {
        if (minigame.getRegenArea1().getY() < minigame.getRegenArea2().getY()) {
            return minigame.getRegenArea2().getY();
        }
        return minigame.getRegenArea1().getY();
    }

    public double getRegenMinZ() {
        if (minigame.getRegenArea1().getZ() > minigame.getRegenArea2().getZ()) {
            return minigame.getRegenArea2().getZ();
        }
        return minigame.getRegenArea1().getZ();
    }

    public double getRegenMaxZ() {
        if (minigame.getRegenArea1().getZ() < minigame.getRegenArea2().getZ()) {
            return minigame.getRegenArea2().getZ();
        }
        return minigame.getRegenArea1().getZ();
    }

    public boolean blockInRegenArea(Location location) {
        return location.getWorld() == minigame.getRegenArea1().getWorld() &&
                location.getBlockX() >= getRegenMinX() && location.getBlockX() <= getRegenMaxX() &&
                location.getBlockY() >= getRegenMinY() && location.getBlockY() <= getRegenMaxY() &&
                location.getBlockZ() >= getRegenMinZ() && location.getBlockZ() <= getRegenMaxZ();
    }

    public void saveAllBlockData() {
        File f = new File(plugin.getDataFolder() + "/minigames/" + minigame.getName(false) + "/backup.json");
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(f)) {
            gson.toJson(blockdata, writer);
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().severe("File not found!!!");
            e.printStackTrace();
        } catch (IOException e) {
            Bukkit.getLogger().severe("IO Error!");
            e.printStackTrace();
        }
    }

    public boolean restoreBlockData() {
        File f = new File(plugin.getDataFolder() + "/minigames/" + minigame.getName(false) + "/backup.json");
        if (covertOldFormat()) {
            saveAllBlockData();
            Minigames.getPlugin().getLogger().info("Converted backup for: " + minigame.getName(false));
            return true;
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, MgBlockData>>() {
            }.getType();
            try (FileReader reader = new FileReader(f)) {
                blockdata = gson.fromJson(reader, type);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean covertOldFormat() {
        File f = new File(plugin.getDataFolder() + "/minigames/" + minigame.getName(false) + "/backup.dat");

        if (!f.exists()) {
            return false;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));

            Map<String, String> args = new HashMap<>();
            String line;
            String[] blocks;
            String[] block;
            World w;
            MgBlockData bd;
            BlockState state;
            ItemStack[] items;
            String[] sitems;
            ItemStack item;
            Map<String, String> iargs = new HashMap<>();

            while (br.ready()) {
                line = br.readLine();

                blocks = line.split("\\}\\{");

                for (String bl : blocks) {
                    args.clear();

                    bl = bl.replace("{", "");
                    bl = bl.replace("}", "");

                    block = bl.split(";");
                    for (String b : block) {
                        String[] spl = b.split(":");
                        if (spl.length > 1) {
                            args.put(spl[0], spl[1]);
                        }
                    }

                    w = Bukkit.getWorld(args.get("world"));
                    state = w.getBlockAt(Integer.parseInt(args.get("x")), Integer.parseInt(args.get("y")), Integer.parseInt(args.get("z"))).getState();
                    state.setBlockData(Bukkit.getUnsafe().fromLegacy(Material.getMaterial(args.get("mat")), Byte.parseByte(args.get("data"))));

                    bd = new MgBlockData(state, null);

                    if (args.containsKey("items")) {
                        if (state.getType() == Material.DISPENSER || state.getType() == Material.DROPPER) {
                            items = new ItemStack[InventoryType.DISPENSER.getDefaultSize()];
                        } else if (state.getType() == Material.HOPPER) {
                            items = new ItemStack[InventoryType.HOPPER.getDefaultSize()];
                        } else if (state.getType() == Material.FURNACE) {
                            items = new ItemStack[InventoryType.FURNACE.getDefaultSize()];
                        } else if (state.getType() == Material.BREWING_STAND) {
                            items = new ItemStack[InventoryType.BREWING.getDefaultSize()];
                        } else {
                            items = new ItemStack[InventoryType.CHEST.getDefaultSize()];
                        }

                        sitems = args.get("items").split("\\)\\(");

                        for (String i : sitems) {
                            i = i.replace("(", "");
                            i = i.replace(")", "");

                            for (String s : i.split("\\|")) {
                                String[] spl = s.split("-");
                                if (spl.length > 1) {
                                    iargs.put(s.split("-")[0], s.split("-")[1]);
                                }
                            }
                            item = new ItemStack(Material.getMaterial(iargs.get("item")),
                                    Integer.parseInt(iargs.get("c")), Short.parseShort(iargs.get("dur")));

                            if (iargs.containsKey("enc")) {
                                for (String s : iargs.get("enc").split("\\]\\[")) {
                                    item.addUnsafeEnchantment(Enchantment.getByName(s.split(",")[0].replace("[", "")),
                                            Integer.parseInt(s.split(",")[1].replace("]", "")));
                                }
                            }

                            items[Integer.parseInt(iargs.get("slot"))] = item;
                            iargs.clear();
                        }

                        bd.setItems(items);
                    }

                    blockdata.put(MinigameUtils.createLocationID(bd.getLocation()), bd);
                }
            }

            br.close();
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().severe("File not found!!!");
            e.printStackTrace();
        } catch (IOException e) {
            Bukkit.getLogger().severe("IO Error!");
            e.printStackTrace();
        }

        return true;
    }

    @EventHandler(ignoreCancelled = true)
    private void vehicleCreate(VehicleCreateEvent event) {
        if (hasRegenArea() && minigame.hasPlayers() && blockInRegenArea(event.getVehicle().getLocation())) {
            addEntity(event.getVehicle(), null, true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void vehicleDestroy(VehicleDestroyEvent event) {
        if (event.getAttacker() == null) {
            if (hasRegenArea() && minigame.hasPlayers() && blockInRegenArea(event.getVehicle().getLocation())) {
                addEntity(event.getVehicle(), null, false);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void animalDeath(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Animals) {
            Animals animal = (Animals) event.getEntity();
            if (hasRegenArea() && minigame.hasPlayers() && !(event.getDamager() instanceof Player)) {
                Location ent = event.getEntity().getLocation();
                if (blockInRegenArea(ent)) {
                    if (animal.getHealth() <= event.getDamage()) {
                        addEntity(event.getEntity(), null, true);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void mobSpawnEvent(CreatureSpawnEvent event) {
        if (hasRegenArea() && minigame.hasPlayers() && blockInRegenArea(event.getLocation())) {
            addEntity(event.getEntity(), null, true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void entityExplode(EntityExplodeEvent event) {
        if (hasRegenArea() && minigame.hasPlayers()) {
            Location block = event.getLocation().getBlock().getLocation();
            if (blockInRegenArea(block)) {
                List<Block> blocks = new ArrayList<>(event.blockList());

                for (Block bl : blocks) {
                    if ((whitelistMode && getWBBlocks().contains(bl.getType())) ||
                            (!whitelistMode && !getWBBlocks().contains(bl.getType()))) {
                        addBlock(bl, null);
                    } else {
                        event.blockList().remove(bl);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void itemDrop(ItemSpawnEvent event) {
        if (hasRegenArea() && minigame.hasPlayers()) {
            Location ent = event.getLocation();
            if (blockInRegenArea(ent)) {
                addEntity(event.getEntity(), null, true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void physicalBlock(EntityChangeBlockEvent event) {
        if (hasRegenArea() && blockInRegenArea(event.getBlock().getLocation())) {
            if (minigame.isRegenerating()) {
                event.setCancelled(true);
                return;
            }
            if (event.getTo() == Material.SAND ||
                    event.getTo() == Material.GRAVEL ||
                    event.getTo() == Material.DRAGON_EGG ||
                    event.getTo() == Material.ANVIL) {

                if (minigame.hasPlayers() || event.getEntity().hasMetadata("FellInMinigame")) {
                    addEntity(event.getEntity(), null, true);
                }
            } else if (event.getEntityType() == EntityType.FALLING_BLOCK && minigame.hasPlayers()) {
                event.getEntity().setMetadata("FellInMinigame", new FixedMetadataValue(Minigames.getPlugin(), true));
                addEntity(event.getEntity(), null, true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void cartHopperPickup(InventoryPickupItemEvent event) {
        if (hasRegenArea() && minigame.hasPlayers() && event.getInventory().getHolder() instanceof HopperMinecart) {
            Location loc = ((HopperMinecart) event.getInventory().getHolder()).getLocation();
            if (blockInRegenArea(loc)) {
                addEntity((HopperMinecart) event.getInventory().getHolder(), null, false);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void cartkMoveItem(InventoryMoveItemEvent event) {
        if (!hasRegenArea() || !minigame.hasPlayers()) return;

        Location loc = null;
        if (event.getInitiator().getHolder() instanceof HopperMinecart) {
            loc = ((HopperMinecart) event.getInitiator().getHolder()).getLocation().clone();
            if (blockInRegenArea(loc))
                addEntity((Entity) event.getInitiator().getHolder(), null, false);
        }

        loc = null;
        if (event.getDestination().getHolder() instanceof HopperMinecart) {
            loc = ((HopperMinecart) event.getDestination().getHolder()).getLocation().clone();
            if (blockInRegenArea(loc))
                addEntity((Entity) event.getInitiator().getHolder(), null, false);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void physEvent(BlockPhysicsEvent event) {
        if (minigame.isRegenerating() && hasRegenArea() && blockInRegenArea(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void waterFlow(BlockFromToEvent event) {
        if (minigame.isRegenerating() && hasRegenArea() && blockInRegenArea(event.getBlock().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void fireSpread(BlockSpreadEvent event) {
        if (minigame.isRegenerating() && hasRegenArea() && blockInRegenArea(event.getBlock().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void interact(PlayerInteractEvent event) {
        if (minigame.isRegenerating() && hasRegenArea() && blockInRegenArea(event.getClickedBlock().getLocation())) {
            event.setCancelled(true);
        }
    }
}
