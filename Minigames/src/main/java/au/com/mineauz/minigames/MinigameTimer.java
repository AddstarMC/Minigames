package au.com.mineauz.minigames;

import au.com.mineauz.minigames.events.MinigameTimerTickEvent;
import au.com.mineauz.minigames.events.TimerExpireEvent;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.sounds.MGSounds;
import au.com.mineauz.minigames.sounds.PlayMGSound;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class MinigameTimer {
    private static final Minigames plugin = Minigames.getPlugin();
    private final long timeLength;
    private final Minigame minigame;
    private final List<Integer> timeMsg = new ArrayList<>();
    private long timeLeft = 0;
    private int taskID = -1;
    private boolean broadcastTime = true;

    public MinigameTimer(Minigame minigame, long timeLength) {
        this.timeLength = timeLength;
        this.timeLeft = timeLength;
        this.minigame = minigame;
        timeMsg.addAll(plugin.getConfig().getIntegerList("multiplayer.timerMessageInterval"));
        startTimer();
    }

    public boolean isBroadcastingTime() {
        return broadcastTime;
    }

    public void setBroadcastTime(boolean bool) {
        broadcastTime = bool;
    }

    public void startTimer() {
        stopTimer();

        //a delay of 1 is used because bukkit doesn't guarantee that it will run on the current tick if the scheduler has
        // already run that tick . In that case it runs next tick - a delay of 1 means the behaviour is consistent.
        /// this effectively means the timer runs 50ms behind expected.
        taskID = Bukkit.getScheduler().runTaskTimer(plugin, this::runTimer, 1L, 20L).getTaskId();
    }

    private void runTimer() {
        timeLeft--;
        if (minigame.isUsingXPBarTimer()) {
            float timeLeftpercent = ((float) timeLeft) / ((float) timeLength);
            int level;
            if (timeLeft / 60 > 0) {
                level = timeLeft / 60;
            } else {
                level = timeLeft;
            }

            for (MinigamePlayer ply : minigame.getPlayers()) {
                if (timeLeftpercent < 0) {
                    ply.getPlayer().setExp(0);
                    ply.getPlayer().setLevel(0);
                } else {
                    ply.getPlayer().setExp(timeLeftpercent);
                    ply.getPlayer().setLevel(level);
                }
            }
        }
        if (timeMsg.contains(timeLeft) && broadcastTime) {
            PlayMGSound.playSound(minigame, MGSounds.TIMER_TICK.getSound());
            plugin.getMinigameManager().sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_TIMELEFT,
                    Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(timeLeft))));
        }

        if (timeLeft <= 0) {
            Bukkit.getServer().getPluginManager().callEvent(new TimerExpireEvent(minigame));
            stopTimer();
        }

        if (timeLeft > 0) {
            Bukkit.getPluginManager().callEvent(new MinigameTimerTickEvent(minigame, minigame.getMinigameTimer()));
        }
    }

    public void stopTimer() {
        if (taskID != -1) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int time) {
        this.timeLeft = time;
    }
}
