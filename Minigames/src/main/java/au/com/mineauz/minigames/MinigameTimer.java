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
    private final Minigame minigame;
    private final List<Integer> timeMsg = new ArrayList<>();
    private long time = 0;
    private long otime = 0;
    private int taskID = -1;
    private boolean broadcastTime = true;

    public MinigameTimer(Minigame minigame, long time) {
        this.time = time;
        otime = time;
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
        if (taskID != -1)
            stopTimer();
        //a delay of 1 is used because bukkit doesn't guarantee that it will run on the current tick if the scheduler has
        // already run that tick . In that case it runs next tick - a delay of 1 means the behaviour is consistent.
        /// this effectively means the timer runs 50ms behind expected.
        taskID = Bukkit.getScheduler().runTaskTimer(plugin, this::runTimer, 1L, 20L).getTaskId();
    }

    private void runTimer() {
        time--;
        if (minigame.isUsingXPBarTimer()) {
            float timeper = ((Integer) time).floatValue() / ((Integer) otime).floatValue();
            int level = 0;
            if (time / 60 > 0)
                level = time / 60;
            else
                level = time;

            for (MinigamePlayer mgPlayer : minigame.getPlayers()) {
                if (timeper < 0) {
                    mgPlayer.getPlayer().setExp(0);
                    mgPlayer.getPlayer().setLevel(0);
                } else {
                    mgPlayer.getPlayer().setExp(timeper);
                    mgPlayer.getPlayer().setLevel(level);
                }
            }
        }
        if (timeMsg.contains(time) && broadcastTime) {
            PlayMGSound.playSound(minigame, MGSounds.TIMER_TICK.getSound());
            MinigameMessageManager.sendMinigameMessage(minigame, MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_TIMELEFT,
                    Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(time))));
        }

        if (time <= 0) {
            Bukkit.getServer().getPluginManager().callEvent(new TimerExpireEvent(minigame));
            stopTimer();
        }

        if (time > 0) {
            Bukkit.getPluginManager().callEvent(new MinigameTimerTickEvent(minigame, minigame.getMinigameTimer()));
        }
    }

    public void stopTimer() {
        if (taskID != -1) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }

    public long getTimeLeft() {
        return time;
    }

    public void setTimeLeft(int time) {
        this.time = time;
    }
}
