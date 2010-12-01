package editor;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import editor.animations.FileCopy;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;

/**
 * 
 * @author Connor Schenck
 *
 *This class represents a panel that allows icons to be
 *selected and previewed. Adding this panel to any other
 *panel will present the user with a list of buttons,
 *and when a valid icon is selected, it will preview that
 *icon next to the button. This panel contains itself in
 *a scroll pain of size (200, 175).
 */
public class IconEditor extends JPanel implements ConfigurationEditor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5888582783506951971L;
	
	private static final ParserKeys[] icons = {ParserKeys.icon, ParserKeys.pressedIcon,
										ParserKeys.rolloverIcon, ParserKeys.selectedIcon};
	
	private HashMap<ParserKeys, IconPanel> panels = new HashMap<ParserKeys, IconEditor.IconPanel>();
	
	private JPanel mainPanel;
	
	/**
	 * Creates the Icon Editor. Does not display it in any way. The
	 * panel that this editor will be on is responsible for adding
	 * it as a component.
	 */
	public IconEditor()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for(ParserKeys key : icons)
		{
			IconPanel panel = new IconPanel(key);
			this.add(panel);
			panels.put(key, panel);
		}
		
		JScrollPane scroller = new JScrollPane(this);
		scroller.setPreferredSize(new Dimension(200, 175));
		mainPanel = new JPanel();
		mainPanel.add(scroller);
	}

	@Override
	public void setData(ConfigData cd) {
		setData(cd, false);
	}

	@Override
	public void forceSetData(ConfigData cd) {
		setData(cd, true);
	}
	
	private void setData(ConfigData cd, boolean force)
	{
		for(ParserKeys key : icons)
		{
			if(cd.getDefinedKeys().contains(key))
			{
				try {
					panels.get(key).setURI(cd.getString(key));
				} catch(Exception e) {
					if(force)
						panels.get(key).clearIcon();
					else
						throw new IllegalArgumentException(key + " is not valid");
				}
			}
			else if(force)
				panels.get(key).clearIcon();
			else
				throw new IllegalArgumentException(key + " is not valid");				
		}
		
		this.validate();
		this.updateUI();
	}
	
	/**
	 * This method takes in a ConfigData object and checks to
	 * see if all the keys needed for icons are present and
	 * if each key maps to a valid URI that represents an icon
	 * image.
	 * 
	 * @param cd	the config data object to check
	 * @return		true if all the keys and values are correct; false otherwise
	 */
	public boolean isValid(ConfigData cd)
	{
		for(ParserKeys key : icons)
		{
			if(cd.getDefinedKeys().contains(key))
			{
				try {
					panels.get(key).testURI(cd.getString(key));
				} catch(Exception e) {
					return false;
				}
			}
			else 
				return false;
		}
		return true;
	}

	@Override
	public void reset() {
		for(ParserKeys key : icons)
			panels.get(key).clearIcon();
	}

	@Override
	public ConfigData getData() {
		ConfigData cd = new ConfigData();
		for(ParserKeys key : icons)
			cd.set(key, panels.get(key).getURI());
		return cd;
	}

	@Override
	public ParserKeys getType() {
		throw new UnsupportedOperationException("Get type doesn't make sense for the icon editor");
	}

	@Override
	public JPanel getPanel() {
		return mainPanel;
	}
	
	private class IconPanel extends JPanel implements ActionListener {
		
		private ParserKeys key;
		private JLabel icon;
		private String uri;
		
		public IconPanel(ParserKeys key)
		{
			this.key = key;
			JButton set = new JButton("Set " + key.toString());
			set.addActionListener(this);
			this.add(set);
		}
		
		public void testURI(String u) {
			ImageIcon i = new ImageIcon(u);
			i = new ImageIcon(i.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
		}

		public String getURI() {
			return "/" + uri;
		}
		
		public ParserKeys getKey() {
			return key;
		}
		
		public void clearIcon() {
			if(icon != null)
				this.remove(icon);
			icon = null;
			this.uri = "";
		}
		
		public void setURI(String u) {
			if(u.charAt(0) == '/')
				u = u.substring(1, u.length());
			
			if(icon != null)
				this.remove(icon);
			ImageIcon i = new ImageIcon(u);
			i = new ImageIcon(i.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
			this.uri = u;
			icon = new JLabel("", i, JLabel.RIGHT);
			this.add(icon);
			this.validate();
			this.updateUI();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser fc = new JFileChooser();
			try {
				Scanner s = new Scanner(new File("lastDirectory.txt"));
				fc = new JFileChooser(s.nextLine());
			} catch (FileNotFoundException e) {	}
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			{
				File f = fc.getSelectedFile();
				
				//test loading the image
				try {
					ImageIcon i = new ImageIcon(f.getAbsolutePath());
					if(i.getImage().getHeight(null) < 0)
						throw new Exception();
				} catch(Exception e) {
					JOptionPane.showMessageDialog(IconEditor.this,
						    "Error loading\n" + f.getAbsolutePath(),
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				File to = new File(new File("resources/animations"), f.getName());
				if(!to.exists())
				{
					try {
						FileCopy.copy(f.getAbsolutePath(), to.getAbsolutePath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				String u = "resources/animations/" + to.getName();
				this.setURI(u);
				
				FileWriter lastDirFile = null;
				try {
					lastDirFile = new FileWriter("lastDirectory.txt");
					lastDirFile.write(f.getParent());
					lastDirFile.flush();
					lastDirFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
		
		
	}

	@Override
	public boolean isValidConfig() {
		for(ParserKeys key : icons)
			if(panels.get(key).getURI().equals(""))
				return false;
		return true;
	}

}
