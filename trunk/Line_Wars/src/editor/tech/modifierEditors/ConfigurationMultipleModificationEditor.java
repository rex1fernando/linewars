package editor.tech.modifierEditors;

import java.awt.BorderLayout;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import linewars.gamestate.tech.ModifierConfiguration;
import linewars.gamestate.tech.ModifierConfiguration.ModifierMetaData;
import configuration.Configuration;
import configuration.Property;
import configuration.Usage;
import editor.BigFrameworkGuy.ConfigType;
import editor.GenericSelector.GenericListCallback;
import editor.ListGenericSelector;
import editor.ListGenericSelector.ListChangeListener;

public class ConfigurationMultipleModificationEditor extends NewModifierEditor {
	
	private Usage validUsage = Usage.CONFIGURATION;
	private Configuration template;
	
	private MultipleSubModificationModification data;
	
	private JPanel panel;
	private ListGenericSelector<String> itemsToModify;
	
	private String highlightedString;
	private NewModifierEditor subEditor;
	
	static{
		NewModifierEditor.setEditorForModifier(MultipleSubModificationModification.class, ConfigurationMultipleModificationEditor.class);
	}

	public ConfigurationMultipleModificationEditor(Property property) {
		if(property.getUsage() != validUsage){
			//FIXME do something here?
		}
		
		template = (Configuration) property.getValue();
		data = new MultipleSubModificationModification();
		
		//set up the editor's JPanel
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		//FIXME use the appropriate layout manager here
		
		//it should have a ListGenericSelector that lets the user pick things from Template to modify
		itemsToModify = new ListGenericSelector<String>("", new templateNameFetcher());
		
		//anything in the list of selected items should have a modification associated with it
		//the highlighted item's associated modification should be opened for editing in the sub-editor
		itemsToModify.addListChangeListener(new ItemsToModifyChangeListener());
		panel.add(itemsToModify, BorderLayout.PAGE_START);
		
		//nothing is highlighted, so subEditor should be null
		setSubEditor(null);
	}

	//The list selector is manipulating data, we should call stuff in list selector and let it modify data
	//Template won't be changing here, they will have to construct a new editor for that
	@Override
	public void setData(Configuration cd) {
		//cd should me of the appropriate type
		MultipleSubModificationModification source = (MultipleSubModificationModification) cd;
		
		//empty this editor's data by clearing the ListGenericSelector
		itemsToModify.setSelectedObjects(new ArrayList<String>());
		
		if(cd == null){
			return;
		}
		
		//now load the data in source into this editor
		//we will do this by setting things in ListGenericSelector
		ArrayList<String> sourceNames = new ArrayList<String>();
		for(String toAdd : source.getModifiedPropertyNames()){
			sourceNames.add(toAdd);
		}
		itemsToModify.setSelectedObjects(sourceNames);
		
		for(String toCopy : sourceNames){
			data.setSubModification(toCopy, source.getSubModification(toCopy));
		}
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		return new MultipleSubModificationModification();
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		
		//this should be of the appropriate type
		MultipleSubModificationModification target = (MultipleSubModificationModification) toSet;
		
		//but it may not be clean, empty it
		Set<String> keySet = target.getModifiedPropertyNames();
		for(String key : keySet){
			target.removeSubModification(key);
		}
		
		saveSubEditorData();
		
		//for each key in data
		for(String key : data.getModifiedPropertyNames()){
			target.setSubModification(key, data.getSubModification(key));
		}
		return null;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public ModifierConfiguration getData() {
		MultipleSubModificationModification ret = new MultipleSubModificationModification();
		getData(ret);
		return ret;
	}

	@Override
	public void resetEditor() {
		throw new UnsupportedOperationException();
	}
	
	private void saveSubEditorData(){
		System.out.println("Saved " + highlightedString + " in " + this.hashCode());
		if(highlightedString == null){
			return;
		}
		
		if(subEditor == null){
			data.setSubModification(highlightedString, null);
		}else{
			//save the data in the current sub editor into 'data'
			data.setSubModification(highlightedString, subEditor.getData());
			System.out.println(((MultipleSubModificationModification) subEditor.getData()).getModifiedPropertyNames());
		}
		
	}
	
	private void setSubEditor(NewModifierEditor newSubEditor){
		if(subEditor != null){
			panel.remove(subEditor.getPanel());			
		}
		subEditor = newSubEditor;
		if(subEditor != null){
			ModifierConfiguration temp = data.getSubModification(highlightedString);
			subEditor.setData(temp);
			panel.add(subEditor.getPanel(), BorderLayout.CENTER);
			panel.updateUI();
			panel.validate();
		}
	}
	
	private class ItemsToModifyChangeListener implements ListChangeListener<String>{

		@Override
		public void objectsRemoved(List<String> removed) {

			saveSubEditorData();
			highlightedString = null;
			setSubEditor(null);
			
			for(String toRemove : removed){
				data.removeSubModification(toRemove);
			}
		}

		@Override
		public void objectAdded(String added) {
			data.setSubModification(added, null);
		}

		@Override
		public void HighlightChange(List<String> highlighted) {			
			//If 0 or 2 or more things are highlighted, do nothing and return
			if(highlighted.size() != 1){
				saveSubEditorData();
				highlightedString = null;
				setSubEditor(null);
				return;
			}
			
			if(highlighted.get(0).equals(highlightedString)){
				return;
			}
			
			saveSubEditorData();
			
			highlightedString = highlighted.get(0);
			
			//find out what modifications can be made to this property
			Usage typeToModify = template.getPropertyForName(highlightedString).getUsage();
			List<ModifierMetaData> validModifications = ModifierConfiguration.getModifiersForUsage(typeToModify);
			
			Class<? extends ModifierConfiguration> selectedModificationType;
			
			Class<? extends NewModifierEditor> newSubEditorType;
			if(validModifications == null){
				return;
			}else if(validModifications.size() == 1){
				selectedModificationType = validModifications.get(0).getModifier();
			}else{
				//prompt the user to choose a way to modify this property
				selectedModificationType = ModifierConfiguration.promptUserToSelectModificationType(panel, validModifications);
				newSubEditorType = null;
				System.err.println("There are multiple modifications that can be made to " + highlightedString + " which is of Usage type " + typeToModify + ".");
				highlightedString = null;
			}
			newSubEditorType = NewModifierEditor.getEditorForModifier(selectedModificationType);
			
			setSubEditor(instantiateSubEditor(newSubEditorType));
		}

		private NewModifierEditor instantiateSubEditor(Class<? extends NewModifierEditor> newSubEditorType) {
			//ick, necessary reflection to construct an instance of the editor
			Constructor<? extends NewModifierEditor> newSubEditorConstructor = null;
			
			try {
				newSubEditorConstructor = newSubEditorType.getConstructor(Property.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Property arg1 = template.getPropertyForName(highlightedString);
			NewModifierEditor ret = null;
			try {
				ret = newSubEditorConstructor.newInstance(arg1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return ret;
		}
		
	}
	
	private class templateNameFetcher implements GenericListCallback<String>{

		/**
		 * Returns a list of the names used by this class' outer ConfigurationMultipleModificationEditor's 'template' Configuration.
		 * This configuration is the one that will be upgraded by the MultipleSubModificationModification being edited, and its
		 * names identify things that can be upgraded by the sub-editors of the MultipleSubModificationModification being edited.
		 */
		@Override
		public List<String> getSelectionList() {
			Configuration nameSource = template;
			
			ArrayList<String> ret = new ArrayList<String>();
			
			for(String toAdd : nameSource.getPropertyNames()){
				ret.add(toAdd);
			}

			return ret;
		}
		
	}
}
