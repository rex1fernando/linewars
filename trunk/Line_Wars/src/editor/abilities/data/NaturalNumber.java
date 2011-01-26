package editor.abilities.data;

public class NaturalNumber implements Data<Integer> {
	
	private int val;
	
	public NaturalNumber(int i)
	{
		val = i;
	}

	@Override
	public Integer getData() {
		return val;
	}

	@Override
	public Integer setData(Integer newData) {
		val = newData;
		return val;
	}

	@Override
	public boolean checkValid() {
		return val >= 0;
	}

	@Override
	public String getDescription() {
		return "A natural number";
	}

}
