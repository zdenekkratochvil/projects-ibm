/**
 * 
 */
package com.ibm.rio.log.remote.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.ibm.rio.log.remote.builders.IEntityBuilder;
import com.ibm.rio.log.remote.model.AbstractEntity;
import com.ibm.rio.log.remote.model.LogLine;
import com.ibm.rio.log.remote.run.PersistenceManager;

/**
 * @author Petr Svoboda (IBM)
 *
 */
public class RemoteLogFileParser {

	private static final Logger logger = LoggerFactory.getLogger(RemoteLogFileParser.class);
	
	private LogLineParser logLineParser;
	private List<IEntityBuilder> entityBuilders;
	private PersistenceManager persistenceManager;
	
	public void parse(String filename) throws FileNotFoundException, IOException {
		CharBuffer buf = createBuffer(filename);

		Map<String, Stack<AbstractEntity>> stackMap = new HashMap<String, Stack<AbstractEntity>>();

		LogLine ll;
		int linesCount = 0;
		do {
			ll = logLineParser.parseLine(buf);
			if(ll != null && StringUtils.isNotBlank(ll.getConnectionId())) {
				//construct model
				rebuildEntityStack(stackMap, ll);
				linesCount++;
			}
		} while (ll != null);

		persistenceManager.flush();
		logger.debug("read " + linesCount + " lines");
	}

	/**
	 * There is always only one builder capable of handling given log line.
	 * Not that each builder is supposed to process log line and entity stack to most recent state according to given log line
	 */
	private void rebuildEntityStack(Map<String, Stack<AbstractEntity>> stackMap, LogLine ll) {
		for(IEntityBuilder builder : entityBuilders) {
			if(builder.canHandle(ll)) {
				logger.debug("Builder " + builder + " can handle log line:\n" + ll);
				Stack<AbstractEntity> entityStack = getEntityStack(stackMap, ll.getConnectionId());
				builder.build(ll, entityStack);
				break;
			}
		}
	}

	private Stack<AbstractEntity> getEntityStack(Map<String, Stack<AbstractEntity>> stackMap, String connectionId) {
		Stack<AbstractEntity> entityStack = stackMap.get(connectionId);
		if(entityStack != null) {
			logger.debug("Entity stack for clientConnectionId=" + connectionId + " found");
			return entityStack;
		}
		logger.debug("Entity stack for clientConnectionId=" + connectionId + " not found, creating new stack instance");
		entityStack = new Stack<AbstractEntity>();
		stackMap.put(connectionId, entityStack);
		return entityStack;
	}

	private CharBuffer createBuffer(String filename) throws FileNotFoundException, IOException {
		File f = new File(filename);
		logger.debug("f: " + f.canRead());
		FileReader fr = new FileReader(f);
		CharBuffer buf = CharBuffer.allocate((int) f.length());
		int result = fr.read(buf);
		logger.debug("read result: " + result);
		fr.close();
		buf.position(0);
		logger.debug("buf len: " + buf.remaining());
		return buf;
	}

	@Required
	public void setEntityBuilders(List<IEntityBuilder> entityBuilders) {
		this.entityBuilders = entityBuilders;
	}

	@Required
	public void setLogLineParser(LogLineParser logLineParser) {
		this.logLineParser = logLineParser;
	}

	@Required
	public void setPersistenceManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

}
