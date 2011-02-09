package configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * This class may be used to store a list of object in a Configuration in a way that allows Techs access to upgrade elements in the list.
 * 
 * @author Knexer
 *
 * @param <T> The type of objects stored in the list
 */
public class ListConfiguration<T> extends Configuration implements Observer{
	
	//this class requires its users to specify the name of each data element... these should all be unique
	//It stores the contents of each ListObject in the superclass by using the name of the corresponding data element
	//as a prefix to a 'data' and 'enabled' suffix.
	//For example, a 'rightarm' data element would be stored in the 'rightarm_data' location in the HashMap, 
	//with the Usage provided, and whether or not that data element is currently enabled in the list would 
	//be stored in the 'rightarm_enabled' location.
	
	/**
	 * Change this if and only if this class is changed in a way that breaks
	 * deserialization from files generated by serializing an old ListConfiguration object
	 */
	private static final long serialVersionUID = 7528591700118553711L;

	//the list that stores all the actual data
	private ArrayList<ListObject> dataList;
	
	//these lists are returned by accessor methods; they can be computed from dataList,
	//but are stored here for efficiency
	private ArrayList<T> fullTList;
	private ArrayList<T> enabledSubList;
	
	public ListConfiguration(ArrayList<T> data, ArrayList<String> names, ArrayList<Usage> usages){
		this(data, names, usages, allTrueListOfSize(data.size()));
	}
	
	
	private static ArrayList<Boolean> allTrueListOfSize(int size) {
		ArrayList<Boolean> ret = new ArrayList<Boolean>();
		for(int i = 0; i < size; i++){
			ret.add(true);
		}
		return ret;
	}


	public ListConfiguration(ArrayList<T> data, ArrayList<String> names, ArrayList<Usage> usages, ArrayList<Boolean> enabled){
		//make sure the lists are all the same length
		int expectedSize = data.size();
		if(names.size() != expectedSize || usages.size() != expectedSize || enabled.size() != expectedSize){
			throw new IllegalArgumentException("The provided parallel lists should all be the same size.");
		}
		
		//make sure the 'names' list is full of unique Strings
		Set<String> alreadySeen = new HashSet<String>();
		for(String currentString : names){
			if(alreadySeen.contains(currentString)){
				//every String in the list should be unique!
				throw new IllegalArgumentException("The String " + currentString + " is in the provided list of unique names more than once.");
			}else{
				//add to the set
				alreadySeen.add(currentString);
			}
		}
		
		dataList = new ArrayList<ListObject>();
		
		for(int i = 0; i < data.size(); i++){
			//add data to subclass
			ListObject toAdd = new ListObject();
			toAdd.name = names.get(i);
			toAdd.enabled = enabled.get(i);
			toAdd.data = data.get(i);
			toAdd.usage = usages.get(i);
			dataList.add(toAdd);
			
			//add data to superclass
			Property dataProperty = new Property(toAdd.usage, toAdd.data);
			Property enabledProperty = new Property(Usage.BOOLEAN, toAdd.enabled);
			this.setPropertyForName(toAdd.name + "_data", dataProperty);
			this.setPropertyForName(toAdd.name + "_enabled", enabledProperty);
		}

		this.addObserver(this);
	}
	
	//get full list
	@SuppressWarnings("unchecked")
	public ArrayList<T> getFullList(){
		if(fullTList != null){
			return (ArrayList<T>) fullTList.clone();
		}
		
		fullTList = new ArrayList<T>();
		for(ListObject toCheck : dataList){
			if(toCheck.enabled){
				fullTList.add(toCheck.data);
			}
		}
		return (ArrayList<T>) fullTList.clone();
	}
	
	//get list of enabled
	@SuppressWarnings("unchecked")
	public ArrayList<T> getEnabledSubList(){
		if(enabledSubList != null){
			return (ArrayList<T>) enabledSubList.clone();
		}
		
		enabledSubList = new ArrayList<T>();
		for(int i = 0; i < dataList.size(); i++){
			if(dataList.get(i).enabled){
				enabledSubList.add(dataList.get(i).data);
			}
		}
		return (ArrayList<T>) enabledSubList.clone();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 != this) return;
		String changedObject = (String) arg1;
		String secondPart = null;
		if(changedObject.endsWith("_data")){
			secondPart = "_data";
		}else{
			secondPart = "_enabled";
		}
		String firstPart = changedObject.substring(0, changedObject.length() - secondPart.length());
		
		//now we know what was changed, we just have to implement the change on our end
		for(int i = 0; i < dataList.size(); i++){
			if(dataList.get(i).name.equals(firstPart)){
				if(secondPart.equals("_data")){
					dataList.get(i).data = (T) this.getPropertyForName(changedObject).getValue();
				}else{
					dataList.get(i).enabled = (Boolean) this.getPropertyForName(changedObject).getValue();
				}
			}
		}
	}
	
	//set full list... this should be the same length as the current list
	public void setFullList(ArrayList<T> newList){
		if(newList.size() != dataList.size()){
			throw new IllegalArgumentException("If you want to change the length of the list, you will have to construct a new ListConfiguration object.");
		}
		
		fullTList = null;
		enabledSubList = null;
		for(int i = 0; i < newList.size(); i++){
			if(!newList.get(i).equals(dataList.get(i).data)){
				Property toSet = new Property(dataList.get(i).usage, dataList.get(i).data);
				this.setPropertyForName(dataList.get(i).name, toSet);
			}
		}
	}
	
	//set list of enabled
	public void setEnabledSubList(ArrayList<T> newList){
		for(int i = 0; i < dataList.size(); i++){
			if(newList.contains(dataList.get(i).data)){
				if(!dataList.get(i).enabled){
					//enable it in superclass
					String name = dataList.get(i).name + "_enabled";
					this.setPropertyForName(name, new Property(Usage.BOOLEAN, true));
				}
			}else{
				if(dataList.get(i).enabled){
					//disable it in superclass
					String name = dataList.get(i).name + "_enabled";
					this.setPropertyForName(name, new Property(Usage.BOOLEAN, true));
				}
			}
		}
	}
	
	public ArrayList<Boolean> getEnabledFlags()
	{
		ArrayList<Boolean> ret = new ArrayList<Boolean>();
		for(ListObject lo : dataList)
			ret.add(lo.enabled);
		return ret;
	}
	
	public ArrayList<String> getNames()
	{
		ArrayList<String> ret = new ArrayList<String>();
		for(ListObject lo : dataList)
			ret.add(lo.name);
		return ret;
	}
	
	public ArrayList<Usage> getUsages()
	{
		ArrayList<Usage> ret = new ArrayList<Usage>();
		for(ListObject lo : dataList)
			ret.add(lo.usage);
		return ret;
	}

	//struct to hold the data in a list
	private class ListObject{
		private T data;
		private Usage usage;
		private String name;
		private boolean enabled;
	}
}
