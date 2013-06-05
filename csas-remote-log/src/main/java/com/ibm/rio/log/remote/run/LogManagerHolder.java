/**
 * 
 */
package com.ibm.rio.log.remote.run;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Zdenek Kratochvil
 *
 */
public class LogManagerHolder {

	private static LogManager manager;
	
	public static LogManager getLogManager() {
		if(manager == null) {
			ApplicationContext context = new ClassPathXmlApplicationContext("remote-log-context.xml");
			manager = context.getBean("logManager", LogManager.class);
		}
		return manager;
	}
}
