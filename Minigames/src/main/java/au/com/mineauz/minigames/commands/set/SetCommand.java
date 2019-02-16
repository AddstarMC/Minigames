package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetCommand implements ICommand {
    private static Map<String, ICommand> parameterList = new HashMap<>();
    private static BufferedWriter cmdFile;

    static {
        if (plugin.getConfig().getBoolean("outputCMDToFile")) {
            try {
                cmdFile = new BufferedWriter(new FileWriter(plugin.getDataFolder() + "/setcmds.txt"));
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
                e.printStackTrace();
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
        registerSetCommand(new SetPlayersGamemodeCommand());
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
        registerSetCommand(new SetAllowEnderpearlsCommand());
        registerSetCommand(new SetStartTimeCommand());
        registerSetCommand(new SetAllowMultiplayerCheckpointsCommand());
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

        if (plugin.getConfig().getBoolean("outputCMDToFile")) {
            try {
                cmdFile.write("|}");
                cmdFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void registerSetCommand(ICommand command) {
        parameterList.put(command.getName(), command);

        if (plugin.getConfig().getBoolean("outputCMDToFile")) {
            try {

                cmdFile.write("|-");
                cmdFile.newLine();
                cmdFile.write("| '''" + command.getName() + "'''");
                cmdFile.newLine();
                if (command.getUsage() != null) {
                    int count = 0;
                    cmdFile.write("| ");
                    for (String use : command.getUsage()) {
                        cmdFile.write(use);
                        count++;
                        if (count != command.getUsage().length) {
                            cmdFile.write("\n\n");
                        }
                    }
                } else
                    cmdFile.write("| N/A");
                cmdFile.newLine();
                if (command.getDescription() != null)
                    cmdFile.write("| " + command.getDescription());
                else
                    cmdFile.write("| N/A");
                cmdFile.newLine();
                if (command.getPermission() != null)
                    cmdFile.write("| " + command.getPermission());
                else
                    cmdFile.write("| N/A");
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
                } else
                    cmdFile.write("| N/A");
                cmdFile.newLine();

            } catch (IOException e) {
                //Failed to write
            }
        }
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Modifies a Minigame using special parameters for each game type.";
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> <Parameters>..."};
    }

    @Override
    public String[] getParameters() {
        String[] parameters = new String[parameterList.size()];
        int inc = 0;
        for (String key : parameterList.keySet()) {
            parameters[inc] = key;
            inc++;
        }
        return parameters;
    }

    @Override
    public String getPermissionMessage() {
        return null;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame, String label, String[] args) {
        Player ply = null;
        if (sender instanceof Player) {
            ply = (Player) sender;
        }

        if (args != null) {
            ICommand comd = null;
            Minigame mgm = null;
            String[] shortArgs = null;

            if (args.length >= 1) {
                if (plugin.getMinigameManager().hasMinigame(args[0])) {
                    mgm = plugin.getMinigameManager().getMinigame(args[0]);
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

                if (args != null && args.length > 2) {
                    shortArgs = new String[args.length - 2];
                    System.arraycopy(args, 2, shortArgs, 0, args.length - 2);
                }
            }

            if (comd != null && mgm != null) {
                if ((ply == null && comd.canBeConsole()) || ply != null) {
                    if (ply == null || (comd.getPermission() == null || ply.hasPermission(comd.getPermission()))) {
                        boolean returnValue = comd.onCommand(sender, mgm, label, shortArgs);
                        if (!returnValue) {
                            sender.sendMessage(ChatColor.GREEN + "------------------Command Info------------------");
                            sender.sendMessage(ChatColor.BLUE + "Description: " + ChatColor.WHITE + comd.getDescription());
                            if (comd.getParameters() != null) {
                                String parameters = "";
                                boolean switchColour = false;
                                for (String par : comd.getParameters()) {
                                    if (switchColour) {
                                        parameters += ChatColor.WHITE + par;
                                        if (!par.equalsIgnoreCase(comd.getParameters()[comd.getParameters().length - 1])) {
                                            parameters += ChatColor.WHITE + ", ";
                                        }
                                        switchColour = false;
                                    } else {
                                        parameters += ChatColor.GRAY + par;
                                        if (!par.equalsIgnoreCase(comd.getParameters()[comd.getParameters().length - 1])) {
                                            parameters += ChatColor.WHITE + ", ";
                                        }
                                        switchColour = true;
                                    }
                                }
                                sender.sendMessage(ChatColor.BLUE + "Parameters: " + parameters);
                            }
                            sender.sendMessage(ChatColor.BLUE + "Usage: ");
                            sender.sendMessage(comd.getUsage());
                            if (comd.getAliases() != null) {
                                String aliases = "";
                                boolean switchColour = false;
                                for (String alias : comd.getAliases()) {
                                    if (switchColour) {
                                        aliases += ChatColor.WHITE + alias;
                                        if (!alias.equalsIgnoreCase(comd.getAliases()[comd.getAliases().length - 1])) {
                                            aliases += ChatColor.WHITE + ", ";
                                        }
                                        switchColour = false;
                                    } else {
                                        aliases += ChatColor.GRAY + alias;
                                        if (!alias.equalsIgnoreCase(comd.getAliases()[comd.getAliases().length - 1])) {
                                            aliases += ChatColor.WHITE + ", ";
                                        }
                                        switchColour = true;
                                    }
                                }
                                sender.sendMessage(ChatColor.BLUE + "Aliases: " + aliases);
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + comd.getPermissionMessage());
                        sender.sendMessage(ChatColor.RED + comd.getPermission());
                    }
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "You must be a player to execute this command!");
                    return true;
                }
            } else if (mgm == null) {
                sender.sendMessage(ChatColor.RED + "There is no Minigame by the name \"" + args[0] + "\"");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame, String alias, String[] args) {
        if (args != null && args.length > 0) {
            Player ply = null;
            if (sender instanceof Player) {
                ply = (Player) sender;
            }
            ICommand comd = null;
            String[] shortArgs = null;
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
                        List<String> l = comd.onTabComplete(sender, mgm, alias, shortArgs);
                        if (l != null)
                            return l;
                        else
                            return MinigameUtils.stringToList("");
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
