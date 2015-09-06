package au.com.mineauz.minigames.degeneration;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.properties.Property;
import au.com.mineauz.minigames.properties.types.EnumProperty;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigames.properties.types.StringProperty;

public class DegenerationStage {
	private final EnumProperty<StartType> startType;
	private final IntegerProperty delay;
	private final IntegerProperty interval;
	private final IntegerProperty maxRunTime;
	private final StringProperty degeneratorType;
	
	// TODO: This doesnt make sense here
	private final IntegerProperty randomChance;
	
	private Location minCorner;
	private Location maxCorner;
	
	// TODO: Also want degen effect and degen effect length
	
	DegenerationStage() {
		startType = new EnumProperty<StartType>(StartType.Immediate, "delayWait");
		delay = new IntegerProperty(Minigames.plugin.getConfig().getInt("multiplayer.floordegenerator.time"), "delay");
		interval = new IntegerProperty(delay.getValue(), "interval");
		maxRunTime = new IntegerProperty(0, "runtime");
		randomChance = new IntegerProperty(15, "randomChance");
		degeneratorType = new StringProperty("inward", "degeneratorType");
	}
	
	public DegenerationStage(Location point1, Location point2) {
		this();
		
		setRegion(point1, point2);
	}
	
	public String getDegeneratorType() {
		return degeneratorType.getValue();
	}
	
	public void setDegeneratorType(String type) {
		degeneratorType.setValue(type);
	}
	
	public Property<String> degeneratorType() {
		return degeneratorType;
	}
	
	public StartType getStartType() {
		return startType.getValue();
	}
	
	public void setStartType(StartType type) {
		startType.setValue(type);
	}
	
	public Property<StartType> startType() {
		return startType;
	}
	
	public int getDelay() {
		return delay.getValue();
	}
	
	public void setDelay(int delay) {
		this.delay.setValue(delay);
	}
	
	public Property<Integer> delay() {
		return delay;
	}
	
	public int getInterval() {
		return interval.getValue();
	}
	
	public void setInteval(int interval) {
		this.interval.setValue(interval);
	}
	
	public Property<Integer> interval() {
		return interval;
	}
	
	public int getMaxRuntime() {
		return maxRunTime.getValue();
	}
	
	public void setMaxRuntime(int max) {
		maxRunTime.setValue(max);
	}
	
	public Property<Integer> maxRuntime() {
		return maxRunTime;
	}
	
	public int getRandomChance() {
		return randomChance.getValue();
	}
	
	public void setRandomChance(int chance) {
		randomChance.setValue(chance);
	}
	
	public Property<Integer> randomChance() {
		return randomChance;
	}
	
	public Location getMinCorner() {
		return minCorner.clone();
	}
	
	public Location getMaxCorner() {
		return maxCorner.clone();
	}
	
	public void setRegion(Location point1, Location point2) {
		Location[] minmax = MinigameUtils.getMinMaxSelection(point1, point2);
		minCorner = minmax[0];
		maxCorner = minmax[1];
	}
	
	public void save(ConfigurationSection section) {
		startType.save(section);
		delay.save(section);
		maxRunTime.save(section);
		interval.save(section);
		randomChance.save(section);
		
		MinigameUtils.saveShortLocation(section.createSection("min"), minCorner);
		MinigameUtils.saveShortLocation(section.createSection("max"), maxCorner);
	}
	
	public void load(ConfigurationSection section) {
		startType.load(section);
		delay.load(section);
		maxRunTime.load(section);
		interval.load(section);
		randomChance.load(section);
		
		minCorner = MinigameUtils.loadShortLocation(section.getConfigurationSection("min"));
		maxCorner = MinigameUtils.loadShortLocation(section.getConfigurationSection("max"));
	}
	
	public enum StartType {
		Immediate,
		AfterLast,
		AfterAll
	}
}
