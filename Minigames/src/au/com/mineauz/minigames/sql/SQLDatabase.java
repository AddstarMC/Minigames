package au.com.mineauz.minigames.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import au.com.mineauz.minigames.Minigames;

public class SQLDatabase {
	private Connection sql = null;
	private static Minigames plugin = Minigames.plugin;
	
	public PreparedStatement checkCompletion;
	public PreparedStatement insertAttempt;
	public PreparedStatement insertStat;
	public PreparedStatement insertStatTotal;
	public PreparedStatement insertStatMin;
	public PreparedStatement insertStatMax;
	
	public PreparedStatement insertMinigame;
	public PreparedStatement insertPlayer;
	
	public boolean loadSQL(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			
			sql = DriverManager.getConnection(
				String.format("jdbc:mysql://%s:%s/%s", 
						plugin.getConfig().getString("sql-host"), 
						plugin.getConfig().getInt("sql-port"), 
						plugin.getConfig().getString("sql-database")), 
				plugin.getConfig().getString("sql-username"),
				plugin.getConfig().getString("sql-password"));
			
			ensureTables();
			createStatements();
			return true;
		}
		catch(ClassNotFoundException e){
			plugin.getLogger().info("MySQL not found! You cannot use SQL to save data!");
			return false;
		}
		catch(SQLException e){
			plugin.getLogger().info("Error connecting to MySQL database. You cannot use SQL to save data!");
			e.printStackTrace();
			return false;
		}
	}
	
	private void ensureTables() {
		try {
			Statement statement = sql.createStatement();
			
			// Check the players table
			try {
				statement.executeQuery("SELECT 1 FROM `Players` LIMIT 0;");
			} catch (SQLException e) {
				statement.executeUpdate("CREATE TABLE `Players` (`player_id` CHAR(36) PRIMARY KEY, `name` VARCHAR(30) NOT NULL, `displayname` VARCHAR(30), INDEX (`name`, `player_id`));");
			}
			
			// Check the minigames table
			try {
				statement.executeQuery("SELECT 1 FROM `Minigames` LIMIT 0;");
			} catch (SQLException e) {
				statement.executeUpdate("CREATE TABLE `Minigames` (`minigame_id` INTEGER AUTO_INCREMENT PRIMARY KEY, `name` VARCHAR(30) NOT NULL, UNIQUE INDEX (`name`));");
			}
			
			// Check the player attempts table
			try {
				statement.executeQuery("SELECT 1 FROM `PlayerAttempts` LIMIT 0;");
			} catch (SQLException e) {
				statement.executeUpdate("CREATE TABLE `PlayerAttempts` (`player_id` CHAR(36) REFERENCES `Players` (`player_id`) ON DELETE CASCADE, `minigame_id` INTEGER REFERENCES `Minigames` (`minigame_id`) ON DELETE CASCADE, `wins` INTEGER NOT NULL, `attempts` INTEGER NOT NULL, `time_min` INTEGER, `time_max` INTEGER, `time_total` INTEGER, PRIMARY KEY (`player_id`, `minigame_id`));");
			}
			
			// Check the player stats table
			try {
				statement.executeQuery("SELECT 1 FROM `PlayerStats` LIMIT 0;");
			} catch (SQLException e) {
				statement.executeUpdate("CREATE TABLE `PlayerStats` (`player_id` CHAR(36) REFERENCES `Players` (`player_id`) ON DELETE CASCADE, `minigame_id` INTEGER REFERENCES `Minigames` (`minigame_id`) ON DELETE CASCADE, `stat` VARCHAR(20) NOT NULL, `value` INTEGER, PRIMARY KEY (`player_id`, `minigame_id`, `key`));");
			}
			
			// Check the stat metadata table
			try {
				statement.executeQuery("SELECT 1 FROM `StatMetadata` LIMIT 0;");
			} catch (SQLException e) {
				statement.executeUpdate("CREATE TABLE `StatMetadata` (`minigame_id` INTEGER REFERENCES `Minigames` (`minigame_id`) ON DELETE CASCADE, `stat` VARCHAR(20) NOT NULL, `display_name` VARCHAR(20), `format` ENUM('LAST', 'LAST+TOTAL', 'MIN', 'MIN+TOTAL', 'MAX', 'MAX+TOTAL', 'MIN+MAX', 'MIN+MAX+TOTAL', 'TOTAL'), PRIMARY KEY (`minigame_id`, `stat`));");
			}
			
			statement.close();
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to initialize database!", e);
		}
	}
	
	private void createStatements() throws SQLException {
		checkCompletion = sql.prepareStatement("SELECT IF(`wins` > 0, 1, 0) FROM `PlayerAttempts` WHERE `player_id`=? AND `minigame_id`=?;");
		insertAttempt = sql.prepareStatement("INSERT INTO `PlayerAttempts` VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `wins`=`wins`+?, `attempts`=`attempts`+?, `time_min`=LEAST(`time_min`, ?), `time_max`=GREATEST(`time_max`, ?), `time_total`=`time_total`+?;");
		insertStat = sql.prepareStatement("INSERT INTO `PlayerStats` VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `value`=?");
		insertStatTotal = sql.prepareStatement("INSERT INTO `PlayerStats` VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `value`=`value`+?");
		insertStatMin = sql.prepareStatement("INSERT INTO `PlayerStats` VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `value`=LEAST(`value`, ?)");
		insertStatMax = sql.prepareStatement("INSERT INTO `PlayerStats` VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `value`=GREATEST(`value`, ?)");
		
		insertMinigame = sql.prepareStatement("INSERT INTO `Minigames` (`name`) VALUES (?) ON DUPLICATE KEY UPDATE `minigame_id`=LAST_INSERT_ID(`minigame_id`);", Statement.RETURN_GENERATED_KEYS);
		insertPlayer = sql.prepareStatement("INSERT INTO `Players` VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `displayname` = VALUES(`displayname`);");
	}

	public Connection getSql() {
		return sql;
	}
	
	public void close(){
		try{
			sql.close();
		}
		catch ( SQLException e ){
			// Doesnt matter
		}
	}
	
	public boolean isOpen()
	{
		try
		{
			return sql != null && !sql.isClosed();
		}
		catch ( SQLException e )
		{
			return false;
		}
	}
	
	public boolean isTable(String table){
		try{
			Statement statement = sql.createStatement();
			statement.executeQuery(String.format("SELECT 1 FROM %s LIMIT 0", table));
			statement.close();
			return true;
		}
		catch(SQLException e){
			return false;
		}
	}
	
	public boolean columnExists(String column, String table)
	{
		try{
			Statement statement = sql.createStatement();
			statement.executeQuery(String.format("SELECT %s FROM %s LIMIT 0", column, table));
			statement.close();
			return true;
		}
		catch(SQLException e){
			return false;
		}
	}
}
