package linewars.gamestate.mapItems;

import java.util.HashMap;
import java.util.Map;

public class MapItemModifier {
	
	public enum MapItemModifiers {
		fireRate, moveSpeed, damageDealt, damageReceived, maxHp, buildingProductionRate
	}
	
	public interface MapItemModifierType {
		public double modify(double x);
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

}
