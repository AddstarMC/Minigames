package au.com.mineauz.minigames;

import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.RespawnModule;
import au.com.mineauz.minigames.sounds.MGSounds;
import au.com.mineauz.minigames.sounds.PlayMGSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 29/08/2017.
 */
public class RespawnTimer {
    private static Minigames plugin = Minigames.plugin;
    private Minigame mg;
    private MinigamePlayer ply;
    private Integer oRespawnTime;
    private Integer respawnTime;
    private int taskID = -1;
    private Location spawn;


    public RespawnTimer(Minigame mg, MinigamePlayer ply, Location spawn) {
        this.mg = mg;
        this.ply = ply;
        this.spawn = spawn;
        Flag<?> f = RespawnModule.getMinigameModule(mg).getFlags().get("timer");
        if (f.getFlag() != null && f.getFlag() instanceof Integer) {
            this.respawnTime = (Integer) f.getFlag();
        } else {
            this.respawnTime = 5;
        }
        this.oRespawnTime = respawnTime;
    }

    public void startTimer() {
        if (taskID != -1)
            removeTimer();
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            @Override
            public void run() {
                if (respawnTime.intValue() == oRespawnTime.intValue()) {
                    ply.sendMessage(ChatColor.GRAY + MinigameUtils.formStr("respawn.start", respawnTime));
                    if (!RespawnModule.getMinigameModule(mg).getCanMoveRespawnWait()) ply.setFrozen(true);
                    if (!RespawnModule.getMinigameModule(mg).getCanInteractRespawnWait()) ply.setCanInteract(false);

                } else if (respawnTime > 0) {
                    ply.sendMessage(ChatColor.GRAY + MinigameUtils.formStr("respawn.timeMsg", respawnTime));
                    PlayMGSound.playSound(mg, MGSounds.getSound("timerTick"));
                } else if (respawnTime == 0) {
                    ply.sendMessage(ChatColor.GREEN + MinigameUtils.getLang("respawn.go"));
                    ply.setCanInteract(true);
                    ply.setFrozen(false);
                    ply.teleport(spawn);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            ply.getPlayer().setNoDamageTicks(60);
                        }
                    });
                    Bukkit.getScheduler().cancelTask(taskID);
                }
                if (respawnTime != 0)
                    respawnTime -= 1;
            }
        }, 0, 20);

    }

    public void removeTimer() {
        if (taskID != -1) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }
}
