package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SetBlockWhitelistCommand implements ICommand {

    @Override
    public String getName() {
        return "blockwhitelist";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"bwl", "blockwl"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Adds, removes and changes whitelist mode on or off (off by default). " +
                "When off, it is in blacklist mode, meaning the blocks in the list are the only blocks that list can't be placed or destroyed";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"add", "remove", "list", "clear"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "/minigame set <Minigame> blockwhitelist <true/false>",
                "/minigame set <Minigame> blockwhitelist add <Block type>",
                "/minigame set <Minigame> blockwhitelist remove <Block type>",
                "/minigame set <Minigame> blockwhitelist list",
                "/minigame set <Minigame> blockwhitelist clear"
        };
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to edit the block whitelist!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.blockwhitelist";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        if (args != null) {
            if (args[0].equalsIgnoreCase("add") && args.length >= 2) {
                if (Material.matchMaterial(args[1].toUpperCase()) != null) {
                    Material mat = Material.matchMaterial(args[1].toUpperCase());

                    minigame.getRecorderData().addWBBlock(mat);

                    final String matStr = mat.toString().replace("_", " ").toLowerCase();
                    if (minigame.getRecorderData().getWhitelistMode()) {
                        sender.sendMessage(ChatColor.GRAY + "Added " + matStr + " to the whitelist for " + minigame);
                    } else {
                        sender.sendMessage(ChatColor.GRAY + "Added " + matStr + " to the blacklist for " + minigame);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid item name or ID!");
                }
            } else if (args[0].equalsIgnoreCase("remove") && args.length >= 2) {
                if (Material.matchMaterial(args[1].toUpperCase()) != null) {
                    Material mat = Material.matchMaterial(args[1].toUpperCase());

                    minigame.getRecorderData().removeWBBlock(mat);

                    final String matStr = mat.toString().replace("_", " ").toLowerCase();
                    if (minigame.getRecorderData().getWhitelistMode()) {
                        sender.sendMessage(ChatColor.GRAY + "Removed " + matStr + " from the whitelist for " + minigame);
                    } else {
                        sender.sendMessage(ChatColor.GRAY + "Removed " + matStr + " from the blacklist for " + minigame);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid item name or ID!");
                }
            } else if (args[0].equalsIgnoreCase("clear")) {
                minigame.getRecorderData().getWBBlocks().clear();
                if (minigame.getRecorderData().getWhitelistMode()) {
                    sender.sendMessage(ChatColor.GRAY + "Cleared all blocks from the whitelist for " + minigame);
                } else {
                    sender.sendMessage(ChatColor.GRAY + "Cleared all blocks from the blacklist for " + minigame);
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                StringBuilder blocks = new StringBuilder();
                boolean switchColour = false;
                for (Material block : minigame.getRecorderData().getWBBlocks()) {
                    if (switchColour) {
                        blocks.append(ChatColor.WHITE).append(block.toString());
                        if (!block.toString().equalsIgnoreCase(minigame.getRecorderData().getWBBlocks().get(minigame.getRecorderData().getWBBlocks().size() - 1).toString())) {
                            blocks.append(ChatColor.WHITE + ", ");
                        }
                        switchColour = false;
                    } else {
                        blocks.append(ChatColor.GRAY).append(block.toString());
                        if (!block.toString().equalsIgnoreCase(minigame.getRecorderData().getWBBlocks().get(minigame.getRecorderData().getWBBlocks().size() - 1).toString())) {
                            blocks.append(ChatColor.WHITE + ", ");
                        }
                        switchColour = true;
                    }
                }
                if (minigame.getRecorderData().getWhitelistMode()) {
                    sender.sendMessage(ChatColor.GRAY + "All blocks on the whitelist:");
                } else {
                    sender.sendMessage(ChatColor.GRAY + "All blocks on the blacklist:");
                }
                sender.sendMessage(blocks.toString());
            } else {
                boolean bool = Boolean.parseBoolean(args[0]);
                minigame.getRecorderData().setWhitelistMode(bool);
                if (bool) {
                    sender.sendMessage(ChatColor.GRAY + "Block placement and breaking is now on whitelist mode for " + minigame);
                } else {
                    sender.sendMessage(ChatColor.GRAY + "Block placement and breaking is now on blacklist mode for " + minigame);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(List.of("true", "false", "add", "remove", "list", "clear"), args[0]);
        else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            List<String> ls = new ArrayList<>();
            for (Material m : minigame.getRecorderData().getWBBlocks()) {
                ls.add(m.toString());
            }
            return MinigameUtils.tabCompleteMatch(ls, args[1]);
        }
        return null;
    }

}
