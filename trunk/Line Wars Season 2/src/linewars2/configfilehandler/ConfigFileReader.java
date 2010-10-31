package linewars2.configfilehandler;

import java.io.InputStream;

/**
 * This class encapsulates the process of reading data from a config file.
 * @author Knexer
 *
 */
public class ConfigFileReader {
	
	/**
	 * Instantiates the ConfigFileReader on the given string, which is assumed to be a filepath.
	 * If the file does not exist, throws an exception.
	 * 
	 * @param path
	 * The filepath to the file to be read.
	 */
	ConfigFileReader(String URI){
		
	}
	
	/**
	 * Instantiates the ConfigFileReader on the given InputStream.
	 * @param in
	 * The stream from which to read data.
	 */
	ConfigFileReader(InputStream in){
		
	}
	
	/**
	 * Reads the data in this Reader's file or input stream into a new ConfigData object.
	 * 
	 * @return
	 * A ConfigData object which contains the configuration data stored in the file.
	 */
	ConfigData read(){
		return null;
	}
}
