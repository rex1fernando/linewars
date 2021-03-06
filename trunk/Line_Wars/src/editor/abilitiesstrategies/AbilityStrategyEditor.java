package editor.abilitiesstrategies;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import configuration.Configuration;
import configuration.ListConfiguration;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;

public class AbilityStrategyEditor extends JPanel implements ConfigurationEditor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7151442519676544131L;
	
	private Map<String, Field> fields = new HashMap<String, Field>();
	private BigFrameworkGuy bfg;
	private Class<? extends Configuration> toInstantiate;
	
	public AbilityStrategyEditor(BigFrameworkGuy bfg, Class<? extends Configuration> toInstantiate)
	{
		this.bfg = bfg;
		this.toInstantiate = toInstantiate;
		setData(instantiateNewConfiguration());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setData(Configuration cd) {
		this.removeAll();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		int foundFields = 0;
		for(String field : cd.getPropertyNames())
		{
			//ignore all non-editor properties
			if(!(cd.getPropertyForName(field) instanceof EditorProperty))
				continue;
			
			foundFields++;
			EditorProperty prop = (EditorProperty)cd.getPropertyForName(field);
			Field f = null;
			switch (prop.getEditorUsage())
			{
				case BuildingConfig:
				case ProjectileConfig:
				case TechConfig:
				case UnitConfig:
				case PartConfig:
					f = new ConfigurationField(field, prop.getDescription(), 
							prop.getEditorUsage(), bfg, (Configuration) prop.getValue());
					break;
				case NaturalNumber:
				case PositiveReal:
				case Real:
				case text:
					if(prop.getValue() != null)
						f = new TextField(field, prop.getDescription(), 
								prop.getEditorUsage(), prop.getValue().toString());
					else
						f = new TextField(field, prop.getDescription(), 
								prop.getEditorUsage());
					break;
				case ListBuildingConfig:
				case ListUnitConfig:
				case ListProjectileConfig:
				case ListTechConfig:
				case ListImpactConfiguration:
					f = new ListConfigurationField(field, prop.getDescription(),
							prop.getEditorUsage(), bfg, (ListConfiguration<? extends Configuration>) prop.getValue());
					break;
				case IconConfig:
					f = new IconConfigurationField(field, prop.getDescription(), (Configuration) prop.getValue());
					break;
				case Boolean:
					f = new BooleanField(field, prop.getDescription(), (Boolean) prop.getValue());
					break;
				default:
					throw new IllegalArgumentException(prop.getEditorUsage().toString() + " is not supported by this editor");
			}
			
			this.add(f);
			fields.put(field, f);
		}
		
		this.add(new JLabel(foundFields + " configurable fields"));
		
		this.validate();
		this.updateUI();
	}
	
	public void resetEditor()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		Configuration c = null;
		try {
			c = toInstantiate.getConstructor().newInstance();
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
		return c;
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		for(Entry<String, Field> e : fields.entrySet())
		{
			EditorProperty prop = (EditorProperty)toSet.getPropertyForName(e.getKey());
			toSet.setPropertyForName(e.getKey(), 
					prop.makeCopy(fields.get(e.getKey()).getValue()));
		}
		
		return null;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		return new ArrayList<BigFrameworkGuy.ConfigType>();
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

}
