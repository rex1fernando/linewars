package linewars.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Abstracts actual interaction with the network protocol, including ensuring
 * that all information is received properly (dropped packets)
 * 
 * Sends Messages over the network to a specified address
 * 
 * Converts incoming network information into Messages
 * 
 * Can be polled for Messages from a specific address for a specific tick id
 * 		- Urgent polling is urgent; this could trigger resend requests, if applicable
 * 		- Normal polling is not urgent, never triggers a resend request
 * 
 * @author Titus Klinge
 * 
 */
public class Gatekeeper
{
	
	/**
	 * Deserializes an array of bytes into an object.
	 * 
	 * @param b
	 *            The serialized byte array to be used in instantiating the object.
	 * @return The instantiated object representation of the bytes provided.
	 * @throws ClassNotFoundException
	 *             Class of a serialized object cannot be found.
	 * @throws IOException
	 *             If an IO error occurs during the deserialization process.
	 */
	public Object deSerialize(byte[] b) throws ClassNotFoundException, IOException
	{
		ObjectInputStream os = new ObjectInputStream(new ByteArrayInputStream(b));
		Object o = os.readObject();
		os.close();
		return o;
	}
	
	/**
	 * Serializes a serializable object into an array of bytes.
	 * 
	 * @param s
	 *            The serializable object to be serialized.
	 * @return An array of bytes representing the object provided.
	 * @throws IOException
	 *             If any IO error occurs during serialization.
	 */
	public byte[] serialize(Serializable s) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(s);
		byte[] toReturn = out.toByteArray();
		os.close();
		return toReturn;
	}
}
