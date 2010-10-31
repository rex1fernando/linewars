package linewars2.configfilehandler;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class encapsulates the process of writing configuration data to a config file.
 * 
 * @author Knexer
 *
 */
public class ConfigFileWriter {
	
	/**
	 * Instantiates the ConfigFileWriter on the given string, which is assumed to be a filepath.
	 * If a file already exists at the destination, it will be overwritten.
	 * 
	 * @param path
	 * The filepath to the file to be created.
	 */
	ConfigFileWriter(String path){
		
	}
	
	/**
	 * Instantiates the ConfigFileWriter on the given OutputStream.
	 * @param in
	 * The stream to which to write data.
	 */
	ConfigFileWriter(OutputStream in){
		
	}
	
	/**
	 * Writes the data in the supplied ConfigData object to this Writer's output file or stream.
	 * @param toWrite
	 * The data which is to be written.
	 */
	void write(ConfigData toWrite){
		
	}
}
