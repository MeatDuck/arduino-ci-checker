import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import jssc.SerialPortList;

public class SettingsFrame extends JFrame {
	private static final long serialVersionUID = -3098199046429878039L;
	private static final int HEIGHT = 30;
	private static final int WIDTH = 500;
	private static final String SETTINGS = "Settings";
	final JButton okBtn = new JButton("OK");
	final Box vBox = Box.createVerticalBox();
	private String[] portNames = SerialPortList.getPortNames();

	public SettingsFrame() {
		super(SETTINGS);
		this.setSize(WIDTH, 500);
		this.setResizable(false);

		String url = SettingsManager.getInstance().getParam("url");
		String port = SettingsManager.getInstance().getParam("port");

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

		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(500, 80));

		okBtn.setSize(WIDTH, HEIGHT);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("url", input.getText());
				if (list.getSelectedValue() != null) {
					map.put("port", list.getSelectedValue().toString());
				}
				SettingsManager.getInstance().setParam(map);
				setVisible(false);
				Starter.getThread().setStop(true);
				try {
					Starter.getThread().join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Starter.initThread().start();
			}
		});

		vBox.add(input);
		vBox.add(listScroller);
		vBox.add(okBtn);
		setContentPane(vBox);
		pack();
	}
	
	public void setVisible(Boolean isVisible) {
		portNames = SerialPortList.getPortNames();
		super.setVisible(isVisible);
	}
}
