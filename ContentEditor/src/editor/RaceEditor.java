package editor;

import javax.swing.JPanel;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;

public class RaceEditor implements ConfigurationEditor
{
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
		public RacePanel()
		{
			
		}
	}
}
