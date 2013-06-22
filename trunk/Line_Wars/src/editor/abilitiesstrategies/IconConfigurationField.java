package editor.abilitiesstrategies;

import java.util.ArrayList;
import java.util.List;

import linewars.display.IconConfiguration.IconType;
import configuration.Configuration;
import editor.ConfigurationEditor;
import editor.IconEditor;

public class IconConfigurationField extends Field {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3671685097815235801L;
	private ConfigurationEditor iconEditor;

	public IconConfigurationField(String name, String description, Configuration initialData) {
		super(name, description);
		List<IconType> neededIcons = new ArrayList<IconType>();
		List<String> iconDescriptions = new ArrayList<String>();
		neededIcons.add(IconType.regular);
		iconDescriptions.add("");
		neededIcons.add(IconType.highlighted);
		iconDescriptions.add("");
		neededIcons.add(IconType.pressed);
		iconDescriptions.add("");
		neededIcons.add(IconType.rollover);
		iconDescriptions.add("");
		iconEditor = new IconEditor(neededIcons, iconDescriptions);
		if(initialData != null)
			iconEditor.setData(initialData);
		else
			iconEditor.resetEditor();
		
		this.add(iconEditor.getPanel());
	}

	@Override
	public Object getValue() {
		Configuration c = iconEditor.instantiateNewConfiguration();
		iconEditor.getData(c);
		return c;
	}

}
