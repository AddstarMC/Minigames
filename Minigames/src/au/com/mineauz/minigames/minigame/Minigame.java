package au.com.mineauz.minigames.minigame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.collect.Maps;

import au.com.mineauz.minigames.FloorDegenerator;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameSave;
import au.com.mineauz.minigames.MinigameTimer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.MultiplayerBets;
import au.com.mineauz.minigames.MultiplayerTimer;
import au.com.mineauz.minigames.blockRecorder.RecorderData;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.EnumFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.ListFlag;
import au.com.mineauz.minigames.config.LocationFlag;
import au.com.mineauz.minigames.config.LocationListFlag;
import au.com.mineauz.minigames.config.RewardsFlag;
import au.com.mineauz.minigames.config.SimpleLocationFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.events.MinigameInitializeEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.mechanics.GameMechanicBase;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemAddFlag;
import au.com.mineauz.minigames.menu.MenuItemDisplayRewards;
import au.com.mineauz.minigames.menu.MenuItemDisplayWhitelist;
import au.com.mineauz.minigames.menu.MenuItemEnum;
import au.com.mineauz.minigames.menu.MenuItemFlag;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemSubMenu;
import au.com.mineauz.minigames.menu.MenuItemSaveMinigame;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.menu.MenuItemTime;
import au.com.mineauz.minigames.menu.MenuItemValue;
import au.com.mineauz.minigames.menu.MenuItemValue.IMenuItemChange;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.Rewards;

public class Minigame {
	private Map<String, Flag<?>> configFlags = new HashMap<String, Flag<?>>();
	
	private final String name;
	private StringFlag displayName = new StringFlag(null, "displayName");
	private StringFlag objective = new StringFlag(null, "objective");
	private StringFlag gametypeName = new StringFlag(null, "gametypeName");
	private EnumFlag<MinigameType> type = new EnumFlag<MinigameType>(MinigameType.SINGLEPLAYER, "type");
	private BooleanFlag enabled = new BooleanFlag(false, "enabled");
	private IntegerFlag minPlayers = new IntegerFlag(2, "minplayers");
	private IntegerFlag maxPlayers = new IntegerFlag(4, "maxplayers");
	private BooleanFlag spMaxPlayers = new BooleanFlag(false, "spMaxPlayers");
	private ListFlag flags = new ListFlag(null, "flags");
	private MinigameState state = MinigameState.IDLE;
	
	private SimpleLocationFlag floorDegen1 = new SimpleLocationFlag(null, "sfloorpos.1");
	private SimpleLocationFlag floorDegen2 = new SimpleLocationFlag(null, "sfloorpos.2");
	private StringFlag degenType = new StringFlag("inward", "degentype");
	private IntegerFlag degenRandomChance = new IntegerFlag(15, "degenrandom");
	private FloorDegenerator sfloordegen;
	private IntegerFlag floorDegenTime = new IntegerFlag(Minigames.plugin.getConfig().getInt("multiplayer.floordegenerator.time"), "floordegentime");
	
	private LocationListFlag startLocations = new LocationListFlag(null, "startpos");
	private LocationFlag endPosition = new LocationFlag(null, "endpos");
	private LocationFlag quitPosition = new LocationFlag(null, "quitpos");
	private LocationFlag lobbyPosisiton = new LocationFlag(null, "lobbypos");
	private LocationFlag spectatorPosition = new LocationFlag(null, "spectatorpos");
	
	private Rewards rewardItem = new Rewards();
	private RewardsFlag rewardItemFlag = new RewardsFlag(null, "reward");
	private Rewards secondaryRewardItem = new Rewards();
	private RewardsFlag secondaryRewardItemFlag = new RewardsFlag(null, "reward2");
	private BooleanFlag usePermissions = new BooleanFlag(false, "usepermissions");
	private IntegerFlag timer = new IntegerFlag(0, "timer");
	private BooleanFlag useXPBarTimer = new BooleanFlag(true, "useXPBarTimer");
	private IntegerFlag startWaitTime = new IntegerFlag(0, "startWaitTime");
	
	private BooleanFlag itemDrops = new BooleanFlag(false, "itemdrops");
	private BooleanFlag deathDrops = new BooleanFlag(false, "deathdrops");
	private BooleanFlag itemPickup = new BooleanFlag(true, "itempickup");
	private BooleanFlag blockBreak = new BooleanFlag(false, "blockbreak");
	private BooleanFlag blockPlace = new BooleanFlag(false, "blockplace");
	private EnumFlag<GameMode> defaultGamemode = new EnumFlag<GameMode>(GameMode.ADVENTURE, "gamemode");
	private BooleanFlag blocksdrop = new BooleanFlag(true, "blocksdrop");
	private BooleanFlag allowEnderpearls = new BooleanFlag(false, "allowEnderpearls");
	private BooleanFlag allowMPCheckpoints = new BooleanFlag(false, "allowMPCheckpoints");
	private BooleanFlag allowFlight = new BooleanFlag(false, "allowFlight");
	private BooleanFlag enableFlight = new BooleanFlag(false, "enableFlight");
	
	private StringFlag mechanic = new StringFlag("custom", "scoretype");
	private BooleanFlag paintBallMode = new BooleanFlag(false, "paintball");
	private IntegerFlag paintBallDamage = new IntegerFlag(2, "paintballdmg");
	private BooleanFlag unlimitedAmmo = new BooleanFlag(false, "unlimitedammo");
	private BooleanFlag saveCheckpoints = new BooleanFlag(false, "saveCheckpoints");
	private BooleanFlag lateJoin = new BooleanFlag(false, "latejoin");
	private IntegerFlag lives = new IntegerFlag(0, "lives");
	
	private LocationFlag regenArea1 = new LocationFlag(null, "regenarea.1");
	private LocationFlag regenArea2 = new LocationFlag(null, "regenarea.2");
	private IntegerFlag regenDelay = new IntegerFlag(0, "regenDelay");
	
	private Map<Class<? extends MinigameModule>, MinigameModule> modules = Maps.newHashMap();
	private Map<Class<? extends MinigameModule>, MinigameModule> cachedModules = Maps.newHashMap();
	private FileConfiguration cachedConfig;
	
	private Scoreboard sbManager = Minigames.plugin.getServer().getScoreboardManager().getNewScoreboard();
	
	private IntegerFlag minScore = new IntegerFlag(5, "minscore");
	private IntegerFlag maxScore = new IntegerFlag(10, "maxscore");
	private BooleanFlag displayScoreboard = new BooleanFlag(true, "displayScoreboard");
	
	private BooleanFlag canSpectateFly = new BooleanFlag(false, "canspectatefly");
	
	private BooleanFlag randomizeChests = new BooleanFlag(false, "randomizechests");
	private IntegerFlag minChestRandom = new IntegerFlag(5, "minchestrandom");
	private IntegerFlag maxChestRandom = new IntegerFlag(10, "maxchestrandom");
	
	private ScoreboardData sbData = new ScoreboardData();
	
	//Unsaved data
	private List<MinigamePlayer> players = new ArrayList<MinigamePlayer>();
	private List<MinigamePlayer> spectators = new ArrayList<MinigamePlayer>();
	private RecorderData blockRecorder = new RecorderData(this);
	private boolean regenerating = false;
	//Multiplayer
	private MultiplayerTimer mpTimer = null;
	private MinigameTimer miniTimer = null;
	private MultiplayerBets mpBets = null;

	public Minigame(String name, MinigameType type, Location start){
		this.name = name;
		setup(type, start);
	}
	
	public Minigame(String name){
		this.name = name;
		setup(MinigameType.SINGLEPLAYER, null);
	}
	
	private void setup(MinigameType type, Location start){
		this.type.setFlag(type);
		startLocations.setFlag(new ArrayList<Location>());
		rewardItemFlag.setFlag(rewardItem);
		secondaryRewardItemFlag.setFlag(secondaryRewardItem);
		
		if(start != null)
			startLocations.getFlag().add(start);
		
		sbManager.registerNewObjective(this.name, "dummy");
		sbManager.getObjective(this.name).setDisplaySlot(DisplaySlot.SIDEBAR);
		
		flags.setFlag(new ArrayList<String>());
		
		addConfigFlag(allowEnderpearls);
		addConfigFlag(allowFlight);
		addConfigFlag(allowMPCheckpoints);
		addConfigFlag(blockBreak);
		addConfigFlag(blockPlace);
		addConfigFlag(blocksdrop);
		addConfigFlag(canSpectateFly);
		addConfigFlag(deathDrops);
		addConfigFlag(defaultGamemode);
		addConfigFlag(degenRandomChance);
		addConfigFlag(degenType);
		addConfigFlag(displayName);
		addConfigFlag(enableFlight);
		addConfigFlag(enabled);
		addConfigFlag(endPosition);
		addConfigFlag(flags);
		addConfigFlag(floorDegen1);
		addConfigFlag(floorDegen2);
		addConfigFlag(floorDegenTime);
		addConfigFlag(gametypeName);
		addConfigFlag(itemDrops);
		addConfigFlag(itemPickup);
		addConfigFlag(lateJoin);
		addConfigFlag(lives);
		addConfigFlag(lobbyPosisiton);
		addConfigFlag(maxChestRandom);
		addConfigFlag(maxPlayers);
		addConfigFlag(maxScore);
		addConfigFlag(minChestRandom);
		addConfigFlag(minPlayers);
		addConfigFlag(minScore);
		addConfigFlag(objective);
		addConfigFlag(paintBallDamage);
		addConfigFlag(paintBallMode);
		addConfigFlag(quitPosition);
		addConfigFlag(randomizeChests);
		addConfigFlag(regenArea1);
		addConfigFlag(regenArea2);
		addConfigFlag(regenDelay);
		addConfigFlag(rewardItemFlag);
		addConfigFlag(saveCheckpoints);
		addConfigFlag(mechanic);
		addConfigFlag(secondaryRewardItemFlag);
		addConfigFlag(spMaxPlayers);
		addConfigFlag(startLocations);
		addConfigFlag(startWaitTime);
		addConfigFlag(timer);
		addConfigFlag(this.type);
		addConfigFlag(unlimitedAmmo);
		addConfigFlag(usePermissions);
		addConfigFlag(useXPBarTimer);
		addConfigFlag(spectatorPosition);
		addConfigFlag(displayScoreboard);
		
		initialize();
	}
		
	private void initialize() {
		cachedModules.putAll(modules);
		modules.clear();
		
		// Add modules for the minigame type
		for (Class<? extends MinigameModule> module : Minigames.plugin.modules.getDefaultModules(type.getFlag())) {
			modules.put(module, MinigameModule.makeModule(module, this));
		}
		
		// Add modules for the mechanic
		GameMechanicBase mechanic = getMechanic();
		if (mechanic != null) {
			mechanic.addRequiredModules(this);
		}
		
		// Fire event to get even more modules
		MinigameInitializeEvent event = new MinigameInitializeEvent(this);
		Bukkit.getPluginManager().callEvent(event);
	}
	
	
	public MinigameState getState(){
		return state;
	}
	
	public void setState(MinigameState state){
		this.state = state;
	}
	
	private void addConfigFlag(Flag<?> flag){
		configFlags.put(flag.getName(), flag);
	}
	
	public Flag<?> getConfigFlag(String name){
		return configFlags.get(name);
	}
	
	public boolean addModule(Class<? extends MinigameModule> module) {
		if(!modules.containsKey(module)){
			if (cachedModules.containsKey(module)) {
				modules.put(module, cachedModules.remove(module));
			} else {
				modules.put(module, MinigameModule.makeModule(module, this));
			}
			return true;
		}
		return false;
	}
	
	public void removeModule(Class<? extends MinigameModule> module){
		if (modules.containsKey(module)) {
			cachedModules.put(module, modules.remove(module));
		}
	}
	
	public Collection<MinigameModule> getModules(){
		return Collections.unmodifiableCollection(modules.values());
	}
	
	public void clearCachedModules() {
		cachedModules.clear();
	}
	
	
	@SuppressWarnings("unchecked")
	public <T extends MinigameModule> T getModule(Class<T> moduleClass) {
		return (T)modules.get(moduleClass);
	}
	
	public boolean hasModule(Class<? extends MinigameModule> moduleClass) {
		return modules.containsKey(moduleClass);
	}
	
	public boolean isTeamGame(){
		// FIXME: This should not attempt to get teams module without checking
		if(getType() == MinigameType.MULTIPLAYER && getModule(TeamsModule.class).getTeams().size() > 0)
			return true;
		return false;
	}

	public List<RewardType> getSecondaryRewardItem(){
		return secondaryRewardItem.getReward();
	}
	
	public Rewards getSecondaryRewardItems(){
		return secondaryRewardItem;
	}

	public List<RewardType> getRewardItem(){
		return rewardItem.getReward();
	}
	
	public Rewards getRewardItems(){
		return rewardItem;
	}
	
	public boolean hasFlags(){
		return !flags.getFlag().isEmpty();
	}
	
	public void addFlag(String flag){
		flags.getFlag().add(flag);
	}
	
	public void setFlags(List<String> flags){
		this.flags.setFlag(flags);
	}
	
	public List<String> getFlags(){
		return flags.getFlag();
	}
	
	public boolean removeFlag(String flag){
		if(flags.getFlag().contains(flag)){
			flags.getFlag().remove(flag);
			return true;
		}
		return false;
	}
	
	public void setStartLocation(Location loc){
		startLocations.getFlag().set(0, loc);
	}
	
	public void addStartLocation(Location loc){
		startLocations.getFlag().add(loc);
	}
	
	public void addStartLocation(Location loc, int number){
		if(startLocations.getFlag().size() >= number){
			startLocations.getFlag().set(number - 1, loc);
		}
		else{
			startLocations.getFlag().add(loc);
		}
	}
	
	public List<Location> getStartLocations(){
		return startLocations.getFlag();
	}
	
	public boolean removeStartLocation(int locNumber){
		if(startLocations.getFlag().size() < locNumber){
			startLocations.getFlag().remove(locNumber);
			return true;
		}
		return false;
	}
	
	public void setSpectatorLocation(Location loc){
		spectatorPosition.setFlag(loc);
	}
	
	public Location getSpectatorLocation(){
		return spectatorPosition.getFlag();
	}
	
	public boolean isEnabled(){
		return enabled.getFlag();
	}

	public void setEnabled(boolean enabled){
		this.enabled.setFlag(enabled);
	}

	public int getMinPlayers(){
		return minPlayers.getFlag();
	}

	public void setMinPlayers(int minPlayers){
		this.minPlayers.setFlag(minPlayers);
	}

	public int getMaxPlayers(){
		return maxPlayers.getFlag();
	}

	public void setMaxPlayers(int maxPlayers){
		this.maxPlayers.setFlag(maxPlayers);
	}

	public boolean isSpMaxPlayers() {
		return spMaxPlayers.getFlag();
	}

	public void setSpMaxPlayers(boolean spMaxPlayers) {
		this.spMaxPlayers.setFlag(spMaxPlayers);
	}

	public Location getFloorDegen1(){
		return floorDegen1.getFlag();
	}

	public void setFloorDegen1(Location loc){
		this.floorDegen1.setFlag(loc);
	}

	public Location getFloorDegen2(){
		return floorDegen2.getFlag();
	}

	public void setFloorDegen2(Location loc){
		this.floorDegen2.setFlag(loc);
	}

	public String getDegenType() {
		return degenType.getFlag();
	}

	public void setDegenType(String degenType) {
		this.degenType.setFlag(degenType);
	}

	public int getDegenRandomChance() {
		return degenRandomChance.getFlag();
	}

	public void setDegenRandomChance(int degenRandomChance) {
		this.degenRandomChance.setFlag(degenRandomChance);
	}

	public Location getEndPosition(){
		return endPosition.getFlag();
	}

	public void setEndPosition(Location endPosition){
		this.endPosition.setFlag(endPosition);
	}

	public Location getQuitPosition(){
		return quitPosition.getFlag();
	}

	public void setQuitPosition(Location quitPosition){
		this.quitPosition.setFlag(quitPosition);
	}

	public Location getLobbyPosition(){
		return lobbyPosisiton.getFlag();
	}

	public void setLobbyPosition(Location lobbyPosisiton){
		this.lobbyPosisiton.setFlag(lobbyPosisiton);
	}
	
	public String getName(boolean useDisplay){
		if(useDisplay && displayName.getFlag() != null)
			return displayName.getFlag();
		return name;
	}

	public void setDisplayName(String displayName) {
		this.displayName.setFlag(displayName);
	}

	public MinigameType getType(){
		return type.getFlag();
	}
	
	public void setType(MinigameType type){
		this.type.setFlag(type);
		initialize();
	}
	
	public MultiplayerTimer getMpTimer() {
		return mpTimer;
	}

	public void setMpTimer(MultiplayerTimer mpTimer) {
		this.mpTimer = mpTimer;
	}
	
	@Deprecated
	public boolean isNotWaitingForPlayers(){
		if(getState() != MinigameState.WAITING){
			return true;
		}
		return false;
	}
	
	public boolean isWaitingForPlayers(){
		if(getState() == MinigameState.WAITING)
			return true;
		return false;
	}
	
	public boolean hasStarted(){
		if(getState() == MinigameState.STARTED || getState() == MinigameState.OCCUPIED)
			return true;
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
		this.usePermissions.setFlag(usePermissions);
	}

	public boolean getUsePermissions() {
		return usePermissions.getFlag();
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
	
	public void setScore(MinigamePlayer ply, int amount){
		sbManager.getObjective(getName(false)).getScore(ply.getName()).setScore(amount);
	}

	public int getMinScore() {
		return minScore.getFlag();
	}

	public void setMinScore(int minScore) {
		this.minScore.setFlag(minScore);
	}

	public int getMaxScore() {
		return maxScore.getFlag();
	}

	public void setMaxScore(int maxScore) {
		this.maxScore.setFlag(maxScore);
	}
	
	public int getMaxScorePerPlayer(){
		float scorePerPlayer = getMaxScore() / getMaxPlayers();
		int score = (int) Math.round(scorePerPlayer * getPlayers().size());
		if(score < minScore.getFlag()){
			score = minScore.getFlag();
		}
		return score;
	}

	public FloorDegenerator getFloorDegenerator() {
		return sfloordegen;
	}

	public void addFloorDegenerator() {
		sfloordegen = new FloorDegenerator(getFloorDegen1(), getFloorDegen2(), this);
	}
	
	public void setTimer(int time){
		timer.setFlag(time);
	}
	
	public int getTimer(){
		return timer.getFlag();
	}

	public boolean isUsingXPBarTimer() {
		return useXPBarTimer.getFlag();
	}

	public void setUseXPBarTimer(boolean useXPBarTimer) {
		this.useXPBarTimer.setFlag(useXPBarTimer);
	}

	public int getStartWaitTime() {
		return startWaitTime.getFlag();
	}

	public void setStartWaitTime(int startWaitTime) {
		this.startWaitTime.setFlag(startWaitTime);
	}

	public boolean hasItemDrops() {
		return itemDrops.getFlag();
	}

	public void setItemDrops(boolean itemDrops) {
		this.itemDrops.setFlag(itemDrops);
	}

	public boolean hasDeathDrops() {
		return deathDrops.getFlag();
	}

	public void setDeathDrops(boolean deathDrops) {
		this.deathDrops.setFlag(deathDrops);
	}

	public boolean hasItemPickup() {
		return itemPickup.getFlag();
	}

	public void setItemPickup(boolean itemPickup) {
		this.itemPickup.setFlag(itemPickup);
	}

	public RecorderData getBlockRecorder() {
		return blockRecorder;
	}

	public boolean isRegenerating() {
		return regenerating;
	}

	public void setRegenerating(boolean regenerating) {
		this.regenerating = regenerating;
	}

	public boolean canBlockBreak() {
		return blockBreak.getFlag();
	}

	public void setCanBlockBreak(boolean blockBreak) {
		this.blockBreak.setFlag(blockBreak);
	}

	public boolean canBlockPlace() {
		return blockPlace.getFlag();
	}

	public void setCanBlockPlace(boolean blockPlace) {
		this.blockPlace.setFlag(blockPlace);
	}
	
	public GameMode getDefaultGamemode() {
		return defaultGamemode.getFlag();
	}
	
	public Callback<String> getDefaultGamemodeCallback(){
		return new Callback<String>() {

			@Override
			public void setValue(String value) {
				defaultGamemode.setFlag(GameMode.valueOf(value.toUpperCase()));
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(defaultGamemode.getFlag().toString());
			}
		};
	}

	public void setDefaultGamemode(GameMode defaultGamemode) {
		this.defaultGamemode.setFlag(defaultGamemode);
	}

	public boolean canBlocksdrop() {
		return blocksdrop.getFlag();
	}

	public void setBlocksdrop(boolean blocksdrop) {
		this.blocksdrop.setFlag(blocksdrop);
	}

	public String getMechanicName() {
		return mechanic.getFlag();
	}
	
	public GameMechanicBase getMechanic(){
		return GameMechanics.getGameMechanic(mechanic.getFlag());
	}

	public void setMechanic(String scoreType) {
		this.mechanic.setFlag(scoreType);
		initialize();
	}

	public boolean hasPaintBallMode() {
		return paintBallMode.getFlag();
	}

	public void setPaintBallMode(boolean paintBallMode) {
		this.paintBallMode.setFlag(paintBallMode);
	}

	public int getPaintBallDamage() {
		return paintBallDamage.getFlag();
	}

	public void setPaintBallDamage(int paintBallDamage) {
		this.paintBallDamage .setFlag(paintBallDamage);
	}

	public boolean hasUnlimitedAmmo() {
		return unlimitedAmmo.getFlag();
	}

	public void setUnlimitedAmmo(boolean unlimitedAmmo) {
		this.unlimitedAmmo.setFlag(unlimitedAmmo);
	}

	public boolean canSaveCheckpoint() {
		return saveCheckpoints.getFlag();
	}

	public void setSaveCheckpoint(boolean saveCheckpoint) {
		this.saveCheckpoints.setFlag(saveCheckpoint);
	}

	public boolean canLateJoin() {
		return lateJoin.getFlag();
	}

	public void setLateJoin(boolean lateJoin) {
		this.lateJoin.setFlag(lateJoin);
	}

	public boolean canSpectateFly() {
		return canSpectateFly.getFlag();
	}

	public void setCanSpectateFly(boolean canSpectateFly) {
		this.canSpectateFly.setFlag(canSpectateFly);
	}

	public boolean isRandomizeChests() {
		return randomizeChests.getFlag();
	}

	public void setRandomizeChests(boolean randomizeChests) {
		this.randomizeChests.setFlag(randomizeChests);
	}

	public int getMinChestRandom() {
		return minChestRandom.getFlag();
	}

	public void setMinChestRandom(int minChestRandom) {
		this.minChestRandom.setFlag(minChestRandom);
	}

	public int getMaxChestRandom() {
		return maxChestRandom.getFlag();
	}

	public void setMaxChestRandom(int maxChestRandom) {
		this.maxChestRandom.setFlag(maxChestRandom);
	}

	public Location getRegenArea1() {
		return regenArea1.getFlag();
	}

	public void setRegenArea1(Location regenArea1) {
		this.regenArea1.setFlag(regenArea1);
	}

	public Location getRegenArea2() {
		return regenArea2.getFlag();
	}

	public void setRegenArea2(Location regenArea2) {
		this.regenArea2.setFlag(regenArea2);
	}

	public int getRegenDelay() {
		return regenDelay.getFlag();
	}

	public void setRegenDelay(int regenDelay) {
		if(regenDelay < 0)
			regenDelay = 0;
		this.regenDelay.setFlag(regenDelay);
	}

	public int getLives() {
		return lives.getFlag();
	}

	public void setLives(int lives) {
		this.lives.setFlag(lives);
	}

	public int getFloorDegenTime() {
		return floorDegenTime.getFlag();
	}

	public void setFloorDegenTime(int floorDegenTime) {
		this.floorDegenTime.setFlag(floorDegenTime);
	}
	
	public boolean isAllowedEnderpearls() {
		return allowEnderpearls.getFlag();
	}

	public void setAllowEnderpearls(boolean allowEnderpearls) {
		this.allowEnderpearls.setFlag(allowEnderpearls);
	}

	public boolean isAllowedMPCheckpoints() {
		return allowMPCheckpoints.getFlag();
	}

	public void setAllowMPCheckpoints(boolean allowMPCheckpoints) {
		this.allowMPCheckpoints.setFlag(allowMPCheckpoints);
	}
	
	public boolean isAllowedFlight() {
		return allowFlight.getFlag();
	}

	public void setAllowedFlight(boolean allowFlight) {
		this.allowFlight.setFlag(allowFlight);
	}

	public boolean isFlightEnabled() {
		return enableFlight.getFlag();
	}

	public void setFlightEnabled(boolean enableFlight) {
		this.enableFlight.setFlag(enableFlight);
	}

	public Scoreboard getScoreboardManager(){
		return sbManager;
	}
	
	public String getObjective() {
		return objective.getFlag();
	}

	public void setObjective(String objective) {
		this.objective.setFlag(objective);
	}

	public String getGametypeName() {
		return gametypeName.getFlag();
	}

	public void setGametypeName(String gametypeName) {
		this.gametypeName.setFlag(gametypeName);
	}
	
	public boolean canDisplayScoreboard(){
		return displayScoreboard.getFlag();
	}
	
	public void setDisplayScoreboard(boolean bool){
		displayScoreboard.setFlag(bool);
	}
	
	private void addGameTypeOptions(Menu menu, MinigameType type) {
		switch (type) {
		case MULTIPLAYER: {
			menu.addItem(minScore.getMenuItem("Min. Score", Material.STEP));
			menu.addItem(maxScore.getMenuItem("Max. Score", Material.STONE));
			menu.addItem(minPlayers.getMenuItem("Min. Players", Material.STEP));
			menu.addItem(maxPlayers.getMenuItem("Max. Players", Material.STONE));
			
			menu.addItem(new MenuItemSubMenu("Lobby Settings", Material.WOOD_DOOR, getModule(LobbySettingsModule.class).createSettingsMenu()));
			menu.addItem(new MenuItemNewLine());
			menu.addItem(new MenuItemTime("Time Length", Material.WATCH, timer.getCallback(), 0, Integer.MAX_VALUE));
			menu.addItem(useXPBarTimer.getMenuItem("Use XP bar as Timer", Material.ENDER_PEARL));
			menu.addItem(new MenuItemTime("Start Wait Time", Material.WATCH, startWaitTime.getCallback(), 3, Integer.MAX_VALUE));
			menu.addItem(lateJoin.getMenuItem("Allow Late Join", Material.DEAD_BUSH));
			break;
		}
		case SINGLEPLAYER:
			menu.addItem(maxPlayers.getMenuItem("Max. Players", Material.STONE));
			menu.addItem(spMaxPlayers.getMenuItem("Enable Singleplayer Max Players", Material.IRON_FENCE));
			menu.addItem(new MenuItemNewLine());
			break;
		case GLOBAL:
			break;
		}
	}
	
	private void buildMenu(final Menu main) {
		Menu playerMenu = new Menu(5, getName(false));
		Menu flags = new Menu(5, getName(false));
		
		main.addItem(enabled.getMenuItem("Enabled", Material.PAPER));
		main.addItem(usePermissions.getMenuItem("Use Permissions", Material.PAPER));
		MenuItemEnum<MinigameType> gameTypeItem = (MenuItemEnum<MinigameType>)type.getMenuItem("Game Type", Material.PAPER);
		gameTypeItem.setChangeHandler(new IMenuItemChange<MinigameType>() {
			@Override
			public void onChange(MenuItemValue<MinigameType> menuItem, MinigamePlayer player, MinigameType previous, MinigameType current) {
				initialize();
				
				main.clear();
				buildMenu(main);
				main.refresh();
			}
		});
		
		main.addItem(gameTypeItem);

		List<String> scoreTypes = new ArrayList<String>();
		for(GameMechanicBase val : GameMechanics.getGameMechanics()){
			scoreTypes.add(MinigameUtils.capitalize(val.getMechanic().replace('_', ' ')));
		}
		
		// Add the game mechanic button
		Callback<String> gameMechanicCallback = new Callback<String>() {
			@Override
			public void setValue(String value) {
				mechanic.setFlag(value.toLowerCase().replace(' ', '_'));
				
				initialize();
				
				main.clear();
				buildMenu(main);
				main.refresh();
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(mechanic.getFlag().replace('_', ' '));
			}
		};
		
		main.addItem(new MenuItemList("Game Mechanic", "Multiplayer Only", Material.ROTTEN_FLESH, gameMechanicCallback, scoreTypes));
		
		// Add game mechanic settings button
		{
			GameMechanicBase mechanic = getMechanic();
			if (mechanic != null) {
				MinigameModule module = mechanic.displaySettings(Minigame.this);
				if (module != null) {
					Menu moduleMenu = module.createSettingsMenu();
					if (moduleMenu != null) {
						main.addItem(new MenuItemSubMenu("Game Mechanic Settings", ChatColor.GRAY + "Edit " + MinigameUtils.capitalize(mechanic.getMechanic().replace('_', ' ')) + " settings", Material.PAPER, moduleMenu));
					}
				}
			}
		}
		
		MenuItemString obj = (MenuItemString) objective.getMenuItem("Objective Description", Material.DIAMOND);
		obj.setAllowNull(true);
		main.addItem(obj);
		obj = (MenuItemString) gametypeName.getMenuItem("Gametype Description", Material.SIGN);
		obj.setAllowNull(true);
		main.addItem(obj);
		obj = (MenuItemString) displayName.getMenuItem("Display Name", Material.SIGN);
		obj.setAllowNull(true);
		main.addItem(obj);
		main.addItem(new MenuItemNewLine());
		
		addGameTypeOptions(main, getType());
		
		main.addItem(displayScoreboard.getMenuItem("Display Scoreboard", Material.SIGN));
		
		main.addItem(new MenuItemDisplayRewards("Primary Rewards", null, Material.CHEST, rewardItem));
		main.addItem(new MenuItemDisplayRewards("Secondary Rewards", null, Material.CHEST, secondaryRewardItem));
		main.addItem(new MenuItemDisplayWhitelist("Block Whitelist/Blacklist", "Blocks that can/can't;be broken", 
				Material.CHEST, getBlockRecorder().getWBBlocks(), getBlockRecorder().getWhitelistModeCallback()));
		main.addItem(new MenuItemNewLine());
		
		List<String> floorDegenOpt = new ArrayList<String>();
		floorDegenOpt.add("Inward");
		floorDegenOpt.add("Circle");
		floorDegenOpt.add("Random");
		main.addItem(new MenuItemList("Floor Degenerator Type", "Mainly used to prevent;islanding in spleef Minigames.", Material.SNOW_BLOCK, new Callback<String>() {

			@Override
			public void setValue(String value) {
				degenType.setFlag(value.toLowerCase());
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(degenType.getFlag());
			}
		}, floorDegenOpt));
		main.addItem(degenRandomChance.getMenuItem("Random Floor Degen Chance", "Chance of block being;removed on random;degeneration.", Material.SNOW, 1, 100));
		main.addItem(floorDegenTime.getMenuItem("Floor Degenerator Delay", Material.WATCH, 1, Integer.MAX_VALUE));
		main.addItem(new MenuItemTime("Regeneration Delay", "Time in seconds before;Minigame regeneration starts", Material.WATCH, regenDelay.getCallback(), 0, Integer.MAX_VALUE));
		main.addItem(new MenuItemNewLine());
		main.addItem(new MenuItemSubMenu("Player Settings", Material.SKULL_ITEM, playerMenu));
		if (getModule(LoadoutModule.class) != null) {
			main.addItem(new MenuItemSubMenu("Loadouts", Material.CHEST, getModule(LoadoutModule.class).createSettingsMenu()));
		}
		main.addItem(canSpectateFly.getMenuItem("Allow Spectator Fly", Material.FEATHER));
		main.addItem(randomizeChests.getMenuItem("Randomize Chests", "Randomize items in;chest upon first opening", Material.CHEST));
		main.addItem(minChestRandom.getMenuItem("Min. Chest Random", "Min. item randomization", Material.STEP, 0, Integer.MAX_VALUE));
		main.addItem(maxChestRandom.getMenuItem("Max. Chest Random", "Max. item randomization", Material.STONE, 0, Integer.MAX_VALUE));
		main.addItem(new MenuItemNewLine());

		main.setControlItem(new MenuItemSaveMinigame("Save " + getName(false), Material.REDSTONE_TORCH_ON, this), 4);

		//----------------------//
		//Minigame Player Settings
		//----------------------//
		List<MenuItem> itemsPlayer = new ArrayList<MenuItem>(14);
		List<String> gmopts = new ArrayList<String>();
		for(GameMode gm : GameMode.values()){
			gmopts.add(MinigameUtils.capitalize(gm.toString()));
		}
		itemsPlayer.add(new MenuItemList("Players Gamemode", Material.WORKBENCH, getDefaultGamemodeCallback(), gmopts));
		itemsPlayer.add(allowEnderpearls.getMenuItem("Allow Enderpearls", Material.ENDER_PEARL));
		itemsPlayer.add(itemDrops.getMenuItem("Allow Item Drops", Material.DIAMOND_SWORD));
		itemsPlayer.add(deathDrops.getMenuItem("Allow Death Drops", Material.SKULL_ITEM));
		itemsPlayer.add(itemPickup.getMenuItem("Allow Item Pickup", Material.DIAMOND));
		itemsPlayer.add(blockBreak.getMenuItem("Allow Block Break", Material.DIAMOND_PICKAXE));
		itemsPlayer.add(blockPlace.getMenuItem("Allow Block Place", Material.STONE));
		itemsPlayer.add(blocksdrop.getMenuItem("Allow Block Drops", Material.COBBLESTONE));
		itemsPlayer.add(lives.getMenuItem("Lives", Material.APPLE, 0, Integer.MAX_VALUE));
		itemsPlayer.add(paintBallMode.getMenuItem("Paintball Mode", Material.SNOW_BALL));
		itemsPlayer.add(paintBallDamage.getMenuItem("Paintball Damage", Material.ARROW, 1, Integer.MAX_VALUE));
		itemsPlayer.add(unlimitedAmmo.getMenuItem("Unlimited Ammo", Material.SNOW_BLOCK));
		itemsPlayer.add(allowMPCheckpoints.getMenuItem("Enable Multiplayer Checkpoints", Material.SIGN));
		itemsPlayer.add(saveCheckpoints.getMenuItem("Save Checkpoints", "Singleplayer Only", Material.SIGN));
		itemsPlayer.add(new MenuItemSubMenu("Flags", "Singleplayer flags", Material.SIGN, flags));
		itemsPlayer.add(allowFlight.getMenuItem("Allow Flight", "Allow flight to;be toggled", Material.FEATHER));
		itemsPlayer.add(enableFlight.getMenuItem("Enable Flight", "Start players;in flight;(Must have Allow;Flight)", Material.FEATHER));
		playerMenu.addItems(itemsPlayer);
		
		//--------------//
		//Minigame Flags//
		//--------------//
		List<MenuItem> itemsFlags = new ArrayList<MenuItem>(getFlags().size());
		for(String flag : getFlags()){
			itemsFlags.add(new MenuItemFlag(Material.SIGN, flag, getFlags()));
		}
		flags.setControlItem(new MenuItemAddFlag("Add Flag", Material.ITEM_FRAME, this), 4);
		flags.addItems(itemsFlags);
	
		for(MinigameModule mod : getModules()){
			mod.addEditMenuOptions(main);
		}
	}

	public void displayMenu(MinigamePlayer player){
		final Menu main = new Menu(5, getName(false));
		buildMenu(main);
		main.displayMenu(player);
	}

	public ScoreboardData getScoreboardData() {
		return sbData;
	}

	public void saveMinigame(){
		MinigameSave minigame = new MinigameSave(name, "config");
		FileConfiguration cfg = minigame.getConfig();
		
		for(MinigameModule module : getModules()){
			if(!module.useSeparateConfig()){
				module.save(cfg);
				
				if(module.getFlags() != null){
					for(Flag<?> flag : module.getFlags().values()){
						if(flag.getFlag() != null && (flag.getDefaultFlag() == null || !flag.getDefaultFlag().equals(flag.getFlag())))
							flag.saveValue(name, cfg);
					}
				}
			}else{
				MinigameSave modsave = new MinigameSave("minigames/" + name + "/" + module.getName().toLowerCase());
				modsave.getConfig().set(name, null);
				module.save(modsave.getConfig());
				
				if(module.getFlags() != null){
					for(Flag<?> flag : module.getFlags().values()){
						if(flag.getFlag() != null && (flag.getDefaultFlag() == null || !flag.getDefaultFlag().equals(flag.getFlag())))
							flag.saveValue(name, modsave.getConfig());
					}
				}
				
				modsave.saveConfig();
			}
		}
		
		for(String configOpt : configFlags.keySet()){
			if(configFlags.get(configOpt).getFlag() != null && 
					(configFlags.get(configOpt).getDefaultFlag() == null ||
						!configFlags.get(configOpt).getDefaultFlag().equals(configFlags.get(configOpt).getFlag())))
				configFlags.get(configOpt).saveValue(name, cfg);
		}
		
		if(!getBlockRecorder().getWBBlocks().isEmpty()){
			List<String> blocklist = new ArrayList<String>();
			for(Material mat : getBlockRecorder().getWBBlocks()){
				blocklist.add(mat.toString());
			}
			minigame.getConfig().set(name + ".whitelistblocks", blocklist);
		}
		
		if(getBlockRecorder().getWhitelistMode()){
			minigame.getConfig().set(name + ".whitelistmode", getBlockRecorder().getWhitelistMode());
		}
		
		getScoreboardData().saveDisplays(minigame, name);
		
		minigame.saveConfig();
	}
	
	private FileConfiguration getModuleConfig(MinigameModule module) {
		FileConfiguration config;
		MinigameSave save = new MinigameSave("minigames/" + name + "/" + module.getName().toLowerCase());
		config = save.getConfig();
		
		return config;
	}
	
	private void loadModule(MinigameModule module) {
		FileConfiguration config;
		if (module.useSeparateConfig()) {
			config = getModuleConfig(module);
		} else {
			config = cachedConfig;
		}
		
		module.load(config);
			
		if(module.getFlags() != null){
			for(String flag : module.getFlags().keySet()){
				if(config.contains(name + "." + flag))
					module.getFlags().get(flag).loadValue(name, config);
			}
		}
	}
	
	public void loadMinigame(){
		MinigameSave save = new MinigameSave(name, "config");
		FileConfiguration cfg = cachedConfig = save.getConfig();
		
		//-----------------------------------------------
		//TODO: Remove me after 1.7
		if(cfg.contains(name + ".type")){
			if(cfg.getString(name + ".type").equals("TEAMS")) {
				cfg.set(name + ".type", "MULTIPLAYER");
				getModule(TeamsModule.class).addTeam(TeamColor.RED);
				getModule(TeamsModule.class).addTeam(TeamColor.BLUE);
			}
			else if(cfg.getString(name + ".type").equals("FREE_FOR_ALL")){
				cfg.set(name + ".type", "MULTIPLAYER");
			}
			else if(cfg.getString(name + ".type").equals("TREASURE_HUNT")){
				cfg.set(name + ".type", "GLOBAL");
				cfg.set(name + ".scoretype", "treasure_hunt");
				cfg.set(name + ".timer", Minigames.plugin.getConfig().getInt("treasurehunt.findtime") * 60);
			}
		}
		//-----------------------------------------------
		
		for(String flag : configFlags.keySet()){
			if(cfg.contains(name + "." + flag))
				configFlags.get(flag).loadValue(name, cfg);
		}
		
		if(cfg.contains(name + ".whitelistmode")){
			getBlockRecorder().setWhitelistMode(cfg.getBoolean(name + ".whitelistmode"));
		}
		
		if(cfg.contains(name + ".whitelistblocks")){
			List<String> blocklist = cfg.getStringList(name + ".whitelistblocks");
			for(String block : blocklist){
				getBlockRecorder().addWBBlock(Material.matchMaterial(block));
			}
		}
		
		initialize();
		
		for(MinigameModule module : getModules()) {
			loadModule(module);
		}

//		Bukkit.getLogger().info("------- Minigame Load -------");
//		Bukkit.getLogger().info("Name: " + getName());
//		Bukkit.getLogger().info("Type: " + getType());
//		Bukkit.getLogger().info("Enabled: " + isEnabled());
//		Bukkit.getLogger().info("-----------------------------");
		
		final Minigame mgm = this;
		
		if(getType() == MinigameType.GLOBAL && isEnabled()){
			Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
				
				@Override
				public void run() {
					Minigames.plugin.mdata.startGlobalMinigame(mgm, null);
				}
			});
		}
		
		getScoreboardData().loadDisplays(cfg, this);
		save.saveConfig();
	}
	
	@Override
	public String toString(){
		return getName(false);
	}
	
	public void checkMinigame(Minigame minigame, boolean ignoreEnable) throws IllegalStateException {
		if (!ignoreEnable && !minigame.isEnabled()) {
			throw new IllegalStateException(MinigameUtils.getLang("minigame.error.notEnabled"));
		} else if (minigame.getType() == MinigameType.GLOBAL) {
			throw new IllegalStateException(MinigameUtils.getLang("minigame.error.wrongType"));
		} else if(minigame.getState() == MinigameState.REGENERATING) {
			throw new IllegalStateException(MinigameUtils.getLang("minigame.error.regenerating"));
		} else if (minigame.getEndPosition() == null) {
			throw new IllegalStateException(MinigameUtils.getLang("minigame.error.noEnd"));
		} else if (minigame.getQuitPosition() == null) {
			throw new IllegalStateException(MinigameUtils.getLang("minigame.error.noQuit"));
		}
		
		if (minigame.getType() == MinigameType.SINGLEPLAYER) {
			if (minigame.getStartLocations().size() == 0) {
				throw new IllegalStateException(MinigameUtils.getLang("minigame.error.noStart"));
			}
			
			if (minigame.isSpMaxPlayers() && minigame.getPlayers().size() >= minigame.getMaxPlayers()) {
				throw new IllegalStateException(MinigameUtils.getLang("minigame.full"));
			}
		} else if (minigame.getType() == MinigameType.MULTIPLAYER) {
			TeamsModule teams = minigame.getModule(TeamsModule.class);
			if (teams != null) {
				if (!teams.hasTeamStartLocations() && minigame.getStartLocations().size() == 0) {
					throw new IllegalStateException(MinigameUtils.getLang("minigame.error.noStart"));
				}
			} else {
				if (minigame.getStartLocations().size() == 0) {
					throw new IllegalStateException(MinigameUtils.getLang("minigame.error.noStart"));
				}
			}
			
			if (minigame.getLobbyPosition() == null) {
				throw new IllegalStateException(MinigameUtils.getLang("minigame.error.noLobby"));
			} else if(minigame.getPlayers().size() >= minigame.getMaxPlayers()) {
				throw new IllegalStateException(MinigameUtils.getLang("minigame.full"));
			} else if(!minigame.getMechanic().validTypes().contains(minigame.getType())) {
				throw new IllegalStateException(MinigameUtils.getLang("minigame.error.invalidMechanic"));
			} else if(minigame.getState() == MinigameState.STARTED && !minigame.canLateJoin()) {
				throw new IllegalStateException(MinigameUtils.getLang("minigame.started"));
			}
		}
	}
}
