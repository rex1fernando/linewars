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

/**
 * A Java Swing Component that allows easy selection of a string.
 * Designed specifically for selecting URIs within the game editor.
 * 
 * @author Titus Klinge
 */
public class URISelector extends JPanel
{	
	private static final long serialVersionUID = 4210934277569886576L;
	
	private static final int WIDTH = 160;
	protected static final int SPACING = 5;
	
	protected JButton button;
	protected JLabel label;
	protected JTextField textField;
	private SelectorOptions options;
	
	/**
	 * Constructs a new URISelector with the given label and actions
	 * defined by the SelectorOptions object.
	 * 
	 * @param label The label to give the compoenent.
	 * @param options The options that control the behavior of the selector.
	 */
	public URISelector(String label, SelectorOptions options)
	{
		setOptions(options);
		
		button = new JButton("Select");
		button.addActionListener(new ButtonClickEvent());
		this.label = new JLabel(label);
		
		textField = new JTextField();
		textField.setPreferredSize(new Dimension(WIDTH, 20));
		textField.setEditable(false);
		
		add(this.label);
		add(Box.createHorizontalStrut(SPACING));
		add(textField);
		add(Box.createHorizontalStrut(SPACING));
		add(button);
	}
	
	protected void setOptions(SelectorOptions options)
	{
		this.options = options;
	}
	
	/**
	 * Get's the selected URI.
	 * 
	 * @return The selected URI.
	 */
	public String getSelectedURI()
	{
		return textField.getText();
	}
	
	/**
	 * Sets the selected URI to the given parameter.
	 * @param uri The URI that is selected.
	 */
	public void setSelectedURI(String uri)
	{
		textField.setText(uri);
	}
	
	/**
	 * An event handler for when the select button is pressed.
	 */
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
					null);

			// string was returned
			if ((s != null) && (s.length() > 0))
			{
			    textField.setText(s);
			    options.uriSelected(s);
			}
		}
	}
	
	/**
	 * The selector options interface for controlling the behavior of
	 * the selector.
	 */
	public static interface SelectorOptions
	{
		/**
		 * Gets the options that are selectable by this selector.
		 * @return The selectable options for this selector.
		 */
		public String[] getOptions();
		
		/**
		 * A callback function that is executed when a URI is selected.
		 * @param uri
		 */
		public void uriSelected(String uri);
	}
}
