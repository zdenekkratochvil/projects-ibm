/**
 * 
 */
package com.ibm.rio.log.remote.model;

import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Zdenek Kratochvil
 *
 */
public abstract class AbstractEntity {

	private static final  Comparator<AbstractEntity> comparator = new Comparator<AbstractEntity>() {
		public int compare(AbstractEntity o1, AbstractEntity o2) {
			Long id1 = getIdForCompare(o1);
			Long id2 = getIdForCompare(o2);
			return id1.compareTo(id2);
		}
	};
	
	private long id;
	private Transition transition;
	private View view;
	private transient Set<AbstractEntity> childs;
	private Date startDate;
	private long duration;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Transition getTransition() {
		return transition;
	}

	private void setTransition(Transition transition) {
		this.transition = transition;
	}

	public View getView() {
		return view;
	}

	private void setView(View view) {
		this.view = view;
	}

	public void setParent(AbstractEntity entity) {
		setParent(entity, false);
	}
	
	public void setParent(AbstractEntity entity, boolean bidirectional) {
		if(entity == null) {
			return;
		} else if(entity instanceof View) {
			setView((View)entity);
		} else if (entity instanceof Transition) {
			setTransition((Transition)entity);
		} else {
			throw new IllegalArgumentException("Entity of type " + entity.getClass().getCanonicalName() + " is not suitable parent for LogLine");
		}
		
		// making relationship bidirectional makes impossible for garbage collector to detect unused
		// parts of tree and its removal from memory. Use only in cases you wish to reconstruct whole model tree
		if(bidirectional) {
			entity.getChilds().add(this);
		}
	}
	
	public AbstractEntity getParent() {
		return view != null ? view : transition;
	}

	public Set<AbstractEntity> getChilds() {
		if(childs == null) {
			childs = new TreeSet<AbstractEntity>(comparator);
		}
		return childs;
	}
	
	/**
	 * Return id of first child log line or entity id if entity is log line 
	 */
	private static final Long getIdForCompare(AbstractEntity o1) {
		if(o1 instanceof LogLine) {
			return o1.getId();
		} else if(o1.getChilds() != null) {
			for(AbstractEntity entity : o1.getChilds()) {
				if(entity instanceof LogLine) {
					return entity.getId();
				}
			}
		}
		return Long.valueOf(0L);
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

}
