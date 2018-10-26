package au.com.mineauz.minigames.minigame;

import au.com.mineauz.minigames.*;
import au.com.mineauz.minigames.blockRecorder.RecorderData;
import au.com.mineauz.minigames.config.*;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.mechanics.GameMechanicBase;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.script.ScriptCollection;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigames.script.ScriptReference;
import au.com.mineauz.minigames.script.ScriptValue;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.StatSettings;
import au.com.mineauz.minigames.stats.StoredGameStats;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
@SuppressWarnings({"deprecation", "unused"})
public class Minigame implements ScriptObject {
    private Map<String, Flag<?>> configFlags = new HashMap<>();
	
	private final String name;
	private StringFlag displayName = new StringFlag(null, "displayName");
	private StringFlag objective = new StringFlag(null, "objective");
	private StringFlag gametypeName = new StringFlag(null, "gametypeName");
    private EnumFlag<MinigameType> type = new EnumFlag<>(MinigameType.SINGLEPLAYER, "type");
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
	private IntegerFlag floorDegenTime = new IntegerFlag(Minigames.getPlugin().getConfig().getInt("multiplayer.floordegenerator.time"), "floordegentime");
    // Respawn Module
	private BooleanFlag respawn = new BooleanFlag(Minigames.getPlugin().getConfig().getBoolean("has-respawn"), "respawn");
	private LocationListFlag startLocations = new LocationListFlag(null, "startpos");
    private BooleanFlag randomizeStart = new BooleanFlag(false, "ranndomizeStart");
	private LocationFlag endPosition = new LocationFlag(null, "endpos");
	private LocationFlag quitPosition = new LocationFlag(null, "quitpos");
	private LocationFlag lobbyPosisiton = new LocationFlag(null, "lobbypos");
	private LocationFlag spectatorPosition = new LocationFlag(null, "spectatorpos");
	
	private BooleanFlag usePermissions = new BooleanFlag(false, "usepermissions");
	private IntegerFlag timer = new IntegerFlag(0, "timer");
	private BooleanFlag useXPBarTimer = new BooleanFlag(true, "useXPBarTimer");
	private IntegerFlag startWaitTime = new IntegerFlag(0, "startWaitTime");
	
	private BooleanFlag itemDrops = new BooleanFlag(false, "itemdrops");
	private BooleanFlag deathDrops = new BooleanFlag(false, "deathdrops");
	private BooleanFlag itemPickup = new BooleanFlag(true, "itempickup");
	private BooleanFlag blockBreak = new BooleanFlag(false, "blockbreak");
	private BooleanFlag blockPlace = new BooleanFlag(false, "blockplace");
    private EnumFlag<GameMode> defaultGamemode = new EnumFlag<>(GameMode.ADVENTURE, "gamemode");
	private BooleanFlag blocksdrop = new BooleanFlag(true, "blocksdrop");
	private BooleanFlag allowEnderpearls = new BooleanFlag(false, "allowEnderpearls");
	private BooleanFlag allowMPCheckpoints = new BooleanFlag(false, "allowMPCheckpoints");
	private BooleanFlag allowFlight = new BooleanFlag(false, "allowFlight");
	private BooleanFlag enableFlight = new BooleanFlag(false, "enableFlight");
	private BooleanFlag allowDragonEggTeleport = new BooleanFlag(true, "allowDragonEggTeleport");
	private BooleanFlag usePlayerDisplayNames = new BooleanFlag(true, "usePlayerDisplayNames");
	private BooleanFlag showPlayerBroadcasts = new BooleanFlag(true, "showPlayerBroadcasts");
	
	private StringFlag mechanic = new StringFlag("custom", "scoretype");
	private BooleanFlag paintBallMode = new BooleanFlag(false, "paintball");
	private IntegerFlag paintBallDamage = new IntegerFlag(2, "paintballdmg");
	private BooleanFlag unlimitedAmmo = new BooleanFlag(false, "unlimitedammo");
	private BooleanFlag saveCheckpoints = new BooleanFlag(false, "saveCheckpoints");
	private BooleanFlag lateJoin = new BooleanFlag(false, "latejoin");
	private FloatFlag lives = new FloatFlag(0F, "lives");
	
	private LocationFlag regenArea1 = new LocationFlag(null, "regenarea.1");
	private LocationFlag regenArea2 = new LocationFlag(null, "regenarea.2");
	private IntegerFlag regenDelay = new IntegerFlag(0, "regenDelay");

    private Map<String, MinigameModule> modules = new HashMap<>();
	
	private Scoreboard sbManager = Minigames.getPlugin().getServer().getScoreboardManager().getNewScoreboard();
	
	private IntegerFlag minScore = new IntegerFlag(5, "minscore");
	private IntegerFlag maxScore = new IntegerFlag(10, "maxscore");
	private BooleanFlag displayScoreboard = new BooleanFlag(true, "displayScoreboard");
	
	private BooleanFlag canSpectateFly = new BooleanFlag(false, "canspectatefly");
	
	private BooleanFlag randomizeChests = new BooleanFlag(false, "randomizechests");
	private IntegerFlag minChestRandom = new IntegerFlag(5, "minchestrandom");
	private IntegerFlag maxChestRandom = new IntegerFlag(10, "maxchestrandom");
	
	private ScoreboardData sbData = new ScoreboardData();
	private Map<MinigameStat, StatSettings> statSettings = Maps.newHashMap();
	
	//Unsaved data
    private List<MinigamePlayer> players = new ArrayList<>();
    private List<MinigamePlayer> spectators = new ArrayList<>();
	private RecorderData blockRecorder = new RecorderData(this);
	//Multiplayer
	private MultiplayerTimer mpTimer = null;
	private MinigameTimer miniTimer = null;
	private MultiplayerBets mpBets = null;
	//CTF
    private Map<MinigamePlayer, CTFFlag> flagCarriers = new HashMap<>();
    private Map<String, CTFFlag> droppedFlag = new HashMap<>();
    private boolean playersAtStart;

    public boolean isPlayersAtStart() {
        return playersAtStart;
    }

    public void setPlayersAtStart(boolean playersAtStart) {
        this.playersAtStart = playersAtStart;
    }

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
        startLocations.setFlag(new ArrayList<>());
		
		if(start != null)
			startLocations.getFlag().add(start);
		
		sbManager.registerNewObjective(this.name, "dummy");
		sbManager.getObjective(this.name).setDisplaySlot(DisplaySlot.SIDEBAR);
		
		for (Class<? extends MinigameModule> mod : Minigames.getPlugin().getMinigameManager().getModules()) {
			try {
				addModule(mod.getDeclaredConstructor(Minigame.class).newInstance(this));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

        flags.setFlag(new ArrayList<>());
		
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
		addConfigFlag(usePlayerDisplayNames);
		addConfigFlag(showPlayerBroadcasts);
		addConfigFlag(minScore);
		addConfigFlag(objective);
		addConfigFlag(paintBallDamage);
		addConfigFlag(paintBallMode);
		addConfigFlag(quitPosition);
		addConfigFlag(randomizeChests);
		addConfigFlag(regenArea1);
		addConfigFlag(regenArea2);
		addConfigFlag(regenDelay);
		addConfigFlag(saveCheckpoints);
		addConfigFlag(mechanic);
		addConfigFlag(spMaxPlayers);
		addConfigFlag(startLocations);
        addConfigFlag(randomizeStart);
		addConfigFlag(startWaitTime);
		addConfigFlag(timer);
		addConfigFlag(this.type);
		addConfigFlag(unlimitedAmmo);
		addConfigFlag(usePermissions);
		addConfigFlag(useXPBarTimer);
		addConfigFlag(spectatorPosition);
		addConfigFlag(displayScoreboard);
		addConfigFlag(allowDragonEggTeleport);
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
	
	public boolean addModule(MinigameModule module){
		if(!modules.containsKey(module.getName())){
			modules.put(module.getName(), module);
			return true;
		}
		return false;
	}
	
	public void removeModule(String moduleName){
		modules.remove(moduleName);
	}
	
	public List<MinigameModule> getModules(){
        return new ArrayList<>(modules.values());
	}
	
	public MinigameModule getModule(String name){
		return modules.get(name);
	}
	
	public boolean isTeamGame(){
        return getType() == MinigameType.MULTIPLAYER && TeamsModule.getMinigameModule(this).getTeams().size() > 0;
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

    public boolean isRandomizeStart() {
        return randomizeStart.getFlag();
    }

    public void setRandomizeStart(boolean bool) {
        randomizeStart.setFlag(bool);
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

	public boolean usePlayerDisplayNames() {
		return usePlayerDisplayNames.getFlag();
	}

    public void setUsePlayerDisplayNames(Boolean value){
	    usePlayerDisplayNames.setFlag(value);
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

	public void setshowPlayerBroadcasts(Boolean showPlayerBroadcasts){
		this.showPlayerBroadcasts.setFlag(showPlayerBroadcasts);
	}

	public Boolean getShowPlayerBroadcasts() {
		return showPlayerBroadcasts.getFlag();
	}

	public MinigameType getType(){
		return type.getFlag();
	}
	
	private Callback<String> getTypeCallback(){
		return new Callback<String>() {

			@Override
			public void setValue(String value) {
				type.setFlag(MinigameType.valueOf(value.toUpperCase().replace(" ", "_")));
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(type.getFlag().toString().replace("_", " "));
			}
		};
	}
	
	public void setType(MinigameType type){
		this.type.setFlag(type);
	}
	
	public MultiplayerTimer getMpTimer() {
		return mpTimer;
	}

	public void setMpTimer(MultiplayerTimer mpTimer) {
		this.mpTimer = mpTimer;
	}
	
	@Deprecated
	public boolean isNotWaitingForPlayers(){
        return getState() != MinigameState.WAITING;
    }
	
	public boolean isWaitingForPlayers(){
        return getState() == MinigameState.WAITING;
    }
	
	public boolean hasStarted(){
        return getState() == MinigameState.STARTED || getState() == MinigameState.OCCUPIED;
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
		int score = Math.round(scorePerPlayer * getPlayers().size());
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
		return state == MinigameState.REGENERATING;
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

	public float getLives() {
		return lives.getFlag();
	}

	public void setLives(float lives) {
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
	
	public boolean allowDragonEggTeleport() {
		return allowDragonEggTeleport.getFlag();
	}
	
	public void setAllowDragonEggTeleport(boolean allow) {
		allowDragonEggTeleport.setFlag(allow);
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

	public void displayMenu(MinigamePlayer player){
		Menu main = new Menu(6, getName(false), player);
		Menu playerMenu = new Menu(6, getName(false), player);
		Menu loadouts = new Menu(6, getName(false), player);
		Menu flags = new Menu(6, getName(false), player);
		Menu lobby = new Menu(6, getName(false), player);

        List<MenuItem> itemsMain = new ArrayList<>();
		itemsMain.add(enabled.getMenuItem("Enabled", Material.LEGACY_PAPER));
		itemsMain.add(usePermissions.getMenuItem("Use Permissions", Material.LEGACY_PAPER));
        List<String> mgTypes = new ArrayList<>();
		for(MinigameType val : MinigameType.values()){
			mgTypes.add(MinigameUtils.capitalize(val.toString().replace("_", " ")));
		}
		itemsMain.add(new MenuItemList("Game Type", Material.LEGACY_PAPER, getTypeCallback(), mgTypes));
        List<String> scoreTypes = new ArrayList<>();
		for(GameMechanicBase val : GameMechanics.getGameMechanics()){
			scoreTypes.add(MinigameUtils.capitalize(val.getMechanic()));
		}
		itemsMain.add(new MenuItemList("Game Mechanic", MinigameUtils.stringToList("Multiplayer Only"), Material.LEGACY_ROTTEN_FLESH, new Callback<String>() {

			@Override
			public void setValue(String value) {
				mechanic.setFlag(value.toLowerCase());
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(mechanic.getFlag());
			}
		}, scoreTypes));
		final MenuItemCustom mechSettings = new MenuItemCustom("Game Mechanic Settings", Material.LEGACY_PAPER);
		final Minigame mgm = this;
		final Menu fmain = main;
		mechSettings.setClick(new InteractionInterface() {
			
			@Override
			public Object interact(Object object) {
				if(getMechanic().displaySettings(mgm) != null && 
						getMechanic().displaySettings(mgm).displayMechanicSettings(fmain))
					return null;
				return mechSettings.getItem();
			}
		});
		itemsMain.add(mechSettings);
		MenuItemString obj = (MenuItemString) objective.getMenuItem("Objective Description", Material.LEGACY_DIAMOND);
		obj.setAllowNull(true);
		itemsMain.add(obj);
		obj = (MenuItemString) gametypeName.getMenuItem("Gametype Description", Material.LEGACY_SIGN);
		obj.setAllowNull(true);
		itemsMain.add(obj);
		obj = (MenuItemString) displayName.getMenuItem("Display Name", Material.LEGACY_SIGN);
		obj.setAllowNull(true);
		itemsMain.add(obj);
		itemsMain.add(new MenuItemNewLine());
		itemsMain.add(minScore.getMenuItem("Min. Score", Material.LEGACY_STEP, MinigameUtils.stringToList("Multiplayer Only")));
		itemsMain.add(maxScore.getMenuItem("Max. Score", Material.LEGACY_STONE, MinigameUtils.stringToList("Multiplayer Only")));
		itemsMain.add(minPlayers.getMenuItem("Min. Players", Material.LEGACY_STEP, MinigameUtils.stringToList("Multiplayer Only")));
		itemsMain.add(maxPlayers.getMenuItem("Max. Players", Material.LEGACY_STONE, MinigameUtils.stringToList("Multiplayer Only")));
		itemsMain.add(spMaxPlayers.getMenuItem("Enable Singleplayer Max Players", Material.LEGACY_IRON_FENCE));
		itemsMain.add(displayScoreboard.getMenuItem("Display Scoreboard", Material.LEGACY_SIGN));
		itemsMain.add(new MenuItemPage("Lobby Settings", MinigameUtils.stringToList("Multiplayer Only"), Material.LEGACY_WOOD_DOOR, lobby));
		itemsMain.add(new MenuItemNewLine());
		itemsMain.add(new MenuItemTime("Time Length", MinigameUtils.stringToList("Multiplayer Only"), Material.LEGACY_WATCH, new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				timer.setFlag(value);
			}

			@Override
			public Integer getValue() {
				return timer.getFlag();
			}
		}, 0, null));
		itemsMain.add(useXPBarTimer.getMenuItem("Use XP bar as Timer", Material.LEGACY_ENDER_PEARL));
		itemsMain.add(new MenuItemTime("Start Wait Time", MinigameUtils.stringToList("Multiplayer Only"), Material.LEGACY_WATCH, new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				startWaitTime.setFlag(value);
			}

			@Override
			public Integer getValue() {
				return startWaitTime.getFlag();
			}
		}, 3, null));
		itemsMain.add(lateJoin.getMenuItem("Allow Late Join", Material.LEGACY_DEAD_BUSH, MinigameUtils.stringToList("Multiplayer Only")));
        itemsMain.add(randomizeStart.getMenuItem("Randomize Start Point", Material.LEGACY_LIGHT_BLUE_GLAZED_TERRACOTTA, MinigameUtils.stringToList("The location will be; chosen at random;from global or team lists.")));
		itemsMain.add(new MenuItemDisplayWhitelist("Block Whitelist/Blacklist", MinigameUtils.stringToList("Blocks that can/can't;be broken"), 
				Material.LEGACY_CHEST, getBlockRecorder().getWBBlocks(), getBlockRecorder().getWhitelistModeCallback()));
		itemsMain.add(new MenuItemNewLine());
        List<String> floorDegenDes = new ArrayList<>();
		floorDegenDes.add("Mainly used to prevent");
		floorDegenDes.add("islanding in spleef Minigames.");
        List<String> floorDegenOpt = new ArrayList<>();
		floorDegenOpt.add("Inward");
		floorDegenOpt.add("Circle");
		floorDegenOpt.add("Random");
		itemsMain.add(new MenuItemList("Floor Degenerator Type", floorDegenDes, Material.LEGACY_SNOW_BLOCK, new Callback<String>() {

			@Override
			public void setValue(String value) {
				degenType.setFlag(value.toLowerCase());
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(degenType.getFlag());
			}
		}, floorDegenOpt));
        List<String> degenRandDes = new ArrayList<>();
		degenRandDes.add("Chance of block being");
		degenRandDes.add("removed on random");
		degenRandDes.add("degeneration.");
		itemsMain.add(degenRandomChance.getMenuItem("Random Floor Degen Chance", Material.LEGACY_SNOW, degenRandDes, 1, 100));
		itemsMain.add(floorDegenTime.getMenuItem("Floor Degenerator Delay", Material.LEGACY_WATCH, 1, null));
		itemsMain.add(new MenuItemTime("Regeneration Delay", MinigameUtils.stringToList("Time in seconds before;Minigame regeneration starts"), Material.LEGACY_WATCH, new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				regenDelay.setFlag(value);
			}

			@Override
			public Integer getValue() {
				return regenDelay.getFlag();
			}
		}, 0, null));
		itemsMain.add(new MenuItemNewLine());
		itemsMain.add(new MenuItemPage("Player Settings", Material.LEGACY_SKULL_ITEM, playerMenu));
        List<String> thDes = new ArrayList<>();
		thDes.add("Treasure hunt related");
		thDes.add("settings.");
//		itemsMain.add(new MenuItemPage("Treasure Hunt Settings", thDes, Material.LEGACY_CHEST, treasureHunt));
//		MenuItemDisplayLoadout defLoad = new MenuItemDisplayLoadout("Default Loadout", Material.LEGACY_DIAMOND_SWORD, LoadoutModule.getMinigameModule(this).getDefaultPlayerLoadout(), this);
//		defLoad.setAllowDelete(false);
//		itemsMain.add(defLoad);
		itemsMain.add(new MenuItemPage("Loadouts", Material.LEGACY_CHEST, loadouts));
		itemsMain.add(canSpectateFly.getMenuItem("Allow Spectator Fly", Material.LEGACY_FEATHER));
        List<String> rndChstDes = new ArrayList<>();
		rndChstDes.add("Randomize items in");
		rndChstDes.add("chest upon first opening");
		itemsMain.add(randomizeChests.getMenuItem("Randomize Chests", Material.LEGACY_CHEST, rndChstDes));
		rndChstDes.clear();
		rndChstDes.add("Min. item randomization");
		itemsMain.add(minChestRandom.getMenuItem("Min. Chest Random", Material.LEGACY_STEP, rndChstDes, 0, null));
		rndChstDes.clear();
		rndChstDes.add("Max. item randomization");
		itemsMain.add(maxChestRandom.getMenuItem("Max. Chest Random", Material.LEGACY_STONE, rndChstDes, 0, null));
		itemsMain.add(new MenuItemStatisticsSettings(this, "Stat Settings", Material.LEGACY_BOOK_AND_QUILL));
		itemsMain.add(new MenuItemNewLine());

		//--------------//
		//Loadout Settings
		//--------------//
        List<MenuItem> mi = new ArrayList<>();
        List<String> des = new ArrayList<>();
		des.add("Shift + Right Click to Delete");
		for(String ld : LoadoutModule.getMinigameModule(this).getLoadouts()){
			Material item = Material.LEGACY_THIN_GLASS;
			if(LoadoutModule.getMinigameModule(this).getLoadout(ld).getItems().size() != 0){
				item = LoadoutModule.getMinigameModule(this).getLoadout(ld).getItem((Integer)LoadoutModule.getMinigameModule(this).getLoadout(ld).getItems().toArray()[0]).getType();
			}
			if(LoadoutModule.getMinigameModule(this).getLoadout(ld).isDeleteable())
				mi.add(new MenuItemDisplayLoadout(ld, des, item, LoadoutModule.getMinigameModule(this).getLoadout(ld), this));
			else
				mi.add(new MenuItemDisplayLoadout(ld, item, LoadoutModule.getMinigameModule(this).getLoadout(ld), this));
		}
		loadouts.addItem(new MenuItemLoadoutAdd("Add Loadout", Material.LEGACY_ITEM_FRAME, LoadoutModule.getMinigameModule(this).getLoadoutMap(), this), 53);
		loadouts.addItem(new MenuItemPage("Back", Material.LEGACY_REDSTONE_TORCH_ON, main), loadouts.getSize() - 9);
		loadouts.addItems(mi);
		
		main.addItems(itemsMain);
		main.addItem(new MenuItemSaveMinigame("Save " + getName(false), Material.LEGACY_REDSTONE_TORCH_ON, this), main.getSize() - 1);

		//----------------------//
		//Minigame Player Settings
		//----------------------//
        List<MenuItem> itemsPlayer = new ArrayList<>(20);
        List<String> gmopts = new ArrayList<>();
		for(GameMode gm : GameMode.values()){
			gmopts.add(MinigameUtils.capitalize(gm.toString()));
		}
		itemsPlayer.add(new MenuItemList("Players Gamemode", Material.LEGACY_WORKBENCH, getDefaultGamemodeCallback(), gmopts));
		itemsPlayer.add(allowEnderpearls.getMenuItem("Allow Enderpearls", Material.LEGACY_ENDER_PEARL));
		itemsPlayer.add(itemDrops.getMenuItem("Allow Item Drops", Material.LEGACY_DIAMOND_SWORD));
		itemsPlayer.add(deathDrops.getMenuItem("Allow Death Drops", Material.LEGACY_SKULL_ITEM));
		itemsPlayer.add(itemPickup.getMenuItem("Allow Item Pickup", Material.LEGACY_DIAMOND));
		itemsPlayer.add(blockBreak.getMenuItem("Allow Block Break", Material.LEGACY_DIAMOND_PICKAXE));
		itemsPlayer.add(blockPlace.getMenuItem("Allow Block Place", Material.LEGACY_STONE));
		itemsPlayer.add(blocksdrop.getMenuItem("Allow Block Drops", Material.LEGACY_COBBLESTONE));
		itemsPlayer.add(lives.getMenuItem("Lives", Material.LEGACY_APPLE, null));
		itemsPlayer.add(paintBallMode.getMenuItem("Paintball Mode", Material.LEGACY_SNOW_BALL));
		itemsPlayer.add(paintBallDamage.getMenuItem("Paintball Damage", Material.LEGACY_ARROW, 1, null));
		itemsPlayer.add(unlimitedAmmo.getMenuItem("Unlimited Ammo", Material.LEGACY_SNOW_BLOCK));
		itemsPlayer.add(allowMPCheckpoints.getMenuItem("Enable Multiplayer Checkpoints", Material.LEGACY_SIGN));
		itemsPlayer.add(saveCheckpoints.getMenuItem("Save Checkpoints", Material.LEGACY_SIGN, MinigameUtils.stringToList("Singleplayer Only")));
		itemsPlayer.add(new MenuItemPage("Flags", MinigameUtils.stringToList("Singleplayer flags"), Material.LEGACY_SIGN, flags));
		itemsPlayer.add(allowFlight.getMenuItem("Allow Flight", Material.LEGACY_FEATHER, MinigameUtils.stringToList("Allow flight to;be toggled")));
		itemsPlayer.add(enableFlight.getMenuItem("Enable Flight", Material.LEGACY_FEATHER, MinigameUtils.stringToList("Start players;in flight;(Must have Allow;Flight)")));
		itemsPlayer.add(allowDragonEggTeleport.getMenuItem("Allow Dragon Egg Teleport", Material.LEGACY_DRAGON_EGG));
		itemsPlayer.add(usePlayerDisplayNames.getMenuItem("Use Players Display Names", Material.LEGACY_POTATO_ITEM, MinigameUtils.stringToList("Use Player Nicks or Real Names")));
		itemsPlayer.add(showPlayerBroadcasts.getMenuItem("Show Join/Exit Broadcasts",Material.LEGACY_PAPER,MinigameUtils.stringToList("Show Join and Exit broadcasts; Plus other Player broadcasts")));
		playerMenu.addItems(itemsPlayer);
		playerMenu.addItem(new MenuItemPage("Back", Material.LEGACY_REDSTONE_TORCH_ON, main), main.getSize() - 9);
		
		//--------------//
		//Minigame Flags//
		//--------------//
        List<MenuItem> itemsFlags = new ArrayList<>(getFlags().size());
		for(String flag : getFlags()){
			itemsFlags.add(new MenuItemFlag(Material.LEGACY_SIGN, flag, getFlags()));
		}
		flags.addItem(new MenuItemPage("Back", Material.LEGACY_REDSTONE_TORCH_ON, playerMenu), flags.getSize() - 9);
		flags.addItem(new MenuItemAddFlag("Add Flag", Material.LEGACY_ITEM_FRAME, this), flags.getSize() - 1);
		flags.addItems(itemsFlags);
		
		//--------------//
		//Lobby Settings//
		//--------------//
        List<MenuItem> itemsLobby = new ArrayList<>(4);
		itemsLobby.add(new MenuItemBoolean("Can Interact on Player Wait", Material.LEGACY_STONE_BUTTON, LobbySettingsModule.getMinigameModule(this).getCanInteractPlayerWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Can Interact on Start Wait", Material.LEGACY_STONE_BUTTON, LobbySettingsModule.getMinigameModule(this).getCanInteractStartWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Can Move on Player Wait", Material.LEGACY_ICE, LobbySettingsModule.getMinigameModule(this).getCanMovePlayerWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Can Move on Start Wait", Material.LEGACY_ICE, LobbySettingsModule.getMinigameModule(this).getCanMoveStartWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Teleport After Player Wait", MinigameUtils.stringToList("Should players be teleported;after player wait time?"), 
				Material.LEGACY_ENDER_PEARL, LobbySettingsModule.getMinigameModule(this).getTeleportOnPlayerWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Teleport on Start", MinigameUtils.stringToList("Should players teleport;to the start position;after lobby?"),
				Material.LEGACY_ENDER_PEARL, LobbySettingsModule.getMinigameModule(this).getTeleportOnStartCallback()));
		itemsLobby.add(new MenuItemInteger("Waiting for Players Time", MinigameUtils.stringToList("The time in seconds;the game will wait for;more players to join.;A value of 0 will use;the config setting"),
				Material.LEGACY_WATCH, LobbySettingsModule.getMinigameModule(this).getPlayerWaitTimeCallback(), 0, Integer.MAX_VALUE));
		lobby.addItems(itemsLobby);
		lobby.addItem(new MenuItemPage("Back", Material.LEGACY_REDSTONE_TORCH_ON, main), lobby.getSize() - 9);

		for(MinigameModule mod : getModules()){
			mod.addEditMenuOptions(main);
		}
		main.displayMenu(player);
		
	}

	public ScoreboardData getScoreboardData() {
		return sbData;
	}

	public void saveMinigame(){
		MinigameSave minigame = new MinigameSave(name, "config");
		FileConfiguration cfg = minigame.getConfig();
		cfg.set(name, null);
		cfg.createSection(name);
		
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
				modsave.getConfig().createSection(name);
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
            List<String> blocklist = new ArrayList<>();
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
		Minigames.getPlugin().getBackend().saveStatSettings(this, statSettings.values());
		
		minigame.saveConfig();
	}
	
	public void loadMinigame(){
		MinigameSave minigame = new MinigameSave(name, "config");
		FileConfiguration cfg = minigame.getConfig();
		
		//-----------------------------------------------
		//TODO: Remove me after 1.12
		if(cfg.contains(name + ".type")){
            switch (cfg.getString(name + ".type")) {
                case "TEAMS":
					Minigames.getPlugin().getLogger().warning("Your configuration files (" + cfg.getCurrentPath() + ") is outdated and contains and Old type: TEAM, please update to use the New Types.");
                    cfg.set(name + ".type", "MULTIPLAYER");
                    TeamsModule.getMinigameModule(this).addTeam(TeamColor.RED);
                    TeamsModule.getMinigameModule(this).addTeam(TeamColor.BLUE);
                    break;
                case "FREE_FOR_ALL":
					Minigames.getPlugin().getLogger().warning("Your configuration files (" + cfg.getCurrentPath() + ") is outdated and contains and Old type: FREE_FOR_ALL, please update to use the New Types.");
                    cfg.set(name + ".type", "MULTIPLAYER");
                    break;
                case "TREASURE_HUNT":
					Minigames.getPlugin().getLogger().warning("Your configuration files (" + cfg.getCurrentPath() + ") is outdated and contains and Old type:TREASURE_HUNT, please update to use the New Types.");
                    cfg.set(name + ".type", "GLOBAL");
                    cfg.set(name + ".scoretype", "treasure_hunt");
					cfg.set(name + ".timer", Minigames.getPlugin().getConfig().getInt("treasurehunt.findtime") * 60);
                    break;
            }
		}
		//-----------------------------------------------
		
		for(MinigameModule module : getModules()){
			if(!module.useSeparateConfig()){
				module.load(cfg);
				
				if(module.getFlags() != null){
					for(String flag : module.getFlags().keySet()){
						if(cfg.contains(name + "." + flag))
							module.getFlags().get(flag).loadValue(name, cfg);
					}
				}
			}else{
				MinigameSave modsave = new MinigameSave("minigames/" + name + "/" + module.getName().toLowerCase());
				module.load(modsave.getConfig());
				
				if(module.getFlags() != null){
					for(String flag : module.getFlags().keySet()){
						if(modsave.getConfig().contains(name + "." + flag))
							module.getFlags().get(flag).loadValue(name, modsave.getConfig());
					}
				}
			}
		}
		
		for(String flag : configFlags.keySet()){
			if(cfg.contains(name + "." + flag))
				configFlags.get(flag).loadValue(name, cfg);
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

//		Bukkit.getLogger().info("------- Minigame Load -------");
//		Bukkit.getLogger().info("Name: " + getName());
//		Bukkit.getLogger().info("Type: " + getType());
//		Bukkit.getLogger().info("Enabled: " + isEnabled());
//		Bukkit.getLogger().info("-----------------------------");
		
		final Minigame mgm = this;
		
		if(getType() == MinigameType.GLOBAL && isEnabled()){
			Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.getPlugin(), new Runnable() {
				
				@Override
				public void run() {
					Minigames.getPlugin().getMinigameManager().startGlobalMinigame(mgm, null);
				}
			});
		}
		
		getScoreboardData().loadDisplays(minigame, this);
		
		ListenableFuture<Map<MinigameStat, StatSettings>> settingsFuture = Minigames.getPlugin().getBackend().loadStatSettings(this);
		Minigames.getPlugin().getBackend().addServerThreadCallback(settingsFuture, new FutureCallback<Map<MinigameStat, StatSettings>>() {
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
		
		saveMinigame();
	}
	
	@Override
	public String toString(){
		return getName(false);
	}
	
	@Override
	public ScriptReference get(String name) {
		if (name.equalsIgnoreCase("players")) {
			return ScriptCollection.of(players);
		} else if (name.equalsIgnoreCase("teams")) {
			TeamsModule module = TeamsModule.getMinigameModule(this);
			if (module != null) {
				return ScriptCollection.of(module.getTeamsNameMap());
			}
		} else if (name.equalsIgnoreCase("name")) {
			return ScriptValue.of(getName(false));
		} else if (name.equalsIgnoreCase("displayname")) {
			return ScriptValue.of(getName(true));
		}
		
		return null;
	}
	
	@Override
	public Set<String> getKeys() {
		return ImmutableSet.of("players", "teams", "name", "displayname");
	}
	
	@Override
	public String getAsString() {
		return getName(false);
	}
}
