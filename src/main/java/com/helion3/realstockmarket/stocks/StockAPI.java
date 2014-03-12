package com.helion3.realstockmarket.stocks;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

import com.helion3.realstockmarket.TypeUtils;

import au.com.bytecode.opencsv.CSVReader;

public class StockAPI {
	
	private final String apiUrl = "http://download.finance.yahoo.com/d/quotes.csv";
	private final String apiCodes = "nsl1op";
	
	private String[] symbols;
	
	
	/**
	 * 
	 * @param symbols
	 */
	public StockAPI( String...symbols ){
		this.symbols = symbols;
	}
	
	
	/**
	 * 
	 * @param symbols
	 */
	public StockAPI( List<String> symbols ){
		String[] _tempSymbols = new String[ symbols.size() ];
		for( int s = 0; s < symbols.size(); s++ ){
			_tempSymbols[s] = symbols.get(s);
		}
		this.symbols = _tempSymbols;
	}
	
	
	/**
	 * 
	 * @return
	 * @throws Exception 
	 */
	public HashMap<String,Stock> fetchLatestPrices() throws Exception{
		
		if( symbols == null || symbols.length == 0 ){
			throw new Exception("No stock symbols have been specified.");
		}
		
		String requestUrl = apiUrl + "?s=" + TypeUtils.join(symbols,",") + "&f=" + apiCodes;
		
		HashMap<String,Stock> stocks = new HashMap<String,Stock>();
		
		CSVReader reader = null;
		try {
			
			URL url = new URL(requestUrl);
			URLConnection connection = url.openConnection();
			InputStream inStream = connection.getInputStream(); 
			
			reader = new CSVReader(new InputStreamReader(inStream),',');
	        String[] nextLine;
	        while ((nextLine = reader.readNext()) != null){
	
	        	String company = nextLine[0];
	        	String symbol = nextLine[1];
	        	Double lastPrice = Double.parseDouble(nextLine[2]);
	        	
	        	stocks.put(symbol,  new Stock(symbol,company,lastPrice) );
	        	
	        }
		}
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

		return stocks;
		
	}
}