package com.expleoautomation.utils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DateUtils {
	


	public static String currentDate(String strFormat) {
		// "dd/MM/yyyy";
		DateFormat dateFormat = new SimpleDateFormat(strFormat);
		Date currentDate = new Date();
		return dateFormat.format(currentDate).toString();
		// return dateStr;

	}

	public static String requiredDate(int numofDays, String strFormat) {
		DateFormat dateFormat = new SimpleDateFormat(strFormat);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, numofDays);
		Date newDate = c.getTime();
		return dateFormat.format(newDate).toString();

	}

	public static String reFormat(String value, String fromFormat, String toFormat) {
		if (value!=null && value.length()>0 ) {
			DateTimeFormatter fromFormatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(fromFormat).toFormatter(Locale.ENGLISH);
			DateTimeFormatter toFormatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(toFormat).toFormatter(Locale.ENGLISH);
			return LocalDate.parse(value, fromFormatter).format(toFormatter).toUpperCase();
		}
		else {
			return null;
		}
	}
	public static String format(String value, String toFormat) {
		if (value!=null) {
			DateTimeFormatter toFormatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(toFormat).toFormatter(Locale.ENGLISH);
			return LocalDate.parse(value).format(toFormatter).toUpperCase();
		}		else {
			return null;
		}
	}
	


}
