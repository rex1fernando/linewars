package editor.mapitems;

import java.awt.Dimension;
import java.util.Scanner;

import javax.swing.*;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigData.NoSuchKeyException;
import linewars.configfilehandler.ParserKeys;

import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;
import editor.IconEditor;

/**
 * 
 * @author Connor Schenck
 *
 * This class represents the panel that allows users
 * to edit the specific values related to only
 * buildings.
 */
public class BuildingEditor extends JPanel implements ConfigurationEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4874979209656216145L;
	
	//variable for holding the cost
	private JTextField cost;
	
	//variable for holding the build time
	private JTextField buildTime;
	
	//variables for the icons
	private ConfigurationEditor icons;
	
	/**
	 * Constructs this building editor.
	 */
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
		
		icons = new IconEditor();
		
		JPanel innerPanel = new JPanel();

		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
		innerPanel.add(costPanel);
		innerPanel.add(buildTimePanel);
		innerPanel.add(icons.getPanel());
		
		JScrollPane scroller = new JScrollPane(innerPanel);
		scroller.setPreferredSize(new Dimension(600, 200));
		this.add(scroller);
	}

	@Override
	public void setData(Configuration cd) {
		setData(cd, false);
	}

	@Override
	public void forceSetData(ConfigData cd) {
		setData(cd, true);
	}
	
	private void setData(ConfigData cd, boolean force)
	{
		if(force)
			icons.forceSetData(cd);
		else
			icons.setData(cd);
		
		try {
			Double d = cd.getNumber(ParserKeys.cost);
			if(d != null)
				cost.setText(d.toString());
			else if(!force)
				throw new IllegalArgumentException("Cost not defined");
			else
				cost.setText("");
		} catch(NoSuchKeyException e) {
			if(!force)
				throw new IllegalArgumentException("Cost not defined");
			else
				cost.setText("");
		}
		
		try {
			Double d = cd.getNumber(ParserKeys.buildTime);
			if(d != null)
				buildTime.setText(d.toString());
			else if(!force)
				throw new IllegalArgumentException("Build time not defined");
			else
				buildTime.setText("");
		} catch(NoSuchKeyException e) {
			if(!force)
				throw new IllegalArgumentException("Build Time not defined");
			else
				buildTime.setText("");
		}
	}

	@Override
	public void reset() {
		cost.setText("");
		buildTime.setText("");
		icons.reset();
	}

	@Override
	public Configuration getData() {
		ConfigData cd = icons.getData();
		
		Scanner s = new Scanner(cost.getText());
		if(s.hasNextDouble())
			cd.set(ParserKeys.cost, s.nextDouble());
		else
			cd.set(ParserKeys.cost, -1.0);
		
		s = new Scanner(buildTime.getText());
		if(s.hasNextDouble())
			cd.set(ParserKeys.buildTime, s.nextDouble());
		
		return cd;
	}

	@Override
	public ConfigType getType() {
		return ParserKeys.buildingURI;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

	@Override
	public boolean isValidConfig() {
		if(cost == null)
			return false;
		
		if(!icons.isValidConfig())
			return false;
		
		Scanner s = new Scanner(cost.getText());
		if(!s.hasNextDouble())
			return false;
		
		s = new Scanner(buildTime.getText());
		if(!s.hasNextDouble())
			return false;
		
		return true;
	}

}
