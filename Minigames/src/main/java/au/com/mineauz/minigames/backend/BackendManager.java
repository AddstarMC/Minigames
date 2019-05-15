package au.com.mineauz.minigames.backend;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.backend.mysql.MySQLBackend;
import au.com.mineauz.minigames.backend.sqlite.SQLiteBackend;
import au.com.mineauz.minigames.backend.test.TestBackEnd;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardOrder;
import au.com.mineauz.minigames.stats.*;
import com.google.common.util.concurrent.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackendManager {
    private final Logger logger;
    private boolean debug;

    private Backend backend;
    private ListeningExecutorService executorService;
    private Executor bukkitThreadExecutor;

    public BackendManager(Logger logger) {
        this.logger = logger;
        this.debug = false;

        bukkitThreadExecutor = command -> Bukkit.getScheduler().runTask(Minigames.getPlugin(), command);

        executorService = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
    }

    private Backend makeBackend(String type) {
        String serverType = Bukkit.getServer().getName();
        if (serverType.equals("ServerMock"))
            return new TestBackEnd();
        switch (type) {
            case "sqlite":
                return new SQLiteBackend(logger);
            case "mysql":
                return new MySQLBackend(logger);
            default:
                return null;
        }
    }

    /**
     * Initializes the backend.
     *
     * @param config The configuration to load settings from
     */
    public boolean initialize(ConfigurationSection config) {
        ConfigurationSection backendSection = config.getConfigurationSection("backend");
        this.debug = config.getBoolean("debug", false);
        if (config.isSet("use-sql")) {
            // Load in values from previous system
            if (config.getBoolean("use-sql")) {
                backendSection.set("type", "mysql");
            } else {
                backendSection.set("type", "sqlite");
            }

            backendSection.set("database", config.getString("sql-database"));
            backendSection.set("host", config.getString("sql-host") + ":" + config.getInt("sql-port"));
            backendSection.set("username", config.getString("sql-username"));
            backendSection.set("password", config.getString("sql-password"));
            backendSection.set("convert", true);

            // Clear the existing value
            config.set("use-sql", null);
            config.set("sql-database", null);
            config.set("sql-port", null);
            config.set("sql-host", null);
            config.set("sql-username", null);
            config.set("sql-password", null);
        }

        // Create the backend
        String type = backendSection.getString("type", "sqlite").toLowerCase();
        backend = makeBackend(type);

        if (backend == null) {
            // Default to this
            logger.warning("Invalid backend type " + type + ". Falling back to SQLite");
            backend = new SQLiteBackend(logger);
        }

        // Init
        if (!backend.initialize(backendSection, debug)) {
            return false;
        }

        // Handle conversion
        if (backendSection.getBoolean("convert", false)) {
            ExportNotifier notifier = new ExportNotifier() {
                @Override
                public void onProgress(String state, int count) {
                    logger.info("Conversion: " + state + " " + count);
                }

                @Override
                public void onError(Throwable e, String state, int count) {
                    logger.log(Level.SEVERE, "Conversion error: " + state + " " + count, e);
                }

                @Override
                public void onComplete() {
                    logger.info("Conversion complete");
                }
            };

            if (backend.doConversion(notifier)) {
                backendSection.set("convert", false);
            } else {
                return false;
            }
        }
        try {
            // Start the cleaning task to remove old connections
            Bukkit.getScheduler().runTaskTimerAsynchronously(Minigames.getPlugin(), this.backend::clean, 300, 300);
        } catch (NullPointerException e) {
            logger.warning("Bukkit could not schedule a the db pool cleaner");
            return false;
        }
        return true;
    }

    /**
     * Asks the backend to shut down
     */
    public void shutdown() {
        backend.shutdown();
    }

    /**
     * Retrieves some stats from the backend. These stats are loaded asynchronously
     *
     * @param minigame The minigame the stats are for
     * @param stat     The stat to load
     * @param field    The field of the stat to load
     * @param order    The order to retrieve in
     * @param offset   The offset to retrieve from
     * @param length   The number of stats to retrieve
     * @return A ListenableFuture that returns the list of StoredStats loaded
     */
    public ListenableFuture<List<StoredStat>> loadStats(final Minigame minigame, final MinigameStat stat, final StatValueField field, final ScoreboardOrder order, final int offset, final int length) {
        return executorService.submit(() -> backend.loadStats(minigame, stat, field, order, offset, length));
    }

    /**
     * Retrieves a single statistic value from the backend. This is loaded asynchronously
     *
     * @param minigame The minigame the stat is for
     * @param stat     The stat to load
     * @param field    The field of the stat to load
     * @param playerId The player that owns the stat
     * @return The value of the stat. If it is not set, 0 will be returned
     */
    public ListenableFuture<Long> loadSingleStat(final Minigame minigame, final MinigameStat stat, final StatValueField field, final UUID playerId) {
        return executorService.submit(() -> backend.getStat(minigame, playerId, stat, field));
    }

    /**
     * Queues a task to save the stats to the backend. The stats will be saved asynchronously
     *
     * @param stats The stats to be saved
     * @return A ListenableFuture that returns the inputed stats for chaining.
     */
    public ListenableFuture<StoredGameStats> saveStats(final StoredGameStats stats) {
        return executorService.submit(() -> {
            backend.saveGameStatus(stats);
            return stats;
        });
    }

    /**
     * Retrieves the settings for all stats in a minigame. This is loaded asynchronously
     *
     * @param minigame The minigame to load settings for
     * @return A ListenerableFuture that returns a map of minigame stats and their settings
     */
    public ListenableFuture<Map<MinigameStat, StatSettings>> loadStatSettings(final Minigame minigame) {

        return executorService.submit(() -> backend.loadStatSettings(minigame));
    }

    /**
     * Saves the stat settings for a minigame. This is performed asynchronously
     *
     * @param minigame The minigame to save settings for
     * @param settings The collection of settings to save
     * @return A ListenableFuture to get the status of the save
     */
    public ListenableFuture<Void> saveStatSettings(final Minigame minigame, final Collection<StatSettings> settings) {
        if (backend instanceof TestBackEnd) {
            backend.saveStatSettings(minigame, settings);
            return null;
        }
        return executorService.submit(() -> {
            backend.saveStatSettings(minigame, settings);
            return null;
        });
    }

    /**
     * Adds a callback to the ListenableFuture that will be executed on the Minecraft server thread
     *
     * @param future   The future to add the callback to
     * @param callback The callback to be added
     */
    public <T> void addServerThreadCallback(ListenableFuture<T> future, FutureCallback<T> callback) {
        Futures.addCallback(future, callback, bukkitThreadExecutor);
    }

    /**
     * Initializes an export to the target backend type.
     * The export can take a while and will prevent all other backend interactions while it goes on
     *
     * @param type     The type of backend to use. Same as in the config
     * @param config   The config to load settings from
     * @param notifier A notifier that will give you status updates
     * @return A future to let you know when the process is finished
     * @throws IllegalArgumentException Thrown if the backend chosen cannot be used. Reason given in message
     */
    public ListenableFuture<Void> exportTo(String type, ConfigurationSection config, final ExportNotifier notifier) throws IllegalArgumentException {
        final Backend destination = makeBackend(type);
        if (destination == null) {
            throw new IllegalArgumentException("Invalid backend type");
        }

        if (destination.getClass().equals(backend.getClass())) {
            throw new IllegalArgumentException("You cannot export to the same backend that is in use");
        }

        if (!destination.initialize(config.getConfigurationSection("backend"), debug)) {
            throw new IllegalArgumentException("Failed to initialize destination backend");
        }

        return executorService.submit(() -> backend.exportTo(destination, notifier), null);
    }

    /**
     * Allows you to change the backend dynamically.
     * This change will only occur after the last task (as of execution) has finished.
     * Note: This change will only apply for this session
     *
     * @param type   The new type of backend to use
     * @param config The config to load settings from
     * @throws IllegalArgumentException Thrown if the backend chosen cannot be used. Reason given in message
     */
    public ListenableFuture<Void> switchBackend(final String type, ConfigurationSection config) throws IllegalArgumentException {
        final Backend newBackend = makeBackend(type);
        if (newBackend == null) {
            throw new IllegalArgumentException("Invalid backend type");
        }

        if (newBackend.getClass().equals(backend.getClass())) {
            throw new IllegalArgumentException("Cannot switch to the same backend");
        }

        if (!newBackend.initialize(config.getConfigurationSection("backend"), debug)) {
            throw new IllegalArgumentException("Failed to initialize target backend");
        }

        return executorService.submit(() -> {
            backend.shutdown();
            backend = newBackend;
            logger.warning("Backend has been switched to " + type);
        }, null);
    }

    public void toggleDebug() {
        debug = !debug;
    }

    public boolean isDebugging() {
        return debug;
    }
}
