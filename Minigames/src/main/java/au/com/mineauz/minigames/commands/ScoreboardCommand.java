package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardOrder;
import au.com.mineauz.minigames.stats.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ScoreboardCommand extends ACommand {
    private final Minigames plugin = Minigames.getPlugin();

    @Override
    public @NotNull String getName() {
        return "scoreboard";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SCOREBOARD_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SCOREBOARD_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.scoreboard";
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length < 3) {
            return false;
        }

        // Decode arguments
        final Minigame minigame = plugin.getMinigameManager().getMinigame(args[0]);
        if (minigame == null) {
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOMINIGAME,
                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), args[0]));
            return true;
        }

        final MinigameStat stat = MinigameStatistics.getStat(args[1]);
        if (stat == null) {
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_SCOREBOARD_ERROR_NOTSTAT,
                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
            return true;
        }

        final StatSettings settings = minigame.getSettings(stat);

        StatisticValueField field = null;
        for (StatisticValueField f : settings.getFormat().getFields()) {
            if (f.name().equalsIgnoreCase(args[2])) {
                field = f;
                break;
            }
        }

        if (field == null) {
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_SCOREBOARD_ERROR_NOTFIELD,
                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[2]),
                    Placeholder.component(MinigamePlaceHolderKey.SCORE.getKey(), stat.getDisplayName()));
            return true;
        }

        // Prepare defaults for optionals
        ScoreboardOrder order = switch (field) {
            case Last, Total, Max -> ScoreboardOrder.DESCENDING;
            case Min -> ScoreboardOrder.ASCENDING;
        };

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
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_SCOREBOARD_ERROR_NOTORDER,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[i + 1]));
                    return false;
                }
            } else if (args[i].equalsIgnoreCase("-l")) {
                // Length
                if (args[i + 1].matches("[1-9][0-9]*")) {
                    length = Integer.parseInt(args[i + 1]);
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTNUMBER,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[i + 1]));
                    return false;
                }
            } else if (args[i].equalsIgnoreCase("-s")) {
                // Start
                if (args[i + 1].matches("[1-9][0-9]*")) {
                    start = Integer.parseInt(args[i + 1]) - 1;
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTNUMBER,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[i + 1]));
                    return false;
                }
            } else {
                return false;
            }
        }

        final ScoreboardOrder fOrder = order;
        final StatisticValueField fField = field;

        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SCOREBOARD_LOAD);
        // Now load the values
        CompletableFuture<List<StoredStat>> future = plugin.getBackend().loadStats(minigame, stat, field, order, start, length);
        future.whenComplete((result, throwable) -> {
            if (throwable == null) {
                MinigameMessageManager.sendMessage(sender, MinigameMessageType.NONE,
                        minigame.getDisplayName().append( //todo don't hardcode this
                                Component.text(" Scoreboard: " + settings.getDisplayName() + " - " + fField.getTitle() + " " + fOrder.toString().toLowerCase(), NamedTextColor.GREEN)
                        ));
                for (StoredStat playerStat : result) {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MgCommandLangKey.COMMAND_SCOREBOARD_LIST_PLAYER,
                            Placeholder.component(MinigamePlaceHolderKey.PLAYER.getKey(), playerStat.getPlayerDisplayName()),
                            Placeholder.component(MinigamePlaceHolderKey.NUMBER.getKey(), stat.displayValue(playerStat.getValue(), settings)));
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_INTERNAL);
                Minigames.getCmpnntLogger().error("An internal error occurred while loading the statistics", throwable);
            }
        });

        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) { // Minigame
            List<String> mgs = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
            return MinigameUtils.tabCompleteMatch(mgs, args[0]);
        } else if (args.length == 2) { // Stat
            return MinigameUtils.tabCompleteMatch(new ArrayList<>(MinigameStatistics.getAllStats().keySet()), args[1]);
        } else if (args.length == 3) { // Field
            MinigameStat stat = MinigameStatistics.getStat(args[1]);
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
            List<String> matches = new ArrayList<>();
            for (StatisticValueField field : format.getFields()) {
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
