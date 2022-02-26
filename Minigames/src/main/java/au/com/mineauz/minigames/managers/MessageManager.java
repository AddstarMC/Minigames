package au.com.mineauz.minigames.managers;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.events.MinigamesBroadcastEvent;
import au.com.mineauz.minigames.managers.message.UTF8Control;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import io.papermc.lib.PaperLib;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

/**
 * Class will hold and store all messages that are required for minigames
 * Created for the Charlton IT Project.
 * Created by benjicharlton on 19/06/2020.
 */
public class MessageManager {
    /**
     * Stores each prop file with a identifier
     */
    private static final Hashtable<String, ResourceBundle> propertiesHashMap = new Hashtable<>();
    private static Locale locale = Locale.getDefault();
    private static Logger logger = null;

    public static void setLocale(Locale locale) {
        MessageManager.locale = locale;
    }

    public static void setLogger(Logger logger) {
        MessageManager.logger = logger;
    }

    public static void registerCoreLanguage() {
        String tag = Minigames.getPlugin().getConfig().getString("lang", Locale.getDefault().toLanguageTag());
        locale = Locale.forLanguageTag(tag);
        Minigames.log().info("MessageManager set locale for language:" + locale.toLanguageTag());
        File file = new File(new File(Minigames.getPlugin().getDataFolder(), "lang"), "minigames.properties");
        registerCoreLanguage(file, Locale.getDefault());
    }

    public static void registerCoreLanguage(File file, Locale locale) {
        MessageManager.setLocale(locale);
        ResourceBundle minigames = null;
        if (file.exists()) {
            try {
                minigames = fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            minigames = ResourceBundle.getBundle("messages", locale, new UTF8Control());
        }
        if (minigames != null) {
            registerMessageFile("minigames", minigames);
        } else {
            logger.severe("No Core Language Resource Could be loaded...messaging will be broken");
        }

    }

    private static ResourceBundle fromFile(File file) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return new PropertyResourceBundle(inputStreamReader);
        }
    }


    /**
     * Register a new Bundle
     * To load the bundle use the {@link UTF8Control instance as the resource control.
     * This loads the resource with UTF8
     *
     * @param identifier Unique identifier for your resource bundle
     * @param bundle     the ResourceBundle
     * @return true on success.
     */
    public static boolean registerMessageFile(String identifier, ResourceBundle bundle) {
        if (propertiesHashMap.containsKey(identifier)) {
            return false;
        } else {
            if (propertiesHashMap.put(identifier, bundle) == null) {
                logger.info("Loaded and registered Resource Bundle " + bundle.getBaseBundleName()
                        + " with Locale:" + bundle.getLocale().toString() + " Added " + bundle.keySet().size() + " keys");
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean deRegisterAll(String identifier) {
        return (propertiesHashMap.remove(identifier) != null);
    }

    /**
     * If the identifier is null this uses the core language file
     *
     * @param identifier Unique identifier of the bundle to search
     * @param key        key
     * @param args       Varargs to replace
     * @return Formatted String.
     */
    public static String getMessage(@Nullable String identifier, @NotNull String key, Object... args) throws MissingResourceException {
        String unformatted = getUnformattedMessage(identifier, key);
        return String.format(unformatted, args);

    }

    /**
     * @param identifier Unique identifier of the bundle to search
     * @param key        key
     * @return Unformatted String.
     * @throws MissingResourceException If bundle not found.
     */
    public static String getUnformattedMessage(@Nullable String identifier, @NotNull String key) throws MissingResourceException {
        ResourceBundle bundle;
        if (identifier == null) {
            bundle = propertiesHashMap.get("minigames");
        } else {
            bundle = propertiesHashMap.get(identifier);
        }
        if (bundle == null) {
            String err = (identifier == null) ? "NULL" : identifier;
            Collection<String> errArgs = new ArrayList<>();
            errArgs.add("Identifier was invalid: " + err);
            errArgs.add(key);
            throw new MissingResourceException(err, "MessageManager", key);
        }
        return bundle.getString(key);
    }

    public static String getMinigamesMessage(String key, Object... args) throws MissingResourceException {
        return getMessage(null, key, args);
    }

    public static void sendInfoMessage(CommandSender sender, String identifier, String key, Object... args) {
        sendMessage(sender, MinigameMessageType.INFO, identifier, key, args);
    }

    public static void sendClickedCommandMessage(CommandSender target, String command, String identifier, String key,
                                                 Object... args) {
        BaseComponent init = getMessageStart(MinigameMessageType.INFO);
        TextComponent message = new TextComponent(getMessage(identifier, key, args));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        sendMessage(target, init, message);
    }

    public static void sendMessage(MinigamePlayer target, MinigameMessageType type, String identifier, String key, String... args) {
        sendMessage(target.getPlayer(), type, identifier, key, args);
    }

    public static void sendCoreMessage(CommandSender target, MinigameMessageType type, String key, Object... args) {
        sendMessage(target, type, null, key, args);
    }

    public static void sendMessage(CommandSender target, MinigameMessageType type, String identifier, String key,
                                   Object... args) {
        BaseComponent init = getMessageStart(type);
        TextComponent message = new TextComponent(getMessage(identifier, key, args));
        sendMessage(target, init, message);
    }

    private static BaseComponent getMessageStart(MinigameMessageType type) {
        BaseComponent init = new TextComponent("[Minigames]");
        switch (type) {
            case ERROR:
                init.setColor(ChatColor.RED);
                break;
            case WIN:
                init.setColor(ChatColor.GREEN);
                break;
            case LOSS:
                init.setColor(ChatColor.DARK_RED);
                break;
            case INFO:
            default:
                init.setColor(ChatColor.AQUA);
        }
        return init;
    }

    private static void sendMessage(CommandSender target, BaseComponent... message) {
        if (PaperLib.isPaper()) {
            target.sendMessage(message);
            return;
        }
        target.spigot().sendMessage(message);

    }

    /**
     * Broadcasts a message with a defined permission.
     *
     * @param message    - The message to be broadcast (Can be manipulated with MinigamesBroadcastEvent)
     * @param minigame   - The Minigame this broadcast is related to.
     * @param permission - The permission required to see this broadcast message.
     */
    public static void broadcast(String message, Minigame minigame, String permission) {
        MinigamesBroadcastEvent ev = new MinigamesBroadcastEvent(org.bukkit.ChatColor.AQUA + "[Minigames]" + org.bukkit.ChatColor.WHITE, message, minigame);
        Bukkit.getPluginManager().callEvent(ev);

        // Only send broadcast if event was not cancelled and is not empty
        if (!ev.isCancelled() && !ev.getMessage().isEmpty())
            Bukkit.getServer().broadcast(ev.getMessageWithPrefix(), permission);
    }


    /**
     * Broadcasts a server message without a permission.
     *
     * @param message     - The message to be broadcasted (Can be manipulated with MinigamesBroadcastEvent)
     * @param minigame    - The Minigame this broadcast is related to.
     * @param prefixColor - The color to be used in the prefix.
     */
    public static void broadcast(String message, Minigame minigame, org.bukkit.ChatColor prefixColor) {
        BaseComponent init = new TextComponent("[Minigames]");
        init.setColor(prefixColor.asBungee());
        TextComponent m = new TextComponent(" "+ message);
        MinigamesBroadcastEvent ev = new MinigamesBroadcastEvent(prefixColor + "[Minigames]" + org.bukkit.ChatColor.WHITE, message, minigame);
        Bukkit.getPluginManager().callEvent(ev);

        // Only send broadcast if event was not cancelled and is not empty
        if (!ev.isCancelled() && !ev.getMessage().isEmpty()) {
            if (PaperLib.isPaper()) {
                Bukkit.getServer().broadcast(init, m);
            } else {
                Bukkit.getServer().spigot().broadcast(init, m);
            }
        }
    }


}

