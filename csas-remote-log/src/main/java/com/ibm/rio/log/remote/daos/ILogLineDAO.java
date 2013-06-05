/**
 * 
 */
package com.ibm.rio.log.remote.daos;

import java.util.Date;
import java.util.List;

import com.ibm.rio.log.remote.model.LogLine;


/**
 * @author Zdenek Kratochvil
 *
 */
public interface ILogLineDAO {

	void insert(LogLine entity);
	
	void update(LogLine entity);

	List<LogLine> load(String clientConnection);

	LogLine findLastLogLine(String clientConnection, Date date);

	List<String> getConnectionsFor(Date from, Date to);

	void flush();
	

}
