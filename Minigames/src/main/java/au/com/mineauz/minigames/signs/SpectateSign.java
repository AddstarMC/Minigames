package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public class SpectateSign implements MinigameSign {

    private Minigames plugin = Minigames.getPlugin();

    @Override
    public String getName() {
        return "Spectate";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.spectate";
    }

    @Override
    public String getCreatePermissionMessage() {
        return MinigameUtils.getLang("sign.spectate.createPermission");
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.spectate";
    }

    @Override
    public String getUsePermissionMessage() {
        return MinigameUtils.getLang("sign.spectate.usePermission");
    }

    @Override
    public boolean signCreate(SignChangeEvent event) {
        if (plugin.getMinigameManager().hasMinigame(event.getLine(2))) {
            event.setLine(1, ChatColor.GREEN + "Spectate");
            event.setLine(2, plugin.getMinigameManager().getMinigame(event.getLine(2)).getName(false));
            return true;
        }
        event.getPlayer().sendMessage(ChatColor.RED + MinigameUtils.formStr("minigame.error.noMinigameName", event.getLine(2)));
        return false;
    }

    @Override
    public boolean signUse(Sign sign, MinigamePlayer player) {
        if (player.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR && !player.isInMinigame()) {
            Minigame mgm = plugin.getMinigameManager().getMinigame(sign.getLine(2));
            if (mgm != null) {
                if (mgm.isEnabled()) {
                    plugin.getPlayerManager().spectateMinigame(player, mgm);
                    return true;
                } else if (!mgm.isEnabled()) {
                    player.sendInfoMessage(MinigameUtils.getLang("minigame.error.notEnabled"));
                }
            } else if (mgm == null) {
                player.sendInfoMessage(MinigameUtils.getLang("minigame.error.noMinigame"));
            }
        } else if (!player.isInMinigame())
            player.sendInfoMessage(MinigameUtils.getLang("sign.emptyHand"));
        return false;
    }

    @Override
    public void signBreak(Sign sign, MinigamePlayer player) {

    }

}
