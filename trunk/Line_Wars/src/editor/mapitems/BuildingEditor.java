package editor.mapitems;

import java.util.Scanner;

import javax.swing.*;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigData.NoSuchKeyException;
import linewars.configfilehandler.ParserKeys;

import editor.ConfigurationEditor;

public class BuildingEditor extends JPanel implements ConfigurationEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4874979209656216145L;
	
	//variable for holding the cost
	private JTextField cost;
	
	//variable for holding the build time
	private JTextField buildTime;
	
	public BuildingEditor()
	{	
		//set up the cost panel
		cost = new JTextField();
		cost.setColumns(10);
		JPanel costPanel = new JPanel();
		costPanel.add(new JLabel("Cost:"));
		costPanel.add(cost);
		
		//set up the build time panel
		buildTime = new JTextField();
		buildTime.setColumns(10);
		JPanel buildTimePanel = new JPanel();
		buildTimePanel.add(new JLabel("Build Time (ms):"));
		buildTimePanel.add(buildTime);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(costPanel);
		this.add(buildTimePanel);
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
		try {
			Double d = cd.getNumber(ParserKeys.cost);
			if(d != null)
				cost.setText(d.toString());
			else if(!force)
				throw new IllegalArgumentException("Cost not defined");
		} catch(NoSuchKeyException e) {
			if(!force)
				throw new IllegalArgumentException("Cost not defined");
		}
		
		try {
			Double d = cd.getNumber(ParserKeys.buildTime);
			if(d != null)
				buildTime.setText(d.toString());
			else if(!force)
				throw new IllegalArgumentException("Build time not defined");
		} catch(NoSuchKeyException e) {
			if(!force)
				throw new IllegalArgumentException("Build Time not defined");
		}
	}

	@Override
	public void reset() {
		cost.setText("");
		buildTime.setText("");
	}

	@Override
	public ConfigData getData() {
		Scanner s = new Scanner(cost.getText());
		return null; //TODO
	}

	@Override
	public ParserKeys getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

}
