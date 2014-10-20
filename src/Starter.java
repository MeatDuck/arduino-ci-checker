
public class Starter {
	private static StatusThread thread;

	public static void main(String[] args) throws InterruptedException {
		UICustomManager.prepareUI();		
		initThread().start();		
	}

	public static StatusThread getThread() {
		return thread;
	}
	
	public static StatusThread initThread() {
		thread = new StatusThread();
		return thread;
	}
}
