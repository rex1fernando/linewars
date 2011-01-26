package editor.abilities.data;

public interface Data<T> {

	public T getData();

	public T setData(T newData);

	public boolean checkValid();
	
	public String getDescription();

}