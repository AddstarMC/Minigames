package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScoreSign implements MinigameSign {

    @Override
    public @NotNull String getName() {
        return "score";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.score";
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.score";
    }

    @Override
    public boolean signCreate(@NotNull SignChangeEvent event) {
        if (event.getLine(2).matches("[0-9]+")) {
            event.setLine(1, ChatColor.GREEN + "Score");
            if (TeamColor.matchColor(event.getLine(3)) != null) {
                TeamColor col = TeamColor.matchColor(event.getLine(3));
                event.setLine(3, col.getColor() + WordUtils.capitalize(col.toString()));
            } else
                event.setLine(3, "");
            return true;
        }
        return false;
    }

    @Override
    public boolean signUse(@NotNull Sign sign, @NotNull MinigamePlayer mgPlayer) {
        if (mgPlayer.isInMinigame()) {
            Minigame mg = mgPlayer.getMinigame();
            int score = Integer.parseInt(sign.getLine(2));
            if (!mg.isTeamGame()) {
                if (mgPlayer.hasClaimedScore(sign.getLocation())) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.SIGN_SCORE_ERROR_ALREADYUSED);
                    return true;
                }
                mgPlayer.addScore(score);
                mg.setScore(mgPlayer, mgPlayer.getScore());
                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.SIGN_SCORE_ADDSCORE,
                        Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(score)),
                        Placeholder.unparsed(MinigamePlaceHolderKey.SCORE.getKey(), String.valueOf(mgPlayer.getScore())));
                if (mg.getMaxScore() != 0 && mg.getMaxScorePerPlayer() <= mgPlayer.getScore()) {
                    Minigames.getPlugin().getPlayerManager().endMinigame(mgPlayer);
                }
                mgPlayer.addClaimedScore(sign.getLocation());
            } else {
                TeamColor steam = TeamColor.matchColor(ChatColor.stripColor(sign.getLine(3)));
                Team pteam = mgPlayer.getTeam();
                if (steam == null || !TeamsModule.getMinigameModule(mg).hasTeam(steam) || pteam.getColor() == steam) {
                    if (Minigames.getPlugin().getMinigameManager().hasClaimedScore(mg, sign.getLocation(), 0)) {
                        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.SIGN_SCORE_ERROR_ALREADYUSEDTEAM);
                        return true;
                    }
                    mgPlayer.addScore(score);
                    mg.setScore(mgPlayer, mgPlayer.getScore());

                    pteam.addScore(score);
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.SIGN_SCORE_ADDSCORETEAM,
                            Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(score)),
                            Placeholder.unparsed(MinigamePlaceHolderKey.SCORE.getKey(), String.valueOf(pteam.getScore())));
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
        }
        return true;
    }

    @Override
    public void signBreak(@NotNull Sign sign, MinigamePlayer mgPlayer) {
        //Eh...

    }

}
