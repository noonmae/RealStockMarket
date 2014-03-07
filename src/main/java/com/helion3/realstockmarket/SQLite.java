package com.helion3.realstockmarket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;

public class SQLite {
	
	/**
	 * 
	 * @return
	 */
	public Connection getConnection(){
		Connection c = null;
	    try {
		    Class.forName("org.sqlite.JDBC");
		    c = DriverManager.getConnection("jdbc:sqlite:plugins/"+RealStockMarket.pluginName+"/"+RealStockMarket.pluginName+".db");
		    RealStockMarket.log.info("Connected to sqlite...");
	    } catch ( Exception e ) {
	    	RealStockMarket.log.warning( e.getClass().getName() + ": " + e.getMessage() );
	    }
	    return c;
	}

	
	/**
	 * 
	 */
	public void createTables(){
		
		Connection c = null;
	    Statement stmt = null;
	    try {
	      
	    	c = getConnection();
	    	
	    	if( c == null || c.isClosed() ){
	    		RealStockMarket.log.warning( "Failed to connect to database." );
	    	} else {
	
				stmt = c.createStatement();
				String sql = ""
				+ "CREATE TABLE IF NOT EXISTS transactions ("
				+ "id INTEGER NOT NULL,"
				+ "player_id INTEGER,"
				+ "trxn_type VARCHAR(4),"
				+ "trxn_date TEXT,"
				+ "symbol VARCHAR(4),"
				+ "symbol_price DOUBLE,"
				+ "quantity INTEGER,"
				+ "total_price DOUBLE,"
				+ "PRIMARY KEY (id))";
				stmt.executeUpdate(sql);
				
	    	}
	    } catch ( Exception e ) {
	    	RealStockMarket.log.warning( e.getClass().getName() + ": " + e.getMessage() );
	    	e.printStackTrace();
	    } finally {
	    	if( stmt != null ) try { stmt.close(); } catch ( Exception ignored) { };
	    	if( c != null ) try { c.close(); } catch ( Exception ignored) { };
	    }
	}
	
	
	/**
	 * Logs the purchase of a quantity of stocks by a player.
	 * 
	 * @param player
	 * @param stock
	 * @param quantity
	 */
	public void logStockPurchase( Player player, Stock stock, int quantity ){
		Connection conn = null;
		PreparedStatement s = null;
		try {

			conn = getConnection();
	    	
	    	if( conn == null || conn.isClosed() ){
	    		RealStockMarket.log.warning( "Failed to connect to database." );
	    	} else {
				s = conn.prepareStatement(
					"INSERT INTO transactions (player_id,trxn_type,trxn_date,symbol,symbol_price,quantity,total_price) "
					+ "VALUES (?,'buy',date('now'),?,?,?,?)");
	    		s.setInt(1, 1); // @todo fix
	    		s.setString(2, stock.getSymbol());
	    		s.setDouble(3, stock.getLatestPrice());
	    		s.setInt(4, quantity);
	    		s.setDouble(5, (stock.getLatestPrice() * quantity));
	    		s.executeUpdate();
	    	}
        } catch (SQLException e) {
        	e.printStackTrace();
        } finally {
        	if(s != null) try { s.close(); } catch (SQLException ignored) {}
        	if(conn != null) try { conn.close(); } catch (SQLException ignored) {}
        }
	}
	
	
	/**
	 * Logs sales of stock holdings by a player.
	 * 
	 * @param player
	 * @param stock
	 * @param quantity
	 */
	public void logStockSale( Player player, Stock stock, int quantity ){
		Connection conn = null;
		PreparedStatement s = null;
		try {

			conn = getConnection();
	    	
	    	if( conn == null || conn.isClosed() ){
	    		RealStockMarket.log.warning( "Failed to connect to database." );
	    	} else {
				s = conn.prepareStatement(
					"INSERT INTO transactions (player_id,trxn_type,trxn_date,symbol,symbol_price,quantity,total_price) "
					+ "VALUES (?,'sale',date('now'),?,?,?,?)");
	    		s.setInt(1, 1); // @todo fix
	    		s.setString(2, stock.getSymbol());
	    		s.setDouble(3, stock.getLatestPrice());
	    		s.setInt(4, quantity);
	    		s.setDouble(5, (stock.getLatestPrice() * quantity));
	    		s.executeUpdate();
	    	}
        } catch (SQLException e) {
        	e.printStackTrace();
        } finally {
        	if(s != null) try { s.close(); } catch (SQLException ignored) {}
        	if(conn != null) try { conn.close(); } catch (SQLException ignored) {}
        }
	}
}