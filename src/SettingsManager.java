import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SettingsManager {
	Logger logger = Logger.getLogger(SettingsManager.class);
	private File configFile = new File("config.properties");
	private static SettingsManager instance;

	private SettingsManager() {
	};

	static public SettingsManager getInstance() {
		if (instance == null) {
			instance = new SettingsManager();
		}
		return instance;
	}

	public String getParam(String paramName) {
		FileReader reader = null;
		String value = "";
		try {
			reader = new FileReader(configFile);
			Properties props = new Properties();
			props.load(reader);
			value = props.getProperty(paramName);
			logger.info("Read from param " + paramName + " = " + value);
			reader.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			logger.debug(ex);
		} catch (IOException ex) {
			ex.printStackTrace();
			logger.debug(ex);
		}
		return value;
	}

	public void setParam(Map<String, String> map) {
		try {
			Properties props = new Properties();
			for (String item : map.keySet()) {
				props.setProperty(item, map.get(item));
				logger.info("Store to param " + item + " = " + map.get(item));
			}			

			FileWriter writer = new FileWriter(configFile);
			props.store(writer, "");
			writer.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			logger.debug(ex);
		} catch (IOException ex) {
			ex.printStackTrace();
			logger.debug(ex);
		}
	}
}
