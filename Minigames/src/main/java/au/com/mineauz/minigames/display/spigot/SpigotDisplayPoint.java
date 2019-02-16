package au.com.mineauz.minigames.display.spigot;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import au.com.mineauz.minigames.display.AbstractDisplayObject;
import au.com.mineauz.minigames.display.DisplayManager;
import au.com.mineauz.minigames.display.IDisplayPoint;
import au.com.mineauz.minigames.display.INonPersistantDisplay;

public class SpigotDisplayPoint extends AbstractDisplayObject implements IDisplayPoint, INonPersistantDisplay {
    private static Location temp = new Location(null, 0, 0, 0);

    private Vector position;
    private boolean showDirection;
    private float yaw;
    private float pitch;

    public SpigotDisplayPoint(DisplayManager manager, Player player, Vector position, float yaw, float pitch, boolean showDirection) {
        this(manager, player.getWorld(), position, yaw, pitch, showDirection);
        this.player = player;
    }

    public SpigotDisplayPoint(DisplayManager manager, World world, Vector position, float yaw, float pitch, boolean showDirection) {
        super(manager, world);

        this.position = position;
        this.showDirection = showDirection;
        this.yaw = yaw;
        this.pitch = pitch;
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
        if (player != null && player.getWorld() != getWorld()) {
            return;
        }

        double dist = 0.25;

        placeEffect(position.getX() - dist, position.getY() - dist, position.getZ() - dist, Particle.FLAME);
        placeEffect(position.getX() + dist, position.getY() - dist, position.getZ() - dist, Particle.FLAME);
        placeEffect(position.getX() - dist, position.getY() + dist, position.getZ() - dist, Particle.FLAME);
        placeEffect(position.getX() + dist, position.getY() + dist, position.getZ() - dist, Particle.FLAME);
        placeEffect(position.getX() - dist, position.getY() - dist, position.getZ() + dist, Particle.FLAME);
        placeEffect(position.getX() + dist, position.getY() - dist, position.getZ() + dist, Particle.FLAME);
        placeEffect(position.getX() - dist, position.getY() + dist, position.getZ() + dist, Particle.FLAME);
        placeEffect(position.getX() + dist, position.getY() + dist, position.getZ() + dist, Particle.FLAME);

        if (showDirection) {
            temp.setYaw(yaw);
            temp.setPitch(pitch);
            Vector start = position.clone();
            Vector dir = temp.getDirection().normalize();

            for (double p = 0; p <= 1; p += 0.25) {
                Vector point = start.clone().add(dir.clone().multiply(p));
                placeEffect(point.getX(), point.getY(), point.getZ(), Particle.FLAME);
            }
        }
    }

    private void placeEffect(double x, double y, double z, Particle effect) {
        temp.setWorld(getWorld());
        temp.setX(x);
        temp.setY(y);
        temp.setZ(z);

        if (player != null) {
            player.getWorld().spawnParticle(effect, temp, 1, 0, 0, 0);
        } else {
            getWorld().spawnParticle(effect, temp, 1, 0, 0, 0);
        }
    }
}
