package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

public class JoinSign implements MinigameSign {

    private static Minigames plugin = Minigames.getPlugin();

    @Override
    public String getName() {
        return "Join";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.join";
    }

    @Override
    public String getCreatePermissionMessage() {
        return MinigameUtils.getLang("sign.join.createPermission");
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.join";
    }

    @Override
    public String getUsePermissionMessage() {
        return MinigameUtils.getLang("sign.join.usePermission");
    }

    @Override
    public boolean signCreate(SignChangeEvent event) {
        if (plugin.getMinigameManager().hasMinigame(event.getLine(2))) {
            event.setLine(1, ChatColor.GREEN + "Join");
            event.setLine(2, plugin.getMinigameManager().getMinigame(event.getLine(2)).getName(false));
            if (Minigames.getPlugin().hasEconomy()) {
                if (!event.getLine(3).isEmpty() && !event.getLine(3).matches("\\$?[0-9]+(.[0-9]{2})?")) {
                    event.getPlayer().sendMessage(ChatColor.RED + MinigameUtils.getLang("sign.join.invalidMoney"));
                    return false;
                } else if (event.getLine(3).matches("[0-9]+(.[0-9]{2})?")) {
                    event.setLine(3, "$" + event.getLine(3));
                }
            } else {
                event.setLine(3, "");
                event.getPlayer().sendMessage(ChatColor.RED + MinigameUtils.getLang("minigame.error.noVault"));
            }
            return true;
        }
        event.getPlayer().sendMessage(ChatColor.RED + MinigameUtils.formStr("minigame.error.noMinigameName", event.getLine(2)));
        return false;
    }

    @Override
    public boolean signUse(Sign sign, MinigamePlayer player) {
        if (player.isInMinigame()) {
            return false;
        }

        boolean invOk = true;
        boolean fullInv;
        if (plugin.getConfig().getBoolean("requireEmptyInventory")) {
            fullInv = true;
            for (ItemStack item : player.getPlayer().getInventory().getContents()) {
                if (item != null) {
                    System.out.println("Found: " + item);
                    invOk = false;
                    break;
                }
            }

            for (ItemStack item : player.getPlayer().getInventory().getArmorContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    System.out.println("Found armor: " + item);
                    invOk = false;
                    break;
                }
            }
        } else {
            fullInv = false;
            invOk = player.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR;
        }
        if (invOk) {
            Minigame mgm = plugin.getMinigameManager().getMinigame(sign.getLine(2));
            if (mgm != null && (!mgm.getUsePermissions() ||
                    player.getPlayer().hasPermission("minigame.join." + mgm.getName(false).toLowerCase()))) {
                if (mgm.isEnabled()) {
                    if (!sign.getLine(3).isEmpty() && Minigames.getPlugin().hasEconomy()) {
                        double amount = Double.parseDouble(sign.getLine(3).replace("$", ""));
                        if (Minigames.getPlugin().getEconomy().getBalance(player.getPlayer().getPlayer()) >= amount) {
                            Minigames.getPlugin().getEconomy().withdrawPlayer(player.getPlayer().getPlayer(), amount);
                        } else {
                            player.sendMessage(MinigameUtils.getLang("sign.join.notEnoughMoney"), MinigameMessageType.ERROR);
                            return false;
                        }
                    }
                    plugin.getPlayerManager().joinMinigame(player, mgm, false, 0.0);
                    return true;
                } else if (!mgm.isEnabled()) {
                    player.sendInfoMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.notEnabled"));
                }
            } else if (mgm == null) {
                player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noMinigame"), MinigameMessageType.ERROR);
            } else if (mgm.getUsePermissions()) {
                player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("minigame.error.noPermission", "minigame.join." + mgm.getName(false).toLowerCase()), MinigameMessageType.ERROR);
            }
        } else if (!MinigameUtils.isMinigameTool(player.getPlayer().getInventory().getItemInMainHand())) {
            if (fullInv) {
                player.sendInfoMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.emptyInv"));
            } else {
                player.sendInfoMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.emptyHand"));
            }
        }

        return false;
    }

    @Override
    public void signBreak(Sign sign, MinigamePlayer player) {

    }

}
