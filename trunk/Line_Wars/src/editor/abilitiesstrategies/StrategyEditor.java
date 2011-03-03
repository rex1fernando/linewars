package editor.abilitiesstrategies;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import linewars.gamestate.mapItems.strategies.Strategy;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategyConfiguration;
import linewars.gamestate.mapItems.strategies.combat.CombatStrategyConfiguration;
import linewars.gamestate.mapItems.strategies.impact.ImpactStrategyConfiguration;
import linewars.gamestate.mapItems.strategies.movement.MovementStrategyConfiguration;
import linewars.gamestate.mapItems.strategies.turret.TurretStrategyConfiguration;
import utility.ForceLoadPackage;
import configuration.Configuration;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;

public class StrategyEditor extends JPanel implements ConfigurationEditor {
	
	static {
		ForceLoadPackage.forceLoadClassesInPackage(Strategy.class.getPackage());
	}

	private ConfigurationEditor realEditor;
	private BigFrameworkGuy bfg;
	
	public StrategyEditor(BigFrameworkGuy bfg)
	{
		this.bfg = bfg;
		this.setLayout(new BorderLayout());
	}
	
	@Override
	public void setData(Configuration cd) {
		for(String name : StrategyConfiguration.getStrategyNameSet())
		{
			if(StrategyConfiguration.getConfig(name).equals(cd.getClass()))
			{
				try {
					realEditor = StrategyConfiguration
							.getEditor(name)
							.getConstructor(BigFrameworkGuy.class, Class.class)
							.newInstance(bfg, StrategyConfiguration.getConfig(name));
					realEditor.setData(cd);
					
					this.removeAll();
					this.add(realEditor.getPanel(), BorderLayout.CENTER);
					
					this.validate();
					this.updateUI();
					break;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
				
			}
		}

	}

	@Override
	public Configuration instantiateNewConfiguration() {
		String type = (String) JOptionPane.showInputDialog(this,
				"Please select which type of strategy you would like to create",
				"Strategy Selection", JOptionPane.PLAIN_MESSAGE, null,
				StrategyConfiguration.getStrategyTypeNameSet().toArray(new String[0]),
				null);
		
		List<String> strategyList = new ArrayList<String>();
		Class<? extends StrategyConfiguration<?>> stratType = StrategyConfiguration.getConfigType(type);
		for(String strat : StrategyConfiguration.getStrategyNameSet())
			if(stratType.isAssignableFrom(StrategyConfiguration.getConfig(strat)))
				strategyList.add(strat);
		
		String name = (String) JOptionPane.showInputDialog(this,
				"Please select which strategy you would like to create",
				"Strategy Selection", JOptionPane.PLAIN_MESSAGE, null,
				strategyList.toArray(new String[0]),
				null);
		
		try {
			realEditor = StrategyConfiguration
						.getEditor(name)
						.getConstructor(BigFrameworkGuy.class, Class.class)
						.newInstance(bfg, StrategyConfiguration.getConfig(name));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		this.removeAll();
		this.add(realEditor.getPanel(), BorderLayout.CENTER);
		this.validate();
		this.updateUI();
		
		return realEditor.instantiateNewConfiguration();
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		realEditor.getData(toSet);
		return getType(toSet);
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		List<ConfigType> ret = new ArrayList<ConfigType>();
		ret.add(ConfigType.collisionStrategy);
		ret.add(ConfigType.combatStrategy);
		ret.add(ConfigType.impactStrategy);
		ret.add(ConfigType.movementStrategy);
		ret.add(ConfigType.turretStrategy);
		return ret;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}
	
	private ConfigType getType(Configuration config)
	{
		if(config instanceof CollisionStrategyConfiguration)
			return ConfigType.collisionStrategy;
		else if(config instanceof CombatStrategyConfiguration)
			return ConfigType.combatStrategy;
		else if(config instanceof ImpactStrategyConfiguration)
			return ConfigType.impactStrategy;
		else if(config instanceof MovementStrategyConfiguration)
			return ConfigType.movementStrategy;
		else if(config instanceof TurretStrategyConfiguration)
			return ConfigType.turretStrategy;
		else
			return null;
	}

}
