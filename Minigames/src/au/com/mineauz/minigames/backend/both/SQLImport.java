package au.com.mineauz.minigames.backend.both;

import java.sql.SQLException;
import java.util.UUID;

import au.com.mineauz.minigames.backend.BackendImportCallback;
import au.com.mineauz.minigames.backend.ConnectionHandler;
import au.com.mineauz.minigames.backend.ConnectionPool;
import au.com.mineauz.minigames.backend.StatementKey;

public class SQLImport implements BackendImportCallback {
	private final ConnectionPool pool;
	
	private final StatementKey clearPlayers;
	private final StatementKey clearMinigames;
	private final StatementKey clearStats;
	
	private final StatementKey insertPlayer;
	private final StatementKey insertMinigame;
	private final StatementKey insertStat;
	
	private ConnectionHandler handler;
	private int playerBatchCount;
	private int minigameBatchCount;
	private int statBatchCount;
	
	public SQLImport(ConnectionPool pool) {
		this.pool = pool;
		
		// Prepare queries
		clearPlayers = new StatementKey("DELETE FROM `Players`;");
		clearMinigames = new StatementKey("DELETE FROM `Minigames`;");
		clearStats = new StatementKey("DELETE FROM `PlayerStats`;");
		
		insertPlayer = new StatementKey("INSERT INTO `Players` VALUES (?,?,?);");
		insertMinigame = new StatementKey("INSERT INTO `Minigames` VALUES (?,?);");
		insertStat = new StatementKey("INSERT INTO `PlayerStats` VALUES (?,?,?,?);"); 
	}
	
	@Override
	public void begin() {
		// Clear the database
		try {
			handler = pool.getConnection();
			handler.beginTransaction();
			
			handler.executeUpdate(clearPlayers);
			handler.executeUpdate(clearMinigames);
			handler.executeUpdate(clearStats);
			
			playerBatchCount = 0;
			minigameBatchCount = 0;
			statBatchCount = 0;
		} catch (SQLException e) {
			if (handler != null) {
				handler.endTransactionFail();
				handler.release();
			}
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void acceptPlayer(UUID playerId, String name, String displayName) {
		try {
			handler.batchUpdate(insertPlayer, playerId.toString(), name, displayName);
			++playerBatchCount;
			
			if (playerBatchCount > 50) {
				handler.executeBatch(insertPlayer);
				playerBatchCount = 0;
			}
		} catch (SQLException e) {
			handler.endTransactionFail();
			handler.release();
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void acceptMinigame(int id, String name) {
		try {
			// Handle any remaining players
			if (playerBatchCount >= 0) {
				handler.executeBatch(insertPlayer);
				playerBatchCount = 0;
			}
			
			// batch minigames
			handler.batchUpdate(insertMinigame, id, name);
			++minigameBatchCount;
			
			if (minigameBatchCount > 50) {
				handler.executeBatch(insertMinigame);
				minigameBatchCount = 0;
			}
		} catch (SQLException e) {
			handler.endTransactionFail();
			handler.release();
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void acceptStat(UUID playerId, int minigameId, String stat, long value) {
		try {
			// Handle any remaining minigames
			if (minigameBatchCount > 0) {
				handler.executeBatch(insertMinigame);
				minigameBatchCount = 0;
			}
			
			// batch stats
			handler.batchUpdate(insertStat, playerId.toString(), minigameId, stat, value);
			++statBatchCount;
			
			if (statBatchCount > 50) {
				handler.executeBatch(insertStat);
				statBatchCount = 0;
			}
		} catch (SQLException e) {
			handler.endTransactionFail();
			handler.release();
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void end() {
		try {
			// Handle remaining stats
			if (statBatchCount > 0) {
				handler.executeBatch(insertStat);
				statBatchCount = 0;
			}
			
			handler.endTransaction();
		} catch (SQLException e) {
			handler.endTransactionFail();
			handler.release();
			throw new IllegalStateException(e);
		}
	}

}
