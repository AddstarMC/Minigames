package au.com.mineauz.minigames.minigame;

import au.com.mineauz.minigames.*;
import au.com.mineauz.minigames.config.*;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.mechanics.GameMechanicBase;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.modules.*;
import au.com.mineauz.minigames.objects.CTFFlag;
import au.com.mineauz.minigames.objects.MgRegion;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.RegenRegionChangeResult;
import au.com.mineauz.minigames.recorder.RecorderData;
import au.com.mineauz.minigames.script.ScriptCollection;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigames.script.ScriptReference;
import au.com.mineauz.minigames.script.ScriptValue;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.StatSettings;
import au.com.mineauz.minigames.stats.StoredGameStats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Minigame implements ScriptObject {
    private final String name; //todo maybe component
    private final Map<String, Flag<?>> configFlags = new HashMap<>();
    private final ComponentFlag displayName = new ComponentFlag(null, "displayName");
    private final ComponentFlag objective = new ComponentFlag(null, "objective");
    private final ComponentFlag gameTypeName = new ComponentFlag(null, "gametypeName");
    private final EnumFlag<MinigameType> type = new EnumFlag<>(MinigameType.SINGLEPLAYER, "type");
    private final BooleanFlag enabled = new BooleanFlag(false, "enabled");
    private final IntegerFlag minPlayers = new IntegerFlag(2, "minplayers");
    private final IntegerFlag maxPlayers = new IntegerFlag(4, "maxplayers");
    private final BooleanFlag spMaxPlayers = new BooleanFlag(false, "spMaxPlayers");
    private final StrListFlag flags = new StrListFlag(null, "flags");
    private final EnumFlag<FloorDegenerator.DegeneratorType> degenType = new EnumFlag<>(FloorDegenerator.DegeneratorType.INWARD, "degentype");
    private final IntegerFlag degenRandomChance = new IntegerFlag(15, "degenrandom");
    private final RegionFlag floorDegen = new RegionFlag(null, "sfloor", "sfloorpos.1", "sfloorpos.2");
    private final TimeFlag floorDegenTime = new TimeFlag(Minigames.getPlugin().getConfig().getLong("multiplayer.floordegenerator.time"), "floordegentime");
    // Respawn Module
    private final BooleanFlag respawn = new BooleanFlag(Minigames.getPlugin().getConfig().getBoolean("has-respawn"), "respawn");
    private final LocationListFlag startLocations = new LocationListFlag(null, "startpos");
    private final BooleanFlag randomizeStart = new BooleanFlag(false, "ranndomizeStart");
    private final LocationFlag endLocation = new LocationFlag(null, "endpos");
    private final LocationFlag quitLocation = new LocationFlag(null, "quitpos");
    private final LocationFlag lobbyLocation = new LocationFlag(null, "lobbypos");
    private final LocationFlag spectatorPosition = new LocationFlag(null, "spectatorpos");
    private final BooleanFlag usePermissions = new BooleanFlag(false, "usepermissions");
    private final TimeFlag timer = new TimeFlag(0L, "timer");
    private final BooleanFlag useXPBarTimer = new BooleanFlag(true, "useXPBarTimer");
    private final TimeFlag startWaitTime = new TimeFlag(0L, "startWaitTime");
    private final BooleanFlag showCompletionTime = new BooleanFlag(false, "showCompletionTime");
    private final BooleanFlag itemDrops = new BooleanFlag(false, "itemdrops");
    private final BooleanFlag deathDrops = new BooleanFlag(false, "deathdrops");
    private final BooleanFlag itemPickup = new BooleanFlag(true, "itempickup");
    private final BooleanFlag blockBreak = new BooleanFlag(false, "blockbreak");
    private final BooleanFlag blockPlace = new BooleanFlag(false, "blockplace");
    private final EnumFlag<GameMode> defaultGamemode = new EnumFlag<>(GameMode.ADVENTURE, "gamemode");
    private final BooleanFlag blocksDrop = new BooleanFlag(true, "blocksdrop");
    private final BooleanFlag allowEnderPearls = new BooleanFlag(false, "allowEnderpearls");
    private final BooleanFlag allowMPCheckpoints = new BooleanFlag(false, "allowMPCheckpoints");
    private final BooleanFlag allowFlight = new BooleanFlag(false, "allowFlight");
    private final BooleanFlag enableFlight = new BooleanFlag(false, "enableFlight");
    private final BooleanFlag allowDragonEggTeleport = new BooleanFlag(true, "allowDragonEggTeleport");
    private final BooleanFlag usePlayerDisplayNames = new BooleanFlag(true, "usePlayerDisplayNames");
    private final BooleanFlag showPlayerBroadcasts = new BooleanFlag(true, "showPlayerBroadcasts");
    private final BooleanFlag showCTFBroadcasts = new BooleanFlag(true, "showCTFBroadcasts");
    private final BooleanFlag keepInventory = new BooleanFlag(false, "keepInventory");
    private final BooleanFlag friendlyFireSplashPotions = new BooleanFlag(true, "friendlyFireSplashPotions");
    private final BooleanFlag friendlyFireLingeringPotions = new BooleanFlag(true, "friendlyFireLingeringPotions");
    private final StringFlag mechanic = new StringFlag("custom", "scoretype");
    private final BooleanFlag paintBallMode = new BooleanFlag(false, "paintball");
    private final IntegerFlag paintBallDamage = new IntegerFlag(2, "paintballdmg");
    private final BooleanFlag unlimitedAmmo = new BooleanFlag(false, "unlimitedammo");
    private final BooleanFlag saveCheckpoints = new BooleanFlag(false, "saveCheckpoints");
    private final BooleanFlag lateJoin = new BooleanFlag(false, "latejoin");
    // just to stay backwards compatible we have to save this int as a float
    private final FloatFlag lives = new FloatFlag(0F, "lives");
    private final RegionMapFlag regenRegions = new RegionMapFlag(new HashMap<>(), "regenRegions", "regenarea.1", "regenarea.2");
    private final TimeFlag regenDelay = new TimeFlag(0L, "regenDelay");
    private final IntegerFlag maxBlocksRegenRegions = new IntegerFlag(300000, "maxBlocksRegenRegions");
    private final @NotNull Map<@NotNull String, @NotNull MinigameModule> modules = new HashMap<>();
    private final IntegerFlag minScore = new IntegerFlag(5, "minscore");
    private final IntegerFlag maxScore = new IntegerFlag(10, "maxscore");
    private final BooleanFlag displayScoreboard = new BooleanFlag(true, "displayScoreboard");
    private final BooleanFlag canSpectateFly = new BooleanFlag(false, "canspectatefly");
    private final BooleanFlag randomizeChests = new BooleanFlag(false, "randomizechests");
    private final IntegerFlag minChestRandom = new IntegerFlag(5, "minchestrandom");
    private final IntegerFlag maxChestRandom = new IntegerFlag(10, "maxchestrandom");
    @NotNull
    private final ScoreboardData sbData = new ScoreboardData();
    private final Map<MinigameStat, StatSettings> statSettings = new HashMap<>();
    //Unsaved data
    private final List<MinigamePlayer> players = new ArrayList<>();
    private final List<MinigamePlayer> spectators = new ArrayList<>();
    private final RecorderData blockRecorder = new RecorderData(this);
    //CTF
    private final Map<MinigamePlayer, CTFFlag> flagCarriers = new HashMap<>();
    private final Map<String, CTFFlag> droppedFlag = new HashMap<>();
    private MinigameState state = MinigameState.IDLE;
    private FloorDegenerator sFloorDegen;
    private Scoreboard sbManager = Minigames.getPlugin().getServer().getScoreboardManager().getNewScoreboard();
    //Multiplayer
    private MultiplayerTimer mpTimer = null;
    private MinigameTimer miniTimer = null;
    private MultiplayerBets mpBets = null;
    private boolean playersAtStart = false;

    public Minigame(@NotNull String name, @NotNull MinigameType type, @NotNull Location start) {
        this.name = name;
        setup(type, start);
    }

    public Minigame(String name) {
        this.name = name;
        setup(MinigameType.SINGLEPLAYER, null);
    }

    public boolean isPlayersAtStart() {
        return playersAtStart;
    }

    public void setPlayersAtStart(boolean playersAtStart) {
        this.playersAtStart = playersAtStart;
    }

    private void setup(@NotNull MinigameType type, @Nullable Location start) {
        this.type.setFlag(type);
        startLocations.setFlag(new ArrayList<>());

        if (start != null)
            startLocations.getFlag().add(start);
        if (sbManager != null) {
            sbManager.registerNewObjective(this.name, Criteria.DUMMY, this.name);
            sbManager.getObjective(this.name).setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        for (ModuleFactory factory : Minigames.getPlugin().getMinigameManager().getModules()) {
            addModule(factory);
        }

        flags.setFlag(new ArrayList<>());

        addConfigFlag(allowEnderPearls);
        addConfigFlag(allowFlight);
        addConfigFlag(allowMPCheckpoints);
        addConfigFlag(blockBreak);
        addConfigFlag(blockPlace);
        addConfigFlag(blocksDrop);
        addConfigFlag(canSpectateFly);
        addConfigFlag(deathDrops);
        addConfigFlag(defaultGamemode);
        addConfigFlag(degenRandomChance);
        addConfigFlag(degenType);
        addConfigFlag(displayName);
        addConfigFlag(enableFlight);
        addConfigFlag(enabled);
        addConfigFlag(endLocation);
        addConfigFlag(flags);
        addConfigFlag(floorDegen);
        addConfigFlag(floorDegenTime);
        addConfigFlag(gameTypeName);
        addConfigFlag(itemDrops);
        addConfigFlag(itemPickup);
        addConfigFlag(lateJoin);
        addConfigFlag(lives);
        addConfigFlag(lobbyLocation);
        addConfigFlag(maxChestRandom);
        addConfigFlag(maxPlayers);
        addConfigFlag(maxScore);
        addConfigFlag(minChestRandom);
        addConfigFlag(minPlayers);
        addConfigFlag(usePlayerDisplayNames);
        addConfigFlag(keepInventory);
        addConfigFlag(friendlyFireSplashPotions);
        addConfigFlag(friendlyFireLingeringPotions);
        addConfigFlag(showPlayerBroadcasts);
        addConfigFlag(showCTFBroadcasts);
        addConfigFlag(minScore);
        addConfigFlag(objective);
        addConfigFlag(paintBallDamage);
        addConfigFlag(paintBallMode);
        addConfigFlag(quitLocation);
        addConfigFlag(randomizeChests);
        addConfigFlag(regenRegions);
        addConfigFlag(regenDelay);
        addConfigFlag(maxBlocksRegenRegions);
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
        addConfigFlag(timerDisplayType);
        addConfigFlag(spectatorPosition);
        addConfigFlag(displayScoreboard);
        addConfigFlag(allowDragonEggTeleport);
        addConfigFlag(showCompletionTime);
    }

    public MinigameState getState() {
        return state;
    }

    public void setState(MinigameState state) {
        this.state = state;
    }

    private void addConfigFlag(Flag<?> flag) {
        configFlags.put(flag.getName(), flag);
    }

    public Flag<?> getConfigFlag(String name) {
        return configFlags.get(name);
    }

    /**
     * returns the old module registed with the same name or null if there wasn't one.
     *
     * @param factory
     * @return
     */
    public @Nullable MinigameModule addModule(@NotNull ModuleFactory factory) {
        return modules.put(factory.getName(), factory.makeNewModule(this));
    }

    public void removeModule(String moduleName) {
        modules.remove(moduleName);
    }

    public List<MinigameModule> getModules() {
        return new ArrayList<>(modules.values());
    }

    /**
     * Please use the Modules getMinigameModule() methode whenever possible - simply because its less error-prone.
     *
     * @param name
     * @return
     */
    public @Nullable MinigameModule getModule(@NotNull String name) {
        return modules.get(name);
    }

    public boolean isTeamGame() {
        TeamsModule teamsModule = TeamsModule.getMinigameModule(this);
        return getType() == MinigameType.MULTIPLAYER && teamsModule != null && !teamsModule.getTeams().isEmpty();
    }

    public boolean hasFlags() {
        return !flags.getFlag().isEmpty();
    }

    public void addFlag(String flag) {
        flags.getFlag().add(flag);
    }

    public List<String> getFlags() {
        return flags.getFlag();
    }

    public void setFlags(List<String> flags) {
        this.flags.setFlag(flags);
    }

    public boolean removeFlag(String flag) {
        if (flags.getFlag().contains(flag)) {
            flags.getFlag().remove(flag);
            return true;
        }
        return false;
    }

    public void setStartLocation(Location loc) {
        if (startLocations.getFlag().isEmpty()) {
            startLocations.getFlag().add(loc);
        } else {
            startLocations.getFlag().set(0, loc);
        }
    }

    public void addStartLocation(Location loc) {
        startLocations.getFlag().add(loc);
    }

    public void addStartLocation(Location loc, int number) {
        if (startLocations.getFlag().size() >= number) {
            startLocations.getFlag().set(number - 1, loc);
        } else {
            startLocations.getFlag().add(loc);
        }
    }

    public List<Location> getStartLocations() {
        return startLocations.getFlag();
    }

    public boolean removeStartLocation(int locNumber) {
        if (startLocations.getFlag().size() < locNumber) {
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

    public Location getSpectatorLocation() {
        return spectatorPosition.getFlag();
    }

    public void setSpectatorLocation(Location loc) {
        spectatorPosition.setFlag(loc);
    }

    public boolean isEnabled() {
        return enabled.getFlag();
    }

    public void setEnabled(boolean enabled) {
        this.enabled.setFlag(enabled);
    }

    public int getMinPlayers() {
        return minPlayers.getFlag();
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers.setFlag(minPlayers);
    }

    public boolean usePlayerDisplayNames() {
        return usePlayerDisplayNames.getFlag();
    }

    public void setUsePlayerDisplayNames(Boolean value) {
        usePlayerDisplayNames.setFlag(value);
    }

    public boolean keepInventory() {
        return keepInventory.getFlag();
    }

    public void setKeepInventory(boolean value) {
        keepInventory.setFlag(value);
    }

    public boolean friendlyFireSplashPotions() {
        return friendlyFireSplashPotions.getFlag();
    }

    public void setFriendlyFireSplashPotions(boolean value) {
        friendlyFireSplashPotions.setFlag(value);
    }

    public boolean friendlyFireLingeringPotions() {
        return friendlyFireLingeringPotions.getFlag();
    }

    public void setFriendlyFireLingeringPotions(boolean value) {
        friendlyFireLingeringPotions.setFlag(value);
    }

    public int getMaxPlayers() {
        return maxPlayers.getFlag();
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers.setFlag(maxPlayers);
    }

    public boolean isSpMaxPlayers() {
        return spMaxPlayers.getFlag();
    }

    public void setSpMaxPlayers(boolean spMaxPlayers) {
        this.spMaxPlayers.setFlag(spMaxPlayers);
    }

    public boolean isGameFull() {
        if ((getType() == MinigameType.SINGLEPLAYER && isSpMaxPlayers()) ||
                getType() == MinigameType.MULTIPLAYER) {
            return getPlayers().size() >= getMaxPlayers();
        }
        return false;
    }

    public @Nullable MgRegion getFloorDegen() {
        return floorDegen.getFlag();
    }

    public void setFloorDegen(@Nullable MgRegion region) {
        floorDegen.setFlag(region);
    }

    public void removeFloorDegen() {
        floorDegen.setFlag(null);
    }

    public FloorDegenerator.DegeneratorType getDegenType() {
        return degenType.getFlag();
    }

    public void setDegenType(FloorDegenerator.DegeneratorType degenType) {
        this.degenType.setFlag(degenType);
    }

    public int getDegenRandomChance() {
        return degenRandomChance.getFlag();
    }

    public void setDegenRandomChance(int degenRandomChance) {
        this.degenRandomChance.setFlag(degenRandomChance);
    }

    public @Nullable Location getEndLocation() {
        return endLocation.getFlag();
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation.setFlag(endLocation);
    }

    public @Nullable Location getQuitLocation() {
        return quitLocation.getFlag();
    }

    public void setQuitLocation(Location quitLocation) {
        this.quitLocation.setFlag(quitLocation);
    }

    public @Nullable Location getLobbyLocation() {
        return lobbyLocation.getFlag();
    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation.setFlag(lobbyLocation);
    }

    public String getName() {
        return name;
    }

    public Component getDisplayName() {
        if (displayName.getFlag() != null) {
            return displayName.getFlag();
        }
        return Component.text(name);
    }

    public void setDisplayName(Component displayName) {
        this.displayName.setFlag(displayName);
    }

    public void setshowPlayerBroadcasts(Boolean showPlayerBroadcasts) {
        this.showPlayerBroadcasts.setFlag(showPlayerBroadcasts);
    }

    public Boolean getShowPlayerBroadcasts() {
        return showPlayerBroadcasts.getFlag();
    }

    public Boolean getShowCTFBroadcasts() {
        return showCTFBroadcasts.getFlag();
    }

    public void setShowCTFBroadcasts(Boolean showCTFBroadcasts) {
        this.showCTFBroadcasts.setFlag(showCTFBroadcasts);
    }

    public @NotNull MinigameType getType() {
        return type.getFlag();
    }

    public void setType(@NotNull MinigameType type) {
        this.type.setFlag(type);
    }

    public MultiplayerTimer getMpTimer() {
        return mpTimer;
    }

    public void setMpTimer(MultiplayerTimer mpTimer) {
        this.mpTimer = mpTimer;
    }

    @Deprecated
    public boolean isNotWaitingForPlayers() {
        return getState() != MinigameState.WAITING;
    }

    public boolean isWaitingForPlayers() {
        return getState() == MinigameState.WAITING;
    }

    public boolean hasStarted() {
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

    public boolean getUsePermissions() {
        return usePermissions.getFlag();
    }

    public void setUsePermissions(boolean usePermissions) {
        this.usePermissions.setFlag(usePermissions);
    }

    public List<MinigamePlayer> getPlayers() {
        return players;
    }

    public void addPlayer(MinigamePlayer player) {
        players.add(player);
    }

    public void removePlayer(MinigamePlayer player) {
        players.remove(player);
    }

    public boolean hasPlayers() {
        return !players.isEmpty();
    }

    public boolean hasSpectators() {
        return !spectators.isEmpty();
    }

    public List<MinigamePlayer> getSpectators() {
        return spectators;
    }

    public void addSpectator(MinigamePlayer player) {
        spectators.add(player);
    }

    public void removeSpectator(MinigamePlayer player) {
        spectators.remove(player);
    }

    public boolean isSpectator(MinigamePlayer player) {
        return spectators.contains(player);
    }

    public void setScore(@NotNull MinigamePlayer mgPlayer, int amount) {
        if (sbManager == null) {
            ScoreboardManager s = Minigames.getPlugin().getServer().getScoreboardManager();
            sbManager = s.getNewScoreboard();
            Minigames.getCmpnntLogger().info("ScoreBoardManager was null - Created new Scoreboard - for:" + name);
        }
        Objective o = sbManager.getObjective(getName());
        if (o != null) {
            o.getScore(mgPlayer.getName()).setScore(amount);
        }
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

    public int getMaxScorePerPlayer() {
        float scorePerPlayer = (float) getMaxScore() / getMaxPlayers();
        int score = Math.round(scorePerPlayer * getPlayers().size());
        if (score < minScore.getFlag()) {
            score = minScore.getFlag();
        }
        return score;
    }

    public FloorDegenerator getFloorDegenerator() {
        return sFloorDegen;
    }

    public void addFloorDegenerator() {
        sFloorDegen = new FloorDegenerator(floorDegen.getFlag(), this);
    }

    public long getTimer() {
        return timer.getFlag();
    }

    public void setTimer(long time) {
        timer.setFlag(time);
    }

    public @NotNull MinigameTimer.DisplayType getTimerDisplayType() {
        return timerDisplayType.getFlag();
    }

    public void setTimerDisplayType(@NotNull MinigameTimer.DisplayType type) {
        this.timerDisplayType.setFlag(type);
    }

    public long getStartWaitTime() {
        return startWaitTime.getFlag();
    }

    public void setStartWaitTime(long startWaitTime) {
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

    /**
     * get the recorder data holder of this minigame.
     * This holds all block and entity changes recorded while the minigame was running.
     */
    public @NotNull RecorderData getRecorderData() {
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

    public @NotNull GameMode getDefaultGamemode() {
        return defaultGamemode.getFlag();
    }

    public void setDefaultGamemode(@NotNull GameMode defaultGamemode) {
        this.defaultGamemode.setFlag(defaultGamemode);
    }

    public boolean canBlocksdrop() {
        return blocksDrop.getFlag();
    }

    public void setBlocksDrop(boolean blocksDrop) {
        this.blocksDrop.setFlag(blocksDrop);
    }

    public String getMechanicName() {
        return mechanic.getFlag();
    }

    public GameMechanicBase getMechanic() {
        return GameMechanics.getGameMechanic(mechanic.getFlag());
    }

    public void setMechanic(@NotNull GameMechanicBase gameMechanicBase) {
        this.mechanic.setFlag(gameMechanicBase.getMechanicName());
    }

    public boolean isFlagCarrier(@Nullable MinigamePlayer mgPlayer) {
        return flagCarriers.containsKey(mgPlayer);
    }

    public void addFlagCarrier(@NotNull MinigamePlayer mgPlayer, @NotNull CTFFlag flag) {
        flagCarriers.put(mgPlayer, flag);
    }

    public void removeFlagCarrier(@NotNull MinigamePlayer mgPlayer) {
        flagCarriers.remove(mgPlayer);
    }

    public @Nullable CTFFlag getFlagCarrier(@NotNull MinigamePlayer mgPlayer) {
        return flagCarriers.get(mgPlayer);
    }

    public void resetFlags() {
        for (MinigamePlayer mgPlayer : flagCarriers.keySet()) {
            getFlagCarrier(mgPlayer).respawnFlag();
            getFlagCarrier(mgPlayer).stopCarrierParticleEffect();
        }
        flagCarriers.clear();
        for (String id : droppedFlag.keySet()) {
            if (!getDroppedFlag(id).isAtHome()) {
                getDroppedFlag(id).stopTimer();
                getDroppedFlag(id).respawnFlag();
            }
        }
        droppedFlag.clear();
    }

    public boolean hasDroppedFlag(String id) {
        return droppedFlag.containsKey(id);
    }

    public void addDroppedFlag(String id, CTFFlag flag) {
        droppedFlag.put(id, flag);
    }

    public void removeDroppedFlag(String id) {
        droppedFlag.remove(id);
    }

    public CTFFlag getDroppedFlag(String id) {
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
        this.paintBallDamage.setFlag(paintBallDamage);
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

    public int getMinChestRandom() {
        return minChestRandom.getFlag();
    }

    /**
     * @param minChestRandom
     * @param maxChestRandom
     * @return true whenever the parameters where valid and randomizing chests is enabled (true) or not (false)
     */
    public boolean setChestRandoms(int minChestRandom, int maxChestRandom) {
        int min;
        int max;
        boolean returnValue;
        if (minChestRandom >= 0 && maxChestRandom > 0) {
            this.randomizeChests.setFlag(true);

            min = Math.min(minChestRandom, maxChestRandom);
            max = Math.max(minChestRandom, maxChestRandom);
            returnValue = true;
        } else { // bounds are not meet. disable random chests
            this.randomizeChests.setFlag(false);

            min = minChestRandom;
            max = maxChestRandom;
            returnValue = false;
        }

        this.minChestRandom.setFlag(min);
        this.maxChestRandom.setFlag(max);

        return returnValue;
    }

    public int getMaxChestRandom() {
        return maxChestRandom.getFlag();
    }

    public Collection<MgRegion> getRegenRegions() {
        return regenRegions.getFlag().values();
    }

    public MgRegion getRegenRegion(String name) {
        return regenRegions.getFlag().get(name);
    }

    public RegenRegionChangeResult removeRegenRegion(String name) {
        boolean removed = regenRegions.getFlag().remove(name) != null;

        long numOfBlocksTotal = 0;
        for (MgRegion region : regenRegions.getFlag().values()) {
            numOfBlocksTotal += (long) Math.ceil(region.getVolume());
        }

        return new RegenRegionChangeResult(removed, numOfBlocksTotal);
    }

    /**
     * checks if the limit of all regen regions together,
     * if we are still under it, add the new region to the list
     * Please note: The regions are name unique,
     * setting a new one with a name that already exists, it will overwrite the old one.
     *
     * @param newRegenRegion new regeneration region.
     * @return a record containing whenever this was a success or not
     * and the total number of all blocks in regen regions after the setting would happen
     */
    public RegenRegionChangeResult setRegenRegion(MgRegion newRegenRegion) {
        long numOfBlocksTotal = (long) Math.ceil(newRegenRegion.getVolume());

        for (MgRegion region : regenRegions.getFlag().values()) {
            numOfBlocksTotal += (long) Math.ceil(region.getVolume());
        }

        if (numOfBlocksTotal <= maxBlocksRegenRegions.getFlag()) {
            regenRegions.getFlag().put(newRegenRegion.getName(), newRegenRegion);
            return new RegenRegionChangeResult(true, numOfBlocksTotal);
        } else {
            return new RegenRegionChangeResult(false, numOfBlocksTotal);
        }
    }

    public long getRegenBlocklimit() {
        return maxBlocksRegenRegions.getFlag();
    }

    public boolean hasRegenArea() {
        return !regenRegions.getFlag().isEmpty();
    }

    public boolean isInRegenArea(Location location) {
        for (MgRegion region : regenRegions.getFlag().values()) {
            if (region.isInRegen(location)) {
                return true;
            }
        }

        return false;
    }

    public long getRegenDelay() {
        return regenDelay.getFlag();
    }

    public void setRegenDelay(long regenDelay) {
        if (regenDelay < 0) {
            regenDelay = 0;
        }
        this.regenDelay.setFlag(regenDelay);
    }

    public int getLives() {
        return lives.getFlag().intValue();
    }

    public void setLives(int lives) {
        this.lives.setFlag((float) lives);
    }

    public long getFloorDegenTime() {
        return floorDegenTime.getFlag();
    }

    public void setFloorDegenTime(long floorDegenTime) {
        this.floorDegenTime.setFlag(floorDegenTime);
    }

    public boolean isAllowedEnderpearls() {
        return allowEnderPearls.getFlag();
    }

    public void setAllowEnderPearls(boolean allowEnderPearls) {
        this.allowEnderPearls.setFlag(allowEnderPearls);
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

    public Scoreboard getScoreboardManager() {
        return sbManager;
    }

    public Component getObjective() {
        return objective.getFlag();
    }

    public void setObjective(Component objective) {
        this.objective.setFlag(objective);
    }

    public @Nullable Component getGameTypeName() {
        return gameTypeName.getFlag();
    }

    public void setGameTypeName(@Nullable Component gameTypeName) {
        this.gameTypeName.setFlag(gameTypeName);
    }

    public boolean canDisplayScoreboard() {
        return displayScoreboard.getFlag();
    }

    public void setDisplayScoreboard(boolean bool) {
        displayScoreboard.setFlag(bool);
    }

    public boolean allowDragonEggTeleport() {
        return allowDragonEggTeleport.getFlag();
    }

    public void setAllowDragonEggTeleport(boolean allow) {
        allowDragonEggTeleport.setFlag(allow);
    }

    public boolean getShowCompletionTime() {
        return showCompletionTime.getFlag();
    }

    public void setShowCompletionTime(boolean bool) {
        showCompletionTime.setFlag(bool);
    }

    public StatSettings getSettings(MinigameStat stat) {
        return statSettings.computeIfAbsent(stat, StatSettings::new);
    }

    public Map<MinigameStat, StatSettings> getStatSettings(StoredGameStats stats) {
        Map<MinigameStat, StatSettings> settings = new HashMap<>();

        for (MinigameStat stat : stats.getStats().keySet()) {
            settings.put(stat, getSettings(stat));
        }

        return settings;
    }

    private record TypeDependentDisplayData(@NotNull MenuItem menuItem,
                                            @NotNull List<@NotNull MinigameType> applicableTypes, int slot) {
    }

    public void displayMenu(MinigamePlayer player) {
        Menu mainMenu = new Menu(6, getDisplayName(), player);
        Menu playerMenu = new Menu(6, getDisplayName(), player);
        Menu loadouts = new Menu(6, getDisplayName(), player);
        Menu flags = new Menu(6, getDisplayName(), player);
        Menu lobby = new Menu(6, getDisplayName(), player);

        mainMenu.addItem(enabled.getMenuItem(Material.PAPER, MgMenuLangKey.MENU_MINIGAME_ENABLED_NAME), 0);
        mainMenu.addItem(usePermissions.getMenuItem(Material.PAPER, MgMenuLangKey.MENU_MINIGAME_USEPERNS_NAME), 1);

        List<TypeDependentDisplayData> typeDependentDisplayData = new ArrayList<>();
        mainMenu.addItem(new MenuItemEnum<>(Material.PAPER, MgMenuLangKey.MENU_MINIGAME_TYPE_NAME, new Callback<>() {

            @Override
            public MinigameType getValue() {
                return type.getFlag();
            }

            @Override
            public void setValue(MinigameType value) {
                type.setFlag(value);

                for (TypeDependentDisplayData data : typeDependentDisplayData) {
                    if (!data.applicableTypes.contains(value)) {
                        mainMenu.removeItem(data.slot);
                    } else {
                        mainMenu.addItem(data.menuItem, data.slot);
                    }
                }
            }
        }, MinigameType.class), 2);

        List<String> scoreTypes = new ArrayList<>();
        for (GameMechanicBase val : GameMechanics.getGameMechanics()) {
            scoreTypes.add(WordUtils.capitalizeFully(val.getMechanicName()));
        }
        final MenuItemList<String> mechanicMenuItem = new MenuItemList<>(Material.ROTTEN_FLESH,
                MgMenuLangKey.MENU_MINIGAME_MECHANIC_NAME, new Callback<>() {

            @Override
            public String getValue() {
                return WordUtils.capitalizeFully(mechanic.getFlag());
            }

            @Override
            public void setValue(String value) {
                mechanic.setFlag(value.toLowerCase());
            }
        }, scoreTypes);
        typeDependentDisplayData.add(new TypeDependentDisplayData(mechanicMenuItem, List.of(MinigameType.MULTIPLAYER), 3));
        if (type.getFlag() == MinigameType.MULTIPLAYER) {
            mainMenu.addItem(mechanicMenuItem, 3);
        }

        final MenuItemCustom mechSettings = new MenuItemCustom(Material.PAPER, MgMenuLangKey.MENU_MINIGAME_MECHANIC_SETTINGS_NAME);
        final Minigame mgm = this;
        final Menu fmain = mainMenu;
        mechSettings.setClick(() -> {
            if (getMechanic().displaySettings(mgm) != null &&
                    getMechanic().displaySettings(mgm).displayMechanicSettings(fmain))
                return null;
            return mechSettings.getDisplayItem();
        });
        typeDependentDisplayData.add(new TypeDependentDisplayData(mechSettings, List.of(MinigameType.MULTIPLAYER), 4));
        if (type.getFlag() == MinigameType.MULTIPLAYER) {
            mainMenu.addItem(mechSettings, 4);
        }

        MenuItemComponent cmpntItem = (MenuItemComponent) objective.getMenuItem(Material.DIAMOND,
                MgMenuLangKey.MENU_MINIGAME_OBJECTIVEDESCRIPTION_NAME);
        cmpntItem.setAllowNull(true);
        mainMenu.addItem(cmpntItem, 5);

        cmpntItem = (MenuItemComponent) gameTypeName.getMenuItem(Material.WRITTEN_BOOK, MgMenuLangKey.MENU_MINIGAME_TYPEDESCRIPTION_NAME);
        cmpntItem.setAllowNull(true);
        mainMenu.addItem(cmpntItem, 6);

        cmpntItem = (MenuItemComponent) displayName.getMenuItem(Material.OAK_SIGN, MgMenuLangKey.MENU_DISPLAYNAME_NAME);
        cmpntItem.setAllowNull(true);
        mainMenu.addItem(cmpntItem, 7);

        mainMenu.addItem(new MenuItemNewLine(), 8);

        final MenuItem scoreMinMenuItem = minScore.getMenuItem(Material.STONE_SLAB, MgMenuLangKey.MENU_MINIGAME_SCORE_MIN_NAME);
        typeDependentDisplayData.add(new TypeDependentDisplayData(scoreMinMenuItem, List.of(MinigameType.MULTIPLAYER), 9));
        if (type.getFlag() == MinigameType.MULTIPLAYER) {
            mainMenu.addItem(scoreMinMenuItem, 9);
        }

        final MenuItem scoreMaxMenuItem = maxScore.getMenuItem(Material.STONE, MgMenuLangKey.MENU_MINIGAME_SCORE_MAX_NAME);
        typeDependentDisplayData.add(new TypeDependentDisplayData(scoreMaxMenuItem, List.of(MinigameType.MULTIPLAYER), 10));
        if (type.getFlag() == MinigameType.MULTIPLAYER) {
            mainMenu.addItem(scoreMaxMenuItem, 10);
        }

        final MenuItem minPlayersMenuItem = minPlayers.getMenuItem(Material.STONE_SLAB, MgMenuLangKey.MENU_MINIGAME_PLAYERS_MIN_NAME);
        typeDependentDisplayData.add(new TypeDependentDisplayData(minPlayersMenuItem, List.of(MinigameType.MULTIPLAYER), 11));
        if (type.getFlag() == MinigameType.MULTIPLAYER) {
            mainMenu.addItem(minPlayersMenuItem, 11);
        }

        final MenuItem maxPlayersMenuItem = maxPlayers.getMenuItem(Material.STONE, MgMenuLangKey.MENU_MINIGAME_PLAYERS_MAX_NAME);
        typeDependentDisplayData.add(new TypeDependentDisplayData(maxPlayersMenuItem, List.of(MinigameType.MULTIPLAYER), 12));
        if (type.getFlag() == MinigameType.MULTIPLAYER) {
            mainMenu.addItem(maxPlayersMenuItem, 12);
        }

        final MenuItemBoolean SinglePlayerAmountCappedMenuItem = spMaxPlayers.getMenuItem(Material.IRON_BARS,
                MgMenuLangKey.MENU_MINIGAME_PLAYERS_SINGLEPLAYER_CAPPED_NAME);
        typeDependentDisplayData.add(new TypeDependentDisplayData(maxPlayersMenuItem, List.of(MinigameType.SINGLEPLAYER), 13));
        if (type.getFlag() == MinigameType.SINGLEPLAYER) {
            mainMenu.addItem(SinglePlayerAmountCappedMenuItem, 13);
        }

        mainMenu.addItem(displayScoreboard.getMenuItem(Material.OAK_SIGN, MgMenuLangKey.MENU_MINIGAME_SCOREBOARD_DISPLAY_NAME), 14);

        final MenuItemPage lobbySettingsMenuItemPage = new MenuItemPage(Material.OAK_DOOR, MgMenuLangKey.MENU_MINIGAME_LOBBY_SETTINGS_NAME, lobby);
        typeDependentDisplayData.add(new TypeDependentDisplayData(lobbySettingsMenuItemPage, List.of(MinigameType.MULTIPLAYER), 14));
        if (type.getFlag() == MinigameType.MULTIPLAYER) {
            mainMenu.addItem(lobbySettingsMenuItemPage, 14);
        }

        mainMenu.addItem(new MenuItemNewLine(), 15);

        final MenuItemTime gamLengthMenuItem = timer.getMenuItem(Material.CLOCK, MgMenuLangKey.MENU_MINIGAME_TIME_GAMELENGTH_NAME, 0L, null);
        typeDependentDisplayData.add(new TypeDependentDisplayData(gamLengthMenuItem, List.of(MinigameType.MULTIPLAYER), 18));
        if (type.getFlag() == MinigameType.MULTIPLAYER) {
            mainMenu.addItem(gamLengthMenuItem, 18);
        }

        mainMenu.addItem(useXPBarTimer.getMenuItem(Material.ENDER_PEARL, "Use XP bar as Timer"), 19); //todo fixed in later commit

        final MenuItemTime startWaitTimeMenuItem = startWaitTime.getMenuItem(Material.CLOCK, MgMenuLangKey.MENU_MINIGAME_TIME_STARTWAIT_NAME, 3L, null);
        typeDependentDisplayData.add(new TypeDependentDisplayData(startWaitTimeMenuItem, List.of(MinigameType.MULTIPLAYER), 19));
        if (type.getFlag() == MinigameType.MULTIPLAYER) {
            mainMenu.addItem(startWaitTimeMenuItem, 19);
        }

        mainMenu.addItem(showCompletionTime.getMenuItem(Material.PAPER, MgMenuLangKey.MENU_MINIGAME_TIME_SHOWCOMPLETION_NAME), 20);

        final MenuItem allowLateJoinMenuItem = lateJoin.getMenuItem(Material.DEAD_BUSH, MgMenuLangKey.MENU_MINIGAME_ALLOWLATEJOIN_NAME);
        typeDependentDisplayData.add(new TypeDependentDisplayData(allowLateJoinMenuItem, List.of(MinigameType.MULTIPLAYER), 21));
        if (type.getFlag() == MinigameType.MULTIPLAYER) {
            mainMenu.addItem(allowLateJoinMenuItem, 21);
        }

        mainMenu.addItem(randomizeStart.getMenuItem(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, MgMenuLangKey.MENU_MINIGAME_STARTPOINT_RANDOMIZE_NAME,
                MgMenuLangKey.MENU_MINIGAME_STARTPOINT_RANDOMIZE_DESCRIPTION), 22);

        mainMenu.addItem(new MenuItemDisplayWhitelist(Material.CHEST,
                MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_MINIGAME_WHITELIST_BLOCK_NAME), // Block Whitelist/Blacklist
                MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_MINIGAME_WHITELIST_BLOCK_DESCRIPTION_MAIN),
                getRecorderData().getWBBlocks(), getRecorderData().getWhitelistModeCallback(),
                MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_MINIGAME_WHITELIST_BLOCK_DESCRIPTION_SECOND)), 23);

        mainMenu.addItem(new MenuItemNewLine(), 24);

        // double pack, since the type shows / hides random chance percent
        final MenuItemInteger randomFloorDegenChanceMenuItem = degenRandomChance.getMenuItem(Material.SNOW,
                MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_MINIGAME_DEGEN_RANDOMCHANCE_NAME),
                MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_MINIGAME_DEGEN_RANDOMCHANCE_DESCRIPTION), 1, 100);
        mainMenu.addItem(new MenuItemList<>(Material.SNOW_BLOCK, MgMenuLangKey.MENU_MINIGAME_DEGEN_TYPE_NAME,
                MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_MINIGAME_DEGEN_TYPE_DESCRIPTION), new Callback<>() {

            @Override
            public FloorDegenerator.DegeneratorType getValue() {
                return degenType.getFlag();
            }

            @Override
            public void setValue(FloorDegenerator.DegeneratorType value) {
                degenType.setFlag(value);

                if (value == FloorDegenerator.DegeneratorType.RANDOM) {
                    mainMenu.addItem(randomFloorDegenChanceMenuItem, 28);
                } else {
                    mainMenu.removeItem(28);
                }
            }

        }, List.of(FloorDegenerator.DegeneratorType.values())), 27);
        if (degenType.getFlag() == FloorDegenerator.DegeneratorType.RANDOM) {
            mainMenu.addItem(randomFloorDegenChanceMenuItem, 28);
        }

        mainMenu.addItem(floorDegenTime.getMenuItem(Material.CLOCK, MgMenuLangKey.MENU_MINIGAME_DEGEN_DELAY_NAME, 1L, null));


        mainMenu.addItem(regenDelay.getMenuItem(Material.CLOCK, MgMenuLangKey.MENU_MINIGAME_REGENDELAY_NAME,
                MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_MINIGAME_REGENDELAY_DESCRIPTION), 0L, null));

        mainMenu.addItem(new MenuItemNewLine());

        mainMenu.addItem(new MenuItemPage(Material.SKELETON_SKULL, MgMenuLangKey.MENU_PLAYERSETTINGS_NAME, playerMenu));

//        List<String> thDes = new ArrayList<>();
//        thDes.add("Treasure hunt related<newline>settings.");
//        itemsMain.add(new MenuItemPage(Material.CHEST, "Treasure Hunt Settings", thDes, treasureHunt));
//        MenuItemDisplayLoadout defLoad = new MenuItemDisplayLoadout("Default Loadout", Material.DIAMOND_SWORD, LoadoutModule.getMinigameModule(this).getDefaultPlayerLoadout(), this);
//        defLoad.setAllowDelete(false);
//        itemsMain.add(defLoad);

        mainMenu.addItem(new MenuItemPage(Material.CHEST, MgMenuLangKey.MENU_MINIGAME_LOADOUTS_NAME, loadouts));

        mainMenu.addItem(canSpectateFly.getMenuItem(Material.FEATHER, MgMenuLangKey.MENU_MINIGAME_ALLOWSPECTATORFLY_NAME));

        mainMenu.addItem(randomizeChests.getMenuItem(Material.CHEST, MgMenuLangKey.MENU_MINIGAME_RANDOMCHESTS_NAME,
                MgMenuLangKey.MENU_MINIGAME_RANDOMCHESTS_DESCRIPTION));

        mainMenu.addItem(minChestRandom.getMenuItem(Material.OAK_STAIRS, MgMenuLangKey.MENU_MINIGAME_RANDOMCHESTS_MIN_NAME,
                MgMenuLangKey.MENU_MINIGAME_RANDOMCHESTS_MIN_DESCRIPTION, 0, null));

        mainMenu.addItem(maxChestRandom.getMenuItem(Material.STONE, MgMenuLangKey.MENU_MINIGAME_RANDOMCHESTS_MAX_NAME,
                MgMenuLangKey.MENU_MINIGAME_RANDOMCHESTS_MAX_DESCRIPTION, 0, null));

        mainMenu.addItem(new MenuItemStatisticsSettings(Material.WRITABLE_BOOK, MgMenuLangKey.MENU_MINIGAME_STATISTIC_NAME, this));

        mainMenu.addItem(new MenuItemNewLine());

        mainMenu.addItem(new MenuItemSaveMinigame(MenuUtility.getSaveMaterial(),
                MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_MINIGAME_SAVE_NAME,
                        Placeholder.component(MinigamePlaceHolderKey.MINIGAME.getKey(), getDisplayName())),
                this), mainMenu.getSize() - 1);

        //--------------//
        //Loadout Settings
        //--------------//
        List<MenuItem> mi = new ArrayList<>();

        LoadoutModule loadoutModule = LoadoutModule.getMinigameModule(this);
        if (loadoutModule != null) {

            for (PlayerLoadout playerLoadout : loadoutModule.getLoadouts()) {
                Material material = Material.GLASS_PANE;

                if (!playerLoadout.getItemSlots().isEmpty()) {
                    material = playerLoadout.getItem((Integer) playerLoadout.getItemSlots().toArray()[0]).getType();
                }
                if (playerLoadout.isDeleteable()) {
                    mi.add(new MenuItemDisplayLoadout(material, playerLoadout.getDisplayName(),
                            MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_DELETE_SHIFTRIGHTCLICK), playerLoadout, this));
                } else {
                    mi.add(new MenuItemDisplayLoadout(material, playerLoadout.getDisplayName(), playerLoadout, this));
                }
            }

            loadouts.addItem(new MenuItemLoadoutAdd(MenuUtility.getCreateMaterial(), MgMenuLangKey.MENU_LOADOUT_ADD_NAME,
                    loadoutModule.getLoadoutMap(), this), 53);
            loadouts.addItem(new MenuItemBack(mainMenu), loadouts.getSize() - 9);
            loadouts.addItems(mi);
        }

        //----------------------//
        //Minigame Player Settings
        //----------------------//
        List<MenuItem> itemsPlayer = new ArrayList<>(20);
        itemsPlayer.add(defaultGamemode.getMenuItem(Material.CRAFTING_TABLE, MgMenuLangKey.MENU_PLAYERSETTINGS_GAMEMODE_NAME));
        itemsPlayer.add(allowEnderPearls.getMenuItem(Material.ENDER_PEARL, MgMenuLangKey.MENU_PLAYERSETTINGS_ENDERPERLS_NAME));
        itemsPlayer.add(itemDrops.getMenuItem(Material.DIAMOND_SWORD, MgMenuLangKey.MENU_PLAYERSETTINGS_DROP_ITEM_NAME));
        itemsPlayer.add(deathDrops.getMenuItem(Material.SKELETON_SKULL, MgMenuLangKey.MENU_PLAYERSETTINGS_DROP_DEATH_NAME));
        itemsPlayer.add(itemPickup.getMenuItem(Material.DIAMOND, MgMenuLangKey.MENU_PLAYERSETTINGS_ITEMPICKUP_NAME));
        itemsPlayer.add(blockBreak.getMenuItem(Material.DIAMOND_PICKAXE, MgMenuLangKey.MENU_PLAYERSETTINGS_BLOCK_BREAK_NAME));
        itemsPlayer.add(blockPlace.getMenuItem(Material.STONE, MgMenuLangKey.MENU_PLAYERSETTINGS_BLOCK_PLACE_NAME));
        itemsPlayer.add(blocksDrop.getMenuItem(Material.COBBLESTONE, MgMenuLangKey.MENU_PLAYERSETTINGS_BLOCK_DROPS_NAME));
        itemsPlayer.add(lives.getMenuItem(Material.APPLE, MgMenuLangKey.MENU_PLAYERSETTINGS_LIVES_NAME));
        itemsPlayer.add(paintBallMode.getMenuItem(Material.SNOWBALL, MgMenuLangKey.MENU_PLAYERSETTINGS_PAINTBALL_MODE_NAME));
        itemsPlayer.add(paintBallDamage.getMenuItem(Material.ARROW, MgMenuLangKey.MENU_PLAYERSETTINGS_PAINTBALL_DAMAGE_NAME, 1, null));
        itemsPlayer.add(unlimitedAmmo.getMenuItem(Material.SNOW_BLOCK, MgMenuLangKey.MENU_PLAYERSETTINGS_UNLIMITEDAMMO_NAME));
        itemsPlayer.add(allowMPCheckpoints.getMenuItem(Material.OAK_SIGN, MgMenuLangKey.MENU_PLAYERSETTINGS_CHECKPOINT_MULTIPLAYER_NAME,
                MgMenuLangKey.MENU_MINIGAME_MULTIPLAYERONLY_DESCRIPTION)); // todo hide if not multiplayer
        itemsPlayer.add(saveCheckpoints.getMenuItem(Material.OAK_SIGN, MgMenuLangKey.MENU_PLAYERSETTINGS_CHECKPOINT_SAVE_NAME,
                MgMenuLangKey.MENU_MINIGAME_SINGLEPLAYERONLY_DESCRIPTION)); // todo hide if not SinglePlayer
        itemsPlayer.add(new MenuItemPage(Material.OAK_SIGN, MgMenuLangKey.MENU_PLAYERSETTINGS_SINGLEPLAYERFLAG_NAME,
                MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_MINIGAME_SINGLEPLAYERONLY_DESCRIPTION), flags)); // todo hide if not SinglePlayer
        itemsPlayer.add(allowFlight.getMenuItem(Material.FEATHER, MgMenuLangKey.MENU_PLAYERSETTINGS_FLIGHT_ALLOW_NAME,
                MgMenuLangKey.MENU_PLAYERSETTINGS_FLIGHT_ALLOW_DESCRIPTION));
        itemsPlayer.add(enableFlight.getMenuItem(Material.FEATHER, MgMenuLangKey.MENU_PLAYERSETTINGS_FLIGHT_ENABLE_NAME,
                MgMenuLangKey.MENU_PLAYERSETTINGS_FLIGHT_ENABLE_DESCRIPTION));
        itemsPlayer.add(allowDragonEggTeleport.getMenuItem(Material.DRAGON_EGG, MgMenuLangKey.MENU_PLAYERSETTINGS_DRAGONEGGTELEPORT_NAME));
        itemsPlayer.add(usePlayerDisplayNames.getMenuItem(Material.POTATO, MgMenuLangKey.MENU_PLAYERSETTINGS_DISPLAYNAMES_NAME,
                MgMenuLangKey.MENU_PLAYERSETTINGS_DISPLAYNAMES_DESCRIPTION));
        itemsPlayer.add(showPlayerBroadcasts.getMenuItem(Material.PAPER, MgMenuLangKey.MENU_PLAYERSETTINGS_BROADCASTS_JOINEXIT_NAME,
                MgMenuLangKey.MENU_PLAYERSETTINGS_BROADCASTS_JOINEXIT_DESCRIPTION)); // todo hide if not multiplayer
        itemsPlayer.add(showCTFBroadcasts.getMenuItem(Material.PAPER, MgMenuLangKey.MENU_PLAYERSETTINGS_BROADCASTS_CTF_NAME,
                MgMenuLangKey.MENU_PLAYERSETTINGS_BROADCASTS_CTF_DESCRIPTION)); //todo hide if not ctf
        itemsPlayer.add(keepInventory.getMenuItem(Material.ZOMBIE_HEAD, MgMenuLangKey.MENU_PLAYERSETTINGS_KEEPINVENTORY_NAME));
        itemsPlayer.add(friendlyFireSplashPotions.getMenuItem(Material.SPLASH_POTION,
                MgMenuLangKey.MENU_PLAYERSETTINGS_FRIENDLYFIRE_SPLASH_NAME)); // todo hide if not multiplayer
        itemsPlayer.add(friendlyFireLingeringPotions.getMenuItem(Material.LINGERING_POTION,
                MgMenuLangKey.MENU_PLAYERSETTINGS_FRIENDLYFIRE_LINGERING_NAME)); // todo hide if not multiplayer
        playerMenu.addItems(itemsPlayer);
        playerMenu.addItem(new MenuItemBack(mainMenu), mainMenu.getSize() - 9);

        //--------------//
        //Minigame Flags//
        //--------------//
        List<MenuItem> itemsFlags = new ArrayList<>(getFlags().size());
        for (String flag : getFlags()) {
            itemsFlags.add(new MenuItemFlag(Material.OAK_SIGN, flag, getFlags()));
        }
        flags.addItem(new MenuItemBack(playerMenu), flags.getSize() - 9);
        flags.addItem(new MenuItemAddFlag(MgMenuLangKey.MENU_FLAGADD_NAME,
                MenuUtility.getCreateMaterial(), this), flags.getSize() - 1);
        flags.addItems(itemsFlags);

        //--------------//
        //Lobby Settings//
        //--------------//
        LobbySettingsModule lobbySettingsModule = LobbySettingsModule.getMinigameModule(this);
        if (lobbySettingsModule != null) {
            List<MenuItem> itemsLobby = new ArrayList<>(4);

            itemsLobby.add(new MenuItemBoolean(Material.STONE_BUTTON, MgMenuLangKey.MENU_LOBBY_WAIT_PLAYER_INTERACT_NAME,
                    lobbySettingsModule.getCanInteractPlayerWaitCallback()));
            itemsLobby.add(new MenuItemBoolean(Material.STONE_BUTTON, MgMenuLangKey.MENU_LOBBY_WAIT_START_INTERACT_NAME,
                    lobbySettingsModule.getCanInteractStartWaitCallback()));
            itemsLobby.add(new MenuItemBoolean(Material.ICE, MgMenuLangKey.MENU_LOBBY_WAIT_PLAYER_MOVE_NAME,
                    lobbySettingsModule.getCanMovePlayerWaitCallback()));
            itemsLobby.add(new MenuItemBoolean(Material.ICE, MgMenuLangKey.MENU_LOBBY_WAIT_START_MOVE_NAME,
                    lobbySettingsModule.getCanMoveStartWaitCallback()));
            itemsLobby.add(new MenuItemBoolean(Material.ENDER_PEARL, MgMenuLangKey.MENU_LOBBY_WAIT_PLAYER_TELEPORT_NAME,
                    MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_LOBBY_WAIT_PLAYER_TELEPORT_DESCRIPTION),
                    lobbySettingsModule.getTeleportOnPlayerWaitCallback()));
            itemsLobby.add(new MenuItemBoolean(Material.ENDER_PEARL, MgMenuLangKey.MENU_LOBBY_WAIT_START_TELEPORT_NAME,
                    MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_LOBBY_WAIT_START_TELEPORT_DESCRIPTION),
                    lobbySettingsModule.getTeleportOnStartCallback()));
            itemsLobby.add(new MenuItemTime(Material.CLOCK, MgMenuLangKey.MENU_LOBBY_WAIT_PLAYER_TIME_NAME,
                    MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_LOBBY_WAIT_PLAYER_TIME_DESCRIPTION),
                    lobbySettingsModule.getPlayerWaitTimeCallback(), 0L, Long.MAX_VALUE));
            lobby.addItems(itemsLobby);
            lobby.addItem(new MenuItemBack(mainMenu), lobby.getSize() - 9);
        }

        for (MinigameModule mod : getModules()) {
            mod.addEditMenuOptions(mainMenu);
        }

        mainMenu.displayMenu(player);
    }

    @NotNull
    public ScoreboardData getScoreboardData() {
        return sbData;
    }

    public void saveMinigame() {
        MinigameSave minigame = new MinigameSave(name, "config");
        FileConfiguration cfg = minigame.getConfig();
        cfg.set(name, null);
        cfg.createSection(name);

        for (MinigameModule module : getModules()) {
            if (!module.useSeparateConfig()) {
                module.save(cfg);

                if (module.getConfigFlags() != null) {
                    for (Flag<?> flag : module.getConfigFlags().values()) {
                        if (flag.getFlag() != null && (flag.getDefaultFlag() == null || !flag.getDefaultFlag().equals(flag.getFlag())))
                            flag.saveValue(name, cfg);
                    }
                }
            } else {
                MinigameSave modsave = new MinigameSave("minigames/" + name + "/" + module.getName().toLowerCase());
                modsave.getConfig().set(name, null);
                modsave.getConfig().createSection(name);
                module.save(modsave.getConfig());

                if (module.getConfigFlags() != null) {
                    for (Flag<?> flag : module.getConfigFlags().values()) {
                        if (flag.getFlag() != null && (flag.getDefaultFlag() == null || !flag.getDefaultFlag().equals(flag.getFlag())))
                            flag.saveValue(name, modsave.getConfig());
                    }
                }

                modsave.saveConfig();
            }
        }

        for (String configOpt : configFlags.keySet()) {
            if (configFlags.get(configOpt).getFlag() != null &&
                    (configFlags.get(configOpt).getDefaultFlag() == null ||
                            !configFlags.get(configOpt).getDefaultFlag().equals(configFlags.get(configOpt).getFlag())))
                configFlags.get(configOpt).saveValue(name, cfg);
        }

        //dataFixerUpper
        if (cfg.contains(name + ".useXPBarTimer")) {
            cfg.set(name + ".useXPBarTimer", null);
        }

        if (!getRecorderData().getWBBlocks().isEmpty()) {
            List<String> blocklist = new ArrayList<>();
            for (Material mat : getRecorderData().getWBBlocks()) {
                blocklist.add(mat.toString());
            }
            minigame.getConfig().set(name + ".whitelistblocks", blocklist);
        }

        if (getRecorderData().getWhitelistMode()) {
            minigame.getConfig().set(name + ".whitelistmode", getRecorderData().getWhitelistMode());
        }

        getScoreboardData().saveDisplays(minigame, name);
        getScoreboardData().refreshDisplays();
        Minigames.getPlugin().getBackend().saveStatSettings(this, statSettings.values());

        minigame.saveConfig();
    }

    public void loadMinigame() {
        MinigameSave minigame = new MinigameSave(name, "config");
        FileConfiguration cfg = minigame.getConfig();
        for (MinigameModule module : getModules()) {
            if (!module.useSeparateConfig()) {
                module.load(cfg);

                if (module.getConfigFlags() != null) {
                    for (String flag : module.getConfigFlags().keySet()) {
                        if (cfg.contains(name + "." + flag))
                            module.getConfigFlags().get(flag).loadValue(name, cfg);
                    }
                }
            } else {
                MinigameSave modsave = new MinigameSave("minigames/" + name + "/" + module.getName().toLowerCase());
                module.load(modsave.getConfig());

                if (module.getConfigFlags() != null) {
                    for (String flag : module.getConfigFlags().keySet()) {
                        if (modsave.getConfig().contains(name + "." + flag)) {
                            module.getConfigFlags().get(flag).loadValue(name, modsave.getConfig());
                        }
                    }
                }
            }
        }

        for (String flag : configFlags.keySet()) {
            if (cfg.contains(name + "." + flag)) {
                configFlags.get(flag).loadValue(name, cfg);
            }
        }

        //dataFixerUpper
        if (cfg.contains(name + ".useXPBarTimer")) {
            if (cfg.getBoolean(name + ".useXPBarTimer")){
                timerDisplayType.setFlag(MinigameTimer.DisplayType.XP_BAR);
            } else {
                timerDisplayType.setFlag(MinigameTimer.DisplayType.NONE);
            }
        }

        if (minigame.getConfig().contains(name + ".whitelistmode")) {
            getRecorderData().setWhitelistMode(minigame.getConfig().getBoolean(name + ".whitelistmode"));
        }

        if (minigame.getConfig().contains(name + ".whitelistblocks")) {
            List<String> blocklist = minigame.getConfig().getStringList(name + ".whitelistblocks");
            for (String block : blocklist) {
                Material material = Material.matchMaterial(block);
                if (material == null) {
                    material = Material.matchMaterial(block, true);
                    if (material == null) {
                        Minigames.getCmpnntLogger().info(" Failed to match config material.");
                        Minigames.getCmpnntLogger().info(block + " did not match a material please update config: " + this.name);
                    } else {
                        Minigames.getCmpnntLogger().info(block + " is a legacy material please review the config we will attempt to auto update..but you may want to add newer materials GAME: " + this.name);
                        getRecorderData().addWBBlock(material);
                    }
                } else {
                    getRecorderData().addWBBlock(material);
                }
            }
        }

        final Minigame mgm = this;

        if (getType() == MinigameType.GLOBAL && isEnabled()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.getPlugin(), () -> Minigames.getPlugin().getMinigameManager().startGlobalMinigame(mgm, null));
        }

        getScoreboardData().loadDisplays(minigame, this);

        CompletableFuture<Map<MinigameStat, StatSettings>> settingsFuture = Minigames.getPlugin().getBackend().loadStatSettings(this);
        // as far as I know it isn't defined what thread will run thenApply,
        // so we pull it back on the main thread with the BukkitScheduler
        settingsFuture.thenApply(result -> Bukkit.getScheduler().runTask(Minigames.getPlugin(), () -> {
            statSettings.clear();
            statSettings.putAll(result);

            getScoreboardData().reload();
        })).exceptionally(t -> {
            Minigames.getCmpnntLogger().error("", t);
            return null;
        });

        saveMinigame();
    }

    @Override
    @Deprecated(forRemoval = true)
    public String toString() {
        return getName();
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
            return ScriptValue.of(getName());
        } else if (name.equalsIgnoreCase("displayname")) {
            return ScriptValue.of(getName());
        }

        return null;
    }

    @Override
    public Set<String> getKeys() {
        return Set.of("players", "teams", "name", "displayname");
    }

    @Override
    public String getAsString() {
        return getName();
    }
}
