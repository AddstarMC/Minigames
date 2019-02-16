package au.com.mineauz.minigames.backend.mysql;

import au.com.mineauz.minigames.backend.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class LegacyExporter {
    private final ConnectionPool pool;
    private final String database;
    private final BackendImportCallback callback;
    private final ExportNotifier notifier;
    private final StatementKey selectTables;
    private final StatementKey selectPlayers;
    private final StatementKey selectStats;
    private ConnectionHandler handler;
    private String notifyState;
    private int notifyCount;
    private long notifyTime;

    public LegacyExporter(ConnectionPool pool, String database, BackendImportCallback callback, ExportNotifier notifier) {
        this.pool = pool;
        this.database = database;
        this.callback = callback;
        this.notifier = notifier;

        selectTables = new StatementKey("SELECT `TABLE_NAME` FROM information_schema.`TABLES` WHERE `TABLE_SCHEMA`=? AND `TABLE_NAME` LIKE 'mgm_%_comp';");
        selectPlayers = new StatementKey("SELECT `UUID`, `Player` FROM `mgm_conversion` GROUP BY `UUID`;");
        selectStats = new StatementKey("SELECT `UUID`, m.`minigame_id`, `Completion`, `Failures`, `Kills`, `Deaths`, `Score`, `Time`, `Reverts`, `TotalKills`, `TotalDeaths`, `TotalScore`, `TotalTime`, `TotalReverts` FROM `mgm_conversion` AS c JOIN `mgm_minigames` AS m ON m.`name`=c.`minigame`;");
    }

    public boolean doExport() {
        try {
            handler = pool.getConnection();
            callback.begin();

            initializeConversion();

            exportPlayers();
            exportMinigames();
            exportStats();

            notifyNext("Done");

            callback.end();
            notifier.onComplete();
            return true;
        } catch (SQLException | IllegalStateException e) {
            notifier.onError(e, notifyState, notifyCount);
            return false;
        } finally {
            handler.release();
        }
    }

    private void initializeConversion() throws SQLException {
        notifyNext("Preparing...");
        // Consolidate the tables into one
        Statement statement = handler.getConnection().createStatement();
        statement.executeUpdate("CREATE TEMPORARY TABLE `mgm_conversion` (`UUID` char(36), `Player` varchar(32), `Minigame` varchar(30), `Completion` INTEGER, `Kills` INTEGER, `Deaths` INTEGER, `Score` INTEGER, `Time` BIGINT, `Reverts` INTEGER, `TotalKills` INTEGER, `TotalDeaths` INTEGER, `TotalScore` INTEGER, `TotalReverts` INTEGER, `TotalTime` BIGINT, `Failures` INTEGER);");

        try (ResultSet rs = handler.executeQuery(selectTables, database)) {
            while (rs.next()) {
                String tableName = rs.getString(1);
                String minigame = tableName.substring(4, tableName.lastIndexOf('_'));

                try {
                    statement.executeUpdate("INSERT INTO `mgm_conversion` SELECT `UUID`, `Player`, '" + minigame + "' AS `Minigame`, `Completion`, `Kills`, `Deaths`, `Score`, IF(`Time`='',0,`Time`), `Reverts`, `TotalKills`, `TotalDeaths`, `TotalScore`, `TotalReverts`, IF(`TotalTime`='',0,`TotalTime`), `Failures` FROM `mgm_" + minigame + "_comp`;");
                } catch (SQLException e) {
                    // Happens if the table is not updated (oreturn false;ld minigame)
                    System.err.println("Skipping stat table for " + minigame + ". " + e.getMessage());
                }

                ++notifyCount;
                notifyProgress();
            }
        }

        statement.close();
    }

    private void exportPlayers() throws SQLException {
        notifyNext("Exporting players...");

        try (ResultSet rs = handler.executeQuery(selectPlayers)) {
            while (rs.next()) {
                callback.acceptPlayer(UUID.fromString(rs.getString("UUID")), rs.getString("Player"), rs.getString("Player"));
                ++notifyCount;
                notifyProgress();
            }
        }
    }

    private void exportMinigames() throws SQLException {
        notifyNext("Exporting minigames...");

        // Create an id for each minigame
        Statement statement = handler.getConnection().createStatement();
        statement.executeUpdate("CREATE TEMPORARY TABLE `mgm_minigames` (`minigame_id` INTEGER AUTO_INCREMENT PRIMARY KEY, `name` VARCHAR(30), UNIQUE INDEX (`name`, `minigame_id`));");
        statement.executeUpdate("INSERT INTO `mgm_minigames` (`name`) SELECT DISTINCT `Minigame` AS `name` FROM `mgm_conversion`;");

        // Do the conversion
        try (ResultSet rs = statement.executeQuery("SELECT `minigame_id`, `name` FROM `mgm_minigames`")) {
            while (rs.next()) {
                callback.acceptMinigame(rs.getInt("minigame_id"), rs.getString("name"));
                ++notifyCount;
                notifyProgress();
            }
        }

        statement.close();
    }

    private void exportStats() throws SQLException {
        notifyNext("Exporting stats...");

        try (ResultSet rs = handler.executeQuery(selectStats)) {
            while (rs.next()) {
                UUID player = UUID.fromString(rs.getString("UUID"));
                int minigameId = rs.getInt("minigame_id");

                int deaths = rs.getInt("deaths");
                int kills = rs.getInt("kills");
                int score = rs.getInt("score");
                long time = rs.getLong("time");
                int reverts = rs.getInt("reverts");

                int totalDeaths = rs.getInt("totaldeaths");
                int totalKills = rs.getInt("totalkills");
                int totalScore = rs.getInt("totalscore");
                long totalTime = rs.getLong("totaltime");
                int totalReverts = rs.getInt("totalreverts");

                int completions = rs.getInt("completion");
                int failures = rs.getInt("failures");

                if (completions == 0) {
                    callback.acceptStat(player, minigameId, "attempts_total", failures);
                } else {
                    callback.acceptStat(player, minigameId, "attempts_total", completions + failures);
                    callback.acceptStat(player, minigameId, "wins_total", completions);

                    callback.acceptStat(player, minigameId, "time_min", time);
                    callback.acceptStat(player, minigameId, "time_total", totalTime);

                    if (deaths != -1 && totalDeaths != -1) {
                        callback.acceptStat(player, minigameId, "deaths_min", deaths);
                        callback.acceptStat(player, minigameId, "deaths_total", totalDeaths);
                    }

                    if (reverts != -1 && totalReverts != -1) {
                        callback.acceptStat(player, minigameId, "reverts_min", reverts);
                        callback.acceptStat(player, minigameId, "reverts_total", totalReverts);
                    }

                    if (kills != -1 && totalKills > 0) {
                        callback.acceptStat(player, minigameId, "kills_max", kills);
                        callback.acceptStat(player, minigameId, "kills_total", totalKills);
                    }

                    if (score != -1 && totalScore > 0) {
                        callback.acceptStat(player, minigameId, "score_max", score);
                        callback.acceptStat(player, minigameId, "score_total", totalScore);
                    }
                }

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
