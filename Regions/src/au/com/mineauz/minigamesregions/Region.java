package au.com.mineauz.minigamesregions;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.properties.Properties;
import au.com.mineauz.minigames.properties.Property;
import au.com.mineauz.minigamesregions.triggers.Triggers;

public class Region extends TriggerArea {
	private final Property<Location> minimum;
	private final Property<Location> maximum;
	
	// Game state
	private List<MinigamePlayer> players = Lists.newArrayList();
	private long taskDelay = 20;
	private BukkitTask tickTask;
	
	public Region(String name, Location point1, Location point2) {
		super(name);
		
		Location[] locs = MinigameUtils.getMinMaxSelection(point1, point2);
		this.minimum = Properties.create(locs[0].clone());
		this.maximum = Properties.create(locs[1].clone());
	}
	
	Region(String name) {
		super(name);
		
		this.minimum = Properties.create();
		this.maximum = Properties.create();
	}
	
	public boolean playerInRegion(MinigamePlayer player) {
		return locationInRegion(player.getLocation());
	}
	
	public boolean locationInRegion(Location location) {
		if (location.getWorld() != getWorld()) {
			return false;
		}
		
		int minX = minimum.getValue().getBlockX();
		int maxX = maximum.getValue().getBlockX();
		int x = location.getBlockX();
		
		if (x >= minX && x <= maxX) {
			int minY = minimum.getValue().getBlockY();
			int maxY = maximum.getValue().getBlockY();
			int y = location.getBlockY();
			
			if (y >= minY && y <= maxY) {
				int minZ = minimum.getValue().getBlockZ();
				int maxZ = maximum.getValue().getBlockZ();
				int z = location.getBlockZ();
				
				if (z >= minZ && z <= maxZ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Location getMinCorner() {
		return minimum.getValue().clone();
	}
	
	public Location getMaxCorner() {
		return maximum.getValue().clone();
	}
	
	public World getWorld() {
		return minimum.getValue().getWorld();
	}
	
	public void setRegion(Location point1, Location point2) {
		Preconditions.checkArgument(point1.getWorld() == point2.getWorld(), "Cannot set a region across worlds");
		
		Location[] locs = MinigameUtils.getMinMaxSelection(point1, point2);
		this.minimum.setValue(locs[0]);
		this.maximum.setValue(locs[1]);
	}
	
	public boolean hasPlayer(MinigamePlayer player) {
		return players.contains(player);
	}
	
	public void addPlayer(MinigamePlayer player) {
		players.add(player);
	}
	
	public void removePlayer(MinigamePlayer player) {
		players.remove(player);
	}
	
	public List<MinigamePlayer> getPlayers() {
		return players;
	}
	
	public void changeTickDelay(long delay) {
		removeTickTask();
		taskDelay = delay;
		startTickTask();
	}
	
	public long getTickDelay() {
		return taskDelay;
	}
	
	public void startTickTask() {
		removeTickTask();
		
		tickTask = Bukkit.getScheduler().runTaskTimer(Minigames.plugin, new TickTask(), 0, taskDelay);
	}
	
	public void removeTickTask() {
		if (tickTask != null) {
			tickTask.cancel();
			tickTask = null;
		}
	}
	
	@Override
	public void save(ConfigurationSection section) {
		MinigameUtils.saveShortLocation(section.createSection("point1"), minimum.getValue());
		MinigameUtils.saveShortLocation(section.createSection("point2"), maximum.getValue());
		
		if (taskDelay != 20) {
			section.set("tickDelay", taskDelay);
		}
		
		super.save(section);
	}
	
	@Override
	public void load(ConfigurationSection section) {
		minimum.setValue(MinigameUtils.loadShortLocation(section.getConfigurationSection("point1")));
		maximum.setValue(MinigameUtils.loadShortLocation(section.getConfigurationSection("point2")));
		
		if (section.contains("tickDelay")) {
			taskDelay = section.getLong("tickDelay");
		}
		
		super.load(section);
	}
	
	private class TickTask implements Runnable {
		@Override
		public void run() {
			for (MinigamePlayer player : Lists.newArrayList(players)) {
				execute(Triggers.getTrigger("TICK"), player);
			}
		}
	}
}
