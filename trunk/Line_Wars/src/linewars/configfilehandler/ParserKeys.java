package linewars.configfilehandler;

/**
 * This enum enumerates all possible meanings of values loaded from config files.
 * 
 * Feel free to create your own keys - but ensure that, if you reuse an existing key, a collision between
 * the two meanings cannot occur.
 * 
 * @author Taylor Bergquist
 *
 */
public enum ParserKeys {
	valid,
	ValidStates, name, cost, 
	buildTime, maxHP, velocity, 
	abilities, functionType, 
	coefficients, costFunction, 
	researchTime, body, collisionStrategy,
	type, combatStrategy, 
	movementStrategy, speed,
	impactStrategy, damage, unitURI,
	techURI, projectileURI, buildingURI, range,
	icon, imageHeight, imageWidth, shapes,
	displayTime, mapItemURI, field,
	valueFunction, preReqs, commandCenterURI, 
	maxTimesResearchable, circle, rectangle,
	radius, width, height, rotation,
	shapetype, x, y, lanes, nodes, p0, p1, p2, p3,
	buildingSpots, shape, commandCenterTransformation,
	isStartNode, gateURI, pressedIcon, rolloverIcon,
	selectedIcon,Idle, Dead, Constructing, Active, Moving,
	techtype, controlPoint, shootCoolDown, stuffIncome, 
	modifiertype, tooltip, modifiedURI, URI, 
	modifiedKey, key, modifier, raceURI, abilityURI,
	animationURI, scale, mapURI, incomplete
}
