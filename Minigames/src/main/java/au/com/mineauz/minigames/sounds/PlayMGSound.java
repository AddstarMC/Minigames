package au.com.mineauz.minigames.sounds;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class PlayMGSound {

    private static final boolean shouldPlay = Minigames.getPlugin().getConfig().getBoolean("playSounds");

    public static void playSound(@NotNull MinigamePlayer mgPlayer, @NotNull MGSound sound) {
        if (!shouldPlay) return;

        if (sound.getCount() == 1)
            mgPlayer.getPlayer().playSound(mgPlayer.getLocation(), sound.getSound(), sound.getVolume(), sound.getPitch());
        else {
            playLoop(mgPlayer, sound.clone());
        }
    }

    private static void playLoop(final @NotNull MinigamePlayer mgPlayer, final @NotNull MGSound sound) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.getPlugin(), () -> {
            mgPlayer.getPlayer().playSound(mgPlayer.getLocation(), sound.getSound(), sound.getVolume(), sound.getPitch());
            sound.setTimesPlayed(sound.getTimesPlayed() + 1);
            if (sound.getTimesPlayed() < sound.getCount())
                playLoop(mgPlayer, sound);
        }, sound.getDelay());
    }

    public static void playSound(@NotNull Minigame minigame, @NotNull MGSound sound) {
        for (MinigamePlayer mgPlayer : minigame.getPlayers())
            playSound(mgPlayer, sound);
    }

    public static void playSound(@NotNull Team team, @NotNull MGSound sound) {
        for (MinigamePlayer player : team.getPlayers())
            playSound(player, sound);
    }
}
