package au.com.mineauz.minigames;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;

public class MinigamePlayer {
	private Player player;
	private boolean allowTP = false;
	private boolean allowGMChange = false;
	private boolean canFly = false;
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
	private boolean isLatejoining = false;
	private boolean isFrozen = false;
	private boolean canPvP = true;
	private boolean isInvincible = false;
	private boolean canInteract = true;
	private boolean isDead = false;
	private Team team = null;
	
	private Menu menu = null;
	private boolean noClose = false;
	private MenuItem manualEntry = null;
	
	private Location selection1 = null;
	private Location selection2 = null;
	
	private OfflineMinigamePlayer oply = null;
	private StoredPlayerCheckpoints spc = null;
	
	private List<String> claimedRewards = new ArrayList<String>();
	private List<String> tempClaimedRewards = new ArrayList<String>();
	private List<ItemStack> tempRewardItems = new ArrayList<ItemStack>();
	private List<String> claimedScoreSigns = new ArrayList<String>();
	
	public MinigamePlayer(Player player){
		this.player = player;
		spc = new StoredPlayerCheckpoints(getUUID().toString());
		
		File plcp = new File(Minigames.plugin.getDataFolder() + "/playerdata/checkpoints/" + getUUID().toString() + ".yml");
		if(plcp.exists()){
			getStoredPlayerCheckpoints().loadCheckpoints();
		}
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public String getName(){
		return player.getName();
	}
	
	public String getDisplayName(){
		return ChatColor.stripColor(player.getDisplayName());
	}
	
	public UUID getUUID(){
		return player.getUniqueId();
	}
	
	public Location getLocation(){
		return player.getLocation();
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
	
	@SuppressWarnings("deprecation")
	public void storePlayerData(){
		ItemStack[] storedItems = player.getInventory().getContents();
		ItemStack[] storedArmour = player.getInventory().getArmorContents();
		int food = player.getFoodLevel();
		double health = player.getHealth();
		float saturation = player.getSaturation();
		lastScoreboard = player.getScoreboard();
		GameMode lastGM = player.getGameMode();
		float exp = player.getExp();
		int level = player.getLevel();
		
		player.setSaturation(15);
		player.setFoodLevel(20);
		player.setHealth(player.getMaxHealth());
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setLevel(0);
		player.setExp(0);
		
		oply = new OfflineMinigamePlayer(getPlayer().getUniqueId().toString(), 
				storedItems, storedArmour, food, health, saturation, lastGM, exp, level, null);
		player.updateInventory();
	}
	
	@SuppressWarnings("deprecation")
	public void restorePlayerData(){
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		
		player.getInventory().setContents(oply.getStoredItems());
		player.getInventory().setArmorContents(oply.getStoredArmour());
		player.setFoodLevel(oply.getFood());
		if(oply.getHealth() > 20)
			player.setHealth(20);
		else
			player.setHealth(oply.getHealth());
		player.setSaturation(oply.getSaturation());
		if(lastScoreboard != null){
			player.setScoreboard(lastScoreboard);
		}
		else{
			player.setScoreboard(player.getServer().getScoreboardManager().getMainScoreboard());
		}
		
		if(oply.getExp() != -1){ //TODO: Remove check after 1.7
			player.setExp(oply.getExp());
			player.setLevel(oply.getLevel());
		}
		
		player.resetPlayerWeather();
		player.resetPlayerTime();
		allowGMChange = true;
		allowTP = true;
		player.setGameMode(oply.getLastGamemode());
		
		oply.deletePlayerData();
		oply = null;
		
		player.updateInventory();
	}
	
	public boolean hasStoredData(){
		if(oply != null)
			return true;
		return false;
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
		else if(team != null && LoadoutModule.getMinigameModule(minigame).hasLoadout(team.getColor().toString().toLowerCase())){
			return LoadoutModule.getMinigameModule(minigame).getLoadout(team.getColor().toString().toLowerCase());
		}
		return LoadoutModule.getMinigameModule(minigame).getLoadout("default");
	}

	public boolean setLoadout(PlayerLoadout loadout) {
		if(getMinigame() == null) return false;
		if(loadout == null || !getMinigame().isTeamGame() || loadout.getTeamColor() == null || getTeam().getColor() == loadout.getTeamColor()){
			this.loadout = loadout;
			return true;
		}
		return false;
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
		if(flags.contains(flagName)){
			return true;
		}
		return false;
	}
	
	public void setFlags(List<String> flags){
		this.flags.addAll(flags);
	}
	
	public void clearFlags(){
		flags.clear();
	}
	
	public Location getCheckpoint(){
		return checkpoint;
	}
	
	public boolean hasCheckpoint(){
		if(checkpoint != null)
			return true;
		return false;
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
	
	public boolean isFrozen() {
		return isFrozen;
	}

	public void setFrozen(boolean isFrozen) {
		this.isFrozen = isFrozen;
	}

	public boolean canPvP() {
		return canPvP;
	}

	public void setCanPvP(boolean canPvP) {
		this.canPvP = canPvP;
	}

	public boolean isInvincible() {
		return isInvincible;
	}

	public void setInvincible(boolean isInvincible) {
		this.isInvincible = isInvincible;
	}

	public boolean canInteract() {
		return canInteract;
	}

	public void setCanInteract(boolean canInteract) {
		this.canInteract = canInteract;
	}
	
	public boolean canFly(){
		return canFly;
	}
	
	public void setCanFly(boolean bool){
		canFly = bool;
		player.setAllowFlight(bool);
	}

	public void resetAllStats(){
//		setLoadout(null);
		loadout = null;
		resetReverts();
		resetDeaths();
		resetKills();
		resetScore();
		resetTime();
		clearFlags();
		removeCheckpoint();
		setFrozen(false);
		setCanPvP(true);
		setInvincible(false);
		setCanInteract(true);
		setLatejoining(false);
		setCanFly(false);
		tempClaimedRewards.clear();
		tempRewardItems.clear();
		claimedScoreSigns.clear();
	}
	
	public boolean isLatejoining() {
		return isLatejoining;
	}

	public void setLatejoining(boolean isLatejoining) {
		this.isLatejoining = isLatejoining;
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
		loc[0] = selection1;
		loc[1] = selection2;
		return loc;
	}
	
	public void clearSelection(){
		showSelection(true);
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
		if(selection2 != null && selection1 != null){
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
		else if(selection1 != null){
			getPlayer().sendBlockChange(selection1, Material.DIAMOND_BLOCK, (byte)0);
		}
		else if(selection2 != null){
			getPlayer().sendBlockChange(selection2, Material.DIAMOND_BLOCK, (byte)0);
		}
	}
	
	public OfflineMinigamePlayer getOfflineMinigamePlayer(){
		return oply;
	}
	
	public void setOfflineMinigamePlayer(OfflineMinigamePlayer oply){
		this.oply = oply;
	}

	public StoredPlayerCheckpoints getStoredPlayerCheckpoints() {
		return spc;
	}
	
	public void setGamemode(GameMode gamemode){
		setAllowGamemodeChange(true);
		player.setGameMode(gamemode);
		setAllowGamemodeChange(false);
	}

	public boolean teleport(Location location){
		boolean bool = false;
		
		setAllowTeleport(true);
		bool = getPlayer().teleport(location);
		setAllowTeleport(false);
		
		return bool;
	}
	
	@SuppressWarnings("deprecation")
	public void updateInventory(){
		getPlayer().updateInventory();
	}

	public boolean isDead() {
		return isDead; //Temporary Fix for silly bukkits set health function not changing Player.isDead() to true.
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}
	
	public void setTeam(Team team){
		this.team = team;
	}
	
	public Team getTeam(){
		return team;
	}
	
	public void removeTeam(){
		if(team != null){
			team.removePlayer(this);
			team = null;
		}
	}
	
	public boolean hasClaimedReward(String reward){
		if(claimedRewards.contains(reward))
			return true;
		return false;
	}
	
	public boolean hasTempClaimedReward(String reward){
		if(tempClaimedRewards.contains(reward))
			return true;
		return false;
	}
	
	public void addTempClaimedReward(String reward){
		tempClaimedRewards.add(reward);
	}
	
	public void addClaimedReward(String reward){
		claimedRewards.add(reward);
	}
	
	public void saveClaimedRewards(){
		if(!claimedRewards.isEmpty()){
			MinigameSave save = new MinigameSave("playerdata/data/" + getUUID().toString());
			FileConfiguration cfg = save.getConfig();
			cfg.set("claims", claimedRewards);
			save.saveConfig();
		}
	}
	
	public void loadClaimedRewards(){
		File f = new File(Minigames.plugin.getDataFolder() + "/playerdata/data/" + getUUID().toString() + ".yml");
		if(f.exists()){
			MinigameSave s = new MinigameSave("playerdata/data/" + getUUID().toString());
			claimedRewards = s.getConfig().getStringList("claims");
		}
	}
	
	public void addTempRewardItem(ItemStack item){
		tempRewardItems.add(item);
	}
	
	public List<ItemStack> getTempRewardItems(){
		return tempRewardItems;
	}
	
	public boolean hasClaimedScore(Location loc){
		String id = MinigameUtils.createLocationID(loc);
		if(claimedScoreSigns.contains(id))
			return true;
		return false;
	}
	
	public void addClaimedScore(Location loc){
		String id = MinigameUtils.createLocationID(loc);
		claimedScoreSigns.add(id);
	}
	
	public void claimTempRewardItems(){
		if(!isDead){
			List<ItemStack> tempItems = new ArrayList<ItemStack>(getTempRewardItems());
			
			if(!tempItems.isEmpty()){
				for(ItemStack item : tempItems){
					Map<Integer, ItemStack> m = player.getPlayer().getInventory().addItem(item);
					if(!m.isEmpty()){
						for(ItemStack i : m.values()){
							player.getPlayer().getWorld().dropItemNaturally(player.getPlayer().getLocation(), i);
						}
					}
				}
			}
		}
	}
}
