package au.com.mineauz.minigames.objects;

import au.com.mineauz.minigames.*;
import au.com.mineauz.minigames.config.MinigameSave;
import au.com.mineauz.minigames.display.IDisplayCuboid;
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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

/**
 * wrapper class to keep track of players with additional information.
 * A MinigamePlayer does NOT have to be in a Minigame to be valid!
 */
public class MinigamePlayer implements ScriptObject {
    private final @NotNull Player player;
    private final @NotNull List<@NotNull String> flags = new ArrayList<>();
    private final @NotNull List<@NotNull String> tempClaimedRewards = new ArrayList<>();
    private final @NotNull List<@NotNull ItemStack> tempRewardItems = new ArrayList<>();
    private final @NotNull List<@NotNull ItemStack> rewardItems = new ArrayList<>();
    private final @NotNull List<@NotNull String> claimedScoreSigns = new ArrayList<>();
    private boolean allowTP;
    private boolean allowGMChange;
    private boolean canFly;
    private @Nullable Scoreboard lastScoreboard;
    private @Nullable Minigame minigame;
    private @Nullable PlayerLoadout loadout;
    private boolean requiredQuit;
    private @Nullable Location startPos;
    private @Nullable Location quitPos;
    private @Nullable Location checkpoint;
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
    private @Nullable Team team;
    private @Nullable Menu menu;
    private boolean noClose;
    private @Nullable MenuItem manualEntry;
    private @Nullable Location selection1;
    private @Nullable Location selection2;
    private @Nullable IDisplayCuboid selectionDisplay;
    private OfflineMinigamePlayer offlineMinigamePlayer;
    private final @Nullable StoredPlayerCheckpoints spc;
    private @NotNull List<@NotNull String> claimedRewards = new ArrayList<>();
    private int lateJoinTimer = -1;

    public MinigamePlayer(final @NotNull Player player) {
        this.player = player;
        this.spc = new StoredPlayerCheckpoints(this.getUUID().toString());

        final File plcp = new File(Minigames.getPlugin().getDataFolder() + "/playerdata/checkpoints/" + this.getUUID() + ".yml");
        if (plcp.exists()) {
            this.getStoredPlayerCheckpoints().loadCheckpoints();
        }
    }

    public @Nullable Location getStartPos() {
        return this.startPos;
    }

    public void setStartPos(final @Nullable Location startPos) {
        this.startPos = startPos;
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    public @NotNull String getName() {
        return ChatColor.stripColor(this.player.getName());
    }

    public @NotNull String getDisplayName() {
        return this.getDisplayName(true);
    }

    public @NotNull String getDisplayName(final Boolean displayName) {
        if (displayName) {
            return ChatColor.stripColor(this.player.getDisplayName());
        } else {
            return this.getName();
        }
    }

    public @NotNull UUID getUUID() {
        return this.player.getUniqueId();
    }

    public @NotNull Location getLocation() {
        return this.player.getLocation();
    }

    @Deprecated(forRemoval = true)
    private void sendMessage(final @NotNull String msg) {
        final int enc = Math.floorDiv(msg.getBytes().length, msg.length());
        if (msg.getBytes().length > 32000) { //chat limit set to a higher number.
            final int capLength = Math.floorDiv(msg.length(), enc);
            final String newMessage = msg.substring(0, capLength);
            this.player.sendMessage(newMessage);
            this.sendMessage(msg.substring(capLength));
        }
        this.player.sendMessage(msg);
    }

    @Deprecated(forRemoval = true)
    private void sendMessage(final @NotNull Component msg) {
        this.player.sendMessage(msg);
    }

    @Deprecated(forRemoval = true)
    public void sendInfoMessage(final @NotNull String msg) {
        this.sendMessage(msg, MinigameMessageType.INFO);
    }

    @Deprecated(forRemoval = true)
    public void sendInfoMessage(final @NotNull Component msg) {
        this.sendMessage(msg, MinigameMessageType.INFO);
    }

    @Deprecated(forRemoval = true)
    public void sendUnprefixedMessage(final @NotNull String msg) {
        this.sendMessage(msg, MinigameMessageType.NONE);
    }

    @Deprecated(forRemoval = true)
    public void sendUnprefixedMessage(final @NotNull Component msg) {
        this.sendMessage(msg, MinigameMessageType.NONE);
    }

    @Deprecated(forRemoval = true)
    public void sendMessage(final @NotNull String msg, final @NotNull MinigameMessageType type) {
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
            case NONE:
                break;
            case INFO:
            default:
                init = ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE;
        }
        this.sendMessage(init + msg);
    }

    @Deprecated(forRemoval = true)
    public void sendMessage(final @NotNull Component msg, final @NotNull MinigameMessageType type) {
        Component init;
        init = switch (type) {
            case ERROR -> Component.text("[Minigames] ", NamedTextColor.RED);
            case WARN -> Component.text("[Minigames]", NamedTextColor.GOLD);
            case WIN -> Component.text("[Minigames] ", NamedTextColor.GREEN);
            case LOSS -> Component.text("[Minigames] ", NamedTextColor.DARK_RED);
            case NONE -> Component.text("");
            default -> Component.text("[Minigames] ", NamedTextColor.AQUA); //also info
        };

        this.sendMessage(init.append(msg.colorIfAbsent(NamedTextColor.WHITE)));
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

        this.offlineMinigamePlayer = new OfflineMinigamePlayer(this.getPlayer().getUniqueId(), storedItems, storedArmour, food,
                health, saturation, lastGM, exp, level, this.getPlayer().getLocation());
        this.player.updateInventory();
    }

    public void restorePlayerData() {
        this.player.getInventory().clear();
        this.player.getInventory().setArmorContents(null);

        this.player.getInventory().setContents(this.offlineMinigamePlayer.getStoredItems());
        this.player.getInventory().setArmorContents(this.offlineMinigamePlayer.getStoredArmour());
        this.player.setFoodLevel(this.offlineMinigamePlayer.getFood());
        if (this.offlineMinigamePlayer.getHealth() > 20) { // todo don't hardcode. use attributes!
            this.player.setHealth(20);
        } else {
            this.player.setHealth(this.offlineMinigamePlayer.getHealth());
        }
        this.player.setSaturation(this.offlineMinigamePlayer.getSaturation());
        this.player.setScoreboard(Objects.requireNonNullElseGet(this.lastScoreboard, () -> this.player.getServer().getScoreboardManager().getMainScoreboard()));

        if (this.offlineMinigamePlayer.getExp() >= 0) {
            this.player.setExp(this.offlineMinigamePlayer.getExp());
            this.player.setLevel(this.offlineMinigamePlayer.getLevel());
        }
        this.startPos = null;
        this.player.resetPlayerWeather();
        this.player.resetPlayerTime();
        this.allowGMChange = true;
        this.allowTP = true;
        this.player.setGameMode(this.offlineMinigamePlayer.getLastGamemode());

        this.offlineMinigamePlayer.deletePlayerData();
        this.offlineMinigamePlayer = null;

        this.player.updateInventory();
    }

    public boolean hasStoredData() {
        return this.offlineMinigamePlayer != null;
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

    public @Nullable Minigame getMinigame() {
        return this.minigame;
    }

    public void setMinigame(final @NotNull Minigame minigame) {
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

    public @Nullable Location getQuitPos() {
        return this.quitPos;
    }

    public void setQuitPos(final @Nullable Location quitPos) {
        this.quitPos = quitPos;
    }

    /**
     * will return null, if the player is NOT in a Minigame
     *
     * @return
     */
    public @Nullable PlayerLoadout getLoadout() {
        if (this.minigame != null) {
            if (this.loadout != null) {
                return this.loadout;
            } else if (this.team != null && LoadoutModule.getMinigameModule(this.minigame).hasLoadout(this.team.getColor().toString().toLowerCase())) {
                return LoadoutModule.getMinigameModule(this.minigame).getLoadout(this.team.getColor().toString().toLowerCase());
            }
            return LoadoutModule.getMinigameModule(this.minigame).getLoadout("default");
        } else {
            return null;
        }
    }

    /**
     * will return null, if the player is NOT in a Minigame
     *
     * @return
     */
    public @Nullable PlayerLoadout getDefaultLoadout() {
        if (this.minigame != null) {
            if (this.team != null && LoadoutModule.getMinigameModule(this.minigame).hasLoadout(this.team.getColor().toString().toLowerCase())) {
                return LoadoutModule.getMinigameModule(this.minigame).getLoadout(this.team.getColor().toString().toLowerCase());
            }
            return LoadoutModule.getMinigameModule(this.minigame).getLoadout("default");
        } else {
            return null;
        }
    }

    public boolean setLoadout(final @Nullable PlayerLoadout loadout) {
        if (this.getMinigame() == null) return false;
        if (loadout == null || !this.getMinigame().isTeamGame() || loadout.getTeamColor() == null || this.getTeam().getColor() == loadout.getTeamColor()) {
            this.loadout = loadout;
            return true;
        }
        return false;
    }

    public @NotNull List<@NotNull String> getFlags() {
        return this.flags;
    }

    public void setFlags(final @NotNull List<@NotNull String> flags) {
        this.flags.addAll(flags);
    }

    public boolean addFlag(final @NotNull String flag) {
        if (!this.flags.contains(flag)) {
            this.flags.add(flag);
            return true;
        }
        return false;
    }

    public boolean hasFlag(final @NotNull String flagName) {
        return this.flags.contains(flagName);
    }

    public void clearFlags() {
        this.flags.clear();
    }

    public @Nullable Location getCheckpoint() {
        return this.checkpoint;
    }

    public void setCheckpoint(final @Nullable Location checkpoint) {
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
        if (this.player.getGameMode() != GameMode.CREATIVE) {
            this.setCanFly(false);
        }
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

    public @Nullable Menu getMenu() {
        return this.menu;
    }

    public void setMenu(final @Nullable Menu menu) {
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

    public @Nullable MenuItem getManualEntry() {
        return this.manualEntry;
    }

    public void setManualEntry(final @Nullable MenuItem item) {
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
        } else {
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

    public @Nullable Location @NotNull [] getSelectionPoints() {
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

    public void setSelection(final @NotNull Location point1, final @NotNull Location point2) {
        this.selection1 = point1;
        this.selection2 = point2;

        this.showSelection(false);
    }

    public void setSelection(final @NotNull MgRegion region) {
        this.selection1 = region.getLocation1();
        this.selection2 = region.getLocation2();

        this.showSelection(false);
    }

    public void showSelection(final boolean clear) {
        if (this.selectionDisplay != null) {
            this.selectionDisplay.remove();
            this.selectionDisplay = null;
        }

        if (!clear) {
            if (this.selection2 != null && this.selection1 != null) {
                this.selectionDisplay = Minigames.getPlugin().display.displayCuboid(this.getPlayer(), selection1, selection2.clone().add(1, 1, 1));
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

    public @Nullable OfflineMinigamePlayer getOfflineMinigamePlayer() {
        return this.offlineMinigamePlayer;
    }

    public void setOfflineMinigamePlayer(final @NotNull OfflineMinigamePlayer oply) {
        this.offlineMinigamePlayer = oply;
    }

    public @Nullable StoredPlayerCheckpoints getStoredPlayerCheckpoints() {
        return this.spc;
    }

    public void setGamemode(final @NotNull GameMode gamemode) {
        this.setAllowGamemodeChange(true);
        this.player.setGameMode(gamemode);
        this.setAllowGamemodeChange(false);
    }

    public boolean teleport(final @NotNull Location location) {
        this.setAllowTeleport(true);
        boolean bool = this.getPlayer().teleport(location);
        this.setAllowTeleport(false);

        return bool;
    }

    public void updateInventory() {
        this.getPlayer().updateInventory();
    }

    public boolean isLiving() {
        return !this.player.isDead();
    }

    public @Nullable Team getTeam() {
        return this.team;
    }

    public void setTeam(final @Nullable Team team) {
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

    public void addTempClaimedReward(final @NotNull String reward) {
        this.tempClaimedRewards.add(reward);
    }

    public void addClaimedReward(final @NotNull String reward) {
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

    public void addTempRewardItem(final @NotNull ItemStack item) {
        this.tempRewardItems.add(item);
    }

    public @NotNull List<@NotNull ItemStack> getTempRewardItems() {
        return this.tempRewardItems;
    }

    public void addRewardItem(final @NotNull ItemStack item) {
        this.rewardItems.add(item);
    }

    public @NotNull List<@NotNull ItemStack> getRewardItems() {
        return this.rewardItems;
    }

    public boolean hasClaimedScore(final @NotNull Location loc) {
        final String id = MinigameUtils.createLocationID(loc);
        return this.claimedScoreSigns.contains(id);
    }

    public boolean applyResourcePack(final @NotNull ResourcePack pack) {
        try {
            this.player.getPlayer().setResourcePack(pack.getUrl().toString(), pack.getSH1Hash());
            return true;
        } catch (final IllegalArgumentException e) {
            Minigames.log().warning(e.getMessage());
        }
        return false;
    }

    public void addClaimedScore(final @NotNull Location loc) {
        final String id = MinigameUtils.createLocationID(loc);
        this.claimedScoreSigns.add(id);
    }

    public void claimTempRewardItems() {
        if (this.isLiving()) {
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
        if (this.isLiving()) {
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
    public @Nullable ScriptReference get(final @NotNull String name) {
        return switch (name.toLowerCase()) {
            case "name" -> ScriptValue.of(this.player.getName());
            case "displayname" -> ScriptValue.of(this.player.getDisplayName());
            case "score" -> ScriptValue.of(this.score);
            case "kills" -> ScriptValue.of(this.kills);
            case "deaths" -> ScriptValue.of(this.deaths);
            case "health" -> ScriptValue.of(this.player.getHealth());
            case "team" -> this.team;
            case "pos" -> ScriptWrapper.wrap(this.player.getLocation());
            case "minigame" -> this.minigame;
            default -> null;
        };
    }

    @Override
    public @NotNull ImmutableSet<@NotNull String> getKeys() {
        return ImmutableSet.of("name", "displayname", "score", "kills", "deaths", "health", "team", "pos", "minigame");
    }

    @Override
    public @NotNull String getAsString() {
        return this.getName();
    }
}
