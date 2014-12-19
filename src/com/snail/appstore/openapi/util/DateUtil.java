package com.snail.appstore.openapi.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 时间转换工具类
 * 
 * @author songdawei
 * 
 */
public final class DateUtil {

	private static Logger logger = Logger.getLogger(DateUtil.class.getName());

	public final static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public final static SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public final static SimpleDateFormat OtherTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public final static SimpleDateFormat OtherDateFormat = new SimpleDateFormat("yyyy/MM/dd");

	private final static SimpleDateFormat rssDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US);

	private final static SimpleTimeZone aZone = new SimpleTimeZone(8, "GMT");

	private final static long maxTimestamp = Integer.MAX_VALUE * 1000L;

	static {
		rssDate.setTimeZone(aZone);
	}

	/**
	 * 将日期对象转换为RSS用的字符串日期
	 * 
	 * @param date
	 * @return
	 */
	public synchronized static String dateToRssDate(Date d) {
		if (d == null) return null;
		return rssDate.format(d);
	}

	/**
	 * 将RSS日期字符串转换为日期对象
	 * 
	 * @param rssDate
	 * @return
	 */
	public synchronized static Date rssDateToDate(String rd) {
		if (rd == null) return null;

		Date d = null;
		try {
			d = new Date(rssDate.parse(rd).getTime());
		} catch (ParseException e) {
			logger.log(Level.WARNING, e.getMessage());
		}

		return d;
	}

	/**
	 * 将时间对象转换为yyyy/mm/dd HH:mm:ss格式用的字符串时间
	 * 
	 * @param date
	 * @return
	 */
	public synchronized static String otherTimeFormat(Date date) {
		if (date == null) return null;
		return OtherTimeFormat.format(date);
	}

	/**
	 * 将时间对象转换为yyyy-mm-dd HH:mm:ss格式用的字符串时间
	 * 
	 * @param date
	 * @return
	 */
	public synchronized static String timeFormat(Date date) {
		if (date == null) return null;
		return TimeFormat.format(date);
	}

	/**
	 * 将日期对象转换为yyyy-mm-dd 格式用的字符串日期
	 * 
	 * @param date
	 * @return
	 */
	public synchronized static String dateFormat(Date date) {
		if (date == null) return null;
		return DateFormat.format(date);
	}

	/**
	 * 将日期对象转换为yyyy/mm/dd 格式用的字符串日期
	 * 
	 * @param date
	 * @return
	 */
	public synchronized static String otherDateFormat(Date date) {
		if (date == null) return null;
		return OtherDateFormat.format(date);
	}

	/**
	 * 将格式为yyyy-mm-dd HH:mm:ss字符串时间转换成时间对象
	 * 
	 * @param date
	 * @return
	 */
	public synchronized static Date parseTime(String date) {
		if (date == null || date.isEmpty()) return null;
		Date d = null;
		try {
			d = TimeFormat.parse(date);
		} catch (ParseException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return d;
	}

	/**
	 * 将格式为yyyy-mm-dd HH:mm:ss字符串时间转换成时间对象
	 * 
	 * @param Timestamp
	 * @return
	 */
	public synchronized static Timestamp getTimestamp(String date) {
		if (date == null || date.isEmpty()) return null;
		Timestamp time = null;
		try {
			time = new Timestamp(TimeFormat.parse(date).getTime());
		} catch (ParseException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return time;
	}

	/**
	 * 将格式为yyyy/mm/dd HH:mm:ss字符串时间转换成时间对象
	 * 
	 * @param date
	 * @return
	 */
	public synchronized static Date parseOtherTime(String date) {
		if (date == null || date.isEmpty()) return null;
		Date d = null;
		try {
			d = OtherTimeFormat.parse(date);
		} catch (ParseException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return d;
	}

	/**
	 * 将格式为yyyy-mm-dd字符串日期转换成日期对象
	 * 
	 * @param date
	 * @return
	 */
	public synchronized static Date parseDate(String date) {
		if (date == null || date.isEmpty()) return null;
		Date d = null;
		try {
			d = DateFormat.parse(date);
		} catch (ParseException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return d;
	}

	/**
	 * 将格式为yyyy/mm/dd字符串日期转换成日期对象
	 * 
	 * @param date
	 * @return
	 */
	public synchronized static Date parseOtherDate(String date) {
		if (date == null || date.isEmpty()) return null;
		Date d = null;
		try {
			d = OtherDateFormat.parse(date);
		} catch (ParseException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return d;
	}

	/**
	 * 获取当前系统时间yyyy/mm/dd HH:mm:ss格式表示的字符串
	 * 
	 * @return
	 */
	public static String getOtherNowTime() {
		return otherTimeFormat(new Date());
	}

	/**
	 * 获取当前系统时间yyyy-mm-dd HH:mm:ss格式表示的字符串
	 * 
	 * @return
	 */
	public static String getNowTime() {
		return timeFormat(new Date());
	}

	/**
	 * 获取当前系统时期yyyy-mm-dd格式表示的字符串
	 * 
	 * @return
	 */
	public static String getNowDate() {
		return dateFormat(new Date());
	}

	/**
	 * 判断是否在当前时间之后 add by zhuyf 20091118
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isAfterNow(Date date) {
		return date.after(new Date());
	}

	/**
	 * 两个时间是否为同一天
	 * 
	 * @param newDate
	 * @param oldDate
	 * @return
	 */
	public synchronized static boolean isSameDay(Date newDate, Date oldDate) {
		if (newDate == null || oldDate == null) {
			return false;
		}

		return DateFormat.format(newDate).equals(DateFormat.format(oldDate));
	}

	/**
	 * 获取有效期
	 * 
	 * @param string
	 *            有效时间，例d7表示7天，y1表示1个月，0表示不限时间
	 * @return 有效期
	 */
	public static Date getDValid(String valid) {
		return getAddDValid(null, valid);
	}

	/**
	 * 获取有效期
	 * 
	 * @param string
	 *            有效时间，例d7表示7天，y1表示1个月，0表示不限时间
	 * @return 有效期
	 */
	public static Date getAddDValid(Date date, String valid) {
		if ("0".equals(valid)) {
			return DateUtil.parseDate("9999-12-31");
		}
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		char c = valid.toLowerCase().charAt(0);
		if (c == 'y') {
			int year = Integer.parseInt(valid.substring(1));
			cal.add(Calendar.YEAR, year);
			return cal.getTime();
		} else if (c == 'm') {
			int month = Integer.parseInt(valid.substring(1));
			cal.add(Calendar.MONTH, month);
			return cal.getTime();
		} else {
			int day = Integer.parseInt(valid.substring(1));
			cal.add(Calendar.DAY_OF_MONTH, day);
			return cal.getTime();
		}
	}

	/**
	 * 获取有效期
	 * 
	 * @param string
	 *            有效时间，例d7表示7天，y1表示1个月，0表示不限时间
	 * @return 有效期
	 */
	public static Timestamp getDValidStamp(String valid) {
		return getAddDValidStamp(null, valid);
	}

	/**
	 * 获取有效期
	 * 
	 * @param string
	 *            有效时间，例d7表示7天，y1表示1个月，0表示不限时间
	 * @return 有效期
	 */
	public static Timestamp getAddDValidStamp(Date date, String valid) {
		if ("0".equals(valid)) {
			return new Timestamp(maxTimestamp);
		}
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}

		char c = valid.toLowerCase().charAt(0);
		if (c == 'y') {
			int year = Integer.parseInt(valid.substring(1));
			cal.add(Calendar.YEAR, year);
		} else if (c == 'm') {
			int month = Integer.parseInt(valid.substring(1));
			cal.add(Calendar.MONTH, month);
		} else {
			int day = Integer.parseInt(valid.substring(1));
			cal.add(Calendar.DAY_OF_MONTH, day);
		}

		long time = cal.getTimeInMillis();
		if (time > maxTimestamp) {
			time = maxTimestamp;
		}
		return new Timestamp(time);
	}
}
