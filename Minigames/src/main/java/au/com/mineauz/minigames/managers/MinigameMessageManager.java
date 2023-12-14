package au.com.mineauz.minigames.managers;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.events.MinigamesBroadcastEvent;
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
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

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
        Minigames.log().info("MessageManager set locale for language:" + locale.toLanguageTag());
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
                Minigames.log().log(Level.WARNING, "couldn't get Ressource bundle from file " + file.getName(), e);
            }
        } else {
            try {
                langBundleMinigames = ResourceBundle.getBundle("messages", locale, Minigames.getPlugin().getClass().getClassLoader(), new UTF8ResourceBundleControl());
            } catch (MissingResourceException e) {
                Minigames.log().log(Level.WARNING, "couldn't get Ressource bundle for lang " + locale.toLanguageTag(), e);
            }
        }
        if (langBundleMinigames != null) {
            registerMessageFile("minigames", langBundleMinigames);
        } else {
            Minigames.log().severe("No Core Language Resource Could be loaded...messaging will be broken");
        }
    }

    /**
     * Register a new Bundle
     * To load the bundle use the {@link UTF8ResourceBundleControl instance as the resource control.
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
                Minigames.log().info("Loaded and registered Resource Bundle " + bundle.getBaseBundleName()
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


    public static Component getMessage(@NotNull LangKey key, TagResolver... resolvers) {
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

    public static String getStrippedMessage(@NotNull LangKey key, TagResolver... resolvers) {
        return getStrippedMessage(null, key, resolvers);
    }

    public static String getStrippedMessage(@Nullable String identifier, @NotNull LangKey key, TagResolver... resolvers) {
        return PlainTextComponentSerializer.plainText().serialize(MiniMessage.miniMessage().deserialize(getUnformattedMessage(identifier, key), resolvers));
    }

    public static String getUnformattedMessage(@NotNull LangKey key) throws MissingResourceException {
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

    public static Component getMinigamesMessage(@NotNull MinigameLangKey key, @NotNull TagResolver... resolvers) {
        return getMessage(null, key, resolvers);
    }

    public static void sendClickedCommandMessage(@NotNull Audience target, @NotNull String command, @Nullable String identifier, @NotNull LangKey key,
                                                 @NotNull TagResolver... resolvers) {
        Component init = getPluginPrefix(MinigameMessageType.INFO);
        Component message = getMessage(identifier, key, resolvers).
                clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command));
        target.sendMessage(init.append(message));
    }

    public static void sendMessage(MinigamePlayer mgPlayer, MinigameMessageType type, @NotNull MinigameLangKey key, @NotNull TagResolver... resolvers) {
        sendMessage(mgPlayer.getPlayer(), type, null, key, resolvers);
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

    public static void sendMessage(@NotNull Audience audience, @NotNull MinigameMessageType messageType, @Nullable String identifier, @NotNull MinigameLangKey key) {
        audience.sendMessage(getPluginPrefix(messageType).append(getMessage(identifier, key)));
    }

    public static void sendMessage(@NotNull Audience audience, @NotNull MinigameMessageType messageType, @NotNull Component message) {
        audience.sendMessage(getPluginPrefix(messageType).append(message));
    }

    public static void sendMessage(@NotNull Audience audience, @NotNull MinigameMessageType messageType, @NotNull MinigameLangKey key) {
        sendMessage(audience, messageType, null, key);
    }

    public static void sendMessage(MinigamePlayer player, MinigameMessageType type, Component message) {
        sendMessage(player.getPlayer(), type, message);
    }


    public enum PlaceHolderKey {
        TEAM("team"),
        OTHER_TEAM("other_team"),
        PLAYER("player"),
        OTHER_PLAYER("other_player"),
        MINIGAME("minigame"),
        SCORE("score"),
        TIME("time"),
        DEATHS("deaths"),
        REVERTS("reverts"),
        KILLS("kills"),
        NUMBER("number"),
        MAX("max"),
        TYPE("type"),
        MECHANIC("mechanic"),
        OBJECTIVE("objective"),
        COMMAND("command"),
        MONEY("money"),
        LOADOUT("loadout");

        private final String placeHolder;

        PlaceHolderKey(String placeHolder) {
            this.placeHolder = placeHolder;
        }

        @Subst("number")
        public String getKey() {
            return placeHolder;
        }
    }

    public static void debugMessage(String message) {
        if (Minigames.getPlugin().isDebugging()) {
            Minigames.getPlugin().getLogger().info(ChatColor.RED + "[Debug] " + ChatColor.WHITE + message);
        }
    }

    public enum MinigameLangKey implements LangKey {
        COMMAND_INFO_OUTPUT_HEADER("command.info.output.header"),
        COMMAND_INFO_OUTPUT_DESCRIPTION("command.info.output.description"),
        COMMAND_INFO_OUTPUT_GAMETYPE("command.info.output.gameType"),
        COMMAND_INFO_OUTPUT_TIMER("command.info.output.timer"),
        COMMAND_INFO_OUTPUT_PLAYERHEADER("command.info.output.playerHeader"),
        COMMAND_INFO_OUTPUT_TEAMDATA("command.info.output.teamData"),
        COMMAND_INFO_OUTPUT_PLAYERDATA("command.info.output.playerData"),
        COMMAND_INFO_OUTPUT_NOPLAYER("command.info.output.noPlayer"),
        COMMAND_INFO_OUTPUT_NOMINIGAME("command.info.noMinigame"),
        COMMAND_INFO_DESCRIPTION("command.info.description"),
        MINIGAME_ERROR_NOTSTARTED("minigame.error.notStarted"),
        MINIGAME_ERROR_NOTENABLED("minigame.error.notEnabled"),
        MINIGAME_ERROR_NOMINIGAME("minigame.error.noMinigame"),
        MINIGAME_ERROR_NOLOBY("minigame.error.noLobby"),
        MINIGAME_ERROR_FULL("minigame.error.full"),
        MINIGAME_ERROR_NODEFAULTTOOL("minigame.error.noDefaultTool"),
        MINIGAME_ERROR_INVALIDMECHANIC("minigame.error.invalidMechanic"),
        MINIGAME_ERROR_MECHANICSTARTFAIL("minigame.error.mechanicStartFail"),
        MINIGAME_ERROR_INVALIDTYPE("minigame.error.invalidType"),
        MINIGAME_ERROR_REGENERATING("minigame.error.regenerating"),
        MINIGAME_ERROR_STARTED("minigame.error.started"),
        MINIGAME_ERROR_NOEND("minigame.error.noEnd"),
        MINIGAME_ERROR_NOQUIT("minigame.error.noQuit"),
        MINIGAME_ERROR_NOSTART("minigame.error.noStart"),
        MINIGAME_ERROR_NOSPECTATEPOS("minigame.error.noSpectatePos"),
        MINIGAME_ERROR_NOTELEPORT("minigame.error.noTeleport"),
        MINIGAME_ERROR_NOTEAM("minigame.error.noTeam"),
        MINIGAME_ERROR_INCORRECTSTART("minigame.error.incorrectStart"),
        MINIGAME_SKIPWAITTIME("minigame.skipWaitTime"),
        MINIGAME_LATEJOIN("minigame.lateJoin"),
        MINIGAME_LATEJOINWAIT("minigame.lateJoinWait"),
        MINIGAME_WAITINGFORPLAYERS("minigame.waitingForPlayers"),
        MINIGAME_WARNING_TELEPORT_ACROSS_WORLDS("minigame.warning.TeleportAcrossWorlds"),
        MINIGAME_SCORETOWIN("minigame.scoreToWin"),
        MINIGAME_LIVESLEFT("minigame.livesLeft"),
        MINIGAME_INFO_SCORE("minigame.info.score"),
        MINIGAME_STARTRANDOMIZED("minigame.startRandomized"),
        MINIGAME_RESSOURCEPACK_APPLY("minigame.resourcepack.apply"),
        MINIGAME_RESSOURCEPACK_REMOVE("minigames.resourcepack.remove"),
        PLAYER_TEAM_ASSIGN_JOINTEAM("player.team.assign.joinTeam"),
        PLAYER_END_TEAM_WIN("player.end.team.win"),
        PLAYER_END_BROADCAST_NOBODY("player.end.broadcast.nobodyWon"),
        PLAYER_END_BROADCAST_WIN("player.end.broadcast.win"),
        PLAYER_END_TEAM_TIE("player.end.team.tie"),
        PLAYER_END_TEAM_TIECOUNT("player.end.team.tieCount"),
        PLAYER_END_TEAM_SCORE("player.end.team.score"),
        PLAYER_JOIN_PLAYERINFO("player.join.plyInfo"),
        PLAYER_JOIN_PLAYERMSG("player.join.plyMsg"),
        PLAYER_JOIN_OBJECTIVE("player.join.objective"),
        PLAYER_SPECTATE_JOIN_PLAYERMSG("player.spectate.join.plyMsg"),
        PLAYER_SPECTATE_JOIN_PLAYERHELP("player.spectate.join.plyHelp"),
        PLAYER_SPECTATE_JOIN_MINIGAMEMSG("player.spectate.join.minigameMsg"),
        PLAYER_SPECTATE_QUIT_PLAYERMSG("player.spectate.quit.plyMsg"),
        PLAYER_SPECTATE_QUIT_MINIGAMEMSG("player.spectate.quit.minigameMsg"),
        PLAYER_QUIT_PLAYERMSG("player.quit.plyMsg"),
        PLAYER_BET_PLAYERMSG("player.bet.plyMsg"),
        PLAYER_BET_NOTENOUGHMONEY("player.bet.notEnoughMoney"),
        PLAYER_BET_NOTENOUGHMONEYINFO("player.bet.notEnoughMoneyInfo"),
        PLAYER_BET_WINMONEY("player.bet.winMoney"),
        PLAYER_CHECKPOINT_DEATHREVERT("player.checkpoint.deathRevert"),
        PLAYER_CHECKPOINT_REVERT("player.checkpoint.revert"),
        PLAYER_BET_PLAYERNOBET("player.bet.plyNoBet"),
        PLAYER_BET_INCORRECTMONEYAMOUNTINFO("player.bet.incorrectMoneyAmountInfo"),
        PLAYER_BET_INCORRECTITEMAMOUNTINFO("player.bet.incorrectItemAmountInfo"),
        PLAYER_COMPLETIONTIME("player.completionTime"),
        COMMAND_DIVIDER_LARGE("command.divider.large"),
        COMMAND_DIVIDER_SMALL("command.divider.small");

        private final @NotNull String path;

        MinigameLangKey(@NotNull String path) {
            this.path = path;
        }

        public @NotNull String getPath() {
            return path;
        }
    }
}