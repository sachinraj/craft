package com.configscanner;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class FileModificationListenerTest {
	private static FileModificationListener fileModificationListener;
	private static final String PROPERTY_FILE = "test.properties";
	private static final String KEY1 = "toggle.feature1.enable";
	private static final String KEY2 = "toggle.feature2.enable";

	@Before
	public void startServer() throws IOException {
		initializeProperties();
		fileModificationListener = new FileModificationListener(PROPERTY_FILE);
		fileModificationListener.listenForChanges();
	}

	@Test
	public void testGetStringAfterUpdate() throws InterruptedException, IOException {
		updateProperty();
		assertEquals("N", ConfigManager.getString(KEY2));
		assertEquals("Y", ConfigManager.getString(KEY1));
	}

	@Test
	public void testGetStringAfterDelete() throws InterruptedException, IOException {
		deleteProperty();
		assertNull(ConfigManager.getString(KEY1));
		assertEquals("DEFAULT", ConfigManager.getString(KEY1, "DEFAULT"));
	}

	@After
	public void stopServer() throws IOException {
		fileModificationListener.stopListening();
		Files.delete(Paths.get(PROPERTY_FILE));
	}

	private void initializeProperties() throws IOException {
		FileWriter fileWriter = new FileWriter(PROPERTY_FILE);
		Properties properties = new Properties();
		properties.setProperty(KEY1, "N");
		properties.setProperty(KEY2, "N");
		properties.store(fileWriter, "Initializing properties");
		fileWriter.close();
	}

	private void updateProperty() throws IOException {
		FileReader fileReader = new FileReader(PROPERTY_FILE);
		Properties properties = new Properties();
		properties.load(fileReader);
		fileReader.close();
		FileWriter fileWriter = new FileWriter(PROPERTY_FILE);
		properties.replace(KEY1, "Y");
		properties.store(fileWriter, "Updating properties");
		fileWriter.close();
		fileWriter = null;
	}

	private void deleteProperty() throws IOException {
		Properties properties = new Properties();
		FileReader fileReader = new FileReader(PROPERTY_FILE);
		properties.load(fileReader);
		fileReader.close();
		FileWriter fileWriter = new FileWriter(PROPERTY_FILE);
		properties.remove(KEY1);
		properties.store(fileWriter, "Updating properties");
		fileWriter.flush();
		fileWriter.close();
	}

}
