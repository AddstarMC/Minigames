package au.com.mineauz.minigames.objects;

import au.com.mineauz.minigames.config.MinigameSave;
import au.com.mineauz.minigames.Minigames;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class OfflineMinigamePlayer {
    private UUID uuid;
    private ItemStack[] storedItems = null;
    private ItemStack[] storedArmour = null;
    private int food = 20;
    private double health = 20;
    private float saturation = 15;
    private float exp = -1; //TODO: Set to default value after 1.7
    private int level = -1; //Set To default value after 1.7
    private GameMode lastGM = GameMode.SURVIVAL;
    private Location loginLocation;
    private boolean shouldSave = Minigames.getPlugin().getConfig().getBoolean("saveInventory");

    public OfflineMinigamePlayer(UUID uuid, ItemStack[] items,
                                 ItemStack[] armour, int food, double health,
                                 float saturation, GameMode lastGM, float exp, int level,
                                 Location loginLocation) {
        this.uuid = uuid;
        storedItems = items;
        storedArmour = armour;
        this.food = food;
        this.health = health;
        this.saturation = saturation;
        this.lastGM = lastGM;
        this.exp = exp;
        this.level = level;
        if (loginLocation != null && loginLocation.getWorld() == null)
            loginLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
        this.loginLocation = loginLocation;
        if (shouldSave)
            savePlayerData();
    }

    public OfflineMinigamePlayer(UUID uuid) {
        MinigameSave save = new MinigameSave("playerdata/inventories/" + uuid.toString());
        FileConfiguration con = save.getConfig();
        this.uuid = uuid;
        food = con.getInt("food");
        health = con.getDouble("health");
        saturation = con.getInt("saturation");
        lastGM = GameMode.valueOf(con.getString("gamemode"));
        if (con.contains("exp")) {
            exp = ((Double) con.getDouble("exp")).floatValue();
        }
        if (con.contains("level"))
            level = con.getInt("level");
        if (con.contains("location")) {
            loginLocation = new Location(Minigames.getPlugin().getServer().getWorld(con.getString("location.world")),
                    con.getDouble("location.x"),
                    con.getDouble("location.y"),
                    con.getDouble("location.z"),
                    new Float(con.getString("location.yaw")),
                    new Float(con.getString("location.pitch")));
            if (loginLocation.getWorld() == null)
                loginLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
        } else
            loginLocation = Bukkit.getWorlds().get(0).getSpawnLocation();

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

    public ItemStack[] getStoredItems() {
        return storedItems;
    }

    public ItemStack[] getStoredArmour() {
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

    public GameMode getLastGamemode() {
        return lastGM;
    }

    public Location getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(Location loc) {
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
