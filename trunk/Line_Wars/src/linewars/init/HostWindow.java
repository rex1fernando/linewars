package linewars.init;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class HostWindow extends JFrame implements ActionListener{
	private JButton cancelButton;
	private JTextField nameField;
	private JLabel mapLabel;
	private JLabel nameLabel;
	private JComboBox mapBox;
	private JButton okButton;
	
	private MainWindow parent;
	private GameLobby gameLobby;
	
	public HostWindow(MainWindow parent, GameLobby lobby) {
		super();
		this.parent = parent;
		gameLobby = lobby;
		initGUI();
		setLocationRelativeTo(parent);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		if (src == okButton)
		{
			if (isValidInput())
			{
				hostTheGame(nameField.getText(), (String) mapBox.getSelectedItem());
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Invalid input!");
			}
		}
		else if (src == cancelButton)
		{
			setVisible(false);
			parent.setVisible(true);
		}
	}
	
	private boolean isValidInput()
	{
		// TODO check for problems with the input
		return true;
	}
	
	private void hostTheGame(String name, String mapURI)
	{
		mapURI = MapComboBoxModel.PREFIX + mapURI + MapComboBoxModel.SUFFIX;
		
		try {
			gameLobby.startServer(nameField.getText(), mapURI);
		} catch (IOException e) {
			e.printStackTrace();
		}
		gameLobby.setLocationRelativeTo(null);
		setVisible(false);
		gameLobby.setVisible(true);
		
		System.out.println("Hosted!");
	}
	
	private static class MapComboBoxModel extends DefaultComboBoxModel
	{
		// FIXME this assumes that all the maps are stored in the same place and have the same naming scheme
		
		private static final String PREFIX = "resources/maps/";
		private static final String SUFFIX = ".cfg";
		
		public MapComboBoxModel(String[] mapURIs)
		{
			super(uriToName(mapURIs));
		}
		
		private static String[] uriToName(String[] URIs)
		{
			String[] mapNames = new String[URIs.length];
			for (int i = 0; i < URIs.length; ++i)
			{
				mapNames[i] = URIs[i].substring(PREFIX.length(), URIs[i].length() - SUFFIX.length());
			}
			return mapNames;
		}
	}
	
	private void initGUI() {
		try {
			addWindowListener(new CloseAdapter(parent));
			GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
			getContentPane().setLayout(thisLayout);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setTitle("Host A Game");
			{
				cancelButton = new JButton();
				cancelButton.setText("Cancel");
				cancelButton.addActionListener(this);
			}
			{
				okButton = new JButton();
				okButton.setText("OK");
				okButton.addActionListener(this);
			}
			{
				nameField = new JTextField();
			}
			{
				ComboBoxModel mapBoxModel = 
					new MapComboBoxModel(
							parent.getMaps());
				mapBox = new JComboBox();
				mapBox.setModel(mapBoxModel);
			}
			{
				nameLabel = new JLabel();
				nameLabel.setText("Name:");
			}
			{
				mapLabel = new JLabel();
				mapLabel.setText("Map:");
			}
			thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(nameField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
				    .addComponent(nameLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(mapBox, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
				    .addComponent(mapLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(cancelButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
				    .addComponent(okButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
				.addContainerGap());
			thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
				.addContainerGap(51, 51)
				.addGroup(thisLayout.createParallelGroup()
				    .addGroup(thisLayout.createSequentialGroup()
				        .addGap(0, 0, Short.MAX_VALUE)
				        .addComponent(mapLabel, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE))
				    .addGroup(thisLayout.createSequentialGroup()
				        .addComponent(nameLabel, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)
				        .addGap(0, 0, Short.MAX_VALUE)))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(thisLayout.createParallelGroup()
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addComponent(mapBox, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE)
				        .addGap(35))
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addComponent(nameField, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE)
				        .addGap(35))
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addGap(0, 64, GroupLayout.PREFERRED_SIZE)
				        .addComponent(okButton, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
				        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 1, GroupLayout.PREFERRED_SIZE)
				        .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE))));
			thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {mapBox, nameField});
			thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {nameLabel, mapLabel});
			thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {nameLabel, mapLabel});
			thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {cancelButton, okButton});
			pack();
			this.setSize(382, 150);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
}
