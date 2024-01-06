package au.com.mineauz.minigames;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.tool.MinigameTool;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

public class MinigameUtils {

    /**
     * Converts seconds into weeks, days, hours, minutes and seconds to be neatly
     * displayed.
     *
     * @param time  - The time in seconds to be converted
     * @param small - If the time should be shortened to: hh:mm:ss
     * @return A message with a neat time
     */
    public static String convertTime(int time, boolean small) { //todo use TimeUnit; make reverse methode
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        int rtime = time;
        String msg = "";

        if (time > 604800) {
            weeks = rtime / 604800;
            rtime = rtime - weeks * 604800;
            days = rtime / 86400;
            rtime = rtime - days * 86400;
            hours = rtime / 3600;
            rtime = rtime - hours * 3600;
            minutes = rtime / 60;
            rtime = rtime - minutes * 60;
            seconds = rtime;
        } else if (time > 86400) {
            days = rtime / 86400;
            rtime = rtime - days * 86400;
            hours = rtime / 3600;
            rtime = rtime - hours * 3600;
            minutes = rtime / 60;
            rtime = rtime - minutes * 60;
            seconds = rtime;
        } else if (time > 3600) {
            hours = rtime / 3600;
            rtime = rtime - hours * 3600;
            minutes = rtime / 60;
            seconds = rtime - minutes * 60;
        } else if (time > 60) {
            minutes = time / 60;
            seconds = rtime - minutes * 60;
        } else {
            seconds = time;
        }

        if (weeks != 0) {
            if (!small)
                msg = String.format(getLang("time.weeks"), weeks);
            else {
                msg = weeks + "w";
            }
        }
        if (days != 0) {
            if (!msg.isEmpty()) {
                if (!small) {
                    if (seconds != 0 || hours != 0 || minutes != 0) {
                        msg += ", ";
                    } else {
                        msg += " " + getLang("time.and") + " ";
                    }
                } else {
                    msg += ":";
                }
            }
            if (!small)
                msg += String.format(getLang("time.days"), days);
            else
                msg += days + "d";
        }
        if (hours != 0) {
            if (!msg.isEmpty()) {
                if (!small) {
                    if (seconds != 0 || minutes != 0) {
                        msg += ", ";
                    } else {
                        msg += " " + getLang("time.and") + " ";
                    }
                } else
                    msg += ":";
            }
            if (!small)
                msg += String.format(getLang("time.hours"), hours);
            else
                msg += hours + "h";
        }
        if (minutes != 0) {
            if (!msg.isEmpty()) {
                if (!small) {
                    if (seconds != 0) {
                        msg += ", ";
                    } else {
                        msg += " " + getLang("time.and") + " ";
                    }
                } else
                    msg += ":";
            }
            if (!small)
                msg += String.format(getLang("time.minutes"), minutes);
            else
                msg += minutes + "m";
        }
        if (seconds != 0 || msg.isEmpty()) {
            if (!msg.isEmpty()) {
                if (!small)
                    msg += " " + getLang("time.and") + " ";
                else
                    msg += ":";
            }
            if (!small)
                msg += String.format(getLang("time.seconds"), seconds);
            else
                msg += seconds + "s";
        }

        return msg;
    }

    /**
     * Converts seconds into weeks, days, hours, minutes and seconds to be neatly
     * displayed.
     *
     * @param time - The time in seconds to be converted
     * @return A message with a neat time
     */
    public static String convertTime(int time) {
        return convertTime(time, false);
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
