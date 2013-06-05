/**
 * 
 */
package com.ibm.rio.log.remote.builders;

import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.rio.log.remote.builders.utils.EntityStackUtil;
import com.ibm.rio.log.remote.daos.ILogLineDAO;
import com.ibm.rio.log.remote.daos.ITransitionDAO;
import com.ibm.rio.log.remote.daos.IViewDAO;
import com.ibm.rio.log.remote.model.AbstractEntity;
import com.ibm.rio.log.remote.model.LogLine;
import com.ibm.rio.log.remote.model.Transition;
import com.ibm.rio.log.remote.model.View;

/**
 * @author Zdenek Kratochvil
 * 
 */
public abstract class AbstractPersistenceBuilder implements IEntityBuilder {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPersistenceBuilder.class);

	private ILogLineDAO logLineDAO;
	private ITransitionDAO transitionDAO;
	private IViewDAO viewDAO;

	@Override
	public void build(LogLine ll, Stack<AbstractEntity> stack) {
		
		if(ll.getParent() != null) {
			logLineDAO.insert(ll);
		} else {
			LOGGER.warn("Skipping line without parent: " + ll);
		}
	}
	
	protected ILogLineDAO getLogLineDAO() {
		return logLineDAO;
	}

	public void setLogLineDAO(ILogLineDAO logLineDAO) {
		this.logLineDAO = logLineDAO;
	}

	protected ITransitionDAO getTransitionDAO() {
		return transitionDAO;
	}

	public void setTransitionDAO(ITransitionDAO transitionDAO) {
		this.transitionDAO = transitionDAO;
	}

	protected IViewDAO getViewDAO() {
		return viewDAO;
	}

	public void setViewDAO(IViewDAO viewDAO) {
		this.viewDAO = viewDAO;
	}

	protected void update(AbstractEntity entity) {
		if(entity instanceof Transition) {
			transitionDAO.update((Transition)entity);
		} else if (entity instanceof View) {
			viewDAO.update((View)entity);
		}
	}
	
	protected AbstractEntity peek(Stack<AbstractEntity> stack) {
		return EntityStackUtil.peek(stack);
	}
}
