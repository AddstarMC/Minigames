package au.com.mineauz.minigames.managers;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.events.MinigamesBroadcastEvent;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
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
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Class will hold and store all messages that are required for minigames
 */
public class MinigameMessageManager { // todo cache unformatted // todo clean all the different sendMessages - there are to many similar
    private final static String BUNDLE_KEY = "minigames";
    private final static String BUNDLE_NAME = "messages";

    /**
     * Stores each prop file with an identifier
     */
    private final static @NotNull Hashtable<String, ResourceBundle> propertiesHashMap = new Hashtable<>();

    public static void registerCoreLanguage() {
        CodeSource src = Minigames.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            initLangFiles(src, BUNDLE_NAME);
        } else {
            Minigames.getCmpnntLogger().warn("Couldn't save lang files: no CodeSource!");
        }

        String tag = Minigames.getPlugin().getConfig().getString("lang", Locale.getDefault().toLanguageTag());
        Locale locale = Locale.forLanguageTag(tag.replace("_", "-"));

        // fall back if locale is undefined
        if (locale.getLanguage().isEmpty()) {
            locale = Locale.getDefault();
        }

        Minigames.getCmpnntLogger().info("MessageManager set locale for language:" + locale.toLanguageTag());
        File file = new File(new File(Minigames.getPlugin().getDataFolder(), "lang"), "minigames.properties");
        registerCoreLanguage(file, Locale.getDefault());
    }

    private static String saveConvert(String theString, boolean escapeSpace) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuilder convertedStrBuilder = new StringBuilder(bufLen);

        for (int i = 0; i < theString.length(); i++) {
            char aChar = theString.charAt(i);
            // Handle common case first
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    if (i + 1 < theString.length()) {
                        final char bChar = theString.charAt(i + 1);
                        if (bChar == ' ' || bChar == 't' || bChar == 'n' || bChar == 'r' ||
                                bChar == 'f' || bChar == '\\' || bChar == 'u' || bChar == '=' ||
                                bChar == ':' || bChar == '#' || bChar == '!') {
                            // don't double escape already escaped chars
                            convertedStrBuilder.append(aChar);
                            convertedStrBuilder.append(bChar);
                            i++;
                            continue;
                        } else {
                            // any other char following
                            convertedStrBuilder.append('\\');
                        }
                    } else {
                        // last char was a backslash. escape!
                        convertedStrBuilder.append('\\');
                    }
                }
                convertedStrBuilder.append(aChar);
                continue;
            }

            // escape non escaped chars that have to get escaped
            switch (aChar) {
                case ' ' -> {
                    if (escapeSpace) {
                        convertedStrBuilder.append('\\');
                    }
                    convertedStrBuilder.append(' ');
                }
                case '\t' -> convertedStrBuilder.append("\\t");
                case '\n' -> convertedStrBuilder.append("\\n");
                case '\r' -> convertedStrBuilder.append("\\r");
                case '\f' -> convertedStrBuilder.append("\\f");
                case '=', ':', '#', '!' -> {
                    convertedStrBuilder.append('\\');
                    convertedStrBuilder.append(aChar);
                }
                default -> convertedStrBuilder.append(aChar);
            }
        }

        return convertedStrBuilder.toString();
    }

    // Thanks, @Feuerreiter, for code from Padlock. Nice Plugin, check it out!
    // #self-marketing
    public static void initLangFiles(@NotNull CodeSource src, @NotNull String bundleName) {
        final Pattern bundleFileNamePattern = Pattern.compile(bundleName + "(?:_.*)?.properties");

        URL jarUrl = src.getLocation();
        try (ZipInputStream zipStream = new ZipInputStream(jarUrl.openStream())) {
            ZipEntry zipEntry;
            while ((zipEntry = zipStream.getNextEntry()) != null) {
                String entryName = zipEntry.getName();

                if (bundleFileNamePattern.matcher(entryName).matches()) {
                    File langFile = new File(new File(Minigames.getPlugin().getDataFolder(), bundleName), entryName);
                    if (!langFile.exists()) { // don't overwrite existing files
                        FileUtils.copyToFile(zipStream, langFile);
                    } else { // add defaults to file to expand in case there are key-value pairs missing
                        Properties defaults = new Properties();
                        try (InputStreamReader reader = new InputStreamReader(zipStream, StandardCharsets.UTF_8)) {
                            defaults.load(reader);
                        } catch (Exception e) {
                            Minigames.getCmpnntLogger().warn("couldn't get default properties file for " + entryName + "!", e);
                            continue;
                        }

                        Properties current = new Properties();
                        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(langFile), StandardCharsets.UTF_8)) {
                            current.load(reader);
                        } catch (Exception e) {
                            Minigames.getCmpnntLogger().warn("couldn't get default properties file for " + entryName + "!", e);
                            continue;
                        }

                        try (FileWriter fw = new FileWriter(langFile, StandardCharsets.UTF_8, true);
                             // we are NOT using Properties#store since it gets rid of comments and doesn't guarantee ordering
                             BufferedWriter bw = new BufferedWriter(fw)) {
                            boolean updated = false; // only write comment once
                            for (Map.Entry<Object, Object> translationPair : defaults.entrySet()) {
                                if (current.get(translationPair.getKey()) == null) {
                                    if (!updated) {
                                        bw.write("# New Values where added. Is everything else up to date? Time of update: " + new Date());
                                        bw.newLine();

                                        Minigames.getCmpnntLogger().trace("Updated langfile \"" + entryName + "\". Might want to check the new translation strings out!");

                                        updated = true;
                                    }

                                    String key = saveConvert((String) translationPair.getKey(), true);
                                    /* No need to escape embedded and trailing spaces for value, hence
                                     * pass false to flag.
                                     */
                                    String val = saveConvert((String) translationPair.getValue(), false);
                                    bw.write((key + "=" + val));
                                    bw.newLine();
                                } // current already knows the key
                            } // end of for
                        } // end of try
                    } // end of else (file exists)
                } // doesn't match
            } // end of elements
        } catch (IOException e) {
            Minigames.getCmpnntLogger().warn("Couldn't save lang files", e);
        }
    }

    public static void registerCoreLanguage(@NotNull File file, @NotNull Locale locale) {
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
                        + " with Locale:" + bundle.getLocale().toLanguageTag() + " Added " + bundle.keySet().size() + " keys");
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean deRegisterMessageFile(String identifier) {
        return (propertiesHashMap.remove(identifier) != null);
    }

    public static Component formatBlockLocation(@NotNull Location location) {
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
    public static String getUnformattedMessage(@Nullable String identifier, @NotNull LangKey key) throws MissingResourceException { //todo don't crash if bundle is missing or can't get string. simply return key
        ResourceBundle bundle = propertiesHashMap.get(Objects.requireNonNullElse(identifier, BUNDLE_KEY));
        if (bundle == null) {
            String err = (identifier == null) ? "NULL" : identifier;
            throw new MissingResourceException(err, "MessageManager", key.getPath());
        }
        return bundle.getString(key.getPath());
    }

    public static void sendClickedCommandMessage(@NotNull Audience target, @NotNull String command,
                                                 @Nullable String identifier, @NotNull LangKey key,
                                                 @NotNull TagResolver... resolvers) {
        Component init = getPluginPrefix(MinigameMessageType.INFO);
        Component message = getMessage(identifier, key, resolvers).
                clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command));
        target.sendMessage(init.append(message));
    }

    public static void sendMessage(MinigamePlayer mgPlayer, MinigameMessageType type, @Nullable String identifier,
                                   @NotNull LangKey key, TagResolver... resolvers) {
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
            case ERROR, TIE -> init.color(NamedTextColor.RED);
            case WARNING -> init.color(NamedTextColor.GOLD);
            case SUCCESS, WIN -> init.color(NamedTextColor.GREEN);
            case LOSS -> init.color(NamedTextColor.DARK_RED);
            case NONE -> Component.empty();
            default -> init.color(NamedTextColor.AQUA);
        };
    }

    /**
     * Broadcasts a message with a defined permission for everyone on a server.
     *
     * @param message    - The message to be broadcastServer (Can be manipulated with MinigamesBroadcastEvent)
     * @param minigame   - The Minigame this broadcast is related to.
     * @param permission - The permission required to see this broadcastServer message.
     */
    public static void broadcastServer(@NotNull Component message, @NotNull Minigame minigame, @NotNull String permission) {
        MinigamesBroadcastEvent ev = new MinigamesBroadcastEvent(getPluginPrefix(MinigameMessageType.DEFAULT), message, minigame);
        Bukkit.getPluginManager().callEvent(ev);

        // Only send broadcastServer if event was not cancelled and is not empty
        if (!ev.isCancelled()) {
            Bukkit.getServer().broadcast(ev.getMessageWithPrefix(), permission);
        }
    }


    /**
     * Broadcasts a server message without a permission for everyone on a server.
     *
     * @param message  - The message to be broadcasted (Can be manipulated with MinigamesBroadcastEvent)
     * @param minigame - The Minigame this broadcast is related to.
     * @param type     - The color to be used in the prefix.
     */
    public static void broadcastServer(@NotNull Component message, @NotNull Minigame minigame, @NotNull MinigameMessageType type) {
        Component init = getPluginPrefix(type);
        MinigamesBroadcastEvent ev = new MinigamesBroadcastEvent(init, message, minigame);
        Bukkit.getPluginManager().callEvent(ev);

        // Only send broadcastServer if event was not cancelled and is not empty
        if (!ev.isCancelled()) {
            Bukkit.getServer().broadcast(ev.getMessageWithPrefix());
        }
    }


    /**
     * Sending a general info Broadcast to all players in the minigame.
     *
     * @param minigame The minigame in which this message shall be sent
     * @param message  The message
     */
    public static void sendMinigameMessage(final @NotNull Minigame minigame, final @NotNull Component message) {
        sendMinigameMessage(minigame, message, MinigameMessageType.INFO);
    }

    /**
     * Sending a general Broadcast to all players in the minigame.
     *
     * @param minigame The minigame in which this message shall be sent
     * @param message  The message
     * @param type     Message Type
     */
    public static void sendMinigameMessage(final @NotNull Minigame minigame, final @NotNull Component message, final @Nullable MinigameMessageType type) {
        sendMinigameMessage(minigame, message, type, (List<MinigamePlayer>) null);
    }

    /**
     * Sending a general Broadcast to all players in the minigame.
     *
     * @param minigame The minigame in which this message shall be sent
     * @param message  The message
     * @param type     Message Type
     * @param exclude  Player, who shall not get this message
     */
    public static void sendMinigameMessage(final @NotNull Minigame minigame, final @NotNull Component message, final @Nullable MinigameMessageType type,
                                           final @NotNull MinigamePlayer exclude) {
        sendMinigameMessage(minigame, message, type, Collections.singletonList(exclude));
    }

    /**
     * Sending a general Broadcast to all players in the minigame.
     *
     * @param minigame The minigame in which this message shall be sent
     * @param message  The message
     * @param type     Message Type
     * @param exclude  Players, which shall not get this message
     */
    public static void sendMinigameMessage(final @NotNull Minigame minigame, final @NotNull Component message, @Nullable MinigameMessageType type,
                                           final @Nullable List<@NotNull MinigamePlayer> exclude) {
        if (!minigame.getShowPlayerBroadcasts()) {
            return;
        }
        sendBroadcastMessageUnchecked(minigame, message, type, exclude);
    }

    // This sends a message to every player which is not excluded from the exclude list
    public static void sendBroadcastMessageUnchecked(@NotNull Minigame minigame, final @NotNull Component message, @Nullable MinigameMessageType type, @Nullable List<@NotNull MinigamePlayer> exclude) {
        if (type == null) {
            type = MinigameMessageType.INFO;
        }

        final List<MinigamePlayer> playersSendTo = new ArrayList<>();
        playersSendTo.addAll(minigame.getPlayers());
        playersSendTo.addAll(minigame.getSpectators());
        if (exclude != null) {
            playersSendTo.removeAll(exclude);
        }

        for (final MinigamePlayer player : playersSendTo) {
            MinigameMessageManager.sendMessage(player, type, message);
        }
    }

    public static void sendMessage(@NotNull Audience audience, @NotNull MinigameMessageType messageType,
                                   @Nullable String identifier, @NotNull LangKey key) {
        audience.sendMessage(getPluginPrefix(messageType).append(getMessage(identifier, key)));
    }

    public static void sendMgMessage(@NotNull Audience audience, @NotNull MinigameMessageType messageType, @NotNull LangKey key) {
        sendMessage(audience, messageType, null, key);
    }

    public static void sendMgMessage(@NotNull Audience audience, @NotNull MinigameMessageType messageType,
                                     @NotNull LangKey key, @NotNull TagResolver... resolvers) {
        sendMessage(audience, messageType, null, key, resolvers);
    }

    public static void sendMgMessage(@NotNull MinigamePlayer mgPlayer, @NotNull MinigameMessageType messageType,
                                     @NotNull LangKey key, @NotNull TagResolver... resolvers) {
        sendMessage(mgPlayer.getPlayer(), messageType, null, key, resolvers);
    }

    public static void sendMgMessage(@NotNull MinigamePlayer mgPlayer, @NotNull MinigameMessageType type,
                                     @NotNull MinigameLangKey key) {
        sendMessage(mgPlayer.getPlayer(), type, null, key);
    }

    public static void sendMessage(@NotNull Audience audience, @NotNull MinigameMessageType messageType,
                                   @NotNull Component message) {
        audience.sendMessage(getPluginPrefix(messageType).append(message));
    }

    public static void sendMessage(@NotNull MinigamePlayer mgPlayer, @NotNull MinigameMessageType type,
                                   @NotNull Component message) {
        sendMessage(mgPlayer.getPlayer(), type, message);
    }

    public static void debugMessage(@NotNull String message) { //todo
        if (Minigames.getPlugin().isDebugging()) {
            Minigames.getCmpnntLogger().info(ChatColor.RED + "[Debug] " + ChatColor.WHITE + message);
        }
    }
}