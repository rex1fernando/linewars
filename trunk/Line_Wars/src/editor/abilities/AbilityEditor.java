package editor.abilities;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.JPanel;

import linewars.gamestate.mapItems.abilities.AbilityDefinition;

import configuration.Configuration;

import editor.BigFrameworkGuy;
import editor.ConfigurationEditor;
import editor.BigFrameworkGuy.ConfigType;

public class AbilityEditor extends JPanel implements ConfigurationEditor {

	private ConfigurationEditor realEditor;
	private BigFrameworkGuy bfg;
	
	public AbilityEditor(BigFrameworkGuy bfg)
	{
		this.bfg = bfg;
		this.setLayout(new BorderLayout());
	}
	
	@Override
	public void setData(Configuration cd) {
		for(String name : AbilityDefinition.getAbilityNameSet())
		{
			if(AbilityDefinition.getConfig(name).getClass().equals(cd.getClass()))
			{
				try {
					realEditor = AbilityDefinition
							.getEditor(name)
							.getConstructor(BigFrameworkGuy.class, Class.class)
							.newInstance(bfg, AbilityDefinition.getConfig(name));
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getPanel() {
		// TODO Auto-generated method stub
		return null;
	}

}
