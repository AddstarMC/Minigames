package au.com.mineauz.minigames.display.bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import au.com.mineauz.minigames.display.AbstractDisplayObject;
import au.com.mineauz.minigames.display.DisplayManager;
import au.com.mineauz.minigames.display.IDisplayPoint;
@SuppressWarnings({"deprecation", "unused"})

public class BukkitDisplayPoint extends AbstractDisplayObject implements IDisplayPoint {
	private static Location temp = new Location(null, 0, 0, 0);
	
	private Vector position;
	private boolean showDirection;
	private float yaw;
	private float pitch;
	
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
		temp.setWorld(world);
		temp.setX(position.getX());
		temp.setY(position.getY());
		temp.setZ(position.getZ());
		
		send(Material.PLAYER_HEAD, (byte)0);
		super.show();
	}
	
	@Override
	public void hide() {
		temp.setWorld(world);
		temp.setX(position.getX());
		temp.setY(position.getY());
		temp.setZ(position.getZ());
		
		send(temp.getBlock().getType(), temp.getBlock().getData());
		super.hide();
	}
	
	private void send(Material mat, byte data) {
		if (player != null) {
			player.sendBlockChange(temp, mat, data);
		} else {
			for (Player player : world.getPlayers()) {
				player.sendBlockChange(temp, mat, data);
			}
		}
	}
}
