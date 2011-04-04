package utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Observable implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4212261185745528536L;
	private List<Observer> obs = new ArrayList<Observer>();
	private boolean changed = false;
	
	public void addObserver(Observer o)
	{
		obs.add(o);
	}
	
	protected void clearChanged()
	{
		changed = false;
	}
	
	public int countObservers()
	{
		return obs.size();
	}
	
	public void deleteObserver(Observer o)
	{
		obs.remove(o);
	}
	
	public boolean hasChanged()
	{
		return changed;
	}
	
	public void notifyObservers()
	{
		notifyObservers(null);
	}
	
	public void notifyObservers(Object arg)
	{
		for(Observer o : obs)
			o.update(this, arg);
	}
	
	protected void setChanged()
	{
		changed = true;
	}
	
	public void removeObserver(Observer obs)
	{
		this.obs.remove(obs);
	}

}
