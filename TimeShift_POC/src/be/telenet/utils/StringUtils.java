package be.telenet.utils;

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

}
