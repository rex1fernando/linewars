package linewars.gamestate.playerabilities;

import linewars.gamestate.Lane;
import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.ProjectileDefinition;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class GiantNuke extends PlayerAbility {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1823741141151561884L;

	static {
		PlayerAbility.setAbilityConfigMapping("Giant Nuke", GiantNuke.class, AbilityStrategyEditor.class);
	}
	
	
	public GiantNuke()
	{
		super.setPropertyForName("nukeConfig", new EditorProperty(Usage.CONFIGURATION, 
				null, EditorUsage.ProjectileConfig, "The configuration for the nuke projectile"));
	}
	
	public ProjectileDefinition getNukeConfig()
	{
		return (ProjectileDefinition)super.getPropertyForName("nukeConfig").getValue();
	}

	@Override
	public boolean requiresPosition() {
		return true;
	}

	@Override
	public void apply(Position p, Player player) {
		//first check to make sure the player has enough energy and charge the player if they do
		if(player.getPlayerEnergy() < this.getEnergyCost())
			return;
		player.setPlayerEnergy(player.getPlayerEnergy() - this.getEnergyCost());
		
		//next find the lane that this projectile needs to go in
		Lane closest = null;
		double dis = Double.POSITIVE_INFINITY;
		for(Lane l : player.getGameState().getMap().getLanes())
		{
			double d = p.distanceSquared(l.getPosition(l.getClosestPointRatio(p)).getPosition());
			if(d < dis)
			{
				dis = d;
				closest = l;
			}
		}
		
		//next create the projectile
		Projectile nuke = this.getNukeConfig().createMapItem(new Transformation(p, 0), player, player.getGameState());
		
		//finally place the projectile in the lane
		closest.addProjectile(nuke);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof GiantNuke) &&
				(((GiantNuke)obj).getNukeConfig().equals(this.getNukeConfig()));
	}

}
