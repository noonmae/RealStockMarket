package com.helion3.realstockmarket;

import org.bukkit.ChatColor;

public class Messenger {
	
	/**
	 * 
	 */
	protected String plugin_name;
	
	
	/**
	 * 
	 * @param plugin_name
	 */
	public Messenger( String plugin_name ){
		this.plugin_name = plugin_name;
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	private String getHeader(){
		return ChatColor.GOLD + plugin_name+" // ";
	}
	

	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String playerMsg( String msg ){
		return playerMsg(msg,false);
	}
	public String playerMsg( String msg, boolean isHeader ){
		if(msg != null){
			return (isHeader?getHeader():"") + ChatColor.WHITE + msg;
		}
		return "";
	}

	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String playerSubduedMsg( String msg ){
		return playerSubduedMsg(msg,false);
	}
	public String playerSubduedMsg( String msg, boolean isHeader ){
		if(msg != null){
			return (isHeader?getHeader():"") + ChatColor.GRAY + msg;
		}
		return "";
	}

	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String playerError( String msg ){
		return playerError(msg,false);
	}
	public String playerError( String msg, boolean isHeader ){
		if(msg != null){
			return (isHeader?getHeader():"") + ChatColor.RED + msg;
		}
		return "";
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String playerSuccess( String msg ){
		return playerSuccess(msg,false);
	}
	public String playerSuccess( String msg, boolean isHeader){
		if(msg != null){
			return (isHeader?getHeader():"") + ChatColor.GREEN + msg;
		}
		return "";
	}
}