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
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;

public class AbilityStrategyEditor extends JPanel implements ConfigurationEditor {
	
	private Map<String, Field> fields = new HashMap<String, Field>();
	private BigFrameworkGuy bfg;
	private Class<? extends Configuration> toInstantiate;
	
	public AbilityStrategyEditor(BigFrameworkGuy bfg, Class<? extends Configuration> toInstantiate)
	{
		this.bfg = bfg;
		this.toInstantiate = toInstantiate;
	}

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
					f = new ConfigurationField(field, prop.getDescription(), 
							prop.getEditorUsage(), bfg);
					break;
				case NaturalNumber:
				case PositiveReal:
				case Real:
					f = new TextField(field, prop.getDescription(), 
							prop.getEditorUsage());
					break;
			}
			
			this.add(f);
			fields.put(field, f);
		}
		
		this.add(new JLabel(foundFields + " configurable fields"));
		
		this.validate();
		this.updateUI();
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
		setData(c);
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
