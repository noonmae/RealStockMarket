package com.helion3.realstockmarket;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

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
	 * @return
	 * @throws Exception 
	 */
	public HashMap<String,Stock> fetchLatestPrices() throws Exception{
		
		if( symbols == null || symbols.length == 0 ){
			throw new Exception("No stock symbols have been specified.");
		}
		
		String requestUrl = apiUrl + "?s=" + join(symbols,",") + "&f=" + apiCodes;
		
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
	
	
	/**
	 * Method to join array elements of type string
	 * @author Hendrik Will, imwill.com, bug fixes by viveleroi
	 * @param inputArray Array which contains strings
	 * @param glueString String between each array element
	 * @return String containing all array elements separated by glue string
	 */
	private static String join(String[] inputArray, String glueString) {
		String output = "";
		if (inputArray.length > 0) {
			StringBuilder sb = new StringBuilder();
			if(!inputArray[0].isEmpty()){
				sb.append(inputArray[0]);
			}
			for (int i=1; i<inputArray.length; i++) {
				if(!inputArray[i].isEmpty()){
					if(sb.length() > 0){
						sb.append(glueString);
					}
					sb.append(inputArray[i]);
				}
			}
			output = sb.toString();
		}
		return output;
	}
}