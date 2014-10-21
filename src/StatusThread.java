
public class StatusThread extends Thread {
	private static final String URL = "url";
	private static final String PORT = "port";
	private boolean isStop = false;

	@Override
	public void run() {
		String port = SettingsManager.getInstance().getParam(PORT);
		String url = SettingsManager.getInstance().getParam(URL);
				while (!isStop()) {
					SerialManager.getInstance().connect(port);
					SerialManager.getInstance().process(url);
				}
	}

	private boolean isStop() {
		return isStop;
	}

	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}
}
