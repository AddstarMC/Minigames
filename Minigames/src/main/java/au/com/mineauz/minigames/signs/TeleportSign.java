package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

public class TeleportSign implements MinigameSign {

    @Override
    public @NotNull String getName() {
        return "Teleport";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.teleport";
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.teleport";
    }

    @Override
    public boolean signCreate(@NotNull SignChangeEvent event) {
        event.setLine(1, ChatColor.GREEN + "Teleport");
        if (event.getLine(2).isEmpty()) {
            return false;
        } else {
            return event.getLine(2).matches("-?[0-9]+,[0-9]+,-?[0-9]+");
        }
    }

    @Override
    public boolean signUse(@NotNull Sign sign, @NotNull MinigamePlayer mgPlayer) {
        if (!sign.getLine(2).isEmpty() && sign.getLine(2).matches("-?[0-9]+,[0-9]+,-?[0-9]+")) {
            int x;
            int y;
            int z;
            String[] split = sign.getLine(2).split(",");
            x = Integer.parseInt(split[0]);
            y = Integer.parseInt(split[1]);
            z = Integer.parseInt(split[2]);

            if (!sign.getLine(3).isEmpty() && sign.getLine(3).matches("-?[0-9]+,-?[0-9]+")) {
                float yaw;
                float pitch;
                String[] split2 = sign.getLine(3).split(",");
                yaw = Float.parseFloat(split2[0]);
                pitch = Float.parseFloat(split2[1]);
                mgPlayer.teleport(new Location(mgPlayer.getPlayer().getWorld(), x + 0.5, y, z + 0.5, yaw, pitch));
                return true;
            }
            mgPlayer.teleport(new Location(mgPlayer.getPlayer().getWorld(), x + 0.5, y, z + 0.5));
            return true;
        }
        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.SIGN_TELEPORT_INVALID);
        return false;
    }

    @Override
    public void signBreak(@NotNull Sign sign, MinigamePlayer mgPlayer) {

    }

}
