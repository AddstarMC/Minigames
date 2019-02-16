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

public class BetSign implements MinigameSign {

    private static final Minigames plugin = Minigames.getPlugin();

    @Override
    public String getName() {
        return "Bet";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.bet";
    }

    @Override
    public String getCreatePermissionMessage() {
        return MinigameUtils.getLang("sign.bet.createPermission");
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.bet";
    }

    @Override
    public String getUsePermissionMessage() {
        return MinigameUtils.getLang("sign.bet.usePermission");
    }

    @Override
    public boolean signCreate(SignChangeEvent event) {
        if (plugin.getMinigameManager().hasMinigame(event.getLine(2))) {
            event.setLine(1, ChatColor.GREEN + "Bet");
            event.setLine(2, plugin.getMinigameManager().getMinigame(event.getLine(2)).getName(false));
            if (event.getLine(3).matches("[0-9]+")) {
                event.setLine(3, "$" + event.getLine(3));
            }
            return true;
        }
        event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("minigame.error.noMinigameName", event.getLine(2)));
        return false;
    }

    @Override
    public boolean signUse(Sign sign, MinigamePlayer player) {
        Minigame mgm = plugin.getMinigameManager().getMinigame(sign.getLine(2));
        if (mgm != null) {
            boolean invOk = true;
            boolean fullInv;
            boolean moneyBet = sign.getLine(3).startsWith("$");

            if (plugin.getConfig().getBoolean("requireEmptyInventory")) {
                fullInv = true;
                ItemStack[] contents = player.getPlayer().getInventory().getContents();
                for (int i = 0; i < contents.length; ++i) {
                    // Non money bets can hold an item
                    if (!moneyBet && i == player.getPlayer().getInventory().getHeldItemSlot()) {
                        continue;
                    }

                    if (contents[i] != null) {
                        invOk = false;
                        break;
                    }
                }

                for (ItemStack item : player.getPlayer().getInventory().getArmorContents()) {
                    if (item != null && item.getType() != Material.AIR) {
                        invOk = false;
                        break;
                    }
                }
            } else {
                fullInv = false;
                invOk = (moneyBet == (player.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR));
            }

            if (invOk) {
                if (mgm.isEnabled() && (!mgm.getUsePermissions() || player.getPlayer().hasPermission("minigame.join." + mgm.getName(false).toLowerCase()))) {
                    if (mgm.isSpectator(player)) {
                        return false;
                    }

                    if (!sign.getLine(3).startsWith("$")) {
                        plugin.getPlayerManager().joinMinigame(player, plugin.getMinigameManager().getMinigame(sign.getLine(2)), true, 0.0);
                    } else {
                        if (plugin.hasEconomy()) {
                            Double bet = Double.parseDouble(sign.getLine(3).replace("$", ""));
                            plugin.getPlayerManager().joinMinigame(player, plugin.getMinigameManager().getMinigame(sign.getLine(2)), true, bet);
                            return true;
                        } else {
                            player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noVault"), MinigameMessageType.ERROR);
                        }
                    }
                } else if (!mgm.isEnabled()) {
                    player.sendInfoMessage(MinigameUtils.getLang("minigame.error.notEnabled"));
                } else if (mgm.getUsePermissions()) {
                    player.sendInfoMessage(MinigameUtils.formStr("minigame.error.noPermission", "minigame.join." + mgm.getName(false).toLowerCase()));
                }
            } else if (!moneyBet) {
                if (fullInv && player.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) {
                    player.sendInfoMessage(MinigameUtils.getLang("sign.emptyInv"));
                } else {
                    player.sendMessage(MinigameUtils.getLang("sign.bet.noBet"), MinigameMessageType.ERROR);
                }
            } else {
                if (fullInv) {
                    player.sendInfoMessage(MinigameUtils.getLang("sign.emptyInv"));
                } else {
                    player.sendInfoMessage(MinigameUtils.getLang("sign.emptyHand"));
                }
            }
        } else {
            player.sendMessage(MinigameUtils.getLang("minigame.error.noMinigame"), MinigameMessageType.ERROR);
        }
        return false;
    }

    @Override
    public void signBreak(Sign sign, MinigamePlayer player) {

    }

}
