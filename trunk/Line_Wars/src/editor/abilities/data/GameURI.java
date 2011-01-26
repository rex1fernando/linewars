package editor.abilities.data;

public abstract class GameURI implements Data<String> {

	private String uri;
	
	public GameURI(String s)
	{
		uri = s;
	}
	
	@Override
	public String getData() {
		return uri;
	}

	@Override
	public String setData(String newData) {
		uri = newData;
		return uri;
	}

	@Override
	public boolean checkValid() {
		return true;
	}

}
