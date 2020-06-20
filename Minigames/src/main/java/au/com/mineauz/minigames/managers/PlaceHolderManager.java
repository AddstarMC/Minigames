package au.com.mineauz.minigames.managers;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.ModulePlaceHolderProvider;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 3/06/2020.
 */
public class PlaceHolderManager extends PlaceholderExpansion {

    private final Minigames plugin;
    private final List<ModulePlaceHolderProvider> providers;
    private final Map<String, String> identifiers;

    public PlaceHolderManager(Minigames plugin) {
        this.plugin = plugin;
        providers = new ArrayList<>();
        identifiers = new HashMap<>();
        identifiers.put("gameCount", "CORE");
        identifiers.put("enabledGameCount", "CORE");
        identifiers.put("totalPlaying", "CORE");
    }

    public Set<String> getRegisteredPlaceHolders() {
        return identifiers.keySet();
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        return super.onRequest(p, params);
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return Minigames.getPlugin().getName();
    }

    @Override
    public String getAuthor() {
        return Minigames.getPlugin().getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return Minigames.getVERSION().toString();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {

        if (player == null) {
            return "";
        }
        if (!identifiers.containsKey(identifier)) {
            return null;
        }
        Set<String> games = plugin.getMinigameManager().getAllMinigames().keySet();
        if (identifier.contains("_")) {
            String[] parts = identifier.split("_");
            String gameName = parts[0];
            if (games.contains(gameName)) {
                Minigame minigame = plugin.getMinigameManager().getMinigame(gameName);
                try {
                    switch (parts[1]) {
                        case "enabled":
                            return Boolean.toString(minigame.isEnabled());
                        case "maxPlayers":
                            return Integer.toString(minigame.getMaxPlayers());
                        case "currentPlayers":
                            return Integer.toString(minigame.getPlayers().size());
                        case "type":
                            return minigame.getType().getName();
                        case "mechanic":
                            return minigame.getMechanicName();
                        case "state":
                            return minigame.getState().name();
                        case "objective":
                            return minigame.getObjective();
                        case "gameType":
                            return minigame.getGametypeName();
                        case "timeLeft":
                            return Integer.toString(minigame.getMinigameTimer().getTimeLeft());
                        case "name":
                            return minigame.getName(true);
                        default:
                            for (ModulePlaceHolderProvider provider : providers) {
                                if (provider.hasPlaceHolder(parts[1])) {
                                    return provider.onPlaceHolderRequest(player, gameName, parts[1]);
                                }
                            }
                            return null;
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Error processing PAPI:" + identifier);
                    plugin.getLogger().warning(e.getMessage());
                    if (plugin.isDebugging()) {
                        e.printStackTrace();
                    }
                    return null;
                }
            } else {
                //this means the first part is not a gameName ?? what else could it be
                return null;
            }
        } else {
            switch (identifier) {
                case "gameCount":
                    return Integer.toString(plugin.getMinigameManager().getAllMinigames().size());
                case "enabledGameCount":
                    return Long.toString(plugin.getMinigameManager().getAllMinigames().values()
                          .stream().filter(Minigame::isEnabled).count());
                case "totalPlaying":
                    return Long.toString(plugin.getPlayerManager().getAllMinigamePlayers().stream()
                          .filter(MinigamePlayer::isInMinigame).count());
                default:

                    return null;
            }
        }
    }

    public void addGameIdentifiers(Minigame game) {
        String name = game.getName(false);
        for (GameOptions o : GameOptions.values()) {
            identifiers.put(name + "_" + o.name, "GAME_" + name);
        }
        for (MinigameModule module : game.getModules()) {
            ModulePlaceHolderProvider provider = module.getModulePlaceHolders();
            if (provider != null) {
                registerModulePlaceholders(game.getName(false), provider);
            }
        }
        plugin.getLogger().info("PAPI hooked for " + game.getName(true));
    }

    public void registerModulePlaceholders(String gameName, ModulePlaceHolderProvider provider) {
        providers.add(provider);
        for (String id : provider.getIdentifiers()) {
            if (identifiers.containsKey(gameName + "_" + id)) {
                plugin.getLogger().info(provider.getClass().getSimpleName() + " tried to add a placeholder " + id + " it conflicts and has been rejected");
                plugin.getLogger().info("Conflicting Module or Game: " + identifiers.get(id));
                continue;
            }
            identifiers.put(gameName + "_" + id, provider.getClass().getName());
        }
    }

    private enum GameOptions {
        ENABLED("enabled"),
        MAX_PLAYERS("maxPlayers"),
        CURRENT_PLAYERS("currentPlayers"),
        TYPE("type"),
        MECHANIC("mechanic"),
        OBJECTIVE("objective"),
        GAME_TYPE("gameType"),
        TIME_LEFT("timeLeft"),
        NAME("name");

        private final String name;

        GameOptions(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
