package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.set.SetCommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandDispatcher implements CommandExecutor, TabCompleter {
    private static final Map<String, ACommand> commands = new HashMap<>();
    private static final Minigames plugin = Minigames.getPlugin();

    static {
        registerCommand(new CreateCommand());
        registerCommand(new SetCommand());
        registerCommand(new JoinCommand());
        registerCommand(new StartCommand());
        registerCommand(new QuitCommand());
        registerCommand(new RevertCommand());
        registerCommand(new HintCommand());
        registerCommand(new EndCommand());
        registerCommand(new HelpCommand());
//        registerCommand(new ReloadCommand());
        registerCommand(new ListCommand());
        registerCommand(new ListPlaceholder());
        registerCommand(new ToggleTimerCommand());
        registerCommand(new DeleteCommand());
        registerCommand(new PartyModeCommand());
        registerCommand(new DeniedCommandCommand());
        registerCommand(new GlobalLoadoutCommand());
        registerCommand(new SpectateCommand());
        registerCommand(new PlayerCommand());
        registerCommand(new ScoreCommand());
        registerCommand(new TeleportCommand());
        registerCommand(new EditCommand());
        registerCommand(new ToolCommand());
        registerCommand(new ScoreboardCommand());
        registerCommand(new EnableAllCommand());
        registerCommand(new DisableAllCommand());
        registerCommand(new SaveCommand());
        registerCommand(new LoadoutCommand());
        registerCommand(new BackupCommand());
        registerCommand(new DebugCommand());
        registerCommand(new BackendCommand());
        registerCommand(new InfoCommand());
        registerCommand(new ResourcePackCommand());
    }

    public static void registerCommand(ACommand command) {
        commands.put(command.getName(), command);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player ply = null;
        if (sender instanceof Player player) {
            ply = player;
        }

        if (args != null && args.length > 0) {
            ACommand comd = null;

            if (commands.containsKey(args[0].toLowerCase())) {
                comd = commands.get(args[0].toLowerCase());
            } else {
                AliasCheck:
                for (ACommand com : commands.values()) {
                    if (com.getAliases() != null) {
                        for (String alias : com.getAliases()) {
                            if (args[0].equalsIgnoreCase(alias)) {
                                comd = com;
                                break AliasCheck;
                            }
                        }
                    }
                }
            }

            if (comd != null) {
                if (ply != null || comd.canBeConsole()) {
                    String[] shortArgs;
                    if (args.length > 1) {
                        shortArgs = new String[args.length - 1];
                        System.arraycopy(args, 1, shortArgs, 0, args.length - 1);
                    } else {
                        shortArgs = new String[]{};
                    }

                    if (ply == null || (comd.getPermission() == null || ply.hasPermission(comd.getPermission()))) {
                        boolean returnValue = comd.onCommand(sender, shortArgs);
                        if (!returnValue) {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MgCommandLangKey.COMMAND_ERROR_INFO_HEADER);
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MgCommandLangKey.COMMAND_ERROR_INFO_DESCRIPTION,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), command.getDescription()));
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MgCommandLangKey.COMMAND_ERROR_INFO_USAGE,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), command.getUsage()));
                            if (comd.getAliases() != null) {
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MgCommandLangKey.COMMAND_ERROR_INFO_ALIASES,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), String.join(", ", command.getAliases())));
                            }
                        }
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOPERMISSION);
                    }
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTAPLAYER);
                }
                return true;
            }
        } else {
            MinigameMessageManager.sendMessage(sender, MinigameMessageType.NONE, Component.text(plugin.getPluginMeta().getName()));
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_MINIGAMES_AUTHORS,
                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), String.join(", ", plugin.getPluginMeta().getAuthors())));
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_MINIGAMES_VERSION,
                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), plugin.getPluginMeta().getVersion()));
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_MINIGAMES_HELP);
            return true;
        }
        return false;
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args != null && args.length > 0) {
            ACommand comd = commands.get(args[0].toLowerCase());

            if (comd != null) {
                String[] shortArgs;
                if (args.length > 1) {
                    shortArgs = new String[args.length - 1];
                    System.arraycopy(args, 1, shortArgs, 0, args.length - 1);
                } else {
                    shortArgs = new String[]{};
                }

                List<String> l = comd.onTabComplete(sender, shortArgs);
                return Objects.requireNonNullElseGet(l, () -> List.of(""));
            } else if (args.length == 1) {
                List<String> ls = new ArrayList<>(commands.keySet());
                return MinigameUtils.tabCompleteMatch(ls, args[0]);
            }
        }
        return null;
    }
}
