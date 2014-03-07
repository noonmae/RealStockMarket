package com.helion3.realstockmarket;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
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
			sender.sendMessage( RealStockMarket.messenger.playerError("The was an error fetching the stock prices: " + e.getMessage()) );
		}
	}
	
	
	/**
	 * Returns the symbol, company name, and latest price for all provided
	 * stock symbols.
	 * 
	 * @param sender
	 * @param symbols
	 */
	public static void viewPlayerPortfolio( CommandSender sender, String playerName ){
		
		sender.sendMessage( RealStockMarket.messenger.playerMsg("Player Portfolio Report",true) );
		
		List<Holding> holdings = RealStockMarket.sqlite.getPlayerHoldings( playerName );
		
		if( holdings.isEmpty() ){
			sender.sendMessage( RealStockMarket.messenger.playerError("No current holdings found.") );
			return;
		}
		
		// Build a list of all symbols so we can call the API once
		List<String> symbols = new ArrayList<String>();
		for( Holding h : holdings ){
			symbols.add( h.getSymbol() );
		}
		
		StockAPI stockAPI = new StockAPI( symbols );
		
		try {
			
			HashMap<String,Stock> stocks = stockAPI.fetchLatestPrices();
			
			if( stocks.isEmpty() ){
				sender.sendMessage( RealStockMarket.messenger.playerError("Couldn't find live stock data for holdings...") );
				return;
			}
			
			double totalPortfolioValue = 0;
			
			// Iterate all stocks by group
			for( Entry<String,Stock> entry : stocks.entrySet() ){
				Stock stock = entry.getValue();
				
				// Stock title bar
				String msg = ChatColor.AQUA + stock.getSymbol();
				msg += ChatColor.WHITE + " " + stock.getCompanyName();
				msg += ChatColor.GRAY + " Currently: ";
				msg += ChatColor.YELLOW  + formatCurrency(stock.getLatestPrice());
				sender.sendMessage( RealStockMarket.messenger.playerMsg( msg ) );
				
				double sharesDiffTotalForSymbol = 0;
				int holdingsCountForSymbol = 0; // if only 1, we can skip a totals message
				
				// Show all holdings reports
				for( Holding holding : holdings ){
					
					// We group by symbols, so only show this symbol
					if( !holding.getSymbol().equals(stock.getSymbol()) ) continue;
					
					holdingsCountForSymbol++;
					
					// Holdings Report (Bought shares and totals)
					// @todo show date?
					msg = ChatColor.GRAY + "- Own:";
					msg += ChatColor.WHITE + " " + holding.getQuantity();
					msg += ChatColor.GRAY + " at "+ChatColor.YELLOW + formatCurrency(holding.getPrice());
					msg += ChatColor.GRAY + " Cost: ("+formatCurrency(holding.getTotal())+")";
					
					double difference = stock.getLatestPrice() - holding.getPrice();
					
//					if( difference > 0 ){
//						msg += " "+ChatColor.GREEN + "+"+formatDouble(difference);
//					} else {
//						msg += " "+ChatColor.RED + ""+formatDouble(difference);
//					}

					// Total earnings
					double totalDifference = difference * holding.getQuantity();
					if( difference > 0 ){
						msg += ChatColor.GRAY + " Total: "+ChatColor.GREEN + "+"+formatCurrency(totalDifference);
					} else {
						msg += ChatColor.GRAY + " Total: "+ChatColor.RED + ""+formatCurrency(totalDifference);
					}
					
					sharesDiffTotalForSymbol += totalDifference;
					
					sender.sendMessage( RealStockMarket.messenger.playerMsg( msg ) );
				}
				
				// Stock totals bar
				if( holdingsCountForSymbol > 1 ){
					msg = ChatColor.GRAY + "Net Share Value for ";
					msg += ChatColor.AQUA + stock.getSymbol();
					msg += ChatColor.GRAY + ": ";
					if( sharesDiffTotalForSymbol > 0 ){
						msg += ChatColor.GREEN + "+"+formatCurrency(sharesDiffTotalForSymbol);
					} else {
						msg += ChatColor.RED + ""+formatCurrency(sharesDiffTotalForSymbol);
					}
					sender.sendMessage( RealStockMarket.messenger.playerMsg( msg ) );
				}
				
				totalPortfolioValue += sharesDiffTotalForSymbol;
				
			}
			
			// Portfolio totals bar
			String msg = ChatColor.GOLD + "Portfolio Net Value: ";
			if( totalPortfolioValue > 0 ){
				msg += ChatColor.GREEN + "+";
			} else {
				msg += ChatColor.RED;
			}
			msg += formatCurrency(totalPortfolioValue);
			sender.sendMessage( RealStockMarket.messenger.playerMsg( msg ) );
			
		} catch (Exception e) {
			e.printStackTrace();
			sender.sendMessage( RealStockMarket.messenger.playerError("The was an error fetching the stock prices: " + e.getMessage()) );
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
					player.sendMessage( RealStockMarket.messenger.playerError("You can't afford " +quantity+ " shares of " + stock.getSymbol() + " totaling " + formatCurrency(totalPrice) ) );
					continue;
				}
				
				// Deduct money
				RealStockMarket.econ.withdrawPlayer( player.getName(), totalPrice);
				player.sendMessage( RealStockMarket.messenger.playerSuccess("Bought " +quantity+ " shares of " + stock.getSymbol() + " totaling " + formatCurrency(totalPrice) ) );
				
				// Log to the db!
				RealStockMarket.sqlite.logStockPurchase(player, stock, quantity);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			player.sendMessage( RealStockMarket.messenger.playerError("The was an error fetching the stock prices: " + e.getMessage()) );
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
			
			// Grab latest prices for the stocks
			HashMap<String,Stock> stocks = stockAPI.fetchLatestPrices();
			
			if( stocks.isEmpty() ){
				player.sendMessage( RealStockMarket.messenger.playerError("No valid stocks found.") );
				return;
			}
			
			// Results found, process each stock
			player.sendMessage( RealStockMarket.messenger.playerMsg("Stock Sales Report",true) );
			for( Entry<String,Stock> result : stocks.entrySet() ){
				Stock stock = result.getValue();
				
				// Find player's current holdings for this stock
				List<Holding> holdings = RealStockMarket.sqlite.getPlayerHoldingsForSymbol(player, stock.getSymbol());
				
				if( holdings.isEmpty() ){
					player.sendMessage( RealStockMarket.messenger.playerError("No current holdings found for " + stock.getSymbol()) );
					continue;
				}
				
				int quantityRemainingToSell = quantity;
	    		
	    		// Loop each holding and sell off the quantity
	    		for( Holding holding : holdings ){
	    			
	    			int soldFromThisHolding = 0;
	    			// if selling more than this holding allows
	    			if( quantityRemainingToSell > holding.getQuantity() ){
	    				soldFromThisHolding = holding.getQuantity();
	    			} else {
	    				soldFromThisHolding = quantityRemainingToSell;
	    			}
	    			
	    			// Update holding
	    			holding.setQuantity( holding.getQuantity() - soldFromThisHolding );
	    			
	    			// Save changes to db
	    			if( holding.getQuantity() <= 0 ){
	    				RealStockMarket.sqlite.deleteHolding(holding);
	    			} else {
	    				RealStockMarket.sqlite.updateHolding(holding);
	    			}
	    			
	    			quantityRemainingToSell -= soldFromThisHolding;
	    			
	    			// Log sale, inform users
	    			if( soldFromThisHolding > 0 ){
	    				
	    				Double totalPrice = (stock.getLatestPrice() * soldFromThisHolding);
	    				RealStockMarket.econ.depositPlayer( player.getName(), totalPrice);
	    				
	    				String msg = ChatColor.GRAY + "Sold ";
	    				msg += ChatColor.WHITE + ""+soldFromThisHolding;
	    				msg += " " + ChatColor.AQUA + stock.getSymbol();
	    				msg += ChatColor.GRAY + " for " + ChatColor.YELLOW + formatCurrency(totalPrice);
	    				msg += ChatColor.GRAY + " (You paid: "+formatCurrency(holding.getPrice()*soldFromThisHolding)+")";
	    				msg += ChatColor.GRAY + " Net: ";
	    				
	    				double difference = (stock.getLatestPrice() - holding.getPrice())*soldFromThisHolding;
	    				if( difference > 0 ){
	    					msg += ChatColor.GREEN+"+";
	    				} else {
	    					msg += ChatColor.RED;
	    				}
	    				msg += formatCurrency(difference);
	    				
	    				player.sendMessage( RealStockMarket.messenger.playerMsg( msg ) );
	    				
	    				RealStockMarket.sqlite.logStockSale(player, stock, soldFromThisHolding);
	    				
	    			}
	    				    		
	    			// If nothing left to sell
	    			if( quantityRemainingToSell <= 0 ){
	    				break;
	    			}
	    		}
			}
		} catch (Exception e) {
			e.printStackTrace();
			player.sendMessage( RealStockMarket.messenger.playerError("The was an error fetching the stock prices: " + e.getMessage()) );
		}
	}
	
	
	/**
	 * 
	 * @param number
	 * @return
	 */
	private static String formatCurrency(double number) {
		DecimalFormat moneyFormat = new DecimalFormat("$#,###.00");
		return moneyFormat.format(number);
	}
}