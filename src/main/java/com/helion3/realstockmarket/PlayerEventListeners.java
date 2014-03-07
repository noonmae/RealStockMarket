package com.helion3.realstockmarket;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListeners implements Listener {

	
	/**
	 * Load all player data into cache for faster db queries
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent event){
		PlayerIdentification.cacheRealStockMarketPlayer( event.getPlayer() );
	}
	
	
	/**
	 * Remove player data from cache
	 * @param event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(final PlayerQuitEvent event){
		RealStockMarket.stockMarketPlayers.remove( event.getPlayer().getName() );
	}
}