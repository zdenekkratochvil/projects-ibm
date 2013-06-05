/**
 * 
 */
package com.ibm.rio.log.remote.model;

import com.ibm.rio.log.remote.model.enums.TransitionType;

/**
 * @author Zdenek Kratochvil
 * 
 */
public class Transition extends AbstractEntity {

	private TransitionType type;

	public TransitionType getType() {
		return type;
	}

	public void setType(TransitionType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Transition [type=" + type + ", getId()=" + getId() + ", getStartDate()=" + getStartDate() + "]";
	}

}
