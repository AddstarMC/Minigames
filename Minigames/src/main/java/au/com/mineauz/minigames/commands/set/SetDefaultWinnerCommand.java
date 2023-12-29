package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetDefaultWinnerCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "defaultwinner";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"defwin"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MinigameLangKey.COMMAND_SET_DEFAULTWINNER_DESCRIPTION);
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MinigameLangKey.COMMAND_SET_DEFAULTWINNER_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.defaultwinner";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            TeamsModule teamsModule = TeamsModule.getMinigameModule(minigame);
            TeamColor teamColor = TeamColor.matchColor(args[0]);

            if (teamColor != null) {
                teamsModule.setDefaultWinner(teamColor);
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_DEFAULTWINNER_SUCCESS,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                        Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), teamColor.getCompName()));
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOTATEAM,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TEAM.getKey(), args[0]));
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                      String alias, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> teams = new ArrayList<>();
            for (Team t : TeamsModule.getMinigameModule(minigame).getTeams()) {
                teams.add(t.getColor().toString().toLowerCase());
            }
            return MinigameUtils.tabCompleteMatch(teams, args[0]);
        }
        return null;
    }

}
