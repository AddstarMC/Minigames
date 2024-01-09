package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SetCommand implements ICommand {
    private static final Map<String, ICommand> parameterList = new HashMap<>();
    private static BufferedWriter cmdFile;

    static {
        if (plugin.getConfig().getBoolean("outputCMDToFile")) {
            try {
                cmdFile = new BufferedWriter(new FileWriter(plugin.getDataFolder() + File.pathSeparator + "setcmds.txt"));
                cmdFile.write("{| class=\"wikitable\"");
                cmdFile.newLine();
                cmdFile.write("! Command");
                cmdFile.newLine();
                cmdFile.write("! Syntax");
                cmdFile.newLine();
                cmdFile.write("! Description");
                cmdFile.newLine();
                cmdFile.write("! Permission");
                cmdFile.newLine();
                cmdFile.write("! Alias");
                cmdFile.newLine();
            } catch (IOException e) {
                Minigames.getCmpnntLogger().warn("couldn't write cmd file", e);
            }
        }
        registerSetCommand(new SetStartCommand());
        registerSetCommand(new SetEndCommand());
        registerSetCommand(new SetQuitCommand());
        registerSetCommand(new SetLobbyCommand());
        registerSetCommand(new SetRewardCommand());
        registerSetCommand(new SetSecondaryRewardCommand());
        registerSetCommand(new SetTypeCommand());
        registerSetCommand(new SetFloorDegeneratorCommand());
        registerSetCommand(new SetMaxPlayersCommand());
        registerSetCommand(new SetMinPlayersCommand());
        registerSetCommand(new SetLoadoutCommand());
        registerSetCommand(new SetEnabledCommand());
        registerSetCommand(new SetMaxRadiusCommand());
        registerSetCommand(new SetMinTreasureCommand());
        registerSetCommand(new SetMaxTreasureCommand());
        registerSetCommand(new SetFlagCommand());
        registerSetCommand(new SetLocationCommand());
        registerSetCommand(new SetUsePermissionsCommand());
        registerSetCommand(new SetMinScoreCommand());
        registerSetCommand(new SetMaxScoreCommand());
        registerSetCommand(new SetTimerCommand());
        registerSetCommand(new SetItemDropCommand());
        registerSetCommand(new SetItemPickupCommand());
        registerSetCommand(new SetBlockBreakCommand());
        registerSetCommand(new SetBlockPlaceCommand());
        registerSetCommand(new SetGamemodeCommand());
        registerSetCommand(new SetBlockWhitelistCommand());
        registerSetCommand(new SetBlocksDropCommand());
        registerSetCommand(new SetGameMechanicCommand());
        registerSetCommand(new SetPaintballCommand());
        registerSetCommand(new SetStoreCheckpointsCommand());
        registerSetCommand(new SetMaxHeightCommand());
        registerSetCommand(new SetPresetCommand());
        registerSetCommand(new SetLateJoinCommand());
        registerSetCommand(new SetUnlimitedAmmoCommand());
        registerSetCommand(new SetSpectateCommand());
        registerSetCommand(new SetRandomizeChestsCommand());
        registerSetCommand(new SetRegenAreaCommand());
        registerSetCommand(new SetLivesCommand());
        registerSetCommand(new SetDefaultWinnerCommand());
        registerSetCommand(new SetAllowEnderPearlsCommand());
        registerSetCommand(new SetStartTimeCommand());
        registerSetCommand(new SetMultiplayerCheckpointsCommand());
        registerSetCommand(new SetObjectiveCommand());
        registerSetCommand(new SetGametypeNameCommand());
        registerSetCommand(new SetSPMaxPlayersCommand());
        registerSetCommand(new SetDisplayNameCommand());
        registerSetCommand(new SetRegenDelayCommand());
        registerSetCommand(new SetTeamCommand());
        registerSetCommand(new SetFlightCommand());
        registerSetCommand(new SetHintDelayCommand());
        registerSetCommand(new SetRestartDelayCommand());
        registerSetCommand(new SetSpectatorSpawnCommand());
        registerSetCommand(new SetInfectedPercentCommand());
        registerSetCommand(new SetGameOverCommand());
        registerSetCommand(new SetDisplayScoreboardCommand());
        registerSetCommand(new SetInfectedTeamCommand());
        registerSetCommand(new SetSurvivorTeamCommand());

        if (plugin.getConfig().getBoolean("outputCMDToFile")) {
            try {
                cmdFile.write("|}");
                cmdFile.close();
            } catch (IOException e) {
                Minigames.getCmpnntLogger().warn("couldn't write cmd file", e);
            }
        }
    }

    public static void registerSetCommand(ICommand command) {
        parameterList.put(command.getName(), command);

        if (plugin.getConfig().getBoolean("outputCMDToFile")) {
            PlainTextComponentSerializer plainCmpntSerial = PlainTextComponentSerializer.plainText();

            try {
                cmdFile.write("|-");
                cmdFile.newLine();
                cmdFile.write("| '''" + command.getName() + "'''");
                cmdFile.newLine();
                if (command.getUsage() != null) {
                    cmdFile.write("| ");
                    cmdFile.write(plainCmpntSerial.serialize(command.getUsage()));
                } else {
                    cmdFile.write("| N/A");
                }
                cmdFile.newLine();
                command.getDescription();
                cmdFile.write("| " + command.getDescription());
                cmdFile.newLine();
                if (command.getPermission() != null) {
                    cmdFile.write("| " + command.getPermission());
                } else {
                    cmdFile.write("| N/A");
                }
                cmdFile.newLine();
                if (command.getAliases() != null) {
                    int count = 0;
                    cmdFile.write("| ");
                    for (String alias : command.getAliases()) {
                        cmdFile.write(alias);
                        count++;
                        if (count != command.getAliases().length) {
                            cmdFile.write("\n\n");
                        }
                    }
                } else {
                    cmdFile.write("| N/A");
                }
                cmdFile.newLine();

            } catch (IOException e) {
                Minigames.getCmpnntLogger().warn("couldn't write cmd file", e);
            }
        }
    }

    @Override
    public @NotNull String getName() {
        return "set";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MinigameLangKey.COMMAND_SET_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MinigameLangKey.COMMAND_SET_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @Nullable Minigame ignored,
                             @NotNull String @Nullable [] args) {

        if (args != null) {
            ICommand comd = null;
            Minigame minigame = null;
            String[] shortArgs = null;

            if (args.length >= 1) {
                if (plugin.getMinigameManager().hasMinigame(args[0])) {
                    minigame = plugin.getMinigameManager().getMinigame(args[0]);
                }
                if (args.length >= 2) {
                    if (parameterList.containsKey(args[1].toLowerCase())) {
                        comd = parameterList.get(args[1].toLowerCase());
                    } else {
                        AliasCheck:
                        for (ICommand com : parameterList.values()) {
                            if (com.getAliases() != null) {
                                for (String alias : com.getAliases()) {
                                    if (args[1].equalsIgnoreCase(alias)) {
                                        comd = com;
                                        break AliasCheck;
                                    }
                                }
                            }
                        }
                    }
                }

                if (args.length > 2) {
                    shortArgs = new String[args.length - 2];
                    System.arraycopy(args, 2, shortArgs, 0, args.length - 2);
                }
            }

            if (comd != null && minigame != null) {
                if (sender instanceof Player || comd.canBeConsole()) {
                    if (comd.getPermission() == null || sender.hasPermission(comd.getPermission())) {
                        boolean returnValue = comd.onCommand(sender, minigame, shortArgs);
                        if (!returnValue) {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MinigameLangKey.COMMAND_SET_HEADER);

                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MinigameLangKey.COMMAND_SET_SUBCOMMAND_DESCRIPTION,
                                    Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), comd.getDescription()));
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MinigameLangKey.COMMAND_SET_SUBCOMMAND_USAGE,
                                    Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), comd.getUsage()));
                            if (comd.getAliases() != null) {
                                String aliases = String.join("<gray>, </gray>", comd.getAliases());

                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MinigameLangKey.COMMAND_SET_SUBCOMMAND_ALIASES,
                                        Placeholder.parsed(MinigamePlaceHolderKey.TEXT.getKey(), aliases));
                            }
                        }
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOPERMISSION);
                    }
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOTAPLAYER);
                }
                return true;
            } else if (minigame == null) {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), args[0]));
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame, @NotNull String @Nullable [] args) {
        if (args != null && args.length > 0) {
            Player ply = null;
            if (sender instanceof Player) {
                ply = (Player) sender;
            }
            ICommand comd = null;
            String[] shortArgs;
            Minigame mgm = null;

            if (plugin.getMinigameManager().hasMinigame(args[0])) {
                mgm = plugin.getMinigameManager().getMinigame(args[0]);
            }

            if (args.length > 1 && mgm != null) {
                if (parameterList.containsKey(args[1].toLowerCase())) {
                    comd = parameterList.get(args[1].toLowerCase());
                }

                shortArgs = new String[args.length - 2];
                System.arraycopy(args, 2, shortArgs, 0, args.length - 2);

                if (comd != null) {
                    if (ply != null) {
                        List<String> l = comd.onTabComplete(sender, mgm, shortArgs);
                        return Objects.requireNonNullElseGet(l, () -> List.of(""));
                    }
                } else {
                    List<String> ls = new ArrayList<>(parameterList.keySet());
                    return MinigameUtils.tabCompleteMatch(ls, args[1]);
                }
            } else if (args.length == 1) {
                List<String> ls = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
                return MinigameUtils.tabCompleteMatch(ls, args[0]);
            }
        }
        return null;
    }

}
