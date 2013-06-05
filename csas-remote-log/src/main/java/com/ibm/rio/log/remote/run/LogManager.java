/**
 * 
 */
package com.ibm.rio.log.remote.run;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.ibm.rio.log.remote.daos.ILogLineDAO;
import com.ibm.rio.log.remote.model.LogLine;
import com.ibm.rio.log.remote.model.View;
import com.ibm.rio.log.remote.parsers.ClientConnectionModelParser;
import com.ibm.rio.log.remote.parsers.RemoteLogFileParser;

/**
 * @author Zdenek Kratochvil
 *
 */
public class LogManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LogManager.class);

	private RemoteLogFileParser remoteLogParser;
	private ClientConnectionModelParser modelParser;
	private ILogLineDAO logLineDAO;

	public void store(String filename) throws FileNotFoundException, IOException {
		LOGGER.info("Parsing file " + filename);
		if(filename.contains("remote")) {
			LOGGER.info("File parser selected: " + RemoteLogFileParser.class.getCanonicalName());
			remoteLogParser.parse(filename);
		}
	}

	public View lookup(String clientConnection) {
		View view = modelParser.build(clientConnection);
		return view;
	}

	public LogLine lookup(String clientConnection, Date date) {
		LogLine ll = modelParser.buildStack(clientConnection, date);
		return ll;
	}

	public Set<String> getConnectionsFor(List<Date> dates) {
		Set<String> connections = new TreeSet<String>();
		for(Date date : dates) {
			DateTime dt = new DateTime(date);
			DateTime start = dt.withTimeAtStartOfDay();
			DateTime end = start.plusDays(1);
			
			List<String> connectionsToDate = logLineDAO.getConnectionsFor(start.toDate(), end.toDate());
			connections.addAll(connectionsToDate);
		}
		return connections;
	}

	@Required
	public void setRemoteLogParser(RemoteLogFileParser remoteLogParser) {
		this.remoteLogParser = remoteLogParser;
	}

	@Required
	public void setModelParser(ClientConnectionModelParser modelParser) {
		this.modelParser = modelParser;
	}

	@Required
	public void setLogLineDAO(ILogLineDAO logLineDAO) {
		this.logLineDAO = logLineDAO;
	}

}
