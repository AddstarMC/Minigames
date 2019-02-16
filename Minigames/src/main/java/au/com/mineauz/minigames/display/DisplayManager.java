package au.com.mineauz.minigames.display;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.display.bukkit.BukkitDisplayCuboid;
import au.com.mineauz.minigames.display.bukkit.BukkitDisplayPoint;
import au.com.mineauz.minigames.display.spigot.SpigotDisplayCuboid;
import au.com.mineauz.minigames.display.spigot.SpigotDisplayPoint;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Map.Entry;

public class DisplayManager {
    private boolean isSpigot;

    private BukkitTask refreshTask;

    private Map<INonPersistantDisplay, Integer> nextTickDelay;

    private SetMultimap<Player, AbstractDisplayObject> playerDisplays;
    private SetMultimap<World, AbstractDisplayObject> worldDisplays;

    public DisplayManager() {
        playerDisplays = HashMultimap.create();
        worldDisplays = HashMultimap.create();

        nextTickDelay = Maps.newIdentityHashMap();

        checkSpigot();
    }

    private void checkSpigot() {
        try {
            Class.forName("org.bukkit.Server$Spigot");
            isSpigot = true;
        } catch (ClassNotFoundException e) {
            // Not spigot
        }
    }

    public boolean isAdvanced() {
        return isSpigot;
    }

    public IDisplayCubiod displayCuboid(Player player, Location corner1, Location corner2) {
        Validate.isTrue(corner1.getWorld() == corner2.getWorld(), "Both corners must be in the same world");

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        return displayCuboid(player, minX, minY, minZ, maxX, maxY, maxZ);
    }

    public IDisplayCubiod displayCuboid(Location corner1, Location corner2) {
        Validate.isTrue(corner1.getWorld() == corner2.getWorld(), "Both corners must be in the same world");

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        return displayCuboid(corner1.getWorld(), minX, minY, minZ, maxX, maxY, maxZ);
    }

    public IDisplayCubiod displayCuboid(Player player, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (isSpigot) {
            return new SpigotDisplayCuboid(this, player, new Vector(minX, minY, minZ), new Vector(maxX, maxY, maxZ));
        } else {
            return new BukkitDisplayCuboid(this, player, new Vector(minX, minY, minZ), new Vector(maxX, maxY, maxZ));
        }
    }

    public IDisplayCubiod displayCuboid(World world, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (isSpigot) {
            return new SpigotDisplayCuboid(this, world, new Vector(minX, minY, minZ), new Vector(maxX, maxY, maxZ));
        } else {
            return new BukkitDisplayCuboid(this, world, new Vector(minX, minY, minZ), new Vector(maxX, maxY, maxZ));
        }
    }

    public IDisplayPoint displayPoint(Player player, Location location, boolean showDirection) {
        return displayPoint(player, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), showDirection);
    }

    public IDisplayPoint displayPoint(Location location, boolean showDirection) {
        return displayPoint(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), showDirection);
    }

    public IDisplayPoint displayPoint(Player player, double x, double y, double z, float yaw, float pitch, boolean showDirection) {
        if (isSpigot) {
            return new SpigotDisplayPoint(this, player, new Vector(x, y, z), yaw, pitch, showDirection);
        } else {
            return new BukkitDisplayPoint(this, player, new Vector(x, y, z), yaw, pitch, showDirection);
        }
    }

    public IDisplayPoint displayPoint(World world, double x, double y, double z, float yaw, float pitch, boolean showDirection) {
        if (isSpigot) {
            return new SpigotDisplayPoint(this, world, new Vector(x, y, z), yaw, pitch, showDirection);
        } else {
            return new BukkitDisplayPoint(this, world, new Vector(x, y, z), yaw, pitch, showDirection);
        }
    }

    public void removeAll(Player player) {
        for (IDisplayObject display : playerDisplays.removeAll(player)) {
            display.hide();
        }
    }

    public void removeAll(World world) {
        for (IDisplayObject display : worldDisplays.removeAll(world)) {
            display.hide();
        }
    }

    public void removeAll() {
        for (IDisplayObject display : playerDisplays.values()) {
            display.hide();
        }

        for (IDisplayObject display : worldDisplays.values()) {
            display.hide();
        }

        playerDisplays.clear();
        worldDisplays.clear();
    }

    protected void onShow(IDisplayObject object) {
        if (object instanceof INonPersistantDisplay) {
            INonPersistantDisplay display = (INonPersistantDisplay) object;
            nextTickDelay.put(display, display.getRefreshInterval());

            enableRefreshTask();
        }
    }

    protected void onHide(IDisplayObject object) {
        if (object instanceof INonPersistantDisplay) {
            INonPersistantDisplay display = (INonPersistantDisplay) object;
            nextTickDelay.remove(display);

            disableRefreshTask();
        }
    }

    protected void onRemove(IDisplayObject object) {
        if (object.isPlayerDisplay()) {
            playerDisplays.remove(object.getPlayer(), object);
        } else {
            worldDisplays.remove(object.getWorld(), object);
        }
    }

    private void doRefreshAll() {
        for (Entry<INonPersistantDisplay, Integer> next : nextTickDelay.entrySet()) {
            if (next.getValue() <= 0) {
                next.setValue(next.getKey().getRefreshInterval());
                next.getKey().refresh();
            } else {
                next.setValue(next.getValue() - 1);
            }
        }
    }

    private void enableRefreshTask() {
        if (refreshTask == null) {
            refreshTask = Bukkit.getScheduler().runTaskTimer(Minigames.getPlugin(), this::doRefreshAll, 1, 1);
        }
    }

    private void disableRefreshTask() {
        if (refreshTask != null && nextTickDelay.isEmpty()) {
            refreshTask.cancel();
            refreshTask = null;
        }
    }
}
