/**
 * 
 */
package com.ibm.rio.log.remote.run;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.rio.log.remote.AbstractLogTest;
import com.ibm.rio.log.remote.model.AbstractEntity;
import com.ibm.rio.log.remote.model.LogLine;
import com.ibm.rio.log.remote.model.View;

/**
 * @author Zdenek Kratochvil
 *
 */
public class LogManagerTest extends AbstractLogTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LogManager.class);

	private LogManager logManager;
	
	@Test
	public void store() throws FileNotFoundException, IOException {
		long currentTimeMillis = System.currentTimeMillis();
		System.out.println("start: " + currentTimeMillis);
		logManager.store(FILENAME);
		System.out.println(System.currentTimeMillis() - currentTimeMillis);
	}
	
	@Test
	public void lookup() {
		View view = logManager.lookup("12FD0869-FD0E-A3F5-F80A-D0AC2842E286");
		printDown(view, 0);
	}
	
	@Test
	public void staticLookup() {
		View view = LogManagerHolder.getLogManager().lookup("12FD0869-FD0E-A3F5-F80A-D0AC2842E286");
		printDown(view, 0);
	}
	
	@Test
	public void lookupStack() throws ParseException {
		String time = "01-01-1970 17:03:15.919";
		Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").parse(time);
		LogLine ll = logManager.lookup("12FD0869-FD0E-A3F5-F80A-D0AC2842E286", date);
		printUp(ll, 0);
	}
	
	@Test
	public void lookupConnections() {
		List<Date> dates = new ArrayList<Date>();
		dates.add(new Date(0));
		Set<String> clientConnections = logManager.getConnectionsFor(dates);
		LOGGER.info(clientConnections.toString());
	}

	private void printDown(AbstractEntity parent, int pad) {
		LOGGER.info(StringUtils.leftPad("", pad, '*') + parent);
		if(parent != null && parent.getChilds() != null) {
			for(AbstractEntity entity : parent.getChilds()) {
				printDown(entity, pad + 1);
			}
		}
	}
	
	private void printUp(AbstractEntity child, int pad) {
		LOGGER.info(StringUtils.leftPad("", pad, '*') + child);
		if(child.getParent() != null) {
			printUp(child.getParent(), pad + 1);
		}
	}
}
