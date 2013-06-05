package com.ibm.rio.log.remote.run;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Throwable {
		
		String filename = null;
		try {
			if (args == null || args.length != 1) {
				throw new IllegalArgumentException("Program must get one argument with file absolute path.");
			}
			filename = args[0];
			
			ApplicationContext context = new ClassPathXmlApplicationContext("remote-log-context.xml");
			LogManager manager = context.getBean("logManager", LogManager.class);
		
			manager.store(filename);
		} catch (Throwable t) {
			logger.error("Exception occured processing " + filename + ":", t);
			throw t;
		}
		
	}

}
