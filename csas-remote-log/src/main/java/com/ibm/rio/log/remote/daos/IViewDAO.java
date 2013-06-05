/**
 * 
 */
package com.ibm.rio.log.remote.daos;

import java.util.List;

import com.ibm.rio.log.remote.model.View;

/**
 * @author Zdenek Kratochvil
 *
 */
public interface IViewDAO {

	void insert(View entity);
	
	void update(View entity);

	List<View> load(List<Long> viewIds);

	View load(long id);
	
}
