package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.objects.MgRegion;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ScriptCollection;
import au.com.mineauz.minigames.script.ScriptReference;
import au.com.mineauz.minigames.script.ScriptValue;
import au.com.mineauz.minigames.script.ScriptWrapper;
import au.com.mineauz.minigamesregions.actions.ActionInterface;
import au.com.mineauz.minigamesregions.conditions.ACondition;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import au.com.mineauz.minigamesregions.triggers.MgRegTrigger;
import au.com.mineauz.minigamesregions.triggers.Trigger;
import com.google.common.collect.ImmutableSet;
import io.papermc.paper.math.Position;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Region extends MgRegion implements ExecutableScriptObject {
    private final List<RegionExecutor> executors = new ArrayList<>();
    private final List<MinigamePlayer> players = new ArrayList<>();
    private final int gameTickDelay = 1;
    private long taskDelay = 20;
    private int taskID;
    private int gameTickTaskID;
    private boolean enabled = true;

    public Region(@NotNull World world, @NotNull String name, @NotNull Position pos1, @NotNull Position pos2) {
        super(world, name, pos1, pos2);
    }

    public Region(@NotNull String name, @NotNull Location loc1, @NotNull Location loc2) {
        super(name, loc1, loc2);
    }

    public boolean playerInRegion(MinigamePlayer player) {
        return super.isInRegen(player.getLocation());
    }

    public boolean locationInRegion(Location loc) {
        return super.isInRegen(loc);
    }

    public Location getFirstPoint() {
        return super.getLocation1();
    }

    public Location getSecondPoint() {
        return super.getLocation2();
    }

    public void updateRegion(Location point1, Location point2) {
        super.updateRegion(point1, point2);
        super.sortPositions();
    }

    public boolean hasPlayer(MinigamePlayer player) {
        return players.contains(player);
    }

    public void addPlayer(MinigamePlayer player) {
        players.add(player);
    }

    public void removePlayer(MinigamePlayer player) {
        players.remove(player);
    }

    public List<MinigamePlayer> getPlayers() {
        return players;
    }

    public int addExecutor(Trigger trigger) {
        executors.add(new RegionExecutor(trigger));
        return executors.size();
    }

    public int addExecutor(RegionExecutor exec) {
        executors.add(exec);
        return executors.size();
    }

    public List<RegionExecutor> getExecutors() {
        return executors;
    }

    public void removeExecutor(int id) {
        if (executors.size() <= id) {
            executors.remove(id - 1);
        }
    }

    public void removeExecutor(RegionExecutor executor) {
        executors.remove(executor);
    }

    public void changeTickDelay(long delay) {
        removeTickTask();
        taskDelay = delay;
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Minigames.getPlugin(), () -> {
            List<MinigamePlayer> plys = new ArrayList<>(players);
            for (MinigamePlayer player : plys) {
                execute(MgRegTrigger.TIME_TICK, player);
            }
        }, 0, delay);
    }

    public long getTickDelay() {
        return taskDelay;
    }

    public void startTickTask() {
        if (taskID != -1) {
            removeTickTask();
        }

        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Minigames.getPlugin(), () -> {
            List<MinigamePlayer> plys = new ArrayList<>(players);
            for (MinigamePlayer player : plys) {
                execute(MgRegTrigger.TIME_TICK, player);
            }
        }, 0, taskDelay);
    }

    public void startGameTickTask() {
        if (gameTickTaskID != -1) {
            removeGameTickTask();
        }

        gameTickTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Minigames.getPlugin(),
                this::executeGameTick,
                0, gameTickDelay);
    }

    public void removeTickTask() {
        Bukkit.getScheduler().cancelTask(taskID);
    }

    public void removeGameTickTask() {
        Bukkit.getScheduler().cancelTask(gameTickTaskID);
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void execute(@NotNull Trigger trigger, @Nullable MinigamePlayer player) {
        if (player != null && player.getMinigame() != null && player.getMinigame().isSpectator(player)) return;
        List<RegionExecutor> toExecute = new ArrayList<>();
        for (RegionExecutor exec : executors) {
            if (exec.getTrigger() == trigger) {
                if (checkConditions(exec, player) && exec.canBeTriggered(player))
                    toExecute.add(exec);
            }
        }
        for (RegionExecutor exec : toExecute) {
            execute(exec, player);
        }
    }

    public boolean checkConditions(@NotNull RegionExecutor exec, @Nullable MinigamePlayer player) {
        for (ACondition con : exec.getConditions()) {
            boolean c = con.checkRegionCondition(player, this);
            if (con.isInverted())
                c = !c;
            if (!c) {
                return false;
            }
        }
        return true;
    }

    public void execute(RegionExecutor exec, MinigamePlayer player) {
        for (ActionInterface act : exec.getActions()) {
            if (!enabled && !act.getName().equalsIgnoreCase("SET_ENABLED")) continue;
            act.executeRegionAction(player, this);
            if (!exec.isTriggerPerPlayer())
                exec.addPublicTrigger();
            else
                exec.addPlayerTrigger(player);
        }
    }

    public void executeGameTick() {
        if (players.isEmpty()) return;
        // There is no condition, which is not player specific, so we can just execute all executors.
        for (RegionExecutor exec : executors) {
            for (ActionInterface act : exec.getActions()) {
                if (!enabled && !act.getName().equalsIgnoreCase("SET_ENABLED")) continue;
                try {
                    if (checkConditions(exec, null) && exec.getTrigger() == MgRegTrigger.TIME_GAMETICK) {
                        act.executeRegionAction(null, this);
                        exec.addPublicTrigger();
                    }
                } catch (Exception e) {
                    for (MinigamePlayer mgPlayer : players) {
                        MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.ERROR, RegionMessageManager.getBundleKey(),
                                RegionLangKey.TRIGGER_TICK_ERROR_CONDITION);
                    }
                }
            }
        }
    }

    @Override
    public ScriptReference get(String name) {
        if (name.equalsIgnoreCase("name")) {
            return ScriptValue.of(name);
        } else if (name.equalsIgnoreCase("players")) {
            return ScriptCollection.of(players);
        } else if (name.equalsIgnoreCase("min")) {
            return ScriptWrapper.wrap(super.getLocation1());
        } else if (name.equalsIgnoreCase("max")) {
            return ScriptWrapper.wrap(super.getLocation2());
        }

        return null;
    }

    @Override
    public String getAsString() {
        return super.getName();
    }

    @Override
    public Set<String> getKeys() {
        return ImmutableSet.of("name", "players", "min", "max");
    }
}
