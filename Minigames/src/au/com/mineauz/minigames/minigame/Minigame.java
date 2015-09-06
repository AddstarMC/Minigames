package au.com.mineauz.minigames.minigame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;

import au.com.mineauz.minigames.FloorDegenerator;
import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameSave;
import au.com.mineauz.minigames.MinigameTimer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.MultiplayerBets;
import au.com.mineauz.minigames.MultiplayerTimer;
import au.com.mineauz.minigames.blockRecorder.RecorderData;
import au.com.mineauz.minigames.events.MinigameInitializeEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.mechanics.GameMechanicBase;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemAddFlag;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemDisplayWhitelist;
import au.com.mineauz.minigames.menu.MenuItemEnum;
import au.com.mineauz.minigames.menu.MenuItemFlag;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemSubMenu;
import au.com.mineauz.minigames.menu.MenuItemSaveMinigame;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.menu.MenuItemTime;
import au.com.mineauz.minigames.menu.MenuItemValue;
import au.com.mineauz.minigames.menu.MenuItemValue.IMenuItemChange;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.MultiplayerModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigames.properties.types.EnumProperty;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigames.properties.types.ListProperty;
import au.com.mineauz.minigames.properties.types.LocationListProperty;
import au.com.mineauz.minigames.properties.types.LocationProperty;
import au.com.mineauz.minigames.properties.types.StringProperty;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.StatSettings;
import au.com.mineauz.minigames.stats.StoredGameStats;

public class Minigame {
	private final String name;
	private final ConfigPropertyContainer properties;
	
	private StringProperty displayName = new StringProperty("displayName");
	private StringProperty objective = new StringProperty("objective");
	private StringProperty gametypeName = new StringProperty("gametypeName");
	private EnumProperty<MinigameType> type = new EnumProperty<MinigameType>(MinigameType.SINGLEPLAYER, "type");
	private BooleanProperty enabled = new BooleanProperty(false, "enabled");
	private IntegerProperty maxPlayers = new IntegerProperty(4, "maxplayers");
	private BooleanProperty spMaxPlayers = new BooleanProperty(false, "spMaxPlayers");
	private ListProperty flags = new ListProperty(null, "flags");
	private MinigameState state = MinigameState.IDLE;
	
	private LocationProperty floorDegen1 = new LocationProperty(null, "sfloorpos.1");
	private LocationProperty floorDegen2 = new LocationProperty(null, "sfloorpos.2");
	private StringProperty degenType = new StringProperty("inward", "degentype");
	private IntegerProperty degenRandomChance = new IntegerProperty(15, "degenrandom");
	private IntegerProperty floorDegenTime = new IntegerProperty(Minigames.plugin.getConfig().getInt("multiplayer.floordegenerator.time"), "floordegentime");
	
	private LocationListProperty startLocations = new LocationListProperty(null, "startpos");
	private LocationProperty endPosition = new LocationProperty(null, "endpos");
	private LocationProperty quitPosition = new LocationProperty(null, "quitpos");
	private LocationProperty spectatorPosition = new LocationProperty(null, "spectatorpos");
	
	private BooleanProperty usePermissions = new BooleanProperty(false, "usepermissions");
	
	private BooleanProperty itemDrops = new BooleanProperty(false, "itemdrops");
	private BooleanProperty deathDrops = new BooleanProperty(false, "deathdrops");
	private BooleanProperty itemPickup = new BooleanProperty(true, "itempickup");
	private BooleanProperty blockBreak = new BooleanProperty(false, "blockbreak");
	private BooleanProperty blockPlace = new BooleanProperty(false, "blockplace");
	private EnumProperty<GameMode> defaultGamemode = new EnumProperty<GameMode>(GameMode.ADVENTURE, "gamemode");
	private BooleanProperty blocksdrop = new BooleanProperty(true, "blocksdrop");
	private BooleanProperty allowEnderpearls = new BooleanProperty(false, "allowEnderpearls");
	private BooleanProperty allowMPCheckpoints = new BooleanProperty(false, "allowMPCheckpoints");
	private BooleanProperty allowFlight = new BooleanProperty(false, "allowFlight");
	private BooleanProperty enableFlight = new BooleanProperty(false, "enableFlight");
	
	private StringProperty mechanic = new StringProperty("custom", "scoretype");
	private BooleanProperty paintBallMode = new BooleanProperty(false, "paintball");
	private IntegerProperty paintBallDamage = new IntegerProperty(2, "paintballdmg");
	private BooleanProperty unlimitedAmmo = new BooleanProperty(false, "unlimitedammo");
	private BooleanProperty saveCheckpoints = new BooleanProperty(false, "saveCheckpoints");
	private IntegerProperty lives = new IntegerProperty(0, "lives");
	
	private LocationProperty regenArea1 = new LocationProperty(null, "regenarea.1");
	private LocationProperty regenArea2 = new LocationProperty(null, "regenarea.2");
	private IntegerProperty regenDelay = new IntegerProperty(0, "regenDelay");
	
	private Map<Class<? extends MinigameModule>, MinigameModule> modules = Maps.newHashMap();
	private Map<Class<? extends MinigameModule>, MinigameModule> cachedModules = Maps.newHashMap();
	private FileConfiguration cachedConfig;
	
	private Scoreboard sbManager = Minigames.plugin.getServer().getScoreboardManager().getNewScoreboard();
	
	private IntegerProperty minScore = new IntegerProperty(5, "minscore");
	private IntegerProperty maxScore = new IntegerProperty(10, "maxscore");
	private BooleanProperty displayScoreboard = new BooleanProperty(true, "displayScoreboard");
	
	private BooleanProperty canSpectateFly = new BooleanProperty(false, "canspectatefly");
	
	private BooleanProperty randomizeChests = new BooleanProperty(false, "randomizechests");
	private IntegerProperty minChestRandom = new IntegerProperty(5, "minchestrandom");
	private IntegerProperty maxChestRandom = new IntegerProperty(10, "maxchestrandom");
	
	private ScoreboardData sbData = new ScoreboardData();
	private Map<MinigameStat, StatSettings> statSettings = Maps.newHashMap();
	
	//Unsaved data
	private List<MinigamePlayer> players = new ArrayList<MinigamePlayer>();
	private List<MinigamePlayer> spectators = new ArrayList<MinigamePlayer>();
	private RecorderData blockRecorder = new RecorderData(this);
	//Multiplayer
	private MultiplayerTimer mpTimer = null;
	private MinigameTimer miniTimer = null;
	private MultiplayerBets mpBets = null;

	public Minigame(String name, MinigameType type, Location start) {
		this.name = name;
		
		properties = new ConfigPropertyContainer();
		setup(type, start);
	}
	
	public Minigame(String name) {
		this.name = name;
		
		properties = new ConfigPropertyContainer();
		setup(MinigameType.SINGLEPLAYER, null);
	}
	
	private void setup(MinigameType type, Location start) {
		this.type.setValue(type);
		startLocations.setValue(new ArrayList<Location>());
		
		if(start != null)
			startLocations.getValue().add(start);
		
		sbManager.registerNewObjective(this.name, "dummy");
		sbManager.getObjective(this.name).setDisplaySlot(DisplaySlot.SIDEBAR);
		
		flags.setValue(new ArrayList<String>());
		
		properties.addProperty(allowEnderpearls);
		properties.addProperty(allowEnderpearls);
		properties.addProperty(allowFlight);
		properties.addProperty(allowMPCheckpoints);
		properties.addProperty(blockBreak);
		properties.addProperty(blockPlace);
		properties.addProperty(blocksdrop);
		properties.addProperty(canSpectateFly);
		properties.addProperty(deathDrops);
		properties.addProperty(defaultGamemode);
		properties.addProperty(degenRandomChance);
		properties.addProperty(degenType);
		properties.addProperty(displayName);
		properties.addProperty(enableFlight);
		properties.addProperty(enabled);
		properties.addProperty(endPosition);
		properties.addProperty(flags);
		properties.addProperty(floorDegen1);
		properties.addProperty(floorDegen2);
		properties.addProperty(floorDegenTime);
		properties.addProperty(gametypeName);
		properties.addProperty(itemDrops);
		properties.addProperty(itemPickup);
		properties.addProperty(lives);
		properties.addProperty(maxChestRandom);
		properties.addProperty(maxPlayers);
		properties.addProperty(maxScore);
		properties.addProperty(minChestRandom);
		properties.addProperty(minScore);
		properties.addProperty(objective);
		properties.addProperty(paintBallDamage);
		properties.addProperty(paintBallMode);
		properties.addProperty(quitPosition);
		properties.addProperty(randomizeChests);
		properties.addProperty(regenArea1);
		properties.addProperty(regenArea2);
		properties.addProperty(regenDelay);
		properties.addProperty(saveCheckpoints);
		properties.addProperty(mechanic);
		properties.addProperty(spMaxPlayers);
		properties.addProperty(startLocations);
		properties.addProperty(this.type);
		properties.addProperty(unlimitedAmmo);
		properties.addProperty(usePermissions);
		properties.addProperty(spectatorPosition);
		properties.addProperty(displayScoreboard);
		
		initialize();
	}
		
	private void initialize() {
		cachedModules.putAll(modules);
		modules.clear();
		
		// Add modules for the minigame type
		for (Class<? extends MinigameModule> module : Minigames.plugin.modules.getDefaultModules(type.getValue())) {
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
	
	public ConfigPropertyContainer getProperties() {
		return properties;
	}
	
	public MinigameState getState(){
		return state;
	}
	
	public void setState(MinigameState state){
		this.state = state;
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

	public boolean hasFlags(){
		return !flags.getValue().isEmpty();
	}
	
	public void addFlag(String flag){
		flags.getValue().add(flag);
	}
	
	public void setFlags(List<String> flags){
		this.flags.setValue(flags);
	}
	
	public List<String> getFlags(){
		return flags.getValue();
	}
	
	public boolean removeFlag(String flag){
		if(flags.getValue().contains(flag)){
			flags.getValue().remove(flag);
			return true;
		}
		return false;
	}
	
	public void setStartLocation(Location loc){
		startLocations.getValue().set(0, loc);
	}
	
	public void addStartLocation(Location loc){
		startLocations.getValue().add(loc);
	}
	
	public void addStartLocation(Location loc, int number){
		if(startLocations.getValue().size() >= number){
			startLocations.getValue().set(number - 1, loc);
		}
		else{
			startLocations.getValue().add(loc);
		}
	}
	
	public List<Location> getStartLocations(){
		return startLocations.getValue();
	}
	
	public boolean removeStartLocation(int locNumber){
		if(startLocations.getValue().size() < locNumber){
			startLocations.getValue().remove(locNumber);
			return true;
		}
		return false;
	}
	
	public void setSpectatorLocation(Location loc){
		spectatorPosition.setValue(loc);
	}
	
	public Location getSpectatorLocation(){
		return spectatorPosition.getValue();
	}
	
	public boolean isEnabled(){
		return enabled.getValue();
	}

	public void setEnabled(boolean enabled){
		this.enabled.setValue(enabled);
	}

	public int getMaxPlayers(){
		return maxPlayers.getValue();
	}

	public void setMaxPlayers(int maxPlayers){
		this.maxPlayers.setValue(maxPlayers);
	}

	public boolean isSpMaxPlayers() {
		return spMaxPlayers.getValue();
	}

	public void setSpMaxPlayers(boolean spMaxPlayers) {
		this.spMaxPlayers.setValue(spMaxPlayers);
	}

	public Location getFloorDegen1(){
		return floorDegen1.getValue();
	}

	public void setFloorDegen1(Location loc){
		this.floorDegen1.setValue(loc);
	}

	public Location getFloorDegen2(){
		return floorDegen2.getValue();
	}

	public void setFloorDegen2(Location loc){
		this.floorDegen2.setValue(loc);
	}

	public String getDegenType() {
		return degenType.getValue();
	}

	public void setDegenType(String degenType) {
		this.degenType.setValue(degenType);
	}

	public int getDegenRandomChance() {
		return degenRandomChance.getValue();
	}

	public void setDegenRandomChance(int degenRandomChance) {
		this.degenRandomChance.setValue(degenRandomChance);
	}

	public Location getEndPosition(){
		return endPosition.getValue();
	}

	public void setEndPosition(Location endPosition){
		this.endPosition.setValue(endPosition);
	}

	public Location getQuitPosition(){
		return quitPosition.getValue();
	}

	public void setQuitPosition(Location quitPosition){
		this.quitPosition.setValue(quitPosition);
	}

	public String getName(boolean useDisplay){
		if(useDisplay && displayName.getValue() != null)
			return displayName.getValue();
		return name;
	}

	public void setDisplayName(String displayName) {
		this.displayName.setValue(displayName);
	}

	public MinigameType getType(){
		return type.getValue();
	}
	
	public void setType(MinigameType type){
		this.type.setValue(type);
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
		this.usePermissions.setValue(usePermissions);
	}

	public boolean getUsePermissions() {
		return usePermissions.getValue();
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
		return minScore.getValue();
	}

	public void setMinScore(int minScore) {
		this.minScore.setValue(minScore);
	}

	public int getMaxScore() {
		return maxScore.getValue();
	}

	public void setMaxScore(int maxScore) {
		this.maxScore.setValue(maxScore);
	}
	
	public int getMaxScorePerPlayer(){
		float scorePerPlayer = getMaxScore() / getMaxPlayers();
		int score = (int) Math.round(scorePerPlayer * getPlayers().size());
		if(score < minScore.getValue()){
			score = minScore.getValue();
		}
		return score;
	}

	public boolean hasItemDrops() {
		return itemDrops.getValue();
	}

	public void setItemDrops(boolean itemDrops) {
		this.itemDrops.setValue(itemDrops);
	}

	public boolean hasDeathDrops() {
		return deathDrops.getValue();
	}

	public void setDeathDrops(boolean deathDrops) {
		this.deathDrops.setValue(deathDrops);
	}

	public boolean hasItemPickup() {
		return itemPickup.getValue();
	}

	public void setItemPickup(boolean itemPickup) {
		this.itemPickup.setValue(itemPickup);
	}

	public RecorderData getBlockRecorder() {
		return blockRecorder;
	}

	public boolean isRegenerating() {
		return state == MinigameState.REGENERATING;
	}

	public boolean canBlockBreak() {
		return blockBreak.getValue();
	}

	public void setCanBlockBreak(boolean blockBreak) {
		this.blockBreak.setValue(blockBreak);
	}

	public boolean canBlockPlace() {
		return blockPlace.getValue();
	}

	public void setCanBlockPlace(boolean blockPlace) {
		this.blockPlace.setValue(blockPlace);
	}
	
	public GameMode getDefaultGamemode() {
		return defaultGamemode.getValue();
	}
	
	public Callback<String> getDefaultGamemodeCallback(){
		return new Callback<String>() {

			@Override
			public void setValue(String value) {
				defaultGamemode.setValue(GameMode.valueOf(value.toUpperCase()));
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(defaultGamemode.getValue().toString());
			}
		};
	}

	public void setDefaultGamemode(GameMode defaultGamemode) {
		this.defaultGamemode.setValue(defaultGamemode);
	}

	public boolean canBlocksdrop() {
		return blocksdrop.getValue();
	}

	public void setBlocksdrop(boolean blocksdrop) {
		this.blocksdrop.setValue(blocksdrop);
	}

	public String getMechanicName() {
		return mechanic.getValue();
	}
	
	public GameMechanicBase getMechanic(){
		return GameMechanics.getGameMechanic(mechanic.getValue());
	}

	public void setMechanic(String scoreType) {
		this.mechanic.setValue(scoreType);
		initialize();
	}

	public boolean hasPaintBallMode() {
		return paintBallMode.getValue();
	}

	public void setPaintBallMode(boolean paintBallMode) {
		this.paintBallMode.setValue(paintBallMode);
	}

	public int getPaintBallDamage() {
		return paintBallDamage.getValue();
	}

	public void setPaintBallDamage(int paintBallDamage) {
		this.paintBallDamage .setValue(paintBallDamage);
	}

	public boolean hasUnlimitedAmmo() {
		return unlimitedAmmo.getValue();
	}

	public void setUnlimitedAmmo(boolean unlimitedAmmo) {
		this.unlimitedAmmo.setValue(unlimitedAmmo);
	}

	public boolean canSaveCheckpoint() {
		return saveCheckpoints.getValue();
	}

	public void setSaveCheckpoint(boolean saveCheckpoint) {
		this.saveCheckpoints.setValue(saveCheckpoint);
	}

	public boolean canSpectateFly() {
		return canSpectateFly.getValue();
	}

	public void setCanSpectateFly(boolean canSpectateFly) {
		this.canSpectateFly.setValue(canSpectateFly);
	}

	public boolean isRandomizeChests() {
		return randomizeChests.getValue();
	}

	public void setRandomizeChests(boolean randomizeChests) {
		this.randomizeChests.setValue(randomizeChests);
	}

	public int getMinChestRandom() {
		return minChestRandom.getValue();
	}

	public void setMinChestRandom(int minChestRandom) {
		this.minChestRandom.setValue(minChestRandom);
	}

	public int getMaxChestRandom() {
		return maxChestRandom.getValue();
	}

	public void setMaxChestRandom(int maxChestRandom) {
		this.maxChestRandom.setValue(maxChestRandom);
	}

	public Location getRegenArea1() {
		return regenArea1.getValue();
	}

	public void setRegenArea1(Location regenArea1) {
		this.regenArea1.setValue(regenArea1);
	}

	public Location getRegenArea2() {
		return regenArea2.getValue();
	}

	public void setRegenArea2(Location regenArea2) {
		this.regenArea2.setValue(regenArea2);
	}

	public int getRegenDelay() {
		return regenDelay.getValue();
	}

	public void setRegenDelay(int regenDelay) {
		if(regenDelay < 0)
			regenDelay = 0;
		this.regenDelay.setValue(regenDelay);
	}

	public int getLives() {
		return lives.getValue();
	}

	public void setLives(int lives) {
		this.lives.setValue(lives);
	}

	public int getFloorDegenTime() {
		return floorDegenTime.getValue();
	}

	public void setFloorDegenTime(int floorDegenTime) {
		this.floorDegenTime.setValue(floorDegenTime);
	}
	
	public boolean isAllowedEnderpearls() {
		return allowEnderpearls.getValue();
	}

	public void setAllowEnderpearls(boolean allowEnderpearls) {
		this.allowEnderpearls.setValue(allowEnderpearls);
	}

	public boolean isAllowedMPCheckpoints() {
		return allowMPCheckpoints.getValue();
	}

	public void setAllowMPCheckpoints(boolean allowMPCheckpoints) {
		this.allowMPCheckpoints.setValue(allowMPCheckpoints);
	}
	
	public boolean isAllowedFlight() {
		return allowFlight.getValue();
	}

	public void setAllowedFlight(boolean allowFlight) {
		this.allowFlight.setValue(allowFlight);
	}

	public boolean isFlightEnabled() {
		return enableFlight.getValue();
	}

	public void setFlightEnabled(boolean enableFlight) {
		this.enableFlight.setValue(enableFlight);
	}

	public Scoreboard getScoreboardManager(){
		return sbManager;
	}
	
	public String getObjective() {
		return objective.getValue();
	}

	public void setObjective(String objective) {
		this.objective.setValue(objective);
	}

	public String getGametypeName() {
		return gametypeName.getValue();
	}

	public void setGametypeName(String gametypeName) {
		this.gametypeName.setValue(gametypeName);
	}
	
	public boolean canDisplayScoreboard(){
		return displayScoreboard.getValue();
	}
	
	public void setDisplayScoreboard(boolean bool){
		displayScoreboard.setValue(bool);
	}
	
	public StatSettings getSettings(MinigameStat stat) {
		StatSettings settings = statSettings.get(stat);
		if (settings == null) {
			settings = new StatSettings(stat);
			statSettings.put(stat, settings);
		}
		
		return settings;
	}
	
	public Map<MinigameStat, StatSettings> getStatSettings(StoredGameStats stats) {
		Map<MinigameStat, StatSettings> settings = Maps.newHashMap();
		
		for (MinigameStat stat : stats.getStats().keySet()) {
			settings.put(stat, getSettings(stat));
		}
		
		return settings;
	}
	
	private void addGameTypeOptions(Menu menu, MinigameType type) {
		switch (type) {
		case MULTIPLAYER: {
			getModule(MultiplayerModule.class).addGameTypeMenuItems(menu);
			break;
		}
		case SINGLEPLAYER:
			menu.addItem(new MenuItemInteger("Max. Players", Material.STONE, maxPlayers, 0, Integer.MAX_VALUE));
			menu.addItem(new MenuItemBoolean("Enable Singleplayer Max Players", Material.IRON_FENCE, spMaxPlayers));
			menu.addItem(new MenuItemNewLine());
			break;
		case GLOBAL:
			break;
		}
	}

	private void buildMenu(final Menu main) {
		Menu playerMenu = new Menu(5, getName(false));
		Menu flags = new Menu(5, getName(false));
		
		main.addItem(new MenuItemBoolean("Enabled", Material.PAPER, enabled));
		main.addItem(new MenuItemBoolean("Use Permissions", Material.PAPER, usePermissions));
		MenuItemEnum<MinigameType> gameTypeItem = new MenuItemEnum<MinigameType>("Game Type", Material.PAPER, type, MinigameType.class);
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

		// Add the game mechanic button
		List<String> scoreTypes = new ArrayList<String>();
		for(GameMechanicBase val : GameMechanics.getGameMechanics()){
			scoreTypes.add(MinigameUtils.capitalize(val.getMechanic().replace('_', ' ')));
		}
		
		MenuItemList mechanicItem = new MenuItemList("Game Mechanic", "Multiplayer Only", Material.ROTTEN_FLESH, mechanic, scoreTypes);
		mechanicItem.setChangeHandler(new IMenuItemChange<String>() {
			@Override
			public void onChange(MenuItemValue<String> menuItem, MinigamePlayer player, String previous, String current) {
				initialize();
				
				main.clear();
				buildMenu(main);
				main.refresh();
			}
		});
		
		main.addItem(mechanicItem);
		
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
		
		MenuItemString obj = new MenuItemString("Objective Description", Material.DIAMOND, objective);
		obj.setAllowNull(true);
		main.addItem(obj);
		obj = new MenuItemString("Gametype Description", Material.SIGN, gametypeName);
		obj.setAllowNull(true);
		main.addItem(obj);
		obj = new MenuItemString("Display Name", Material.SIGN, displayName);
		obj.setAllowNull(true);
		main.addItem(obj);
		main.addItem(new MenuItemNewLine());
		
		main.addItem(new MenuItemInteger("Min. Score", Material.STEP, minScore, 0, Integer.MAX_VALUE));
		main.addItem(new MenuItemInteger("Max. Score", Material.STONE, maxScore, 0, Integer.MAX_VALUE));
		
		addGameTypeOptions(main, getType());
		
		main.addItem(new MenuItemBoolean("Display Scoreboard", Material.SIGN, displayScoreboard));
		
		
		main.addItem(new MenuItemDisplayWhitelist("Block Whitelist/Blacklist", "Blocks that can/can't;be broken", 
				Material.CHEST, getBlockRecorder().getWBBlocks(), getBlockRecorder().whitelistMode()));
		main.addItem(new MenuItemNewLine());
		
		List<String> floorDegenOpt = new ArrayList<String>();
		floorDegenOpt.add("Inward");
		floorDegenOpt.add("Circle");
		floorDegenOpt.add("Random");
		main.addItem(new MenuItemList("Floor Degenerator Type", "Mainly used to prevent;islanding in spleef Minigames.", Material.SNOW_BLOCK, degenType, floorDegenOpt));
		main.addItem(new MenuItemInteger("Random Floor Degen Chance", "Chance of block being;removed on random;degeneration.", Material.SNOW, degenRandomChance, 1, 100));
		main.addItem(new MenuItemTime("Floor Degenerator Delay", Material.WATCH, floorDegenTime, 1, Integer.MAX_VALUE));
		main.addItem(new MenuItemTime("Regeneration Delay", "Time in seconds before;Minigame regeneration starts", Material.WATCH, regenDelay, 0, Integer.MAX_VALUE));
		main.addItem(new MenuItemNewLine());
		main.addItem(new MenuItemSubMenu("Player Settings", Material.SKULL_ITEM, playerMenu));
		if (getModule(LoadoutModule.class) != null) {
			main.addItem(new MenuItemSubMenu("Loadouts", Material.CHEST, getModule(LoadoutModule.class).createSettingsMenu()));
		}
		main.addItem(new MenuItemBoolean("Allow Spectator Fly", Material.FEATHER, canSpectateFly));
		main.addItem(new MenuItemBoolean("Randomize Chests", "Randomize items in;chest upon first opening", Material.CHEST, randomizeChests));
		main.addItem(new MenuItemInteger("Min. Chest Random", "Min. item randomization", Material.STEP, minChestRandom, 0, Integer.MAX_VALUE));
		main.addItem(new MenuItemInteger("Max. Chest Random", "Max. item randomization", Material.STONE, maxChestRandom, 0, Integer.MAX_VALUE));
		main.addItem(new MenuItemNewLine());

		main.setControlItem(new MenuItemSaveMinigame("Save " + getName(false), Material.REDSTONE_TORCH_ON, this), 4);

		//----------------------//
		//Minigame Player Settings
		//----------------------//
		List<MenuItem> itemsPlayer = new ArrayList<MenuItem>(14);
		itemsPlayer.add(new MenuItemEnum<GameMode>("Players Gamemode", Material.WORKBENCH, defaultGamemode, GameMode.class));
		itemsPlayer.add(new MenuItemBoolean("Allow Enderpearls", Material.ENDER_PEARL, allowEnderpearls));
		itemsPlayer.add(new MenuItemBoolean("Allow Item Drops", Material.DIAMOND_SWORD, itemDrops));
		itemsPlayer.add(new MenuItemBoolean("Allow Death Drops", Material.SKULL_ITEM, deathDrops));
		itemsPlayer.add(new MenuItemBoolean("Allow Item Pickup", Material.DIAMOND, itemPickup));
		itemsPlayer.add(new MenuItemBoolean("Allow Block Break", Material.DIAMOND_PICKAXE, blockBreak));
		itemsPlayer.add(new MenuItemBoolean("Allow Block Place", Material.STONE, blockPlace));
		itemsPlayer.add(new MenuItemBoolean("Allow Block Drops", Material.COBBLESTONE, blocksdrop));
		itemsPlayer.add(new MenuItemInteger("Lives", Material.APPLE, lives, 0, Integer.MAX_VALUE));
		itemsPlayer.add(new MenuItemBoolean("Paintball Mode", Material.SNOW_BALL, paintBallMode));
		itemsPlayer.add(new MenuItemInteger("Paintball Damage", Material.ARROW, paintBallDamage, 1, Integer.MAX_VALUE));
		itemsPlayer.add(new MenuItemBoolean("Unlimited Ammo", Material.SNOW_BLOCK, unlimitedAmmo));
		itemsPlayer.add(new MenuItemBoolean("Enable Multiplayer Checkpoints", Material.SIGN, allowMPCheckpoints));
		itemsPlayer.add(new MenuItemBoolean("Save Checkpoints", "Singleplayer Only", Material.SIGN, saveCheckpoints));
		itemsPlayer.add(new MenuItemSubMenu("Flags", "Singleplayer flags", Material.SIGN, flags));
		itemsPlayer.add(new MenuItemBoolean("Allow Flight", "Allow flight to;be toggled", Material.FEATHER, allowFlight));
		itemsPlayer.add(new MenuItemBoolean("Enable Flight", "Start players;in flight;(Must have Allow;Flight)", Material.FEATHER, enableFlight));
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
		
		ConfigurationSection root = cfg.createSection(name);
		
		for (MinigameModule module : getModules()) {
			if (!module.useSeparateConfig()) {
				module.save(cfg);
				
				if (module.getProperties() != null) {
					module.getProperties().saveAll(root);
				}
			} else {
				MinigameSave modsave = new MinigameSave("minigames/" + name + "/" + module.getName().toLowerCase());
				modsave.getConfig().set(name, null);
				ConfigurationSection moduleRoot = modsave.getConfig().createSection(name);
				module.save(modsave.getConfig());
				
				if (module.getProperties() != null) {
					module.getProperties().saveAll(moduleRoot);
				}
				
				modsave.saveConfig();
			}
		}
		
		properties.saveAll(root);
		
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
		getScoreboardData().refreshDisplays();
		Minigames.plugin.getBackend().saveStatSettings(this, statSettings.values());
		
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
		
		ConfigurationSection root = config.getConfigurationSection(name);
		
		module.load(config);
		
		if (module.getProperties() != null) {
			module.getProperties().loadAll(root);
		}
	}
	
	public void loadMinigame(){
		MinigameSave save = new MinigameSave(name, "config");
		FileConfiguration cfg = cachedConfig = save.getConfig();
		ConfigurationSection root = cfg.getConfigurationSection(name);
		
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
		
		properties.loadAll(root);
		
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
		
		ListenableFuture<Map<MinigameStat, StatSettings>> settingsFuture = Minigames.plugin.getBackend().loadStatSettings(this);
		Minigames.plugin.getBackend().addServerThreadCallback(settingsFuture, new FutureCallback<Map<MinigameStat, StatSettings>>() {
			@Override
			public void onSuccess(Map<MinigameStat, StatSettings> result) {
				statSettings.clear();
				statSettings.putAll(result);
				
				getScoreboardData().reload();
			}
			
			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		});
		
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
			MultiplayerModule multiplayer = minigame.getModule(MultiplayerModule.class);
			
			if (teams != null) {
				if (!teams.hasTeamStartLocations() && minigame.getStartLocations().size() == 0) {
					throw new IllegalStateException(MinigameUtils.getLang("minigame.error.noStart"));
				}
			} else {
				if (minigame.getStartLocations().size() == 0) {
					throw new IllegalStateException(MinigameUtils.getLang("minigame.error.noStart"));
				}
			}
			
			if (multiplayer.getLobbyPosition() == null) {
				throw new IllegalStateException(MinigameUtils.getLang("minigame.error.noLobby"));
			} else if(minigame.getPlayers().size() >= minigame.getMaxPlayers()) {
				throw new IllegalStateException(MinigameUtils.getLang("minigame.full"));
			} else if(!minigame.getMechanic().validTypes().contains(minigame.getType())) {
				throw new IllegalStateException(MinigameUtils.getLang("minigame.error.invalidMechanic"));
			} else if(minigame.getState() == MinigameState.STARTED && !multiplayer.canLateJoin()) {
				throw new IllegalStateException(MinigameUtils.getLang("minigame.started"));
			}
		}
	}
	
	public void broadcast(String message, MessageType type) {
		broadcastExcept(message, type, null);
	}
	
	public void broadcastExcept(String message, MessageType type, MinigamePlayer except) {
		StringBuffer buffer = new StringBuffer();
		switch (type) {
		case Normal:
			buffer.append(ChatColor.AQUA);
			break;
		case Win:
			buffer.append(ChatColor.GREEN);
			break;
		case Error:
			buffer.append(ChatColor.RED);
			break;
		default:
			buffer.append(ChatColor.AQUA);
			break;
		}
		buffer.append("[Minigames] ");
		buffer.append(ChatColor.WHITE);
		buffer.append(message);
		
		String finalMessage = buffer.toString();
		
		for (MinigamePlayer player : players) {
			if (player != except) {
				player.sendMessage(finalMessage);
			}
		}
		
		for (MinigamePlayer spectator : spectators) {
			if (spectator != except) {
				spectator.sendMessage(finalMessage);
			}
		}
	}
}
