package com.helion3.realstockmarket;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StockBroker {
	
	
	/**
	 * Returns the symbol, company name, and latest price for all provided
	 * stock symbols.
	 * 
	 * @param sender
	 * @param symbols
	 */
	public static void viewInfoForStock( CommandSender sender, String[] symbols ){
		
		StockAPI stockAPI = new StockAPI( symbols );
		
		try {
			
			HashMap<String,Stock> stocks = stockAPI.fetchLatestPrices();
			
			if( stocks.isEmpty() ){
				sender.sendMessage( RealStockMarket.messenger.playerError("No valid stocks found.") );
				return;
			}
			
			// Results found, show 'em
			sender.sendMessage( RealStockMarket.messenger.playerMsg("Stock Prices:",true) );
			for( Entry<String,Stock> result : stocks.entrySet() ){
				Stock stock = result.getValue();
				sender.sendMessage( RealStockMarket.messenger.playerMsg( stock.getLatestPrice() + " - " + stock.getSymbol() + " - " + stock.getCompanyName() ) );
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Purchase a set quantity of stocks
	 * 
	 * @param player
	 * @param symbols
	 * @param quantity
	 */
	public static void buyStock( Player player, String[] symbols, int quantity ){
		
		StockAPI stockAPI = new StockAPI( symbols );
		
		try {
			
			HashMap<String,Stock> stocks = stockAPI.fetchLatestPrices();
			
			if( stocks.isEmpty() ){
				player.sendMessage( RealStockMarket.messenger.playerError("No valid stocks found.") );
				return;
			}
			
			// Results found, show 'em
			player.sendMessage( RealStockMarket.messenger.playerMsg("Stock Purchase Report",true) );
			for( Entry<String,Stock> result : stocks.entrySet() ){
				Stock stock = result.getValue();
				
				Double currentBalance = RealStockMarket.econ.getBalance( player.getName() );
				Double totalPrice = (stock.getLatestPrice() * quantity);
				
				if( currentBalance < totalPrice ){
					player.sendMessage( RealStockMarket.messenger.playerError("You can't afford " +quantity+ " shares of " + stock.getSymbol() + " totaling $" + formatDouble(totalPrice) ) );
					continue;
				}
				
				RealStockMarket.econ.withdrawPlayer( player.getName(), totalPrice);
				player.sendMessage( RealStockMarket.messenger.playerSuccess("Bought " +quantity+ " shares of " + stock.getSymbol() + " totaling $" + formatDouble(totalPrice) ) );
				
				// @todo log to a db

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Purchase a set quantity of stocks
	 * 
	 * @param player
	 * @param symbols
	 * @param quantity
	 */
	public static void sellStock( Player player, String[] symbols, int quantity ){
		
		StockAPI stockAPI = new StockAPI( symbols );
		
		try {
			
			HashMap<String,Stock> stocks = stockAPI.fetchLatestPrices();
			
			if( stocks.isEmpty() ){
				player.sendMessage( RealStockMarket.messenger.playerError("No valid stocks found.") );
				return;
			}
			
			// Results found, show 'em
			player.sendMessage( RealStockMarket.messenger.playerMsg("Stock Sales Report",true) );
			for( Entry<String,Stock> result : stocks.entrySet() ){
				Stock stock = result.getValue();
				
				// @todo verify the user has enough to sell
				
				Double totalPrice = (stock.getLatestPrice() * quantity);
				
				RealStockMarket.econ.depositPlayer( player.getName(), totalPrice);
				player.sendMessage( RealStockMarket.messenger.playerSuccess("Sold " +quantity+ " shares of " + stock.getSymbol() + " totaling $" + formatDouble(totalPrice) ) );
				
				// @todo log to a db

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
     * 
     * @param val
     * @return
     */
	private static float formatDouble( double val ){
    	return Float.parseFloat(new DecimalFormat("#.##").format(val));
    }
}