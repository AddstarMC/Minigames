package au.com.mineauz.minigames.display.bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import au.com.mineauz.minigames.display.AbstractDisplayObject;
import au.com.mineauz.minigames.display.DisplayManager;
import au.com.mineauz.minigames.display.IDisplayPoint;

public class BukkitDisplayPoint extends AbstractDisplayObject implements IDisplayPoint {
    private static final Location temp = new Location(null, 0, 0, 0);

    private final Vector position;
    private final boolean showDirection;
    private final float yaw;
    private final float pitch;

    public BukkitDisplayPoint(DisplayManager manager, Player player, Vector position, float yaw, float pitch, boolean showDirection) {
        this(manager, player.getWorld(), position, yaw, pitch, showDirection);
        this.player = player;
    }

    public BukkitDisplayPoint(DisplayManager manager, World world, Vector position, float yaw, float pitch, boolean showDirection) {
        super(manager, world);

        this.position = position;
        this.showDirection = showDirection;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void show() {
        temp.setWorld(getWorld());
        temp.setX(position.getX());
        temp.setY(position.getY());
        temp.setZ(position.getZ());

        send(Material.SKELETON_SKULL, (byte) 0);
        super.show();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void hide() {
        temp.setWorld(getWorld());
        temp.setX(position.getX());
        temp.setY(position.getY());
        temp.setZ(position.getZ());

        send(temp.getBlock().getType(), temp.getBlock().getData());
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
