import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import jssc.SerialPort;
import jssc.SerialPortException;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SerialManager {
	private static final String FAILED_TO_WRITE_TO_SERIAL_PORT = "Failed to write to  serial port";
	private static final String FAILURE_STR = "FAILURE";
	private static final String SUCCESS_STR = "SUCCESS";
	private static final String UNSTABLE_STR = "UNSTABLE";
	private static final String BUILDING_STR = "BUILDING";
	private static final String FAILED_TO_PARSE_JSON = "Failed to parse json";
	static String SUCCESS = "g";
	static String FAILURE = "r";
	static String BUILDING = "a";
	static String UNSTABLE = "y";
	private static SerialManager instance;
	private static SerialPort serialPort = null;

	private SerialManager() {

	}

	public static SerialManager getInstance() {
		if (instance == null) {
			instance = new SerialManager();
		}
		return instance;
	}

	public void connect(String comPort) {
		if (serialPort == null || !serialPort.isOpened()) {
			serialPort = new SerialPort(comPort);
			try {
				serialPort.openPort();
				serialPort.setParams(SerialPort.BAUDRATE_9600,
						SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);
				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN
						| SerialPort.FLOWCONTROL_RTSCTS_OUT);
			} catch (SerialPortException e) {
				UICustomManager.setStatus("Can't open port " + comPort);
			}
		}
	}

	public void reconnect(String comPort) {
		if (serialPort != null && serialPort.isOpened()) {
			try {
				serialPort.closePort();
			} catch (SerialPortException e) {
				e.printStackTrace();
			}
		}

		connect(comPort);
	}

	public String getStatus(String jenkinsUrl) {
		String jenkinsStream;
		JSONObject buildStatusJson = null;
		InputStream in = null;
		try {
			in = new URL(jenkinsUrl).openStream();
			jenkinsStream = IOUtils.toString(in);
			try {
				JSONParser json = new JSONParser();
				buildStatusJson = (JSONObject) json.parse(jenkinsStream);

			} catch (ParseException e) {
				UICustomManager.setStatus(FAILED_TO_PARSE_JSON);
			}
		} catch (MalformedURLException ex) {
			UICustomManager.setStatus("Url " + jenkinsUrl + " probably wrong");
		} catch (IOException ex) {
			UICustomManager.setStatus("Failed to connect to  " + jenkinsUrl);
		} finally {
			if (in != null) {
				IOUtils.closeQuietly(in);
			}
		}

		Object result = buildStatusJson.get("result");
		if (result == null) {
			result = new String(BUILDING_STR);
		}
		return result.toString();
	}

	public void process(String jenkinsUrl) {
		String status = getStatus(jenkinsUrl + "/lastBuild/api/json");
		System.out.println(status);
		try {
			if (UNSTABLE_STR.equals(status))
				serialPort.writeString(UNSTABLE);
			else if (SUCCESS_STR.equals(status))
				serialPort.writeString(SUCCESS);
			else if (FAILURE_STR.equals(status))
				serialPort.writeString(FAILURE);
			else
				serialPort.writeString(BUILDING);
		} catch (SerialPortException e) {
			UICustomManager.setStatus(FAILED_TO_WRITE_TO_SERIAL_PORT);
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
