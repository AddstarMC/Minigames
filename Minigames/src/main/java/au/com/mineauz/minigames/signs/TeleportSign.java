package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public class TeleportSign implements MinigameSign {

    @Override
    public String getName() {
        return "Teleport";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.teleport";
    }

    @Override
    public String getCreatePermissionMessage() {
        return MinigameUtils.getLang("sign.teleport.createPermission");
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.teleport";
    }

    @Override
    public String getUsePermissionMessage() {
        return MinigameUtils.getLang("sign.teleport.usePermission");
    }

    @Override
    public boolean signCreate(SignChangeEvent event) {
        event.setLine(1, ChatColor.GREEN + "Teleport");
        if (event.getLine(2).isEmpty()) {
            return false;
        } else {
            return event.getLine(2).matches("-?[0-9]+,[0-9]+,-?[0-9]+");
        }
    }

    @Override
    public boolean signUse(Sign sign, MinigamePlayer player) {
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
                player.teleport(new Location(player.getPlayer().getWorld(), x + 0.5, y, z + 0.5, yaw, pitch));
                return true;
            }
            player.teleport(new Location(player.getPlayer().getWorld(), x + 0.5, y, z + 0.5));
            return true;
        }
        player.sendMessage(MinigameUtils.getLang("sign.teleport.invalid"), MinigameMessageType.ERROR);
        return false;
    }

    @Override
    public void signBreak(Sign sign, MinigamePlayer player) {

    }

}
