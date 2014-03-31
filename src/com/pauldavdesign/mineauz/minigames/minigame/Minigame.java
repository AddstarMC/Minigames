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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
import com.pauldavdesign.mineauz.minigames.PlayerLoadout;
import com.pauldavdesign.mineauz.minigames.RestoreBlock;
import com.pauldavdesign.mineauz.minigames.TreasureHuntTimer;
import com.pauldavdesign.mineauz.minigames.blockRecorder.RecorderData;
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
import com.pauldavdesign.mineauz.minigames.menu.MenuItemInteger;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemList;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemLoadoutAdd;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemNewLine;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemSaveMinigame;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemString;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemTime;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardGroup;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardItem;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardRarity;
import com.pauldavdesign.mineauz.minigames.minigame.reward.Rewards;

public class Minigame {
	private String name = "GenericName";
	private String displayName = null;
	private String objective = null;
	private String gametypeName = null;
	private MinigameType type = null;
	private boolean enabled = false;
	private int minPlayers = 2;
	private int maxPlayers = 4;
	private boolean spMaxPlayers = false;
	private List<String> flags = new ArrayList<String>();
	
	private Location floorDegen1 = null;
	private Location floorDegen2 = null;
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
	private Map<String, RestoreBlock> restoreBlocks = new HashMap<String, RestoreBlock>();
	private String location = null;
	private int maxRadius = 1000;
	private int maxHeight = 20;
	private int minTreasure = 0;
	private int maxTreasure = 8;
	private Rewards rewardItem = new Rewards();
	private Rewards secondaryRewardItem = new Rewards();
	private boolean usePermissions = false;
	private int timer = 0;
	private int startWaitTime = 0;
	private Map<MinigamePlayer, CTFFlag> flagCarriers = new HashMap<MinigamePlayer, CTFFlag>();
	private Map<String, CTFFlag> droppedFlag = new HashMap<String, CTFFlag>();
	
	//Lobby settings
	private boolean canMovePlayerWait = true;
	private boolean canMoveStartWait = true;
	private boolean canInteractPlayerWait = true;
	private boolean canInteractStartWait = true;
	private boolean teleportOnPlayerWait = false;
	private boolean teleportOnStart = true;
	
	private boolean itemDrops = false;
	private boolean deathDrops = false;
	private boolean itemPickup = true;
	private boolean blockBreak = false;
	private boolean blockPlace = false;
	private GameMode defaultGamemode = GameMode.ADVENTURE;
	private boolean blocksdrop = true;
	private boolean allowEnderpearls = false;
	private boolean allowMPCheckpoints = false;
	
	private String scoreType = "custom";
	private boolean paintBallMode = false;
	private int paintBallDamage = 2;
	private boolean unlimitedAmmo = false;
	private boolean saveCheckpoint = false;
	private boolean lateJoin = false;
	private int lives = 0;
	
	private Location regenArea1 = null;
	private Location regenArea2 = null;
	private int regenDelay = 0;
	
//	private int redTeamScore = 0; //TODO: Remove
//	private int blueTeamScore = 0; //TODO: Remove
	private Map<TeamColor, Team> teams = new HashMap<TeamColor, Team>();
	private Scoreboard sbManager = Minigames.plugin.getServer().getScoreboardManager().getNewScoreboard();
	
	private int minScore = 5;
	private int maxScore = 10;

//	private List<Location> startLocationsBlue = new ArrayList<Location>(); //TODO: Remove
//	private List<Location> startLocationsRed = new ArrayList<Location>(); //TODO: Remove
//	private String defaultWinner = "none"; //TODO: Use TeamColor object
	private Team defaultWinner = null;
	
	private boolean canSpectateFly = false;
	
	private boolean randomizeChests = false;
	private int minChestRandom = 5;
	private int maxChestRandom = 10;
	
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
		this.name = name;
		this.type = type;
		startLocations.add(start);
		
		addTeam(TeamColor.RED);
		addTeam(TeamColor.BLUE);
		sbManager.registerNewObjective(this.name, "dummy");
		sbManager.getObjective(this.name).setDisplaySlot(DisplaySlot.SIDEBAR);
		
	}
	
	public Minigame(String name){
		this.name = name;

		addTeam(TeamColor.RED);
		addTeam(TeamColor.BLUE);
		sbManager.registerNewObjective(this.name, "dummy");
		sbManager.getObjective(this.name).setDisplaySlot(DisplaySlot.SIDEBAR);
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
		this.maxRadius = maxRadius;
	}

	public int getMaxRadius(){
		return maxRadius;
	}
	
	private Callback<Integer> getMaxRadiusCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				maxRadius = value;
			}

			@Override
			public Integer getValue() {
				return maxRadius;
			}
		};
	}

	public int getMaxHeight() {
		return maxHeight;
	}
	
	private Callback<Integer> getMaxHeightCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				maxHeight = value;
			}

			@Override
			public Integer getValue() {
				return maxHeight;
			}
		};
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public String getLocation(){
		return location;
	}
	
	public Callback<String> getLocationCallback(){
		return new Callback<String>() {

			@Override
			public void setValue(String value) {
				location = value;
			}

			@Override
			public String getValue() {
				return location;
			}
		};
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
	
	public PlayerLoadout getDefaultPlayerLoadout(){
		return defaultLoadout;
	}
	
	public boolean hasDefaultLoadout(){
		if(defaultLoadout.getItems().isEmpty() && defaultLoadout.getAllPotionEffects().isEmpty() && 
				defaultLoadout.hasFallDamage() && !defaultLoadout.hasHunger()){
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
	
	public Map<String, PlayerLoadout> getLoadoutMap(){
		return extraLoadouts;
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
	
	public boolean isEnabled(){
		return enabled;
	}
	
	private Callback<Boolean> getEnabledCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				enabled = value;
			}

			@Override
			public Boolean getValue() {
				return enabled;
			}
		};
	}

	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}

	public int getMinPlayers(){
		return minPlayers;
	}
	
	private Callback<Integer> getMinPlayersCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				minPlayers = value;
			}

			@Override
			public Integer getValue() {
				return minPlayers;
			}
		};
	}

	public void setMinPlayers(int minPlayers){
		this.minPlayers = minPlayers;
	}

	public int getMaxPlayers(){
		return maxPlayers;
	}
	
	private Callback<Integer> getMaxPlayersCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				maxPlayers = value;
			}

			@Override
			public Integer getValue() {
				return maxPlayers;
			}
		};
	}

	public void setMaxPlayers(int maxPlayers){
		this.maxPlayers = maxPlayers;
	}

	public boolean isSpMaxPlayers() {
		return spMaxPlayers;
	}

	public void setSpMaxPlayers(boolean spMaxPlayers) {
		this.spMaxPlayers = spMaxPlayers;
	}
	
	private Callback<Boolean> getSPMaxPlayerCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				spMaxPlayers = value;
			}

			@Override
			public Boolean getValue() {
				return spMaxPlayers;
			}
		};
	}

	public Location getFloorDegen1(){
		return floorDegen1;
	}

	public void setFloorDegen1(Location loc){
		this.floorDegen1 = loc;
	}

	public Location getFloorDegen2(){
		return floorDegen2;
	}

	public void setFloorDegen2(Location loc){
		this.floorDegen2 = loc;
	}

	public String getDegenType() {
		return degenType;
	}
	
	private Callback<String> getDegenTypeCallback(){
		return new Callback<String>() {

			@Override
			public void setValue(String value) {
				degenType = value.toLowerCase();
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(degenType);
			}
		};
	}

	public void setDegenType(String degenType) {
		this.degenType = degenType;
	}

	public int getDegenRandomChance() {
		return degenRandomChance;
	}
	
	private Callback<Integer> getDegenRandomChanceCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				degenRandomChance = value;
			}

			@Override
			public Integer getValue() {
				return degenRandomChance;
			}
		};
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
	
	public String getName(boolean useDisplay){
		if(useDisplay && displayName != null)
			return displayName;
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public Callback<String> getDisplayNameCallback(){
		return new Callback<String>() {

			@Override
			public void setValue(String value) {
				displayName = value;
			}

			@Override
			public String getValue() {
				return displayName;
			}
		};
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
	
	private Callback<Boolean> getUsePermissionsCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				usePermissions = value;
			}

			@Override
			public Boolean getValue() {
				return usePermissions;
			}
		};
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
	
	public Team getTeam(TeamColor color){
		return teams.get(color);
	}
	
	public List<Team> getTeams(){
		return new ArrayList<Team>(teams.values());
	}
	
	public Team addTeam(TeamColor color){
		return addTeam(color, "");
	}
	
	public Team addTeam(TeamColor color, String name){
		if(!teams.containsKey(color)){
			teams.put(color, new Team(color, this));
			String sbTeam = color.toString().toLowerCase();
			sbManager.registerNewTeam(sbTeam);
			sbManager.getTeam(sbTeam).setPrefix(color.getColor().toString());
			sbManager.getTeam(sbTeam).setAllowFriendlyFire(false);
			sbManager.getTeam(sbTeam).setCanSeeFriendlyInvisibles(true);
		}
		if(!name.equals(""))
			teams.get(color).setDisplayName(name);
		return teams.get(color);
	}
	
	public void addTeam(TeamColor color, Team team){
		teams.put(color, team);
		String sbTeam = color.toString().toLowerCase();
		sbManager.registerNewTeam(sbTeam);
		sbManager.getTeam(sbTeam).setPrefix(color.getColor().toString());
		sbManager.getTeam(sbTeam).setAllowFriendlyFire(false);
		sbManager.getTeam(sbTeam).setCanSeeFriendlyInvisibles(true);
	}
	
	public boolean hasTeam(TeamColor color){
		if(teams.containsKey(color))
			return true;
		return false;
	}
	
	public void removeTeam(TeamColor color){
		if(teams.containsKey(color)){
			teams.remove(color);
			sbManager.getTeam(color.toString().toLowerCase()).unregister();
		}
	}
	
	public boolean hasTeamStartLocations(){
		for(Team t : teams.values()){
			if(!t.hasStartLocations())
				return false;
		}
		return true;
	}

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
		sbManager.getObjective(name).getScore(ply.getPlayer()).setScore(amount);
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
		return minScore;
	}
	
	private Callback<Integer> getMinScoreCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				minScore = value;
			}

			@Override
			public Integer getValue() {
				return minScore;
			}
		};
	}

	public void setMinScore(int minScore) {
		this.minScore = minScore;
	}

	public int getMaxScore() {
		return maxScore;
	}
	
	private Callback<Integer> getMaxScoreCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				maxScore = value;
			}

			@Override
			public Integer getValue() {
				return maxScore;
			}
		};
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
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
		if(score < minScore){
			score = minScore;
		}
		return score;
	}

	public int getMinTreasure() {
		return minTreasure;
	}
	
	private Callback<Integer> getMinTreasureCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				minTreasure = value;
			}

			@Override
			public Integer getValue() {
				return minTreasure;
			}
		};
	}

	public void setMinTreasure(int minTreasure) {
		this.minTreasure = minTreasure;
	}

	public int getMaxTreasure() {
		return maxTreasure;
	}
	
	private Callback<Integer> getMaxTreasureCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				maxTreasure = value;
			}

			@Override
			public Integer getValue() {
				return maxTreasure;
			}
		};
	}

	public void setMaxTreasure(int maxTreasure) {
		this.maxTreasure = maxTreasure;
	}

	public FloorDegenerator getFloorDegenerator() {
		return sfloordegen;
	}

	public void addFloorDegenerator() {
		sfloordegen = new FloorDegenerator(getFloorDegen1(), getFloorDegen2(), this);
	}
	
	public void setTimer(int time){
		timer = time;
	}
	
	public int getTimer(){
		return timer;
	}
	
	private Callback<Integer> getTimerCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				timer = value;
			}

			@Override
			public Integer getValue() {
				return timer;
			}
		};
	}

	public int getStartWaitTime() {
		return startWaitTime;
	}
	
	private Callback<Integer> getStartWaitTimeCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				startWaitTime = value;
			}

			@Override
			public Integer getValue() {
				return startWaitTime;
			}
		};
	}

	public void setStartWaitTime(int startWaitTime) {
		this.startWaitTime = startWaitTime;
	}

	public boolean hasItemDrops() {
		return itemDrops;
	}
	
	private Callback<Boolean> getItemDropsCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				itemDrops = value;
			}

			@Override
			public Boolean getValue() {
				return itemDrops;
			}
		};
	}

	public void setItemDrops(boolean itemDrops) {
		this.itemDrops = itemDrops;
	}

	public boolean hasDeathDrops() {
		return deathDrops;
	}
	
	private Callback<Boolean> getDeathDropsCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				deathDrops = value;
			}

			@Override
			public Boolean getValue() {
				return deathDrops;
			}
		};
	}

	public void setDeathDrops(boolean deathDrops) {
		this.deathDrops = deathDrops;
	}

	public boolean hasItemPickup() {
		return itemPickup;
	}
	
	private Callback<Boolean> getItemPickupCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				itemPickup = value;
			}

			@Override
			public Boolean getValue() {
				return itemPickup;
			}
		};
	}

	public void setItemPickup(boolean itemPickup) {
		this.itemPickup = itemPickup;
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
		return blockBreak;
	}
	
	private Callback<Boolean> getBlockBreakCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				blockBreak = value;
			}

			@Override
			public Boolean getValue() {
				return blockBreak;
			}
		};
	}

	public boolean canBlockPlace() {
		return blockPlace;
	}
	
	private Callback<Boolean> getBlockPlaceCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				blockPlace = value;
			}

			@Override
			public Boolean getValue() {
				return blockPlace;
			}
		};
	}

	public void setCanBlockPlace(boolean blockPlace) {
		this.blockPlace = blockPlace;
	}

	public void setCanBlockBreak(boolean blockBreak) {
		this.blockBreak = blockBreak;
	}
	
	public GameMode getDefaultGamemode() {
		return defaultGamemode;
	}

	public void setDefaultGamemode(GameMode defaultGamemode) {
		this.defaultGamemode = defaultGamemode;
	}

	public boolean canBlocksdrop() {
		return blocksdrop;
	}
	
	private Callback<Boolean> getBlocksDropCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				blocksdrop = value;
			}

			@Override
			public Boolean getValue() {
				return blocksdrop;
			}
		};
	}

	public void setBlocksdrop(boolean blocksdrop) {
		this.blocksdrop = blocksdrop;
	}

	public String getScoreType() {
		return scoreType;
	}
	
	private Callback<String> getScoreTypeCallback(){
		return new Callback<String>() {

			@Override
			public void setValue(String value) {
				scoreType = value.toLowerCase();
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(scoreType);
			}
		};
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
	
	private Callback<Boolean> getPaintballModeCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				paintBallMode = value;
			}

			@Override
			public Boolean getValue() {
				return paintBallMode;
			}
		};
	}

	public void setPaintBallMode(boolean paintBallMode) {
		this.paintBallMode = paintBallMode;
	}

	public int getPaintBallDamage() {
		return paintBallDamage;
	}
	
	private Callback<Integer> getPaintballDamageCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				paintBallDamage = value;
			}

			@Override
			public Integer getValue() {
				return paintBallDamage;
			}
		};
	}

	public void setPaintBallDamage(int paintBallDamage) {
		this.paintBallDamage = paintBallDamage;
	}

	public boolean hasUnlimitedAmmo() {
		return unlimitedAmmo;
	}
	
	private Callback<Boolean> getUnlimitedAmmoCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				unlimitedAmmo = value;
			}

			@Override
			public Boolean getValue() {
				return unlimitedAmmo;
			}
		};
	}

	public void setUnlimitedAmmo(boolean unlimitedAmmo) {
		this.unlimitedAmmo = unlimitedAmmo;
	}

	public boolean canSaveCheckpoint() {
		return saveCheckpoint;
	}
	
	private Callback<Boolean> getSaveCheckpointCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				saveCheckpoint = value;
			}

			@Override
			public Boolean getValue() {
				return saveCheckpoint;
			}
		};
	}

	public void setSaveCheckpoint(boolean saveCheckpoint) {
		this.saveCheckpoint = saveCheckpoint;
	}

	public boolean canLateJoin() {
		return lateJoin;
	}
	
	private Callback<Boolean> getLateJoinCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				lateJoin = value;
			}

			@Override
			public Boolean getValue() {
				return lateJoin;
			}
		};
	}

	public void setLateJoin(boolean lateJoin) {
		this.lateJoin = lateJoin;
	}

	public boolean canSpectateFly() {
		return canSpectateFly;
	}
	
	private Callback<Boolean> getSpectatorFlyCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				canSpectateFly = value;
			}

			@Override
			public Boolean getValue() {
				return canSpectateFly;
			}
		};
	}

	public void setCanSpectateFly(boolean canSpectateFly) {
		this.canSpectateFly = canSpectateFly;
	}

	public boolean isRandomizeChests() {
		return randomizeChests;
	}
	
	private Callback<Boolean> getRandomizeChestsCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				randomizeChests = value;
			}

			@Override
			public Boolean getValue() {
				return randomizeChests;
			}
		};
	}

	public void setRandomizeChests(boolean randomizeChests) {
		this.randomizeChests = randomizeChests;
	}

	public int getMinChestRandom() {
		return minChestRandom;
	}
	
	private Callback<Integer> getMinChestRandomCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				minChestRandom = value;
			}

			@Override
			public Integer getValue() {
				return minChestRandom;
			}
		};
	}

	public void setMinChestRandom(int minChestRandom) {
		this.minChestRandom = minChestRandom;
	}

	public int getMaxChestRandom() {
		return maxChestRandom;
	}
	
	private Callback<Integer> getMaxChestRandomCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				maxChestRandom = value;
			}

			@Override
			public Integer getValue() {
				return maxChestRandom;
			}
		};
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

	public int getRegenDelay() {
		return regenDelay;
	}

	public void setRegenDelay(int regenDelay) {
		if(regenDelay < 0)
			regenDelay = 0;
		this.regenDelay = regenDelay;
	}
	
	private Callback<Integer> getRegenDelayCallback(){
		return new Callback<Integer>() {
			@Override
			public void setValue(Integer value) {
				regenDelay = value;
			}
			@Override
			public Integer getValue() {
				return regenDelay;
			}
		};
	}

	public int getLives() {
		return lives;
	}
	
	private Callback<Integer> getLivesCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				lives = value;
			}

			@Override
			public Integer getValue() {
				return lives;
			}
		};
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public int getFloorDegenTime() {
		return floorDegenTime;
	}
	
	private Callback<Integer> getFloorDegenTimeCallback(){
		return new Callback<Integer>() {

			@Override
			public void setValue(Integer value) {
				floorDegenTime = value;
			}

			@Override
			public Integer getValue() {
				return floorDegenTime;
			}
		};
	}

	public void setFloorDegenTime(int floorDegenTime) {
		this.floorDegenTime = floorDegenTime;
	}

	public Team getDefaultWinner() {
		return defaultWinner;
	}
	
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
	private Callback<String> getDefaultWinnerCallback(){
		return new Callback<String>() {

			@Override
			public void setValue(String value) {
				defaultWinner = getTeam(TeamColor.matchColor(value.replace(" ", "_")));
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(defaultWinner.getColor().toString().replace("_", " "));
			}
		};
	}

	public void setDefaultWinner(Team defaultWinner) {
		this.defaultWinner = defaultWinner;
	}
	
	public boolean isAllowedEnderpearls() {
		return allowEnderpearls;
	}
	
	private Callback<Boolean> getAllowedEnderpearlsCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				allowEnderpearls = value;
			}

			@Override
			public Boolean getValue() {
				return allowEnderpearls;
			}
		};
	}

	public void setAllowEnderpearls(boolean allowEnderpearls) {
		this.allowEnderpearls = allowEnderpearls;
	}

	public boolean isAllowedMPCheckpoints() {
		return allowMPCheckpoints;
	}

	public void setAllowMPCheckpoints(boolean allowMPCheckpoints) {
		this.allowMPCheckpoints = allowMPCheckpoints;
	}
	
	public Callback<Boolean> getAllowMPCheckpointsCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				allowMPCheckpoints = value;
			}

			@Override
			public Boolean getValue() {
				return allowMPCheckpoints;
			}
		};
	}

	public Scoreboard getScoreboardManager(){
		return sbManager;
	}
	
	public String getObjective() {
		return objective;
	}
	
	private Callback<String> getObjectiveCallback(){
		return new Callback<String>() {

			@Override
			public void setValue(String value) {
				objective = value;
			}

			@Override
			public String getValue() {
				return objective;
			}
		};
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public String getGametypeName() {
		return gametypeName;
	}
	
	private Callback<String> getGametypeNameCallback(){
		return new Callback<String>(){

			@Override
			public void setValue(String value) {
				gametypeName = value;
			}

			@Override
			public String getValue() {
				return gametypeName;
			}
			
		};
	}

	public void setGametypeName(String gametypeName) {
		this.gametypeName = gametypeName;
	}

	public void displayMenu(MinigamePlayer player){
		Menu main = new Menu(6, getName(false), player);
		Menu playerMenu = new Menu(6, getName(false), player);
		Menu treasureHunt = new Menu(6, getName(false), player);
		Menu loadouts = new Menu(6, getName(false), player);
		Menu flags = new Menu(6, getName(false), player);
		Menu lobby = new Menu(6, getName(false), player);
		
		List<MenuItem> itemsMain = new ArrayList<MenuItem>();
		itemsMain.add(new MenuItemBoolean("Enabled", Material.PAPER, getEnabledCallback()));
		itemsMain.add(new MenuItemBoolean("Use Permissions", Material.PAPER, getUsePermissionsCallback()));
		List<String> mgTypes = new ArrayList<String>();
		for(MinigameType val : MinigameType.values()){
			mgTypes.add(MinigameUtils.capitalize(val.toString().replace("_", " ")));
		}
		itemsMain.add(new MenuItemList("Game Type", Material.PAPER, getTypeCallback(), mgTypes));
		List<String> scoreTypes = new ArrayList<String>();
		for(String val : Minigames.plugin.getScoreTypes().getScoreTypes().keySet()){
			scoreTypes.add(MinigameUtils.capitalize(val));
		}
		itemsMain.add(new MenuItemList("Score Type", MinigameUtils.stringToList("Multiplayer Only"), Material.ROTTEN_FLESH, getScoreTypeCallback(), scoreTypes));
		MenuItemString obj = new MenuItemString("Objective Description", MinigameUtils.stringToList("Objective for the player;to see when they;join the game"),
				Material.DIAMOND, getObjectiveCallback());
		obj.setAllowNull(true);
		itemsMain.add(obj);
		obj = new MenuItemString("Gametype Description", MinigameUtils.stringToList("Gametype name to replace;\"Singleplayer\" or \"Free For All\""), 
				Material.SIGN, getGametypeNameCallback());
		obj.setAllowNull(true);
		itemsMain.add(obj);
		obj = new MenuItemString("Display Name", MinigameUtils.stringToList("Public announced name; that will display in chat."), Material.SIGN, getDisplayNameCallback());
		obj.setAllowNull(true);
		itemsMain.add(obj);
		List<String> teams = new ArrayList<String>(this.teams.size());
		for(TeamColor t : this.teams.keySet()){
			teams.add(MinigameUtils.capitalize(t.getColor().toString().replace("_", " ")));
		}
		itemsMain.add(new MenuItemList("Default Winning Team", Material.PAPER, getDefaultWinnerCallback(), teams));
		itemsMain.add(new MenuItemNewLine());
		itemsMain.add(new MenuItemInteger("Min. Score", MinigameUtils.stringToList("Multiplayer Only"), Material.STEP, getMinScoreCallback(), 0, null));
		itemsMain.add(new MenuItemInteger("Max. Score", MinigameUtils.stringToList("Multiplayer Only"), Material.DOUBLE_STEP, getMaxScoreCallback(), 0, null));
		itemsMain.add(new MenuItemInteger("Min. Players", MinigameUtils.stringToList("Multiplayer Only"), Material.STEP, getMinPlayersCallback(), 0, null));
		itemsMain.add(new MenuItemInteger("Max. Players", MinigameUtils.stringToList("Multiplayer Only"), Material.DOUBLE_STEP, getMaxPlayersCallback(), 0, null));
		itemsMain.add(new MenuItemBoolean("Enable Singleplayer Max Players", Material.IRON_FENCE, getSPMaxPlayerCallback()));
		itemsMain.add(new MenuItemPage("Lobby Settings", MinigameUtils.stringToList("Multiplayer Only"), Material.WOOD_DOOR, lobby));
		itemsMain.add(new MenuItemNewLine());
		itemsMain.add(new MenuItemTime("Time Length", MinigameUtils.stringToList("Multiplayer Only"), Material.WATCH, getTimerCallback(), 0, null));
		itemsMain.add(new MenuItemTime("Start Wait Time", MinigameUtils.stringToList("Multiplayer Only"), Material.WATCH, getStartWaitTimeCallback(), 3, null));
		itemsMain.add(new MenuItemBoolean("Allow Late Join", MinigameUtils.stringToList("Multiplayer Only"), Material.DEAD_BUSH, getLateJoinCallback()));
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
		itemsMain.add(new MenuItemList("Floor Degenerator Type", floorDegenDes, Material.SNOW_BLOCK, getDegenTypeCallback(), floorDegenOpt));
		List<String> degenRandDes = new ArrayList<String>();
		degenRandDes.add("Chance of block being");
		degenRandDes.add("removed on random");
		degenRandDes.add("degeneration.");
		itemsMain.add(new MenuItemInteger("Random Floor Degen Chance", degenRandDes, Material.SNOW, getDegenRandomChanceCallback(), 1, 100));
		itemsMain.add(new MenuItemTime("Floor Degenerator Delay", Material.WATCH, getFloorDegenTimeCallback(), 1, null));
		itemsMain.add(new MenuItemTime("Regeneration Delay", MinigameUtils.stringToList("Time in seconds before;Minigame regeneration starts"), Material.WATCH, getRegenDelayCallback(), 0, null));
		itemsMain.add(new MenuItemNewLine());
		itemsMain.add(new MenuItemPage("Player Settings", Material.SKULL_ITEM, playerMenu));
		List<String> thDes = new ArrayList<String>();
		thDes.add("Treasure hunt related");
		thDes.add("settings.");
		itemsMain.add(new MenuItemPage("Treasure Hunt Settings", thDes, Material.CHEST, treasureHunt));
		MenuItemDisplayLoadout defLoad = new MenuItemDisplayLoadout("Default Loadout", Material.DIAMOND_SWORD, defaultLoadout, this);
		defLoad.setAllowDelete(false);
		itemsMain.add(defLoad);
		itemsMain.add(new MenuItemPage("Additional Loadouts", Material.CHEST, loadouts));
		itemsMain.add(new MenuItemBoolean("Allow Spectator Fly", Material.FEATHER, getSpectatorFlyCallback()));
		List<String> rndChstDes = new ArrayList<String>();
		rndChstDes.add("Randomize items in");
		rndChstDes.add("chest upon first opening");
		itemsMain.add(new MenuItemBoolean("Randomize Chests", rndChstDes, Material.CHEST, getRandomizeChestsCallback()));
		rndChstDes.clear();
		rndChstDes.add("Min. item randomization");
		itemsMain.add(new MenuItemInteger("Min. Chest Random", rndChstDes, Material.STEP, getMinChestRandomCallback(), 0, null));
		rndChstDes.clear();
		rndChstDes.add("Max. item randomization");
		itemsMain.add(new MenuItemInteger("Max. Chest Random", rndChstDes, Material.DOUBLE_STEP, getMaxChestRandomCallback(), 0, null));

		//--------------//
		//Loadout Settings
		//--------------//
		List<MenuItem> mi = new ArrayList<MenuItem>();
		List<String> des = new ArrayList<String>();
		des.add("Shift + Right Click to Delete");
		for(String ld : getLoadouts()){
			Material item = Material.THIN_GLASS;
			if(getLoadout(ld).getItems().size() != 0){
				item = getLoadout(ld).getItem((Integer)getLoadout(ld).getItems().toArray()[0]).getType();
			}
			mi.add(new MenuItemDisplayLoadout(ld, des, item, getLoadout(ld), this));
		}
		loadouts.addItem(new MenuItemLoadoutAdd("Add Loadout", Material.ITEM_FRAME, extraLoadouts, this), 53);
		loadouts.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, main), loadouts.getSize() - 9);
		loadouts.addItems(mi);
		
		main.addItems(itemsMain);
		main.addItem(new MenuItemSaveMinigame("Save " + getName(false), Material.REDSTONE_TORCH_ON, this), main.getSize() - 1);

		//--------------------//
		//Treasure Hunt Settings
		//--------------------//
		List<MenuItem> itemsTreasureHunt = new ArrayList<MenuItem>(5);
		itemsTreasureHunt.add(new MenuItemString("Location Name", MinigameUtils.stringToList("Name to appear when;treasure spawns"), Material.BED, getLocationCallback()));
		itemsTreasureHunt.add(new MenuItemInteger("Max. Radius", Material.ENDER_PEARL, getMaxRadiusCallback(), 10, null));
		List<String> maxHeightDes = new ArrayList<String>();
		maxHeightDes.add("Max. height of where a");
		maxHeightDes.add("chest can generate.");
		maxHeightDes.add("Can still move above to");
		maxHeightDes.add("avoid terrain");
		itemsTreasureHunt.add(new MenuItemInteger("Max. Height", Material.BEACON, getMaxHeightCallback(), 1, 256));
		List<String> minDes = new ArrayList<String>();
		minDes.add("Minimum items to");
		minDes.add("spawn in chest.");
		itemsTreasureHunt.add(new MenuItemInteger("Min. Items", minDes, Material.STEP, getMinTreasureCallback(), 0, 27));
		List<String> maxDes = new ArrayList<String>();
		maxDes.add("Maximum items to");
		maxDes.add("spawn in chest.");
		itemsTreasureHunt.add(new MenuItemInteger("Max. Items", maxDes, Material.DOUBLE_STEP, getMaxTreasureCallback(), 0, 27));
		treasureHunt.addItems(itemsTreasureHunt);
		treasureHunt.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, main), main.getSize() - 9);

		//----------------------//
		//Minigame Player Settings
		//----------------------//
		List<MenuItem> itemsPlayer = new ArrayList<MenuItem>(14);
		itemsPlayer.add(new MenuItemBoolean("Allow Enderpearls", Material.ENDER_PEARL, getAllowedEnderpearlsCallback()));
		itemsPlayer.add(new MenuItemBoolean("Allow Item Drops", Material.DIAMOND_SWORD, getItemDropsCallback()));
		itemsPlayer.add(new MenuItemBoolean("Allow Death Drops", Material.SKULL_ITEM, getDeathDropsCallback()));
		itemsPlayer.add(new MenuItemBoolean("Allow Item Pickup", Material.DIAMOND, getItemPickupCallback()));
		itemsPlayer.add(new MenuItemBoolean("Allow Block Break", Material.DIAMOND_PICKAXE, getBlockBreakCallback()));
		itemsPlayer.add(new MenuItemBoolean("Allow Block Place", Material.STONE, getBlockPlaceCallback()));
		itemsPlayer.add(new MenuItemBoolean("Allow Block Drops", Material.COBBLESTONE, getBlocksDropCallback()));
		itemsPlayer.add(new MenuItemInteger("Lives", Material.APPLE, getLivesCallback(), 0, null));
		itemsPlayer.add(new MenuItemBoolean("Paintball Mode", Material.SNOW_BALL, getPaintballModeCallback()));
		itemsPlayer.add(new MenuItemInteger("Paintball Damage", Material.ARROW, getPaintballDamageCallback(), 1, null));
		itemsPlayer.add(new MenuItemBoolean("Unlimited Ammo", Material.SNOW_BLOCK, getUnlimitedAmmoCallback()));
		itemsPlayer.add(new MenuItemBoolean("Enable Multiplayer Checkpoints", Material.SIGN, getAllowMPCheckpointsCallback()));
		itemsPlayer.add(new MenuItemBoolean("Save Checkpoints", MinigameUtils.stringToList("Singleplayer Only"), Material.SIGN, getSaveCheckpointCallback()));
		itemsPlayer.add(new MenuItemPage("Flags", MinigameUtils.stringToList("Singleplayer flags"), Material.SIGN, flags));
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
		itemsLobby.add(new MenuItemBoolean("Can Interact on Player Wait", Material.STONE_BUTTON, getCanInteractPlayerWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Can Interact on Start Wait", Material.STONE_BUTTON, getCanInteractStartWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Can Move on Player Wait", Material.ICE, getCanMovePlayerWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Can Move on Start Wait", Material.ICE, getCanMoveStartWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Teleport After Player Wait", MinigameUtils.stringToList("Should players be teleported;after player wait time?"), 
				Material.ENDER_PEARL, getTeleportOnPlayerWaitCallback()));
		itemsLobby.add(new MenuItemBoolean("Teleport on Start", MinigameUtils.stringToList("Should players teleport;to the start position;after lobby?"),
				Material.ENDER_PEARL, getTeleportOnStartCallback()));
		lobby.addItems(itemsLobby);
		lobby.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, main), lobby.getSize() - 9);
		
		main.displayMenu(player);
		
	}

	public ScoreboardData getScoreboardData() {
		return sbData;
	}

	public boolean canMovePlayerWait() {
		return canMovePlayerWait;
	}

	public void setCanMovePlayerWait(boolean canMovePlayerWait) {
		this.canMovePlayerWait = canMovePlayerWait;
	}
	
	private Callback<Boolean> getCanMovePlayerWaitCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				canMovePlayerWait = value;
			}
			@Override
			public Boolean getValue(){
				return canMovePlayerWait;
			}
		};
	}

	public boolean canMoveStartWait() {
		return canMoveStartWait;
	}

	public void setCanMoveStartWait(boolean canMoveStartWait) {
		this.canMoveStartWait = canMoveStartWait;
	}
	
	private Callback<Boolean> getCanMoveStartWaitCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				canMoveStartWait = value;
			}
			@Override
			public Boolean getValue(){
				return canMoveStartWait;
			}
		};
	}

	public boolean canInteractPlayerWait() {
		return canInteractPlayerWait;
	}

	public void setCanInteractPlayerWait(boolean canInteractPlayerWait) {
		this.canInteractPlayerWait = canInteractPlayerWait;
	}
	
	private Callback<Boolean> getCanInteractPlayerWaitCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				canInteractPlayerWait = value;
			}
			@Override
			public Boolean getValue(){
				return canInteractPlayerWait;
			}
		};
	}

	public boolean canInteractStartWait() {
		return canInteractStartWait;
	}

	public void setCanInteractStartWait(boolean canInteractStartWait) {
		this.canInteractStartWait = canInteractStartWait;
	}
	
	private Callback<Boolean> getCanInteractStartWaitCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				canInteractStartWait = value;
			}
			@Override
			public Boolean getValue(){
				return canInteractStartWait;
			}
		};
	}

	public boolean isTeleportOnStart() {
		return teleportOnStart;
	}

	public void setTeleportOnStart(boolean teleportOnStart) {
		this.teleportOnStart = teleportOnStart;
	}
	
	private Callback<Boolean> getTeleportOnStartCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				teleportOnStart = value;
			}
			@Override
			public Boolean getValue(){
				return teleportOnStart;
			}
		};
	}

	public boolean isTeleportOnPlayerWait() {
		return teleportOnPlayerWait;
	}

	public void setTeleportOnPlayerWait(boolean teleportOnPlayerWait) {
		this.teleportOnPlayerWait = teleportOnPlayerWait;
	}
	
	private Callback<Boolean> getTeleportOnPlayerWaitCallback(){
		return new Callback<Boolean>() {
			@Override
			public void setValue(Boolean value){
				teleportOnPlayerWait = value;
			}
			@Override
			public Boolean getValue(){
				return teleportOnPlayerWait;
			}
		};
	}

	public void saveMinigame(){
		MinigameSave minigame = new MinigameSave(name, "config");
		FileConfiguration cfg = minigame.getConfig();
		cfg.set(name, null);
		
		minigame.getConfig().set(name + ".displayName", displayName);
		minigame.getConfig().set(name + ".startpos", null);
		minigame.getConfig().set(name + ".startposred", null);
		minigame.getConfig().set(name + ".startposblue", null);
		if(!getStartLocations().isEmpty()){
			for(int i = 0; i < getStartLocations().size(); i++){
				Minigames.plugin.mdata.minigameSetLocations(name, getStartLocations().get(i), "startpos." + String.valueOf(i), minigame.getConfig());
			}
		}
		for(Team team : teams.values()){
			cfg.set(name + ".teams." + team.getColor().toString() + ".displayName", team.getDisplayName());
			if(!team.getStartLocations().isEmpty()){
				for(int i = 0; i < team.getStartLocations().size(); i++){
					Minigames.plugin.mdata.minigameSetLocations(name, team.getStartLocations().get(i), "teams." + team.getColor().toString().toLowerCase() + ".startpos." + i, cfg);
				}
			}
		}
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
		if(getEndPosition() != null){
			Minigames.plugin.mdata.minigameSetLocations(name, getEndPosition(), "endpos", minigame.getConfig());
		}
		if(getLobbyPosition() != null){
			Minigames.plugin.mdata.minigameSetLocations(name, getLobbyPosition(), "lobbypos", minigame.getConfig());
		}
		if(getQuitPosition() != null){
			Minigames.plugin.mdata.minigameSetLocations(name, getQuitPosition(), "quitpos", minigame.getConfig());
		}
		if(getFloorDegen1() != null){
			Minigames.plugin.mdata.minigameSetLocations(name, getFloorDegen1(), "sfloorpos.1", minigame.getConfig());
		}
		else{
			minigame.getConfig().set(name + ".sfloorpos", null);
		}//TODO: remove all these set null checks
		if(getFloorDegen2() != null){
			Minigames.plugin.mdata.minigameSetLocations(name, getFloorDegen2(), "sfloorpos.2", minigame.getConfig());
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
		
		minigame.getConfig().set(name + ".type", getType().toString());
		minigame.getConfig().set(name + ".minplayers", getMinPlayers());
		minigame.getConfig().set(name + ".maxplayers", getMaxPlayers());
		if(isSpMaxPlayers())
			minigame.getConfig().set(name + ".spMaxPlayers", isSpMaxPlayers());
		else
			minigame.getConfig().set(name + ".spMaxPlayers", null);
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
		minigame.getConfig().set(name + ".reward", null);
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
		minigame.getConfig().set(name + ".reward2", null);
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
		else{
			minigame.getConfig().set(name + ".flags", null);
		}
		
		if(hasDefaultLoadout()){
			minigame.getConfig().set(name + ".loadout", null);
			for(Integer slot : getDefaultPlayerLoadout().getItems()){
				minigame.getConfig().set(name + ".loadout." + slot, getDefaultPlayerLoadout().getItem(slot));
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
			
			if(!getDefaultPlayerLoadout().hasFallDamage())
				minigame.getConfig().set(name + ".loadout.falldamage", getDefaultPlayerLoadout().hasFallDamage());
			else
				minigame.getConfig().set(name + ".loadout.falldamage", null);
			
			if(getDefaultPlayerLoadout().hasHunger())
				minigame.getConfig().set(name + ".loadout.hunger", getDefaultPlayerLoadout().hasHunger());
			else
				minigame.getConfig().set(name + ".loadout.hunger", null);
		}
		else{
			minigame.getConfig().set(name + ".loadout", null);
		}
		
		if(hasLoadouts()){
			minigame.getConfig().set(name + ".extraloadouts", null);
			for(String loadout : getLoadouts()){
				for(Integer slot : getLoadout(loadout).getItems()){
					minigame.getConfig().set(name + ".extraloadouts." + loadout + "." + slot, getLoadout(loadout).getItem(slot));
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
				
				if(!getLoadout(loadout).hasFallDamage())
					minigame.getConfig().set(name + ".extraloadouts." + loadout + ".falldamage", getLoadout(loadout).hasFallDamage());
				else
					minigame.getConfig().set(name + ".extraloadouts." + loadout + ".falldamage", null);
				
				if(getLoadout(loadout).hasHunger())
					minigame.getConfig().set(name + ".extraloadouts." + loadout + ".hunger", getLoadout(loadout).hasHunger());
				else
					minigame.getConfig().set(name + ".extraloadouts." + loadout + ".hunger", null);
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
		
		if(getDefaultGamemode() != GameMode.ADVENTURE){
			minigame.getConfig().set(name + ".gamemode", getDefaultGamemode().toString());
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
		
		if(!getScoreType().equals("custom")){
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
		
		if(getDefaultWinner() != null){
			minigame.getConfig().set(name + ".defaultwinner", getDefaultWinner());
		}
		else{
			minigame.getConfig().set(name + ".defaultwinner", null);
		}
		
		if(isAllowedEnderpearls()){
			minigame.getConfig().set(name + ".allowEnderpearls", isAllowedEnderpearls());
		}
		else{
			minigame.getConfig().set(name + ".allowEnderpearls", null);
		}
		if(isAllowedMPCheckpoints())
			minigame.getConfig().set(name + ".allowMPCheckpoints", isAllowedMPCheckpoints());
		else
			minigame.getConfig().set(name + ".allowMPCheckpoints", null);
		
		if(getObjective() != null)
			minigame.getConfig().set(name + ".objective", getObjective());
		else
			minigame.getConfig().set(name + ".objective", null);
		
		if(getGametypeName() != null)
			minigame.getConfig().set(name + ".gametypeName", getGametypeName());
		else
			minigame.getConfig().set(name + "gametypeName", null);
		
		if(!canInteractPlayerWait)
			minigame.getConfig().set(name + ".canInteractPlayerWait", canInteractPlayerWait);
		else
			minigame.getConfig().set(name + ".canInteractPlayerWait", null);
		if(!canInteractStartWait)
			minigame.getConfig().set(name + ".canInteractStartWait", canInteractStartWait);
		else
			minigame.getConfig().set(name + ".canInteractStartWait", null);
		if(!canMovePlayerWait)
			minigame.getConfig().set(name + ".canMovePlayerWait", canMovePlayerWait);
		else
			minigame.getConfig().set(name + ".canMovePlayerWait", null);
		if(!canMoveStartWait)
			minigame.getConfig().set(name + ".canMoveStartWait", canMoveStartWait);
		else
			minigame.getConfig().set(name + ".canMoveStartWait", null);
		if(!teleportOnStart)
			minigame.getConfig().set(name + ".teleportOnStart", teleportOnStart);
		else
			minigame.getConfig().set(name + ".teleportOnStart", null);
		if(teleportOnPlayerWait)
			minigame.getConfig().set(name + ".teleportOnPlayerWait", teleportOnPlayerWait);
		else
			minigame.getConfig().set(name + ".teleportOnPlayerWait", null);
		if(regenDelay != 0)
			minigame.getConfig().set(name + ".regenDelay", regenDelay);
		else
			minigame.getConfig().set(name + ".regenDelay", null);
		
		getScoreboardData().saveDisplays(minigame, name);
		
		minigame.saveConfig();
	}
	
	public void loadMinigame(){
		MinigameSave minigame = new MinigameSave(name, "config");
		FileConfiguration cfg = minigame.getConfig();
		
		if(minigame.getConfig().contains(name + ".displayName")){
			displayName = minigame.getConfig().getString(name + ".displayName");
		}
		if(minigame.getConfig().contains(name + ".startpos")){
			Set<String> locs = minigame.getConfig().getConfigurationSection(name + ".startpos").getKeys(false);
			
			for(int i = 0; i < locs.size(); i++){
				addStartLocation(Minigames.plugin.mdata.minigameLocations(name, "startpos." + String.valueOf(i), minigame.getConfig()), i + 1);
			}
		}
		
		if(cfg.contains(name + ".teams")){
			Set<String> teams = cfg.getConfigurationSection(name + ".teams").getKeys(false);
			for(String team : teams){
				Team t = addTeam(TeamColor.valueOf(team), cfg.getString(name + ".teams." + team + ".displayName"));
				if(cfg.contains(name + ".teams." + team + ".startPos")){
					Set<String> locations = cfg.getConfigurationSection(name + ".teams." + team + ".startPos").getKeys(false);
					for(String loc : locations){
						t.addStartLocation(Minigames.plugin.mdata.minigameLocations(name, "teams." + team + ".startPos." + loc, cfg));
					}
				}
			}
		}
//		if(minigame.getConfig().contains(name + ".startposred")){
//			Set<String> locs = minigame.getConfig().getConfigurationSection(name + ".startposred").getKeys(false);
//			
//			for(int i = 0; i < locs.size(); i++){
//				addStartLocationRed(Minigames.plugin.mdata.minigameLocations(name, "startposred." + String.valueOf(i), minigame.getConfig()), i + 1);
//			}
//		}
//		if(minigame.getConfig().contains(name + ".startposblue")){
//			Set<String> locs = minigame.getConfig().getConfigurationSection(name + ".startposblue").getKeys(false);
//			
//			for(int i = 0; i < locs.size(); i++){
//				addStartLocationBlue(Minigames.plugin.mdata.minigameLocations(name, "startposblue." + String.valueOf(i), minigame.getConfig()), i + 1);
//			}
//		} TODO: Reassign ME!
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
			setFloorDegen1(Minigames.plugin.mdata.minigameLocations(name, "sfloorpos.1", minigame.getConfig()));
		}
		if(minigame.getConfig().contains(name + ".sfloorpos.2")){
			setFloorDegen2(Minigames.plugin.mdata.minigameLocations(name, "sfloorpos.2", minigame.getConfig()));
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
		setType(MinigameType.valueOf(minigame.getConfig().getString(name + ".type")));
		setMinPlayers(minigame.getConfig().getInt(name + ".minplayers"));
		setMaxPlayers(minigame.getConfig().getInt(name + ".maxplayers"));
		if(minigame.getConfig().contains(name + ".spMaxPlayers"))
			setSpMaxPlayers(minigame.getConfig().getBoolean(name + ".spMaxPlayers"));
		setEnabled(minigame.getConfig().getBoolean(name + ".enabled"));
		if(minigame.getConfig().contains(name + ".maxradius")){
			setMaxRadius(minigame.getConfig().getInt(name + ".maxradius"));
		}
		if(minigame.getConfig().contains(name + ".maxheight")){
			setMaxHeight(minigame.getConfig().getInt(name + ".maxheight"));
		}
		setUsePermissions(minigame.getConfig().getBoolean(name + ".usepermissions"));
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
		if(minigame.getConfig().contains(name + ".loadout")){
			Set<String> keys = minigame.getConfig().getConfigurationSection(name + ".loadout").getKeys(false);
			for(String key : keys){
				if(key.matches("[0-9]+"))
					getDefaultPlayerLoadout().addItem(minigame.getConfig().getItemStack(name + ".loadout." + key), Integer.parseInt(key));
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
			
			if(minigame.getConfig().contains(name + ".loadout.falldamage")){
				getDefaultPlayerLoadout().setHasFallDamage(minigame.getConfig().getBoolean(name + ".loadout.falldamage"));
			}
			if(minigame.getConfig().contains(name + ".loadout.hunger")){
				getDefaultPlayerLoadout().setHasHunger(minigame.getConfig().getBoolean(name + ".loadout.hunger"));
			}
		}
		if(minigame.getConfig().contains(name + ".extraloadouts")){
			Set<String> keys = minigame.getConfig().getConfigurationSection(name + ".extraloadouts").getKeys(false);
			for(String loadout : keys){
				addLoadout(loadout);
				Set<String> items = minigame.getConfig().getConfigurationSection(name + ".extraloadouts." + loadout).getKeys(false);
				for(String key : items){
					if(key.matches("[0-9]+"))
						getLoadout(loadout).addItem(minigame.getConfig().getItemStack(name + ".extraloadouts." + loadout + "." + key), Integer.parseInt(key));
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
				
				if(minigame.getConfig().contains(name + ".extraloadouts." + loadout + ".falldamage"))
					getLoadout(loadout).setHasFallDamage(minigame.getConfig().getBoolean(name + ".extraloadouts." + loadout + ".falldamage"));
				
				if(minigame.getConfig().contains(name + ".extraloadouts." + loadout + ".hunger"))
					getLoadout(loadout).setHasHunger(minigame.getConfig().getBoolean(name + ".extraloadouts." + loadout + ".hunger"));
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
		
//		if(minigame.getConfig().contains(name + ".defaultwinner")){
//			setDefaultWinner(minigame.getConfig().getString(name + ".defaultwinner"));
//		} TODO: Fix me!
		
		if(minigame.getConfig().contains(name + ".allowEnderpearls")){
			setAllowEnderpearls(minigame.getConfig().getBoolean(name + ".allowEnderpearls"));
		}
		
		if(minigame.getConfig().contains(name + ".allowMPCheckpoints"))
			setAllowMPCheckpoints(minigame.getConfig().getBoolean(name + ".allowMPCheckpoints"));
		
		if(minigame.getConfig().contains(name + ".objective"))
			setObjective(minigame.getConfig().getString(name + ".objective"));
		
		if(minigame.getConfig().contains(name + ".gametypeName"))
			setGametypeName(minigame.getConfig().getString(name + ".gametypeName"));
		
		if(minigame.getConfig().contains(name + ".canInteractPlayerWait"))
			canInteractPlayerWait = minigame.getConfig().getBoolean(name + ".canInteractPlayerWait");
		if(minigame.getConfig().contains(name + ".canInteractStartWait"))
			canInteractStartWait = minigame.getConfig().getBoolean(name + ".canInteractStartWait");
		if(minigame.getConfig().contains(name + ".canMovePlayerWait"))
			canMovePlayerWait = minigame.getConfig().getBoolean(name + ".canMovePlayerWait");
		if(minigame.getConfig().contains(name + ".canMoveStartWait"))
			canMoveStartWait = minigame.getConfig().getBoolean(name + ".canMoveStartWait");
		if(minigame.getConfig().contains(name + ".teleportOnStart"))
			teleportOnStart = minigame.getConfig().getBoolean(name + ".teleportOnStart");
		if(minigame.getConfig().contains(name + ".teleportOnPlayerWait"))
			teleportOnPlayerWait = minigame.getConfig().getBoolean(name + ".teleportOnPlayerWait");
		if(minigame.getConfig().contains(name + ".regenDelay"))
			regenDelay = minigame.getConfig().getInt(name + ".regenDelay");

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
