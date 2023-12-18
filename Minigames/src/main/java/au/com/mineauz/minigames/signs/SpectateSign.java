package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

public class SpectateSign implements MinigameSign {

    private final Minigames plugin = Minigames.getPlugin();

    @Override
    public @NotNull String getName() {
        return "Spectate";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.spectate";
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.spectate";
    }

    @Override
    public boolean signCreate(@NotNull SignChangeEvent event) {
        if (plugin.getMinigameManager().hasMinigame(event.getLine(2))) {
            event.setLine(1, ChatColor.GREEN + "Spectate");
            event.setLine(2, plugin.getMinigameManager().getMinigame(event.getLine(2)).getName(false));
            return true;
        }

        MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                Placeholder.component(MinigamePlaceHolderKey.MINIGAME.getKey(), event.line(2)));
        return false;
    }

    @Override
    public boolean signUse(@NotNull Sign sign, @NotNull MinigamePlayer mgPlayer) {
        if (mgPlayer.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR && !mgPlayer.isInMinigame()) {
            Minigame mgm = plugin.getMinigameManager().getMinigame(sign.getSide(Side.FRONT).getLine(2));
            if (mgm != null) {
                if (mgm.isEnabled()) {
                    plugin.getPlayerManager().spectateMinigame(mgPlayer, mgm);
                    return true;
                } else if (!mgm.isEnabled()) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOTENABLED);
                }
            } else {
                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME);
            }
        } else if (!mgPlayer.isInMinigame()) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.SIGN_ERROR_EMPTYHAND);
        }
        return false;
    }

    @Override
    public void signBreak(@NotNull Sign sign, MinigamePlayer mgPlayer) {

    }

}
