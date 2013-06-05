/**
 * 
 */
package com.ibm.rio.log.remote.builders.transition;

import java.util.Stack;

import org.apache.commons.lang.StringUtils;

import com.ibm.rio.log.remote.builders.utils.EntityStackUtil;
import com.ibm.rio.log.remote.model.AbstractEntity;
import com.ibm.rio.log.remote.model.LogLine;
import com.ibm.rio.log.remote.model.Transition;
import com.ibm.rio.log.remote.model.enums.TransitionType;

/**
 * @author Zdenek Kratochvil
 *
 */
public class LoadContextTransitionBuilder extends AbstractTransitionBuilder {
	
	private static final String START_LOGGER = "org.springextensions.actionscript.context.support.XMLApplicationContext";
	private static final String START_TEXT = "- Loading object definitions";
	private static final String END_LOGGER = "org.springextensions.actionscript.ioc.factory.support.DefaultListableObjectFactory";
	private static final String END_TEXT = "- Wiring";

	@Override
	public boolean canHandle(LogLine ll) {
		return isStart(ll) || isEnd(ll);
	}

	private boolean isStart(LogLine ll) {
		return START_LOGGER.equals(ll.getClassName()) && StringUtils.startsWith(ll.getLogText(), START_TEXT);
	}

	private boolean isEnd(LogLine ll) {
		return END_LOGGER.equals(ll.getClassName()) && StringUtils.startsWith(ll.getLogText(), END_TEXT);
	}
	
	@Override
	public void build(LogLine ll, Stack<AbstractEntity> stack) {
		if(isStart(ll)) {
			buildStart(ll, stack);
		} else if (isEnd(ll)) {
			buildEnd(ll, stack);
		}

		super.build(ll, stack);
	}

	private void buildStart(LogLine ll, Stack<AbstractEntity> stack) {
		AbstractEntity parent = peek(stack);
		Transition tr = new Transition();
		tr.setType(TransitionType.LOAD_CONTEXT);
		tr.setStartDate(ll.getStartDate());
		tr.setParent(parent);
		
		ll.setParent(tr);
		stack.push(tr);
		
		getTransitionDAO().insert(tr);
	}

	private void buildEnd(LogLine ll, Stack<AbstractEntity> stack) {
		Transition tr = EntityStackUtil.findFirst(stack, TransitionType.LOAD_CONTEXT);
		if(tr != null) {
			ll.setParent(tr);
			tr.setDuration(ll.getStartDate().getTime() - tr.getStartDate().getTime());
			update(tr);
		}
	}

}
