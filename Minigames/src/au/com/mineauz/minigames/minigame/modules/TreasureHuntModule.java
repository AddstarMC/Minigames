package au.com.mineauz.minigames.minigame.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.menu.MenuItemTime;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigames.properties.types.StringProperty;

public class TreasureHuntModule extends MinigameModule{
	
	private final ConfigPropertyContainer properties;
	private final StringProperty location = new StringProperty(null, "location");
	private final IntegerProperty maxRadius = new IntegerProperty(1000, "maxradius");
	private final IntegerProperty maxHeight = new IntegerProperty(20, "maxheight");
	private final IntegerProperty minTreasure = new IntegerProperty(0, "mintreasure");
	private final IntegerProperty maxTreasure = new IntegerProperty(8, "maxtreasure");
	private final IntegerProperty treasureWaitTime = new IntegerProperty(Minigames.plugin.getConfig().getInt("treasurehunt.waittime"), "treasurehuntwait");
	private final IntegerProperty hintWaitTime = new IntegerProperty(500, "hintWaitTime");
	private final IntegerProperty timer = new IntegerProperty(0, "timer");
	
	//Unsaved Data
	private Location treasureLocation = null;
	private boolean treasureFound = false;
	private ArrayList<String> curHints = new ArrayList<String>();
	private Map<UUID, Long> hintUse = new HashMap<UUID, Long>();

	public TreasureHuntModule(Minigame mgm) {
		super(mgm);
		
		properties = new ConfigPropertyContainer();
		properties.addProperty(location);
		properties.addProperty(maxRadius);
		properties.addProperty(maxHeight);
		properties.addProperty(minTreasure);
		properties.addProperty(maxTreasure);
		properties.addProperty(treasureWaitTime);
		properties.addProperty(hintWaitTime);
		properties.addProperty(timer);
	}

	@Override
	public String getName() {
		return "TreasureHunt";
	}

	@Override
	public ConfigPropertyContainer getProperties() {
		return properties;
	}

	@Override
	public boolean useSeparateConfig() {
		return false;
	}

	@Override
	public void save(FileConfiguration config) {
	}

	@Override
	public void load(FileConfiguration config) {
	}

	@Override
	public Menu createSettingsMenu(){
		Menu treasureHunt = new Menu(6, getMinigame().getName(false));
		
		treasureHunt.addItem(new MenuItemString("Location Name", "Name to appear when;treasure spawns", Material.BED, location));
		treasureHunt.addItem(new MenuItemInteger("Max. Radius", Material.ENDER_PEARL, maxRadius, 10, Integer.MAX_VALUE));
		treasureHunt.addItem(new MenuItemInteger("Max. Height", "Max. height of where a;chest can generate.;Can still move above to;avoid terrain", Material.BEACON, maxHeight, 1, 256));
		treasureHunt.addItem(new MenuItemInteger("Min. Items", "Minimum items to;spawn in chest.", Material.STEP, minTreasure, 0, 27));
		treasureHunt.addItem(new MenuItemInteger("Max. Items", "Maximum items to;spawn in chest.", Material.STONE, maxTreasure, 0, 27));
		treasureHunt.addItem(new MenuItemTime("Time Length", Material.WATCH, timer, 0, Integer.MAX_VALUE));
		treasureHunt.addItem(new MenuItemTime("Restart Delay", Material.WATCH, treasureWaitTime, 0, Integer.MAX_VALUE));
		treasureHunt.addItem(new MenuItemTime("Hint Usage Delay", Material.WATCH, hintWaitTime, 0, Integer.MAX_VALUE));
		return treasureHunt;
	}
	
	@Deprecated
	public static TreasureHuntModule getMinigameModule(Minigame minigame){
		return (TreasureHuntModule) minigame.getModule(TreasureHuntModule.class);
	}

	public void setMaxRadius(int maxRadius){
		this.maxRadius.setValue(maxRadius);
	}

	public int getMaxRadius(){
		return maxRadius.getValue();
	}

	public int getMaxHeight() {
		return maxHeight.getValue();
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight.setValue(maxHeight);
	}

	public String getLocation(){
		return location.getValue();
	}

	public void setLocation(String location){
		this.location.setValue(location);
	}

	public int getMinTreasure() {
		return minTreasure.getValue();
	}

	public void setMinTreasure(int minTreasure) {
		this.minTreasure.setValue(minTreasure);
	}

	public int getMaxTreasure() {
		return maxTreasure.getValue();
	}

	public void setMaxTreasure(int maxTreasure) {
		this.maxTreasure.setValue(maxTreasure);
	}
	
	public Location getTreasureLocation(){
		return treasureLocation.clone();
	}
	
	public void setTreasureLocation(Location loc){
		treasureLocation = loc;
	}
	
	public boolean hasTreasureLocation(){
		return treasureLocation != null;
	}
	
	public boolean isTreasureFound(){
		return treasureFound;
	}
	
	public void setTreasureFound(boolean bool){
		treasureFound = bool;
	}
	
	public List<String> getCurrentHints(){
		return curHints;
	}
	
	public void addHint(String hint){
		curHints.add(hint);
	}
	
	public void clearHints(){
		curHints.clear();
	}
	
	public int getTreasureWaitTime(){
		return treasureWaitTime.getValue();
	}
	
	public void setTreasureWaitTime(int time){
		treasureWaitTime.setValue(time);
	}
	
	public long getLastHintUse(MinigamePlayer player){
		if(!hintUse.containsKey(player.getUUID()))
			return -1L;
		return hintUse.get(player.getUUID());
	}
	
	public boolean canUseHint(MinigamePlayer player){
		if(hintUse.containsKey(player.getUUID())){
			long curtime = System.currentTimeMillis();
			long lastuse = curtime - hintUse.get(player.getUUID());
			if(lastuse >= getHintDelay() * 1000)
				return true;
			return false;
		}
		return true;
	}
	
	public void addHintUse(MinigamePlayer player){
		hintUse.put(player.getUUID(), System.currentTimeMillis());
	}
	
	public void clearHintUsage(){
		hintUse.clear();
	}
	
	public void getHints(MinigamePlayer player){
		if(!hasTreasureLocation()) return;
		Location block = getTreasureLocation();
		if(player.getPlayer().getWorld().getName().equals(getTreasureLocation().getWorld().getName())){
			Location ploc = player.getLocation();
			double distance = ploc.distance(block);
			int maxradius = getMaxRadius();
			if(canUseHint(player)){
				if(distance > maxradius){
					player.sendMessage(ChatColor.LIGHT_PURPLE + MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.distance6"));
				}
				else if(distance > maxradius / 2){
					player.sendMessage(ChatColor.LIGHT_PURPLE + MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.distance5"));
				}
				else if(distance > maxradius / 4){
					player.sendMessage(ChatColor.LIGHT_PURPLE + MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.distance4"));
				}
				else if(distance > 50){
					player.sendMessage(ChatColor.LIGHT_PURPLE + MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.distance3"));
				}
				else if(distance > 20){
					player.sendMessage(ChatColor.LIGHT_PURPLE + MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.distance2"));
				}
				else if(distance < 20){
					player.sendMessage(ChatColor.LIGHT_PURPLE + MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.distance1"));
				}
				player.sendMessage(ChatColor.GRAY + MinigameUtils.formStr("minigame.treasurehunt.playerSpecificHint.timeLeft", 
						MinigameUtils.convertTime(getMinigame().getMinigameTimer().getTimeLeft())));
				player.sendMessage(ChatColor.GREEN + MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.globalHints"));
				if(getCurrentHints().isEmpty()){
					player.sendMessage(ChatColor.GRAY + MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.noHint"));
				}
				else{
					for(String h : getCurrentHints()){
						player.sendMessage(h);
					}
				}
				
				addHintUse(player);
			}
			else{
				player.sendMessage(ChatColor.RED + MinigameUtils.formStr("minigame.treasurehunt.playerSpecificHint.noUse", 
						getMinigame().getName(true)));
				int nextuse = (300000 - (int) (System.currentTimeMillis() - getLastHintUse(player))) / 1000;
				player.sendMessage(ChatColor.GRAY + MinigameUtils.formStr("minigame.treasurehunt.playerSpecificHint.nextUse", 
						MinigameUtils.convertTime(nextuse)));
				player.sendMessage(ChatColor.GRAY + MinigameUtils.formStr("minigame.treasurehunt.playerSpecificHint.treasureTimeLeft", 
						MinigameUtils.convertTime(getMinigame().getMinigameTimer().getTimeLeft())));
			}
		}
		else{
			String world = block.getWorld().getName();
			if(world.equalsIgnoreCase("world")){
				world = MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.wrongWorld.overworld");
			}
			player.sendMessage(ChatColor.RED + MinigameUtils.formStr("minigame.treasurehunt.playerSpecificHint.wrongWorld", world));
		}
	}
	
	public void setHintDelay(int time){
		hintWaitTime.setValue(time);
	}
	
	public int getHintDelay(){
		return hintWaitTime.getValue();
	}
	
	public void setTimer(int time){
		timer.setValue(time);
	}
	
	public int getTimer(){
		return timer.getValue();
	}
}
