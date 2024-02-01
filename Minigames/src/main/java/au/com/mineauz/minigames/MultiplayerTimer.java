package au.com.mineauz.minigames;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.MinigamePlayerManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.sounds.MGSounds;
import au.com.mineauz.minigames.sounds.PlayMGSound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MultiplayerTimer {
    private static final Minigames plugin = Minigames.getPlugin();
    private final long oLobbyWaitTime;
    private final long oStartWaitTime;
    private final Minigame minigame;
    private final MinigamePlayerManager playerManager = plugin.getPlayerManager();
    private final List<Long> timeMsg = new ArrayList<>();
    private long currentLobbyWaitTime;
    private long startWaitTime;
    private boolean paused = false;
    private int taskID = -1;

    public MultiplayerTimer(Minigame mg) {
        minigame = mg;

        currentLobbyWaitTime = LobbySettingsModule.getMinigameModule(mg).getPlayerWaitTime();

        if (currentLobbyWaitTime == 0) {
            currentLobbyWaitTime = plugin.getConfig().getInt("multiplayer.waitforplayers");
            if (currentLobbyWaitTime <= 0)
                currentLobbyWaitTime = 10;
        }
        oLobbyWaitTime = currentLobbyWaitTime;
        startWaitTime = minigame.getStartWaitTime();  //minigames setting should be priority over general plugin config.
        if (startWaitTime == 0) {
            startWaitTime = plugin.getConfig().getInt("multiplayer.startcountdown");
            if (startWaitTime <= 0)
                startWaitTime = 5;
        }
        oStartWaitTime = startWaitTime;
        timeMsg.addAll(plugin.getConfig().getIntegerList("multiplayer.timerMessageInterval"));
    }

    public void startTimer() {
        if (taskID != -1)
            removeTimer();
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, this::doTimer, 1, 20L);
        taskID = task.getTaskId();
    }

    private void doTimer() {
        if (currentLobbyWaitTime != 0 && !paused) {
            if (currentLobbyWaitTime == oLobbyWaitTime) {

                MinigameMessageManager.sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_STARTUP_WAITINGFORPLAYERS), MinigameMessageType.INFO);
                MinigameMessageManager.sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_STARTUP_TIME,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(currentLobbyWaitTime))), MinigameMessageType.INFO);
                allowInteraction(LobbySettingsModule.getMinigameModule(minigame).canInteractPlayerWait());
                freezePlayers(!LobbySettingsModule.getMinigameModule(minigame).canMovePlayerWait());
                minigame.setState(MinigameState.WAITING);
            } else if (timeMsg.contains(currentLobbyWaitTime)) {
                MinigameMessageManager.sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_STARTUP_TIME,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(currentLobbyWaitTime))), MinigameMessageType.INFO);
                PlayMGSound.playSound(minigame, MGSounds.TIMER_TICK.getSound());
            }
        } else if (currentLobbyWaitTime == 0 && startWaitTime != 0 && !paused) {
            //wait time done game will start.
            if (startWaitTime == oStartWaitTime) {
                minigame.setState(MinigameState.STARTING);
                MinigameMessageManager.sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_STARTUP_MINIGAMESTARTS), MinigameMessageType.INFO);

                freezePlayers(!LobbySettingsModule.getMinigameModule(minigame).canMoveStartWait());
                allowInteraction(LobbySettingsModule.getMinigameModule(minigame).canInteractStartWait());

                if (LobbySettingsModule.getMinigameModule(minigame).isTeleportOnPlayerWait()) {
                    reclearInventories(minigame);
                    playerManager.balanceGame(minigame);
                    playerManager.getStartLocations(minigame.getPlayers(), minigame);
                    if (!minigame.isPlayersAtStart()) {
                        playerManager.teleportToStart(minigame);
                        minigame.setPlayersAtStart(true);
                    }
                }
            } else if (timeMsg.contains(startWaitTime)) {
                MinigameMessageManager.sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_STARTUP_TIME,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(startWaitTime))), MinigameMessageType.INFO);
                PlayMGSound.playSound(minigame, MGSounds.TIMER_TICK.getSound());
            }
        } else if (currentLobbyWaitTime == 0 && startWaitTime == 0) {
            //game should start..
            MinigameMessageManager.sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_STARTUP_GO), MinigameMessageType.INFO);
            reclearInventories(minigame);
            if (!LobbySettingsModule.getMinigameModule(minigame).isTeleportOnPlayerWait()) {
                playerManager.balanceGame(minigame);
                playerManager.getStartLocations(minigame.getPlayers(), minigame);
            }
            if (LobbySettingsModule.getMinigameModule(minigame).isTeleportOnStart()) {
                playerManager.startMPMinigame(minigame, true);
                if (!minigame.isPlayersAtStart()) {
                    playerManager.teleportToStart(minigame);
                    minigame.setPlayersAtStart(true);
                }
            } else {
                playerManager.startMPMinigame(minigame);
                if (!minigame.isPlayersAtStart()) {
                    Minigames.getCmpnntLogger().info("Minigame started and Players not teleported check configs:" + minigame.getName(false));
                }
            }
            freezePlayers(false);
            allowInteraction(true);

            if (minigame.getFloorDegen() != null) {
                minigame.addFloorDegenerator();
                minigame.getFloorDegenerator().startDegeneration();
            }

            if (minigame.getTimer() > 0) {
                minigame.setMinigameTimer(new MinigameTimer(minigame, minigame.getTimer()));
                MinigameMessageManager.sendMinigameMessage(minigame,
                        MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_TIMELEFT,
                                Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(Duration.ofSeconds(minigame.getTimer())))));
            }

            Bukkit.getScheduler().cancelTask(taskID);
        }

        if (!paused) {
            if (currentLobbyWaitTime != 0)
                currentLobbyWaitTime -= 1;
            else
                startWaitTime -= 1;
        }
    }

    private void reclearInventories(Minigame minigame) {
        for (MinigamePlayer ply : minigame.getPlayers()) {
            ply.getPlayer().getInventory().clear();
        }
    }

    private void freezePlayers(boolean freeze) {
        for (MinigamePlayer ply : minigame.getPlayers()) {
            ply.setFrozen(freeze);
        }
    }

    private void allowInteraction(boolean allow) {
        for (MinigamePlayer ply : minigame.getPlayers()) {
            ply.setCanInteract(allow);
        }
    }

    public long getPlayerWaitTimeLeft() {
        return currentLobbyWaitTime;
    }

    public int getStartWaitTimeLeft() {
        return startWaitTime;
    }

    public void setCurrentLobbyWaitTime(int time) {
        currentLobbyWaitTime = time;
    }

    public void setStartWaitTime(int time) {
        startWaitTime = time;
    }

    public void pauseTimer() {
        pauseTimer(Component.empty());
    }

    public void pauseTimer(Component reason) {
        paused = true;
        MinigameMessageManager.sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_STARTUP_PAUSED,
                Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), reason)), MinigameMessageType.INFO);
    }

    public void removeTimer() {
        if (taskID != -1) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }

    public void resumeTimer() {
        paused = false;
        MinigameMessageManager.sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_STARTUP_RESUMED), MinigameMessageType.INFO);
    }

    public boolean isPaused() {
        return paused;
    }
}
