package au.com.mineauz.minigames.display.bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import au.com.mineauz.minigames.display.AbstractDisplayObject;
import au.com.mineauz.minigames.display.DisplayManager;
import au.com.mineauz.minigames.display.IDisplayCubiod;

public class BukkitDisplayCuboid extends AbstractDisplayObject implements IDisplayCubiod {
    private final Location temp = new Location(null, 0, 0, 0);

    private final Vector minCorner;
    private final Vector maxCorner;

    public BukkitDisplayCuboid(DisplayManager manager, World world, Vector minCorner, Vector maxCorner) {
        super(manager, world);
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;
        this.maxCorner.subtract(new Vector(1, 1, 1));
    }

    public BukkitDisplayCuboid(DisplayManager manager, Player player, Vector minCorner, Vector maxCorner) {
        this(manager, player.getWorld(), minCorner, maxCorner);
        this.player = player;
    }

    @Override
    public void show() {
        temp.setWorld(getWorld());
        for (int x = minCorner.getBlockX(); x <= maxCorner.getBlockX(); x++) {
            temp.setX(x);
            for (int y = minCorner.getBlockY(); y <= maxCorner.getBlockY(); y++) {
                temp.setY(y);
                for (int z = minCorner.getBlockZ(); z <= maxCorner.getBlockZ(); z++) {
                    temp.setZ(z);
                    if (((z == minCorner.getBlockZ() || z == maxCorner.getBlockZ()) && (x == minCorner.getBlockX() || x == maxCorner.getBlockX()) && (y == minCorner.getBlockY() || y == maxCorner.getBlockY())) ||
                            ((x == minCorner.getBlockX() || x == maxCorner.getBlockX()) && (y == minCorner.getBlockY() || y == maxCorner.getBlockY())) ||
                            ((z == minCorner.getBlockZ() || z == maxCorner.getBlockZ()) && (y == minCorner.getBlockY() || y == maxCorner.getBlockY())) ||
                            ((z == minCorner.getBlockZ() || z == maxCorner.getBlockZ()) && (x == minCorner.getBlockX() || x == maxCorner.getBlockX()))) {
                        send(Material.DIAMOND_BLOCK, (byte) 0);
                    }
                }
            }
        }

        super.show();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void hide() {
        temp.setWorld(getWorld());
        for (int x = minCorner.getBlockX(); x <= maxCorner.getBlockX(); x++) {
            temp.setX(x);
            for (int y = minCorner.getBlockY(); y <= maxCorner.getBlockY(); y++) {
                temp.setY(y);
                for (int z = minCorner.getBlockZ(); z <= maxCorner.getBlockZ(); z++) {
                    temp.setZ(z);
                    if (((z == minCorner.getBlockZ() || z == maxCorner.getBlockZ()) && (x == minCorner.getBlockX() || x == maxCorner.getBlockX()) && (y == minCorner.getBlockY() || y == maxCorner.getBlockY())) ||
                            ((x == minCorner.getBlockX() || x == maxCorner.getBlockX()) && (y == minCorner.getBlockY() || y == maxCorner.getBlockY())) ||
                            ((z == minCorner.getBlockZ() || z == maxCorner.getBlockZ()) && (y == minCorner.getBlockY() || y == maxCorner.getBlockY())) ||
                            ((z == minCorner.getBlockZ() || z == maxCorner.getBlockZ()) && (x == minCorner.getBlockX() || x == maxCorner.getBlockX()))) {
                        send(temp.getBlock().getType(), temp.getBlock().getData());
                    }
                }
            }
        }

        super.hide();
    }

    @SuppressWarnings("deprecation")
    private void send(Material mat, byte data) {
        if (player != null) {
            player.sendBlockChange(temp, mat, data);
        } else {
            for (Player player : getWorld().getPlayers()) {
                player.sendBlockChange(temp, mat, data);
            }
        }
    }
}
