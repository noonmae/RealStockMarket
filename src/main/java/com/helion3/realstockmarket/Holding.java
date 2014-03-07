package com.helion3.realstockmarket;

public class Holding {
	
//	private final int player_id;
	private final String symbol;
	private final double symbolPrice;
	private final int quantity;
	private final double holdingTotal;
	
	
	/**
	 * 
	 * @param player_id
	 * @param symbol
	 * @param symbolPrice
	 * @param quantity
	 * @param holdingTotal
	 */
	public Holding(int player_id, String symbol, double symbolPrice, int quantity, double holdingTotal) {
//		this.player_id = player_id;
		this.symbol = symbol;
		this.symbolPrice = symbolPrice;
		this.quantity = quantity;
		this.holdingTotal = holdingTotal;
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
}