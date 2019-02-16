package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import java.util.ArrayList;
import java.util.List;

public class ScoreSign implements MinigameSign {

    @Override
    public String getName() {
        return "score";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.score";
    }

    @Override
    public String getCreatePermissionMessage() {
        return MinigameUtils.getLang("sign.score.createPermission");
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.score";
    }

    @Override
    public String getUsePermissionMessage() {
        return MinigameUtils.getLang("sign.score.usePermission");
    }

    @Override
    public boolean signCreate(SignChangeEvent event) {
        if (event.getLine(2).matches("[0-9]+")) {
            event.setLine(1, ChatColor.GREEN + "Score");
            if (TeamColor.matchColor(event.getLine(3)) != null) {
                TeamColor col = TeamColor.matchColor(event.getLine(3));
                event.setLine(3, col.getColor() + MinigameUtils.capitalize(col.toString()));
            } else
                event.setLine(3, "");
            return true;
        }
        return false;
    }

    @Override
    public boolean signUse(Sign sign, MinigamePlayer player) {
        if (player.isInMinigame() && player.getPlayer().isOnGround()) {
            Minigame mg = player.getMinigame();
            int score = Integer.parseInt(sign.getLine(2));
            if (!mg.isTeamGame()) {
                if (player.hasClaimedScore(sign.getLocation())) {
                    player.sendMessage(MinigameUtils.getLang("sign.score.alreadyUsed"), MinigameMessageType.ERROR);
                    return true;
                }
                player.addScore(score);
                mg.setScore(player, player.getScore());
                player.sendInfoMessage(MinigameUtils.formStr("sign.score.addScore", score, player.getScore()));
                if (mg.getMaxScore() != 0 && mg.getMaxScorePerPlayer() <= player.getScore()) {
                    Minigames.getPlugin().getPlayerManager().endMinigame(player);
                }
                player.addClaimedScore(sign.getLocation());
            } else {
                TeamColor steam = TeamColor.matchColor(ChatColor.stripColor(sign.getLine(3)));
                Team pteam = player.getTeam();
                if (steam == null || !TeamsModule.getMinigameModule(mg).hasTeam(steam) || pteam.getColor() == steam) {
                    if (Minigames.getPlugin().getMinigameManager().hasClaimedScore(mg, sign.getLocation(), 0)) {
                        player.sendMessage(MinigameUtils.getLang("sign.score.alreadyUsedTeam"), MinigameMessageType.ERROR);
                        return true;
                    }
                    player.addScore(score);
                    mg.setScore(player, player.getScore());

                    pteam.addScore(score);
                    player.sendInfoMessage(MinigameUtils.formStr("sign.score.addScoreTeam",
                            score, pteam.getChatColor().toString() + pteam.getScore()));
                    Minigames.getPlugin().getMinigameManager().addClaimedScore(mg, sign.getLocation(), 0);
                    if (mg.getMaxScore() != 0 && mg.getMaxScorePerPlayer() <= pteam.getScore()) {
                        List<MinigamePlayer> winners = new ArrayList<>(pteam.getPlayers());
                        List<MinigamePlayer> losers = new ArrayList<>(mg.getPlayers().size() - pteam.getPlayers().size());
                        for (Team t : TeamsModule.getMinigameModule(mg).getTeams()) {
                            if (t != pteam)
                                losers.addAll(t.getPlayers());
                        }
                        Minigames.getPlugin().getPlayerManager().endMinigame(mg, winners, losers);
                    }
                }
            }
        } else if (player.isInMinigame() && !player.getPlayer().isOnGround()) {
            player.sendMessage(MinigameUtils.getLang("sign.onGround"), MinigameMessageType.ERROR);
        }
        return true;
    }

    @Override
    public void signBreak(Sign sign, MinigamePlayer player) {
        //Eh...

    }

}
