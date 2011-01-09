package editor.abilities.data;

public class PositiveReal extends Real {

	public PositiveReal(double d) {
		super(d);
	}
	
	@Override
	public boolean checkValid()
	{
		return this.getData() > 0;
	}
	
	@Override
	public String getDescription()
	{
		return "A positive real value";
	}

}
