package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgSignLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardDisplay;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

public class ScoreboardSign extends AMinigameSign {
    private final Minigames plugin = Minigames.getPlugin();

    @Override
    public @NotNull Component getName() {
        return MinigameMessageManager.getMgMessage(MgSignLangKey.TYPE_SCOREBOARD);
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.scoreboard";
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.scoreboard";
    }

    @Override
    public boolean signCreate(@NotNull SignChangeEvent event) {
        if (event.getBlock().getState().getBlockData() instanceof WallSign signData) {
            // Parse minigame
            Sign signState = (Sign) event.getBlock().getState();
            Minigame minigame = getMinigame(signState, event.line(2));

            if (minigame != null) {
                // Parse size
                int width;
                int height;

                if (event.getLine(3).isEmpty()) {
                    width = ScoreboardDisplay.defaultWidth;
                    height = ScoreboardDisplay.defaultHeight;
                } else if (event.getLine(3).matches("[0-9]+x[0-9]+")) {
                    String[] parts = event.getLine(3).split("x");
                    width = Integer.parseInt(parts[0]);
                    height = Integer.parseInt(parts[1]);
                } else {
                    MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.SIGN_SCOREBOARD_ERROR_SIZE);
                    return false;
                }

                // So we don't have to deal with even size scoreboards
                if (width % 2 == 0) {
                    MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.SIGN_SCOREBOARD_ERROR_UNEVENLENGTH);
                    return false;
                }

                BlockFace facing = signData.getFacing();

                // Add our display
                ScoreboardDisplay display = new ScoreboardDisplay(minigame, width, height,
                        event.getBlock().getLocation(), facing);
                display.placeSigns(signData.getMaterial());

                minigame.getScoreboardData().addDisplay(display);

                // Reformat this sign for the next part
                event.line(1, getName());
                event.line(2, minigame.getDisplayName());
                setPersistentMinigame(signState, minigame);

                event.getBlock().setMetadata("Minigame", new FixedMetadataValue(plugin, minigame));
                return true;
            } else {
                MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.SIGN_SCOREBOARD_ERROR_WALL);
                return false;
            }
        } else {
            MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                    Placeholder.component(MinigamePlaceHolderKey.MINIGAME.getKey(), event.line(2)));
            return false;
        }

    }

    @Override
    public boolean signUse(@NotNull Sign sign, @NotNull MinigamePlayer mgPlayer) {
        Minigame minigame = getMinigame(sign);
        if (minigame == null) {
            return false;
        }

        ScoreboardDisplay display = minigame.getScoreboardData().getDisplay(sign.getBlock());
        if (display == null) {
            return false;
        }

        display.displayMenu(mgPlayer);

        return false;
    }

    @Override
    public void signBreak(@NotNull Sign sign, MinigamePlayer mgPlayer) {
        Minigame minigame = (Minigame) sign.getBlock().getMetadata("Minigame").get(0).value();
        if (minigame != null) {
            minigame.getScoreboardData().removeDisplay(sign.getBlock());
        }
    }

}
