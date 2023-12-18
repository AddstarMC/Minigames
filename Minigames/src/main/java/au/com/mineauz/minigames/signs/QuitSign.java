package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

public class QuitSign implements MinigameSign {

    private static final Minigames plugin = Minigames.getPlugin();

    @Override
    public @NotNull String getName() {
        return "Quit";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.quit";
    }

    @Override
    public String getUsePermission() {
        return null;
    }

    @Override
    public boolean signCreate(@NotNull SignChangeEvent event) {
        event.setLine(1, ChatColor.GREEN + "Quit");
        return true;
    }

    @Override
    public boolean signUse(@NotNull Sign sign, @NotNull MinigamePlayer mgPlayer) {
        if (mgPlayer.isInMinigame() && mgPlayer.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
            plugin.getPlayerManager().quitMinigame(mgPlayer, false);
            return true;
        } else if (mgPlayer.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.SIGN_ERROR_EMPTYHAND);
        }
        return false;
    }

    @Override
    public void signBreak(@NotNull Sign sign, MinigamePlayer mgPlayer) {

    }

}
