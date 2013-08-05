package utility;

import java.net.URL;

public class Resources
{
	public static URL load(String path)
	{
		return Resources.class.getResource(path);
	}
}
