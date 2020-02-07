package au.com.mineauz.minigames.display.spigot;

import au.com.mineauz.minigames.display.AbstractDisplayObject;
import au.com.mineauz.minigames.display.DisplayManager;
import au.com.mineauz.minigames.display.IDisplayCubiod;
import au.com.mineauz.minigames.display.INonPersistantDisplay;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SpigotDisplayCuboid extends AbstractDisplayObject implements IDisplayCubiod, INonPersistantDisplay {
    private static Location temp = new Location(null, 0, 0, 0);

    private Vector minCorner;
    private Vector maxCorner;

    public SpigotDisplayCuboid(DisplayManager manager, World world, Vector minCorner, Vector maxCorner) {
        super(manager, world);
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;
    }

    public SpigotDisplayCuboid(DisplayManager manager, Player player, Vector minCorner, Vector maxCorner) {
        this(manager, player.getWorld(), minCorner, maxCorner);
        this.player = player;
    }

    @Override
    public void show() {
        refresh();
        super.show();
    }

    @Override
    public int getRefreshInterval() {
        return 10;
    }

    @Override
    public void refresh() {
        // Dont display effect if they cant see it
        if (player != null && player.getWorld() != getWorld()) {
            return;
        }
        double step = 0.5;

        // X axis
        for (double x = minCorner.getX(); x <= maxCorner.getX(); x += step) {
            placeEffectAt(x, minCorner.getY(), minCorner.getZ());
            placeEffectAt(x, maxCorner.getY(), minCorner.getZ());
            placeEffectAt(x, minCorner.getY(), maxCorner.getZ());
            placeEffectAt(x, maxCorner.getY(), maxCorner.getZ());
        }

        // Y axis
        for (double y = minCorner.getY(); y <= maxCorner.getY(); y += step) {
            placeEffectAt(minCorner.getX(), y, minCorner.getZ());
            placeEffectAt(maxCorner.getX(), y, minCorner.getZ());
            placeEffectAt(minCorner.getX(), y, maxCorner.getZ());
            placeEffectAt(maxCorner.getX(), y, maxCorner.getZ());
        }

        // Z axis
        for (double z = minCorner.getZ(); z <= maxCorner.getZ(); z += step) {
            placeEffectAt(minCorner.getX(), minCorner.getY(), z);
            placeEffectAt(maxCorner.getX(), minCorner.getY(), z);
            placeEffectAt(minCorner.getX(), maxCorner.getY(), z);
            placeEffectAt(maxCorner.getX(), maxCorner.getY(), z);
        }
    }

    private void placeEffectAt(double x, double y, double z) {
        temp.setX(x);
        temp.setY(y);
        temp.setZ(z);
        temp.setWorld(getWorld());

        if (player == null) {
            getWorld().spawnParticle(Particle.BARRIER, temp, 1);
        } else {
            player.spawnParticle(Particle.BARRIER, temp, 1);
        }
    }
}
