/**
 * 
 */
package com.ibm.rio.youtrack.report.formatter;

/**
 * @author Zdenek Kratochvil
 *
 */
public interface IValueFormatter {

	boolean canHandle(String clazz);
	String format(String value);
}
