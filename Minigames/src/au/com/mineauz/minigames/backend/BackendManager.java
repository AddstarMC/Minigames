package au.com.mineauz.minigames.backend;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.backend.mysql.MySQLBackend;
import au.com.mineauz.minigames.backend.sqlite.SQLiteBackend;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardOrder;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.StatValueField;
import au.com.mineauz.minigames.stats.StoredGameStats;
import au.com.mineauz.minigames.stats.StoredStat;

public class BackendManager {
	private final Logger logger;
	
	private Backend backend;
	private ListeningExecutorService executorService;
	private Executor bukkitThreadExecutor;
	
	public BackendManager(Logger logger) {
		this.logger = logger;
		
		bukkitThreadExecutor = new Executor() {
			@Override
			public void execute(Runnable command) {
				Bukkit.getScheduler().runTask(Minigames.plugin, command);
			}
		};
		
		executorService = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
	}
	
	/**
	 * Initializes the backend.
	 * @param config The configuration to load settings from
	 */
	public boolean initialize(ConfigurationSection config) {
		ConfigurationSection backendSection = config.getConfigurationSection("backend");
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
		if (type.equals("sqlite")) {
			backend = new SQLiteBackend();
		} else if (type.equals("mysql")) {
			backend = new MySQLBackend(logger);
		} else {
			// Default to this
			logger.warning("Invalid backend type " + type + ". Falling back to SQLite");
			backend = new SQLiteBackend();
		}
		
		// Init
		return backend.initialize(backendSection);
	}
	
	/**
	 * Asks the backend to shut down
	 */
	public void shutdown() {
		
	}
	
	/**
	 * Retrieves some stats from the backend. These stats are loaded asynchronously
	 * @param minigame The minigame the stats are for
	 * @param stat The stat to load
	 * @param field The field of the stat to load
	 * @param order The order to retrieve in
	 * @param offset The offset to retrieve from
	 * @param length The number of stats to retrieve
	 * @return A ListenableFuture that returns the list of StoredStats loaded
	 */
	public ListenableFuture<List<StoredStat>> loadStats(final Minigame minigame, final MinigameStat stat, final StatValueField field, final ScoreboardOrder order, final int offset, final int length) {
		return executorService.submit(new Callable<List<StoredStat>>() {
			@Override
			public List<StoredStat> call() throws Exception {
				return backend.loadStats(minigame, stat, field, order, offset, length);
			}
		});
	}
	
	/**
	 * Retrieves a single statistic value from the backend. This is loaded asynchronously
	 * @param minigame The minigame the stat is for
	 * @param stat The stat to load
	 * @param field The field of the stat to load
	 * @param playerId The player that owns the stat
	 * @return The value of the stat. If it is not set, 0 will be returned
	 */
	public ListenableFuture<Long> loadSingleStat(final Minigame minigame, final MinigameStat stat, final StatValueField field, final UUID playerId) {
		return executorService.submit(new Callable<Long>() {
			@Override
			public Long call() throws Exception {
				return backend.getStat(minigame, playerId, stat, field);
			}
		});
	}
	
	/**
	 * Queues a task to save the stats to the backend. The stats will be saved asynchronously
	 * @param stats The stats to be saved
	 * @return A ListenableFuture that returns the inputed stats for chaining.
	 */
	public ListenableFuture<StoredGameStats> saveStats(final StoredGameStats stats) {
		return executorService.submit(new Callable<StoredGameStats>() {
			@Override
			public StoredGameStats call() throws Exception {
				backend.saveGameStatus(stats);
				return stats;
			}
		});
	}
	
	/**
	 * Adds a callback to the ListenableFuture that will be executed on the Minecraft server thread
	 * @param future The future to add the callback to
	 * @param callback The callback to be added
	 */
	public <T> void addServerThreadCallback(ListenableFuture<T> future, FutureCallback<T> callback) {
		Futures.addCallback(future, callback, bukkitThreadExecutor);
	}
}
