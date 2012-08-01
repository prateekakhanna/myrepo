package be.telenet.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EPCDate {

	/**
	 * Converts EPC date into a Calendar object.
	 * EPC date is a string with the format "yyyyMMdd HHmmssSSS". In case input date is 'NONE', null is returned.
	 * @param dateAsStr
	 * @return
	 */
	public static Calendar getEPCDateAsCalendar(String dateAsStr) {
					Calendar calendar = null;
					if (dateAsStr != null && dateAsStr.length() > 0) {
								   if (dateAsStr == "NONE")//EPC representation of non existing date value
								   {
									   return null;
								   }
								   else {
										   calendar = Calendar.getInstance();
										   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
										   Date date;
										   try {
											dateAsStr = dateAsStr.replace("T", " ");
											date = format.parse(dateAsStr);
											calendar.setTime(date);
										   } catch (ParseException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}										
								   }
					}
					return calendar;
	}
	
	public static String getCalendarAsEPCDateStr(Calendar calendar) {
		
		if (calendar != null) {
			Date date = calendar.getTime();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			String dateAsStr = format.format(date);
			
			return dateAsStr.replace(" ", "T");
		}
		return null;
	}
	
}
