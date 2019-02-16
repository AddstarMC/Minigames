package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetRandomizeChestsCommand implements ICommand {

    @Override
    public String getName() {
        return "randomizechests";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"randomisechests", "randomchests", "rchests"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "All chests in a Minigame will have their items randomized as soon as a Minigame player opens one.\n" +
                "Items will only be randomized once and will be reverted to their default state after the game ends. A " +
                "new game will result in different items again. The number of items that are set in the chests are defined in this command.\n" +
                "(Defaults: false, min: 5, max: 10)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> randomizechests <true/false>",
                "/minigame set <Minigame> randomizechests <minValue> <maxValue>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to enable randomization of chests in a Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.randomizechests";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args.length == 1) {
                boolean bool = Boolean.parseBoolean(args[0]);
                minigame.setRandomizeChests(bool);
                if (bool) {
                    sender.sendMessage(ChatColor.GRAY + "Chest randomization has been enabled for " + minigame);
                } else {
                    sender.sendMessage(ChatColor.GRAY + "Chest randomization has been disabled for " + minigame);
                }
                return true;
            } else if (args.length >= 2 && args[0].matches("[0-9]+") && args[1].matches("[0-9]+")) {
                int min = Integer.parseInt(args[0]);
                int max = Integer.parseInt(args[1]);
                minigame.setMinChestRandom(min);
                minigame.setMaxChestRandom(max);

                sender.sendMessage(ChatColor.GRAY + "Chest randomization set for " + minigame + ". Minimum: " + min + ", Maximum: " + max);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("true;false"), args[0]);
        return null;
    }

}
