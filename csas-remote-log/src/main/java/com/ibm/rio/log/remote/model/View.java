/**
 * 
 */
package com.ibm.rio.log.remote.model;

import com.ibm.rio.log.remote.model.enums.ViewType;

/**
 * @author Zdenek Kratochvil
 *
 */
public class View extends AbstractEntity {

	private ViewType type;
	private String name;

	public ViewType getType() {
		return type;
	}
	
	public void setType(ViewType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "View [type=" + type + ", name=" + name + ", getId()=" + getId() + ", getStartDate()=" + getStartDate() + "]";
	}

}
