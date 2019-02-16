package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import java.util.ArrayList;
import java.util.List;

public class FinishSign implements MinigameSign {

    private static Minigames plugin = Minigames.getPlugin();

    @Override
    public String getName() {
        return "Finish";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.finish";
    }

    @Override
    public String getCreatePermissionMessage() {
        return MinigameUtils.getLang("sign.finish.createPermission");
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
        event.setLine(1, ChatColor.GREEN + "Finish");
        if (!event.getLine(2).isEmpty() && plugin.getMinigameManager().hasMinigame(event.getLine(2))) {
            event.setLine(2, plugin.getMinigameManager().getMinigame(event.getLine(2)).getName(false));
        } else if (!event.getLine(2).isEmpty()) {
            event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noMinigame"));
            return false;
        }
        return true;
    }

    @Override
    public boolean signUse(Sign sign, MinigamePlayer player) {
        if (player.isInMinigame() && player.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
            Minigame minigame = player.getMinigame();

            if (minigame.isSpectator(player) || minigame.getState() == MinigameState.ENDED) {
                return false;
            }

            if (!minigame.getFlags().isEmpty()) {
                if (player.getPlayer().isOnGround()) {
                    if (plugin.getPlayerManager().checkRequiredFlags(player, minigame.getName(false)).isEmpty()) {
                        if (sign.getLine(2).isEmpty() || sign.getLine(2).equals(player.getMinigame().getName(false))) {
                            if (player.getMinigame().isTeamGame()) {
                                List<MinigamePlayer> w = new ArrayList<>(player.getTeam().getPlayers());
                                List<MinigamePlayer> l = new ArrayList<>(minigame.getPlayers().size() - player.getTeam().getPlayers().size());
                                for (Team t : TeamsModule.getMinigameModule(minigame).getTeams()) {
                                    if (t != player.getTeam())
                                        l.addAll(t.getPlayers());
                                }
                                plugin.getPlayerManager().endMinigame(minigame, w, l);
                            } else {
                                if (minigame.getType() == MinigameType.MULTIPLAYER) {
                                    List<MinigamePlayer> w = new ArrayList<>(1);
                                    w.add(player);
                                    List<MinigamePlayer> l = new ArrayList<>(minigame.getPlayers().size());
                                    l.addAll(minigame.getPlayers());
                                    l.remove(player);
                                    plugin.getPlayerManager().endMinigame(minigame, w, l);
                                } else
                                    plugin.getPlayerManager().endMinigame(player);
                            }

                            plugin.getPlayerManager().partyMode(player, 3, 10L);
                        }
                    } else {
                        List<String> requiredFlags = plugin.getPlayerManager().checkRequiredFlags(player, minigame.getName(false));
                        String flags = "";
                        int num = requiredFlags.size();

                        for (int i = 0; i < num; i++) {
                            flags += requiredFlags.get(i);
                            if (i != num - 1) {
                                flags += ", ";
                            }
                        }
                        player.sendInfoMessage(MinigameUtils.getLang("sign.finish.requireFlags"));
                        player.sendInfoMessage(ChatColor.GRAY + flags);
                    }
                }
                return true;
            } else {
                if (player.getPlayer().isOnGround()) {
                    if (player.getMinigame().isTeamGame()) {
                        List<MinigamePlayer> w = new ArrayList<>(player.getTeam().getPlayers());
                        List<MinigamePlayer> l = new ArrayList<>(minigame.getPlayers().size() - player.getTeam().getPlayers().size());
                        for (Team t : TeamsModule.getMinigameModule(minigame).getTeams()) {
                            if (t != player.getTeam())
                                l.addAll(t.getPlayers());
                        }
                        plugin.getPlayerManager().endMinigame(minigame, w, l);
                    } else {
                        if (minigame.getType() == MinigameType.MULTIPLAYER) {
                            List<MinigamePlayer> w = new ArrayList<>(1);
                            w.add(player);
                            List<MinigamePlayer> l = new ArrayList<>(minigame.getPlayers().size());
                            l.addAll(minigame.getPlayers());
                            l.remove(player);
                            plugin.getPlayerManager().endMinigame(minigame, w, l);
                        } else
                            plugin.getPlayerManager().endMinigame(player);
                    }
                    plugin.getPlayerManager().partyMode(player);
                    return true;
                }
            }
        } else if (player.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) {
            player.sendMessage(MinigameUtils.getLang("sign.emptyHand"), MinigameMessageType.ERROR);
        }
        return false;
    }

    @Override
    public void signBreak(Sign sign, MinigamePlayer player) {

    }

}
