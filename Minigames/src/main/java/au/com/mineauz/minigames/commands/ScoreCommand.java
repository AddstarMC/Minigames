package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ScoreCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "score";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SCORE_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SCORE_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.score";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        if (args.length >= 2) {
            MinigamePlayer mgPlayer = null;
            TeamColor color = TeamColor.matchColor(args[1]);

            if (color == null) {
                List<Player> plys = PLUGIN.getServer().matchPlayer(args[1]);
                if (!plys.isEmpty()) {
                    mgPlayer = PLUGIN.getPlayerManager().getMinigamePlayer(plys.get(0));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAD_ERROR_NOTPLAYER,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("get")) {
                if (mgPlayer != null) {
                    if (mgPlayer.isInMinigame()) {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SCORE_GET_PLAYER,
                                Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.displayName()),
                                Placeholder.unparsed(MinigamePlaceHolderKey.SCORE.getKey(), String.valueOf(mgPlayer.getScore())));
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTINMINIGAME_PLAYER,
                                Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.displayName()));
                    }
                } else {
                    if (args.length >= 3) {
                        Minigame mg;
                        if (PLUGIN.getMinigameManager().hasMinigame(args[2])) {
                            mg = PLUGIN.getMinigameManager().getMinigame(args[2]);
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), args[2]));
                            return true;
                        }

                        TeamsModule tmod = TeamsModule.getMinigameModule(mg);

                        if (mg.isTeamGame()) {
                            if (tmod.hasTeam(color)) {
                                Team team = tmod.getTeam(color);
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SCORE_GET_TEAM,
                                        Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), team.getColoredDisplayName()),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mg.getName(false)),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.SCORE.getKey(), String.valueOf(mgPlayer.getScore())));
                            } else {
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOTEAM,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mg.getName(false)),
                                        Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), color.getCompName()));
                            }
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOTTEAMGAME,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mg.getName(false)));
                            return true;
                        }
                    } else {
                        return false;
                    }
                }
                return true;
            } else if (args[0].equalsIgnoreCase("set") && args.length >= 3) {

                int score;

                if (args[2].matches("-?[0-9]+")) {
                    score = Integer.parseInt(args[2]);
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTNUMBER,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[2]));
                    return true;
                }

                if (mgPlayer != null) {
                    if (mgPlayer.isInMinigame()) {
                        mgPlayer.setScore(score);
                        mgPlayer.getMinigame().setScore(mgPlayer, mgPlayer.getScore());
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SCORE_SET_PLAYER,
                                Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.displayName()),
                                Placeholder.unparsed(MinigamePlaceHolderKey.SCORE.getKey(), String.valueOf(score)));

                        if (mgPlayer.getMinigame().getMaxScore() != 0 && score >= mgPlayer.getMinigame().getMaxScorePerPlayer()) {
                            PLUGIN.getPlayerManager().endMinigame(mgPlayer);
                        }
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_PLAYERNOTINMINIGAME,
                                Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.displayName()));
                    }
                } else {
                    if (args.length >= 4) {
                        Minigame mg;
                        if (PLUGIN.getMinigameManager().hasMinigame(args[3])) {
                            mg = PLUGIN.getMinigameManager().getMinigame(args[3]);
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), args[2]));
                            return true;
                        }

                        TeamsModule tmod = TeamsModule.getMinigameModule(mg);

                        if (mg.isTeamGame() && mg.hasPlayers()) {
                            Team team;
                            if (tmod.hasTeam(color)) {
                                team = tmod.getTeam(color);
                                team.setScore(score);
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SCORE_SET_TEAM,
                                        Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), team.getColoredDisplayName()),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mg.getName(false)),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.SCORE.getKey(), String.valueOf(score)));
                            } else {
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOTEAM,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mg.getName(false)),
                                        Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), color.getCompName()));
                                return true;
                            }

                            if (mg.getMaxScore() != 0 && score >= mg.getMaxScorePerPlayer()) {
                                List<MinigamePlayer> w = new ArrayList<>(team.getPlayers());
                                List<MinigamePlayer> l = new ArrayList<>(mg.getPlayers().size() - team.getPlayers().size());
                                for (Team te : tmod.getTeams()) {
                                    if (te != team) {
                                        l.addAll(te.getPlayers());
                                    }
                                }
                                PLUGIN.getPlayerManager().endMinigame(mg, w, l);
                            }
                        } else if (!mg.hasPlayers()) {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_ISEMPTY,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mg.getName(false)));
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOTTEAMGAME,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mg.getName(false)));
                        }
                    } else {
                        return false;
                    }
                }
                return true;
            } else if (args[0].equalsIgnoreCase("add") && args.length >= 3) {
                int score;

                if (args[2].matches("-?[0-9]+")) {
                    score = Integer.parseInt(args[2]);
                } else {
                    score = 1;
                }

                if (mgPlayer != null) {
                    if (mgPlayer.isInMinigame()) {
                        mgPlayer.addScore(score);
                        mgPlayer.getMinigame().setScore(mgPlayer, mgPlayer.getScore());
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SCORE_ADD_PLAYER,
                                Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), mgPlayer.displayName()),
                                Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(score)),
                                Placeholder.unparsed(MinigamePlaceHolderKey.SCORE.getKey(), String.valueOf(mgPlayer.getScore())));

                        if (mgPlayer.getMinigame().getMaxScore() != 0 && mgPlayer.getScore() >= mgPlayer.getMinigame().getMaxScorePerPlayer()) {
                            PLUGIN.getPlayerManager().endMinigame(mgPlayer);
                        }
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_PLAYERNOTINMINIGAME,
                                Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), mgPlayer.displayName()));
                    }
                } else {
                    Minigame mg;
                    String mgName;

                    if (args.length == 4) {
                        mgName = args[3];
                    } else {
                        mgName = args[2];
                    }


                    if (PLUGIN.getMinigameManager().hasMinigame(mgName)) {
                        mg = PLUGIN.getMinigameManager().getMinigame(mgName);
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgName));
                        return true;
                    }

                    TeamsModule tmod = TeamsModule.getMinigameModule(mg);

                    if (mg.isTeamGame() && mg.hasPlayers()) {
                        Team team;
                        if (tmod.hasTeam(color)) {
                            team = tmod.getTeam(color);
                            team.addScore(score);
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SCORE_ADD_TEAM,
                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), team.getColoredDisplayName()),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(score)),
                                    Placeholder.unparsed(MinigamePlaceHolderKey.SCORE.getKey(), String.valueOf(team.getScore())));
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOTEAM,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mg.getName(false)),
                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), color.getCompName()));
                            return true;
                        }

                        if (mg.getMaxScore() != 0 && team.getScore() >= mg.getMaxScorePerPlayer()) {
                            List<MinigamePlayer> w = new ArrayList<>(team.getPlayers());
                            List<MinigamePlayer> l = new ArrayList<>(mg.getPlayers().size() - team.getPlayers().size());
                            for (Team te : tmod.getTeams()) {
                                if (te != team) {
                                    l.addAll(te.getPlayers());
                                }
                            }
                            PLUGIN.getPlayerManager().endMinigame(mg, w, l);
                        }
                    } else if (!mg.hasPlayers()) {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_ISEMPTY,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mg.getName(false)));
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOTTEAMGAME,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mg.getName(false)));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return MinigameUtils.tabCompleteMatch(List.of("get", "set", "add"), args[0]);
        } else if (args.length == 2) {

            List<String> pt = new ArrayList<>(PLUGIN.getServer().getOnlinePlayers().size() + 2);
            for (Player pl : PLUGIN.getServer().getOnlinePlayers()) {
                pt.add(pl.getName());
            }
            pt.addAll(TeamColor.validColorNames());

            return MinigameUtils.tabCompleteMatch(pt, args[1]);
        }
        List<String> mgs = new ArrayList<>(PLUGIN.getMinigameManager().getAllMinigames().keySet());
        return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
    }

}
