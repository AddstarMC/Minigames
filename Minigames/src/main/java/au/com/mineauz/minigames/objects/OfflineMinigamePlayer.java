package au.com.mineauz.minigames.objects;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.MinigameSave;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * player data saved on disk
 */
public class OfflineMinigamePlayer {
    private final @NotNull UUID uuid;
    private final @Nullable ItemStack @Nullable [] storedItems;
    private final @Nullable ItemStack @Nullable [] storedArmour;
    private final int food;
    private final double health;
    private final float saturation;
    private final float exp;
    private final int level;
    private @NotNull GameMode lastGM = GameMode.SURVIVAL;
    private @Nullable Location loginLocation;

    public OfflineMinigamePlayer(@NotNull UUID uuid, @Nullable ItemStack @Nullable [] items,
                                 @Nullable ItemStack @Nullable [] armour, int food, double health,
                                 float saturation, @NotNull GameMode lastGM, float exp, int level,
                                 final @Nullable Location loginLocation) {
        this.uuid = uuid;
        storedItems = items;
        storedArmour = armour;
        this.food = food;
        this.health = health;
        this.saturation = saturation;
        this.lastGM = lastGM;
        this.exp = exp;
        this.level = level;
        if (loginLocation != null && loginLocation.getWorld() == null) {
            this.loginLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
        } else {
            this.loginLocation = loginLocation;
        }
        if (Minigames.getPlugin().getConfig().getBoolean("saveInventory"))
            savePlayerData();
    }

    /**
     * loads player data from disk
     *
     * @param uuid the uuid of the user to load
     */
    public OfflineMinigamePlayer(final @NotNull UUID uuid) {
        MinigameSave save = new MinigameSave("playerdata/inventories/" + uuid);
        FileConfiguration con = save.getConfig();
        this.uuid = uuid;
        food = con.getInt("food", 20);
        health = con.getDouble("health", 20);
        saturation = con.getInt("saturation", 15);
        lastGM = GameMode.valueOf(con.getString("gamemode"));
        exp = ((Double) con.getDouble("exp", 0)).floatValue();
        level = con.getInt("level", 0);
        if (con.contains("location")) {
            loginLocation = new Location(Minigames.getPlugin().getServer().getWorld(con.getString("location.world")),
                    con.getDouble("location.x"),
                    con.getDouble("location.y"),
                    con.getDouble("location.z"),
                    (float) con.getDouble("location.yaw"),
                    (float) con.getDouble("location.pitch"));
            if (loginLocation.getWorld() == null) {
                loginLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
            }
        } else {
            loginLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
        }

        ItemStack[] items = Minigames.getPlugin().getServer().createInventory(null, InventoryType.PLAYER).getContents();
        ItemStack[] armour = new ItemStack[4];
        for (int i = 0; i < items.length; i++) {
            if (con.contains("items." + i)) {
                items[i] = con.getItemStack("items." + i);
            }
        }
        for (int i = 0; i < 4; i++) {
            armour[i] = con.getItemStack("armour." + i);
        }
        storedItems = items;
        storedArmour = armour;
    }

    public UUID getUUID() {
        return uuid;
    }

    public @Nullable ItemStack @Nullable [] getStoredItems() {
        return storedItems;
    }

    public @Nullable ItemStack @Nullable [] getStoredArmour() {
        return storedArmour;
    }

    public int getFood() {
        return food;
    }

    public double getHealth() {
        return health;
    }

    public float getSaturation() {
        return saturation;
    }

    public @NotNull GameMode getLastGamemode() {
        return lastGM;
    }

    public @Nullable Location getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(@Nullable Location loc) {
        loginLocation = loc;
    }

    public float getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public void savePlayerData() {
        MinigameSave save = new MinigameSave("playerdata/inventories/" + uuid.toString());
        FileConfiguration con = save.getConfig();
        if (storedItems != null) {
            int num = 0;
            for (ItemStack item : storedItems) {
                if (item != null) {
                    con.set("items." + num, item);
                }
                num++;
            }
        }

        if (storedArmour != null) {
            int num = 0;
            for (ItemStack item : storedArmour) {
                if (item != null) {
                    con.set("armour." + num, item);
                }
                num++;
            }
        }

        con.set("food", food);
        con.set("saturation", saturation);
        con.set("health", health);
        con.set("gamemode", lastGM.toString());
        con.set("exp", exp);
        con.set("level", level);
        if (loginLocation != null) {
            con.set("location.x", loginLocation.getBlockX());
            con.set("location.y", loginLocation.getBlockY());
            con.set("location.z", loginLocation.getBlockZ());
            con.set("location.yaw", loginLocation.getYaw());
            con.set("location.pitch", loginLocation.getPitch());
            con.set("location.world", loginLocation.getWorld().getName());
        }
        save.saveConfig();
    }

    public void deletePlayerData() {
        MinigameSave save = new MinigameSave("playerdata/inventories/" + uuid);
        save.deleteFile();
    }
}
