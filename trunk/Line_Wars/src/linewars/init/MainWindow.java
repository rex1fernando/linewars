package linewars.init;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import linewars.configfilehandler.ConfigFileReader;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.configfilehandler.ParserKeys;


public class MainWindow extends javax.swing.JFrame implements ActionListener {
	private JButton hostButton;
	private JButton joinButton;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainWindow inst = new MainWindow();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	private JFrame hostWindow;
	private JFrame joinWindow;
	private GameLobby gameLobby;
	
	public MainWindow() {
		super();
		initGUI();
		
		gameLobby = new GameLobby(this);
		hostWindow = new HostWindow(this, gameLobby);
		joinWindow = new JoinWindow(this, gameLobby);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		if (src == hostButton)
		{
			setVisible(false);
			hostWindow.setVisible(true);
		}
		else if (src == joinButton)
		{
			setVisible(false);
			joinWindow.setVisible(true);
		}
	}
	
	@Override
	public void dispose()
	{
		hostWindow.dispose();
		joinWindow.dispose();
		gameLobby.dispose();
		
		super.dispose();
	}
	
	public String[] getMaps()
	{
		try {
			return (new ConfigFileReader("resources/masterList.cfg").read().getStringList(ParserKeys.mapURI)).toArray(new String[0]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidConfigFileException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String[] getRaces()
	{
		try {
			return (new ConfigFileReader("resources/masterList.cfg").read().getStringList(ParserKeys.raceURI)).toArray(new String[0]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidConfigFileException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void initGUI() {
		try {
			addWindowListener(new CloseAdapter(this));
			GridLayout thisLayout = new GridLayout(1, 2);
			thisLayout.setHgap(5);
			thisLayout.setVgap(5);
			thisLayout.setColumns(1);
			getContentPane().setLayout(thisLayout);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setTitle("Line Wars Starter");
			{
				hostButton = new JButton();
				getContentPane().add(hostButton);
				hostButton.setText("Host A Game");
				hostButton.addActionListener(this);
			}
			{
				joinButton = new JButton();
				getContentPane().add(joinButton);
				joinButton.setText("Join A Game");
				joinButton.addActionListener(this);
			}
			pack();
			this.setSize(337, 81);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
}
