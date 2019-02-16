package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.blockRecorder.RecorderData;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class BackupCommand implements ICommand {

    @Override
    public String getName() {
        return "backup";
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
        return "Backs up or restores the regen area of a Minigame in case of regeneration failure.\n"
                + "Note: This is not 100% accurate, some blocks may not return to their original state.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "/minigame backup <Minigame> [restore]"
        };
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to backup Minigames!";
    }

    @Override
    public String getPermission() {
        return "minigame.backup";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (Minigames.getPlugin().getMinigameManager().hasMinigame(args[0])) {
                minigame = Minigames.getPlugin().getMinigameManager().getMinigame(args[0]);
                if (minigame.getRegenArea1() != null && minigame.getRegenArea2() != null) {
                    if (args.length == 1) {
                        if (minigame.getPlayers().size() == 0) {
                            minigame.setState(MinigameState.REGENERATING);
                            Minigames.getPlugin().getMinigameManager().addBlockRecorderData(minigame);
                            RecorderData d = minigame.getBlockRecorder();
                            d.saveAllBlockData();

                            d.clearRestoreData();

                            minigame.setState(MinigameState.IDLE);

                            sender.sendMessage(ChatColor.GRAY + minigame.getName(false) + " has been successfully backed up!");
                        } else {
                            sender.sendMessage(ChatColor.RED + minigame.getName(false) + " has players playing, can't be backed up until Minigame is empty.");
                        }
                    } else if (args.length == 2 && args[1].equalsIgnoreCase("restore")) {
                        if (minigame.getPlayers().size() == 0) {

                            if (!minigame.getBlockRecorder().restoreBlockData()) {
                                sender.sendMessage(ChatColor.RED + "No backup found for " + minigame.getName(false));
                                return true;
                            }
                            minigame.getBlockRecorder().restoreBlocks();

                            sender.sendMessage(ChatColor.GRAY + minigame.getName(false) + " is now restoring from backup.");
                        } else {
                            sender.sendMessage(ChatColor.RED + minigame.getName(false) + " has players playing, can't be restored until Minigame is empty.");
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + minigame.getName(false) + " has no regen area!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "No Minigame found by the name '" + args[0] + "'!");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args != null) {
            if (args.length == 1) {
                return MinigameUtils.tabCompleteMatch(new ArrayList<>(Minigames.getPlugin().getMinigameManager().getAllMinigames().keySet()), args[0]);
            } else if (args.length == 2) {
                return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("restore"), args[1]);
            }
        }
        return null;
    }

}
