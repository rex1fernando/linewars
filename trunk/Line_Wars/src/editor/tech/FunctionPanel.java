package editor.tech;

import javax.swing.JComboBox;
import javax.swing.JDialog;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.Function;

public class FunctionPanel{

	private ConfigData cd;
	private JComboBox typeOptionBox;

	public FunctionPanel()
	{
		String[] typeOptions = {"Polynomial", "Exponential"};
		
		cd = new ConfigData();
		
		
	}
	
}
