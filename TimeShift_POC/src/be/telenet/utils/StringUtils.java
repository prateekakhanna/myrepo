package be.telenet.utils;

import java.util.ArrayList;
import java.util.StringTokenizer;

import be.telenet.config.TimeShiftConstants;

public class StringUtils {
	
	public static int countOccurenceInString(String line, String word) {
		if (line != null && word != null)
		{
			int count=0;
			int idx=-1;
			for(String tmpLine=line; (idx = tmpLine.indexOf(word)) != -1; tmpLine = tmpLine.substring(idx+1), count++);
			
			return count;
		}
		return -1;
	}
	
	private static ArrayList<String> getStringToList(String line) {
		StringTokenizer st = new StringTokenizer(line, TimeShiftConstants.XPATH_SEPARATOR);
		ArrayList<String> list = new ArrayList<String>(st.countTokens());
		while(st.hasMoreElements()) {
			list.add(st.nextToken());
		}
		
		return list;
	}

	public static String getSubstringBySeparator(String line, int index){
					
		if(line.length() > 0 && !line.contains(TimeShiftConstants.XPATH_SEPARATOR))
			return line;
		
		ArrayList<String> list = getStringToList(line);
		
		if (index > list.size())
			return null;
					
		return list.get(index);
	}
	
	public static String getRemainingString(String line) {
		String xmlParse = getSubstringBySeparator(line, 0);
		
		if (xmlParse != null) {
			if (line.equals(xmlParse) && !line.contains(TimeShiftConstants.XPATH_SEPARATOR))
				return null;
			
			return line.substring(xmlParse.length()+1);
		}
		return null;
	}
	
	public static String getLastString(String line) {
		if (line != null) {
			ArrayList<String> list = getStringToList(line);
			if (!list.isEmpty()) {
				return list.get(list.size()-1);
			}
		}
		
		return null;
	}
}
