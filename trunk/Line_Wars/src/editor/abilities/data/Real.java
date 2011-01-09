package editor.abilities.data;


public class Real implements Data<Double> {
	private double val;
	public Real(double d)
	{
		val = d;
	}
	@Override
	public Double getData() {
		return val;
	}

	@Override
	public Double setData(Double newData) {
		val = newData;
		return val;
	}
	@Override
	public boolean checkValid() {
		return true;
	}
	@Override
	public String getDescription() {
		return "A real value";
	}
}