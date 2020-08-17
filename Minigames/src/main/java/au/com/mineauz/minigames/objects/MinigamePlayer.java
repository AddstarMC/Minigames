package au.com.mineauz.minigames.objects;

import au.com.mineauz.minigames.*;
import au.com.mineauz.minigames.config.MinigameSave;
import au.com.mineauz.minigames.display.IDisplayCubiod;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigames.script.ScriptReference;
import au.com.mineauz.minigames.script.ScriptValue;
import au.com.mineauz.minigames.script.ScriptWrapper;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class MinigamePlayer implements ScriptObject {
    private final Player player;
    private final List<String> flags = new ArrayList<>();
    private final List<String> tempClaimedRewards = new ArrayList<>();
    private final List<ItemStack> tempRewardItems = new ArrayList<>();
    private final List<ItemStack> rewardItems = new ArrayList<>();
    private final List<String> claimedScoreSigns = new ArrayList<>();
    private boolean allowTP;
    private boolean allowGMChange;
    private boolean canFly;
    private Scoreboard lastScoreboard;
    private Minigame minigame;
    private PlayerLoadout loadout;
    private boolean requiredQuit;
    private Location startPos;
    private Location quitPos;
    private Location checkpoint;
    private int kills;
    private int deaths;
    private int score;
    private long startTime;
    private long endTime;
    private long storedTime;
    private long completeTime;
    private int reverts;
    private boolean isLatejoining;
    private boolean isFrozen;
    private boolean canPvP = true;
    private boolean isInvincible;
    private boolean canInteract = true;
    private Team team;
    private Menu menu;
    private boolean noClose;
    private MenuItem manualEntry;
    private Location selection1;
    private Location selection2;
    private IDisplayCubiod selectionDisplay;
    private OfflineMinigamePlayer oply;
    private StoredPlayerCheckpoints spc;
    private List<String> claimedRewards = new ArrayList<>();
    private int lateJoinTimer = -1;
    public MinigamePlayer(final Player player) {
        this.player = player;
        this.spc = new StoredPlayerCheckpoints(this.getUUID().toString());

        final File plcp = new File(Minigames.getPlugin().getDataFolder() + "/playerdata/checkpoints/" + this.getUUID() + ".yml");
        if (plcp.exists()) {
            this.getStoredPlayerCheckpoints().loadCheckpoints();
        }
    }

    public Location getStartPos() {
        return this.startPos;
    }

    public void setStartPos(final Location startPos) {
        this.startPos = startPos;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getName() {
        return ChatColor.stripColor(this.player.getName());
    }

    public String getDisplayName() {
        return this.getDisplayName(true);
    }

    public String getDisplayName(final Boolean displayName) {
        if (displayName) {
            return ChatColor.stripColor(this.player.getDisplayName());
        } else {
            return this.getName();
        }
    }

    public UUID getUUID() {
        return this.player.getUniqueId();
    }

    public Location getLocation() {
        return this.player.getLocation();
    }

    private void sendMessage(final String msg) {
        final int enc = Math.floorDiv(msg.getBytes().length, msg.length());
        if (msg.getBytes().length > 32000) {
            final int capLength = Math.floorDiv(msg.length(), enc);
            final String newMessage = msg.substring(0, capLength);
            this.player.sendMessage(newMessage);
            this.sendMessage(msg.substring(capLength));
        }
        this.player.sendMessage(msg);
    }

    public void sendInfoMessage(final String msg) {
        this.sendMessage(msg, MinigameMessageType.INFO);
    }

    public void sendMessage(final String msg, final @NotNull MinigameMessageType type) {
        String init = "";
        switch (type) {
            case ERROR:
                init = ChatColor.RED + "[Minigames] " + ChatColor.WHITE;
                break;
            case WIN:
                init = ChatColor.GREEN + "[Minigames] " + ChatColor.WHITE;
                break;
            case LOSS:
                init = ChatColor.DARK_RED + "[Minigames] " + ChatColor.WHITE;
                break;
            case INFO:
            default:
                init = ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE;
        }
        this.sendMessage(init + msg);
    }

    public void storePlayerData() {
        final ItemStack[] storedItems = this.player.getInventory().getContents();
        final ItemStack[] storedArmour = this.player.getInventory().getArmorContents();
        final int food = this.player.getFoodLevel();
        final double health = this.player.getHealth();
        final float saturation = this.player.getSaturation();
        this.lastScoreboard = this.player.getScoreboard();
        final GameMode lastGM = this.player.getGameMode();
        float exp = this.player.getExp();
        if (exp < 0) {
            Minigames.log.warning("Player Experience was less that 0: " + this.player.getDisplayName() + " " + this.player.getExp());
            exp = 0;
        }
        final int level = this.player.getLevel();

        this.player.setSaturation(15);
        this.player.setFoodLevel(20);
        this.player.setHealth(this.player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
        this.player.getInventory().clear();
        this.player.getInventory().setArmorContents(null);
        this.player.setLevel(0);
        this.player.setExp(0);

        this.oply = new OfflineMinigamePlayer(this.getPlayer().getUniqueId(),storedItems, storedArmour, food,
                health, saturation, lastGM, exp, level,this.getPlayer().getLocation());
        this.player.updateInventory();
    }

    public void restorePlayerData() {
        this.player.getInventory().clear();
        this.player.getInventory().setArmorContents(null);

        this.player.getInventory().setContents(this.oply.getStoredItems());
        this.player.getInventory().setArmorContents(this.oply.getStoredArmour());
        this.player.setFoodLevel(this.oply.getFood());
        if (this.oply.getHealth() > 20)
            this.player.setHealth(20);
        else
            this.player.setHealth(this.oply.getHealth());
        this.player.setSaturation(this.oply.getSaturation());
        if (this.lastScoreboard != null) {
            this.player.setScoreboard(this.lastScoreboard);
        } else {
            this.player.setScoreboard(this.player.getServer().getScoreboardManager().getMainScoreboard());
        }

        if (this.oply.getExp() >= 0) {
            this.player.setExp(this.oply.getExp());
            this.player.setLevel(this.oply.getLevel());
        }
        this.startPos = null;
        this.player.resetPlayerWeather();
        this.player.resetPlayerTime();
        this.allowGMChange = true;
        this.allowTP = true;
        this.player.setGameMode(this.oply.getLastGamemode());

        this.oply.deletePlayerData();
        this.oply = null;

        this.player.updateInventory();
    }

    public boolean hasStoredData() {
        return this.oply != null;
    }

    public boolean getAllowTeleport() {
        return this.allowTP;
    }

    public void setAllowTeleport(final boolean allowTP) {
        this.allowTP = allowTP;
    }

    public boolean getAllowGamemodeChange() {
        return this.allowGMChange;
    }

    public void setAllowGamemodeChange(final boolean allowGMChange) {
        this.allowGMChange = allowGMChange;
    }

    public Minigame getMinigame() {
        return this.minigame;
    }

    public void setMinigame(final Minigame minigame) {
        this.minigame = minigame;
    }

    public void removeMinigame() {
        this.minigame = null;
    }

    public boolean isInMinigame() {
        return this.minigame != null;
    }

    public boolean isRequiredQuit() {
        return this.requiredQuit;
    }

    public void setRequiredQuit(final boolean requiredQuit) {
        this.requiredQuit = requiredQuit;
    }

    public Location getQuitPos() {
        return this.quitPos;
    }

    public void setQuitPos(final Location quitPos) {
        this.quitPos = quitPos;
    }

    public PlayerLoadout getLoadout() {
        if (this.loadout != null) {
            return this.loadout;
        } else if (this.team != null && LoadoutModule.getMinigameModule(this.minigame).hasLoadout(this.team.getColor().toString().toLowerCase())) {
            return LoadoutModule.getMinigameModule(this.minigame).getLoadout(this.team.getColor().toString().toLowerCase());
        }
        return LoadoutModule.getMinigameModule(this.minigame).getLoadout("default");
    }

    public boolean setLoadout(final PlayerLoadout loadout) {
        if (this.getMinigame() == null) return false;
        if (loadout == null || !this.getMinigame().isTeamGame() || loadout.getTeamColor() == null || this.getTeam().getColor() == loadout.getTeamColor()) {
            this.loadout = loadout;
            return true;
        }
        return false;
    }

    public List<String> getFlags() {
        return this.flags;
    }

    public void setFlags(final List<String> flags) {
        this.flags.addAll(flags);
    }

    public boolean addFlag(final String flag) {
        if (!this.flags.contains(flag)) {
            this.flags.add(flag);
            return true;
        }
        return false;
    }

    public boolean hasFlag(final String flagName) {
        return this.flags.contains(flagName);
    }

    public void clearFlags() {
        this.flags.clear();
    }

    public Location getCheckpoint() {
        return this.checkpoint;
    }

    public void setCheckpoint(final Location checkpoint) {
        this.checkpoint = checkpoint;
    }

    public boolean hasCheckpoint() {
        return this.checkpoint != null;
    }

    public void removeCheckpoint() {
        this.checkpoint = null;
    }

    public int getKills() {
        return this.kills;
    }

    public void addKill() {
        this.kills++;
    }

    public void resetKills() {
        this.kills = 0;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void setDeaths(final int deaths) {
        this.deaths = deaths;
    }

    public void addDeath() {
        this.deaths++;
    }

    public void resetDeaths() {
        this.deaths = 0;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(final int score) {
        this.score = score;
    }

    public void addScore() {
        this.score++;
    }

    public void addScore(final int amount) {
        this.score += amount;
    }

    public void resetScore() {
        this.score = 0;
    }

    public void takeScore() {
        this.score--;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(final long ms) {
        this.startTime = ms;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(final long ms) {
        this.endTime = ms;
    }

    public void resetTime() {
        this.startTime = 0;
        this.endTime = 0;
        this.storedTime = 0;
    }

    public long getStoredTime() {
        return this.storedTime;
    }

    public void setStoredTime(final long ms) {
        this.storedTime = ms;
    }

    public long getCompletionTime() {
        return this.completeTime;
    }

    public void setCompleteTime(final long ms) {
        this.completeTime = ms;
    }

    public void addRevert() {
        this.reverts++;
    }

    public int getReverts() {
        return this.reverts;
    }

    public void setReverts(final int count) {
        this.reverts = count;
    }

    public void resetReverts() {
        this.reverts = 0;
    }

    public boolean isFrozen() {
        return this.isFrozen;
    }

    public void setFrozen(final boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    public boolean canPvP() {
        return this.canPvP;
    }

    public void setCanPvP(final boolean canPvP) {
        this.canPvP = canPvP;
    }

    public boolean isInvincible() {
        return this.isInvincible;
    }

    public void setInvincible(final boolean isInvincible) {
        this.isInvincible = isInvincible;
    }

    public boolean canInteract() {
        return this.canInteract;
    }

    public void setCanInteract(final boolean canInteract) {
        this.canInteract = canInteract;
    }

    public boolean canFly() {
        return this.canFly;
    }

    public void setCanFly(final boolean bool) {
        this.canFly = bool;
        this.player.setAllowFlight(bool);
    }

    public void resetAllStats() {
//        setLoadout(null);
        this.loadout = null;
        this.resetReverts();
        this.resetDeaths();
        this.resetKills();
        this.resetScore();
        this.resetTime();
        this.clearFlags();
        this.removeCheckpoint();
        this.setFrozen(false);
        this.setCanPvP(true);
        this.setInvincible(false);
        this.setCanInteract(true);
        this.setLatejoining(false);
        if (this.player.getGameMode() != GameMode.CREATIVE)
            this.setCanFly(false);
        this.tempClaimedRewards.clear();
        this.tempRewardItems.clear();
        this.claimedScoreSigns.clear();
        if (this.lateJoinTimer != -1) {
            Bukkit.getScheduler().cancelTask(this.lateJoinTimer);
            this.setLateJoinTimer(-1);
        }
    }

    public boolean isLatejoining() {
        return this.isLatejoining;
    }

    public void setLatejoining(final boolean isLatejoining) {
        this.isLatejoining = isLatejoining;
    }

    public Menu getMenu() {
        return this.menu;
    }

    public void setMenu(final Menu menu) {
        this.menu = menu;
    }

    public boolean isInMenu() {
        return this.menu != null;
    }

    public boolean getNoClose() {
        return this.noClose;
    }

    public void setNoClose(final boolean value) {
        this.noClose = value;
    }

    public MenuItem getManualEntry() {
        return this.manualEntry;
    }

    public void setManualEntry(final MenuItem item) {
        this.manualEntry = item;
    }

    public void addSelectionPoint(final Location loc) {
        if (this.selection1 == null) {
            this.selection1 = loc;
            this.showSelection(false);
            this.sendMessage("Position 1 set", MinigameMessageType.INFO);
        } else if (this.selection2 == null) {
            this.selection2 = loc;
            this.showSelection(false);
            this.sendMessage("Position 2 set", MinigameMessageType.INFO);
        } else if (this.selection2 != null) {
            this.showSelection(true);
            this.selection1 = loc;
            this.sendMessage("Selection restarted", MinigameMessageType.INFO);
            this.sendMessage("Position 1 set", MinigameMessageType.INFO);
            this.selection2 = null;
            this.showSelection(false);
        }
    }

    public boolean hasSelection() {
        return this.selection1 != null && this.selection2 != null;
    }

    public Location[] getSelectionPoints() {
        final Location[] loc = new Location[2];
        loc[0] = this.selection1;
        loc[1] = this.selection2;
        return loc;
    }

    public void clearSelection() {
        this.showSelection(true);
        this.selection1 = null;
        this.selection2 = null;
    }

    public void setSelection(final Location point1, final Location point2) {
        this.selection1 = point1;
        this.selection2 = point2;

        this.showSelection(false);
    }

    public void showSelection(final boolean clear) {
        if (this.selectionDisplay != null) {
            this.selectionDisplay.remove();
            this.selectionDisplay = null;
        }

        if (!clear) {
            if (this.selection2 != null && this.selection1 != null) {
                final Location[] locs = MinigameUtils.getMinMaxSelection(this.selection1, this.selection2);
                this.selectionDisplay = Minigames.getPlugin().display.displayCuboid(this.getPlayer(), locs[0], locs[1].add(1, 1, 1));
                this.selectionDisplay.show();
            } else if (this.selection1 != null) {
                this.selectionDisplay = Minigames.getPlugin().display.displayCuboid(this.getPlayer(), this.selection1, this.selection1.clone().add(1, 1, 1));
                this.selectionDisplay.show();
            } else if (this.selection2 != null) {
                this.selectionDisplay = Minigames.getPlugin().display.displayCuboid(this.getPlayer(), this.selection2, this.selection2.clone().add(1, 1, 1));
                this.selectionDisplay.show();
            }
        }
    }

    public OfflineMinigamePlayer getOfflineMinigamePlayer() {
        return this.oply;
    }

    public void setOfflineMinigamePlayer(final OfflineMinigamePlayer oply) {
        this.oply = oply;
    }

    public StoredPlayerCheckpoints getStoredPlayerCheckpoints() {
        return this.spc;
    }

    public void setGamemode(final GameMode gamemode) {
        this.setAllowGamemodeChange(true);
        this.player.setGameMode(gamemode);
        this.setAllowGamemodeChange(false);
    }

    public boolean teleport(final @NotNull Location location) {
        boolean bool = false;
        this.setAllowTeleport(true);
        bool = this.getPlayer().teleport(location);
        this.setAllowTeleport(false);

        return bool;
    }

    public void updateInventory() {
        this.getPlayer().updateInventory();
    }

    public boolean isDead() {
        return this.player.isDead();
    }

    public Team getTeam() {
        return this.team;
    }

    public void setTeam(final Team team) {
        this.team = team;
    }

    public void removeTeam() {
        if (this.team != null) {
            this.team.removePlayer(this);
            this.team = null;
        }
    }

    public boolean hasClaimedReward(final String reward) {
        return this.claimedRewards.contains(reward);
    }

    public boolean hasTempClaimedReward(final String reward) {
        return this.tempClaimedRewards.contains(reward);
    }

    public void addTempClaimedReward(final String reward) {
        this.tempClaimedRewards.add(reward);
    }

    public void addClaimedReward(final String reward) {
        this.claimedRewards.add(reward);
    }

    public void saveClaimedRewards() {
        if (!this.claimedRewards.isEmpty()) {
            final MinigameSave save = new MinigameSave("playerdata/data/" + this.getUUID());
            final FileConfiguration cfg = save.getConfig();
            cfg.set("claims", this.claimedRewards);
            save.saveConfig();
        }
    }

    public void loadClaimedRewards() {
        final File f = new File(Minigames.getPlugin().getDataFolder() + "/playerdata/data/" + this.getUUID() + ".yml");
        if (f.exists()) {
            final MinigameSave s = new MinigameSave("playerdata/data/" + this.getUUID());
            this.claimedRewards = s.getConfig().getStringList("claims");
        }
    }

    public void addTempRewardItem(final ItemStack item) {
        this.tempRewardItems.add(item);
    }

    public List<ItemStack> getTempRewardItems() {
        return this.tempRewardItems;
    }

    public void addRewardItem(final ItemStack item) {
        this.rewardItems.add(item);
    }

    public List<ItemStack> getRewardItems() {
        return this.rewardItems;
    }

    public boolean hasClaimedScore(final Location loc) {
        final String id = MinigameUtils.createLocationID(loc);
        return this.claimedScoreSigns.contains(id);
    }

    public boolean applyResourcePack(final ResourcePack pack) {
        try {
            this.player.getPlayer().setResourcePack(pack.getUrl().toString(), pack.getSH1Hash());
            return true;
        } catch (final IllegalArgumentException e) {
            Minigames.log().warning(e.getMessage());
        }
        return false;
    }

    public void addClaimedScore(final Location loc) {
        final String id = MinigameUtils.createLocationID(loc);
        this.claimedScoreSigns.add(id);
    }

    public void claimTempRewardItems() {
        if (!this.isDead()) {
            final List<ItemStack> tempItems = new ArrayList<>(this.getTempRewardItems());

            if (!tempItems.isEmpty()) {
                for (final ItemStack item : tempItems) {
                    final Map<Integer, ItemStack> m = this.player.getPlayer().getInventory().addItem(item);
                    if (!m.isEmpty()) {
                        for (final ItemStack i : m.values()) {
                            this.player.getPlayer().getWorld().dropItemNaturally(this.player.getPlayer().getLocation(), i);
                        }
                    }
                }
            }
        }
    }

    public void claimRewards() {
        if (!this.isDead()) {
            final List<ItemStack> tempItems = new ArrayList<>(this.getRewardItems());

            if (!tempItems.isEmpty()) {
                for (final ItemStack item : tempItems) {
                    final Map<Integer, ItemStack> m = this.player.getPlayer().getInventory().addItem(item);
                    if (!m.isEmpty()) {
                        for (final ItemStack i : m.values()) {
                            this.player.getPlayer().getWorld().dropItemNaturally(this.player.getPlayer().getLocation(), i);
                        }
                    }
                }
            }
        }
    }

    public void setLateJoinTimer(final int taskID) {
        this.lateJoinTimer = taskID;
    }

    @Override
    public ScriptReference get(final String name) {
        if (name.equalsIgnoreCase("name")) {
            return ScriptValue.of(this.player.getName());
        } else if (name.equalsIgnoreCase("displayname")) {
            return ScriptValue.of(this.player.getDisplayName());
        } else if (name.equalsIgnoreCase("score")) {
            return ScriptValue.of(this.score);
        } else if (name.equalsIgnoreCase("kills")) {
            return ScriptValue.of(this.kills);
        } else if (name.equalsIgnoreCase("deaths")) {
            return ScriptValue.of(this.deaths);
        } else if (name.equalsIgnoreCase("health")) {
            return ScriptValue.of(this.player.getHealth());
        } else if (name.equalsIgnoreCase("team")) {
            return this.team;
        } else if (name.equalsIgnoreCase("pos")) {
            return ScriptWrapper.wrap(this.player.getLocation());
        } else if (name.equalsIgnoreCase("minigame")) {
            return this.minigame;
        }

        return null;
    }

    @Override
    public Set<String> getKeys() {
        return ImmutableSet.of("name", "displayname", "score", "kills", "deaths", "health", "team", "pos", "minigame");
    }

    @Override
    public String getAsString() {
        return this.getName();
    }
}
