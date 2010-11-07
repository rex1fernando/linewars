package editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class URISelector extends JPanel
{	
	private static final long serialVersionUID = 4210934277569886576L;
	private static final int WIDTH = 160;
	private static final int SPACING = 5;
	
	private JButton button;
	private JLabel label;
	private JTextField textField;
	private SelectorOptions options;
	
	public URISelector(String label, SelectorOptions options)
	{
		this.options = options;
		
		button = new JButton("Select");
		button.addActionListener(new ButtonClickEvent());
		this.label = new JLabel(label);
		
		textField = new JTextField();
		textField.setPreferredSize(new Dimension(WIDTH, 20));
		
		add(this.label);
		add(Box.createHorizontalStrut(SPACING));
		add(textField);
		add(Box.createHorizontalStrut(SPACING));
		add(button);
	}
	
	public String getSelectedURI()
	{
		return textField.getText();
	}
	
	private class ButtonClickEvent implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			String[] opts = options.getOptions();
			String s = (String) JOptionPane.showInputDialog(
					URISelector.this,
					"Please select a " + label.getText() + ".",
					label.getText() + " Selector",
					JOptionPane.PLAIN_MESSAGE,
					null,
					opts,
					opts[0]);

			// string was returned
			if ((s != null) && (s.length() > 0))
			{
			    textField.setText(s);
			    options.uriSelected(s);
			}
		}
	}
	
	public static interface SelectorOptions
	{
		public String[] getOptions();
		public void uriSelected(String uri);
	}
}
