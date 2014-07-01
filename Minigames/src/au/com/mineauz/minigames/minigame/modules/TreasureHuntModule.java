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
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemTime;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameModule;

public class TreasureHuntModule extends MinigameModule{
	
	private StringFlag location = new StringFlag(null, "location");
	private IntegerFlag maxRadius = new IntegerFlag(1000, "maxradius");
	private IntegerFlag maxHeight = new IntegerFlag(20, "maxheight");
	private IntegerFlag minTreasure = new IntegerFlag(0, "mintreasure");
	private IntegerFlag maxTreasure = new IntegerFlag(8, "maxtreasure");
	private IntegerFlag treasureWaitTime = new IntegerFlag(Minigames.plugin.getConfig().getInt("treasurehunt.waittime"), "treasurehuntwait");
	private IntegerFlag hintWaitTime = new IntegerFlag(500, "hintWaitTime");
	
	//Unsaved Data
	private Location treasureLocation = null;
	private boolean treasureFound = false;
	private ArrayList<String> curHints = new ArrayList<String>();
	private Map<UUID, Long> hintUse = new HashMap<UUID, Long>();

	public TreasureHuntModule(Minigame mgm) {
		super(mgm);
	}

	@Override
	public String getName() {
		return "TreasureHunt";
	}

	@Override
	public Map<String, Flag<?>> getFlags() {
		Map<String, Flag<?>> flags = new HashMap<String, Flag<?>>();
		flags.put(location.getName(), location);
		flags.put(maxRadius.getName(), maxRadius);
		flags.put(maxHeight.getName(), maxHeight);
		flags.put(minTreasure.getName(), minTreasure);
		flags.put(maxTreasure.getName(), maxTreasure);
		flags.put(treasureWaitTime.getName(), treasureWaitTime);
		flags.put(hintWaitTime.getName(), hintWaitTime);
		return flags;
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
	public void addMenuOptions(Menu menu) {
		
	}
	
	@Override
	public boolean getMenuOptions(Menu previous){
		Menu treasureHunt = new Menu(6, getMinigame().getName(false), previous.getViewer());
		
		List<MenuItem> itemsTreasureHunt = new ArrayList<MenuItem>(5);
		itemsTreasureHunt.add(location.getMenuItem("Location Name", Material.BED, MinigameUtils.stringToList("Name to appear when;treasure spawns")));
		itemsTreasureHunt.add(maxRadius.getMenuItem("Max. Radius", Material.ENDER_PEARL, 10, null));
		List<String> maxHeightDes = new ArrayList<String>();
		maxHeightDes.add("Max. height of where a");
		maxHeightDes.add("chest can generate.");
		maxHeightDes.add("Can still move above to");
		maxHeightDes.add("avoid terrain");
		itemsTreasureHunt.add(maxHeight.getMenuItem("Max. Height", Material.BEACON, maxHeightDes, 1, 256));
		List<String> minDes = new ArrayList<String>();
		minDes.add("Minimum items to");
		minDes.add("spawn in chest.");
		itemsTreasureHunt.add(minTreasure.getMenuItem("Min. Items", Material.STEP, minDes, 0, 27));
		List<String> maxDes = new ArrayList<String>();
		maxDes.add("Maximum items to");
		maxDes.add("spawn in chest.");
		itemsTreasureHunt.add(maxTreasure.getMenuItem("Max. Items", Material.DOUBLE_STEP, maxDes, 0, 27));
		itemsTreasureHunt.add(new MenuItemTime("Restart Delay", Material.WATCH, treasureWaitTime.getCallback(), 0, null));
		itemsTreasureHunt.add(new MenuItemTime("Hint Usage Delay", Material.WATCH, hintWaitTime.getCallback(), 0, null));
		treasureHunt.addItems(itemsTreasureHunt);
		treasureHunt.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), treasureHunt.getSize() - 9);
		treasureHunt.displayMenu(treasureHunt.getViewer());
		return true;
	}
	
	public static TreasureHuntModule getMinigameModule(Minigame minigame){
		return (TreasureHuntModule) minigame.getModule("TreasureHunt");
	}

	public void setMaxRadius(int maxRadius){
		this.maxRadius.setFlag(maxRadius);
	}

	public int getMaxRadius(){
		return maxRadius.getFlag();
	}

	public int getMaxHeight() {
		return maxHeight.getFlag();
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight.setFlag(maxHeight);
	}

	public String getLocation(){
		return location.getFlag();
	}

	public void setLocation(String location){
		this.location.setFlag(location);
	}

	public int getMinTreasure() {
		return minTreasure.getFlag();
	}

	public void setMinTreasure(int minTreasure) {
		this.minTreasure.setFlag(minTreasure);
	}

	public int getMaxTreasure() {
		return maxTreasure.getFlag();
	}

	public void setMaxTreasure(int maxTreasure) {
		this.maxTreasure.setFlag(maxTreasure);
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
		return treasureWaitTime.getFlag();
	}
	
	public void setTreasureWaitTime(int time){
		treasureWaitTime.setFlag(time);
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
		hintWaitTime.setFlag(time);
	}
	
	public int getHintDelay(){
		return hintWaitTime.getFlag();
	}
}
