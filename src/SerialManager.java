import org.apache.log4j.Logger;

import jssc.SerialPort;
import jssc.SerialPortException;

public class SerialManager {
	private static final String FAILED_TO_PARSE_STATUS = "Failed to parse status";
	Logger logger = Logger.getLogger(SerialManager.class);
	private static final String FAILED_TO_WRITE_TO_SERIAL_PORT = "Failed to write to  serial port";
	private static final String FAILURE_STR = "FAILURE";
	private static final String SUCCESS_STR = "SUCCESS";
	private static final String UNSTABLE_STR = "UNSTABLE";

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
				logger.debug(e);
			}
		}
	}

	public void reconnect(String comPort) {
		if (serialPort != null && serialPort.isOpened()) {
			try {
				serialPort.closePort();
			} catch (SerialPortException e) {
				e.printStackTrace();
				logger.debug(e);
			}
		}

		connect(comPort);
	}

	public void process(String jenkinsUrl) {
		NetManager network = new NetManager();

		try {
			String status = network.getStatus(jenkinsUrl + "/lastBuild/api/json");
			logger.info(status);
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
			logger.debug(e);
		} catch (Exception e) {
			UICustomManager.setStatus(e.getMessage());
			logger.debug(e);
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.debug(e);
		}

	}
}
