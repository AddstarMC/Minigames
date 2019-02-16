package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardOrder;
import au.com.mineauz.minigames.stats.*;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScoreboardCommand implements ICommand {
    private Minigames plugin = Minigames.getPlugin();

    @Override
    public String getName() {
        return "scoreboard";
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
        return "Displays a scoreboard of the desired Minigame, SQL must be enabled!";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame scoreboard <Minigame> <Statistic> <Field> [-o <asc/desc>|-l <length>|-s <start>]"};
    }

    @Override
    public String getPermissionMessage() {
        return "You don't have permission to view the scoreboard!";
    }

    @Override
    public String getPermission() {
        return "minigame.scoreboard";
    }

    @Override
    public boolean onCommand(final CommandSender sender, Minigame ignore, String label, String[] args) {
        if (args == null || args.length < 3) {
            return false;
        }

        // Decode arguments
        final Minigame minigame = plugin.getMinigameManager().getMinigame(args[0]);
        if (minigame == null) {
            sender.sendMessage(ChatColor.RED + "No Minigame found by the name " + args[0]);
            return true;
        }

        final MinigameStat stat = MinigameStats.getStat(args[1]);
        if (stat == null) {
            sender.sendMessage(ChatColor.RED + "No statistic found by the name " + args[1]);
            return true;
        }

        final StatSettings settings = minigame.getSettings(stat);

        StatValueField field = null;
        for (StatValueField f : settings.getFormat().getFields()) {
            if (f.name().equalsIgnoreCase(args[2])) {
                field = f;
                break;
            }
        }

        if (field == null) {
            sender.sendMessage(ChatColor.RED + "No field found by the name " + args[2] + " for the statistic " + stat.getDisplayName());
            return true;
        }

        // Prepare defaults for optionals
        ScoreboardOrder order;
        switch (field) {
            case Last:
            case Total:
            case Max:
                order = ScoreboardOrder.DESCENDING;
                break;
            case Min:
                order = ScoreboardOrder.ASCENDING;
                break;
            default:
                throw new AssertionError();
        }

        int start = 0;
        int length = 8;

        // Now the optionals
        for (int i = 3; i < args.length - 1; i += 2) {
            if (args[i].equalsIgnoreCase("-o")) {
                // Order
                if (args[i + 1].equalsIgnoreCase("asc") || args[i + 1].equalsIgnoreCase("ascending")) {
                    order = ScoreboardOrder.ASCENDING;
                } else if (args[i + 1].equalsIgnoreCase("desc") || args[i + 1].equalsIgnoreCase("descending")) {
                    order = ScoreboardOrder.DESCENDING;
                } else {
                    sender.sendMessage(ChatColor.RED + "Unknown order " + args[i + 1] + ". Expected asc, ascending, desc, or descending.");
                    return true;
                }
            } else if (args[i].equalsIgnoreCase("-l")) {
                // Length
                if (args[i + 1].matches("[1-9][0-9]*")) {
                    length = Integer.parseInt(args[i + 1]);
                } else {
                    sender.sendMessage(ChatColor.RED + "Unknown length " + args[i + 1] + ". Expected positive non-zero number");
                    return true;
                }
            } else if (args[i].equalsIgnoreCase("-s")) {
                // Start
                if (args[i + 1].matches("[1-9][0-9]*")) {
                    start = Integer.parseInt(args[i + 1]) - 1;
                } else {
                    sender.sendMessage(ChatColor.RED + "Unknown start " + args[i + 1] + ". Expected positive non-zero number");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Unknown option " + args[i] + ". Expected -o, -l, or -s");
                return true;
            }
        }

        final ScoreboardOrder fOrder = order;
        final StatValueField fField = field;

        sender.sendMessage(ChatColor.GRAY + "Loading scoreboard...");
        // Now load the values
        ListenableFuture<List<StoredStat>> future = plugin.getBackend().loadStats(minigame, stat, field, order, start, length);

        Futures.addCallback(future, new FutureCallback<List<StoredStat>>() {
            @Override
            public void onSuccess(List<StoredStat> result) {
                sender.sendMessage(ChatColor.GREEN + minigame.getName(true) + " Scoreboard: " + settings.getDisplayName() + " - " + fField.getTitle() + " " + fOrder.toString().toLowerCase());
                for (StoredStat playerStat : result) {
                    sender.sendMessage(ChatColor.AQUA + playerStat.getPlayerDisplayName() + ": " + ChatColor.WHITE + stat.displayValue(playerStat.getValue(), settings));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                sender.sendMessage(ChatColor.RED + "An internal error occured while loading the statistics");
                t.printStackTrace();
            }
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame ignore, String alias, String[] args) {
        if (args.length == 1) { // Minigame
            List<String> mgs = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
            return MinigameUtils.tabCompleteMatch(mgs, args[0]);
        } else if (args.length == 2) { // Stat
            return MinigameUtils.tabCompleteMatch(Lists.newArrayList(MinigameStats.getAllStats().keySet()), args[1]);
        } else if (args.length == 3) { // Field
            MinigameStat stat = MinigameStats.getStat(args[1]);
            if (stat == null) {
                return null;
            }

            final Minigame minigame = plugin.getMinigameManager().getMinigame(args[0]);
            StatFormat format;
            if (minigame == null) {
                format = stat.getFormat();
            } else {
                StatSettings settings = minigame.getSettings(stat);
                format = settings.getFormat();
            }

            String toMatch = args[2].toLowerCase();
            List<String> matches = Lists.newArrayList();
            for (StatValueField field : format.getFields()) {
                if (field.name().toLowerCase().startsWith(toMatch)) {
                    matches.add(field.name());
                }
            }

            return matches;
        } else if (args.length > 3) {
            if (args.length % 2 == 0) {
                // Option
                return MinigameUtils.tabCompleteMatch(Arrays.asList("-o", "-l", "-s"), args[args.length - 1]);
            } else {
                // Option Parameter
                String previous = args[args.length - 2].toLowerCase();
                String toMatch = args[args.length - 1].toLowerCase();

                if (previous.equals("-o")) {
                    // Order
                    return MinigameUtils.tabCompleteMatch(Arrays.asList("asc", "ascending", "desc", "descending"), toMatch);
                }
                // The others cannot be tab completed
            }
        }

        return null;
    }
}
