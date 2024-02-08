package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.tool.MinigameTool;
import au.com.mineauz.minigames.tool.ToolMode;
import au.com.mineauz.minigames.tool.ToolModes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ToolCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "tool";
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_TOOL_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_TOOL_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.tool";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args) { // lang rework feuer here, this might need some reorganisation. But for now I jest want to get done with the commands.
        if (sender instanceof Player player) {
            MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(player);
            if (args.length == 0) {
                MinigameUtils.giveMinigameTool(mgPlayer);
            } else if (MinigameUtils.hasMinigameTool(mgPlayer)) {
                if (args[0].equalsIgnoreCase("minigame") && args.length == 2) {
                    if (Minigames.getPlugin().getMinigameManager().hasMinigame(args[1])) {
                        MinigameTool tool;
                        Minigame mg = Minigames.getPlugin().getMinigameManager().getMinigame(args[1]);
                        tool = MinigameUtils.hasMinigameTool(mgPlayer) ? MinigameUtils.getMinigameTool(mgPlayer) : MinigameUtils.giveMinigameTool(mgPlayer);

                        tool.setMinigame(mg);
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), args[1]));
                    }
                } else if (args[0].equalsIgnoreCase("select")) {
                    MinigameTool tool = !MinigameUtils.hasMinigameTool(mgPlayer) ? MinigameUtils.giveMinigameTool(mgPlayer) : MinigameUtils.getMinigameTool(mgPlayer);

                    if (tool.getMinigame() != null && tool.getMode() != null) {
                        tool.getMode().select(mgPlayer, tool.getMinigame(),
                                TeamsModule.getMinigameModule(tool.getMinigame()).getTeam(tool.getTeam()));
                    } else {
                        if (tool.getMode() == null) {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_TOOL_ERROR_NOMODE);
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_TOOL_ERROR_NOMGSELECTED);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("deselect")) {
                    MinigameTool tool = !MinigameUtils.hasMinigameTool(mgPlayer) ? MinigameUtils.giveMinigameTool(mgPlayer) : MinigameUtils.getMinigameTool(mgPlayer);

                    if (tool.getMinigame() != null && tool.getMode() != null) {
                        tool.getMode().deselect(mgPlayer, tool.getMinigame(),
                                TeamsModule.getMinigameModule(tool.getMinigame()).getTeam(tool.getTeam()));
                    } else {
                        if (tool.getMode() == null) {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_TOOL_ERROR_NOMODE);
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_TOOL_ERROR_NOMGSELECTED);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("team") && args.length == 2) {
                    if (TeamColor.matchColor(args[1]) != null || args[1].equalsIgnoreCase("none")) {
                        MinigameTool tool = MinigameUtils.hasMinigameTool(mgPlayer) ? MinigameUtils.getMinigameTool(mgPlayer) : MinigameUtils.giveMinigameTool(mgPlayer);

                        tool.setTeam(args[1].equalsIgnoreCase("none") ? null : TeamColor.matchColor(args[1]));
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_TOOL_SETTEAM,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEAM.getKey(), args[1]));
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTEAM,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                    }
                } else if (ToolModes.getToolMode(args[0]) != null) {
                    MinigameTool tool = MinigameUtils.hasMinigameTool(mgPlayer) ? MinigameUtils.getMinigameTool(mgPlayer) : MinigameUtils.giveMinigameTool(mgPlayer);

                    tool.setMode(ToolModes.getToolMode(args[0]));
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_TOOL_SETMODE,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), tool.getMode().getName().toLowerCase().replace("_", " ")));
                } else {
                    return false;
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_TOOL_ERROR_NOTOOL);
            }
        } else {
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_SENDERNOTAPLAYER);
        }
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> result = new ArrayList<>(ToolModes.getToolModes().stream().map(ToolMode::getName).toList());
            result.addAll(List.of("minigame", "select", "deselect", "team"));

            return MinigameUtils.tabCompleteMatch(result, args[0]);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("team")) {
                List<String> ret = new ArrayList<>();
                for (TeamColor col : TeamColor.values()) {
                    ret.add(col.toString());
                }
                return MinigameUtils.tabCompleteMatch(ret, args[1]);
            } else if (args[0].equalsIgnoreCase("minigame")) {
                List<String> ret = new ArrayList<>(PLUGIN.getMinigameManager().getAllMinigames().keySet());
                return MinigameUtils.tabCompleteMatch(ret, args[1]);
            }
        }
        return null;
    }

}
