package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public class CheckpointSign implements MinigameSign {

    @Override
    public String getName() {
        return "Checkpoint";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.checkpoint";
    }

    @Override
    public String getCreatePermissionMessage() {
        return MinigameUtils.getLang("sign.checkpoint.createPermission");
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.checkpoint";
    }

    @Override
    public String getUsePermissionMessage() {
        return MinigameUtils.getLang("sign.checkpoint.usePermission");
    }

    @Override
    public boolean signCreate(SignChangeEvent event) {
        event.setLine(1, ChatColor.GREEN + "Checkpoint");
        if (event.getLine(2).equalsIgnoreCase("global")) {
            event.setLine(2, ChatColor.BLUE + "Global");
        }
        return true;
    }

    @Override
    public boolean signUse(Sign sign, MinigamePlayer player) {
        if ((player.isInMinigame() || (!player.isInMinigame() && sign.getLine(2).equals(ChatColor.BLUE + "Global")))
                && player.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
            if (player.isInMinigame() && player.getMinigame().isSpectator(player)) {
                return false;
            }
            if (player.getPlayer().isOnGround()) {
                Location newloc = player.getPlayer().getLocation();
                if (!sign.getLine(2).equals(ChatColor.BLUE + "Global")) {
                    player.setCheckpoint(newloc);
                } else {
                    player.getStoredPlayerCheckpoints().setGlobalCheckpoint(newloc);
                }

                player.sendInfoMessage(MinigameUtils.getLang("sign.checkpoint.set"));
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.checkpoint.fail"), MinigameMessageType.ERROR);
            }
        } else
            player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.emptyHand"), MinigameMessageType.INFO);
        return false;
    }

    @Override
    public void signBreak(Sign sign, MinigamePlayer player) {

    }

}
