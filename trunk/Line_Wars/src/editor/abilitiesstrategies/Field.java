package editor.abilitiesstrategies;

import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class Field extends JPanel {
	
	private String name;
	private String description;
	
	public Field(String name, String description)
	{
		this.name = name;
		this.description = description;
		
		this.add(new JLabel(name + ":"));
		this.add(new JLabel(description));
	}
	
	public abstract Object getValue();

}
