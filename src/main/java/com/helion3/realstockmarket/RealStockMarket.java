package com.helion3.realstockmarket;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class RealStockMarket extends JavaPlugin {
	
	// public constants
	public static final String pluginName = "RealStockMarket";
	public static Logger log = Logger.getLogger("Minecraft");
	public static final Messenger messenger = new Messenger(pluginName);
	public static Economy econ;
	public static final SQLite sqlite = new SQLite();
	public static HashMap<UUID,StockMarketPlayer> stockMarketPlayers = new HashMap<UUID,StockMarketPlayer>();
	
	private FileConfiguration config;
	
	
	/**
	 * 
	 */
	public void onEnable(){
		
		log.info(pluginName + " starting...");
		
		config = setupConfig();
		
		// Start sqlite
		sqlite.createTables();
		
//		try {
//		    Metrics metrics = new Metrics(this);
//		    metrics.start();
//		} catch (IOException e) {
//		    log("MCStats submission failed.");
//		}
		
		// Hook into vault
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
        	econ = rsp.getProvider();
        }
        
        // Register events
        getServer().getPluginManager().registerEvents(new PlayerEventListeners(), this);
        
        // Cache players for anyone online
        PlayerIdentification.cacheOnlinePlayerPrimaryKeys();

	}
	
	
	/**
	 * 
	 */
	private FileConfiguration setupConfig(){
		
		FileConfiguration config = getConfig();
		
		// Copy defaults
		config.options().copyDefaults(true);
		
		// save the defaults/config
		saveConfig();
		
		return config;
		
	}
	
	
	/**
	 * 
	 */
    public boolean onCommand( final CommandSender sender, Command cmd, String label, final String[] args){
    	
    	// Help/credits
    	if( args.length < 1 || args[0].equals("?") ){
    		sender.sendMessage( messenger.playerMsg("Help",true) );
    		sender.sendMessage( messenger.playerSubduedMsg("By viveleroi") );
    		sender.sendMessage( messenger.playerSubduedMsg("sm view (stock)" +ChatColor.WHITE+ " - Latest prices: /sm view AAPL,GOOG") );
    		sender.sendMessage( messenger.playerSubduedMsg("sm buy (stock) (quant)" +ChatColor.WHITE+ " - Buy stocks: /sm buy AAPL 50") );
    		sender.sendMessage( messenger.playerSubduedMsg("sm sell (stock) (quant)" +ChatColor.WHITE+ " - Buy stocks: /sm buy AAPL 10") );
    		return true;
    	}
    	
    	// List available stocks
    	if( args[0].equals("list") ){
    		sender.sendMessage( messenger.playerMsg("There are LOTS. http://www.reuters.com/finance/stocks",true) );
    		return true;
    	}
    	
    	// View current stock prices
    	if( args[0].equals("view") ){
    		
    		if( args.length != 2 ){
        		sender.sendMessage( messenger.playerError("Invalid command. Check /sm ? for help.") );
        		return true;
        	}
    		
    		// Run lookup in an async thread
    		getServer().getScheduler().runTaskAsynchronously(this, new Runnable(){
    			public void run(){
    				StockBroker.viewInfoForStock(sender, args[1].split(",") );
    			}
    		});
    		
    		return true;
    	}
    	
    	// View your portfolio
    	if( args[0].equals("mine") || args[0].equals("portfolio") ){
    		
    		String playerName = sender.getName();
    		if( args.length > 1 ){
    			playerName = args[1];
    		}
    		
    		StockBroker.viewPlayerPortfolio(sender, playerName);
    		
    		return true;
    		
    	}
    	
    	// Buy stocks
    	if( args[0].equals("buy") ){
    		
    		if( args.length != 3 ){
        		sender.sendMessage( messenger.playerError("Invalid command. Check /sm ? for help.") );
        		return true;
        	}
    		
    		if( !(sender instanceof Player) ){
    			sender.sendMessage( messenger.playerError("Only in-game players may buy stocks.") );
        		return true;
    		}
    		
    		if( !isNumeric(args[2]) ){
    			sender.sendMessage( messenger.playerError("Quantity must be a number. Check /sm ? for help.") );
        		return true;
    		}
    		
    		final int quantity = Integer.parseInt(args[2]);
    		if( quantity <= 0 ){
    			sender.sendMessage( messenger.playerError("Quantity must be higher than one. Check /sm ? for help.") );
        		return true;
    		}
    		
    		// Run lookup in an async thread
    		getServer().getScheduler().runTaskAsynchronously(this, new Runnable(){
    			public void run(){
    				StockBroker.buyStock( (Player)sender , args[1].split(","), quantity );
    			}
    		});
    		
    		return true;
    	}
    	
    	// Sell stocks
    	if( args[0].equals("sell") ){
    		
    		if( args.length != 3 ){
        		sender.sendMessage( messenger.playerError("Invalid command. Check /sm ? for help.") );
        		return true;
        	}
    		
    		if( !(sender instanceof Player) ){
    			sender.sendMessage( messenger.playerError("Only in-game players may sell stocks.") );
        		return true;
    		}
    		
    		if( !isNumeric(args[2]) ){
    			sender.sendMessage( messenger.playerError("Quantity must be a number. Check /sm ? for help.") );
        		return true;
    		}
    		
    		final int quantity = Integer.parseInt(args[2]);
    		if( quantity <= 0 ){
    			sender.sendMessage( messenger.playerError("Quantity must be higher than one. Check /sm ? for help.") );
        		return true;
    		}
    		
    		// Run lookup in an async thread
    		getServer().getScheduler().runTaskAsynchronously(this, new Runnable(){
    			public void run(){
    				StockBroker.sellStock( (Player)sender , args[1].split(","), quantity );
    			}
    		});
    		
    		return true;
    	}
    	
    	// List available stocks
		sender.sendMessage( messenger.playerError("Invalid command. Check /sm ? for help.",true) );
		return true;

    }
    
    
    /**
     * Is the string numeric
     * @param str
     * @return
     */
	public static boolean isNumeric( String str ){  
		try{  
			Integer.parseInt(str);
		}
		catch(NumberFormatException nfe){  
			return false;
		}
		return true;
	}
    
    
    /**
	 * 
	 */
	public void onDisnable(){
		log.info(pluginName + " shutting down...");
	}
}