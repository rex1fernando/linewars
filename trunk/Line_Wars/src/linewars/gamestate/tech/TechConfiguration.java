package linewars.gamestate.tech;

import java.util.Observable;
import java.util.Observer;

import linewars.gamestate.Player;
import linewars.gamestate.Race;

import configuration.Configuration;
import configuration.Property;
import configuration.Usage;

public class TechConfiguration extends Configuration implements Observer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5760115591033230971L;
	
	
	private static final String modificationKey = "modification";
	private static final String costKey = "cost";
	private static final String nameKey = "name";
	private static final String tooltipKey = "tooltip";
	private static final String iconURIKey = "iconURI";
	private static final String pressedIconURIKey = "pressedIconURI";
	private static final String rolloverIconURIKey = "rolloverIconURI";
	private static final String selectedIconURIKey = "selectedIconURI";
	private static final String disabledIconURIKey = "disabledIconURI";
	
	//The Modification to be made to the owner's race
	private ModifierConfiguration modification;
	//The cost to research this this Tech
	private double cost;
	
	//The name of this Tech
	private String name;
	//The tooltip to be displayed when the user hovers the mouse over this Tech
	private String tooltip;
	//The URI of this Tech's icon
	private String iconURI;
	//The URI of the icon displayed while this Tech is clicked
	private String pressedIconURI;
	//The URI of the icon displayed while the mouse is over this Tech
	private String rolloverIconURI;
	//The URI of the icon displayed when the Tech's icon is selected (like via Tab)
	private String selectedIconURI;
	//The URI of the icon to be displayed when this Tech is disabled
	private String disabledIconURI;

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 != this) return;
		if(!(arg1 instanceof String)) return;
		String propertyName = (String) arg1;
		Object value = this.getPropertyForName(propertyName);

		if(propertyName == modificationKey){
			modification = (ModifierConfiguration) value;
		}else if(propertyName == costKey){
			cost = (Double) value;
		}else if(propertyName == nameKey){
			name = (String) value;
		}else if(propertyName == tooltipKey){
			tooltip = (String) value;
		}else if(propertyName == iconURIKey){
			iconURI = (String) value;
		}else if(propertyName == pressedIconURIKey){
			pressedIconURI = (String) value;
		}else if(propertyName == rolloverIconURIKey){
			rolloverIconURI = (String) value;
		}else if(propertyName == selectedIconURIKey){
			selectedIconURI = (String) value;
		}else if(propertyName == disabledIconURIKey){
			disabledIconURI = (String) value;
		}
	}
	
	public void research(Player owner){
		Race toModify = owner.getRace();
		modification.applyTo(new Property(Usage.CONFIGURATION, toModify));
	}
	
	public ModifierConfiguration getModification() {
		return modification;
	}
	
	public void setModification(ModifierConfiguration modification) {
		this.setPropertyForName(modificationKey, new Property(Usage.CONFIGURATION, modification));
	}
	
	public double getCost() {
		return cost;
	}
	
	public void setCost(double cost) {
		this.setPropertyForName(costKey, new Property(Usage.NUMERIC_FLOATING_POINT, cost));
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.setPropertyForName(nameKey, new Property(Usage.STRING, name));
	}
	
	public String getTooltip() {
		return tooltip;
	}
	
	public void setTooltip(String tooltip) {
		this.setPropertyForName(tooltipKey, new Property(Usage.STRING, tooltip));
	}
	
	public String getIconURI() {
		return iconURI;
	}
	
	public void setIconURI(String iconURI) {
		this.setPropertyForName(iconURIKey, new Property(Usage.STRING, iconURI));
	}
	
	public String getPressedIconURI() {
		return pressedIconURI;
	}
	
	public void setPressedIconURI(String pressedIconURI) {
		this.setPropertyForName(pressedIconURIKey, new Property(Usage.STRING, pressedIconURI));
	}
	
	public String getRolloverIconURI() {
		return rolloverIconURI;
	}
	
	public void setRolloverIconURI(String rolloverIconURI) {
		this.setPropertyForName(rolloverIconURIKey, new Property(Usage.STRING, rolloverIconURI));
	}
	
	public String getSelectedIconURI() {
		return selectedIconURI;
	}
	
	public void setSelectedIconURI(String selectedIconURI) {
		this.setPropertyForName(selectedIconURIKey, new Property(Usage.STRING, selectedIconURI));
	}
	
	public String getDisabledIconURI(){
		return disabledIconURI;
	}
	
	public void setDisabledIconURI(String disabledIconURI){
		this.setPropertyForName(disabledIconURIKey, new Property(Usage.STRING, disabledIconURI));
	}
}
