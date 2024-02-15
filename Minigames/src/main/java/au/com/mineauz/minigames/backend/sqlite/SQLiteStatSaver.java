package au.com.mineauz.minigames.backend.sqlite;

import au.com.mineauz.minigames.backend.ConnectionHandler;
import au.com.mineauz.minigames.backend.StatementKey;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.StatFormat;
import au.com.mineauz.minigames.stats.StatisticValueField;
import au.com.mineauz.minigames.stats.StoredGameStats;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.UUID;

class SQLiteStatSaver {
    private final SQLiteBackend backend;
    private final ComponentLogger logger;

    private final StatementKey insertStat;
    private final StatementKey insertStatTotal;
    private final StatementKey insertStatMin;
    private final StatementKey insertStatMax;

    private final StatementKey[] insertStatements = new StatementKey[4];

    public SQLiteStatSaver(SQLiteBackend backend, ComponentLogger logger) {
        this.backend = backend;
        this.logger = logger;

        String sqlDateEntered = "(SELECT coalesce((SELECT `entered`      FROM `PlayerStats` WHERE `player_id`=? AND `minigame_id`=? AND `stat`=?), datetime('now','localtime')))";
        String sqlSumValue = "(SELECT coalesce((SELECT (`value`+?)    FROM `PlayerStats` WHERE `player_id`=? AND `minigame_id`=? AND `stat`=?), ?))";
        String sqlMinValue = "(SELECT coalesce((SELECT MIN(`value`,?) FROM `PlayerStats` WHERE `player_id`=? AND `minigame_id`=? AND `stat`=?), ?))";
        String sqlMaxValue = "(SELECT coalesce((SELECT MAX(`value`,?) FROM `PlayerStats` WHERE `player_id`=? AND `minigame_id`=? AND `stat`=?), ?))";

        // Create statements
        insertStat = new StatementKey("INSERT OR REPLACE INTO `PlayerStats` (`player_id`, `minigame_id`, `stat`, `value`, `last_updated`, `entered`) VALUES (?, ?, ?, ? " + ", datetime('now','localtime'), " + sqlDateEntered + ");");
        insertStatTotal = new StatementKey("INSERT OR REPLACE INTO `PlayerStats` (`player_id`, `minigame_id`, `stat`, `value`, `last_updated`, `entered`) VALUES (?, ?, ?, " + sqlSumValue + ", datetime('now','localtime'), " + sqlDateEntered + ");");
        insertStatMin = new StatementKey("INSERT OR REPLACE INTO `PlayerStats` (`player_id`, `minigame_id`, `stat`, `value`, `last_updated`, `entered`) VALUES (?, ?, ?, " + sqlMinValue + ", datetime('now','localtime'), " + sqlDateEntered + ");");
        insertStatMax = new StatementKey("INSERT OR REPLACE INTO `PlayerStats` (`player_id`, `minigame_id`, `stat`, `value`, `last_updated`, `entered`) VALUES (?, ?, ?, " + sqlMaxValue + ", datetime('now','localtime'), " + sqlDateEntered + ");");

        // Prepare lookup table
        insertStatements[StatisticValueField.Last.ordinal()] = insertStat;
        insertStatements[StatisticValueField.Min.ordinal()] = insertStatMin;
        insertStatements[StatisticValueField.Max.ordinal()] = insertStatMax;
        insertStatements[StatisticValueField.Total.ordinal()] = insertStatTotal;
    }

    public void saveData(StoredGameStats data) {
        MinigameMessageManager.debugMessage("SQLite beginning save of " + data);

        ConnectionHandler handler = null;
        try {
            handler = backend.getPool().getConnection();
            try {
                handler.beginTransaction();

                // Get the minigame id and update both the player and game
                int minigameId = backend.getMinigameId(handler, data.getMinigame());
                backend.updatePlayer(handler, data.getPlayer());

                saveStats(handler, data, data.getPlayer().getUUID(), minigameId);

                // Commit the changes
                handler.endTransaction();
            } catch (SQLException e) {
                logger.error("Failed to save stats for " + data.getPlayer().getName(), e);

                handler.endTransactionFail();
            } finally {
                MinigameMessageManager.debugMessage("SQLite completed save of " + data);
            }
        } catch (SQLException e) {
            logger.warn("", e);
        } finally {
            if (handler != null) {
                handler.release();
            }
        }
    }

    private void saveStats(ConnectionHandler handler, StoredGameStats data, UUID player, int minigameId) throws SQLException {

        MinigameMessageManager.debugMessage("SQLite saving stats for " + player + ", game " + minigameId);

        // Prepare all updates
        for (Entry<MinigameStat, Long> entry : data.getStats().entrySet()) {
            StatFormat format = data.getFormat(entry.getKey());
            // Only store this stat if it's required
            if (entry.getKey().shouldStoreStat(entry.getValue(), format)) {
                queueStat(handler, entry.getKey(), entry.getValue(), format, player, minigameId);
            }
        }

        // Push all to database
        handler.executeBatch(insertStat);
        handler.executeBatch(insertStatTotal);
        handler.executeBatch(insertStatMin);
        handler.executeBatch(insertStatMax);

        MinigameMessageManager.debugMessage("SQLite completed save for " + player + ", game " + minigameId);
    }

    private void queueStat(ConnectionHandler handler, MinigameStat stat, long value, StatFormat format, UUID player, int minigameId) throws SQLException {
        for (StatisticValueField field : format.getFields()) {
            String statName = stat.getName() + field.getSuffix();

            if (field == StatisticValueField.Last) {
                //                              player_id,         minigame_id, stat,    value, [..... fields for sqlDateEntered .....]
                handler.batchUpdate(insertStat, player.toString(), minigameId, statName, value, player.toString(), minigameId, statName);
            } else {
                //                                                     player_id,         minigame_id, stat,    value, [fields for sqlSumValue|sqlMaxValue|sqlMinValue], [..... fields for sqlDateEntered .....]
                handler.batchUpdate(insertStatements[field.ordinal()], player.toString(), minigameId, statName, value, player.toString(), minigameId, statName, value, player.toString(), minigameId, statName);
            }
        }
    }
}
