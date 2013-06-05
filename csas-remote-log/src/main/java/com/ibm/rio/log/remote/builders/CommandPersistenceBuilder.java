/**
 * 
 */
package com.ibm.rio.log.remote.builders;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.ibm.rio.log.remote.model.AbstractEntity;
import com.ibm.rio.log.remote.model.LogLine;
import com.ibm.rio.log.remote.model.View;
import com.ibm.rio.log.remote.model.enums.ViewType;

/**
 * @author Zdenek Kratochvil
 *
 */
public class CommandPersistenceBuilder extends AbstractPersistenceBuilder {

	private static final String START = "- Executing command: ";
	
	@Override
	public boolean canHandle(LogLine ll) {
		return StringUtils.startsWith(ll.getLogText(), START);
	}

	@Override
	public void build(LogLine ll, Stack<AbstractEntity> stack) {
		View command = new View();
		command.setType(ViewType.COMMAND);
		String name = createName(ll.getLogText());
		command.setName(name);
		
		ll.setParent(command);
		command.setParent(peek(stack));
		
		getViewDAO().insert(command);
		
		super.build(ll, stack);
	}
	
	private String createName(String logText) {
		String regex = START + "([:\\.a-zA-Z0-9]+)";
		Matcher matcher = Pattern.compile(regex).matcher(logText);
		if(matcher.find()) {
			String name = matcher.group(1);
			return name;
		}
		return null;
	}

}
