/**
 * 
 */
package com.ibm.rio.log.remote.run;

import org.springframework.beans.factory.annotation.Required;

import com.ibm.rio.log.remote.aop.PerfAspect;
import com.ibm.rio.log.remote.daos.ILogLineDAO;

/**
 * @author Zdenek Kratochvil
 *
 */
public class PersistenceManager {
	
	private ILogLineDAO logLineDAO;
	private PerfAspect perfAspect;

	public void flush() {
		logLineDAO.flush();
		perfAspect.flush();
	}

	@Required
	public void setLogLineDAO(ILogLineDAO logLineDAO) {
		this.logLineDAO = logLineDAO;
	}

	@Required
	public void setPerfAspect(PerfAspect perfAspect) {
		this.perfAspect = perfAspect;
	}
}
