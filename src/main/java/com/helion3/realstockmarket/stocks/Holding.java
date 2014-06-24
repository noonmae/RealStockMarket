package com.helion3.realstockmarket.stocks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Holding {
	
	private final int holding_id;
	private final String symbol;
	private final double symbolPrice;
	private int quantity;
	private double holdingTotal;
	private Date purchaseDate;
	
	
	/**
	 * 
	 * @param player_id
	 * @param symbol
	 * @param symbolPrice
	 * @param quantity
	 * @param holdingTotal
	 */
	public Holding( int holding_id, int player_id, String symbol, double symbolPrice, int quantity, double holdingTotal, String purchaseDate ) {
		this.holding_id = holding_id;
		this.symbol = symbol.toUpperCase();
		this.symbolPrice = symbolPrice;
		this.quantity = quantity;
		this.holdingTotal = holdingTotal;
		try {
            this.purchaseDate = new SimpleDateFormat("yyyy-MM-dd").parse( purchaseDate);
        } catch ( ParseException e ) {
            e.printStackTrace();
        }
	}
	
	
	/**
	 * 
	 * @return
	 */
	public int getId(){
		return holding_id;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public int getQuantity(){
		return quantity;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public void setQuantity( int quantity ){
		this.quantity = quantity;
		holdingTotal = (quantity * symbolPrice);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getSymbol(){
		return symbol;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getPrice(){
		return symbolPrice;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getTotal(){
		return holdingTotal;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public Date getPurchaseDate(){
	    return purchaseDate;
	}
}