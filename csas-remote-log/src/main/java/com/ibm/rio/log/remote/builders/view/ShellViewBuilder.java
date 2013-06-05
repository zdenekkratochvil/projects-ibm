/**
 * 
 */
package com.ibm.rio.log.remote.builders.view;

import java.util.Stack;

import org.apache.commons.lang.StringUtils;

import com.ibm.rio.log.remote.model.AbstractEntity;
import com.ibm.rio.log.remote.model.LogLine;
import com.ibm.rio.log.remote.model.View;
import com.ibm.rio.log.remote.model.enums.ViewType;

/**
 * @author Zdenek Kratochvil
 *
 */
public class ShellViewBuilder extends AbstractViewBuilder {

	@Override
	public boolean canHandle(LogLine ll) {
		return StringUtils.startsWith(ll.getLogText(), "CSASShell - Start Application");
	}

	@Override
	public void build(LogLine ll, Stack<AbstractEntity> stack) {
		View shell = new View();
		shell.setType(ViewType.SHELL);
		ll.setParent(shell);
		
		stack.push(shell);
		
		getViewDAO().insert(shell);
		
		super.build(ll, stack);
	}

}
