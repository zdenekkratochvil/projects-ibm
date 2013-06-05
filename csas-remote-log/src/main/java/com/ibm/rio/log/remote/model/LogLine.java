package com.ibm.rio.log.remote.model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogLine extends AbstractEntity {
	
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");

	private static final Pattern CLIENT_CONNECTION_PATTERN = Pattern.compile("[A-Z0-9-]+");

	private static final Logger LOGGER = LoggerFactory.getLogger(LogLine.class);
	
	private String	processId;
	private String	userId;
	private BigDecimal	elapsedTime;
	private String	loglevel;
	private String	connectionId;
	private String	className;
	private String	logText;
	
	public static LogLine makeFrom(List<String> lineTokens) {
		if (lineTokens == null)
			return null;
		LogLine result = new LogLine();
		result.parseProcessId(lineTokens.remove(0));
		result.parseUserId(lineTokens.remove(0));
		result.parseElapsedTime(lineTokens.remove(0));
		result.parseLogLevel(lineTokens.remove(0));
		result.parseConnectionId(lineTokens.remove(0));
		result.parseLogTime(lineTokens.remove(0));
		result.parseClass(lineTokens.remove(0));
		result.parseText(lineTokens.remove(0));

		return result;
	}

	public void parseLogTime(String token) {
		try {
			setStartDate(SIMPLE_DATE_FORMAT.parse(StringUtils.chomp(token, "[")));
		} catch (ParseException e) {
			LOGGER.error(e.getLocalizedMessage());
		}
	}

	public void parseText(String token) {
		setLogText(token.trim());
	}

	public void parseClass(String token) {
		setClassName(token.trim());
	}

	public void parseConnectionId(String token) {
		token = token.replace("[", "");
		token = token.replace("]", "");
		if(CLIENT_CONNECTION_PATTERN.matcher(token).matches()) {
			setConnectionId(token.trim());
		}
	}

	public void parseLogLevel(String token) {
		token = token.replace("[", "");
		token = token.replace("]", "");
		setLoglevel(token.trim());
	}

	public void parseElapsedTime(String token) {
		token = token.replace("[", "");
		token = token.replace("s]", "");
		if(NumberUtils.isNumber(token)) {
			setElapsedTime(new BigDecimal(token));
		}
	}

	public void parseUserId(String token) {
		token = token.replace("[user=", "");
		token = token.replace("]", "");
		setUserId(token);
	}

	public void parseProcessId(String token) {
		token = token.replace("[pid=", "");
		token = token.replace("]", "");
		setProcessId(token.trim());
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setElapsedTime(BigDecimal bigDecimal) {
		this.elapsedTime = bigDecimal;
	}

	public BigDecimal getElapsedTime() {
		return elapsedTime;
	}

	public void setLoglevel(String loglevel) {
		this.loglevel = loglevel;
	}

	public String getLoglevel() {
		return loglevel;
	}

	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}

	public String getConnectionId() {
		return connectionId;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public void setLogText(String logText) {
		this.logText = logText;
	}

	public String getLogText() {
		return logText;
	}

	@Override
	public String toString() {
		return "LogLine [connectionId=" + connectionId + ", logText=" + logText + ", getId()=" + getId() + ", getStartDate()=" + formatDate() + "]";
	}

	private String formatDate() {
		return getStartDate() != null ? SIMPLE_DATE_FORMAT.format(getStartDate()) : null;
	}

}
