package editor.abilitiesstrategies;

import javax.swing.JCheckBox;

public class BooleanField extends Field {
	
	private JCheckBox box;

	public BooleanField(String name, String description, Boolean initialValue) {
		super(name, description);
		box = new JCheckBox("Check to enable");
		this.add(box);
		if(initialValue == null)
			initialValue = false;
		box.setSelected(initialValue);
	}

	@Override
	public Object getValue() {
		return (Boolean)box.isSelected();
	}

}
