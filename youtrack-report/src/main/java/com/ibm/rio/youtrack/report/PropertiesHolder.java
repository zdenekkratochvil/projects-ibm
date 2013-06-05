/**
 * 
 */
package com.ibm.rio.youtrack.report;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

/**
 * @author Zdenek Kratochvil
 *
 */
public class PropertiesHolder {
	
	private static Properties props;

	public static String getProperty(String key) {
		String systemAuthentication = System.getProperty(key);
		if(StringUtils.isBlank(systemAuthentication)) {
			return getProps().getProperty(key);
		}
		return systemAuthentication;
	}

	private static Properties getProps() {
		if(props == null) {
			InputStream is = PropertiesHolder.class.getResourceAsStream("/" + PropertyConstants.APPLICATION_PROPERTIES);
			try {
				if(is == null || is.available() == 0) {
					throw new IllegalStateException("Unable to locate application.properties");
				}
				
				props = new Properties();
				props.load(is);
			} catch (IOException ex) {
				throw new IllegalStateException("Cannot initialize application properties");
			}
		}
		return props;
	}
	
}
