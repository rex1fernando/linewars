package linewars.gamestate.mapItems.strategies.targeting;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import utility.Observable;
import utility.Observer;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public strictfp class GoStraightConfiguration extends TargetingStrategyConfiguration implements Observer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3414871283041555093L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Go Straight",
				GoStraightConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private double velocity;
	
	public strictfp class GoStraight implements TargetingStrategy
	{
		private Projectile projectile;
		
		private GoStraight(Projectile p)
		{
			projectile = p;
		}

		@Override
		public String name() {
			return "Go Straight at " + velocity + " per second";
		}

		@Override
		public TargetingStrategyConfiguration getConfig() {
			return GoStraightConfiguration.this;
		}

		@Override
		public Transformation getTarget() {
			Transformation ret = new Transformation(projectile.getPosition().add(
					Position.getUnitVector(projectile.getRotation())
					.scale(projectile.getModifier().getModifier(MapItemModifiers.moveSpeed)*
							velocity*(projectile.getGameState().getLastLoopTime()))), 
					projectile.getRotation());
			return ret;
		}
		
	}
	
	public GoStraightConfiguration()
	{
		super.setPropertyForName("velocity", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, 0, EditorUsage.PositiveReal,
				"The velocity of the projectile per second"));
		super.addObserver(this);
	}

	@Override
	public TargetingStrategy createStrategy(MapItem m) {
		if(m instanceof Projectile)
			return new GoStraight((Projectile) m);
		else
			throw new IllegalArgumentException("Only projectiles may have targeting strategies");
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof GoStraightConfiguration) &&
				(((GoStraightConfiguration)obj).velocity == velocity);
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o == this && arg.equals("velocity"))
			velocity = (double)(Double)super.getPropertyForName("velocity").getValue();
	}

}
