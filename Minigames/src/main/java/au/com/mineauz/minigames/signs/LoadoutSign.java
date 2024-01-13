package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

public class LoadoutSign implements MinigameSign {

    private static final Minigames plugin = Minigames.getPlugin();

    @Override
    public @NotNull String getName() {
        return "Loadout";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.loadout";
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.loadout";
    }

    @Override
    public boolean signCreate(@NotNull SignChangeEvent event) {
        event.setLine(1, ChatColor.GREEN + "Loadout");
        if (event.getLine(2).equalsIgnoreCase("menu"))
            event.setLine(2, ChatColor.GREEN + "Menu");
        return true;
    }

    @Override
    public boolean signUse(@NotNull Sign sign, @NotNull MinigamePlayer mgPlayer) {
        if (mgPlayer.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR && mgPlayer.isInMinigame()) {
            Minigame mgm = mgPlayer.getMinigame();

            if (mgm == null || mgm.isSpectator(mgPlayer)) {
                return false;
            }

            LoadoutModule loadoutModule = LoadoutModule.getMinigameModule(mgm);

            if (sign.getSide(Side.FRONT).getLine(2).equals(ChatColor.GREEN + "Menu")) {
                boolean nores = !sign.getSide(Side.FRONT).getLine(3).equalsIgnoreCase("respawn");
                LoadoutModule.getMinigameModule(mgm).displaySelectionMenu(mgPlayer, nores);
            } else {
                String loadOutName = sign.getSide(Side.FRONT).getLine(2);
                PlayerLoadout loadout = loadoutModule.getLoadout(loadOutName);

                if (loadout == null) {
                    //loadout module failed. try to get global loadout
                    loadout = plugin.getMinigameManager().getLoadout(loadOutName);
                }

                if (loadout != null) {
                    if (!loadout.getUsePermissions() || mgPlayer.getPlayer().hasPermission("minigame.loadout." + sign.getSide(Side.FRONT).getLine(2).toLowerCase())) {
                        if (mgPlayer.setLoadout(loadout)) {
                            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.PLAYER_LOADOUT_EQUIPPED,
                                    Placeholder.component(MinigamePlaceHolderKey.LOADOUT.getKey(), sign.getSide(Side.FRONT).line(2)));

                            if (mgm.getType() == MinigameType.SINGLEPLAYER ||
                                    mgm.hasStarted()) {
                                if (sign.getSide(Side.FRONT).getLine(3).equalsIgnoreCase("respawn")) {
                                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.PLAYER_LOADOUT_NEXTRESPAWN);
                                } else if (sign.getSide(Side.FRONT).getLine(3).equalsIgnoreCase("temporary")) {
                                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.PLAYER_LOADOUT_TEMPORARILY);
                                    loadout.equiptLoadout(mgPlayer);
                                    mgPlayer.setLoadout(mgPlayer.getDefaultLoadout());
                                } else {
                                    loadout.equiptLoadout(mgPlayer);
                                }
                            }
                        }
                        return true;
                    } else {
                        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOPERMISSION);
                    }
                } else {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.PLAYER_LOADOUT_ERROR_NOLOADOUT);
                }
            }
        } else if (mgPlayer.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.SIGN_ERROR_EMPTYHAND);
        }
        return false;
    }

    @Override
    public void signBreak(@NotNull Sign sign, MinigamePlayer mgPlayer) {

    }

}
