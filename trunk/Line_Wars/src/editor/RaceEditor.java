package editor;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import editor.URISelector.SelectorOptions;

public class RaceEditor implements ConfigurationEditor
{
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		f.setContentPane(new RaceEditor(null).getPanel());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}
	
	private BigFrameworkGuy superEditor;
	private RacePanel racePanel;
	private ConfigData configData;

	public RaceEditor(BigFrameworkGuy bfg)
	{
		superEditor = bfg;
		racePanel = new RacePanel();
		reset();	// creates a configData object and updates the GUI
	}

	@Override
	public void setData(ConfigData cd)
	{
		if (isValid(cd))
		{
			forceSetData(cd);
		}
		throw new RuntimeException("The configuration file: " + cd + " is invalid.");
	}

	@Override
	public void forceSetData(ConfigData cd)
	{
		updatePanel(cd);
	}

	@Override
	public void reset()
	{
		configData = new ConfigData();
		updatePanel(configData);
	}

	@Override
	public ConfigData getData()
	{
		return configData;
	}

	@Override
	public boolean isValid()
	{
		configData = createConfigData(racePanel);
		return isValid(configData);
	}

	@Override
	public ParserKeys getType()
	{
		return ParserKeys.raceURI;
	}
	
	private boolean isValid(ConfigData cd)
	{
		// TODO implement
		return false;
	}
	
	private void updatePanel(ConfigData cd)
	{
		// TODO implement
	}
	
	private ConfigData createConfigData(RacePanel rp)
	{
		// TODO implement
		return null;
	}

	@Override
	public JPanel getPanel()
	{
		return racePanel;
	}

	private class RacePanel extends JPanel
	{
		private static final int SPACING = 3;
		
		private NameBox nameBox;
		
		private URISelector commandCenter;
		private URISelector unit;
		private URISelector building;
		private URISelector tech;
		private URISelector gate;
		
		public RacePanel()
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			nameBox = new NameBox();
			add(nameBox);
			
			initURISelectors();
		}
		
		private void initURISelectors()
		{
			commandCenter = new URISelector("Command Center", new SelectorOptions() {
				public String[] getOptions() { return superEditor.getCommandCenterURIs(); }
				public void uriSelected(String uri) {}
			});
			initURISelector(commandCenter);
			
			unit = new URISelector("Unit", new SelectorOptions() {
				public String[] getOptions() { return superEditor.getUnitURIs(); }
				public void uriSelected(String uri) {}
			});
			initURISelector(unit);
			
			building = new URISelector("Building", new SelectorOptions() {
				public String[] getOptions() { return superEditor.getCommandCenterURIs(); }
				public void uriSelected(String uri) {}
			});
			initURISelector(building);
			
			tech = new URISelector("Tech", new SelectorOptions() {
				public String[] getOptions() { return superEditor.getTechURIs(); }
				public void uriSelected(String uri) {}
			});
			initURISelector(tech);
			
			gate = new URISelector("Gate", new SelectorOptions() {
				public String[] getOptions() { return superEditor.getGateURIs(); }
				public void uriSelected(String uri) {}
			});
			initURISelector(gate);
		}
		
		private void initURISelector(JPanel p)
		{
			add(p);
		}
	}
	
	private class NameBox extends JPanel
	{
		private JTextField name;
		
		public NameBox()
		{
			add(new JLabel("Name"));
			add(Box.createHorizontalStrut(5));
			
			name = new JTextField();
			name.setPreferredSize(new Dimension(160, 20));
			add(name);
		}
	}
}
