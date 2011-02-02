package editor.abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import configuration.Configuration;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;

public class AbilityStrategyEditor extends JPanel implements ConfigurationEditor {
	
	private Map<String, Field> fields = new HashMap<String, Field>();
	private BigFrameworkGuy bfg;
	private ConfigType workingType;
	
	public AbilityStrategyEditor(BigFrameworkGuy bfg)
	{
		this.bfg = bfg;
		workingType = ConfigType.ability;
	}

	@Override
	public void setData(Configuration cd) {
		this.removeAll();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		for(String field : cd.getPropertyNames())
		{
			//ignore all non-editor properties
			if(!(cd.getPropertyForName(field) instanceof EditorProperty))
				continue;
			
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
		
		this.validate();
		this.updateUI();
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		Configuration c = null;
		// TODO figure out how to instantiate the config type
		setData(c);
		return null;
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		for(Entry<String, Field> e : fields.entrySet())
		{
			EditorProperty prop = (EditorProperty)toSet.getPropertyForName(e.getKey());
			toSet.setPropertyForName(e.getKey(), 
					prop.makeCopy(fields.get(e.getKey()).getValue()));
		}
		
		return workingType;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		List<ConfigType> ret = new ArrayList<BigFrameworkGuy.ConfigType>();
		ret.add(workingType);
		return ret;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

}
