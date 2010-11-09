package editor;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import editor.animations.FileCopy;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;

public class IconEditor extends JPanel implements ConfigurationEditor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5888582783506951971L;
	
	private static final ParserKeys[] icons = {ParserKeys.icon, ParserKeys.pressedIcon,
										ParserKeys.rolloverIcon, ParserKeys.selectedIcon};
	
	private HashMap<ParserKeys, IconPanel> panels = new HashMap<ParserKeys, IconEditor.IconPanel>();
	
	public IconEditor()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for(ParserKeys key : icons)
		{
			IconPanel panel = new IconPanel(key);
			this.add(panel);
			panels.put(key, panel);
		}
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
		return this;
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
		
		public String getURI() {
			return uri;
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
