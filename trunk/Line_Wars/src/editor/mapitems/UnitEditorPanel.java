package editor.mapitems;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.*;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigData.NoSuchKeyException;
import linewars.configfilehandler.ParserKeys;

import editor.ConfigurationEditor;
import editor.mapitems.StrategySelector.StrategySelectorCallback;
import editor.mapitems.StrategySelector.StrategySelectorFieldType;

/**
 * 
 * @author Connor Schenck
 *
 *  This class represents the panel that allows users
 * to edit the specific values related to only
 * units.
 *
 */
public class UnitEditorPanel extends JPanel implements ConfigurationEditor, ActionListener, StrategySelectorCallback {

	//variable for storing the max hp
	private JTextField maxHP;
	
	//variables related to the combat strat
	private ConfigData combatConfig;
	private JButton combatButton;
	private JLabel combatStatus;
	
	//variables related to the movement strat
	private ConfigData movConfig;
	private JButton movButton;
	private JLabel movStatus;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8788172270105836095L;
	
	public UnitEditorPanel()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//set up the hp panel
		maxHP = new JTextField();
		maxHP.setColumns(20);
		JPanel hpPanel = new JPanel();
		hpPanel.add(new JLabel("Max HP:"));
		hpPanel.add(maxHP);
		
		//set up the combat strat panel
		combatConfig = null;
		combatButton = new JButton("Set the Combat Strategy...");
		combatButton.addActionListener(this);
		combatStatus = new JLabel("Not set");
		JPanel combatPanel = new JPanel();
		combatPanel.add(combatButton);
		combatPanel.add(combatStatus);
		
		
		//set up the mov strat panel
		movConfig = null;
		movButton = new JButton("Set the Movement Strategy...");
		movButton.addActionListener(this);
		movStatus = new JLabel("Not set");
		JPanel movPanel = new JPanel();
		movPanel.add(movButton);
		movPanel.add(movStatus);
		
		//now add them all
		this.add(hpPanel);
		this.add(combatPanel);
		this.add(movPanel);
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
			Double d = cd.getNumber(ParserKeys.maxHP);
			if(d == null)
			{
				if(force)
					maxHP.setText("");
				else
					throw new IllegalArgumentException(ParserKeys.maxHP.toString() + " is not defined");
			}
			maxHP.setText(d.toString());
		} catch(NoSuchKeyException e) {
			if(force)
				maxHP.setText("");
			else
				throw new IllegalArgumentException(ParserKeys.maxHP.toString() + " is not defined");
		}
		
		try {
			ConfigData d = cd.getConfig(ParserKeys.combatStrategy);
			if(d == null)
			{
				if(force)
					combatConfig = null;
				else
					throw new IllegalArgumentException(ParserKeys.combatStrategy.toString() + " is not defined");
			}
			combatConfig = d;
			combatStatus.setText("Set");
		} catch(NoSuchKeyException e) {
			if(force)
				combatConfig = null;
			else
				throw new IllegalArgumentException(ParserKeys.combatStrategy.toString() + " is not defined");
		}
		
		try {
			ConfigData d = cd.getConfig(ParserKeys.movementStrategy);
			if(d == null)
			{
				if(force)
					movConfig = null;
				else
					throw new IllegalArgumentException(ParserKeys.movementStrategy.toString() + " is not defined");
			}
			movConfig = d;
			movStatus.setText("Set");
		} catch(NoSuchKeyException e) {
			if(force)
				movConfig = null;
			else
				throw new IllegalArgumentException(ParserKeys.combatStrategy.toString() + " is not defined");
		}
		
		this.validate();
		this.updateUI();
	}

	@Override
	public void reset() {
		maxHP.setText("");
		combatConfig = null;
		combatStatus.setText("Not Set");
		movConfig = null;
		movStatus.setText("Not Set");
	}

	@Override
	public ConfigData getData() {
		ConfigData cd = new ConfigData();
		Scanner s = new Scanner(maxHP.getText());
		if(s.hasNextDouble())
			cd.set(ParserKeys.maxHP, s.nextDouble());
		else
			cd.set(ParserKeys.maxHP, -1.0);
		if(combatConfig != null)
			cd.set(ParserKeys.combatStrategy, combatConfig);
		if(movConfig != null)
			cd.set(ParserKeys.movementStrategy, movConfig);
		return cd;
	}

	@Override
	public ParserKeys getType() {
		return ParserKeys.unitURI;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(combatButton))
		{
			Map<String, Map<ParserKeys, StrategySelectorFieldType>> fieldMap = 
				new HashMap<String, Map<ParserKeys,StrategySelectorFieldType>>();
			Map<ParserKeys, StrategySelectorFieldType> field = new HashMap<ParserKeys, StrategySelector.StrategySelectorFieldType>();
			
			//make the shoot closest target combat
			field.put(ParserKeys.shootCoolDown, StrategySelectorFieldType.numeric);
			fieldMap.put("ShootClosestTarget", field);
			
			//make the no combat strat
			field = new HashMap<ParserKeys, StrategySelector.StrategySelectorFieldType>();
			fieldMap.put("NoCombat", field);
			
			StrategySelector ss = new StrategySelector(this, "Combat", fieldMap);
			if(combatConfig != null)
				ss.setData(combatConfig);
		}
		if(e.getSource().equals(movButton))
		{
			Map<String, Map<ParserKeys, StrategySelectorFieldType>> fieldMap = 
				new HashMap<String, Map<ParserKeys,StrategySelectorFieldType>>();
			Map<ParserKeys, StrategySelectorFieldType> field = new HashMap<ParserKeys, StrategySelector.StrategySelectorFieldType>();
			
			//make the straight mov strat
			field.put(ParserKeys.speed, StrategySelectorFieldType.numeric);
			fieldMap.put("Straight", field);
			
			//make the immovable mov strat
			field = new HashMap<ParserKeys, StrategySelector.StrategySelectorFieldType>();
			fieldMap.put("Immovable", field);
			
			StrategySelector ss = new StrategySelector(this, "Movement", fieldMap);
			if(movConfig != null)
				ss.setData(movConfig);
		}
	}

	@Override
	public void setConfigForStrategy(StrategySelector caller, ConfigData cd) {
		if(caller.getTitle().equalsIgnoreCase("Combat"))
		{
			combatConfig = cd;
			combatStatus.setText("Set");
		}
		else if(caller.getTitle().equalsIgnoreCase("Movement"))
		{
			movConfig = cd;
			movStatus.setText("Set");
		}
	}

	@Override
	public boolean isValidConfig() {
		Scanner s = new Scanner(maxHP.getText());
		if(!s.hasNextDouble())
			return false;
		
		if(combatConfig == null)
			return false;
		
		if(movConfig == null)
			return false;
		
		return true;
	}

}
