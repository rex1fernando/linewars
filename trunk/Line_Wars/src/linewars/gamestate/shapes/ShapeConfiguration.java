package linewars.gamestate.shapes;

import linewars.gamestate.Transformation;
import configuration.Configuration;

public abstract class ShapeConfiguration extends Configuration {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5372884949906933269L;

	/**
	 * Constructs a Shape specified by this ShapeConfiguration at the position and rotation specified by the provided Transformation
	 * @return The constructed Shape.
	 */
	public abstract Shape construct(Transformation location);
}
