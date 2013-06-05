/**
 * 
 */
package com.ibm.rio.youtrack.report.connection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * @author Zdenek Kratochvil
 * 
 */
public class HttpConnectionHandler {

	private String authStringEnc;

	public HttpConnectionHandler(String authString) {
		this.authStringEnc = authString;
	}

	public HttpURLConnection connect(String urlStr) {

		HttpURLConnection connection = null;
		try {
			// Create connection
			connection = createConnection(urlStr);
			connection.connect();

			// Get Response
			return connection;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 

	}

	private HttpURLConnection createConnection(String urlStr) throws MalformedURLException, IOException, ProtocolException {
		URL url;
		HttpURLConnection connection;
		url = new URL(urlStr);
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Language", "en-US");
		connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
		
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		return connection;
	}

}
