package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public class QuitSign implements MinigameSign {

    private static Minigames plugin = Minigames.getPlugin();

    @Override
    public String getName() {
        return "Quit";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.quit";
    }

    @Override
    public String getCreatePermissionMessage() {
        return MinigameUtils.getLang("sign.quit.createPermission");
    }

    @Override
    public String getUsePermission() {
        return null;
    }

    @Override
    public String getUsePermissionMessage() {
        return null;
    }

    @Override
    public boolean signCreate(SignChangeEvent event) {
        event.setLine(1, ChatColor.GREEN + "Quit");
        return true;
    }

    @Override
    public boolean signUse(Sign sign, MinigamePlayer player) {
        if (player.isInMinigame() && player.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
            plugin.getPlayerManager().quitMinigame(player, false);
            return true;
        } else if (player.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR)
            player.sendInfoMessage(MinigameUtils.getLang("sign.emptyHand"));
        return false;
    }

    @Override
    public void signBreak(Sign sign, MinigamePlayer player) {

    }

}
