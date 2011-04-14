package linewars.gamestate.mapItems;

import java.util.HashMap;
import java.util.Map;

public strictfp class MapItemModifier {
	
	public enum MapItemModifiers {
		fireRate, moveSpeed, damageDealt, damageReceived, maxHp, buildingProductionRate
	}
	
	public interface MapItemModifierType {
		public double modify(double x);
		public boolean equals(Object obj);
	}
	
	public static class Constant implements MapItemModifierType {
		
		private double value;
		
		public Constant(double v)
		{
			value = v;
		}
		
		@Override
		public double modify(double x) {
			return value;
		}
		
		public boolean equals(Object obj)
		{
			return (obj instanceof Constant) && ((Constant)obj).value == value;
		}
		
	}
	
	public static class Add implements MapItemModifierType {
		
		private double value;
		
		public Add(double v)
		{
			value = v;
		}
		
		@Override
		public double modify(double x) {
			return x + value;
		}
		
		public boolean equals(Object obj)
		{
			return (obj instanceof Add) && ((Add)obj).value == value;
		}
		
	}
	
	public static class Multiply implements MapItemModifierType {
		
		private double value;
		
		public Multiply(double v)
		{
			value = v;
		}
		
		@Override
		public double modify(double x) {
			return x*value;
		}
		
		public boolean equals(Object obj)
		{
			return (obj instanceof Multiply) && ((Multiply)obj).value == value;
		}
		
	}
	
	private class ModifierTypePair {
		private MapItemModifiers mod;
		private MapItemModifierType type;
	}
	
	private Map<MapItemModifiers, MapItemModifierType> mappings = new HashMap<MapItemModifier.MapItemModifiers, MapItemModifier.MapItemModifierType>();
	private MapItemModifier wrapped = null;

	public void setWrapped(MapItemModifier mim)
	{
		wrapped = mim;
	}
	
	public void setMapping(MapItemModifiers m, MapItemModifierType t)
	{
		mappings.put(m, t);
	}
	
	public double getModifier(MapItemModifiers m)
	{
		if(mappings.get(m) == null)
		{
			if(wrapped == null)
				throw new IllegalStateException("Cannot provide mapping for " + m.toString());
			else
				return wrapped.getModifier(m);
		}
		else if(wrapped == null)
			return mappings.get(m).modify(1);
		else
			return mappings.get(m).modify(wrapped.getModifier(m));
	}
	
	public MapItemModifier removeModifierLayer(MapItemModifier mim)
	{
		if(this == mim)
			return wrapped;
		else if(wrapped == null)
			return this;
		else
		{
			wrapped = wrapped.removeModifierLayer(mim);
			return this;
		}
	}
	
	/**
	 * Takes the input modifier and puts it and its entire set of
	 * wrapped modifiers at the bottom of this modifier's stack
	 * of wrapped modifiers.
	 * 
	 * @param mim
	 */
	public void pushUnderStack(MapItemModifier mim)
	{
		if(wrapped == null)
			this.setWrapped(mim);
		else
			wrapped.pushUnderStack(mim);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof MapItemModifier) &&
				((MapItemModifier)obj).mappings.equals(mappings) &&
				((((MapItemModifier)obj).wrapped == null && wrapped == null) || 
						((MapItemModifier)obj).wrapped.equals(wrapped));
	}

}
