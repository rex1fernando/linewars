package editor.abilities;

import configuration.Property;
import configuration.Usage;

public class EditorProperty extends Property {
	
	private EditorUsage eUsage;
	private String description;

	public EditorProperty(Usage type, Object initialData, EditorUsage eType, String description) {
		super(type, initialData);
		eUsage = eType;
		this.description = description;
	}
	
	public EditorUsage getEditorUsage()
	{
		return eUsage;
	}
	
	public String getDescription()
	{
		return description;
	}

}
