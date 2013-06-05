/**
 * 
 */
package com.ibm.rio.log.remote.daos;

import java.util.List;

import com.ibm.rio.log.remote.model.Transition;


/**
 * @author Zdenek Kratochvil
 *
 */
public interface ITransitionDAO {

	void insert(Transition entity);
	
	void update(Transition entity);

	List<Transition> load(List<Long> transitionIds);

	Transition load(long id);
	
}
