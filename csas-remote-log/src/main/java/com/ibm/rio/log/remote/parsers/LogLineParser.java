/**
 * 
 */
package com.ibm.rio.log.remote.parsers;

import java.nio.CharBuffer;

import com.ibm.rio.log.remote.model.LogLine;

/**
 * @author Zdenek Kratochvil
 *
 */
public class LogLineParser {

	private static final int	NO_TOKEN				= -1;
	private static final int	PROCESS_ID_TOKEN		= 0;
	private static final int	USER_ID_TOKEN			= 1;
	private static final int	ELAPSED_TIME_TOKEN		= 2;
	private static final int	LOGLEVEL_TOKEN			= 3;
	private static final int	CONNECTIONID_TOKEN		= 4;
	private static final int	LOG_TIME_TOKEN			= 5;
	private static final int	CLASS_TOKEN				= 6;
	private static final int	LOG_TEXT_TOKEN			= 7;
	private static final int	INITIAL_SEPARATOR_TOKEN	= 8;

	private static final int	PROCESS_CHAR			= 0;
	private static final int	EVAL_TOKEN				= 1;

	private static char			CR						= '\n';
	

	public LogLine parseLine(CharBuffer buf) {
		if (buf.remaining() == 0)
			return null;

		LogLine ll = new LogLine();
		char ch = buf.get();

		StringBuffer token = null;
		StringBuffer log = new StringBuffer();
		int nextTokenType = PROCESS_ID_TOKEN;
		int action = PROCESS_CHAR;
		char lastChar = ' ';
		while (buf.remaining() > 0) {
			if (ch == CR) {
				break;
			} else {
				if ((ch == ']' || ch == ' ' || ch == '[') && lastChar != '\'')
					action = EVAL_TOKEN;
				else
					action = PROCESS_CHAR;

				switch (action) {
				// case '[':
				// if (nextTokenType != NO_TOKEN) {
				// token = new StringBuffer();
				// token.append(ch);
				// break;
				// }
				case EVAL_TOKEN:
					if (token != null && (nextTokenType != LOG_TEXT_TOKEN || (nextTokenType == LOG_TEXT_TOKEN && ch == '['))) {
						token.append(ch);
						String tokenStr = token.toString();
						token = null;

						switch (nextTokenType) {
						case PROCESS_ID_TOKEN:
							ll.parseProcessId(tokenStr);
							nextTokenType = USER_ID_TOKEN;
							break;
						case USER_ID_TOKEN:
							ll.parseUserId(tokenStr);
							nextTokenType = LOG_TIME_TOKEN;
							break;
						case ELAPSED_TIME_TOKEN:
							ll.parseElapsedTime(tokenStr);
							nextTokenType = INITIAL_SEPARATOR_TOKEN;
							break;
						case INITIAL_SEPARATOR_TOKEN:
							nextTokenType = LOGLEVEL_TOKEN;
							break;
						case LOGLEVEL_TOKEN:
							ll.parseLogLevel(tokenStr);
							nextTokenType = CLASS_TOKEN;
							break;
						case CONNECTIONID_TOKEN:
							ll.parseConnectionId(tokenStr);
							nextTokenType = NO_TOKEN;
							break;
						case LOG_TIME_TOKEN:
							ll.parseLogTime(tokenStr);
							nextTokenType = ELAPSED_TIME_TOKEN;
							break;
						case CLASS_TOKEN:
							ll.parseClass(tokenStr);
							nextTokenType = LOG_TEXT_TOKEN;
							break;
						case LOG_TEXT_TOKEN:
							ll.parseText(tokenStr);
							nextTokenType = CONNECTIONID_TOKEN;
							break;
						}
						break;
						// because evaluation failed, process char as usualla
						// action = PROCESS_CHAR;
					}
				default:
					if (nextTokenType != NO_TOKEN) {
						if (token == null)
							token = new StringBuffer();
					}

					if (token != null)
						token.append(ch);
					else
						log.append(ch);
				}
			}
			lastChar = ch;
			// read next char
			ch = buf.get();
		}

		return ll;
	}


}
