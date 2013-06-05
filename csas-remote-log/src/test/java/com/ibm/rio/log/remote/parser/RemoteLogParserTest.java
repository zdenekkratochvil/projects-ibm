package com.ibm.rio.log.remote.parser;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.rio.log.remote.AbstractLogTest;
import com.ibm.rio.log.remote.parsers.RemoteLogFileParser;


public class RemoteLogParserTest extends AbstractLogTest {

	@Autowired
	private RemoteLogFileParser parser;

	@Test
	public void parse() throws FileNotFoundException, IOException {
		parser.parse(FILENAME);
	}
}
