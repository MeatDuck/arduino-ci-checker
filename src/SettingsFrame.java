import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;

import org.apache.commons.codec.binary.Base64;

import jssc.SerialPortList;

public class SettingsFrame extends JFrame {
	private static final long serialVersionUID = -3098199046429878039L;
	private static final int HEIGHT = 30;
	private static final int WIDTH = 500;
	private static final String SETTINGS = "Settings";
	private static final String URL = "url";
	private static final String PORT = "port";
	final JButton okBtn = new JButton("OK");
	final Box vBox = Box.createVerticalBox();
	private String[] portNames = SerialPortList.getPortNames();

	public SettingsFrame() {
		super(SETTINGS);
		this.setSize(WIDTH, 500);
		this.setResizable(false);

		String url = SettingsManager.getInstance().getParam(URL);
		String port = SettingsManager.getInstance().getParam(PORT);

		final JTextField input = new JTextField(url);
		input.setText(url);

		final JList list = new JList(portNames);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		if (port != "") {
			for (int i = 0; i < portNames.length; i++) {
				if (portNames[i].equals(port)) {
					list.setSelectedIndex(i);
				}
			}
		}

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();
		cs.fill = GridBagConstraints.HORIZONTAL;

		JLabel lbUsername = new JLabel("Username: ");
		cs.gridx = 0;
		cs.gridy = 0;
		cs.gridwidth = 1;
		panel.add(lbUsername, cs);

		final JTextField tfUsername = new JTextField(20);
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		panel.add(tfUsername, cs);

		JLabel lbPassword = new JLabel("Password: ");
		cs.gridx = 0;
		cs.gridy = 1;
		cs.gridwidth = 1;
		panel.add(lbPassword, cs);

		final JPasswordField pfPassword = new JPasswordField(20);
		cs.gridx = 1;
		cs.gridy = 1;
		cs.gridwidth = 2;
		panel.add(pfPassword, cs);
		panel.setBorder(new LineBorder(Color.GRAY));

		JLabel urlL = new JLabel("URL: ");
		cs.gridx = 0;
		cs.gridy = 2;
		cs.gridwidth = 1;
		panel.add(urlL, cs);

		cs.gridx = 1;
		cs.gridy = 2;
		cs.gridwidth = 2;
		panel.add(input, cs);

		JLabel coms = new JLabel("COM ports: ");
		cs.gridx = 0;
		cs.gridy = 3;
		cs.gridwidth = 1;
		panel.add(coms, cs);

		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(500, 80));
		cs.gridx = 1;
		cs.gridy = 3;
		cs.gridwidth = 2;
		panel.add(listScroller, cs);

		okBtn.setSize(WIDTH, HEIGHT);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Map<String, String> map = new HashMap<String, String>();
				map.put(URL, input.getText());
				if (list.getSelectedValue() != null) {
					map.put(PORT, list.getSelectedValue().toString());
				}

				if (!"".equals(tfUsername.getText())
						&& pfPassword.getPassword().length > 0) {
					String authString = tfUsername.getText() + ":"
							+ String.valueOf(pfPassword.getPassword());
					byte[] authEncBytes = Base64.encodeBase64(authString
							.getBytes());
					String authStringEnc = new String(authEncBytes);
					map.put("encoded", authStringEnc);
				}

				map.put(URL, input.getText());
				SettingsManager.getInstance().setParam(map);
				setVisible(false);
				Starter.getThread().setStop(true);
				try {
					Starter.getThread().join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				String port = SettingsManager.getInstance().getParam(PORT);
				SerialManager.getInstance().reconnect(port);

				Starter.initThread().start();
			}
		});

		vBox.add(panel, BorderLayout.EAST);
		vBox.add(okBtn);
		setContentPane(vBox);
		pack();
	}

	public void setVisible(Boolean isVisible) {
		portNames = SerialPortList.getPortNames();
		super.setVisible(isVisible);
	}
}
