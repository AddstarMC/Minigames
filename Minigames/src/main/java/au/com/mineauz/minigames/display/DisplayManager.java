package au.com.mineauz.minigames.display;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.objects.MgRegion;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DisplayManager {
    private final Map<INonPersistentDisplay, Integer> nextTickDelay = new IdentityHashMap<>();
    private final SetMultimap<Player, AbstractDisplayObject> playerDisplays;
    private final SetMultimap<World, AbstractDisplayObject> worldDisplays;
    private BukkitTask refreshTask;

    public DisplayManager() {
        playerDisplays = HashMultimap.create();
        worldDisplays = HashMultimap.create();
    }

    public DisplayCuboid displayCuboid(Player player, Location corner1, Location corner2) {
        Validate.isTrue(corner1.getWorld() == corner2.getWorld(), "Both corners must be in the same world");

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        return displayCuboid(player, minX, minY, minZ, maxX, maxY, maxZ);
    }

    public DisplayCuboid displayCuboid(Location corner1, Location corner2) {
        Validate.isTrue(corner1.getWorld() == corner2.getWorld(), "Both corners must be in the same world");

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        return displayCuboid(corner1.getWorld(), minX, minY, minZ, maxX, maxY, maxZ);
    }

    public DisplayCuboid displayCuboid(Player player, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new DisplayCuboid(this, player, new Vector(minX, minY, minZ), new Vector(maxX, maxY, maxZ));
    }

    public DisplayCuboid displayCuboid(Player player, MgRegion region) {
        return new DisplayCuboid(this, player, new Vector(region.getMinX(), region.getMinY(), region.getMinZ()), new Vector(region.getMaxX(), region.getMaxY(), region.getMaxY()));
    }

    public DisplayCuboid displayCuboid(World world, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new DisplayCuboid(this, world, new Vector(minX, minY, minZ), new Vector(maxX, maxY, maxZ));
    }

    public DisplayCuboid displayCuboid(World world, MgRegion region) {
        return new DisplayCuboid(this, world, new Vector(region.getMinX(), region.getMinY(), region.getMinZ()), new Vector(region.getMaxX(), region.getMaxY(), region.getMaxY()));
    }

    public DisplayPoint displayPoint(Player player, Location location, boolean showDirection) {
        return displayPoint(player, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), showDirection);
    }

    public DisplayPoint displayPoint(Location location, boolean showDirection) {
        return displayPoint(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), showDirection);
    }

    public DisplayPoint displayPoint(Player player, double x, double y, double z, float yaw, float pitch, boolean showDirection) {
        return new DisplayPoint(this, player, new Vector(x, y, z), yaw, pitch, showDirection);
    }

    public DisplayPoint displayPoint(World world, double x, double y, double z, float yaw, float pitch, boolean showDirection) {
        return new DisplayPoint(this, world, new Vector(x, y, z), yaw, pitch, showDirection);
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
        if (object instanceof INonPersistentDisplay display) {
            nextTickDelay.put(display, display.getRefreshInterval());

            enableRefreshTask();
        }
    }

    protected void onHide(IDisplayObject object) {
        if (object instanceof INonPersistentDisplay display) {
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
        for (Entry<INonPersistentDisplay, Integer> next : nextTickDelay.entrySet()) {
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
