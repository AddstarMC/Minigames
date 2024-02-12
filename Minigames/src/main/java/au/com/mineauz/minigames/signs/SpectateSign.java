package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgSignLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SpectateSign extends AMinigameSign {
    private final Minigames plugin = Minigames.getPlugin();

    @Override
    public @NotNull Component getName() {
        return MinigameMessageManager.getMgMessage(MgSignLangKey.TYPE_SPECTATE);
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
        final Sign sign = (Sign) event.getBlock().getState();
        final Minigame minigame = getMinigame(sign, event.line(2));

        if (minigame != null) {
            event.line(1, getName());

            event.line(2, minigame.getDisplayName());
            setPersistentMinigame(sign, minigame);
            return true;
        } else {
            MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                    Placeholder.component(MinigamePlaceHolderKey.MINIGAME.getKey(), Objects.requireNonNullElse(event.line(2), Component.empty())));
            return false;
        }
    }

    @Override
    public boolean signUse(@NotNull Sign sign, @NotNull MinigamePlayer mgPlayer) {
        if (mgPlayer.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR && !mgPlayer.isInMinigame()) {
            Minigame mgm = getMinigame(sign);
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
