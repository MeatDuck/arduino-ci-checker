import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class UICustomManager {
	private static final String IMG = "img/jenkinsLogo.png";
	public static final Image IMAGE = Toolkit.getDefaultToolkit().getImage(IMG);
	private static final String INITIAL_STATUS = "Starting...";
	static private TrayIcon trayIcon;
	static private SettingsFrame settings = new SettingsFrame();

	static void prepareUI() {
		SystemTray tray;
		try {
			System.out.println("setting look and feel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Unable to set LookAndFeel");
		}
		if (SystemTray.isSupported()) {
			System.out.println("system tray supported");
			tray = SystemTray.getSystemTray();

			PopupMenu popup = new PopupMenu();
			MenuItem settingsltItem = new MenuItem("Settings");
			settingsltItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					settings.setVisible(true);
				}
			});
			popup.add(settingsltItem);

			MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("Exiting....");
					System.exit(0);
				}
			});
			popup.add(exitItem);

			trayIcon = new TrayIcon(IMAGE, INITIAL_STATUS, popup);
			trayIcon.setImageAutoSize(true);
			try {
				tray.add(trayIcon);
				System.out.println("added to SystemTray");
			} catch (AWTException ex) {
				System.out.println("unable to add to tray");
			}
		} else {
			System.out.println("system tray not supported");
		}
	}

	public static String getStatus() {
		if (trayIcon != null) {
			return trayIcon.getToolTip();
		}
		return "";
	}

	public static void setStatus(String status) {
		if (trayIcon != null) {
			trayIcon.setToolTip(status);
		}
	}
}
