package au.com.mineauz.minigames.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;

public class SQLUtilities {
	/**
	 * Gets the minigame's id. This will insert into the table if required
	 * @param statement The statement to execute on
	 * @param minigame The minigame to get the id for
	 * @return The minigame id
	 * @throws SQLException thrown if an SQLException occurs somewhere in the statement
	 */
	public static int getMinigameId(PreparedStatement statement, Minigame minigame) throws SQLException {
		statement.setString(1, minigame.getName(false));
		statement.executeUpdate();
		
		ResultSet rs = statement.getGeneratedKeys();
		try {
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				// Insert should always return the value
				throw new AssertionError();
			}
		} finally {
			rs.close();
		}
	}
	
	/**
	 * Updates the players data. This may include updating the name or display name if required
	 * @param statement The statement to execute on
	 * @param player The player to update
	 * @throws SQLException thrown if an SQLException occurs somewhere in the statement
	 */
	public static void updatePlayer(PreparedStatement statement, MinigamePlayer player) throws SQLException {
		statement.setString(1, player.getUUID().toString());
		statement.setString(2, player.getName());
		statement.setString(3, player.getDisplayName());
		
		statement.executeUpdate();
	}
}
