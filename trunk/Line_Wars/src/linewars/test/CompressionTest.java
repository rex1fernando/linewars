package linewars.test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CompressionTest
{
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		FileInputStream file = new FileInputStream(new File(System.getProperty("user.dir") + "\\resources\\images\\Desert.png"));
		byte[] buffer = new byte [1024];
		int length = 0;
		int read = 0;
		while(read >= 0)
		{
			read = file.read(buffer);
			length += read;
		}
		
		System.out.println("file length: " + length);
		
		byte[] imageBytes = new byte[length];
		file.read(imageBytes);
		
		ByteArrayInputStream inStream = new ByteArrayInputStream(imageBytes);
		BufferedImage original = ImageIO.read(inStream);
		

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ImageIO.write(original, "png", outStream);
		int bytelen = outStream.toByteArray().length;

		System.out.println("byte length: " + bytelen);
		
		
//		Deflater compresser = new Deflater();
//		compresser.setInput(original.getData().);
//		compresser.finish();
//		int compressedDataLength = compresser.deflate(output);
	}
}
