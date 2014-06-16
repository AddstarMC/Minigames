package com.pauldavdesign.mineauz.minigames.minigame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import com.pauldavdesign.mineauz.minigames.CTFFlag;
import com.pauldavdesign.mineauz.minigames.FloorDegenerator;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameSave;
import com.pauldavdesign.mineauz.minigames.MinigameTimer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.MultiplayerBets;
import com.pauldavdesign.mineauz.minigames.MultiplayerTimer;
import com.pauldavdesign.mineauz.minigames.RestoreBlock;
import com.pauldavdesign.mineauz.minigames.TreasureHuntTimer;
import com.pauldavdesign.mineauz.minigames.blockRecorder.RecorderData;
import com.pauldavdesign.mineauz.minigames.config.BooleanFlag;
import com.pauldavdesign.mineauz.minigames.config.Flag;
import com.pauldavdesign.mineauz.minigames.config.IntegerFlag;
import com.pauldavdesign.mineauz.minigames.config.LocationFlag;
import com.pauldavdesign.mineauz.minigames.config.SimpleLocationFlag;
import com.pauldavdesign.mineauz.minigames.config.StringFlag;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemAddFlag;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemBoolean;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemDisplayLoadout;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemDisplayRewards;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemDisplayWhitelist;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemFlag;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemList;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemLoadoutAdd;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemNewLine;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemSaveMinigame;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemString;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemTime;
import com.pauldavdesign.mineauz.minigames.minigame.modules.LoadoutModule;
import com.pauldavdesign.mineauz.minigames.minigame.modules.LobbySettingsModule;
import com.pauldavdesign.mineauz.minigames.minigame.modules.TeamsModule;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardGroup;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardItem;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardRarity;
import com.pauldavdesign.mineauz.minigames.minigame.reward.Rewards;

public class Minigame {
	private Map<String, Flag<?>> configFlags = new HashMap<String, Flag<?>>();
	
	private String name = "GenericName";
	private StringFlag displayName = new StringFlag(null, "displayName");
	private StringFlag objective = new StringFlag(null, "objective");
	private StringFlag gametypeName = new StringFlag(null, "gametypeName");
	private MinigameType type = null; //TODO
	private BooleanFlag enabled = new BooleanFlag(false, "enabled");
	private IntegerFlag minPlayers = new IntegerFlag(2, "minplayers");
	private IntegerFlag maxPlayers = new IntegerFlag(4, "maxplayers");
	private BooleanFlag spMaxPlayers = new BooleanFlag(false, "spMaxPlayers");
	private List<String> flags = new ArrayList<String>(); //TODO
	
	private SimpleLocationFlag floorDegen1 = new SimpleLocationFlag(null, "sfloorpos.1");
	private SimpleLocationFlag floorDegen2 = new SimpleLocationFlag(null, "sfloorpos.2");
	private StringFlag degenType = new StringFlag("inward", "degentype");
	private IntegerFlag degenRandomChance = new IntegerFlag(15, "degenrandom");
	private FloorDegenerator sfloordegen;
	private IntegerFlag floorDegenTime = new IntegerFlag(Minigames.plugin.getConfig().getInt("multiplayer.floordegenerator.time"), "floordegentime");
	
	private List<Location> startLocations = new ArrayList<Location>(); //TODO
	private LocationFlag endPosition = new LocationFlag(null, "endpos");
	private LocationFlag quitPosition = new LocationFlag(null, "quitpos");
	private LocationFlag lobbyPosisiton = new LocationFlag(null, "lobbypos");
	
	private Map<String, RestoreBlock> restoreBlocks = new HashMap<String, RestoreBlock>(); //TODO
	private StringFlag location = new StringFlag(null, "location");
	private IntegerFlag maxRadius = new IntegerFlag(1000, "maxradius");
	private IntegerFlag maxHeight = new IntegerFlag(20, "maxheight");
	private IntegerFlag minTreasure = new IntegerFlag(0, "mintreasure");
	private IntegerFlag maxTreasure = new IntegerFlag(8, "maxtreasure");
	private Rewards rewardItem = new Rewards(); //TODO
	private Rewards secondaryRewardItem = new Rewards(); //TODO
	private BooleanFlag usePermissions = new BooleanFlag(false, "usepermissions");
	private IntegerFlag timer = new IntegerFlag(0, "timer");
	private BooleanFlag useXPBarTimer = new BooleanFlag(true, "useXPBarTimer");
	private IntegerFlag startWaitTime = new IntegerFlag(0, "startWaitTime");
	private Map<MinigamePlayer, CTFFlag> flagCarriers = new HashMap<MinigamePlayer, CTFFlag>();
	private Map<String, CTFFlag> droppedFlag = new HashMap<String, CTFFlag>();
	
	private BooleanFlag itemDrops = new BooleanFlag(false, "itemdrops");
	private BooleanFlag deathDrops = new BooleanFlag(false, "deathdrops");
	private BooleanFlag itemPickup = new BooleanFlag(true, "itempickup");
	private BooleanFlag blockBreak = new BooleanFlag(false, "blockbreak");
	private BooleanFlag blockPlace = new BooleanFlag(false, "blockplace");
	private GameMode defaultGamemode = GameMode.ADVENTURE; //TODO
	private BooleanFlag blocksdrop = new BooleanFlag(true, "blocksdrop");
	private BooleanFlag allowEnderpearls = new BooleanFlag(false, "allowEnderpearls");
	private BooleanFlag allowMPCheckpoints = new BooleanFlag(false, "allowMPCheckpoints");
	private BooleanFlag allowFlight = new BooleanFlag(false, "allowFlight");
	private BooleanFlag enableFlight = new BooleanFlag(false, "enableFlight");
	
	private StringFlag scoreType = new StringFlag("custom", "scoretype");
	private BooleanFlag paintBallMode = new BooleanFlag(false, "paintball");
	private IntegerFlag paintBallDamage = new IntegerFlag(2, "paintballdmg");
	private BooleanFlag unlimitedAmmo = new BooleanFlag(false, "unlimitedammo");
	private BooleanFlag saveCheckpoints = new BooleanFlag(false, "saveCheckpoints");
	private BooleanFlag lateJoin = new BooleanFlag(false, "latejoin");
	private IntegerFlag lives = new IntegerFlag(0, "lives");
	
	private LocationFlag regenArea1 = new LocationFlag(null, "regenarea.1");
	private LocationFlag regenArea2 = new LocationFlag(null, "regenarea.2");
	private IntegerFlag regenDelay = new IntegerFlag(0, "regenDelay");
	
	private Map<String, MinigameModule> modules = new HashMap<String, MinigameModule>();
	
//	private int redTeamScore = 0; //TODO: Remove
//	private int blueTeamScore = 0; //TODO: Remove
//	private Map<TeamColor, Team> teams = new HashMap<TeamColor, Team>();
	private Scoreboard sbManager = Minigames.plugin.getServer().getScoreboardManager().getNewScoreboard();
	
	private IntegerFlag minScore = new IntegerFlag(5, "minscore");
	private IntegerFlag maxScore = new IntegerFlag(10, "maxscore");

//	private List<Location> startLocationsBlue = new ArrayList<Location>(); //TODO: Remove
//	private List<Location> startLocationsRed = new ArrayList<Location>(); //TODO: Remove
//	private String defaultWinner = "none"; //TODO: Use TeamColor object
//	private Team defaultWinner = null;
	
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
	//TreasureHunt
	private TreasureHuntTimer thTimer = null;

	public Minigame(String name, MinigameType type, Location start){
		setup(name, type, start);
	}
	
	public Minigame(String name){
		setup(name, MinigameType.SINGLEPLAYER, null);
	}
	
	private void setup(String name, MinigameType type, Location start){
		this.name = name;
		this.type = type;
		startLocations.add(start);
		
		sbManager.registerNewObjective(this.name, "dummy");
		sbManager.getObjective(this.name).setDisplaySlot(DisplaySlot.SIDEBAR);
		
		for(Class<? extends MinigameModule> mod : Minigames.plugin.mdata.getModules()){
			try {
				addModule(mod.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		addConfigFlag(allowEnderpearls);
		addConfigFlag(allowFlight);
		addConfigFlag(allowMPCheckpoints);
		addConfigFlag(blockBreak);
		addConfigFlag(blockPlace);
		addConfigFlag(blocksdrop);
		addConfigFlag(canSpectateFly);
		addConfigFlag(deathDrops);
		addConfigFlag(degenRandomChance);
		addConfigFlag(degenType);
		addConfigFlag(displayName);
		addConfigFlag(enableFlight);
		addConfigFlag(enabled);
		addConfigFlag(floorDegenTime);
		addConfigFlag(gametypeName);
		addConfigFlag(itemDrops);
		addConfigFlag(itemPickup);
		addConfigFlag(lateJoin);
		addConfigFlag(lives);
		addConfigFlag(location);
		addConfigFlag(maxChestRandom);
		addConfigFlag(maxHeight);
		addConfigFlag(maxPlayers);
		addConfigFlag(maxRadius);
		addConfigFlag(maxScore);
		addConfigFlag(maxTreasure);
		addConfigFlag(minChestRandom);
		addConfigFlag(minPlayers);
		addConfigFlag(minScore);
		addConfigFlag(minTreasure);
		addConfigFlag(objective);
		addConfigFlag(paintBallDamage);
		addConfigFlag(paintBallMode);
		addConfigFlag(randomizeChests);
		addConfigFlag(regenDelay);
		addConfigFlag(saveCheckpoints);
		addConfigFlag(scoreType);
		addConfigFlag(spMaxPlayers);
		addConfigFlag(startWaitTime);
		addConfigFlag(timer);
		addConfigFlag(unlimitedAmmo);
		addConfigFlag(usePermissions);
		addConfigFlag(useXPBarTimer);
	}
	
	private void addConfigFlag(Flag<?> flag){
		configFlags.put(flag.getName(), flag);
	}
	
	public boolean addModule(MinigameModule module){
		if(!modules.containsKey(module.getName())){
			modules.put(module.getName(), module);
			return true;
		}
		return false;
	}
	
	public List<MinigameModule> getModules(){
		return new ArrayList<MinigameModule>(modules.values());
	}
	
	public MinigameModule getModule(String name){
		return modules.get(name);
	}
	
	public boolean isTeamGame(){
		if(getType() == MinigameType.MULTIPLAYER && TeamsModule.getMinigameModule(this).getTeams().size() > 0)
			return true;
		return false;
	}

	public List<RewardItem> getSecondaryRewardItem(){
		return secondaryRewardItem.getReward();
	}
	
	public Rewards getSecondaryRewardItems(){
		return secondaryRewardItem;
	}

	public List<RewardItem> getRewardItem(){
		return rewardItem.getReward();
	}
	
	public Rewards getRewardItems(){
		return rewardItem;
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
	
	public void addStartLocation(Location loc){
		startLocations.add(loc);
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
		if(useDisplay && displayName != null)
			return displayName.getFlag();
		return name;
	}

	public void setDisplayName(String displayName) {
		this.displayName.setFlag(displayName);
	}

	public MinigameType getType(){
		return type;
	}
	
	private Callback<String> getTypeCallback(){
		return new Callback<String>() {

			@Override
			public void setValue(String value) {
				type = MinigameType.valueOf(value.toUpperCase().replace(" ", "_"));
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(type.toString().replace("_", " "));
			}
		};
	}
	
	public void setType(MinigameType type){
		this.type = type;
	}
	
	public MultiplayerTimer getMpTimer() {
		return mpTimer;
	}

	public void setMpTimer(MultiplayerTimer mpTimer) {
		this.mpTimer = mpTimer;
	}
	
	public boolean isNotWaitingForPlayers(){
		if(mpTimer != null && mpTimer.getPlayerWaitTimeLeft() == 0){
			return true;
		}
		return false;
	}
	
	public boolean hasStarted(){
		if(mpTimer != null && mpTimer.getStartWaitTimeLeft() == 0){
			return true;
		}
		else if(type == MinigameType.SINGLEPLAYER && hasPlayers())
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
	
//	public Team getTeam(TeamColor color){
//		return teams.get(color);
//	}
//	
//	public List<Team> getTeams(){
//		return new ArrayList<Team>(teams.values());
//	}
//	
//	public Team addTeam(TeamColor color){
//		return addTeam(color, "");
//	}
//	
//	public Team addTeam(TeamColor color, String name){
//		if(!teams.containsKey(color)){
//			teams.put(color, new Team(color, this));
//			String sbTeam = color.toString().toLowerCase();
//			sbManager.registerNewTeam(sbTeam);
//			sbManager.getTeam(sbTeam).setPrefix(color.getColor().toString());
//			sbManager.getTeam(sbTeam).setAllowFriendlyFire(false);
//			sbManager.getTeam(sbTeam).setCanSeeFriendlyInvisibles(true);
//		}
//		if(!name.equals(""))
//			teams.get(color).setDisplayName(name);
//		return teams.get(color);
//	}
//	
//	public void addTeam(TeamColor color, Team team){
//		teams.put(color, team);
//		String sbTeam = color.toString().toLowerCase();
//		sbManager.registerNewTeam(sbTeam);
//		sbManager.getTeam(sbTeam).setPrefix(color.getColor().toString());
//		sbManager.getTeam(sbTeam).setAllowFriendlyFire(false);
//		sbManager.getTeam(sbTeam).setCanSeeFriendlyInvisibles(true);
//	}
//	
//	public boolean hasTeam(TeamColor color){
//		if(teams.containsKey(color))
//			return true;
//		return false;
//	}
//	
//	public void removeTeam(TeamColor color){
//		if(teams.containsKey(color)){
//			teams.remove(color);
//			sbManager.getTeam(color.toString().toLowerCase()).unregister();
//		}
//	}
//	
//	public boolean hasTeamStartLocations(){
//		for(Team t : teams.values()){
//			if(!t.hasStartLocations())
//				return false;
//		}
//		return true;
//	}

//	public List<OfflinePlayer> getRedTeam() {
//		List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
//		for(OfflinePlayer offply : sbManager.getTeam("Red").getPlayers()){
//			players.add(offply);
//		}
//		return players;
//	}
//
//	public void addRedTeamPlayer(MinigamePlayer player) {
//		sbManager.getTeam("Red").addPlayer(player.getPlayer().getPlayer());
//		player.getPlayer().setScoreboard(sbManager);
//	}
//	
//	public void removeRedTeamPlayer(MinigamePlayer player){
//		sbManager.getTeam("Red").removePlayer(player.getPlayer());
//		if(player.getPlayer().isOnline())
//			player.getPlayer().setScoreboard(Minigames.plugin.getServer().getScoreboardManager().getMainScoreboard());
//	}
//
//	public List<OfflinePlayer> getBlueTeam() {
//		List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
//		for(OfflinePlayer offply : sbManager.getTeam("Blue").getPlayers()){
//			players.add(offply);
//		}
//		return players;
//	}
//
//	public void addBlueTeamPlayer(MinigamePlayer player) {
//		sbManager.getTeam("Blue").addPlayer(player.getPlayer().getPlayer());
//		player.getPlayer().setScoreboard(sbManager);
//	}
//	
//	public void removeBlueTeamPlayer(MinigamePlayer player){
//		sbManager.getTeam("Blue").removePlayer(player.getPlayer());
//		if(player.getPlayer().isOnline())
//			player.getPlayer().setScoreboard(Minigames.plugin.getServer().getScoreboardManager().getMainScoreboard());
//	}
	
	public void setScore(MinigamePlayer ply, int amount){
		sbManager.getObjective(getName(false)).getScore(ply.getName()).setScore(amount);
	}
//
//	public int getRedTeamScore() {
//		return redTeamScore;
//	}
//
//	public void setRedTeamScore(int redTeamScore) {
//		this.redTeamScore = redTeamScore;
//		if(redTeamScore != 0){
//			getScoreboardManager().getObjective(name).getScore(Minigames.plugin.getServer().getOfflinePlayer(ChatColor.RED + "Red Team: ")).setScore(redTeamScore);
//		}
//		else{
//			getScoreboardManager().resetScores(Minigames.plugin.getServer().getOfflinePlayer(ChatColor.RED + "Red Team: "));
//		}
//	}
//	
//	public void incrementRedTeamScore(){
//		redTeamScore++;
//		getScoreboardManager().getObjective(name).getScore(Minigames.plugin.getServer().getOfflinePlayer(ChatColor.RED + "Red Team: ")).setScore(redTeamScore);
//	}
//	
//	public void incrementRedTeamScore(int amount){
//		redTeamScore += amount;
//		getScoreboardManager().getObjective(name).getScore(Minigames.plugin.getServer().getOfflinePlayer(ChatColor.RED + "Red Team: ")).setScore(redTeamScore);
//	}
//
//	public int getBlueTeamScore() {
//		return blueTeamScore;
//	}
//
//	public void setBlueTeamScore(int blueTeamScore) {
//		this.blueTeamScore = blueTeamScore;
//		if(blueTeamScore != 0){
//			getScoreboardManager().getObjective(name).getScore(Minigames.plugin.getServer().getOfflinePlayer(ChatColor.BLUE + "Blue Team: ")).setScore(blueTeamScore);
//		}
//		else{
//			getScoreboardManager().resetScores(Minigames.plugin.getServer().getOfflinePlayer(ChatColor.BLUE + "Blue Team: "));
//		}
//	}
//	
//	public void incrementBlueTeamScore(){
//		blueTeamScore++;
//		getScoreboardManager().getObjective(name).getScore(Minigames.plugin.getServer().getOfflinePlayer(ChatColor.BLUE + "Blue Team: ")).setScore(blueTeamScore);
//	}
//	
//	public void incrementBlueTeamScore(int amount){
//		blueTeamScore += amount;
//		getScoreboardManager().getObjective(name).getScore(Minigames.plugin.getServer().getOfflinePlayer(ChatColor.BLUE + "Blue Team: ")).setScore(blueTeamScore);
//	}

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

//	public void addStartLocationBlue(Location loc){
//		startLocationsBlue.add(loc);
//	}
//	
//	public void addStartLocationBlue(Location loc, int number){
//		if(startLocationsBlue.size() >= number){
//			startLocationsBlue.set(number - 1, loc);
//		}
//		else{
//			startLocationsBlue.add(loc);
//		}
//	}
//	
//	public List<Location> getStartLocationsBlue(){
//		return startLocationsBlue;
//	}
//	
//	public boolean removeStartLocationBlue(int locNumber){
//		if(startLocationsBlue.size() < locNumber){
//			startLocationsBlue.remove(locNumber);
//			return true;
//		}
//		return false;
//	}
//
//	public void addStartLocationRed(Location loc){
//		startLocationsRed.add(loc);
//	}
//	
//	public void addStartLocationRed(Location loc, int number){
//		if(startLocationsRed.size() >= number){
//			startLocationsRed.set(number - 1, loc);
//		}
//		else{
//			startLocationsRed.add(loc);
//		}
//	}
//	
//	public List<Location> getStartLocationsRed(){
//		return startLocationsRed;
//	}
//	
//	public boolean removeStartLocationRed(int locNumber){
//		if(startLocationsRed.size() < locNumber){
//			startLocationsRed.remove(locNumber);
//			return true;
//		}
//		return false;
//	}
	
	public int getMaxScorePerPlayer(){
		float scorePerPlayer = getMaxScore() / getMaxPlayers();
		int score = (int) Math.round(scorePerPlayer * getPlayers().size());
		if(score < minScore.getFlag()){
			score = minScore.getFlag();
		}
		return score;
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
		return defaultGamemode;
	}

	public void setDefaultGamemode(GameMode defaultGamemode) {
		this.defaultGamemode = defaultGamemode;
	}

	public boolean canBlocksdrop() {
		return blocksdrop.getFlag();
	}

	public void setBlocksdrop(boolean blocksdrop) {
		this.blocksdrop.setFlag(blocksdrop);
	}

	public String getScoreType() {
		return scoreType.getFlag();
	}

	public void setScoreType(String scoreType) {
		this.scoreType.setFlag(scoreType);
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

//	public Team getDefaultWinner() {
//		return defaultWinner;
//	}
	
//	private Callback<String> getDefaultWinnerCallback(){
//		return new Callback<String>() {
//
//			@Override
//			public void setValue(String value) {
//				defaultWinner = value;
//			}
//
//			@Override
//			public String getValue() {
//				return defaultWinner;
//			}
//		};
//	}
//	
//	private Callback<String> getDefaultWinnerCallback(){
//		return new Callback<String>() {
//
//			@Override
//			public void setValue(String value) {
//				if(!value.equals("None"))
//					defaultWinner = getTeam(TeamColor.matchColor(value.replace(" ", "_")));
//				else
//					defaultWinner = null;
//			}
//
//			@Override
//			public String getValue() {
//				if(defaultWinner != null)
//					return MinigameUtils.capitalize(defaultWinner.getColor().toString().replace("_", " "));
//				return "None";
//			}
//		};
//	}
//
//	public void setDefaultWinner(Team defaultWinner) {
//		this.defaultWinner = defaultWinner;
//	}
	
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

	public void displayMenu(MinigamePlayer player){
		Menu main = new Menu(6, getName(false), player);
		Menu playerMenu = new Menu(6, getName(false), player);
		Menu treasureHunt = new Menu(6, getName(false), player);
		Menu loadouts = new Menu(6, getName(false), player);
		Menu flags = new Menu(6, getName(false), player);
		Menu lobby = new Menu(6, getName(false), player);
		
		List<MenuItem> itemsMain = new ArrayList<MenuItem>();
		itemsMain.add(enabled.getMenuItem("Enabled", Material.PAPER));
		itemsMain.add(usePermissions.getMenuItem("Use Permissions", Material.PAPER));
		List<String> mgTypes = new ArrayList<String>();
		for(MinigameType val : MinigameType.values()){
			mgTypes.add(MinigameUtils.capitalize(val.toString().replace("_", " ")));
		}
		itemsMain.add(new MenuItemList("Game Type", Material.PAPER, getTypeCallback(), mgTypes));
		List<String> scoreTypes = new ArrayList<String>();
		for(String val : Minigames.plugin.getScoreTypes().getGameMechanics().keySet()){
			scoreTypes.add(MinigameUtils.capitalize(val));
		}
		itemsMain.add(new MenuItemList("Score Type", MinigameUtils.stringToList("Multiplayer Only"), Material.ROTTEN_FLESH, new Callback<String>() {

			@Override
			public void setValue(String value) {
				scoreType.setFlag(value.toLowerCase());
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(scoreType.getFlag());
			}
		}, scoreTypes));
		MenuItemString obj = (MenuItemString) objective.getMenuItem("Objective Description", Material.DIAMOND);
		obj.setAllowNull(true);
		itemsMain.add(obj);
		obj = (MenuItemString) gametypeName.getMenuItem("Gametype Description", Material.SIGN);
		obj.setAllowNull(true);
		itemsMain.add(obj);
		obj = (MenuItemString) displayName.getMenuItem("Display Name", Material.SIGN);
		obj.setAllowNull(true);
		itemsMain.add(obj);
		itemsMain.add(new MenuItemNewLine());
		itemsMain.add(minScore.getMenuItem("Min. Score", Material.STEP, MinigameUtils.stringToList("Multiplayer Only")));
		itemsMain.add(maxScore.getMenuItem("Max. Score", Material.DOUBLE_STEP, MinigameUtils.stringToList("Multiplayer Only")));
		itemsMain.add(minPlayers.getMenuItem("Min. Players", Material.STEP, MinigameUtils.stringToList("Multiplayer Only")));
		itemsMain.add(maxPlayers.getMenuItem("Max. Players", Material.DOUBLE_STEP, MinigameUtils.stringToList("Multiplayer Only")));
		itemsMain.add(spMaxPlayers.getMenuItem("Enable Singleplayer Max Players", Material.IRON_FENCE));
		itemsMain.add(new MenuItemPage("Lobby Settings", MinigameUtils.stringToList("Multiplayer Only"), Material.WOOD_DOOR, lobby));
		itemsMain.add(new MenuItemNewLine());
		itemsMain.add(new MenuItemTime("Time Length", MinigameUtils.stringToList("Multiplayer Only"), Material.WATCH, new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				timer.setFlag(value);
			}

			@Override
			public Integer getValue() {
				return timer.getFlag();
			}
		}, 0, null));
		itemsMain.add(useXPBarTimer.getMenuItem("Use XP bar as Timer", Material.ENDER_PEARL));
		itemsMain.add(new MenuItemTime("Start Wait Time", MinigameUtils.stringToList("Multiplayer Only"), Material.WATCH, new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				startWaitTime.setFlag(value);
			}

			@Override
			public Integer getValue() {
				return startWaitTime.getFlag();
			}
		}, 3, null));
		itemsMain.add(lateJoin.getMenuItem("Allow Late Join", Material.DEAD_BUSH, MinigameUtils.stringToList("Multiplayer Only")));
		itemsMain.add(new MenuItemDisplayRewards("Primary Rewards", Material.CHEST, rewardItem));
		itemsMain.add(new MenuItemDisplayRewards("Secondary Rewards", Material.CHEST, secondaryRewardItem));
		itemsMain.add(new MenuItemDisplayWhitelist("Block Whitelist/Blacklist", MinigameUtils.stringToList("Blocks that can/can't;be broken"), 
				Material.CHEST, getBlockRecorder().getWBBlocks(), getBlockRecorder().getWhitelistModeCallback()));
		itemsMain.add(new MenuItemNewLine());
		List<String> floorDegenDes = new ArrayList<String>();
		floorDegenDes.add("Mainly used to prevent");
		floorDegenDes.add("islanding in spleef Minigames.");
		List<String> floorDegenOpt = new ArrayList<String>();
		floorDegenOpt.add("Inward");
		floorDegenOpt.add("Circle");
		floorDegenOpt.add("Random");
		itemsMain.add(new MenuItemList("Floor Degenerator Type", floorDegenDes, Material.SNOW_BLOCK, new Callback<String>() {

			@Override
			public void setValue(String value) {
				degenType.setFlag(value.toLowerCase());
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(degenType.getFlag());
			}
		}, floorDegenOpt));
		List<String> degenRandDes = new ArrayList<String>();
		degenRandDes.add("Chance of block being");
		degenRandDes.add("removed on random");
		degenRandDes.add("degeneration.");
		itemsMain.add(degenRandomChance.getMenuItem("Random Floor Degen Chance", Material.SNOW, degenRandDes, 1, 100));
		itemsMain.add(floorDegenTime.getMenuItem("Floor Degenerator Delay", Material.WATCH, 1, null));
		itemsMain.add(new MenuItemTime("Regeneration Delay", MinigameUtils.stringToList("Time in seconds before;Minigame regeneration starts"), Material.WATCH, new Callback<Integer>() {

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
		itemsMain.add(new MenuItemPage("Player Settings", Material.SKULL_ITEM, playerMenu));
		List<String> thDes = new ArrayList<String>();
		thDes.add("Treasure hunt related");
		thDes.add("settings.");
		itemsMain.add(new MenuItemPage("Treasure Hunt Settings", thDes, Material.CHEST, treasureHunt));
		MenuItemDisplayLoadout defLoad = new MenuItemDisplayLoadout("Default Loadout", Material.DIAMOND_SWORD, LoadoutModule.getMinigameModule(this).getDefaultPlayerLoadout(), this);
		defLoad.setAllowDelete(false);
		itemsMain.add(defLoad);
		itemsMain.add(new MenuItemPage("Additional Loadouts", Material.CHEST, loadouts));
		itemsMain.add(canSpectateFly.getMenuItem("Allow Spectator Fly", Material.FEATHER));
		List<String> rndChstDes = new ArrayList<String>();
		rndChstDes.add("Randomize items in");
		rndChstDes.add("chest upon first opening");
		itemsMain.add(randomizeChests.getMenuItem("Randomize Chests", Material.CHEST, rndChstDes));
		rndChstDes.clear();
		rndChstDes.add("Min. item randomization");
		itemsMain.add(minChestRandom.getMenuItem("Min. Chest Random", Material.STEP, rndChstDes, 0, null));
		rndChstDes.clear();
		rndChstDes.add("Max. item randomization");
		itemsMain.add(maxChestRandom.getMenuItem("Max. Chest Random", Material.DOUBLE_STEP, rndChstDes, 0, null));
		itemsMain.add(new MenuItemNewLine());

		//--------------//
		//Loadout Settings
		//--------------//
		List<MenuItem> mi = new ArrayList<MenuItem>();
		List<String> des = new ArrayList<String>();
		des.add("Shift + Right Click to Delete");
		for(String ld : LoadoutModule.getMinigameModule(this).getLoadouts()){
			Material item = Material.THIN_GLASS;
			if(LoadoutModule.getMinigameModule(this).getLoadout(ld).getItems().size() != 0){
				item = LoadoutModule.getMinigameModule(this).getLoadout(ld).getItem((Integer)LoadoutModule.getMinigameModule(this).getLoadout(ld).getItems().toArray()[0]).getType();
			}
			mi.add(new MenuItemDisplayLoadout(ld, des, item, LoadoutModule.getMinigameModule(this).getLoadout(ld), this));
		}
		loadouts.addItem(new MenuItemLoadoutAdd("Add Loadout", Material.ITEM_FRAME, LoadoutModule.getMinigameModule(this).getLoadoutMap(), this), 53);
		loadouts.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, main), loadouts.getSize() - 9);
		loadouts.addItems(mi);
		
		main.addItems(itemsMain);
		main.addItem(new MenuItemSaveMinigame("Save " + getName(false), Material.REDSTONE_TORCH_ON, this), main.getSize() - 1);

		//--------------------//
		//Treasure Hunt Settings
		//--------------------//
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
		treasureHunt.addItems(itemsTreasureHunt);
		treasureHunt.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, main), main.getSize() - 9);

		//----------------------//
		//Minigame Player Settings
		//----------------------//
		List<MenuItem> itemsPlayer = new ArrayList<MenuItem>(14);
		itemsPlayer.add(allowEnderpearls.getMenuItem("Allow Enderpearls", Material.ENDER_PEARL));
		itemsPlayer.add(itemDrops.getMenuItem("Allow Item Drops", Material.DIAMOND_SWORD));
		itemsPlayer.add(deathDrops.getMenuItem("Allow Death Drops", Material.SKULL_ITEM));
		itemsPlayer.add(itemPickup.getMenuItem("Allow Item Pickup", Material.DIAMOND));
		itemsPlayer.add(blockBreak.getMenuItem("Allow Block Break", Material.DIAMOND_PICKAXE));
		itemsPlayer.add(blockPlace.getMenuItem("Allow Block Place", Material.STONE));
		itemsPlayer.add(blocksdrop.getMenuItem("Allow Block Drops", Material.COBBLESTONE));
		itemsPlayer.add(lives.getMenuItem("Lives", Material.APPLE, 0, null));
		itemsPlayer.add(paintBallMode.getMenuItem("Paintball Mode", Material.SNOW_BALL));
		itemsPlayer.add(paintBallDamage.getMenuItem("Paintball Damage", Material.ARROW, 1, null));
		itemsPlayer.add(unlimitedAmmo.getMenuItem("Unlimited Ammo", Material.SNOW_BLOCK));
		itemsPlayer.add(allowMPCheckpoints.getMenuItem("Enable Multiplayer Checkpoints", Material.SIGN));
		itemsPlayer.add(saveCheckpoints.getMenuItem("Save Checkpoints", Material.SIGN, MinigameUtils.stringToList("Singleplayer Only")));
		itemsPlayer.add(new MenuItemPage("Flags", MinigameUtils.stringToList("Singleplayer flags"), Material.SIGN, flags));
		itemsPlayer.add(allowFlight.getMenuItem("Allow Flight", Material.FEATHER, MinigameUtils.stringToList("Allow flight to;be toggled")));
		itemsPlayer.add(enableFlight.getMenuItem("Enable Flight", Material.FEATHER, MinigameUtils.stringToList("Start players;in flight;(Must have Allow;Flight)")));
		playerMenu.addItems(itemsPlayer);
		playerMenu.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, main), main.getSize() - 9);
		
		//--------------//
		//Minigame Flags//
		//--------------//
		List<MenuItem> itemsFlags = new ArrayList<MenuItem>(getFlags().size());
		for(String flag : getFlags()){
			itemsFlags.add(new MenuItemFlag(Material.SIGN, flag, getFlags()));
		}
		flags.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, playerMenu), flags.getSize() - 9);
		flags.addItem(new MenuItemAddFlag("Add Flag", Material.ITEM_FRAME, this), flags.getSize() - 1);
		flags.addItems(itemsFlags);
		
		//--------------//
		//Lobby Settings//
		//--------------//
		List<MenuItem> itemsLobby = new ArrayList<MenuItem>(4);
		itemsLobby.add(new MenuItemBoolean("Can Interact on Player Wait", Material.STONE_BUTTON, LobbySettingsModule.getMinigameModule(this).getCanInteractPlayerWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Can Interact on Start Wait", Material.STONE_BUTTON, LobbySettingsModule.getMinigameModule(this).getCanInteractStartWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Can Move on Player Wait", Material.ICE, LobbySettingsModule.getMinigameModule(this).getCanMovePlayerWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Can Move on Start Wait", Material.ICE, LobbySettingsModule.getMinigameModule(this).getCanMoveStartWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Teleport After Player Wait", MinigameUtils.stringToList("Should players be teleported;after player wait time?"), 
				Material.ENDER_PEARL, LobbySettingsModule.getMinigameModule(this).getTeleportOnPlayerWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Teleport on Start", MinigameUtils.stringToList("Should players teleport;to the start position;after lobby?"),
				Material.ENDER_PEARL, LobbySettingsModule.getMinigameModule(this).getTeleportOnStartCallback()));
		lobby.addItems(itemsLobby);
		lobby.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, main), lobby.getSize() - 9);

		for(MinigameModule mod : getModules()){
			mod.addMenuOptions(main, this);
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
		
		for(MinigameModule module : getModules()){
			if(!module.useSeparateConfig())
				module.save(this, cfg);
			else{
				MinigameSave modsave = new MinigameSave("minigames/" + name + "/" + module.getName().toLowerCase());
				modsave.getConfig().set(name, null);
				module.save(this, modsave.getConfig());
				modsave.saveConfig();
			}
		}
		
		for(String configOpt : configFlags.keySet()){
			if(configFlags.get(configOpt).getDefaultFlag() == null || 
					!configFlags.get(configOpt).getDefaultFlag().equals(configFlags.get(configOpt).getFlag()))
				configFlags.get(configOpt).saveValue(name, cfg);
		}
		if(!getStartLocations().isEmpty()){
			for(int i = 0; i < getStartLocations().size(); i++){
				Minigames.plugin.mdata.minigameSetLocations(name, getStartLocations().get(i), "startpos." + String.valueOf(i), minigame.getConfig());
			}
		}
//		for(Team team : teams.values()){
//			cfg.set(name + ".teams." + team.getColor().toString() + ".displayName", team.getDisplayName());
//			if(!team.getStartLocations().isEmpty()){
//				for(int i = 0; i < team.getStartLocations().size(); i++){
//					Minigames.plugin.mdata.minigameSetLocations(name, team.getStartLocations().get(i), "teams." + team.getColor().toString() + ".startpos." + i, cfg);
//				}
//			}
//		}
//		if(!getStartLocationsBlue().isEmpty()){
//			for(int i = 0; i < getStartLocationsBlue().size(); i++){
//				Minigames.plugin.mdata.minigameSetLocations(name, getStartLocationsBlue().get(i), "startposblue." + String.valueOf(i), minigame.getConfig());
//			}
//		}
//		if(!getStartLocationsRed().isEmpty()){
//			for(int i = 0; i < getStartLocationsRed().size(); i++){
//				Minigames.plugin.mdata.minigameSetLocations(name, getStartLocationsRed().get(i), "startposred." + String.valueOf(i), minigame.getConfig());
//			}
//		} //TODO: Remove Me!
		if(!getRewardItems().getRewards().isEmpty() || !getRewardItems().getGroups().isEmpty()){
			int count = 0;
			for(RewardItem item : getRewardItems().getRewards()){
				if(item.getItem() != null){
					minigame.getConfig().set(name + ".reward." + count + ".item", item.getItem());
					minigame.getConfig().set(name + ".reward." + count + ".rarity", item.getRarity().toString());
				}
				else if(item.getMoney() != 0){
					minigame.getConfig().set(name + ".reward." + count + ".money", item.getMoney());
					minigame.getConfig().set(name + ".reward." + count + ".rarity", item.getRarity().toString());
				}
				count++;
			}
			for(RewardGroup group : getRewardItems().getGroups()){
				count = 0;
				for(RewardItem item : group.getItems()){
					if(item.getItem() != null){
						minigame.getConfig().set(name + ".reward." + group.getName() + "." + count + ".item", item.getItem());
					}
					else if(item.getMoney() != 0){
						minigame.getConfig().set(name + ".reward." + group.getName() + "." + count + ".money", item.getMoney());
					}
					count++;
				}
				minigame.getConfig().set(name + ".reward." + group.getName() + ".rarity", group.getRarity().toString());
			}
		}
		if(!getSecondaryRewardItems().getRewards().isEmpty() || !getSecondaryRewardItems().getGroups().isEmpty()){
			int count = 0;
			for(RewardItem item : getSecondaryRewardItems().getRewards()){
				if(item.getItem() != null){
					minigame.getConfig().set(name + ".reward2." + count + ".item", item.getItem());
					minigame.getConfig().set(name + ".reward2." + count + ".rarity", item.getRarity().toString());
				}
				else if(item.getMoney() != 0){
					minigame.getConfig().set(name + ".reward2." + count + ".money", item.getMoney());
					minigame.getConfig().set(name + ".reward2." + count + ".rarity", item.getRarity().toString());
				}
				count++;
			}
			for(RewardGroup group : getSecondaryRewardItems().getGroups()){
				count = 0;
				for(RewardItem item : group.getItems()){
					if(item.getItem() != null){
						minigame.getConfig().set(name + ".reward2." + group.getName() + "." + count + ".item", item.getItem());
					}
					else if(item.getMoney() != 0){
						minigame.getConfig().set(name + ".reward2." + group.getName() + "." + count + ".money", item.getMoney());
					}
					count++;
				}
				minigame.getConfig().set(name + ".reward2." + group.getName() + ".rarity", group.getRarity().toString());
			}
		}
		if(!getFlags().isEmpty()){
			minigame.getConfig().set(name + ".flags", getFlags());
		}
		
		if(!restoreBlocks.isEmpty()){
			Set<String> blocks = restoreBlocks.keySet();
			for(String block : blocks){
				minigame.getConfig().set(name + ".resblocks." + block + ".type", restoreBlocks.get(block).getBlock().toString());
				Minigames.plugin.mdata.minigameSetLocationsShort(name, restoreBlocks.get(block).getLocation(), "resblocks." + block + ".location", minigame.getConfig());
			}
		}
		
		if(getDefaultGamemode() != GameMode.ADVENTURE){
			minigame.getConfig().set(name + ".gamemode", getDefaultGamemode().toString());
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
	
	public void loadMinigame(){
		MinigameSave minigame = new MinigameSave(name, "config");
		FileConfiguration cfg = minigame.getConfig();
		
		for(MinigameModule module : getModules()){
			if(!module.useSeparateConfig())
				module.load(this, cfg);
			else{
				MinigameSave modsave = new MinigameSave("minigames/" + name + "/" + module.getName().toLowerCase());
				module.load(this, modsave.getConfig());
			}
		}
		
		//-----------------------------------------------
		//TODO: Remove me after 1.7
		if(cfg.contains(name + ".type")){
			if(cfg.getString(name + ".type").equals("TEAMS")) {
				cfg.set(name + ".type", "MULTIPLAYER");
				TeamsModule.getMinigameModule(this).addTeam(this, TeamColor.RED);
				TeamsModule.getMinigameModule(this).addTeam(this, TeamColor.BLUE);
				//TODO Convert old team spawn point loading into new teams
			}
			else if(cfg.getString(name + ".type").equals("FREE_FOR_ALL")){
				cfg.set(name + ".type", "MULTIPLAYER");
			}
		}
		//-----------------------------------------------
		
		for(String flag : configFlags.keySet()){
			if(cfg.contains(name + "." + flag))
				configFlags.get(flag).loadValue(name, cfg);
		}
		if(minigame.getConfig().contains(name + ".startpos")){
			Set<String> locs = minigame.getConfig().getConfigurationSection(name + ".startpos").getKeys(false);
			
			for(int i = 0; i < locs.size(); i++){
				addStartLocation(Minigames.plugin.mdata.minigameLocations(name, "startpos." + String.valueOf(i), minigame.getConfig()), i + 1);
			}
		}
//		
//		if(cfg.contains(name + ".teams")){
//			Set<String> teams = cfg.getConfigurationSection(name + ".teams").getKeys(false);
//			for(String team : teams){
//				Team t = addTeam(TeamColor.valueOf(team), cfg.getString(name + ".teams." + team + ".displayName"));
//				if(cfg.contains(name + ".teams." + team + ".startPos")){
//					Set<String> locations = cfg.getConfigurationSection(name + ".teams." + team + ".startPos").getKeys(false);
//					for(String loc : locations){
//						t.addStartLocation(Minigames.plugin.mdata.minigameLocations(name, "teams." + team + ".startPos." + loc, cfg));
//					}
//				}
//			}
//		}
//		if(minigame.getConfig().contains(name + ".startposred")){ //TODO: Remove after 1.7
//			if(!hasTeam(TeamColor.RED))
//				addTeam(TeamColor.RED);
//			Set<String> locs = minigame.getConfig().getConfigurationSection(name + ".startposred").getKeys(false);
//			
//			for(int i = 0; i < locs.size(); i++){
//				getTeam(TeamColor.RED).addStartLocation(Minigames.plugin.mdata.minigameLocations(name, "startposred." + String.valueOf(i), cfg));
//			}
//		}
//		if(minigame.getConfig().contains(name + ".startposblue")){ //TODO: Remove after 1.7
//			if(!hasTeam(TeamColor.BLUE))
//				addTeam(TeamColor.BLUE);
//			Set<String> locs = minigame.getConfig().getConfigurationSection(name + ".startposblue").getKeys(false);
//			
//			for(int i = 0; i < locs.size(); i++){
//				getTeam(TeamColor.BLUE).addStartLocation(Minigames.plugin.mdata.minigameLocations(name, "startposblue." + String.valueOf(i), cfg));
//			}
//		}
		if(minigame.getConfig().contains(name + ".reward")){
			Set<String> keys = minigame.getConfig().getConfigurationSection(name + ".reward").getKeys(false);
			for(String key : keys){
				if(minigame.getConfig().contains(name + ".reward." + key + ".item") || minigame.getConfig().contains(name + ".reward." + key + ".money")){
					ItemStack item = minigame.getConfig().getItemStack(name + ".reward." + key + ".item");
					double money = minigame.getConfig().getDouble(name + ".reward." + key + ".money");
					RewardRarity rarity = RewardRarity.valueOf(minigame.getConfig().getString(name + ".reward." + key + ".rarity"));
					if(item != null)
						getRewardItems().addItem(item, rarity);
					else
						getRewardItems().addMoney(money, rarity);
				}
				else{
					Set<String> keys2 = minigame.getConfig().getConfigurationSection(name + ".reward." + key).getKeys(false);
					RewardGroup group = getRewardItems().addGroup(key, RewardRarity.valueOf(minigame.getConfig().getString(name + ".reward." + key + ".rarity")));
					for(String key2 : keys2){
						if(!key2.equals("rarity")){
							ItemStack item = minigame.getConfig().getItemStack(name + ".reward." + key + "." + key2 + ".item");
							double money = minigame.getConfig().getDouble(name + ".reward." + key + "." + key2 + ".money");
							RewardRarity rarity = RewardRarity.NORMAL;
							if(item != null){
								RewardItem it = new RewardItem(item, rarity);
								group.addItem(it);
							}
							else{
								RewardItem it = new RewardItem(money, rarity);
								group.addItem(it);
							}
						}
					}
				}
			}
		}
		if(minigame.getConfig().contains(name + ".reward2")){
			Set<String> keys = minigame.getConfig().getConfigurationSection(name + ".reward2").getKeys(false);
			for(String key : keys){
				if(minigame.getConfig().contains(name + ".reward2." + key + ".item") || minigame.getConfig().contains(name + ".reward2." + key + ".money")){
					ItemStack item = minigame.getConfig().getItemStack(name + ".reward2." + key + ".item");
					double money = minigame.getConfig().getDouble(name + ".reward2." + key + ".money");
					RewardRarity rarity = RewardRarity.valueOf(minigame.getConfig().getString(name + ".reward2." + key + ".rarity"));
					if(item != null)
						getSecondaryRewardItems().addItem(item, rarity);
					else
						getSecondaryRewardItems().addMoney(money, rarity);
				}
				else{
					Set<String> keys2 = minigame.getConfig().getConfigurationSection(name + ".reward2." + key).getKeys(false);
					RewardGroup group = getSecondaryRewardItems().addGroup(key, RewardRarity.valueOf(minigame.getConfig().getString(name + ".reward2." + key + ".rarity")));
					for(String key2 : keys2){
						if(!key2.equals("rarity")){
							ItemStack item = minigame.getConfig().getItemStack(name + ".reward2." + key + "." + key2 + ".item");
							double money = minigame.getConfig().getDouble(name + ".reward2." + key + "." + key2 + ".money");
							RewardRarity rarity = RewardRarity.NORMAL;
							if(item != null){
								RewardItem it = new RewardItem(item, rarity);
								group.addItem(it);
							}
							else{
								RewardItem it = new RewardItem(money, rarity);
								group.addItem(it);
							}
						}
					}
				}
			}
		}
		if(!minigame.getConfig().getStringList(name + ".flags").isEmpty()){
			setFlags(minigame.getConfig().getStringList(name + ".flags"));
		}
		if(minigame.getConfig().contains(name + ".resblocks") && minigame.getConfig().getString(name + ".resblocks") != "true" && minigame.getConfig().getString(name + ".resblocks") != "false"){
			Set<String> blocks = minigame.getConfig().getConfigurationSection(name + ".resblocks").getKeys(false);
			for(String block : blocks){
				RestoreBlock res = new RestoreBlock(block, Material.getMaterial(minigame.getConfig().getString(name + ".resblocks." + block + ".type")), Minigames.plugin.mdata.minigameLocationsShort(name, ".resblocks." + block + ".location", minigame.getConfig()));
				
				addRestoreBlock(res);
			}
		}
		
		if(minigame.getConfig().contains(name + ".gamemode")){
			setDefaultGamemode(GameMode.valueOf(minigame.getConfig().getString(name + ".gamemode")));
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
		
		if(getType() == MinigameType.TREASURE_HUNT && isEnabled()){
			Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
				
				@Override
				public void run() {
					Minigames.plugin.mdata.startGlobalMinigame(getName(false));
				}
			});
		}
		
		getScoreboardData().loadDisplays(minigame, this);
		
		saveMinigame();
	}
	
	@Override
	public String toString(){
		return getName(false);
	}
}
