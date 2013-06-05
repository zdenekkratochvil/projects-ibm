/**
 * 
 */
package com.ibm.rio.log.remote;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * @author Zdenek Kratochvil
 *
 */
@ContextConfiguration(locations={"classpath:remote-log-context.xml"})
public abstract class AbstractLogTest extends AbstractJUnit4SpringContextTests {

	protected static final String FILENAME = "src/test/resources/csas-remoteLog-2013-05-14.log";
}
