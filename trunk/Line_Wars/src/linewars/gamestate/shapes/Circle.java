package linewars.gamestate.shapes;

import linewars.gamestate.Transformation;

public class Circle extends Shape {

	@Override
	public Shape stretch(Transformation change) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Shape transform(Transformation change) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Transformation position() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Circle boundingCircle() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public Rectangle boundingRectangle() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getRadius(){return 0;}//TODO
}
