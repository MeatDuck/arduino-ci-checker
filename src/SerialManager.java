import java.io.InputStream;
import java.net.URL;

import jssc.SerialPort;
import jssc.SerialPortException;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SerialManager {
	static String SUCCESS = "g";
	static String FAILURE = "r";
	static String BUILDING = "a";
	static String UNSTABLE = "y";
	private static SerialManager instance;
	private static SerialPort serialPort = null;

	private SerialManager(String comPort) throws SerialPortException {
		serialPort = new SerialPort(comPort);
		try {
			serialPort.openPort();
			serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | 
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);
		} catch (SerialPortException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}

	public static SerialManager getInstance(String comPort)
			throws SerialPortException {
		if (instance == null) {
			instance = new SerialManager(comPort);
		}
		return instance;
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

			} catch (Exception e) {
				System.out.println("Failed to parse json");
				System.exit(3);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("(job url [" + jenkinsUrl + "] probably wrong)");
		} finally {
			if (in != null) {
				IOUtils.closeQuietly(in);
			}
		}

		Object result = buildStatusJson.get("result");
		if (result == null) {
			result = new String("BUILDING");
		}
		return result.toString();
	}

	public void process(String jenkinsUrl) {
		String status = getStatus(jenkinsUrl + "/lastBuild/api/json");
		System.out.println(status);
		try {
			if ("UNSTABLE".equals(status))
				serialPort.writeString(UNSTABLE);
			else if ("SUCCESS".equals(status))
				serialPort.writeString(SUCCESS);
			else if ("FAILURE".equals(status))
				serialPort.writeString(FAILURE);
			else
				serialPort.writeString(BUILDING);
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
