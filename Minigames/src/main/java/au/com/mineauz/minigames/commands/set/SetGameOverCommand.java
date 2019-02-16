package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.GameOverModule;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetGameOverCommand implements ICommand {

    @Override
    public String getName() {
        return "gameover";
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
        return "Modifies game over settings. Players will remain in the game until the game over timer ends.\n"
                + "For the humiliation setting, losers will be stripped of items and can't attack.\n"
                + "If interact is false, players will not be able to interact with buttons/pressure plates/doors etc.";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"timer", "invincible", "humiliation", "interact"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "/minigame set <Minigame> gameover timer <time>",
                "/minigame set <Minigame> gameover invincible <true/false>",
                "/minigame set <Minigame> gameover humiliation <true/false>",
                "/minigame set <Minigame> gameover interact <true/false>"
        };
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to modify a Minigames game over settings!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.gameover";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            GameOverModule gmo = GameOverModule.getMinigameModule(minigame);
            if (args[0].equalsIgnoreCase("timer") && args.length == 2) {
                if (args[1].matches("[0-9]+")) {
                    Integer t = Integer.parseInt(args[1]);
                    gmo.setTimer(t);
                    sender.sendMessage(ChatColor.GRAY + minigame.getName(false) + "'s game over timer has been set to " + MinigameUtils.convertTime(t));
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid time length! Value must be a number!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("invincible") && args.length == 2) {
                Boolean bool = Boolean.valueOf(args[1]);
                gmo.setInvincible(bool);
                sender.sendMessage(ChatColor.GRAY + "Set game over invincibility to " + bool.toString().toLowerCase() + " for " + minigame);
                return true;
            } else if (args[0].equalsIgnoreCase("humiliation") && args.length == 2) {
                Boolean bool = Boolean.valueOf(args[1]);
                gmo.setHumiliationMode(bool);
                sender.sendMessage(ChatColor.GRAY + "Set game over humiliation to " + bool.toString().toLowerCase() + " for " + minigame);
                return true;
            } else if (args[0].equalsIgnoreCase("interact") && args.length == 2) {
                Boolean bool = Boolean.valueOf(args[1]);
                gmo.setInteractAllowed(bool);
                sender.sendMessage(ChatColor.GRAY + "Set game over interating to " + bool.toString().toLowerCase() + " for " + minigame);
            }

        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        List<String> opts = new ArrayList<>();
        if (args.length == 1) {
            Collections.addAll(opts, getParameters());
            return MinigameUtils.tabCompleteMatch(opts, args[0]);
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("invincible") ||
                args[0].equalsIgnoreCase("humiliation") ||
                args[0].equalsIgnoreCase("interact"))) {
            opts.add("true");
            opts.add("false");
            return MinigameUtils.tabCompleteMatch(opts, args[1]);
        }
        return null;
    }

}
