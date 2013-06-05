/**
 * 
 */
package com.ibm.rio.youtrack.report.run;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.ibm.rio.youtrack.report.PropertiesHolder;
import com.ibm.rio.youtrack.report.PropertyConstants;
import com.ibm.rio.youtrack.report.connection.HttpConnectionHandler;
import com.ibm.rio.youtrack.report.connection.UrlBuilder;
import com.ibm.rio.youtrack.report.convertor.XML2XLSConvertor;

/**
 * http://youtrack.csin.cz:91/rest/issue?filter=project%3A+BRA+%23PROD_INCIDENT+HPQC+IDS%3A+{No+HPQC+Id}+%23Unresolved&with=id&with=summary&with=Priority&with=Type&with=State&max=1000&dataType=json
 * @author Zdenek Kratochvil
 *
 */
public class Main {

	static {
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		
		String authStringEnc = PropertiesHolder.getProperty(PropertyConstants.YOUTRACK_AUTHENTICATION);
 
		Map<String,String> fieldsList= new LinkedHashMap<String, String>();
		String url = UrlBuilder.createUrl(args, fieldsList);
		if(StringUtils.isBlank(url)) {
			System.exit(0);
			return;
		}
		
		System.out.println("Calling " + url);

		HttpURLConnection con = new HttpConnectionHandler(authStringEnc).connect(url);
		try {
			new XML2XLSConvertor(PropertiesHolder.getProperty(PropertyConstants.FILE_OUT), fieldsList).convert(con.getInputStream());
		} finally {
			con.disconnect();
		}
		
	}

}
