package au.com.mineauz.minigames.backend.sqlite;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.backend.ConnectionHandler;
import au.com.mineauz.minigames.backend.StatementKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardOrder;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.StatValueField;
import au.com.mineauz.minigames.stats.StoredStat;
import com.google.common.collect.Lists;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

class SQLiteStatLoader {
    private final SQLiteBackend backend;
    private final Logger logger;

    private final StatementKey getSingleAsc;
    private final StatementKey getSingleDesc;
    private final StatementKey getSingle;

    public SQLiteStatLoader(SQLiteBackend backend, Logger logger) {
        this.backend = backend;
        this.logger = logger;

        // Init the statements used for querying
        getSingleAsc = new StatementKey("SELECT `p`.`player_id`, `p`.`name`, `p`.`displayname`, `value` FROM `PlayerStats` AS `s` JOIN `Players` AS `p` ON (`p`.`player_id` = `s`.`player_id`) WHERE `minigame_id`=? AND `stat`=? ORDER BY `value` ASC LIMIT ?, ?;");
        getSingleDesc = new StatementKey("SELECT `p`.`player_id`, `p`.`name`, `p`.`displayname`, `value` FROM `PlayerStats` AS `s` JOIN `Players` AS `p` ON (`p`.`player_id` = `s`.`player_id`) WHERE `minigame_id`=? AND `stat`=? ORDER BY `value` DESC LIMIT ?, ?;");
        getSingle = new StatementKey("SELECT `value` FROM `PlayerStats` WHERE `minigame_id`=? AND `player_id`=? AND `stat`=?;");
    }

    public List<StoredStat> loadStatValues(Minigame minigame, MinigameStat stat, StatValueField field, ScoreboardOrder order, int offset, int length) {
        MinigameUtils.debugMessage("SQLite beginning stat load for " + minigame.getName(false) + ", " + stat + ", " + field);
        ConnectionHandler handler = null;
        try {
            handler = backend.getPool().getConnection();
            // First get the id
            int minigameId = backend.getMinigameId(handler, minigame);

            return loadStats(handler, minigameId, stat, field, order, offset, length);
        } catch (SQLException e) {
            return Collections.emptyList();
        } finally {
            if (handler != null) {
                handler.release();
            }
            MinigameUtils.debugMessage("SQLite completed stat load for " + minigame.getName(false));
        }
    }

    public long loadSingleValue(Minigame minigame, MinigameStat stat, StatValueField field, UUID playerId) {
        ConnectionHandler handler = null;
        try {
            handler = backend.getPool().getConnection();
            // First get the id
            int minigameId = backend.getMinigameId(handler, minigame);

            String statName = stat.getName() + field.getSuffix();

            try (ResultSet rs = handler.executeQuery(getSingle, minigameId, playerId.toString(), statName)) {
                if (rs.next()) {
                    return rs.getLong("value");
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to load stat for " + minigame.getName(false) + " " + playerId, e);
            return 0;
        } finally {
            if (handler != null) {
                handler.release();
            }
        }
    }

    // Loads from the stats table
    private List<StoredStat> loadStats(ConnectionHandler handler, int minigameId, MinigameStat stat, StatValueField field, ScoreboardOrder order, int offset, int length) throws SQLException {
        String statName = stat.getName() + field.getSuffix();

        StatementKey statement;
        switch (order) {
            case ASCENDING:
                statement = getSingleAsc;
                break;
            case DESCENDING:
                statement = getSingleDesc;
                break;
            default:
                throw new AssertionError();
        }

        List<StoredStat> stats = Lists.newArrayList();
        try (ResultSet rs = handler.executeQuery(statement, minigameId, statName, offset, length)) {
            while (rs.next()) {
                stats.add(loadStat(rs));
            }

            return stats;
        }
    }

    private StoredStat loadStat(ResultSet rs) throws SQLException {
        UUID playerId = UUID.fromString(rs.getString("player_id"));
        String name = rs.getString("name");
        String displayName = rs.getString("displayname");
        long value = rs.getLong("value");

        return new StoredStat(playerId, name, displayName, value);
    }
}
