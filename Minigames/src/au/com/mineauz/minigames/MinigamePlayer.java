package au.com.mineauz.minigames;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

import au.com.mineauz.minigames.blockRecorder.RecorderData;
import au.com.mineauz.minigames.display.IDisplayCubiod;
import au.com.mineauz.minigames.events.JoinMinigameEvent;
import au.com.mineauz.minigames.events.PreJoinMinigameEvent;
import au.com.mineauz.minigames.events.SpectateMinigameEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.mechanics.GameMechanicBase;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuSession;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;

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
	private Team team = null;
	
	private MenuSession menu = null;
	private boolean noClose = false;
	private MenuItem manualEntry = null;
	private BukkitTask manualEntryTimer = null;
	
	private Location selection1 = null;
	private Location selection2 = null;
	private IDisplayCubiod selectionDisplay = null;
	
	private OfflineMinigamePlayer oply = null;
	private StoredPlayerCheckpoints spc = null;
	
	private List<String> claimedRewards = new ArrayList<String>();
	private List<String> tempClaimedRewards = new ArrayList<String>();
	private List<ItemStack> tempRewardItems = new ArrayList<ItemStack>();
	private List<ItemStack> rewardItems = new ArrayList<ItemStack>();
	private List<String> claimedScoreSigns = new ArrayList<String>();
	private int lateJoinTimer = -1;
	
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
		LoadoutModule module = minigame.getModule(LoadoutModule.class);
		
		if(loadout != null){
			return loadout;
		}
		else if(team != null && module.hasLoadout(team.getColor().toString().toLowerCase())){
			return module.getLoadout(team.getColor().toString().toLowerCase());
		}
		return module.getLoadout("default");
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
		if(player.getGameMode() != GameMode.CREATIVE)
			setCanFly(false);
		tempClaimedRewards.clear();
		tempRewardItems.clear();
		claimedScoreSigns.clear();
		if(lateJoinTimer != -1){
			Bukkit.getScheduler().cancelTask(lateJoinTimer);
			setLateJoinTimer(-1);
		}
	}
	
	public boolean isLatejoining() {
		return isLatejoining;
	}

	public void setLatejoining(boolean isLatejoining) {
		this.isLatejoining = isLatejoining;
	}

	@Deprecated
	public Menu getMenu(){
		return menu.current;
	}
	
	public MenuSession getMenuSession() {
		return menu;
	}
	
	public void setMenuSession(MenuSession menu){
		this.menu = menu;
	}
	
	public boolean isInMenu(){
		if(menu != null){
			return true;
		}
		return false;
	}
	
	public void showPreviousMenu() {
		if (menu != null) {
			MenuSession session = menu.previous;
			if (session != null) {
				session.current.displaySession(this, session);
			}
		}
	}
	
	public void showPreviousMenu(int backCount) {
		if (menu != null) {
			MenuSession session = menu;
			while(session != null && backCount > 0) {
				session = session.previous;
				--backCount;
			}
			
			if (session != null) {
				session.current.displaySession(this, session);
			}
		}
	}
	
	public void setNoClose(boolean value){
		noClose = value;
	}
	
	public boolean getNoClose(){
		return noClose;
	}
	
	public void startManualEntry(MenuItem item, int time) {
		manualEntry = item;
		manualEntryTimer = Bukkit.getScheduler().runTaskLater(Minigames.plugin, new Runnable() {
			@Override
			public void run() {
				noClose = false;
				manualEntry = null;
				manualEntryTimer = null;
				if (menu != null) {
					menu.current.displaySession(MinigamePlayer.this, menu);
				}
			}
		}, (long)(time * 20));
	}
	
	public void cancelMenuReopen() {
		if (manualEntryTimer != null) {
			manualEntryTimer.cancel();
		}
		manualEntry = null;
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
	
	public void showSelection(boolean clear){
		if (selectionDisplay != null) {
			selectionDisplay.remove();
			selectionDisplay = null;
		}
		
		if (!clear) {
			if(selection2 != null && selection1 != null) {
				Location[] locs = MinigameUtils.getMinMaxSelection(selection1, selection2);
				selectionDisplay = Minigames.plugin.display.displayCuboid(getPlayer(), locs[0], locs[1].add(1, 1, 1));
				selectionDisplay.show();
			} else if (selection1 != null) {
				selectionDisplay = Minigames.plugin.display.displayCuboid(getPlayer(), selection1, selection1.clone().add(1, 1, 1));
				selectionDisplay.show();
			} else if (selection2 != null) {
				selectionDisplay = Minigames.plugin.display.displayCuboid(getPlayer(), selection2, selection2.clone().add(1, 1, 1));
				selectionDisplay.show();
			}
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
	
	public void updateInventory(){
		getPlayer().updateInventory();
	}

	public boolean isDead() {
		return player.isDead();
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
	
	public void addRewardItem(ItemStack item){
		rewardItems.add(item);
	}
	
	public List<ItemStack> getRewardItems(){
		return rewardItems;
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
		if(!isDead()){
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
	
	public void claimRewards(){
		if(!isDead()){
			List<ItemStack> tempItems = new ArrayList<ItemStack>(getRewardItems());
			
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
	
	public void setLateJoinTimer(int taskID){
		lateJoinTimer = taskID;
	}
	
	private void checkPlayerJoin(Minigame minigame) throws IllegalStateException {
		if (this.minigame != null) {
			throw new IllegalStateException(MinigameUtils.getLang("player.join.alreadyPlaying"));
		}
		
		if (minigame.getUsePermissions() && !player.hasPermission("minigame.join." + minigame.getName(false).toLowerCase())) {
			throw new IllegalStateException(MinigameUtils.formStr("player.join.noMinigamePermission", "minigame.join." + minigame.getName(false).toLowerCase()));
		}
	}
	
	public boolean joinMinigame(Minigame minigame) throws IllegalStateException {
		Validate.notNull(minigame);
		
		checkPlayerJoin(minigame);
		
		PreJoinMinigameEvent event = new PreJoinMinigameEvent(this, minigame);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return false;
		}
		
		// Check game integrity
		minigame.checkMinigame(minigame, player.hasPermission("minigame.join.disabled"));
		
		GameMechanicBase mechanic = minigame.getMechanic();
		if (!mechanic.checkCanStart(minigame)) {
			return false;
		}
		
		// Send them into the game
		Location destination;
		switch (minigame.getType()) {
		case SINGLEPLAYER:
			destination = minigame.getStartLocations().get(RandomUtils.nextInt(minigame.getStartLocations().size()));
			break;
		case MULTIPLAYER:
			destination = minigame.getLobbyPosition();
			break;
		default:
			throw new AssertionError("Unimplemented join for minigame type " + minigame.getType());
		}
		
		// Admin warning for cross world teleport
		if(Minigames.plugin.getConfig().getBoolean("warnings") && player.getPlayer().getWorld() != destination.getWorld() && player.getPlayer().hasPermission("minigame.set.start")) {
			sendMessage(ChatColor.RED + "WARNING: " + ChatColor.WHITE + "Join location is across worlds! This may cause some server performance issues!", "error");
		}
		
		// Send the player in
		if (!teleport(destination)) {
			throw new IllegalStateException(MinigameUtils.getLang("minigame.error.noTeleport"));
		}
		
		// Give them the game type name
		if(minigame.getGametypeName() == null)
			sendMessage(MinigameUtils.formStr("player.join.plyInfo", minigame.getType().getName()), "win");
		else
			sendMessage(MinigameUtils.formStr("player.join.plyInfo", minigame.getGametypeName()), "win");
		
		// Give them the objective
		if(minigame.getObjective() != null){
			player.sendMessage(ChatColor.GREEN + "----------------------------------------------------");
			player.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + MinigameUtils.formStr("player.join.objective", 
					ChatColor.RESET.toString() + ChatColor.WHITE + minigame.getObjective()));
			player.sendMessage(ChatColor.GREEN + "----------------------------------------------------");
		}
		
		// Prepare regeneration region for rollback.
		if(minigame.getBlockRecorder().hasRegenArea() && !minigame.getBlockRecorder().hasCreatedRegenBlocks()){
			RecorderData d = minigame.getBlockRecorder();
			d.setCreatedRegenBlocks(true);
			
			World world = minigame.getRegenArea1().getWorld();
			for(int y = (int)d.getRegenMinY(); y <= d.getRegenMaxY(); y++){
				for(int x = (int)d.getRegenMinX(); x <= d.getRegenMaxX(); x++){
					for(int z = (int)d.getRegenMinZ(); z <= d.getRegenMaxZ(); z++){
						d.addBlock(world.getBlockAt(x,y,z), null);
					}
				}
			}
		}
		
		// From this point on the player will be in the minigame.
		storePlayerData();
		this.minigame = minigame;
		minigame.addPlayer(this);
		
		// Apply game settings to player
		setCheckpoint(player.getLocation());
		player.setFallDistance(0);
		player.setWalkSpeed(0.2f);
		setStartTime(Calendar.getInstance().getTimeInMillis());
		setGamemode(minigame.getDefaultGamemode());
		
		if(minigame.getType() == MinigameType.SINGLEPLAYER) {
			if(!minigame.isAllowedFlight()) {
				setCanFly(false);
			} else {
				setCanFly(true);
				if(minigame.isFlightEnabled())
					player.getPlayer().setFlying(true);
			}
		} else {
			// Dont set this yet because of lobby
			player.getPlayer().setAllowFlight(false);
		}
		
		// Apply module settings to player
		for (MinigameModule module : minigame.getModules()) {
			module.applySettings(this);
		}
		
		// Hide Spectators
		for(MinigamePlayer pl : minigame.getSpectators()){
			player.hidePlayer(pl.getPlayer());
		}
		
		if(minigame.getPlayers().size() == 1){
			//Register regen recorder events
			if(minigame.getBlockRecorder().hasRegenArea())
				Bukkit.getServer().getPluginManager().registerEvents(minigame.getBlockRecorder(), Minigames.plugin);
		}
		
		// Call Type specific join
		// TODO: Should be minigame.getType().something
		Minigames.plugin.mdata.minigameType(minigame.getType()).joinMinigame(this, minigame);
		
		// Call Mechanic specific join
		minigame.getMechanic().joinMinigame(minigame, this);

		// Send other players the join message.
		// TODO: Should be minigame.broadcast
		Minigames.plugin.mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.join.plyMsg", player.getDisplayName(), minigame.getName(true)), null, this);
		player.updateInventory();
		
		if(minigame.canDisplayScoreboard()){
			player.setScoreboard(minigame.getScoreboardManager());
			minigame.setScore(this, 1);
			minigame.setScore(this, 0);
		}
		
		Bukkit.getServer().getPluginManager().callEvent(new JoinMinigameEvent(this, minigame));
		
		return true;
	}
	
	public boolean joinMinigameWithBet(Minigame minigame, double money) throws IllegalStateException, IllegalArgumentException {
		checkPlayerJoin(minigame);
		
		if (minigame.getType() != MinigameType.MULTIPLAYER) {
			throw new IllegalArgumentException(MinigameUtils.getLang("player.bet.wrongType"));
		}
		
		if (money <= 0) {
			throw new IllegalArgumentException(MinigameUtils.getLang("player.bet.plyNoBet"));
		}
		
		MultiplayerBets bets = minigame.getMpBets();
		if (bets == null) {
			bets = new MultiplayerBets();
			minigame.setMpBets(bets);
		}
		
		if(!bets.canBet(this, money)) {
			throw new IllegalArgumentException(MinigameUtils.formStr("player.bet.incorrectAmount", bets.getHighestMoneyBet()));
		}
		
		if (!Minigames.plugin.getEconomy().has(player, money)) {
			throw new IllegalArgumentException(MinigameUtils.formStr("player.bet.notEnoughMoney", money));
		}
		
		if (!joinMinigame(minigame)) {
			return false;
		}
		
		bets.addBet(this, money);
		sendMessage(MinigameUtils.getLang("player.bet.plyMsg"), null);
		return true;
	}
	
	public boolean joinMinigameWithBet(Minigame minigame, ItemStack bet) throws IllegalStateException, IllegalArgumentException {
		checkPlayerJoin(minigame);
		
		if (minigame.getType() != MinigameType.MULTIPLAYER) {
			throw new IllegalArgumentException(MinigameUtils.getLang("player.bet.wrongType"));
		}
		
		if (bet == null || bet.getType() == Material.AIR) {
			throw new IllegalArgumentException(MinigameUtils.getLang("player.bet.plyNoBet"));
		}
		
		MultiplayerBets bets = minigame.getMpBets();
		if (bets == null) {
			bets = new MultiplayerBets();
			minigame.setMpBets(bets);
		}
		
		if(!bets.canBet(this, bet)) {
			throw new IllegalArgumentException(MinigameUtils.formStr("player.bet.incorrectItem", 1, bets.highestBetName()));
		}
		
		if (!joinMinigame(minigame)) {
			return false;
		}
		
		bets.addBet(this, bet);
		sendMessage(MinigameUtils.getLang("player.bet.plyMsg"), null);
		return true;
	}
	
	public boolean spectateMinigame(Minigame minigame) throws IllegalStateException {
		Validate.notNull(minigame);
		
		SpectateMinigameEvent event = new SpectateMinigameEvent(this, minigame);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if (event.isCancelled()) {
			return false;
		}
		
		Location destination = minigame.getSpectatorLocation();
		if (destination == null) {
			throw new IllegalStateException(MinigameUtils.getLang("minigame.error.noSpectatePos"));
		}
		
		// Admin warning for cross world teleport
		if(Minigames.plugin.getConfig().getBoolean("warnings") && player.getPlayer().getWorld() != destination.getWorld() && player.getPlayer().hasPermission("minigame.set.start")) {
			sendMessage(ChatColor.RED + "WARNING: " + ChatColor.WHITE + "Join location is across worlds! This may cause some server performance issues!", "error");
		}
		
		// Send the player in
		if (!teleport(destination)) {
			throw new IllegalStateException(MinigameUtils.getLang("minigame.error.noTeleport"));
		}
		
		// Setup player
		storePlayerData();
		this.minigame = minigame;
		setGamemode(GameMode.ADVENTURE);
		// TODO: Use spigot no entity collide thing
		
		minigame.addSpectator(this);
		
		if (minigame.canSpectateFly()) {
			player.setAllowFlight(true);
		}
		
		for(MinigamePlayer pl : minigame.getPlayers()){
			pl.getPlayer().hidePlayer(player.getPlayer());
		}
		
		player.getPlayer().setScoreboard(minigame.getScoreboardManager());
		
		for(PotionEffect potion : player.getPlayer().getActivePotionEffects()){
			player.getPlayer().removePotionEffect(potion.getType());
		}
		
		
		sendMessage(MinigameUtils.formStr("player.spectate.join.plyMsg", minigame.getName(false)) + "\n" +
				MinigameUtils.formStr("player.spectate.join.plyHelp", "\"/minigame quit\""), null);
		// TODO: Should be minigame.broadcast
		Minigames.plugin.mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.spectate.join.minigameMsg", player.getDisplayName(), minigame.getName(false)), null, this);
		
		return true;
	}
}
