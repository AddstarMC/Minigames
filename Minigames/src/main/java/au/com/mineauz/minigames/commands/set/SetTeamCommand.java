package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.MgModules;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetTeamCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "team";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_TEAM_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_TEAM_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.team";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            TeamsModule tmod = TeamsModule.getMinigameModule(minigame);

            if (tmod != null) {
                switch (args[0].toLowerCase()) {
                    case "add" -> {
                        if (args.length >= 2) {
                            TeamColor teamColor = TeamColor.matchColor(args[1]);
                            StringBuilder name = new StringBuilder();
                            if (teamColor != null) {
                                if (args.length > 2) {
                                    for (int i = 2; i < args.length; i++) {
                                        name.append(args[i]);
                                        if (i != args.length - 1)
                                            name.append(" ");
                                    }
                                }

                                Team team = tmod.addTeam(teamColor, name.toString());
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_TEAM_ADD,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                                        Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), team.getColor().getCompName()),
                                        Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), team.getColoredDisplayName()));

                                return true;
                            } else {
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTEAM,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                                //sender.sendMessage(TeamColor.validColorNamesComp()); //todo
                            }
                        }
                    }
                    case "list" -> {
                        List<Component> teamsList = new ArrayList<>(tmod.getTeams().size());

                        for (Team team : tmod.getTeams()) {
                            teamsList.add(team.getColor().getCompName().append(MiniMessage.miniMessage().deserialize("<gray>(<team>)</gray>",
                                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), team.getColoredDisplayName()))));
                        }

                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_TEAM_LIST,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                                Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), Component.join(JoinConfiguration.commas(true), teamsList)));
                        return true;
                    }
                    case "remove" -> {
                        if (args.length >= 2) {
                            TeamColor teamColor = TeamColor.matchColor(args[1]);
                            if (teamColor != null) {
                                if (tmod.hasTeam(teamColor)) {
                                    tmod.removeTeam(teamColor);
                                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_TEAM_REMOVE,
                                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                                            Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), teamColor.getCompName()));
                                } else {
                                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTEAM,
                                            Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), teamColor.getCompName()));
                                }
                                return true;
                            } else {
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTEAM,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                                //String cols = tmod.getTeams().stream().map(t -> t.getColor().toString()).collect(Collectors.joining("<gray>, </gray>")); //todo
                            }
                        }
                    }
                    case "rename" -> {
                        if (args.length > 2) {
                            TeamColor teamColor = TeamColor.matchColor(args[1]);
                            StringBuilder name = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                name.append(args[i]);
                                if (i != args.length - 1)
                                    name.append(" ");
                            }
                            if (teamColor != null) {
                                Team team = tmod.getTeam(teamColor);

                                if (team != null) {
                                    team.setDisplayName(name.toString());
                                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_TEAM_RENAME,
                                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                                            Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), teamColor.getCompName()),
                                            Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), team.getColoredDisplayName()));
                                } else {
                                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTEAM,
                                            Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), teamColor.getCompName()));
                                }
                                return true;
                            } else {
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTEAM,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));

                                //String cols = tmod.getTeams().stream().map(t -> t.getColor().toString()).collect(Collectors.joining("<gray>, </gray>")); //todo
                            }
                        }
                    }
                    case "maxplayers" -> {
                        if (args.length >= 3) {
                            TeamColor teamColor = TeamColor.matchColor(args[1]);
                            if (teamColor != null) {
                                Team team = tmod.getTeam(teamColor);

                                if (team != null) {
                                    if (args[2].matches("[0-9]+")) {
                                        int val = Integer.parseInt(args[2]);
                                        team.setMaxPlayers(val);

                                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_TEAM_MAXPLAYERS,
                                                Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), team.getColoredDisplayName()),
                                                Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(val)));
                                    } else {
                                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTNUMBER,
                                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[2]));
                                    }
                                } else {
                                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTEAM,
                                            Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), teamColor.getCompName()));
                                }
                            } else {
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTEAM,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                            }
                        }
                        return true;
                    }
                    default ->
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_UNKNOWN_PARAM,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTGAMEMECHANIC,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                        Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), MgModules.TEAMS.getName()));
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return MinigameUtils.tabCompleteMatch(List.of("add", "rename", "remove", "list", "maxplayers"), args[0]);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                return MinigameUtils.tabCompleteMatch(new ArrayList<>(TeamColor.validColorNames()), args[1]);
            } else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("rename")) {
                TeamsModule teamsModule = TeamsModule.getMinigameModule(minigame);

                if (teamsModule != null) {
                    return MinigameUtils.tabCompleteMatch(teamsModule.getTeams().stream().map(t -> t.getColor().toString()).toList(), args[1]);
                }
            } else if (args[0].equalsIgnoreCase("maxplayers")) {
                TeamsModule teamsModule = TeamsModule.getMinigameModule(minigame);

                if (teamsModule != null) {
                    List<String> cols = new ArrayList<>();
                    for (Team t : teamsModule.getTeams()) {
                        cols.add(t.getColor().toString());
                    }

                    return MinigameUtils.tabCompleteMatch(cols, args[1]);
                }
            }
        }
        return null;
    }

}
