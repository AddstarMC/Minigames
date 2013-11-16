package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class MinigamePlayer {
	private Player player;
	private ItemStack[] storedItems = null;
	private ItemStack[] storedArmour = null;
	private int food = 20;
	private double health = 20;
	private float saturation = 15;
	private boolean allowTP = true;
	private boolean allowGMChange = true;
	private GameMode lastGM = GameMode.SURVIVAL;
	private Scoreboard lastScoreboard = null;
	
	private Minigame minigame = null;
	private PlayerLoadout loadout = null;
	private boolean requiredQuit = false;
	private Location quitPos = null;
	private List<String> flags = new ArrayList<String>();
	private Location checkpoint = null;
	private int kills = 0;
	private int deaths = 0;
	private int score = 0;
	private long startTime = 0;
	private long endTime = 0;
	private long storedTime = 0;
	private int reverts = 0;
	
	private Menu menu = null;
	private boolean noClose = false;
	private MenuItem manualEntry = null;
	
	private Location selection1 = null;
	private Location selection2 = null;
	
	private PlayerData pdata = Minigames.plugin.pdata;
	
	public MinigamePlayer(Player player){
		this.player = player;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public String getName(){
		return player.getName();
	}
	
	public void sendMessage(String msg){
		player.sendMessage(msg);
	}
	
	public void sendMessage(String msg, String type){
		String init = "";
		if(type != null){
			if(type.equals("error")){
				init = ChatColor.RED + "[Minigames] " + ChatColor.WHITE;
			}
			else if(type.equals("win")){
				init = ChatColor.GREEN + "[Minigames] " + ChatColor.WHITE;
			}
			else{
				init = ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE;
			}
		}
		else{
			init = ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE;
		}
		player.sendMessage(init + msg);
	}
	
	public ItemStack[] getStoredItems(){
		return storedItems;
	}
	
	public ItemStack[] getStoredArmour(){
		return storedArmour;
	}
	
	public int getFood(){
		return food;
	}
	
	public double getHealth(){
		return health;
	}
	
	public float getSaturation(){
		return saturation;
	}
	
	public GameMode getLastGamemode(){
		return lastGM;
	}
	
	@SuppressWarnings("deprecation")
	public void storePlayerData(){
		storedItems = player.getInventory().getContents();
		storedArmour = player.getInventory().getArmorContents();
		food = player.getFoodLevel();
		health = player.getHealth();
		saturation = player.getSaturation();
		lastScoreboard = player.getScoreboard();
		lastGM = player.getGameMode();
		
		player.setSaturation(15);
		player.setFoodLevel(20);
		player.setHealth(player.getMaxHealth());
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		
		savePlayerData();
		player.updateInventory();
	}
	
	public void savePlayerData(){
		if(storedItems != null){
			int num = 0;
			for(ItemStack item : storedItems){
				if(item != null){
					pdata.invsave.getConfig().set("inventories." + player.getName() + "." + num, item);
				}
				num++;
			}
		}
		else{
			pdata.invsave.getConfig().set("inventories." + player.getName(), null);
			return;
		}
		
		if(storedArmour != null){
			int num = 0;
			for(ItemStack item : storedArmour){
				if(item != null){
					pdata.invsave.getConfig().set("inventories." + player.getName() + ".armour." + num, item);
				}
				num++;
			}
		}
		
		pdata.invsave.getConfig().set("inventories." + player.getName() + ".food", food);
		pdata.invsave.getConfig().set("inventories." + player.getName() + ".saturation", saturation);
		pdata.invsave.getConfig().set("inventories." + player.getName() + ".health", health);
		String gamemode = "SURVIVAL";
		if(lastGM.equals(GameMode.ADVENTURE))
			gamemode = "ADVENTURE";
		else if(lastGM.equals(GameMode.CREATIVE))
			gamemode = "CREATIVE";
		pdata.invsave.getConfig().set("inventories." + player.getName() + ".lastGM", gamemode);
		
		pdata.invsave.saveConfig();
	}
	
	public void setPlayerData(ItemStack[] items, ItemStack[] armour, int food, double health, float saturation, GameMode lastGM){
		storedItems = items;
		storedArmour = armour;
		this.food = food;
		this.health = health;
		this.saturation = saturation;
		this.lastGM = lastGM;
	}
	
	@SuppressWarnings("deprecation")
	public void restorePlayerData(){
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		
		player.getInventory().setContents(storedItems);
		player.getInventory().setArmorContents(storedArmour);
		player.setFoodLevel(food);
		if(health > 20){
			health = 20;
		}
		player.setHealth(health);
		player.setSaturation(saturation);
		if(lastScoreboard != null){
			player.setScoreboard(lastScoreboard);
		}
		else{
			player.setScoreboard(player.getServer().getScoreboardManager().getMainScoreboard());
		}
		
		allowGMChange = true;
		allowTP = true;
		player.setGameMode(lastGM);
		
		storedItems = null;
		storedArmour = null;
		
		pdata.invsave.getConfig().set("inventories." + player.getName(), null);
		pdata.invsave.saveConfig();
		
		player.updateInventory();
	}
	
	public boolean getAllowTeleport(){
		return allowTP;
	}
	
	public void setAllowTeleport(boolean allowTP){
		this.allowTP = allowTP;
	}
	
	public boolean getAllowGamemodeChange(){
		return allowGMChange;
	}
	
	public void setAllowGamemodeChange(boolean allowGMChange){
		this.allowGMChange = allowGMChange;
	}
	
	public Minigame getMinigame(){
		return minigame;
	}
	
	public void setMinigame(Minigame minigame){
		this.minigame = minigame;
	}
	
	public void removeMinigame(){
		minigame = null;
	}
	
	public boolean isInMinigame(){
		if(minigame != null)
			return true;
		return false;
	}
	
	public boolean isRequiredQuit() {
		return requiredQuit;
	}

	public void setRequiredQuit(boolean requiredQuit) {
		this.requiredQuit = requiredQuit;
	}

	public Location getQuitPos() {
		return quitPos;
	}

	public void setQuitPos(Location quitPos) {
		this.quitPos = quitPos;
	}

	public PlayerLoadout getLoadout() {
		if(loadout != null){
			return loadout;
		}
		else if(getMinigame().getRedTeam().contains(player.getPlayer()) && minigame.hasLoadout("red")){
			return minigame.getLoadout("red");
		}
		else if(getMinigame().getBlueTeam().contains(player.getPlayer()) && minigame.hasLoadout("blue")){
			return minigame.getLoadout("blue");
		}
		return minigame.getDefaultPlayerLoadout();
	}

	public void setLoadout(PlayerLoadout loadout) {
		this.loadout = loadout;
	}

	public List<String> getFlags(){
		return flags;
	}
	
	public boolean addFlag(String flag){
		if(!flags.contains(flag)){
			flags.add(flag);
			return true;
		}
		return false;
	}
	
	public boolean hasFlag(String flagName){
		if(flags.contains("flag")){
			return true;
		}
		return false;
	}
	
	public void setFlags(List<String> flags){
		this.flags = flags;
	}
	
	public void clearFlags(){
		flags.clear();
	}
	
	public Location getCheckpoint(){
		return checkpoint;
	}
	
	public void setCheckpoint(Location checkpoint){
		this.checkpoint = checkpoint;
	}
	
	public void removeCheckpoint(){
		checkpoint = null;
	}
	
	public int getKills(){
		return kills;
	}
	
	public void addKill(){
		kills++;
	}
	
	public void resetKills(){
		kills = 0;
	}
	
	public int getDeaths(){
		return deaths;
	}
	
	public void addDeath(){
		deaths++;
	}
	
	public void resetDeaths(){
		deaths = 0;
	}
	
	public void setDeaths(int deaths){
		this.deaths = deaths;
	}
	
	public int getScore(){
		return score;
	}
	
	public void addScore(){
		score++;
	}
	
	public void addScore(int amount){
		score += amount;
	}
	
	public void resetScore(){
		score = 0;
	}
	
	public void takeScore(){
		score--;
	}
	
	public void setScore(int score){
		this.score = score;
	}
	
	public void setStartTime(long ms){
		startTime = ms;
	}
	
	public void setEndTime(long ms){
		endTime = ms;
	}
	
	public long getStartTime(){
		return startTime;
	}
	
	public long getEndTime(){
		return endTime;
	}
	
	public void resetTime(){
		startTime = 0;
		endTime = 0;
		storedTime = 0;
	}
	
	public void setStoredTime(long ms){
		storedTime = ms;
	}
	
	public long getStoredTime(){
		return storedTime;
	}
	
	public void setReverts(int count){
		reverts = count;
	}
	
	public void addRevert(){
		reverts++;
	}
	
	public int getReverts(){
		return reverts;
	}
	
	public void resetReverts(){
		reverts = 0;
	}
	
	public Menu getMenu(){
		return menu;
	}
	
	public void setMenu(Menu menu){
		this.menu = menu;
	}
	
	public boolean isInMenu(){
		if(menu != null){
			return true;
		}
		return false;
	}
	
	public void setNoClose(boolean value){
		noClose = value;
	}
	
	public boolean getNoClose(){
		return noClose;
	}
	
	public void setManualEntry(MenuItem item){
		manualEntry = item;
	}
	
	public MenuItem getManualEntry(){
		return manualEntry;
	}
	
	public void addSelectionPoint(Location loc){
		if(selection1 == null){
			selection1 = loc;
			showSelection(false);
			sendMessage("Position 1 set", null);
		}
		else if(selection2 == null){
			selection2 = loc;
			showSelection(false);
			sendMessage("Position 2 set", null);
		}
		else if(selection2 != null){
			showSelection(true);
			selection1 = loc;
			sendMessage("Selection restarted", null);
			sendMessage("Position 1 set", null);
			selection2 = null;
			showSelection(false);
		}
	}
	
	public boolean hasSelection(){
		if(selection1 != null && selection2 != null)
			return true;
		return false;
	}
	
	public Location[] getSelectionPoints(){
		Location[] loc = new Location[2];
		if(hasSelection()){
			loc[0] = selection1;
			loc[1] = selection2;
			return loc;
		}
		return null;
	}
	
	public void clearSelection(){
		selection1 = null;
		selection2 = null;
	}
	
	public void setSelection(Location point1, Location point2){
		selection1 = point1;
		selection2 = point2;
		
		showSelection(false);
	}
	
	@SuppressWarnings("deprecation") //TODO: Use alternative once available
	public void showSelection(boolean clear){
		if(selection2 == null){
			getPlayer().sendBlockChange(selection1, Material.DIAMOND_BLOCK, (byte)0);
		}
		else{
			Location[] locs = MinigameUtils.getMinMaxSelection(selection1, selection2);

			int minx = locs[0].getBlockX();
			int miny = locs[0].getBlockY();
			int minz = locs[0].getBlockZ();
			int maxx = locs[1].getBlockX();
			int maxy = locs[1].getBlockY();
			int maxz = locs[1].getBlockZ();
			
			Location cur = new Location(selection1.getWorld(), minx, miny, minz);
			
			for(int x = minx; x <= maxx; x++){
				cur.setX(x);
				for(int y = miny; y <= maxy; y++){
					cur.setY(y);
					for(int z = minz; z <= maxz; z++){
						cur.setZ(z);
						if(((z == minz || z == maxz) && (x == minx || x == maxx) && (y == miny || y == maxy)) ||
								((x == minx || x == maxx) && (y == miny || y == maxy)) ||
								((z == minz || z == maxz) && (y == miny || y == maxy)) || 
								((z == minz || z == maxz) && (x == minx || x == maxx))){
							if(!clear)
								getPlayer().sendBlockChange(cur, Material.DIAMOND_BLOCK, (byte)0);
							else
								getPlayer().sendBlockChange(cur, cur.getBlock().getType(), cur.getBlock().getData());
						}
					}
				}
			}
		}
	}
}
