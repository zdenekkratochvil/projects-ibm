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
public class SetViewTransitionBuilder extends AbstractTransitionBuilder {

	private static final String START_TEXT = "- Executing command: cz.csas.client.components.ccfw.command.local::SetViewCommand as reaction to event:";
	private static final String END_LOGGER = "cz.csas.client.components.ccfw.command.local.SetViewCommand";
	private static final String END_TEXT = "- View set to display hierarchy";
	
	
	@Override
	public boolean canHandle(LogLine ll) {
		return isStart(ll) || isEnd(ll);
	}

	private boolean isStart(LogLine ll) {
		return StringUtils.startsWith(ll.getLogText(), START_TEXT);
	}

	private boolean isEnd(LogLine ll) {
		return END_LOGGER.equals(ll.getClass()) && StringUtils.startsWith(ll.getLogText(), END_TEXT);
	}

	@Override
	public void build(LogLine ll, Stack<AbstractEntity> stack) {
		if(isStart(ll)) {
			buildStart(ll, stack);
		} else if(isEnd(ll)) {
			buildEnd(ll, stack);
		}
		
		super.build(ll, stack);
	}

	private void buildEnd(LogLine ll, Stack<AbstractEntity> stack) {
		Transition tr = EntityStackUtil.findFirstAndRemoveUpToFound(stack, TransitionType.SET_VIEW);
		ll.setParent(tr);
		tr.setDuration(ll.getStartDate().getTime() - tr.getStartDate().getTime());
		
		update(tr);
	}

	private void buildStart(LogLine ll, Stack<AbstractEntity> stack) {
		removeTopSetView(stack);
		
		Transition setView = create(ll);
		setView.setParent(peek(stack));
		stack.push(setView);
		
		getTransitionDAO().insert(setView);
	}

	private Transition create(LogLine ll) {
		Transition setView = new Transition();
		setView.setType(TransitionType.SET_VIEW);
		setView.setStartDate(ll.getStartDate());
		
		ll.setParent(setView);
		
		return setView;
	}

	private void removeTopSetView(Stack<AbstractEntity> stack) {
		AbstractEntity topEntity = peek(stack);
		if(topEntity instanceof Transition) {
			Transition tr = (Transition) topEntity;
			if(TransitionType.SET_VIEW.equals(tr.getType())) {
				stack.pop();
			}
		}
	}

}
