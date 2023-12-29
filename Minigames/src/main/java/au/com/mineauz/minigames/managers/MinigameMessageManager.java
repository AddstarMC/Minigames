package au.com.mineauz.minigames.managers;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.events.MinigamesBroadcastEvent;
import au.com.mineauz.minigames.managers.language.LangKey;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Class will hold and store all messages that are required for minigames
 */
public class MinigameMessageManager { // todo cache unformatted // todo clean all the different sendMessages - there are to many similar
    private final static String BUNDLE_KEY = "minigames";
    /**
     * Stores each prop file with an identifier
     */
    private final static @NotNull Hashtable<String, ResourceBundle> propertiesHashMap = new Hashtable<>();
    private static @NotNull Locale locale = Locale.getDefault();

    public static void registerCoreLanguage() {
        String tag = Minigames.getPlugin().getConfig().getString("lang", Locale.getDefault().toLanguageTag());
        locale = Locale.forLanguageTag(tag);
        Minigames.getCmpnntLogger().info("MessageManager set locale for language:" + locale.toLanguageTag());
        File file = new File(new File(Minigames.getPlugin().getDataFolder(), "lang"), "minigames.properties");
        registerCoreLanguage(file, Locale.getDefault());
    }

    public static void registerCoreLanguage(@NotNull File file, @NotNull Locale locale) {
        MinigameMessageManager.locale = locale;
        ResourceBundle langBundleMinigames = null;
        if (file.exists()) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                langBundleMinigames = new PropertyResourceBundle(inputStreamReader);
            } catch (IOException e) {
                Minigames.getCmpnntLogger().warn("couldn't get Ressource bundle from file " + file.getName(), e);
            }
        } else {
            try {
                langBundleMinigames = ResourceBundle.getBundle("messages", locale, Minigames.getPlugin().getClass().getClassLoader(), new UTF8ResourceBundleControl());
            } catch (MissingResourceException e) {
                Minigames.getCmpnntLogger().warn("couldn't get Ressource bundle for lang " + locale.toLanguageTag(), e);
            }
        }
        if (langBundleMinigames != null) {
            registerMessageFile("minigames", langBundleMinigames);
        } else {
            Minigames.getCmpnntLogger().error("No Core Language Resource Could be loaded...messaging will be broken");
        }
    }

    /**
     * Register a new Bundle
     * To load the bundle use the {@link UTF8ResourceBundleControl instance as the resource control.
     * This loads the resource with UTF8
     * @param identifier Unique identifier for your resource bundle
     * @param bundle     the ResourceBundle
     * @return true on success.
     */
    public static boolean registerMessageFile(String identifier, ResourceBundle bundle) {
        if (propertiesHashMap.containsKey(identifier)) {
            return false;
        } else {
            if (propertiesHashMap.put(identifier, bundle) == null) {
                Minigames.getCmpnntLogger().info("Loaded and registered Resource Bundle " + bundle.getBaseBundleName()
                        + " with Locale:" + bundle.getLocale().toString() + " Added " + bundle.keySet().size() + " keys");
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean deRegisterMessageFile(String identifier) {
        return (propertiesHashMap.remove(identifier) != null);
    }

    public static Component formatBlockPostion(@NotNull Location location) {
        return Component.text(location.blockX() + ", " + location.blockY() + " ," + location.blockZ());
    }

    public static Component getMgMessage(@NotNull LangKey key, TagResolver... resolvers) {
        return getMessage(null, key, resolvers);
    }

    /**
     * If the identifier is null this uses the core language file
     *
     * @param identifier Unique identifier of the bundle to search
     * @param key        key
     * @param resolvers  resolver of placeholders
     * @return Formatted String.
     */
    public static Component getMessage(@Nullable String identifier, @NotNull LangKey key, TagResolver... resolvers) {
        String unformatted = getUnformattedMessage(identifier, key);

        return MiniMessage.miniMessage().deserialize(unformatted, resolvers);
    }

    public static String getStrippedMgMessage(@NotNull LangKey key, TagResolver... resolvers) {
        return getStrippedMessage(null, key, resolvers);
    }

    public static String getStrippedMessage(@Nullable String identifier, @NotNull LangKey key, TagResolver... resolvers) {
        return PlainTextComponentSerializer.plainText().serialize(MiniMessage.miniMessage().deserialize(getUnformattedMessage(identifier, key), resolvers));
    }

    public static String getUnformattedMgMessage(@NotNull LangKey key) throws MissingResourceException {
        return getUnformattedMessage(null, key);
    }

    /**
     * @param identifier Unique identifier of the bundle to search
     * @param key        key
     * @return Unformatted String.
     * @throws MissingResourceException If bundle not found.
     */
    public static String getUnformattedMessage(@Nullable String identifier, @NotNull LangKey key) throws MissingResourceException {
        ResourceBundle bundle = propertiesHashMap.get(Objects.requireNonNullElse(identifier, BUNDLE_KEY));
        if (bundle == null) {
            String err = (identifier == null) ? "NULL" : identifier;
            throw new MissingResourceException(err, "MessageManager", key.getPath());
        }
        return bundle.getString(key.getPath());
    }

    public static void sendClickedCommandMessage(@NotNull Audience target, @NotNull String command, @Nullable String identifier, @NotNull LangKey key,
                                                 @NotNull TagResolver... resolvers) {
        Component init = getPluginPrefix(MinigameMessageType.INFO);
        Component message = getMessage(identifier, key, resolvers).
                clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command));
        target.sendMessage(init.append(message));
    }

    public static void sendMessage(MinigamePlayer mgPlayer, MinigameMessageType type, @Nullable String identifier, @NotNull LangKey key, TagResolver... resolvers) {
        sendMessage(mgPlayer.getPlayer(), type, identifier, key, resolvers);
    }

    public static void sendMessage(@NotNull Audience target, MinigameMessageType type, @Nullable String identifier, LangKey key,
                                   TagResolver... resolvers) {
        Component init = getPluginPrefix(type);
        Component message = getMessage(identifier, key, resolvers);
        target.sendMessage(init.append(message));
    }

    private static Component getPluginPrefix(MinigameMessageType type) { //todo get from langfile
        Component init = Component.text("[Minigames] ");
        return switch (type) {
            case ERROR, WARNING, TIE -> init.color(NamedTextColor.RED);
            case WIN -> init.color(NamedTextColor.GREEN);
            case LOSS -> init.color(NamedTextColor.DARK_RED);
            case NONE -> Component.empty();
            default -> init.color(NamedTextColor.AQUA);
        };
    }

    /**
     * Broadcasts a message with a defined permission.
     *
     * @param message    - The message to be broadcast (Can be manipulated with MinigamesBroadcastEvent)
     * @param minigame   - The Minigame this broadcast is related to.
     * @param permission - The permission required to see this broadcast message.
     */
    public static void broadcast(@NotNull Component message, @NotNull Minigame minigame, @NotNull String permission) {
        MinigamesBroadcastEvent ev = new MinigamesBroadcastEvent(getPluginPrefix(MinigameMessageType.DEFAULT), message, minigame);
        Bukkit.getPluginManager().callEvent(ev);

        // Only send broadcast if event was not cancelled and is not empty
        if (!ev.isCancelled()) {
            Bukkit.getServer().broadcast(ev.getMessageWithPrefix(), permission);
        }
    }


    /**
     * Broadcasts a server message without a permission.
     *
     * @param message  - The message to be broadcasted (Can be manipulated with MinigamesBroadcastEvent)
     * @param minigame - The Minigame this broadcast is related to.
     * @param type     - The color to be used in the prefix.
     */
    public static void broadcast(@NotNull Component message, @NotNull Minigame minigame, @NotNull MinigameMessageType type) {
        Component init = getPluginPrefix(type);
        MinigamesBroadcastEvent ev = new MinigamesBroadcastEvent(init, message, minigame);
        Bukkit.getPluginManager().callEvent(ev);

        // Only send broadcast if event was not cancelled and is not empty
        if (!ev.isCancelled()) {
            Bukkit.getServer().broadcast(ev.getMessageWithPrefix());
        }
    }

    public static void sendMessage(@NotNull Audience audience, @NotNull MinigameMessageType messageType, @Nullable String identifier, @NotNull LangKey key) {
        audience.sendMessage(getPluginPrefix(messageType).append(getMessage(identifier, key)));
    }

    public static void sendMgMessage(@NotNull Audience audience, @NotNull MinigameMessageType messageType, @NotNull MinigameLangKey key) {
        sendMessage(audience, messageType, null, key);
    }

    public static void sendMgMessage(@NotNull Audience audience, @NotNull MinigameMessageType messageType, @NotNull MinigameLangKey key, @NotNull TagResolver... resolvers) {
        sendMessage(audience, messageType, null, key, resolvers);
    }

    public static void sendMgMessage(@NotNull MinigamePlayer mgPlayer, @NotNull MinigameMessageType messageType, @NotNull MinigameLangKey key, @NotNull TagResolver... resolvers) {
        sendMessage(mgPlayer.getPlayer(), messageType, null, key, resolvers);
    }

    public static void sendMgMessage(@NotNull MinigamePlayer mgPlayer, @NotNull MinigameMessageType type, @NotNull MinigameLangKey key) {
        sendMessage(mgPlayer.getPlayer(), type, null, key);
    }

    public static void sendMessage(@NotNull Audience audience, @NotNull MinigameMessageType messageType, @NotNull Component message) {
        audience.sendMessage(getPluginPrefix(messageType).append(message));
    }

    public static void sendMessage(@NotNull MinigamePlayer mgPlayer, @NotNull MinigameMessageType type, @NotNull Component message) {
        sendMessage(mgPlayer.getPlayer(), type, message);
    }

    public static void debugMessage(@NotNull String message) { //todo
        if (Minigames.getPlugin().isDebugging()) {
            Minigames.getPlugin().getLogger().info(ChatColor.RED + "[Debug] " + ChatColor.WHITE + message);
        }
    }
}