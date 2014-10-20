import jssc.SerialPortException;

public class StatusThread extends Thread {
	private boolean isStop = false;

	@Override
	public void run() {
		String port = SettingsManager.getInstance().getParam("port");
		String url = SettingsManager.getInstance().getParam("url");
		if (url != "" && port != "") {
			try {
				while (!isStop()) {
					SerialManager.getInstance(port).process(url);
				}
				UICustomManager.setStatus("Checking...");
			} catch (SerialPortException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private boolean isStop() {
		return isStop;
	}

	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}
}
