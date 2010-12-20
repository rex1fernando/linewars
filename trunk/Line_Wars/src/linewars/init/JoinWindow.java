package linewars.init;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class JoinWindow extends JFrame implements ActionListener{
	private JButton cancelButton;
	private JTextField nameField;
	private JLabel mapLabel;
	private JLabel nameLabel;
	private JTextField serverField;
	private JButton okButton;
	
	private MainWindow parent;
	private GameLobby gameLobby;
	
	public JoinWindow(MainWindow parent, GameLobby lobby) {
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
			if (inputIsValid())
			{
				connectToServer(nameField.getText(), serverField.getText());
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
	
	private boolean inputIsValid()
	{
		// TODO check input for problems
		return true;
	}
	
	private void connectToServer(String name, String serverAddress)
	{
		try {
			gameLobby.startClient(name, serverAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
		gameLobby.setLocationRelativeTo(null);
		setVisible(false);
		gameLobby.setVisible(true);
	}
	
	private void initGUI() {
		try {
			addWindowListener(new CloseAdapter(parent));
			GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
			getContentPane().setLayout(thisLayout);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setTitle("Join A Game");
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
				serverField = new JTextField();
			}
			{
				nameLabel = new JLabel();
				nameLabel.setText("Name:");
			}
			{
				mapLabel = new JLabel();
				mapLabel.setText("Server");
			}
			thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(nameField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
				    .addComponent(nameLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(serverField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
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
				        .addComponent(serverField, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE)
				        .addGap(35))
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addComponent(nameField, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE)
				        .addGap(35))
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addGap(0, 64, GroupLayout.PREFERRED_SIZE)
				        .addComponent(okButton, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
				        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 1, GroupLayout.PREFERRED_SIZE)
				        .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE))));
			thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {serverField, nameField});
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
