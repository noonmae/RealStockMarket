package com.helion3.realstockmarket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerIdentification {
	
	
	/**
	 * Loads `players` ID for a real player into our cache. 
	 * 
	 * Runs during PlayerJoin events, so it will never be for a fake/null
	 * player.
	 * 
	 * @param player
	 */
	public static void cacheRealStockMarketPlayer( final Player player ){
		
		// Lookup the player
		StockMarketPlayer stockMarketPlayer = getRealStockMarketPlayer( player );
		if( stockMarketPlayer != null ){
			stockMarketPlayer = comparePlayerToCache( player, stockMarketPlayer );
			RealStockMarket.log.info("Loaded player " + player.getName() + ", id: " + stockMarketPlayer.getId() + " into the cache.");
			RealStockMarket.stockMarketPlayers.put( player.getUniqueId(), stockMarketPlayer );
			return;
		}
			
		// Player is new, create a record for them
		addPlayer( player );
		
	}
	
	
	/**
	 * Returns a `players` ID for the described player name. If
	 * one cannot be found, returns 0.
	 * 
	 * Used by the recorder in determining proper foreign key
	 * 
	 * @param playerName
	 * @return
	 */
	public static StockMarketPlayer getRealStockMarketPlayer( String playerName ){
		
		Player player = Bukkit.getPlayer(playerName);

		if( player != null ) return getRealStockMarketPlayer( player );
			
		// Player not online, we need to go to cache
		StockMarketPlayer stockMarketPlayer = lookupByName( playerName );
		
		// Player found! Return the id
		if( stockMarketPlayer != null ) return stockMarketPlayer;
		
		// No player exists! We must create one
		return null;
		
	}
	
	
	/**
	 * Returns a `players` ID for the described player object. If
	 * one cannot be found, returns 0.
	 * 
	 * Used by the recorder in determining proper foreign key
	 * 
	 * @param playerName
	 * @return
	 */
	public static StockMarketPlayer getRealStockMarketPlayer( Player player ){
		
		if( player.getUniqueId() == null ){
			// If they have a name, we can attempt to find them that way
			if( player.getName() != null && !player.getName().trim().isEmpty() ){
				return getRealStockMarketPlayer( player.getName() );
			}
			// No name, no UUID, no service.
			return null;
		}
		
		// Lookup by UUID
		StockMarketPlayer stockMarketPlayer = lookupByUUID( player.getUniqueId() );
		if( stockMarketPlayer != null ) return stockMarketPlayer;
		
		// Still not found, try looking them up by name
		stockMarketPlayer = lookupByName( player.getName() );
		if( stockMarketPlayer != null ) return stockMarketPlayer;
		
		return null;
		
	}
	
	
	/**
	 * Compares the known player to the cached data. If there's a difference
	 * we need to handle it.
	 * 
	 * If usernames are different: Update `players` with new name
	 * (@todo track historical?)
	 * 
	 * If UUID is different, log an error.
	 * 
	 * @param player
	 * @param stockMarketPlayer
	 * @return
	 */
	protected static StockMarketPlayer comparePlayerToCache( Player player, StockMarketPlayer stockMarketPlayer ){
		
		// Compare for username differences, update database
		if( !player.getName().equals( stockMarketPlayer.getName() ) ){
			RealStockMarket.log.info("Player name for " +player.getName() + " does not match our cache. Updating to: " +stockMarketPlayer.getName());
			stockMarketPlayer.setName( player.getName() );
			updatePlayer(stockMarketPlayer);
		}
		
		// Compare UUID
		if( !player.getUniqueId().equals( stockMarketPlayer.getUUID() ) ){
			RealStockMarket.log.info("Player UUID for " +player.getName() + " does not match our cache (" +player.getUniqueId()+ "). Updating to: " + stockMarketPlayer.getUUID());
			
			// Update anyway...
			stockMarketPlayer.setUUID( player.getUniqueId() );
			updatePlayer(stockMarketPlayer);
			
		}
		
		return stockMarketPlayer;
		
	}
	
	
	/**
	 * Saves a real player's UUID and current Username to the `players` 
	 * table. At this stage, we're pretty sure the UUID and username do not
	 * already exist.
	 * @param player
	 */
	protected static void addPlayer( Player player ){

		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {

			conn = RealStockMarket.sqlite.getConnection();
            s = conn.prepareStatement( "INSERT INTO players (player,player_uuid) VALUES (?,?)" , Statement.RETURN_GENERATED_KEYS);
            s.setString(1, player.getName() );
            s.setString(2, player.getUniqueId().toString() );
            s.executeUpdate();
            
            rs = s.getGeneratedKeys();
            if (rs.next()) {
            	RealStockMarket.log.info("Saved and loaded player " + player.getName() + " (" + player.getUniqueId() + ") into the cache.");
            	RealStockMarket.stockMarketPlayers.put( player.getUniqueId(), new StockMarketPlayer( rs.getInt(1), player.getUniqueId(), player.getName() ) );
            } else {
                throw new SQLException("Insert statement failed - no generated key obtained.");
            }
		} catch (SQLException e) {
			e.printStackTrace();
        } finally {
        	if(rs != null) try { rs.close(); } catch (SQLException e) {}
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
	}
	
	
	/**
	 * Saves a fake player's name and generated UUID to the `players` 
	 * table. At this stage, we're pretty sure the UUID and username do not
	 * already exist.
	 * 
	 * @param playerName
	 * @return
	 */
	protected static StockMarketPlayer addFakePlayer( String playerName ){
		
		StockMarketPlayer fakePlayer = new StockMarketPlayer( 0, UUID.randomUUID(), playerName );
		
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {

			conn = RealStockMarket.sqlite.getConnection();
            s = conn.prepareStatement( "INSERT INTO players (player,player_uuid) VALUES (?,?)" , Statement.RETURN_GENERATED_KEYS);
            s.setString(1, fakePlayer.getName() );
            s.setString(2, fakePlayer.getUUID().toString() );
            s.executeUpdate();
            
            rs = s.getGeneratedKeys();
            if (rs.next()){
            	fakePlayer.setId( rs.getInt(1) );
            	RealStockMarket.log.info("Saved and loaded fake player " + fakePlayer.getName() + " into the cache.");
            	RealStockMarket.stockMarketPlayers.put( fakePlayer.getUUID(), fakePlayer );
            } else {
                throw new SQLException("Insert statement failed - no generated key obtained.");
            }
		} catch (SQLException e) {
        	e.printStackTrace();
        } finally {
        	if(rs != null) try { rs.close(); } catch (SQLException e) {}
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
		return fakePlayer;
	}
	
	
	/**
	 * Saves a player's UUID to the players table. We cache the current username
	 * as well.
	 */
	protected static void updatePlayer( StockMarketPlayer stockMarketPlayer ){

		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {

			conn = RealStockMarket.sqlite.getConnection();
            s = conn.prepareStatement( "UPDATE players SET player = ?, player_uuid = ? WHERE player_id = ?");
            s.setString(1, stockMarketPlayer.getName() );
            s.setString(2, stockMarketPlayer.getUUID().toString() );
            s.setInt(3, stockMarketPlayer.getId() );
            s.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
        } finally {
        	if(rs != null) try { rs.close(); } catch (SQLException e) {}
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
	}
	
	
	/**
	 * Loads `players` ID for a player into our cache.
	 */
	protected static StockMarketPlayer lookupByName( String playerName ){
		StockMarketPlayer stockMarketPlayer = null;
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {

			conn = RealStockMarket.sqlite.getConnection();
    		s = conn.prepareStatement( "SELECT player_id, player, player_uuid FROM players WHERE player = ?" );
    		s.setString(1, playerName);
    		rs = s.executeQuery();

    		if( rs.next() ){
    			stockMarketPlayer = new StockMarketPlayer( rs.getInt(1), UUID.fromString(rs.getString(3)), rs.getString(2) );
    		}
		} catch (SQLException e) {
			e.printStackTrace();
        } finally {
        	if(rs != null) try { rs.close(); } catch (SQLException e) {}
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
		return stockMarketPlayer;
	}
	
	
	/**
	 * Loads `players` ID for a player into our cache.
	 */
	protected static StockMarketPlayer lookupByUUID( UUID uuid ){
		StockMarketPlayer stockMarketPlayer = null;
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {

			conn = RealStockMarket.sqlite.getConnection();
    		s = conn.prepareStatement( "SELECT player_id, player, player_uuid FROM players WHERE player_uuid = ?" );
    		s.setString(1, uuid.toString());
    		rs = s.executeQuery();

    		if( rs.next() ){
    			stockMarketPlayer = new StockMarketPlayer( rs.getInt(1), UUID.fromString(rs.getString(3)), rs.getString(2) );
    		}
		} catch (SQLException e) {
			e.printStackTrace();
        } finally {
        	if(rs != null) try { rs.close(); } catch (SQLException e) {}
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
		return stockMarketPlayer;
	}
	
	
	/**
	 * Build-load all online players into cache
	 */
	public static void cacheOnlinePlayerPrimaryKeys(){
		
		String[] playerNames;
		playerNames = new String[ Bukkit.getServer().getOnlinePlayers().length ];
		int i = 0;
		for( Player pl : Bukkit.getServer().getOnlinePlayers() ){
			playerNames[i] = pl.getName();
			i++;
		}
		
		if( playerNames.length == 0 ) return;
		
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {

			conn = RealStockMarket.sqlite.getConnection();
    		s = conn.prepareStatement( "SELECT player_id, player, player_uuid FROM players WHERE player IN ('?')" );
    		s.setString(1, TypeUtils.join(playerNames, "','"));
    		rs = s.executeQuery();

    		while( rs.next() ){
    			StockMarketPlayer stockMarketPlayer = new StockMarketPlayer( rs.getInt(1), UUID.fromString(rs.getString(3)), rs.getString(2) );
    			RealStockMarket.log.info("Loaded player " + rs.getString(2) + ", id: " + rs.getInt(1) + " into the cache.");
    			RealStockMarket.stockMarketPlayers.put( UUID.fromString(rs.getString(2)), stockMarketPlayer );
    		}
		} catch (SQLException e) {
			e.printStackTrace();
        } finally {
        	if(rs != null) try { rs.close(); } catch (SQLException e) {}
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
	}
}