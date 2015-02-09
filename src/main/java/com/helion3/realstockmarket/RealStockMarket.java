package com.helion3.realstockmarket;

import java.io.IOException;
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

import com.helion3.realstockmarket.stocks.StockBroker;
import com.helion3.realstockmarket.stocks.StockMarketPlayer;

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
		
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		    log.info("MCStats submission failed.");
		}
		
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
    	if( args.length < 1 || args[0].equals("?") || args[0].equalsIgnoreCase("help") ){
    		sender.sendMessage( messenger.playerMsg("Help",true) );
    		sender.sendMessage( messenger.playerSubduedMsg("By viveleroi") );
    		sender.sendMessage( messenger.playerSubduedMsg("sm list" +ChatColor.WHITE+ " - Where to find symbols") );
    		sender.sendMessage( messenger.playerSubduedMsg("sm view (stock)" +ChatColor.WHITE+ " - Latest prices: /sm view AAPL,GOOG") );
    		sender.sendMessage( messenger.playerSubduedMsg("sm buy (stock) (quant)" +ChatColor.WHITE+ " - 주식구매: /sm buy AAPL 50") );
    		sender.sendMessage( messenger.playerSubduedMsg("sm sell (stock) (quant)" +ChatColor.WHITE+ " - 주식판매: /sm sell AAPL 10") );
    		sender.sendMessage( messenger.playerSubduedMsg("sm (mine|portfolio)" +ChatColor.WHITE+ " - 본인의 주식현황") );
    		sender.sendMessage( messenger.playerSubduedMsg("sm portfolio (player)" +ChatColor.WHITE+ " - 설정한 사람의 주식현황") );
    		sender.sendMessage( messenger.playerSubduedMsg("sm (?|help)" +ChatColor.WHITE+ " - Help. You are here.") );
    		sender.sendMessage( messenger.playerMsg("http://www.investopedia.com/university/stocks/  에서 주식을 확인해주시길 바랍니다.") );
    		return true;
    	}
    	
    	// List available stocks
    	if( args[0].equals("list") ){
    		sender.sendMessage( messenger.playerMsg("주식 목록",true) );
    		sender.sendMessage( messenger.playerSubduedMsg("You may use *any* REAL US stock symbols. Check your favorite stock website or choose from here:") );
    		sender.sendMessage( messenger.playerMsg("가능한 주식 목록: "+ChatColor.AQUA+"http://on.mktw.net/1gYZhpp"+" 에서 구매하고 싶은 주식의 Symbol 이름을 적어주시면 됩니다.") );
    		// http://on.mktw.net/1gYZhpp
    		return true;
    	}
    	
    	// View current stock prices
    	if( args[0].equals("view") ){
    		
    		if( !sender.hasPermission("realstockmarket.view") ){
    			sender.sendMessage( messenger.playerError("당신은 권한이 없습니다.") );
        		return true;
    		}
    		
    		if( args.length != 2 ){
        		sender.sendMessage( messenger.playerError("Please supply a stock symbol to view. Check /sm ? for help.") );
        		return true;
        	}
    		
    		// Run lookup in an async thread
    		sender.sendMessage( messenger.playerSubduedMsg("찾고 있습니다..") );
    		getServer().getScheduler().runTaskAsynchronously(this, new Runnable(){
    			public void run(){
    				StockBroker.viewInfoForStock(sender, args[1].split(",") );
    			}
    		});
    		
    		return true;
    	}
    	
    	// View your portfolio
    	if( args[0].equals("mine") || args[0].equals("portfolio") ){
    		
    		if( args[0].equals("portfolio") && !sender.hasPermission("realstockmarket.portfolio.others") ){
    			sender.sendMessage( messenger.playerError("당신은 권한이 없습니다.") );
        		return true;
    		}
    		
    		else if( args[0].equals("mine") && !sender.hasPermission("realstockmarket.buy") ){
    			sender.sendMessage( messenger.playerError("당신은 권한이 없습니다.") );
        		return true;
    		}
    		
    		if( args[0].equals("mine") && args.length != 1 ){
        		sender.sendMessage( messenger.playerError("Too many arguments. Check /sm ? for correct usage.") );
        		return true;
        	}
    		
    		String tmpPlayerName = sender.getName();
    		if( args.length > 1 ){
    		    tmpPlayerName = args[1];
    		}
    		
    		final String playerName = tmpPlayerName;
    		getServer().getScheduler().runTaskAsynchronously(this, new Runnable(){
                public void run(){
                   StockBroker.viewPlayerPortfolio(sender, playerName);
                }
    		});
    		
    		return true;
    		
    	}
    	
    	// Buy stocks
    	if( args[0].equals("buy") ){
    		
    		if( !sender.hasPermission("realstockmarket.buy") ){
    			sender.sendMessage( messenger.playerError("당신은 권한이 없습니다.") );
        		return true;
    		}
    		
    		if( args.length < 3 ) {
    			sender.sendMessage( messenger.playerError("Stock, quantity, or both are missing. Check /sm ? for correct usage.") );
    			return true;
    		}
    		
    		if( args.length > 3 ){
        		sender.sendMessage( messenger.playerError("Too many arguments. Check /sm ? for correct usage.") );
        		return true;
        	}
    		
    		if( !(sender instanceof Player) ){
    			sender.sendMessage( messenger.playerError("Only in-game players may buy stocks.") );
        		return true;
    		}
    		
    		if( !TypeUtils.isNumeric(args[2]) ){
    			sender.sendMessage( messenger.playerError("Quantity must be a number. Check /sm ? for help.") );
        		return true;
    		}
    		
    		final int quantity = Integer.parseInt(args[2]);
    		if( quantity <= 0 ){
    			sender.sendMessage( messenger.playerError("Quantity must be one or higher. Check /sm ? for help.") );
        		return true;
    		}
    		
    		// Run lookup in an async thread
    		sender.sendMessage( messenger.playerSubduedMsg("Talking to your stock broker...") );
    		getServer().getScheduler().runTaskAsynchronously(this, new Runnable(){
    			public void run(){
    				StockBroker.buyStock( (Player)sender , args[1].split(","), quantity );
    			}
    		});
    		
    		return true;
    	}
    	
    	// Sell stocks
    	if( args[0].equals("sell") ){
    		
    		if( !sender.hasPermission("realstockmarket.buy") ){
    			sender.sendMessage( messenger.playerError("당신은 권한이 없습니다.") );
        		return true;
    		}
    		
    		if( args.length < 3 ){
        		sender.sendMessage( messenger.playerError("Stock, quantity, or both are missing. Check /sm ? for correct usage.") );
        		return true;
        	}
        	
        	if ( args.length > 3 ) {
        		sender.sendMessage ( messenger.playerError("Too many arguments. Check /sm ? for correct usage."));
        		return true;
        	}
    		
    		if( !(sender instanceof Player) ){
    			sender.sendMessage( messenger.playerError("Only in-game players may sell stocks.") );
        		return true;
    		}
    		
    		if( !TypeUtils.isNumeric(args[2]) ){
    			sender.sendMessage( messenger.playerError("Quantity must be a number. Check /sm ? for help.") );
        		return true;
    		}
    		
    		final int quantity = Integer.parseInt(args[2]);
    		if( quantity <= 0 ){
    			sender.sendMessage( messenger.playerError("Quantity must be one or higher. Check /sm ? for help.") );
        		return true;
    		}
    		
    		// Run lookup in an async thread
    		sender.sendMessage( messenger.playerSubduedMsg("Talking to your stock broker...") );
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
	 * 
	 */
	public void onDisable(){
		log.info(pluginName + " shutting down...");
	}
}
