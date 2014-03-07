package com.helion3.realstockmarket;

public class Stock {
	
	private final String symbol;
	private final String companyName;
	private final Double lastPrice;
	
	
	/**
	 * 
	 * @param symbol
	 * @param companyName
	 * @param lastPrice
	 */
	public Stock( String symbol, String companyName, Double lastPrice ){
		this.symbol = symbol.toUpperCase();
		this.companyName = companyName;
		this.lastPrice = lastPrice;
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
	public String getCompanyName(){
		return companyName;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getLatestPrice(){
		return lastPrice;
	}
}