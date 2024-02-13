package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgSignLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FinishSign extends AMinigameSign {
    private static final Minigames plugin = Minigames.getPlugin();

    @Override
    public @NotNull Component getName() {
        return MinigameMessageManager.getMgMessage(MgSignLangKey.TYPE_FINISH);
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.finish";
    }

    @Override
    public String getUsePermission() {
        return null;
    }

    @Override
    public boolean signCreate(@NotNull SignChangeEvent event) {
        event.line(1, getName());
        Component line2 = event.line(2);
        if (line2 != null) {
            final Sign sign = (Sign) event.getBlock().getState();
            final Minigame minigame = getMinigame(sign, event.line(2));

            if (minigame != null) {
                event.line(2, minigame.getDisplayName());
                setPersistentMinigame(sign, minigame);
            } else {
                MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                        Placeholder.component(MinigamePlaceHolderKey.MINIGAME.getKey(), line2));
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean signUse(@NotNull Sign sign, @NotNull MinigamePlayer mgPlayer) {
        if (mgPlayer.isInMinigame() && mgPlayer.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
            Minigame minigame = mgPlayer.getMinigame();

            if (minigame.isSpectator(mgPlayer) || minigame.getState() == MinigameState.ENDED) {
                return false;
            }

            if (!minigame.getFlags().isEmpty()) {
                if (mgPlayer.getPlayer().isOnGround()) {
                    if (plugin.getPlayerManager().checkRequiredFlags(mgPlayer, minigame.getName()).isEmpty()) {
                        Minigame mgmOnSign = getMinigame(sign);
                        if (sign.getLine(2).isEmpty() ||
                                (mgmOnSign != null && mgmOnSign.getName().equals(mgPlayer.getMinigame().getName()))) {
                            if (mgPlayer.getMinigame().isTeamGame()) {
                                List<MinigamePlayer> w = new ArrayList<>(mgPlayer.getTeam().getPlayers());
                                List<MinigamePlayer> l = new ArrayList<>(minigame.getPlayers().size() - mgPlayer.getTeam().getPlayers().size());
                                for (Team t : TeamsModule.getMinigameModule(minigame).getTeams()) {
                                    if (t != mgPlayer.getTeam())
                                        l.addAll(t.getPlayers());
                                }
                                plugin.getPlayerManager().endMinigame(minigame, w, l);
                            } else {
                                if (minigame.getType() == MinigameType.MULTIPLAYER) {
                                    List<MinigamePlayer> w = new ArrayList<>(1);
                                    w.add(mgPlayer);
                                    List<MinigamePlayer> l = new ArrayList<>(minigame.getPlayers().size());
                                    l.addAll(minigame.getPlayers());
                                    l.remove(mgPlayer);
                                    plugin.getPlayerManager().endMinigame(minigame, w, l);
                                } else
                                    plugin.getPlayerManager().endMinigame(mgPlayer);
                            }

                            plugin.getPlayerManager().partyMode(mgPlayer, 3, 10L);
                        }
                    } else {
                        String requiredFlags = String.join(", ", plugin.getPlayerManager().checkRequiredFlags(mgPlayer, minigame.getName()));
                        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.SIGN_FINISH_REQUIREFLAGS,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), requiredFlags));
                    }
                }
                return true;
            } else {
                if (mgPlayer.getPlayer().isOnGround()) {
                    if (mgPlayer.getMinigame().isTeamGame()) {
                        List<MinigamePlayer> w = new ArrayList<>(mgPlayer.getTeam().getPlayers());
                        List<MinigamePlayer> l = new ArrayList<>(minigame.getPlayers().size() - mgPlayer.getTeam().getPlayers().size());
                        for (Team t : TeamsModule.getMinigameModule(minigame).getTeams()) {
                            if (t != mgPlayer.getTeam())
                                l.addAll(t.getPlayers());
                        }
                        plugin.getPlayerManager().endMinigame(minigame, w, l);
                    } else {
                        if (minigame.getType() == MinigameType.MULTIPLAYER) {
                            List<MinigamePlayer> w = new ArrayList<>(1);
                            w.add(mgPlayer);
                            List<MinigamePlayer> l = new ArrayList<>(minigame.getPlayers().size());
                            l.addAll(minigame.getPlayers());
                            l.remove(mgPlayer);
                            plugin.getPlayerManager().endMinigame(minigame, w, l);
                        } else
                            plugin.getPlayerManager().endMinigame(mgPlayer);
                    }
                    plugin.getPlayerManager().partyMode(mgPlayer);
                    return true;
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
