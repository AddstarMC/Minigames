package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public class LoadoutSign implements MinigameSign {

    private static Minigames plugin = Minigames.getPlugin();

    @Override
    public String getName() {
        return "Loadout";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.loadout";
    }

    @Override
    public String getCreatePermissionMessage() {
        return MinigameUtils.getLang("sign.loadout.createPermission");
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.loadout";
    }

    @Override
    public String getUsePermissionMessage() {
        return MinigameUtils.getLang("sign.loadout.usePermission");
    }

    @Override
    public boolean signCreate(SignChangeEvent event) {
        event.setLine(1, ChatColor.GREEN + "Loadout");
        if (event.getLine(2).equalsIgnoreCase("menu"))
            event.setLine(2, ChatColor.GREEN + "Menu");
        return true;
    }

    @Override
    public boolean signUse(Sign sign, MinigamePlayer player) {
        if (player.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR && player.isInMinigame()) {
            Minigame mgm = player.getMinigame();
            LoadoutModule loadout = LoadoutModule.getMinigameModule(mgm);
            if (mgm == null || mgm.isSpectator(player)) {
                return false;
            }

            if (sign.getLine(2).equals(ChatColor.GREEN + "Menu")) {
                boolean nores = true;
                if (sign.getLine(3).equalsIgnoreCase("respawn"))
                    nores = false;
                LoadoutModule.getMinigameModule(mgm).displaySelectionMenu(player, nores);
            } else if (loadout.hasLoadout(sign.getLine(2))) {
                if (!loadout.getLoadout(sign.getLine(2)).getUsePermissions() || player.getPlayer().hasPermission("minigame.loadout." + sign.getLine(2).toLowerCase())) {
                    if (player.setLoadout(loadout.getLoadout(sign.getLine(2)))) {
                        player.sendInfoMessage(MinigameUtils.formStr("sign.loadout.equipped", sign.getLine(2)));

                        if (mgm.getType() == MinigameType.SINGLEPLAYER ||
                                mgm.hasStarted()) {
                            if (sign.getLine(3).equalsIgnoreCase("respawn")) {
                                player.sendInfoMessage(MinigameUtils.getLang("sign.loadout.nextRespawn"));
                            } else {
                                loadout.getLoadout(sign.getLine(2)).equiptLoadout(player);
                            }
                        }
                    }
                    return true;
                } else {
                    player.sendMessage(MinigameUtils.formStr("sign.loadout.noPermisson", sign.getLine(2)), MinigameMessageType.ERROR);
                }
            } else if (plugin.getMinigameManager().hasLoadout(sign.getLine(2))) {
                if (!plugin.getMinigameManager().getLoadout(sign.getLine(2)).getUsePermissions() || player.getPlayer().hasPermission("minigame.loadout." + sign.getLine(2).toLowerCase())) {
                    if (player.setLoadout(plugin.getMinigameManager().getLoadout(sign.getLine(2)))) {
                        player.sendInfoMessage(MinigameUtils.formStr("sign.loadout.equipped", sign.getLine(2)));

                        if (mgm.getType() == MinigameType.SINGLEPLAYER ||
                                mgm.hasStarted()) {
                            if (sign.getLine(3).equalsIgnoreCase("respawn")) {
                                player.sendInfoMessage(MinigameUtils.getLang("sign.loadout.nextRespawn"));
                            } else {
                                plugin.getMinigameManager().getLoadout(sign.getLine(2)).equiptLoadout(player);
                            }
                        }
                    }
                    return true;
                } else {
                    player.sendMessage(MinigameUtils.formStr("sign.loadout.noPermission", sign.getLine(2)), MinigameMessageType.ERROR);
                }
            } else {
                player.sendMessage(MinigameUtils.getLang("sign.loadout.noLoadout"), MinigameMessageType.ERROR);
            }
        } else if (player.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR)
            player.sendMessage(MinigameUtils.getLang("sign.emptyHand"), MinigameMessageType.ERROR);
        return false;
    }

    @Override
    public void signBreak(Sign sign, MinigamePlayer player) {

    }

}
