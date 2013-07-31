package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import com.pauldavdesign.mineauz.minigames.blockRecorder.RecorderData;

public class Minigame {
	private String name = "GenericName";
	private String type = null;
	private boolean enabled = false;
	private int minPlayers = 2;
	private int maxPlayers = 4;
	private List<String> flags = new ArrayList<String>();
	
	private Location spleefFloor1 = null;
	private Location spleefFloor2 = null;
	private String degenType = "inward";
	private int degenRandomChance = 15;
	private FloorDegenerator sfloordegen;
	private int floorDegenTime = Minigames.plugin.getConfig().getInt("multiplayer.floordegenerator.time");
	
	private List<Location> startLocations = new ArrayList<Location>();
	private Location endPosition = null;
	private Location quitPosition = null;
	private Location lobbyPosisiton = null;
	private PlayerLoadout defaultLoadout = new PlayerLoadout("default");
	private Map<String, PlayerLoadout> extraLoadouts = new HashMap<String, PlayerLoadout>();
	private Map<MinigamePlayer, PlayerLoadout> playerLoadouts = new HashMap<MinigamePlayer, PlayerLoadout>();
	private Map<String, RestoreBlock> restoreBlocks = new HashMap<String, RestoreBlock>();
	private String location = null;
	private int maxRadius = 1000;
	private int maxHeight = 20;
	private int minTreasure = 0;
	private int maxTreasure = 8;
	private ItemStack rewardItem = null;
	private double rewardPrice = 0;
	private ItemStack secondaryRewardItem = null;
	private double secondaryRewardPrice = 0;
	private boolean usePermissions = false;
	private int timer = 0;
	private Map<MinigamePlayer, CTFFlag> flagCarriers = new HashMap<MinigamePlayer, CTFFlag>();
	private Map<String, CTFFlag> droppedFlag = new HashMap<String, CTFFlag>();
	
	private boolean itemDrops = false;
	private boolean deathDrops = false;
	private boolean itemPickup = true;
	private boolean blockBreak = false;
	private boolean blockPlace = false;
	private int defaultGamemode = 2;
	private boolean blocksdrop = true;
	
	private String scoreType = "none";
	private boolean paintBallMode = false;
	private int paintBallDamage = 2;
	private boolean unlimitedAmmo = false;
	private boolean saveCheckpoint = false;
	private boolean lateJoin = false;
	private int lives = 0;
	
	private Location regenArea1 = null;
	private Location regenArea2 = null;
	
	//Teams
	//private List<Player> redTeam = new ArrayList<Player>();
	//private List<Player> blueTeam = new ArrayList<Player>();
	
	private int redTeamScore = 0;
	private int blueTeamScore = 0;
	private Scoreboard sbManager = Minigames.plugin.getServer().getScoreboardManager().getNewScoreboard();
	
	private int minScore = 5;
	private int maxScore = 10;

	private List<Location> startLocationsBlue = new ArrayList<Location>();
	private List<Location> startLocationsRed = new ArrayList<Location>();
	private String defaultWinner = "none";
	
	private boolean canSpectateFly = false;
	
	private boolean randomizeChests = false;
	private int minChestRandom = 5;
	private int maxChestRandom = 10;
	
	//Unsaved data
	private List<MinigamePlayer> players = new ArrayList<MinigamePlayer>();
	private List<MinigamePlayer> spectators = new ArrayList<MinigamePlayer>();
	private RecorderData blockRecorder = new RecorderData(this);
	//Multiplayer
	private MultiplayerTimer mpTimer = null;
	private MinigameTimer miniTimer = null;
	private MultiplayerBets mpBets = null;
	//TreasureHunt
	private TreasureHuntTimer thTimer = null;

	public Minigame(String name, String type, Location start){
		this.name = name;
		this.type = type;
		startLocations.add(start);
		
		sbManager.registerNewTeam("Red");
		sbManager.getTeam("Red").setPrefix(ChatColor.RED.toString());
		sbManager.getTeam("Red").setAllowFriendlyFire(false);
		sbManager.getTeam("Red").setCanSeeFriendlyInvisibles(true);
		sbManager.registerNewTeam("Blue");
		sbManager.getTeam("Blue").setPrefix(ChatColor.BLUE.toString());
		sbManager.getTeam("Blue").setAllowFriendlyFire(false);
		sbManager.getTeam("Blue").setCanSeeFriendlyInvisibles(true);
		sbManager.registerNewObjective(this.name, "dummy");
		sbManager.getObjective(this.name).setDisplaySlot(DisplaySlot.SIDEBAR);
		
	}
	
	public Minigame(String name){
		this.name = name;
		
		sbManager.registerNewTeam("Red");
		sbManager.getTeam("Red").setPrefix(ChatColor.RED.toString());
		sbManager.getTeam("Red").setAllowFriendlyFire(false);
		sbManager.getTeam("Red").setCanSeeFriendlyInvisibles(true);
		sbManager.registerNewTeam("Blue");
		sbManager.getTeam("Blue").setPrefix(ChatColor.BLUE.toString());
		sbManager.getTeam("Blue").setAllowFriendlyFire(false);
		sbManager.getTeam("Blue").setCanSeeFriendlyInvisibles(true);
		sbManager.registerNewObjective(this.name, "dummy");
		sbManager.getObjective(this.name).setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public void setSecondaryRewardItem(ItemStack secondaryRewardItem){
		this.secondaryRewardItem = secondaryRewardItem;
	}

	public ItemStack getSecondaryRewardItem(){
		return secondaryRewardItem;
	}

	public void setSecondaryRewardPrice(double secondaryRewardPrice) {
		this.secondaryRewardPrice = secondaryRewardPrice;
	}

	public double getSecondaryRewardPrice() {
		return secondaryRewardPrice;
	}

	public void setRewardItem(ItemStack rewardItem){
		this.rewardItem = rewardItem;
	}

	public ItemStack getRewardItem(){
		return rewardItem;
	}

	public void setRewardPrice(double rewardPrice) {
		this.rewardPrice = rewardPrice;
	}

	public double getRewardPrice() {
		return rewardPrice;
	}

	public void setMaxRadius(int maxRadius){
		this.maxRadius = maxRadius;
	}

	public int getMaxRadius(){
		return maxRadius;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public String getLocation(){
		return location;
	}

	public void setLocation(String location){
		this.location = location;
	}
	
	public boolean hasRestoreBlocks(){
		if(restoreBlocks.isEmpty()){
			return false;
		}
		return true;
	}
	
	public void addRestoreBlock(RestoreBlock block){
		restoreBlocks.put(block.getName(), block);
	}
	
	public boolean removeRestoreBlock(String name){
		if(restoreBlocks.containsKey(name)){
			restoreBlocks.remove(name);
			return true;
		}
		return false;
	}
	
	public Map<String, RestoreBlock> getRestoreBlocks(){
		return restoreBlocks;
	}
	
	public boolean hasFlags(){
		return !flags.isEmpty();
	}
	
	public void addFlag(String flag){
		flags.add(flag);
	}
	
	public void setFlags(List<String> flags){
		this.flags = flags;
	}
	
	public List<String> getFlags(){
		return flags;
	}
	
	public boolean removeFlag(String flag){
		if(flags.contains(flag)){
			flags.remove(flag);
			return true;
		}
		return false;
	}
	
	public void setStartLocation(Location loc){
		startLocations.set(0, loc);
	}
	
	public void addStartLocation(Location loc, int number){
		if(startLocations.size() >= number){
			startLocations.set(number - 1, loc);
		}
		else{
			startLocations.add(loc);
		}
	}
	
	public List<Location> getStartLocations(){
		return startLocations;
	}
	
	public boolean removeStartLocation(int locNumber){
		if(startLocations.size() < locNumber){
			startLocations.remove(locNumber);
			return true;
		}
		return false;
	}
	
	public PlayerLoadout getDefaultPlayerLoadout(){
		return defaultLoadout;
	}
	
	public boolean hasDefaultLoadout(){
		if(defaultLoadout.getItems().isEmpty()){
			return false;
		}
		return true;
	}
	
	public void addLoadout(String name){
		extraLoadouts.put(name, new PlayerLoadout(name));
	}
	
	public void deleteLoadout(String name){
		if(extraLoadouts.containsKey(name)){
			extraLoadouts.remove(name);
		}
	}
	
	public Set<String> getLoadouts(){
		return extraLoadouts.keySet();
	}
	
	public PlayerLoadout getLoadout(String name){
		PlayerLoadout pl = null;
		if(name.equalsIgnoreCase("default")){
			pl = getDefaultPlayerLoadout();
		}
		else{
			if(extraLoadouts.containsKey(name)){
				pl = extraLoadouts.get(name);
			}
		}
		return pl;
	}
	
	public boolean hasLoadouts(){
		if(extraLoadouts.isEmpty()){
			return false;
		}
		return true;
	}
	
	public boolean hasLoadout(String name){
		if(!name.equalsIgnoreCase("default")){
			return extraLoadouts.containsKey(name);
		}
		else{
			return true;
		}
	}
	
	public PlayerLoadout getPlayersLoadout(MinigamePlayer player){
		if(playerLoadouts.containsKey(player)){
			return playerLoadouts.get(player);
		}
		else if(getRedTeam().contains(player.getPlayer()) && extraLoadouts.containsKey("red")){
			return extraLoadouts.get("red");
		}
		else if(getBlueTeam().contains(player.getPlayer()) && extraLoadouts.containsKey("blue")){
			return extraLoadouts.get("blue");
		}
		return getDefaultPlayerLoadout();
	}
	
	public void setPlayersLoadout(MinigamePlayer player, PlayerLoadout loadout){
		playerLoadouts.put(player, loadout);
	}
	
	public void removePlayersLoadout(MinigamePlayer player){
		playerLoadouts.remove(player);
	}
	
	public void removeAllPlayerLoadouts(){
		playerLoadouts.clear();
	}
	
	public boolean isEnabled(){
		return enabled;
	}

	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}

	public int getMinPlayers(){
		return minPlayers;
	}

	public void setMinPlayers(int minPlayers){
		this.minPlayers = minPlayers;
	}

	public int getMaxPlayers(){
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers){
		this.maxPlayers = maxPlayers;
	}

	public Location getSpleefFloor1(){
		return spleefFloor1;
	}

	public void setSpleefFloor1(Location spleefFloor1){
		this.spleefFloor1 = spleefFloor1;
	}

	public Location getSpleefFloor2(){
		return spleefFloor2;
	}

	public void setSpleefFloor2(Location spleefFloor2){
		this.spleefFloor2 = spleefFloor2;
	}

	public String getDegenType() {
		return degenType;
	}

	public void setDegenType(String degenType) {
		this.degenType = degenType;
	}

	public int getDegenRandomChance() {
		return degenRandomChance;
	}

	public void setDegenRandomChance(int degenRandomChance) {
		this.degenRandomChance = degenRandomChance;
	}

	public Location getEndPosition(){
		return endPosition;
	}

	public void setEndPosition(Location endPosition){
		this.endPosition = endPosition;
	}

	public Location getQuitPosition(){
		return quitPosition;
	}

	public void setQuitPosition(Location quitPosition){
		this.quitPosition = quitPosition;
	}

	public Location getLobbyPosition(){
		return lobbyPosisiton;
	}

	public void setLobbyPosition(Location lobbyPosisiton){
		this.lobbyPosisiton = lobbyPosisiton;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getType(){
		return type;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public MultiplayerTimer getMpTimer() {
		return mpTimer;
	}

	public void setMpTimer(MultiplayerTimer mpTimer) {
		this.mpTimer = mpTimer;
	}
	
	public boolean hasStarted(){
		if(mpTimer != null && mpTimer.getStartWaitTimeLeft() == 0){
			return true;
		}
		return false;
	}
	
	public MinigameTimer getMinigameTimer() {
		return miniTimer;
	}

	public void setMinigameTimer(MinigameTimer mgTimer) {
		this.miniTimer = mgTimer;
	}

	public MultiplayerBets getMpBets() {
		return mpBets;
	}

	public void setMpBets(MultiplayerBets mpBets) {
		this.mpBets = mpBets;
	}

	public void setUsePermissions(boolean usePermissions) {
		this.usePermissions = usePermissions;
	}

	public boolean getUsePermissions() {
		return usePermissions;
	}

	public TreasureHuntTimer getThTimer() {
		return thTimer;
	}

	public void setThTimer(TreasureHuntTimer thTimer) {
		this.thTimer = thTimer;
	}

	public List<MinigamePlayer> getPlayers() {
		return players;
	}
	
	public void addPlayer(MinigamePlayer player){
		players.add(player);
	}
	
	public void removePlayer(MinigamePlayer player){
		if(players.contains(player)){
			players.remove(player);
		}
	}
	
	public boolean hasPlayers(){
		return !players.isEmpty();
	}
	
	public boolean hasSpectators(){
		return !spectators.isEmpty();
	}
	
	public List<MinigamePlayer> getSpectators() {
		return spectators;
	}
	
	public void addSpectator(MinigamePlayer player){
		spectators.add(player);
	}
	
	public void removeSpectator(MinigamePlayer player){
		if(spectators.contains(player)){
			spectators.remove(player);
		}
	}
	
	public boolean isSpectator(MinigamePlayer player){
		return spectators.contains(player);
	}

	public List<OfflinePlayer> getRedTeam() {
		List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
		for(OfflinePlayer offply : sbManager.getTeam("Red").getPlayers()){
			players.add(offply);
		}
		return players;
	}

	public void addRedTeamPlayer(MinigamePlayer player) {
		sbManager.getTeam("Red").addPlayer(player.getPlayer().getPlayer());
		player.getPlayer().setScoreboard(sbManager);
	}
	
	public void removeRedTeamPlayer(MinigamePlayer player){
		sbManager.getTeam("Red").removePlayer(player.getPlayer());
		player.getPlayer().setScoreboard(Minigames.plugin.getServer().getScoreboardManager().getMainScoreboard());
	}

	public List<OfflinePlayer> getBlueTeam() {
		List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
		for(OfflinePlayer offply : sbManager.getTeam("Blue").getPlayers()){
			players.add(offply);
		}
		return players;
	}

	public void addBlueTeamPlayer(MinigamePlayer player) {
		sbManager.getTeam("Blue").addPlayer(player.getPlayer().getPlayer());
		player.getPlayer().setScoreboard(sbManager);
	}
	
	public void removeBlueTeamPlayer(MinigamePlayer player){
		sbManager.getTeam("Blue").removePlayer(player.getPlayer());
		player.getPlayer().setScoreboard(Minigames.plugin.getServer().getScoreboardManager().getMainScoreboard());
	}
	
	public void setScore(MinigamePlayer ply, int amount){
		sbManager.getObjective(name).getScore(ply.getPlayer()).setScore(amount);
	}

	public int getRedTeamScore() {
		return redTeamScore;
	}

	public void setRedTeamScore(int redTeamScore) {
		this.redTeamScore = redTeamScore;
		if(redTeamScore != 0){
			getScoreboardManager().getObjective(name).getScore(Minigames.plugin.getServer().getOfflinePlayer(ChatColor.RED + "Red Team: ")).setScore(redTeamScore);
		}
		else{
			getScoreboardManager().resetScores(Minigames.plugin.getServer().getOfflinePlayer(ChatColor.RED + "Red Team: "));
		}
	}
	
	public void incrementRedTeamScore(){
		redTeamScore++;
		getScoreboardManager().getObjective(name).getScore(Minigames.plugin.getServer().getOfflinePlayer(ChatColor.RED + "Red Team: ")).setScore(redTeamScore);
	}

	public int getBlueTeamScore() {
		return blueTeamScore;
	}

	public void setBlueTeamScore(int blueTeamScore) {
		this.blueTeamScore = blueTeamScore;
		if(blueTeamScore != 0){
			getScoreboardManager().getObjective(name).getScore(Minigames.plugin.getServer().getOfflinePlayer(ChatColor.BLUE + "Blue Team: ")).setScore(blueTeamScore);
		}
		else{
			getScoreboardManager().resetScores(Minigames.plugin.getServer().getOfflinePlayer(ChatColor.BLUE + "Blue Team: "));
		}
	}
	
	public void incrementBlueTeamScore(){
		blueTeamScore++;
		getScoreboardManager().getObjective(name).getScore(Minigames.plugin.getServer().getOfflinePlayer(ChatColor.BLUE + "Blue Team: ")).setScore(blueTeamScore);
	}

	public int getMinScore() {
		return minScore;
	}

	public void setMinScore(int minScore) {
		this.minScore = minScore;
	}

	public int getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}
	
	public void addStartLocationBlue(Location loc, int number){
		if(startLocationsBlue.size() >= number){
			startLocationsBlue.set(number - 1, loc);
		}
		else{
			startLocationsBlue.add(loc);
		}
	}
	
	public List<Location> getStartLocationsBlue(){
		return startLocationsBlue;
	}
	
	public boolean removeStartLocationBlue(int locNumber){
		if(startLocationsBlue.size() < locNumber){
			startLocationsBlue.remove(locNumber);
			return true;
		}
		return false;
	}
	
	public void addStartLocationRed(Location loc, int number){
		if(startLocationsRed.size() >= number){
			startLocationsRed.set(number - 1, loc);
		}
		else{
			startLocationsRed.add(loc);
		}
	}
	
	public List<Location> getStartLocationsRed(){
		return startLocationsRed;
	}
	
	public boolean removeStartLocationRed(int locNumber){
		if(startLocationsRed.size() < locNumber){
			startLocationsRed.remove(locNumber);
			return true;
		}
		return false;
	}
	
	public int getMaxScorePerPlayer(int playerCount){
		float scorePerPlayer = getMaxScore() / getMaxPlayers();
		int score = (int) Math.round(scorePerPlayer * playerCount);
		if(score < minScore){
			score = minScore;
		}
		return score;
	}

	public int getMinTreasure() {
		return minTreasure;
	}

	public void setMinTreasure(int minTreasure) {
		this.minTreasure = minTreasure;
	}

	public int getMaxTreasure() {
		return maxTreasure;
	}

	public void setMaxTreasure(int maxTreasure) {
		this.maxTreasure = maxTreasure;
	}

	public FloorDegenerator getFloorDegenerator() {
		return sfloordegen;
	}

	public void addFloorDegenerator() {
		sfloordegen = new FloorDegenerator(getSpleefFloor1(), getSpleefFloor2(), this);
	}
	
	public void setTimer(int time){
		timer = time;
	}
	
	public int getTimer(){
		return timer;
	}

	public boolean hasItemDrops() {
		return itemDrops;
	}

	public void setItemDrops(boolean itemDrops) {
		this.itemDrops = itemDrops;
	}

	public boolean hasDeathDrops() {
		return deathDrops;
	}

	public void setDeathDrops(boolean deathDrops) {
		this.deathDrops = deathDrops;
	}

	public boolean hasItemPickup() {
		return itemPickup;
	}

	public void setItemPickup(boolean itemPickup) {
		this.itemPickup = itemPickup;
	}

	public RecorderData getBlockRecorder() {
		return blockRecorder;
	}

	public boolean canBlockBreak() {
		return blockBreak;
	}

	public boolean canBlockPlace() {
		return blockPlace;
	}

	public void setCanBlockPlace(boolean blockPlace) {
		this.blockPlace = blockPlace;
	}

	public void setCanBlockBreak(boolean blockBreak) {
		this.blockBreak = blockBreak;
	}

	public int getDefaultGamemodeInt() {
		return defaultGamemode;
	}
	
	public GameMode getDefaultGamemode() {
		return GameMode.getByValue(defaultGamemode);
	}

	public void setDefaultGamemode(int defaultGamemode) {
		this.defaultGamemode = defaultGamemode;
	}

	public boolean canBlocksdrop() {
		return blocksdrop;
	}

	public void setBlocksdrop(boolean blocksdrop) {
		this.blocksdrop = blocksdrop;
	}

	public String getScoreType() {
		return scoreType;
	}

	public void setScoreType(String scoreType) {
		this.scoreType = scoreType;
	}
	
	public boolean isFlagCarrier(MinigamePlayer ply){
		return flagCarriers.containsKey(ply);
	}
	
	public void addFlagCarrier(MinigamePlayer ply, CTFFlag flag){
		flagCarriers.put(ply, flag);
	}
	
	public void removeFlagCarrier(MinigamePlayer ply){
		flagCarriers.remove(ply);
	}
	
	public CTFFlag getFlagCarrier(MinigamePlayer ply){
		return flagCarriers.get(ply);
	}
	
	public void resetFlags(){
		for(MinigamePlayer ply : flagCarriers.keySet()){
			getFlagCarrier(ply).respawnFlag();
			getFlagCarrier(ply).stopCarrierParticleEffect();
		}
		flagCarriers.clear();
		for(String id : droppedFlag.keySet()){
			if(!getDroppedFlag(id).isAtHome()){
				getDroppedFlag(id).stopTimer();
				getDroppedFlag(id).respawnFlag();
			}
		}
		droppedFlag.clear();
	}
	
	public boolean hasDroppedFlag(String id){
		return droppedFlag.containsKey(id);
	}
	
	public void addDroppedFlag(String id, CTFFlag flag){
		droppedFlag.put(id, flag);
	}
	
	public void removeDroppedFlag(String id){
		droppedFlag.remove(id);
	}
	
	public CTFFlag getDroppedFlag(String id){
		return droppedFlag.get(id);
	}

	public boolean hasPaintBallMode() {
		return paintBallMode;
	}

	public void setPaintBallMode(boolean paintBallMode) {
		this.paintBallMode = paintBallMode;
	}

	public int getPaintBallDamage() {
		return paintBallDamage;
	}

	public void setPaintBallDamage(int paintBallDamage) {
		this.paintBallDamage = paintBallDamage;
	}

	public boolean hasUnlimitedAmmo() {
		return unlimitedAmmo;
	}

	public void setUnlimitedAmmo(boolean unlimitedAmmo) {
		this.unlimitedAmmo = unlimitedAmmo;
	}

	public boolean canSaveCheckpoint() {
		return saveCheckpoint;
	}

	public void setSaveCheckpoint(boolean saveCheckpoint) {
		this.saveCheckpoint = saveCheckpoint;
	}

	public boolean canLateJoin() {
		return lateJoin;
	}

	public void setLateJoin(boolean lateJoin) {
		this.lateJoin = lateJoin;
	}

	public boolean canSpectateFly() {
		return canSpectateFly;
	}

	public void setCanSpectateFly(boolean canSpectateFly) {
		this.canSpectateFly = canSpectateFly;
	}

	public boolean isRandomizeChests() {
		return randomizeChests;
	}

	public void setRandomizeChests(boolean randomizeChests) {
		this.randomizeChests = randomizeChests;
	}

	public int getMinChestRandom() {
		return minChestRandom;
	}

	public void setMinChestRandom(int minChestRandom) {
		this.minChestRandom = minChestRandom;
	}

	public int getMaxChestRandom() {
		return maxChestRandom;
	}

	public void setMaxChestRandom(int maxChestRandom) {
		this.maxChestRandom = maxChestRandom;
	}

	public Location getRegenArea1() {
		return regenArea1;
	}

	public void setRegenArea1(Location regenArea1) {
		this.regenArea1 = regenArea1;
	}

	public Location getRegenArea2() {
		return regenArea2;
	}

	public void setRegenArea2(Location regenArea2) {
		this.regenArea2 = regenArea2;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public int getFloorDegenTime() {
		return floorDegenTime;
	}

	public void setFloorDegenTime(int floorDegenTime) {
		this.floorDegenTime = floorDegenTime;
	}

	public String getDefaultWinner() {
		return defaultWinner;
	}

	public void setDefaultWinner(String defaultWinner) {
		this.defaultWinner = defaultWinner;
	}
	
	public Scoreboard getScoreboardManager(){
		return sbManager;
	}

	public void saveMinigame(){
		MinigameSave minigame = new MinigameSave(name, "config");
		
		if(!getStartLocations().isEmpty()){
			for(int i = 0; i < getStartLocations().size(); i++){
				Minigames.plugin.mdata.minigameSetLocations(name, getStartLocations().get(i), "startpos." + String.valueOf(i), minigame.getConfig());
			}
		}
		if(!getStartLocationsBlue().isEmpty()){
			for(int i = 0; i < getStartLocationsBlue().size(); i++){
				Minigames.plugin.mdata.minigameSetLocations(name, getStartLocationsBlue().get(i), "startposblue." + String.valueOf(i), minigame.getConfig());
			}
		}
		if(!getStartLocationsRed().isEmpty()){
			for(int i = 0; i < getStartLocationsRed().size(); i++){
				Minigames.plugin.mdata.minigameSetLocations(name, getStartLocationsRed().get(i), "startposred." + String.valueOf(i), minigame.getConfig());
			}
		}
		if(getEndPosition() != null){
			Minigames.plugin.mdata.minigameSetLocations(name, getEndPosition(), "endpos", minigame.getConfig());
		}
		if(getLobbyPosition() != null){
			Minigames.plugin.mdata.minigameSetLocations(name, getLobbyPosition(), "lobbypos", minigame.getConfig());
		}
		if(getQuitPosition() != null){
			Minigames.plugin.mdata.minigameSetLocations(name, getQuitPosition(), "quitpos", minigame.getConfig());
		}
		if(getSpleefFloor1() != null){
			Minigames.plugin.mdata.minigameSetLocations(name, getSpleefFloor1(), "sfloorpos.1", minigame.getConfig());
		}
		else{
			minigame.getConfig().set(name + ".sfloorpos", null);
		}
		if(getSpleefFloor2() != null){
			Minigames.plugin.mdata.minigameSetLocations(name, getSpleefFloor2(), "sfloorpos.2", minigame.getConfig());
		}
		else{
			minigame.getConfig().set(name + ".sfloorpos", null);
		}
		
		if(getDegenType() != "inward"){
			minigame.getConfig().set(name + ".degentype", getDegenType());
		}
		else{
			minigame.getConfig().set(name + ".degentype", null);
		}
		
		if(getDegenRandomChance() != 15){
			minigame.getConfig().set(name + ".degenrandom", getDegenRandomChance());
		}
		else{
			minigame.getConfig().set(name + ".degenrandom", null);
		}
		
		if(getMinTreasure() != 0){
			minigame.getConfig().set(name + ".mintreasure", getMinTreasure());
		}
		else{
			minigame.getConfig().set(name + ".mintreasure", null);
		}
		
		if(getMaxTreasure() != 8){
			minigame.getConfig().set(name + ".maxtreasure", getMaxTreasure());
		}
		else{
			minigame.getConfig().set(name + ".maxtreasure", null);
		}
		
		minigame.getConfig().set(name + ".type", getType());
		minigame.getConfig().set(name + ".minplayers", getMinPlayers());
		minigame.getConfig().set(name + ".maxplayers", getMaxPlayers());
		minigame.getConfig().set(name + ".bets", null);
		minigame.getConfig().set(name + ".enabled", isEnabled());
		if(getMaxRadius() != 1000){
			minigame.getConfig().set(name + ".maxradius", getMaxRadius());
		}
		else{
			minigame.getConfig().set(name + ".maxradius", null);
		}
		if(getMaxHeight() != 20){
			minigame.getConfig().set(name + ".maxheight", getMaxHeight());
		}
		else{
			minigame.getConfig().set(name + ".maxheight", null);
		}
		minigame.getConfig().set(name + ".usepermissions", usePermissions);
		if(getRewardItem() != null){
			minigame.getConfig().set(name + ".reward", getRewardItem());
		}
		else{
			minigame.getConfig().set(name + ".reward", null);
		}
		
		if(getSecondaryRewardItem() != null){
			minigame.getConfig().set(name + ".reward2", getSecondaryRewardItem());
		}
		else{
			minigame.getConfig().set(name + ".reward2", null);
		}
				
		if(getRewardPrice() != 0){
			minigame.getConfig().set(name + ".rewardprice", getRewardPrice());
		}
		else{
			minigame.getConfig().set(name + ".rewardprice", null);
		}
		
		if(getSecondaryRewardPrice() != 0){
			minigame.getConfig().set(name + ".rewardprice2", getSecondaryRewardPrice());
		}
		else{
			minigame.getConfig().set(name + ".rewardprice2", null);
		}
		
		if(!getFlags().isEmpty()){
			minigame.getConfig().set(name + ".flags", getFlags());
		}
		else{
			minigame.getConfig().set(name + ".flags", null);
		}
		
		if(hasDefaultLoadout()){
			minigame.getConfig().set(name + ".loadout", null);
			for(int i = 0; i < getDefaultPlayerLoadout().getItems().size(); i++){
				minigame.getConfig().set(name + ".loadout." + i, getDefaultPlayerLoadout().getItems().get(i));
			}
			
			if(!getDefaultPlayerLoadout().getAllPotionEffects().isEmpty()){
				for(PotionEffect eff : getDefaultPlayerLoadout().getAllPotionEffects()){
					minigame.getConfig().set(name + ".loadout.potions." + eff.getType().getName() + ".amp", eff.getAmplifier());
					minigame.getConfig().set(name + ".loadout.potions." + eff.getType().getName() + ".dur", eff.getDuration());
				}
			}
			else{
				minigame.getConfig().set(name + ".loadout.potions", null);
			}
			if(getDefaultPlayerLoadout().getUsePermissions()){
				minigame.getConfig().set(name + ".loadout.usepermissions", true);
			}
			else{
				minigame.getConfig().set(name + ".loadout.usepermissions", null);
			}
		}
		else{
			minigame.getConfig().set(name + ".loadout", null);
		}
		
		if(hasLoadouts()){
			for(String loadout : getLoadouts()){
				for(int i = 0; i < getLoadout(loadout).getItems().size(); i++){
					minigame.getConfig().set(name + ".extraloadouts." + loadout + "." + i, getLoadout(loadout).getItems().get(i));
				}
				if(!getLoadout(loadout).getAllPotionEffects().isEmpty()){
					for(PotionEffect eff : getLoadout(loadout).getAllPotionEffects()){
						minigame.getConfig().set(name + ".extraloadouts." + loadout + ".potions." + eff.getType().getName() + ".amp", eff.getAmplifier());
						minigame.getConfig().set(name + ".extraloadouts." + loadout + ".potions." + eff.getType().getName() + ".dur", eff.getDuration());
					}
				}
				else{
					minigame.getConfig().set(name + ".extraloadouts." + loadout + ".potions", null);
				}
				
				if(getLoadout(loadout).getUsePermissions()){
					minigame.getConfig().set(name + ".extraloadouts." + loadout + ".usepermissions", true);
				}
				else{
					minigame.getConfig().set(name + ".extraloadouts." + loadout + ".usepermissions", null);
				}
			}
		}
		else{
			minigame.getConfig().set(name + ".extraloadouts", null);
		}
		
		if(getMaxScore() != 10){
			minigame.getConfig().set(name + ".maxscore", getMaxScore());
		}
		else{
			minigame.getConfig().set(name + ".maxscore", null);
		}
		
		if(getMinScore() != 5){
			minigame.getConfig().set(name + ".minscore", getMinScore());
		}
		else{
			minigame.getConfig().set(name + ".minscore", null);
		}
		
		if(!restoreBlocks.isEmpty()){
			Set<String> blocks = restoreBlocks.keySet();
			for(String block : blocks){
				minigame.getConfig().set(name + ".resblocks." + block + ".type", restoreBlocks.get(block).getBlock().toString());
				Minigames.plugin.mdata.minigameSetLocationsShort(name, restoreBlocks.get(block).getLocation(), "resblocks." + block + ".location", minigame.getConfig());
			}
		}
		else{
			minigame.getConfig().set(name + ".resblocks", null);
		}
		
		if(getLocation() != null){
			minigame.getConfig().set(name + ".location", getLocation());
		}
		
		if(getTimer() > 0){
			minigame.getConfig().set(name + ".timer", getTimer());
		}
		else{
			minigame.getConfig().set(name + ".timer", null);
		}
		
		if(hasItemDrops()){
			minigame.getConfig().set(name + ".itemdrops", hasItemDrops());
		}
		else{
			minigame.getConfig().set(name + ".itemdrops", null);
		}
		
		if(hasDeathDrops()){
			minigame.getConfig().set(name + ".deathdrops", hasDeathDrops());
		}
		else{
			minigame.getConfig().set(name + ".deathdrops", null);
		}
		
		if(hasItemPickup()){
			minigame.getConfig().set(name + ".itempickup", hasItemPickup());
		}
		else{
			minigame.getConfig().set(name + ".itempickup", null);
		}
		
		if(canBlockBreak()){
			minigame.getConfig().set(name + ".blockbreak", canBlockBreak());
		}
		else{
			minigame.getConfig().set(name + ".blockbreak", null);
		}
		
		if(canBlockPlace()){
			minigame.getConfig().set(name + ".blockplace", canBlockPlace());
		}
		else{
			minigame.getConfig().set(name + ".blockplace", null);
		}
		
		if(getDefaultGamemodeInt() != 2){
			minigame.getConfig().set(name + ".gamemode", getDefaultGamemodeInt());
		}
		else{
			minigame.getConfig().set(name + ".gamemode", null);
		}
		
		if(!getBlockRecorder().getWBBlocks().isEmpty()){
			List<String> blocklist = new ArrayList<String>();
			for(Material mat : getBlockRecorder().getWBBlocks()){
				blocklist.add(mat.toString());
			}
			minigame.getConfig().set(name + ".whitelistblocks", blocklist);
		}
		else{
			minigame.getConfig().set(name + ".whitelistblocks", null);
		}
		
		if(getBlockRecorder().getWhitelistMode()){
			minigame.getConfig().set(name + ".whitelistmode", getBlockRecorder().getWhitelistMode());
		}
		else{
			minigame.getConfig().set(name + ".whitelistmode", null);
		}
		
		if(!canBlocksdrop()){
			minigame.getConfig().set(name + ".blocksdrop", canBlocksdrop());
		}
		else{
			minigame.getConfig().set(name + ".blocksdrop", null);
		}
		
		if(!getScoreType().equals("none")){
			minigame.getConfig().set(name + ".scoretype", getScoreType());
		}else{
			minigame.getConfig().set(name + ".scoretype", null);
		}
		
		if(hasPaintBallMode()){
			minigame.getConfig().set(name + ".paintball", hasPaintBallMode());
		}
		else{
			minigame.getConfig().set(name + ".paintball", null);
		}
		
		if(getPaintBallDamage() != 2){
			minigame.getConfig().set(name + ".paintballdmg", getPaintBallDamage());
		}
		else{
			minigame.getConfig().set(name + ".paintballdmg", null);
		}
		
		if(hasUnlimitedAmmo()){
			minigame.getConfig().set(name + ".unlimitedammo", hasUnlimitedAmmo());
		}
		else{
			minigame.getConfig().set(name + ".unlimitedammo", null);
		}
		
		if(canSaveCheckpoint()){
			minigame.getConfig().set(name + ".savecheckpoint", canSaveCheckpoint());
		}
		else{
			minigame.getConfig().set(name + ".savecheckpoint", null);
		}
		
		if(canLateJoin()){
			minigame.getConfig().set(name + ".latejoin", canLateJoin());
		}
		else{
			minigame.getConfig().set(name + ".latejoin", null);
		}
		
		if(canSpectateFly()){
			minigame.getConfig().set(name + ".canspectatefly", canSpectateFly());
		}
		else{
			minigame.getConfig().set(name + ".canspectatefly", null);
		}
		
		if(isRandomizeChests()){
			minigame.getConfig().set(name + ".randomizechests", isRandomizeChests());
		}
		else{
			minigame.getConfig().set(name + ".randomizechests", null);
		}
		if(getMinChestRandom() != 5){
			minigame.getConfig().set(name + ".minchestrandom", getMinChestRandom());
		}
		else{
			minigame.getConfig().set(name + ".minchestrandom", null);
		}
		if(getMaxChestRandom() != 10){
			minigame.getConfig().set(name + ".maxchestrandom", getMaxChestRandom());
		}
		else{
			minigame.getConfig().set(name + ".maxchestrandom", null);
		}
		
		if(getRegenArea1() != null){
			Minigames.plugin.mdata.minigameSetLocations(name, getRegenArea1(), "regenarea.1", minigame.getConfig());
		}
		else{
			minigame.getConfig().set(name + ".regenarea", null);
		}
		if(getRegenArea2() != null){
			Minigames.plugin.mdata.minigameSetLocations(name, getRegenArea2(), "regenarea.2", minigame.getConfig());
		}
		else{
			minigame.getConfig().set(name + ".regenarea", null);
		}
		
		if(getLives() != 0){
			minigame.getConfig().set(name + ".lives", getLives());
		}
		else{
			minigame.getConfig().set(name + ".lives", null);
		}
		
		if(getFloorDegenTime() != Minigames.plugin.getConfig().getInt("multiplayer.floordegenerator.time")){
			minigame.getConfig().set(name + ".floordegentime", getFloorDegenTime());
		}
		else{
			minigame.getConfig().set(name + ".floordegentime", null);
		}
		
		if(!getDefaultWinner().equals("none")){
			minigame.getConfig().set(name + ".defaultwinner", getDefaultWinner());
		}
		else{
			minigame.getConfig().set(name + ".defaultwinner", null);
		}
		
		minigame.saveConfig();
	}
	
	public void loadMinigame(){
		MinigameSave minigame = new MinigameSave(name, "config");
		
		if(minigame.getConfig().contains(name + ".startpos")){
			Set<String> locs = minigame.getConfig().getConfigurationSection(name + ".startpos").getKeys(false);
			
			for(int i = 0; i < locs.size(); i++){
				addStartLocation(Minigames.plugin.mdata.minigameLocations(name, "startpos." + String.valueOf(i), minigame.getConfig()), i + 1);
			}
		}
		if(minigame.getConfig().contains(name + ".startposred")){
			Set<String> locs = minigame.getConfig().getConfigurationSection(name + ".startposred").getKeys(false);
			
			for(int i = 0; i < locs.size(); i++){
				addStartLocationRed(Minigames.plugin.mdata.minigameLocations(name, "startposred." + String.valueOf(i), minigame.getConfig()), i + 1);
			}
		}
		if(minigame.getConfig().contains(name + ".startposblue")){
			Set<String> locs = minigame.getConfig().getConfigurationSection(name + ".startposblue").getKeys(false);
			
			for(int i = 0; i < locs.size(); i++){
				addStartLocationBlue(Minigames.plugin.mdata.minigameLocations(name, "startposblue." + String.valueOf(i), minigame.getConfig()), i + 1);
			}
		}
		if(minigame.getConfig().contains(name + ".endpos")){
			setEndPosition(Minigames.plugin.mdata.minigameLocations(name, "endpos", minigame.getConfig()));
		}
		if(minigame.getConfig().contains(name + ".lobbypos")){
			setLobbyPosition(Minigames.plugin.mdata.minigameLocations(name, "lobbypos", minigame.getConfig()));
		}
		if(minigame.getConfig().contains(name + ".quitpos")){
			setQuitPosition(Minigames.plugin.mdata.minigameLocations(name, "quitpos", minigame.getConfig()));
		}
		if(minigame.getConfig().contains(name + ".sfloorpos.1")){
			setSpleefFloor1(Minigames.plugin.mdata.minigameLocations(name, "sfloorpos.1", minigame.getConfig()));
		}
		if(minigame.getConfig().contains(name + ".sfloorpos.2")){
			setSpleefFloor2(Minigames.plugin.mdata.minigameLocations(name, "sfloorpos.2", minigame.getConfig()));
		}
		if(minigame.getConfig().contains(name + ".degentype")){
			setDegenType(minigame.getConfig().getString(name + ".degentype"));
		}
		if(minigame.getConfig().contains(name + ".degenrandom")){
			setDegenRandomChance(minigame.getConfig().getInt(name + ".degenrandom"));
		}
		if(minigame.getConfig().contains(name + ".mintreasure")){
			setMinTreasure(minigame.getConfig().getInt(name + ".mintreasure"));
		}
		if(minigame.getConfig().contains(name + ".maxtreasure")){
			setMaxTreasure(minigame.getConfig().getInt(name + ".maxtreasure"));
		}
		setType(minigame.getConfig().getString(name + ".type"));
		setMinPlayers(minigame.getConfig().getInt(name + ".minplayers"));
		setMaxPlayers(minigame.getConfig().getInt(name + ".maxplayers"));
		setEnabled(minigame.getConfig().getBoolean(name + ".enabled"));
		if(minigame.getConfig().contains(name + ".maxradius")){
			setMaxRadius(minigame.getConfig().getInt(name + ".maxradius"));
		}
		if(minigame.getConfig().contains(name + ".maxheight")){
			setMaxHeight(minigame.getConfig().getInt(name + ".maxheight"));
		}
		setUsePermissions(minigame.getConfig().getBoolean(name + ".usepermissions"));
		if(minigame.getConfig().contains(name + ".reward")){
			setRewardItem(minigame.getConfig().getItemStack(name + ".reward"));
		}
		if(minigame.getConfig().contains(name + ".reward2")){
			setSecondaryRewardItem(minigame.getConfig().getItemStack(name + ".reward2"));
		}
		if(minigame.getConfig().contains(name + ".rewardprice")){
			setRewardPrice(minigame.getConfig().getDouble(name + ".rewardprice"));
		}
		if(minigame.getConfig().contains(name + ".rewardprice2")){
			setSecondaryRewardPrice(minigame.getConfig().getDouble(name + ".rewardprice2"));
		}
		if(!minigame.getConfig().getStringList(name + ".flags").isEmpty()){
			setFlags(minigame.getConfig().getStringList(name + ".flags"));
		}
		if(minigame.getConfig().contains(name + ".loadout")){
			Set<String> keys = minigame.getConfig().getConfigurationSection(name + ".loadout").getKeys(false);
			for(int i = 0; i < keys.size(); i++){
				if(minigame.getConfig().contains(name + ".loadout." + i)){
					getDefaultPlayerLoadout().addItemToLoadout(minigame.getConfig().getItemStack(name + ".loadout." + i));
				}
			}
			
			if(minigame.getConfig().contains(name + ".loadout.potions")){
				keys = minigame.getConfig().getConfigurationSection(name + ".loadout.potions").getKeys(false);
				for(String eff : keys){
					if(PotionEffectType.getByName(eff) != null){
						PotionEffect effect = new PotionEffect(PotionEffectType.getByName(eff),
								minigame.getConfig().getInt(name + ".loadout.potions." + eff + ".dur"),
								minigame.getConfig().getInt(name + ".loadout.potions." + eff + ".amp"), true);
						getDefaultPlayerLoadout().addPotionEffect(effect);
					}
				}
			}
			
			if(minigame.getConfig().contains(name + ".loadout.usepermissions")){
				getDefaultPlayerLoadout().setUsePermissions(minigame.getConfig().getBoolean(name + ".loadout.usepermissions"));
			}
		}
		if(minigame.getConfig().contains(name + ".extraloadouts")){
			Set<String> keys = minigame.getConfig().getConfigurationSection(name + ".extraloadouts").getKeys(false);
			for(String loadout : keys){
				addLoadout(loadout);
				Set<String> items = minigame.getConfig().getConfigurationSection(name + ".extraloadouts." + loadout).getKeys(false);
				for(int i = 0; i < items.size(); i++){
					if(minigame.getConfig().contains(name + ".extraloadouts." + loadout + "." + i)){
						getLoadout(loadout).addItemToLoadout(minigame.getConfig().getItemStack(name + ".extraloadouts." + loadout + "." + i));
					}
				}
				if(minigame.getConfig().contains(name + ".extraloadouts." + loadout + ".potions")){
					Set<String> pots = minigame.getConfig().getConfigurationSection(name + ".extraloadouts." + loadout + ".potions").getKeys(false);
					for(String eff : pots){
						if(PotionEffectType.getByName(eff) != null){
							PotionEffect effect = new PotionEffect(PotionEffectType.getByName(eff),
									minigame.getConfig().getInt(name + ".extraloadouts." + loadout + ".potions." + eff + ".dur"),
									minigame.getConfig().getInt(name + ".extraloadouts." + loadout + ".potions." + eff + ".amp"));
							getLoadout(loadout).addPotionEffect(effect);
						}
					}
				}
				
				if(minigame.getConfig().contains(name + ".extraloadouts." + loadout + ".usepermissions")){
					getLoadout(loadout).setUsePermissions(minigame.getConfig().getBoolean(name + ".extraloadouts." + loadout + ".usepermissions"));
				}
			}
		}
		if(minigame.getConfig().contains(name + ".maxscore")){
			setMaxScore(minigame.getConfig().getInt(name + ".maxscore"));
		}
		if(minigame.getConfig().contains(name + ".minscore")){
			setMinScore(minigame.getConfig().getInt(name + ".minscore"));
		}
		if(minigame.getConfig().contains(name + ".resblocks") && minigame.getConfig().getString(name + ".resblocks") != "true" && minigame.getConfig().getString(name + ".resblocks") != "false"){
			Set<String> blocks = minigame.getConfig().getConfigurationSection(name + ".resblocks").getKeys(false);
			for(String block : blocks){
				RestoreBlock res = new RestoreBlock(block, Material.getMaterial(minigame.getConfig().getString(name + ".resblocks." + block + ".type")), Minigames.plugin.mdata.minigameLocationsShort(name, ".resblocks." + block + ".location", minigame.getConfig()));
				
				addRestoreBlock(res);
			}
		}
		if(minigame.getConfig().contains(name + ".location")){
			setLocation(minigame.getConfig().getString(name + ".location"));
		}
		
		if(minigame.getConfig().contains(name + ".timer")){
			setTimer(minigame.getConfig().getInt(name + ".timer"));
		}
		
		if(minigame.getConfig().contains(name + ".itemdrops")){
			setItemDrops(minigame.getConfig().getBoolean(name + ".itemdrops"));
		}
		
		if(minigame.getConfig().contains(name + ".deathdrops")){
			setDeathDrops(minigame.getConfig().getBoolean(name + ".deathdrops"));
		}
		
		if(minigame.getConfig().contains(name + ".itempickup")){
			setItemPickup(minigame.getConfig().getBoolean(name + ".itempickup"));
		}
		
		if(minigame.getConfig().contains(name + ".blockbreak")){
			setCanBlockBreak(minigame.getConfig().getBoolean(name + ".blockbreak"));
		}
		
		if(minigame.getConfig().contains(name + ".blockplace")){
			setCanBlockPlace(minigame.getConfig().getBoolean(name + ".blockplace"));
		}
		
		if(minigame.getConfig().contains(name + ".gamemode")){
			setDefaultGamemode(minigame.getConfig().getInt(name + ".gamemode"));
		}
		
		if(minigame.getConfig().contains(name + ".whitelistmode")){
			getBlockRecorder().setWhitelistMode(minigame.getConfig().getBoolean(name + ".whitelistmode"));
		}
		
		if(minigame.getConfig().contains(name + ".whitelistblocks")){
			List<String> blocklist = minigame.getConfig().getStringList(name + ".whitelistblocks");
			for(String block : blocklist){
				getBlockRecorder().addWBBlock(Material.matchMaterial(block));
			}
		}
		
		if(minigame.getConfig().contains(name + ".blocksdrop")){
			setBlocksdrop(minigame.getConfig().getBoolean(name + ".blocksdrop"));
		}
		
		if(minigame.getConfig().contains(name + ".scoretype")){
			setScoreType(minigame.getConfig().getString(name + ".scoretype"));
		}
		
		if(minigame.getConfig().contains(name + ".paintball")){
			setPaintBallMode(minigame.getConfig().getBoolean(name + ".paintball"));
		}
		
		if(minigame.getConfig().contains(name + ".paintballdmg")){
			setPaintBallDamage(minigame.getConfig().getInt(name + ".paintballdmg"));
		}
		
		if(minigame.getConfig().contains(name + ".unlimitedammo")){
			setUnlimitedAmmo(minigame.getConfig().getBoolean(name + ".unlimitedammo"));
		}
		
		if(minigame.getConfig().contains(name + ".savecheckpoint")){
			setSaveCheckpoint(minigame.getConfig().getBoolean(name + ".savecheckpoint"));
		}
		
		if(minigame.getConfig().contains(name + ".latejoin")){
			setLateJoin(minigame.getConfig().getBoolean(name + ".latejoin"));
		}
		
		if(minigame.getConfig().contains(name + ".canspectatefly")){
			setCanSpectateFly(minigame.getConfig().getBoolean(name + ".canspectatefly"));
		}
		
		if(minigame.getConfig().contains(name + ".randomizechests")){
			setRandomizeChests(minigame.getConfig().getBoolean(name + ".randomizechests"));
		}
		if(minigame.getConfig().contains(name + ".minchestrandom")){
			setMinChestRandom(minigame.getConfig().getInt(name + ".minchestrandom"));
		}
		if(minigame.getConfig().contains(name + ".maxchestrandom")){
			setMaxChestRandom(minigame.getConfig().getInt(name + ".maxchestrandom"));
		}
		
		if(minigame.getConfig().contains(name + ".regenarea.1")){
			setRegenArea1(Minigames.plugin.mdata.minigameLocations(name, "regenarea.1", minigame.getConfig()));
		}
		
		if(minigame.getConfig().contains(name + ".regenarea.2")){
			setRegenArea2(Minigames.plugin.mdata.minigameLocations(name, "regenarea.2", minigame.getConfig()));
		}
		
		if(minigame.getConfig().contains(name + ".lives")){
			setLives(minigame.getConfig().getInt(name + ".lives"));
		}
		
		if(minigame.getConfig().contains(name + ".floordegentime")){
			setFloorDegenTime(minigame.getConfig().getInt(name + ".floordegentime"));
		}
		
		if(minigame.getConfig().contains(name + ".defaultwinner")){
			setDefaultWinner(minigame.getConfig().getString(name + ".defaultwinner"));
		}
		
		if(getType().equals("th") && isEnabled()){
			Minigames.plugin.mdata.startGlobalMinigame(getName());
		}
		
		saveMinigame();
	}
	
	@Override
	public String toString(){
		return getName();
	}
}
