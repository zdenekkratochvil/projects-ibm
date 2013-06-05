/**
 * 
 */
package com.ibm.rio.log.remote.builders;

import java.util.Stack;

import com.ibm.rio.log.remote.model.AbstractEntity;
import com.ibm.rio.log.remote.model.LogLine;

/**
 * Should be last in a row. Binds all handled log lines to the top object in the stack
 * @author Zdenek Kratochvil
 *
 */
public class DefaultPersistenceBuilder extends AbstractPersistenceBuilder {

	@Override
	public boolean canHandle(LogLine ll) {
		return true;
	}

	@Override
	public void build(LogLine ll, Stack<AbstractEntity> stack) {
		AbstractEntity entity = peek(stack);
		ll.setParent(entity);
		
		super.build(ll, stack);
	}

}
