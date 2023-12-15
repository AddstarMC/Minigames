package au.com.mineauz.minigames;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.MinigamePlayerManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
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

import java.util.ArrayList;
import java.util.List;

public class MultiplayerTimer {
    private static final Minigames plugin = Minigames.getPlugin();
    private final int oLobbyWaitTime;
    private final int oStartWaitTime;
    private final Minigame minigame;
    private final MinigamePlayerManager playerManager = plugin.getPlayerManager();
    private final List<Integer> timeMsg = new ArrayList<>();
    private int currentLobbyWaitTime;
    private int startWaitTime;
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

                plugin.getMinigameManager().sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_STARTUP_WAITINGFORPLAYERS), MinigameMessageType.INFO);
                plugin.getMinigameManager().sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_STARTUP_TIME,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(currentLobbyWaitTime))), MinigameMessageType.INFO);
                allowInteraction(LobbySettingsModule.getMinigameModule(minigame).canInteractPlayerWait());
                freezePlayers(!LobbySettingsModule.getMinigameModule(minigame).canMovePlayerWait());
                minigame.setState(MinigameState.WAITING);
            } else if (timeMsg.contains(currentLobbyWaitTime)) {
                plugin.getMinigameManager().sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_STARTUP_TIME,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(currentLobbyWaitTime))), MinigameMessageType.INFO);
                PlayMGSound.playSound(minigame, MGSounds.getSound("timerTick"));
            }
        } else if (currentLobbyWaitTime == 0 && startWaitTime != 0 && !paused) {
            //wait time done game will start.
            if (startWaitTime == oStartWaitTime) {
                minigame.setState(MinigameState.STARTING);
                plugin.getMinigameManager().sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_STARTUP_MINIGAMESTARTS), MinigameMessageType.INFO);

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
                plugin.getMinigameManager().sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_STARTUP_TIME,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(startWaitTime))), MinigameMessageType.INFO);
                PlayMGSound.playSound(minigame, MGSounds.getSound("timerTick"));
            }
        } else if (currentLobbyWaitTime == 0 && startWaitTime == 0) {
            //game should start..
            plugin.getMinigameManager().sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_STARTUP_GO), MinigameMessageType.INFO);
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
                    Minigames.getPlugin().getLogger().info("Minigame started and Players not teleported check configs:" + minigame.getName(false));
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
                plugin.getMinigameManager().sendMinigameMessage(minigame,
                        MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_TIMELEFT,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(minigame.getTimer()))));
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

    public int getPlayerWaitTimeLeft() {
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
        plugin.getMinigameManager().sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_STARTUP_PAUSED,
                Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), reason)), MinigameMessageType.INFO);
    }

    public void removeTimer() {
        if (taskID != -1) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }

    public void resumeTimer() {
        paused = false;
        plugin.getMinigameManager().sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_STARTUP_RESUMED), MinigameMessageType.INFO);
    }

    public boolean isPaused() {
        return paused;
    }
}
