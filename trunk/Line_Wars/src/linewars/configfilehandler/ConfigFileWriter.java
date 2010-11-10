package linewars.configfilehandler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * This class encapsulates the process of writing configuration data to a config file.
 * 
 * @author Knexer
 *
 */
public class ConfigFileWriter {
	
	private OutputStream out;
	
	/**
	 * Instantiates the ConfigFileWriter on the given string, which is assumed to be a filepath.
	 * If a file already exists at the destination, it will be overwritten.
	 * 
	 * @param path
	 * The filepath to the file to be created.
	 * @throws FileNotFoundException 
	 */
	public ConfigFileWriter(String path) throws FileNotFoundException{
		out = new FileOutputStream(path);
	}
	
	/**
	 * Instantiates the ConfigFileWriter on the given OutputStream.
	 * @param in
	 * The stream to which to write data.
	 */
	public ConfigFileWriter(OutputStream in){
		out = in;
	}
	
	/**
	 * Writes the data in the supplied ConfigData object to this Writer's output file or stream.
	 * @param toWrite
	 * The data which is to be written.
	 * @param isValid
	 * specifies whether this is a valid config data or not
	 * @throws IOException 
	 */
	public void write(ConfigData toWrite, boolean isValid) throws IOException{
		if(toWrite.getDefinedKeys().contains(ParserKeys.valid))
			toWrite.remove(ParserKeys.valid, toWrite.getString(ParserKeys.valid));
		out.write(new String(ParserKeys.valid + " = ").getBytes());
		if(isValid)
			out.write(new String("true").getBytes());
		else
			out.write(new String("false").getBytes());
		out.write(new String("\n\n").getBytes());
		out.flush();
		writeRecurse(toWrite, "");
	}
	
	private void writeRecurse(ConfigData toWrite, String prefix) throws IOException {
		List<ParserKeys> keys = toWrite.getDefinedKeys();
		for(ParserKeys key : keys)
		{
			for(String value : toWrite.getStringList(key))
			{
				out.write(new String(prefix + key + " = " + value + "\n").getBytes());
				out.flush();
			}
			
			for(ConfigData value : toWrite.getConfigList(key))
			{
				out.write(new String(prefix + key + " = {\n").getBytes());
				out.flush();
				writeRecurse(value, prefix + "\t");
				out.write(new String("}\n").getBytes());
				out.flush();
			}
		}
	}
}
