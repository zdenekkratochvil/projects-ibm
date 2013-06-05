/**
 * 
 */
package com.ibm.rio.log.remote.builders;

import java.util.Stack;

import com.ibm.rio.log.remote.model.AbstractEntity;
import com.ibm.rio.log.remote.model.LogLine;

/**
 * Note that each entity builder is supposed to modify entity stack with respect to given log line.
 * Persistence is responsibility of each builder
 * 
 * @author Zdenek Kratochvil
 *
 */
public interface IEntityBuilder {

	/**
	 * @return true if this builder can handle given log line
	 */
	boolean canHandle(LogLine ll);
	
	/**
	 * Rebuild entity stack with respect to given log line
	 */
	void build(LogLine ll, Stack<AbstractEntity> stack);
}
