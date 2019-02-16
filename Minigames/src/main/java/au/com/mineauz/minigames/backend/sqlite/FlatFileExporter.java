package au.com.mineauz.minigames.backend.sqlite;

import au.com.mineauz.minigames.backend.BackendImportCallback;
import au.com.mineauz.minigames.backend.ExportNotifier;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FlatFileExporter {
    private final File file;
    private final FileConfiguration config;
    private final ExportNotifier notifier;
    private final BackendImportCallback callback;

    private SetMultimap<String, UUID> completions;

    private Map<String, Integer> minigameIds;
    private int nextMinigameId;

    private String notifyState;
    private int notifyCount;
    private long notifyTime;

    public FlatFileExporter(File file, BackendImportCallback callback, ExportNotifier notifier) {
        this.file = file;
        this.callback = callback;
        this.notifier = notifier;

        config = new YamlConfiguration();
        minigameIds = Maps.newHashMap();
    }

    public boolean doExport() {
        try {
            callback.begin();

            config.load(file);

            loadCompletions();

            exportPlayers();
            exportMinigames();
            exportStats();

            notifyNext("Done");

            callback.end();
            notifier.onComplete();

            return true;
        } catch (InvalidConfigurationException | IOException e) {
            notifier.onError(e, notifyState, notifyCount);
            return false;
        }
    }

    private void loadCompletions() {
        completions = HashMultimap.create();

        for (String minigame : config.getKeys(false)) {
            List<String> rawIds = config.getStringList(minigame);

            for (String rawPlayerId : rawIds) {
                UUID playerId = UUID.fromString(rawPlayerId.replace('_', '-'));
                completions.put(minigame, playerId);
            }
        }
    }

    private void exportPlayers() {
        notifyNext("Exporting players...");
        Set<UUID> uniquePlayers = Sets.newHashSet(completions.values());

        for (UUID playerId : uniquePlayers) {
            // Attempt to get information about this player
            OfflinePlayer player = Bukkit.getPlayer(playerId);
            if (player != null && player.getName() != null) {
                callback.acceptPlayer(playerId, player.getName(), player.getName());
            } else {
                callback.acceptPlayer(playerId, "Unknown", "Unknown");
            }

            ++notifyCount;
            notifyProgress();
        }
    }

    private void exportMinigames() {
        notifyNext("Exporting minigames...");

        for (String minigame : completions.keySet()) {
            int id = nextMinigameId++;
            minigameIds.put(minigame, id);

            callback.acceptMinigame(id, minigame);

            ++notifyCount;
            notifyProgress();
        }
    }

    private void exportStats() {
        notifyNext("Exporting stats...");

        for (String minigame : completions.keySet()) {
            int id = minigameIds.get(minigame);

            for (UUID playerId : completions.get(minigame)) {
                callback.acceptStat(playerId, id, "wins", 1);
                callback.acceptStat(playerId, id, "attempts", 1);

                ++notifyCount;
                notifyProgress();
            }
        }
    }

    private void notifyProgress() {
        if (System.currentTimeMillis() - notifyTime >= 2000) {
            notifier.onProgress(notifyState, notifyCount);
            notifyTime = System.currentTimeMillis();
        }
    }

    private void notifyNext(String state) {
        if (notifyCount != 0) {
            notifier.onProgress(notifyState, notifyCount);
        }

        notifyTime = System.currentTimeMillis();
        notifyCount = 0;
        notifyState = state;

        notifier.onProgress(state, 0);
    }
}
