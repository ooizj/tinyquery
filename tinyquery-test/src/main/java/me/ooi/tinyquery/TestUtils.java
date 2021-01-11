package me.ooi.tinyquery;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jun.zhao
 */
public class TestUtils {
	
	private static final String DF_PATTERN_STRING = "yyyy-MM-dd HH:mm:ss";
	private static final String DF_DAY_PATTERN_STRING = "yyyy-MM-dd";
	
	private static final ThreadLocal<DateFormat> DF_TL = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat(DF_PATTERN_STRING);
		}
	};
	
	private static final ThreadLocal<DateFormat> DF_TL_DAY = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat(DF_DAY_PATTERN_STRING);
		}
	};

	public static Date date(String dateStr) {
		try {
			if( DF_PATTERN_STRING.length() == dateStr.length() ) {
				return DF_TL.get().parse(dateStr);
			}else if( DF_DAY_PATTERN_STRING.length() == dateStr.length() ) {
				return DF_TL_DAY.get().parse(dateStr);
			}else {
				throw new RuntimeException("unsupported date");
			}
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static boolean timeEq(Date d1, Date d2) {
		return DF_TL.get().format(d1).equals(DF_TL.get().format(d2));
	}
	
	public static boolean dayEq(Date d1, Date d2) {
		return DF_TL_DAY.get().format(d1).equals(DF_TL_DAY.get().format(d2));
	}

}
