package editor.abilitiesstrategies;

import java.util.Scanner;

import javax.swing.JTextField;

public class TextField extends Field {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8453378525517770107L;
	private EditorUsage usage;
	private JTextField text;
	
	public TextField(String name, String description, EditorUsage usage) 
	{
		this(name, description, usage, "");
	}
	
	public TextField(String name, String description, EditorUsage usage, String initialText) {
		super(name, description);
		text = new JTextField(20);
		text.setText(initialText);
		this.usage = usage;
		this.add(text);
	}

	@SuppressWarnings("null")
	@Override
	public Object getValue() {
		Scanner s = new Scanner(text.getText());
		Object ret = null;
		switch (usage)
		{
			case Real:
				if(s.hasNextDouble())
					ret = s.nextDouble();
				else
					text.setText("Error: Must be a real-valued number.");
				break;
			case PositiveReal:
				if(!s.hasNextDouble() || (Double)(ret = s.nextDouble()) <= 0)
					text.setText("Error: Must be a positive real-valued number.");
				break;
			case NaturalNumber:
				if(!s.hasNextInt() || (Integer)(ret = s.nextInt()) < 0)
					text.setText("Error: Must be a natural number");
				break;
			case text:
				return text.getText();
			default:
				text.setText("Error: This editor has not been updated to support this"
					 + "data type, please contact a programmer to fix it.");	
		}
		return ret;
	}

}
