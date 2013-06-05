/**
 * 
 */
package com.ibm.rio.youtrack.report.connection;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.rio.youtrack.report.PropertiesHolder;
import com.ibm.rio.youtrack.report.PropertyConstants;

/**
 * @author Zdenek Kratochvil
 *
 */
public class UrlBuilder {

	
	private static final String REGEX = ".*\\((.*)\\)";

	public static String createUrl(String[] args, Map<String,String> fieldsMap) {
		String baseUrl = PropertiesHolder.getProperty(PropertyConstants.URL_BASE);
		String otherUrl = PropertiesHolder.getProperty(PropertyConstants.URL_OTHER);
		String criteria;
		String fieldsWith;
		if(args.length > 2) {
			System.out.println("Maximum number of arguments is 2. First represents query for youtrack report, second enumerates fields for each issue");
			System.out.println("Example: 'project: BRA #PROD_INCIDENT HPQC IDS: {No HPQC Id} #Unresolved' 'id,summary,Priority,Type,State'");
			return null;
		} else if(args.length == 2) {
			criteria = createCriteriaEnc(args[0]);
			fieldsWith = createFieldsWith(args[1], fieldsMap);
		} else if (args.length == 1) {
			criteria = createCriteriaEnc(args[0]);
			fieldsWith = createFieldsWith(PropertiesHolder.getProperty(PropertyConstants.URL_FIELDS), fieldsMap);
		} else {
			criteria = createCriteriaEnc(PropertiesHolder.getProperty(PropertyConstants.URL_CRITERIA));
			fieldsWith = createFieldsWith(PropertiesHolder.getProperty(PropertyConstants.URL_FIELDS), fieldsMap);
		}
		
		return baseUrl + criteria + fieldsWith + otherUrl;
	}

	private static String createCriteriaEnc(String arg) {
		try {
			return URLEncoder.encode(arg, "utf8") + "&";
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private static String createFieldsWith(String fieldsProp, Map<String,String> fieldsMap) {
		String fieldsWith;
		String[] split = fieldsProp.split(",");
		StringBuffer fields = new StringBuffer();
		for(String field : split) {
			String fieldName = parseFieldName(field, fieldsMap);
			fields.append("with=").append(fieldName).append("&");
		}
		fieldsWith = fields.toString();
		return fieldsWith;
	}

	private static String parseFieldName(String field, Map<String, String> fieldsMap) {
		Matcher matcher = Pattern.compile(REGEX).matcher(field);
		if(matcher.matches() == false) {
			fieldsMap.put(field, String.class.getCanonicalName());
			return field;
		}
		String fieldName = field.substring(0, field.indexOf('('));
		String clazz = matcher.group(1);
		fieldsMap.put(fieldName, clazz);
		return fieldName;
	}


}
