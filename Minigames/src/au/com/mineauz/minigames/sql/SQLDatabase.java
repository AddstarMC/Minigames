package au.com.mineauz.minigames.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import au.com.mineauz.minigames.Minigames;

public class SQLDatabase {
	private Connection sql = null;
	private static Minigames plugin = Minigames.plugin;
	
	public boolean loadSQL(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			
			setSql(DriverManager.getConnection(
				String.format("jdbc:mysql://%s:%s/%s", 
						plugin.getConfig().getString("sql-host"), 
						plugin.getConfig().getInt("sql-port"), 
						plugin.getConfig().getString("sql-database")), 
				plugin.getConfig().getString("sql-username"),
				plugin.getConfig().getString("sql-password")));
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

	public Connection getSql() {
		return sql;
	}

	public void setSql(Connection sql) {
		this.sql = sql;
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
