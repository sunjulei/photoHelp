package com.sunlee.photoaide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ͨ
 * @author Android
 *
 */

public class CommonUtils {
/**
 *
 */
	private static String defaulPattern = "yyyy-MM-dd HH:mm";
	private static SimpleDateFormat sdf = new SimpleDateFormat(defaulPattern, Locale.CHINA);
	private static Date date = new Date();
	
	
	
	public static String getFirmattedTime(long timeMillis){
		return getFormattedTime("yyyy-MM-dd HH:mm",timeMillis);
	}
	
	public static String getFormattedTime(String pattern, long timeMillis){
		if(pattern != null){
			sdf.applyPattern(pattern);
		}
		date.setTime(timeMillis);
		return sdf.format(date);
	}
}
