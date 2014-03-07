package com.helion3.realstockmarket;

public class TypeUtils {
	
	
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
	 * Method to join array elements of type string
	 * @author Hendrik Will, imwill.com, bug fixes by viveleroi
	 * @param inputArray Array which contains strings
	 * @param glueString String between each array element
	 * @return String containing all array elements separated by glue string
	 */
	public static String join(String[] inputArray, String glueString) {
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