/**
 * 
 */
package com.ibm.rio.youtrack.report.convertor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.rio.youtrack.report.PropertiesHolder;
import com.ibm.rio.youtrack.report.PropertyConstants;
import com.ibm.rio.youtrack.report.formatter.DateValueFormatter;
import com.ibm.rio.youtrack.report.formatter.IValueFormatter;

/**
 * @author Zdenek Kratochvil
 *
 */
public class XML2XLSConvertor {
	
	private static final String FILE_SUFFIX = ".xls";
	
	private final String filepath;
	private final Map<String,String> columnsMap;
	private final List<String> columnNames;
	private final Set<IValueFormatter> valueFormatters;
	
	public XML2XLSConvertor(String filepath, Map<String,String> columnsMap) {
		this.filepath = filepath;
		this.columnsMap = Collections.unmodifiableMap(columnsMap);
		this.columnNames = new LinkedList<String>(columnsMap.keySet());
		
		this.valueFormatters = new LinkedHashSet<IValueFormatter>();
		this.valueFormatters.add(new DateValueFormatter());
	}

	public void convert(InputStream is) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet();
		
		createHeader(sheet);
		
		NodeList nodeList = doc.getElementsByTagName("issue");
		for(int index = 0; index < nodeList.getLength(); index++) {
			Node node = nodeList.item(index);
			if(node instanceof Element) {
				Element el = (Element) node;
				createSheetRow(sheet, index + 1, el);
			}
		}
		
		String filename = createFilename(filepath);
		FileOutputStream fos = new FileOutputStream(filename);
		try {
			wb.write(fos);
		} finally {
			fos.close();
		}
	}

	private String createFilename(String rawFilepath) {
		String fileDatePattern = PropertiesHolder.getProperty(PropertyConstants.FILE_DATE_PATTERN);
		String fileDateSuffix = new SimpleDateFormat(fileDatePattern).format(Calendar.getInstance().getTime());
		return rawFilepath + fileDateSuffix + FILE_SUFFIX;
	}

	private void createSheetRow(Sheet sheet, int rowIndex, Element el) {
		Row row = sheet.createRow(rowIndex);
		
		if(columnNames.contains("id")) {
			createCell(row, "id", el.getAttribute("id"));
		}
		
		for(int cellIndex = 0; cellIndex < el.getChildNodes().getLength(); cellIndex++) {
			Node node = el.getChildNodes().item(cellIndex);
			Element child = (Element) node;
			String value = parseIssueValue(child);
			createCell(row, child.getAttribute("name"), value);
		}
	}

	private String parseIssueValue(Element child) {
		return child.getFirstChild().getTextContent();
	}

	private void createCell(Row row, String name, String value) {
		int cellIndex = columnNames.indexOf(name);
		Cell cell = row.createCell(cellIndex);
		
		String clazz = columnsMap.get(name);
		
		value = formatValue(value, clazz);
		
		cell.setCellValue(value);
		if(value != null) {
			int columnWidth = row.getSheet().getColumnWidth(cellIndex);
			//length unit is in 1/256 of character. +2 for borders without slider
			int textWidth = (value.length() + 2) * 256;
			if(columnWidth < textWidth) {
				row.getSheet().setColumnWidth(cellIndex, textWidth);
			}
		}
	}

	private String formatValue(String value, String clazz) {
		for(IValueFormatter formatter : valueFormatters) {
			if(formatter.canHandle(clazz)) {
				return formatter.format(value);
			}
		}
		return value;
	}

	private void createHeader(Sheet sheet) {
		Row headerRow = sheet.createRow(0);
		
		for(String name : columnNames) {
			Cell cell = headerRow.createCell(headerRow.getLastCellNum() != -1 ? headerRow.getLastCellNum() : 0);
			cell.setCellValue(name);
		}
	}

}
