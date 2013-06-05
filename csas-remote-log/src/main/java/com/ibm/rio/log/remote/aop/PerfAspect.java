/**
 * 
 */
package com.ibm.rio.log.remote.aop;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author Zdenek Kratochvil
 *
 */
public class PerfAspect {
	
	private Map<String, Integer> map = new HashMap<String, Integer>();

	public Object advice(final ProceedingJoinPoint pjp) throws Throwable {
		
		String methodQualifiedName = pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName();
		Integer value = map.get(methodQualifiedName);
		if(value == null) {
			value = 0;
		}
		
		long millis = System.currentTimeMillis();
		try {
			return pjp.proceed();
		} finally {
			map.put(methodQualifiedName, (value + (int)(System.currentTimeMillis() - millis)));
		}
	}
	
	public void flush() {
		for(Entry<String, Integer> entry : map.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}
}
