package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BetSign implements MinigameSign {

    private static final Minigames plugin = Minigames.getPlugin();

    @Override
    public @NotNull String getName() {
        return "Bet";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.bet";
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.bet";
    }

    @Override
    public boolean signCreate(@NotNull SignChangeEvent event) {
        if (plugin.getMinigameManager().hasMinigame(event.getLine(2))) {
            event.setLine(1, ChatColor.GREEN + "Bet");
            event.setLine(2, plugin.getMinigameManager().getMinigame(event.getLine(2)).getName(false));
            if (event.getLine(3).matches("[0-9]+")) {
                //todo use plugin.getEconomy().currencyNamePlural()
                event.setLine(3, "$" + event.getLine(3));
            }
            return true;
        }
        MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                Placeholder.component(MinigamePlaceHolderKey.MINIGAME.getKey(), event.line(2)));
        return false;
    }

    @Override
    public boolean signUse(@NotNull Sign sign, @NotNull MinigamePlayer mgPlayer) {
        Minigame mgm = plugin.getMinigameManager().getMinigame(sign.getSide(Side.FRONT).getLine(2));
        if (mgm != null) {
            boolean invOk = true;
            boolean fullInv;
            boolean moneyBet = sign.getLine(3).startsWith("$");

            if (plugin.getConfig().getBoolean("requireEmptyInventory")) {
                fullInv = true;
                ItemStack[] contents = mgPlayer.getPlayer().getInventory().getContents();
                for (int i = 0; i < contents.length; ++i) {
                    // Non money bets can hold an item
                    if (!moneyBet && i == mgPlayer.getPlayer().getInventory().getHeldItemSlot()) {
                        continue;
                    }

                    if (contents[i] != null) {
                        invOk = false;
                        break;
                    }
                }

                for (ItemStack item : mgPlayer.getPlayer().getInventory().getArmorContents()) {
                    if (item != null && item.getType() != Material.AIR) {
                        invOk = false;
                        break;
                    }
                }
            } else {
                fullInv = false;
                invOk = (moneyBet == (mgPlayer.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR));
            }

            if (invOk) {
                if (mgm.isEnabled() && (!mgm.getUsePermissions() || mgPlayer.getPlayer().hasPermission("minigame.join." + mgm.getName(false).toLowerCase()))) {
                    if (mgm.isSpectator(mgPlayer)) {
                        return false;
                    }

                    if (!sign.getLine(3).startsWith("$")) {
                        plugin.getPlayerManager().joinMinigame(mgPlayer, plugin.getMinigameManager().getMinigame(sign.getLine(2)), true, 0.0);
                    } else {
                        if (plugin.hasEconomy()) {
                            //todo use  plugin.getEconomy().currencyNamePlural()
                            Double bet = Double.parseDouble(sign.getLine(3).replace("$", ""));
                            plugin.getPlayerManager().joinMinigame(mgPlayer, plugin.getMinigameManager().getMinigame(sign.getLine(2)), true, bet);
                            return true;
                        } else if (plugin.getConfig().getBoolean("warnings")) {
                            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.WARNING, MinigameLangKey.MINIGAME_WARNING_NOVAULT);
                        }
                    }
                } else if (!mgm.isEnabled()) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOTENABLED);
                } else if (mgm.getUsePermissions()) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOPERMISSION);
                }
            } else if (!moneyBet) {
                if (fullInv && mgPlayer.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.SIGN_ERROR_FULLINV);
                } else {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.PLAYER_BET_ERROR_NOBET);
                }
            } else {
                if (fullInv) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.SIGN_ERROR_FULLINV);
                } else {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.SIGN_ERROR_EMPTYHAND);
                }
            }
        } else {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                    Placeholder.component(MinigamePlaceHolderKey.MINIGAME.getKey(), sign.getSide(Side.FRONT).line(2)));
        }
        return false;
    }

    @Override
    public void signBreak(@NotNull Sign sign, MinigamePlayer mgPlayer) {

    }

}
