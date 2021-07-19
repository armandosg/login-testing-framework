package com.example.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

import java.io.IOException;

import com.example.config.Environment;
import lombok.extern.log4j.Log4j2;

import com.example.common.DataFileReader;
import org.testng.annotations.Test;

@Log4j2
public class DataFileReaderTest {

	@Test
	void populateListTestDataTest() {
		DataFileReader dataFileReader1 = DataFileReader.getInstance();
		try {
			dataFileReader1.init(Environment.dataFilePath, Environment.envName);
			dataFileReader1.populateListTestData();
			dataFileReader1.getListTestData().forEach(log::info);
		} catch (IOException e) {
			log.error(e);
		}
	}

	@Test
	void dataFileReaderReturnsTheSameInstance() {
		DataFileReader dataFileReader1 = DataFileReader.getInstance();
		DataFileReader dataFileReader2 = DataFileReader.getInstance();
		assertEquals(dataFileReader1, dataFileReader2);
	}
}
