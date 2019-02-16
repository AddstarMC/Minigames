package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.events.TakeFlagEvent;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public class FlagSign implements MinigameSign {

    @Override
    public String getName() {
        return "Flag";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.flag";
    }

    @Override
    public String getCreatePermissionMessage() {
        return MinigameUtils.getLang("sign.flag.createPermission");
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
        event.setLine(1, ChatColor.GREEN + "Flag");
        if (TeamColor.matchColor(event.getLine(2)) != null) {
            TeamColor col = TeamColor.matchColor(event.getLine(2));
            event.setLine(2, col.getColor() + MinigameUtils.capitalize(col.toString()));
        } else if (event.getLine(2).equalsIgnoreCase("neutral")) {
            event.setLine(2, ChatColor.GRAY + "Neutral");
        } else if (event.getLine(2).equalsIgnoreCase("capture") && !event.getLine(3).isEmpty()) {
            event.setLine(2, ChatColor.GREEN + "Capture");
            if (TeamColor.matchColor(event.getLine(3)) != null) {
                TeamColor col = TeamColor.matchColor(event.getLine(3));
                event.setLine(3, col.getColor() + MinigameUtils.capitalize(col.toString()));
            } else if (event.getLine(3).equalsIgnoreCase("neutral")) {
                event.setLine(3, ChatColor.GRAY + "Neutral");
            } else {
                event.getBlock().breakNaturally();
                event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.flag.invalidSyntax") + " red, blue and neutral.");
                return false;
            }
        }
//        else{
//            event.getBlock().breakNaturally();
//            event.getPlayer().sendInfoMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.flag.invalidSyntax") + " red, blue and neutral.");
//            return false;
//        }
        return true;
    }

    @Override
    public boolean signUse(Sign sign, MinigamePlayer player) {
        if (player.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR && player.isInMinigame()) {
            Minigame mgm = player.getMinigame();

            if (mgm.isSpectator(player)) {
                return false;
            }
            if (!sign.getLine(2).isEmpty() && player.getPlayer().isOnGround() &&
                    !mgm.getMechanicName().equals("ctf") &&
                    !player.hasFlag(ChatColor.stripColor(sign.getLine(2)))) {
                TakeFlagEvent ev = new TakeFlagEvent(mgm, player, ChatColor.stripColor(sign.getLine(2)));
                Bukkit.getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    player.addFlag(ChatColor.stripColor(sign.getLine(2)));
                    player.sendInfoMessage(MinigameUtils.formStr("sign.flag.taken", ChatColor.stripColor(sign.getLine(2))));
                }
                return true;
            }
        } else if (player.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR)
            player.sendInfoMessage(MinigameUtils.getLang("sign.emptyHand"));
        return false;
    }

    @Override
    public void signBreak(Sign sign, MinigamePlayer player) {

    }

}
