package menu.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class NetworkUtil
{
	public static Object readObject(ObjectInputStream in)
	{
		try {
			return in.readObject();
		} catch (ClassNotFoundException e) {
			// TODO handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	public static void writeObject(ObjectOutputStream out, Object obj)
	{
		try {
			out.writeObject(obj);
		} catch (IOException e) {
			// TODO handle exception
			e.printStackTrace();
		}
	}
}
