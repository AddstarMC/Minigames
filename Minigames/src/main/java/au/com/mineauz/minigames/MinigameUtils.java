package au.com.mineauz.minigames;

import au.com.mineauz.minigames.managers.MessageManager;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.tool.MinigameTool;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Pattern;

//import net.minecraft.server.v1_6_R2.EntityPlayer;
//
//import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;

public class MinigameUtils {

    /**
     * Returns the item stack from a number or name.
     *
     * @param item     - The items name or ID.
     * @param quantity - The number of said item
     * @return The ItemStack referred to in the parameter.
     */
    public static ItemStack stringToItemStack(String item, int quantity) {
        String itemName = "";
        short itemData = 0;
        String[] split = null;

        if (item.contains(":")) {
            split = item.split(":");
            itemName = split[0].toUpperCase();
            if (split[1].matches("[0-9]+")) {
                itemData = Short.parseShort(split[1]);
            }
        } else {
            itemName = item.toUpperCase();
        }

        ItemStack it = null;

        if (Material.getMaterial(itemName) != null) {
            it = new ItemStack(Material.getMaterial(itemName), quantity);
        }
        return it;
    }

    /**
     * Gets the name of an ItemStack
     *
     * @param item - The ItemStack to get the name of
     * @return The name of the item
     */
    public static String getItemStackName(ItemStack item) {
        return item.getType().toString().toLowerCase().replace("_", " ");
    }

    /**
     * Converts seconds into weeks, days, hours, minutes and seconds to be neatly
     * displayed.
     *
     * @param time  - The time in seconds to be converted
     * @param small - If the time should be shortened to: hh:mm:ss
     * @return A message with a neat time
     */
    public static String convertTime(int time, boolean small) {
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
            if (!msg.equals("")) {
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
            if (!msg.equals("")) {
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
            if (!msg.equals("")) {
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
        if (seconds != 0 || msg.equals("")) {
            if (!msg.equals("")) {
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
     * Turns a list to a string.
     *
     * @param list the list to convert
     * @return A string representation of the list
     */
    public static String listToString(List<String> list) {
        String slist = "";
        boolean switchColour = false;
        for (String entry : list) {
            if (switchColour) {
                slist += ChatColor.WHITE + entry;
                if (!entry.equalsIgnoreCase(list.get(list.size() - 1))) {
                    slist += ChatColor.WHITE + ", ";
                }
                switchColour = false;
            } else {
                slist += ChatColor.GRAY + entry;
                if (!entry.equalsIgnoreCase(list.get(list.size() - 1))) {
                    slist += ChatColor.WHITE + ", ";
                }
                switchColour = true;
            }
        }
        return slist;
    }

    /**
     * Converts a string to a list. Separate each list item with a ";".
     *
     * @param toList - String to be turned into a list.
     * @return A List with the defined items.
     */
    public static List<String> stringToList(String toList) {
        String[] st = toList.split(";");
        List<String> list = new ArrayList<>();
        Collections.addAll(list, st);
        return list;
    }

    /**
     * Formats a string from the language file.
     *
     * @param format - The location in the YAML of the string to format.
     * @param text   - What to replace the formatted variables with.
     * @return The formatted string. If not found, will return the format
     * @deprecated use {@link MessageManager#getMinigamesMessage(String, String...)}
     */
    @Deprecated
    public static String formStr(String format, Object... text) {
        String lang = getLang(format);
        try {
            return String.format(lang, text);
        }catch (IllegalArgumentException e){
            return lang;
        }
    }

    /**
     * Gets the language string from the localization file.
     *
     * @param arg1 - The path of the language string
     * @return The translation. If not found, will return the argument.
     * @deprecated use {@link MessageManager#getUnformattedMessage(String, String)}
     */
    @Deprecated
    public static String getLang(String arg1) {
        String out;
        try {
            out = MessageManager.getUnformattedMessage(null, arg1);
        }catch (MissingResourceException e){
            out = "No path found in: " +e.getMessage() +" for " + e.getKey();
        }
        return out;
    }

    /**
     * Gives the defined player a Minigame tool.
     *
     * @param player - The player to give the tool to.
     * @return The Minigame Tool
     */
    public static MinigameTool giveMinigameTool(MinigamePlayer player) {
        Material toolMat = Material.getMaterial(Minigames.getPlugin().getConfig().getString("tool"));
        if (toolMat == null) {
            toolMat = Material.BLAZE_ROD;
            MessageManager.sendMessage(player,MinigameMessageType.ERROR,null,"minigame.error.noDefaultTool");
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
            if (i != null && i.getItemMeta() != null && i.getItemMeta().getDisplayName() != null &&
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
    public static boolean isMinigameTool(ItemStack item) {
        return item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Minigame Tool");
    }

    /**
     * Gets the item Minigames considers a Miniame tool from the players inventory
     *
     * @param player The player to get the tool from
     * @return null if no tool was found
     */
    public static MinigameTool getMinigameTool(MinigamePlayer player) {
        for (ItemStack i : player.getPlayer().getInventory().getContents()) {
            if (i != null && i.getItemMeta() != null && i.getItemMeta().getDisplayName() != null &&
                    i.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Minigame Tool")) {
                return new MinigameTool(i);
            }
        }
        return null;
    }

    /**
     * Creates an array containing the smallest and largest points in a selection.
     *
     * @param selection1 First point of the selection
     * @param selection2 Second point of the selection
     * @return an array containing 2 locations, first being the smallest point, second being the largest
     */
    public static Location[] getMinMaxSelection(Location selection1, Location selection2) {
        int minx;
        int maxx;
        int miny;
        int maxy;
        int minz;
        int maxz;

        if (selection1.getBlockX() > selection2.getBlockX()) {
            minx = selection2.getBlockX();
            maxx = selection1.getBlockX();
        } else {
            minx = selection1.getBlockX();
            maxx = selection2.getBlockX();
        }
        if (selection1.getBlockY() > selection2.getBlockY()) {
            miny = selection2.getBlockY();
            maxy = selection1.getBlockY();
        } else {
            miny = selection1.getBlockY();
            maxy = selection2.getBlockY();
        }
        if (selection1.getBlockZ() > selection2.getBlockZ()) {
            minz = selection2.getBlockZ();
            maxz = selection1.getBlockZ();
        } else {
            minz = selection1.getBlockZ();
            maxz = selection2.getBlockZ();
        }

        Location[] arr = new Location[2];
        arr[0] = new Location(selection1.getWorld(), minx, miny, minz);
        arr[1] = new Location(selection1.getWorld(), maxx, maxy, maxz);
        return arr;
    }

    /**
     * Capitalizes the first letter of every word in a sentence.
     *
     * @param toCapitalize The string to capitalize
     * @return The capitalized string
     * @deprecated use {@link org.apache.commons.lang.WordUtils#capitalize(String)}
     */
    @Deprecated
    public static String capitalize(String toCapitalize) {
        if (toCapitalize == null) return null;
        String val = toCapitalize.toLowerCase();
        String[] spl = val.split(" ");
        val = "";
        for (String s : spl) {
            String c = Character.toString(s.charAt(0));
            s = s.substring(1);
            c = c.toUpperCase();
            s = c + s;
            val += s + " ";
        }
        val = val.trim();
        return val;
    }

    /**
     * Automatically assembles a tab complete array for the use in commands, matching a given string.
     *
     * @param orig  The full list to match the string to
     * @param match The string used to match
     * @return A list of possible tab completions
     */
    public static List<String> tabCompleteMatch(List<String> orig, String match) {
        if (match.equals(""))
            return orig;
        else {
            List<String> ret = new ArrayList<>(orig.size());
            for (String m : orig) {
                if (m.toLowerCase().startsWith(match.toLowerCase()))
                    ret.add(m);
            }
            return ret;
        }
    }

    /**
     * Serializes a location to be stored in a config file.
     *
     * @param loc The location to be stored
     * @return A map of values to store
     */
    public static Map<String, Object> serializeLocation(Location loc) {
        Map<String, Object> sloc = new HashMap<>();
        sloc.put("x", loc.getX());
        sloc.put("y", loc.getY());
        sloc.put("z", loc.getZ());
        sloc.put("yaw", loc.getYaw());
        sloc.put("pitch", loc.getPitch());
        String name;
        if (loc.getWorld() != null) {
            name = loc.getWorld().getName();
        } else {
            debugMessage("A Location could not be deserialized the world was null");
            return Collections.emptyMap();
        }
        sloc.put("world", name);
        return sloc;
    }

    /**
     * Broadcasts a message with a defined permission.
     *
     * @param message    - The message to be broadcast (Can be manipulated with MinigamesBroadcastEvent)
     * @param minigame   - The Minigame this broadcast is related to.
     * @param permission - The permission required to see this broadcast message.
     * @deprecated use {@link MessageManager#broadcast(String, Minigame, String)}
     */
    @Deprecated
    public static void broadcast(String message, Minigame minigame, String permission) {
       MessageManager.broadcast(message,minigame,permission);
    }

    /**
     * Broadcasts a server message without a permission.
     *
     * @param message     - The message to be broadcasted (Can be manipulated with MinigamesBroadcastEvent)
     * @param minigame    - The Minigame this broadcast is related to.
     * @param prefixColor - The color to be used in the prefix.
     * @deprecated use {@link MessageManager#broadcast(String, Minigame, ChatColor)}
     */
    @Deprecated
    public static void broadcast(String message, Minigame minigame, ChatColor prefixColor) {
        MessageManager.broadcast(message,minigame,prefixColor);
    }

    public static void debugMessage(String message) {
        if (Minigames.getPlugin().isDebugging()) {
            Minigames.getPlugin().getLogger().info(ChatColor.RED + "[Debug] " + ChatColor.WHITE + message);
        }
    }

    /**
     * Loads a short location (x, y, z, world) from a configuration section
     *
     * @param section The section that contains the fields
     * @return A location with the the contents of that section, or null if the world is invalid
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


//    public static void removePlayerArrows(MinigamePlayer player){
//        try{
//            Class.forName("net.minecraft.server.v1_5_R3.EntityPlayer");
//            EntityPlayer eply = ((CraftPlayer) player.getPlayer()).getHandle();
//            eply.re(0);
//        }catch(ClassNotFoundException e){}
//    }
}
