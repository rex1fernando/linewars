package linewars.gamestate.mapItems.strategies.impact;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategyConfiguration;
import linewars.gamestate.shapes.Circle;
import configuration.Configuration;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class AreaOfEffectConfiguration extends ImpactStrategyConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6279663150583295152L;
	
	static {
		StrategyConfiguration.setStrategyConfigMapping("Area of Effect",
				AreaOfEffectConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class AreaOfEffect implements ImpactStrategy
	{

		private Projectile proj;
		
		private AreaOfEffect(Projectile p)
		{
			proj = p;
		}
		
		@Override
		public String name() {
			return "Deal an area of effect damage";
		}

		@Override
		public ImpactStrategyConfiguration getConfig() {
			return AreaOfEffectConfiguration.this;
		}

		@Override
		public void handleImpact(MapItem m) {
			handleImpact(proj.getPosition());
		}

		@Override
		public void handleImpact(Position p) {
			proj.setState(MapItemState.Dead);
			
			Circle damageCircle = new Circle(proj.getTransformation(), getMaxRange());
			for(Unit u : proj.getLane().getUnitsIn(damageCircle))
			{
				//if they're not allowed to collide, skip
				if(!CollisionStrategyConfiguration.isAllowedToCollide(u, proj))
					continue;
				double distance = Math.sqrt(proj.getPosition().distanceSquared(u.getPosition()));
				u.setHP(u.getHP() - damage(distance));
			}
		}
		
	}
	
	public AreaOfEffectConfiguration()
	{
		super.setPropertyForName("maxRange", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.PositiveReal, "The max range at which a unit may get hit by this explosion"));
		super.setPropertyForName("a", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.Real, "the a in damage(x) = a*x^2 + b*x + c"));
		super.setPropertyForName("b", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.Real, "the b in damage(x) = a*x^2 + b*x + c"));
		super.setPropertyForName("c", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.Real, "the c in damage(x) = a*x^2 + b*x + c"));
	}
	
	public double getMaxRange()
	{
		return (Double) super.getPropertyForName("maxRange").getValue();
	}
	
	public double damage(double x)
	{
		double a = (Double) super.getPropertyForName("a").getValue();
		double b = (Double) super.getPropertyForName("b").getValue();
		double c = (Double) super.getPropertyForName("c").getValue();
		
		return a*x*x + b*x + c;
	}

	@Override
	public ImpactStrategy createStrategy(MapItem m) {
		return new AreaOfEffect((Projectile) m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof AreaOfEffectConfiguration) &&
				(((Configuration)obj).getPropertyForName("maxRange").equals(super.getPropertyForName("maxRange"))) &&
				(((Configuration)obj).getPropertyForName("a").equals(super.getPropertyForName("a"))) &&
				(((Configuration)obj).getPropertyForName("b").equals(super.getPropertyForName("b"))) &&
				(((Configuration)obj).getPropertyForName("c").equals(super.getPropertyForName("c")));
	}

}
