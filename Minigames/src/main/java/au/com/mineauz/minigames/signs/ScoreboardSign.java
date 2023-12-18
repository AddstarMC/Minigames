package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardDisplay;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

public class ScoreboardSign implements MinigameSign {
    private final Minigames plugin = Minigames.getPlugin();

    @Override
    public @NotNull String getName() {
        return "Scoreboard";
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
        if (event.getBlock().getState().getBlockData() instanceof WallSign sign) {
            // Parse minigame
            Minigame minigame;

            if (plugin.getMinigameManager().hasMinigame(event.getLine(2))) {
                minigame = plugin.getMinigameManager().getMinigame(event.getLine(2));
            } else {
                MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                        Placeholder.component(MinigamePlaceHolderKey.MINIGAME.getKey(), event.line(2)));
                return false;
            }

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

            BlockFace facing = sign.getFacing();

            // Add our display
            ScoreboardDisplay display = new ScoreboardDisplay(minigame, width, height,
                    event.getBlock().getLocation(), facing);
            display.placeSigns(sign.getMaterial());

            minigame.getScoreboardData().addDisplay(display);

            // Reformat this sign for the next part
            event.setLine(1, ChatColor.GREEN + "Scoreboard");
            event.setLine(2, minigame.getName(false));

            event.getBlock().setMetadata("Minigame", new FixedMetadataValue(plugin, minigame));
            return true;
        } else {
            MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.SIGN_SCOREBOARD_ERROR_WALL);
            return false;
        }

    }

    @Override
    public boolean signUse(@NotNull Sign sign, @NotNull MinigamePlayer mgPlayer) {
        Minigame minigame = plugin.getMinigameManager().getMinigame(sign.getLine(2));
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
