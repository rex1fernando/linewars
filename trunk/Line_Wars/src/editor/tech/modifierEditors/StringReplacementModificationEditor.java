package editor.tech.modifierEditors;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import linewars.gamestate.tech.ModifierConfiguration;
import configuration.Configuration;
import configuration.Property;
import editor.BigFrameworkGuy.ConfigType;

public class StringReplacementModificationEditor extends ModifierEditor {
	
	static{
		ModifierEditor.setEditorForModifier(StringReplacementModification.class, StringReplacementModificationEditor.class);
	}
	
	private JPanel panel;
	private JTextArea textArea;
	
	public StringReplacementModificationEditor(Property discarded){
		panel = new JPanel();
		textArea = new JTextArea();
		
		
		JScrollPane textAreaScroller = new JScrollPane(textArea);

		textAreaScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setPreferredSize(new Dimension(400, 200));
		textAreaScroller.setPreferredSize(new Dimension(400, 100));
		
		panel.add(new JLabel("Replace the modified string with:"));
		panel.add(textAreaScroller);
	}

	@Override
	public void setData(Configuration cd) {
		if(cd == null){
			return;
		}
		if(!(cd instanceof StringReplacementModification)){
			return;
		}
		StringReplacementModification toCopy = (StringReplacementModification) cd;
		textArea.setText(toCopy.getReplacement());
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		return new StringReplacementModification();
	}

	@Override
	public void resetEditor() {
		textArea.setText("");
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		if(toSet == null || !(toSet instanceof StringReplacementModification)){
			throw new IllegalArgumentException();
		}
		StringReplacementModification target = (StringReplacementModification) toSet;
		target.setReplacement(textArea.getText());
		return null;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		return null;
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public ModifierConfiguration getData() {
		StringReplacementModification ret = new StringReplacementModification();
		getData(ret);
		return ret;
	}

}
