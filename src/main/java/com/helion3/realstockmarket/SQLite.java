package com.helion3.realstockmarket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SQLite {
	
	
	/**
	 * 
	 * @return
	 */
	public Connection getConnection(){
		Connection c = null;
	    try {
		    Class.forName("org.sqlite.JDBC");
		    c = DriverManager.getConnection("jdbc:sqlite:"+RealStockMarket.pluginName+".db");
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
	    		return;
	    	}
	
			stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS"; // put sql here 
			stmt.executeUpdate(sql);

	    } catch ( Exception e ) {
	    	RealStockMarket.log.warning( e.getClass().getName() + ": " + e.getMessage() );
	    } finally {
	    	if( stmt != null ) try { stmt.close(); } catch ( Exception ignored) { };
	    	if( c != null ) try { c.close(); } catch ( Exception ignored) { };
	    }
	}
}