package au.com.mineauz.minigames;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.tool.MinigameTool;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinigameUtils {
    private static final @NotNull Pattern PERIOD_PATTERN = Pattern.compile("(\\d+)\\s*((ms)|[tsmhdw])", Pattern.CASE_INSENSITIVE);

    /**
     * Try to get a time period of a string.
     * using the same time unit more than once is permitted.
     * If no time unit follows a number, it gets treated as seconds.
     *
     * @return the parsed duration in milliseconds, or null if not possible
     */
    public static @Nullable Long parsePeriod(@NotNull String periodStr) {
        Matcher matcher = PERIOD_PATTERN.matcher(periodStr);
        Long millis = null;

        while (matcher.find()) {
            // we got a match.
            if (millis == null) {
                millis = 0L;
            }

            try {
                long num = Long.parseLong(matcher.group(1));

                if (matcher.groupCount() > 1) {
                    String typ = matcher.group(2);
                    millis += switch (typ) { // from periodPattern
                        case "ms" -> num;
                        case "t" -> TimeUnit.SECONDS.toMillis(20L * num); // ticks
                        case "s" -> TimeUnit.SECONDS.toMillis(num);
                        case "m" -> TimeUnit.MINUTES.toMillis(num);
                        case "h" -> TimeUnit.HOURS.toMillis(num);
                        case "d" -> TimeUnit.DAYS.toMillis(num);
                        case "w" -> TimeUnit.DAYS.toMillis(num * 7);
                        default -> 0; // should never get reached because of pattern
                    };
                } else {
                    millis += TimeUnit.SECONDS.toMillis(num);
                }
            } catch (NumberFormatException e) {
                Minigames.getPlugin().getComponentLogger().warn("Couldn't get time period for " + periodStr, e);
            }
        }
        return millis;
    }

    /**
     * Converts seconds into weeks, days, hours, minutes and seconds to be neatly
     * displayed.
     *
     * @param duration - The duration to be converted
     * @param small    - If the time should be shortened to: hh:mm:ss
     * @return A message with a neat time
     */
    public static Component convertTime(Duration duration, boolean small) { //todo make reverse methode
        long weeks = duration.toDaysPart() / 7L;
        long days = duration.toDaysPart() % 7L;
        int hours = duration.toHoursPart();
        int minutes = duration.toMinutesPart();
        int seconds = duration.toSecondsPart();

        Stack<Component> timeComponents = new Stack<>();

        if (small) {
            if (weeks != 0) {
                timeComponents.add(MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_WEEKS_SHORT,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(weeks))));
            }
            if (days != 0) {
                timeComponents.add(MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_DAYS_SHORT,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(days))));
            }
            if (hours != 0) {
                timeComponents.add(MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_HOURS_SHORT,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(hours))));
            }
            if (minutes != 0) {
                timeComponents.add(MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_MINUTES_SHORT,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(minutes))));
            }

            if (seconds != 0 || timeComponents.isEmpty()) {
                timeComponents.add(MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_SECONDS_SHORT,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(seconds))));
            }

            return Component.join(JoinConfiguration.separator(Component.text(":")), timeComponents);
        } else {
            if (weeks != 0) {
                timeComponents.add(MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_WEEKS_LONG,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(weeks))));
            }
            if (days != 0) {
                timeComponents.add(MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_DAYS_LONG,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(days))));
            }
            if (hours != 0) {
                timeComponents.add(MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_HOURS_LONG,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(hours))));
            }
            if (minutes != 0) {
                timeComponents.add(MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_MINUTES_LONG,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(minutes))));
            }
            if (seconds != 0 || timeComponents.isEmpty()) {
                timeComponents.add(MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_SECONDS_LONG,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(seconds))));
            }

            Component lastTimeComponent = timeComponents.pop();
            if (!timeComponents.isEmpty()) {
                return Component.join(JoinConfiguration.commas(true), timeComponents).
                        appendSpace().append(MinigameMessageManager.getMgMessage(MinigameLangKey.TIME_AND)).appendSpace().
                        append(lastTimeComponent);
            } else {
                return lastTimeComponent;
            }
        }
    }

    /**
     * Converts seconds into weeks, days, hours, minutes and seconds to be neatly
     * displayed.
     *
     * @param duration - The time to be converted
     * @return A message with a neat time
     */
    public static Component convertTime(Duration duration) {
        return convertTime(duration, false);
    }

    /**
     * Creates a string ID to compare locations.
     *
     * @param location - The location to give an ID to.
     * @return The ID
     */
    public static String createLocationID(Location location) {
        return location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ() + ":" + location.getWorld().getName();
    }

    /**
     * Gives the defined player a Minigame tool.
     *
     * @param player - The player to give the tool to.
     * @return The Minigame Tool
     */
    public static MinigameTool giveMinigameTool(MinigamePlayer player) {
        Material toolMat = Material.matchMaterial(Minigames.getPlugin().getConfig().getString("tool"));
        if (toolMat == null) {
            toolMat = Material.BLAZE_ROD;
            MinigameMessageManager.sendMgMessage(player, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NODEFAULTTOOL);
        }

        ItemStack tool = new ItemStack(toolMat);
        MinigameTool mgTool = new MinigameTool(tool);

        player.getPlayer().getInventory().addItem(mgTool.getTool());

        return mgTool;
    }

    /**
     * Checks if a player has a Minigame tool.
     *
     * @param player The player to check
     * @return false if the player doesn't have one.
     */
    public static boolean hasMinigameTool(MinigamePlayer player) {
        for (ItemStack i : player.getPlayer().getInventory().getContents()) {
            if (i != null && i.getItemMeta() != null &&
                    i.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Minigame Tool")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a specific item is a Minigame tool
     *
     * @param item The item to check
     * @return false if the item was not a Minigame tool
     */
    public static boolean isMinigameTool(@Nullable ItemStack item) {
        return item != null && item.getItemMeta() != null && item.getItemMeta().displayName() != null && item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Minigame Tool");
    }

    /**
     * Gets the item, Minigames considers as a Minigame tool, from the players inventory
     * It will prefer the item in main/offhand
     *
     * @param player The player to get the tool from
     * @return null if no tool was found
     */
    public static @Nullable MinigameTool getMinigameTool(@NotNull MinigamePlayer player) {
        ItemStack inHand = player.getPlayer().getInventory().getItemInMainHand();
        if (isMinigameTool(inHand)) {
            return new MinigameTool(inHand);
        }

        inHand = player.getPlayer().getInventory().getItemInOffHand();
        if (isMinigameTool(inHand)) {
            return new MinigameTool(inHand);
        }

        //was not in hands, search in inventory.
        for (ItemStack item : player.getPlayer().getInventory().getContents()) {
            if (isMinigameTool(item)) {
                return new MinigameTool(item);
            }
        }
        return null;
    }

    /**
     * Automatically assembles a tab complete array for the use in commands, matching a given string.
     *
     * @param orig  The full list to match the string to
     * @param match The string used to match
     * @return A list of possible tab completions
     */
    public static List<String> tabCompleteMatch(List<String> orig, String match) {
        if (match.isEmpty()) {
            return orig;
        } else {
            return orig.stream().filter(m -> m.toLowerCase().startsWith(match.toLowerCase())).toList();
        }
    }

    /**
     * Loads a short location (x, y, z, world) from a configuration section
     *
     * @param section The section that contains the fields
     * @return A location with the contents of that section, or null if the world is invalid
     */
    public static Location loadShortLocation(ConfigurationSection section) {
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");

        String worldName = section.getString("world");
        World world = Bukkit.getWorld(worldName);

        if (world != null) {
            return new Location(world, x, y, z);
        } else {
            return null;
        }
    }

    /**
     * Saves a short location (x, y, z, world) to a configuration section
     *
     * @param section  The ConfigurationSection to save into
     * @param location The location to save
     */
    public static void saveShortLocation(ConfigurationSection section, Location location) {
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("world", location.getWorld().getName());
    }

    /**
     * Limits the length of a string ignoring all colour codes within it
     *
     * @param string    The string to limit
     * @param maxLength The maximum number of characters to allow
     * @return The string that is never longer than maxLength with chat colours stripped out
     */
    public static String limitIgnoreCodes(String string, int maxLength) {
        if (string.length() <= maxLength) {
            return string;
        }

        int size = 0;
        int chompStart = -1;
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);

            if (c == ChatColor.COLOR_CHAR) {
                ++i;
                continue;
            }

            ++size;
            if (size > maxLength) {
                chompStart = i;
                break;
            }
        }

        if (chompStart != -1) {
            return string.substring(0, chompStart);
        } else {
            return string;
        }
    }

    public static String sanitizeYamlString(String input) {
        final Pattern pattern = Pattern.compile("^[a-zA-Z\\d_]+$");
        if (!pattern.matcher(input).matches()) {
            return null;
        } else {
            return input;
        }
    }
}
