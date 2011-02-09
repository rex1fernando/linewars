package editor.mapitems;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.*;

import configuration.Configuration;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import linewars.configfilehandler.ConfigData.NoSuchKeyException;

import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;
import editor.abilities.StrategySelector;
import editor.abilities.StrategySelector.StrategySelectorCallback;
import editor.abilities.StrategySelector.StrategySelectorFieldType;

/**
 * 
 * @author Connor Schenck
 *
 *  This class represents the panel that allows users
 * to edit the specific values related to only
 * projectiles.
 *
 */
public class ProjectileEditor extends JPanel implements ConfigurationEditor, ActionListener, StrategySelectorCallback {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8198606751190360416L;
	
	//variable associated with the velocity
	private JTextField velocity;
	
	//variables associated with the impact strategy
	private JButton impactButton;
	private ConfigData impactData;
	private JLabel impactStatus;
	
	public ProjectileEditor()
	{
		//set up the velocity panel
		velocity = new JTextField();
		velocity.setColumns(20);
		JPanel velPanel = new JPanel();
		velPanel.add(new JLabel("Velocity:"));
		velPanel.add(velocity);
		
		//set up the impact strat panel
		impactButton = new JButton("Set Impact Strategy");
		impactButton.addActionListener(this);
		impactData = null;
		impactStatus = new JLabel("Not Set");
		JPanel impactPanel = new JPanel();
		impactPanel.add(impactButton);
		impactPanel.add(impactStatus);
		
		//set up this panel
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(velPanel);
		this.add(impactPanel);
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
		try {
			Double d = cd.getNumber(ParserKeys.velocity);
			if(d != null)
				velocity.setText(d.toString());
			else if(force)
				velocity.setText("");
			else
				throw new IllegalArgumentException("Velocity is not defined");
		} catch(NoSuchKeyException e) {
			if(force)
				velocity.setText("");
			else
				throw new IllegalArgumentException("Velocity is not defined");
		}
		
		try {
			ConfigData d = cd.getConfig(ParserKeys.impactStrategy);
			if(d != null)
			{
				impactData = d;
				impactStatus.setText("Set");
			}
			else if(force)
			{
				impactData = null;
				impactStatus.setText("Not Set");
			}
			else
				throw new IllegalArgumentException("Impact Strategy is not defined");
		} catch(NoSuchKeyException e) {
			if(force)
			{
				impactData = null;
				impactStatus.setText("Not Set");
			}
			else
				throw new IllegalArgumentException("Impact Strategy is not defined");
		}
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		velocity.setText("");
		impactData = null;
		impactStatus.setText("Not Set");
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		ConfigData cd = new ConfigData();
		
		Scanner s = new Scanner(velocity.getText());
		if(s.hasNextDouble())
			cd.set(ParserKeys.velocity, s.nextDouble());
		else
			cd.set(ParserKeys.velocity, -1.0);
		
		if(impactData != null)
			cd.set(ParserKeys.impactStrategy, impactData);
		
		return cd;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		return ParserKeys.projectileURI;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(impactButton))
		{
			Map<String, Map<ParserKeys, StrategySelectorFieldType>> fieldMap = 
				new HashMap<String, Map<ParserKeys,StrategySelectorFieldType>>();
			Map<ParserKeys, StrategySelectorFieldType> field = new HashMap<ParserKeys, StrategySelector.StrategySelectorFieldType>();
			
			//make the shoot closest target combat
			field.put(ParserKeys.damage, StrategySelectorFieldType.numeric);
			fieldMap.put("DealDamageOnce", field);
			
			StrategySelector ss = new StrategySelector(this, "Impact", fieldMap);
			if(impactData != null)
				ss.setData(impactData);
		}
		
	}


	@Override
	public void setConfigForStrategy(StrategySelector caller, ConfigData cd) {
		if(caller.getTitle().equalsIgnoreCase("Impact"))
		{
			impactData = cd;
			impactStatus.setText("Set");
		}
		
		this.validate();
		this.updateUI();
	}


	@Override
	public boolean isValidConfig() {
		Scanner s = new Scanner(velocity.getText());
		if(!s.hasNextDouble())
			return false;
		
		if(impactData == null)
			return false;
		
		return true;
	}

}
