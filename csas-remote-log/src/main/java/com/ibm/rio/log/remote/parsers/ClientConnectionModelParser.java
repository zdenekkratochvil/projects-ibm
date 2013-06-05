/**
 * 
 */
package com.ibm.rio.log.remote.parsers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.ibm.rio.log.remote.daos.ILogLineDAO;
import com.ibm.rio.log.remote.daos.ITransitionDAO;
import com.ibm.rio.log.remote.daos.IViewDAO;
import com.ibm.rio.log.remote.model.AbstractEntity;
import com.ibm.rio.log.remote.model.LogLine;
import com.ibm.rio.log.remote.model.Transition;
import com.ibm.rio.log.remote.model.View;
import com.ibm.rio.log.remote.model.enums.ViewType;

/**
 * @author Zdenek Kratochvil
 *
 */
public class ClientConnectionModelParser {
	
	private ILogLineDAO logLineDAO;
	private IViewDAO viewDAO;
	private ITransitionDAO transitionDAO;

	public View build(String clientConnection) {
		List<LogLine> logLines = logLineDAO.load(clientConnection);
		
		if(logLines.isEmpty()) {
			return null;
		}
		
		List<Long> viewIds = new ArrayList<Long>();
		List<Long> transitionIds = new ArrayList<Long>();
		
		for(LogLine ll : logLines) {
			if(ll.getView() != null) {
				viewIds.add(ll.getView().getId());
			} else if (ll.getTransition() != null) {
				transitionIds.add(ll.getTransition().getId());
			}
		}
		
		List<View> views = viewDAO.load(viewIds);
		List<Transition> transitions = transitionDAO.load(transitionIds);
		
		View shellView = reconstructShellModel(logLines, views, transitions);
		return shellView;
	}

	private View reconstructShellModel(List<LogLine> logLines, List<View> views, List<Transition> transitions) {
		List<View> resultViews = findShellViews(views);
		if(resultViews.size() != 1) {
			throw new IllegalStateException("There can be only one shell view for given client connection id!");
		}
		
		List<AbstractEntity> entities = new ArrayList<AbstractEntity>();
		entities.addAll(views);
		entities.addAll(transitions);
		
		View shellView = resultViews.get(0);
		
		resolveLogLines(shellView, logLines);
		
		constructTree(shellView, entities, logLines);
		
		return shellView;
	}
	
	private List<View> findShellViews(List<View> views) {
		List<View> result = new ArrayList<View>();
		
		for(Iterator<View> iterator = views.iterator(); iterator.hasNext();) {
			View view = iterator.next();
			if(ViewType.SHELL.equals(view.getType())) {
				result.add(view);
				iterator.remove();
			}
		}
		
		return result;
	}

	private void constructTree(AbstractEntity parent, List<AbstractEntity> entities, List<LogLine> logLines) {
		if(parent instanceof LogLine == false) {
			for(Iterator<AbstractEntity> iterator = entities.iterator(); iterator.hasNext();) {
				AbstractEntity entity = iterator.next();
				resolveLogLines(entity, logLines);
				if(isView(parent, entity) || isTransition(parent, entity)) {
					entity.setParent(parent, true);
					iterator.remove();
				}
			}
			
			for(AbstractEntity child : parent.getChilds()) {
				constructTree(child, entities, logLines);
			}
		}
	}

	private boolean isTransition(AbstractEntity parent, AbstractEntity entity) {
		return entity.getTransition() != null && parent instanceof Transition && entity.getTransition().getId() == parent.getId();
	}

	private boolean isView(AbstractEntity parent, AbstractEntity entity) {
		return entity.getView() != null && parent instanceof View && entity.getView().getId() == parent.getId();
	}
	
	private void resolveLogLines(AbstractEntity entity, List<LogLine> logLines) {
		for(Iterator<LogLine> iterator = logLines.iterator(); iterator.hasNext();) {
			LogLine line = iterator.next();
			if(((entity instanceof View && line.getView() != null) || (entity instanceof Transition && line.getTransition() != null)) && entity.getId() == line.getParent().getId()) {
				line.setParent(entity, true);
				iterator.remove();
			}
		}
	}

	public LogLine buildStack(String clientConnection, Date date) {
		View shell = build(clientConnection);
		
		LogLine[] last = new LogLine[1];
		if(findLastLogLineBefore(shell, date, last)) {
			return last[0];
		}
		
		return null;
	}

	private boolean findLastLogLineBefore(AbstractEntity parent, Date date, LogLine[] last) {
		for(AbstractEntity entity : parent.getChilds()) {
			if(entity instanceof LogLine) {
				 LogLine ll = (LogLine) entity;
				if(ll.getStartDate().getTime() > date.getTime()) {
					return true;
				} else if (ll.getStartDate().getTime() == date.getTime()) {
					last[0] = ll;
					return true;
				}
				
				last[0] = ll;
			} else {
				boolean found = findLastLogLineBefore(entity, date, last);
				if(found) {
					return true;
				}
			}
		}
		return false;
	}

	public void setLogLineDAO(ILogLineDAO logLineDAO) {
		this.logLineDAO = logLineDAO;
	}

	public void setViewDAO(IViewDAO viewDAO) {
		this.viewDAO = viewDAO;
	}

	public void setTransitionDAO(ITransitionDAO transitionDAO) {
		this.transitionDAO = transitionDAO;
	}
}
