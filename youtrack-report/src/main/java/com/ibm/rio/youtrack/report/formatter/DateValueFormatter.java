/**
 * 
 */
package com.ibm.rio.youtrack.report.formatter;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ibm.rio.youtrack.report.PropertiesHolder;
import com.ibm.rio.youtrack.report.PropertyConstants;

/**
 * @author Zdenek Kratochvil
 *
 */
public class DateValueFormatter implements IValueFormatter {

	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(PropertiesHolder.getProperty(PropertyConstants.DATE_FORMAT));
	
	@Override
	public boolean canHandle(String clazz) {
		return Date.class.getCanonicalName().equals(clazz);
	}

	@Override
	public String format(String value) {
		long millis = Long.parseLong(value);
		Date date = new Date(millis);
		return DATE_FORMATTER.format(date);
	}

}
