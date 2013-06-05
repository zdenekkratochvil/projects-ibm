/**
 * 
 */
package com.ibm.rio.log.remote.builders.view;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.rio.log.remote.model.AbstractEntity;
import com.ibm.rio.log.remote.model.LogLine;
import com.ibm.rio.log.remote.model.View;
import com.ibm.rio.log.remote.model.enums.ViewType;

/**
 * @author Zdenek Kratochvil
 *
 */
public class WidgetViewBuilder extends AbstractViewBuilder {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WidgetViewBuilder.class);
	
	private static final String START_LOGGER = "com.ibm.rio.module.RIOModule";
	private static final String START_TEXT = "- Adding widget ";
	private static final String REMOVE_LOGGER = "com.ibm.rio.manager.RIOWidgetManager";
	private static final String REMOVE_TEXT_1 = "- Widget: ";
	private static final String REMOVE_TEXT_2 = " removed from display hierarchy";
	

	@Override
	public boolean canHandle(LogLine ll) {
		return isStart(ll) || isRemove(ll);
	}

	private boolean isRemove(LogLine ll) {
		return REMOVE_LOGGER.equals(ll.getClassName()) && StringUtils.startsWith(ll.getLogText(), REMOVE_TEXT_1) && StringUtils.contains(ll.getLogText(), REMOVE_TEXT_2);
	}

	private boolean isStart(LogLine ll) {
		return START_LOGGER.equals(ll.getClassName()) && StringUtils.startsWith(ll.getLogText(), START_TEXT);
	}

	@Override
	public void build(LogLine ll, Stack<AbstractEntity> stack) {
		if(isStart(ll)) {
			buildStart(stack, ll);
		} else if (isRemove(ll)) {
			buildRemove(stack, ll);
		}
		
		super.build(ll, stack);
	}

	private void buildRemove(Stack<AbstractEntity> stack, LogLine ll) {
		if(stack.isEmpty()) {
			LOGGER.warn("Stack is empty which means inconsistency in given log. ");
			return;
		}
		
		AbstractEntity entity = null;
		do {
			if(stack.isEmpty()) {
				LOGGER.error("Unable to find entity for line:\n" + ll);
				break;
			}
			
			entity = stack.pop();
			
			LOGGER.debug("Processing entity during stack remove:\n" + entity);
			
			if(entity.getStartDate() != null) {
				entity.setDuration(ll.getStartDate().getTime() - entity.getStartDate().getTime());
				
				update(entity);
			} else {
				LOGGER.warn("Entity has no start date:\n" + entity);
			}
			
		} while (foundWidget(entity, ll.getLogText()) == false);
		
		ll.setParent(entity);
	}

	private boolean foundWidget(AbstractEntity entity, String logText) {
		if(entity instanceof View) {
			View view = (View) entity;
			String removeString = REMOVE_TEXT_1 + view.getName() + REMOVE_TEXT_2;
			return StringUtils.startsWith(logText, removeString);
		}
		return false;
	}

	private void buildStart(Stack<AbstractEntity> stack, LogLine ll) {
		View view = new View();
		view.setParent(peek(stack));
		view.setType(ViewType.WIDGET);
		view.setStartDate(ll.getStartDate());
		
		String name = createName(ll.getLogText());
		view.setName(name);
		
		stack.push(view);
		ll.setParent(view);
		
		getViewDAO().insert(view);
	}

	private String createName(String logText) {
		String regex = START_TEXT + "([a-zA-Z0-9]+)";
		Matcher matcher = Pattern.compile(regex).matcher(logText);
		if(matcher.find()) {
			String name = matcher.group(1);
			return name;
		}
		return null;
	}

}
