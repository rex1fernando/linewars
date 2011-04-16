package linewars.test;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.Deflater;

import javax.imageio.ImageIO;

public class CompressionTest
{
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		File file = new File(System.getProperty("user.dir") + "\\resources\\images\\Desert.png");
		FileInputStream fis = new FileInputStream(file);
		long length = file.length();
		
		System.out.println("file length: " + length);
		
		byte[] imageBytes = new byte[(int)length];
		fis.read(imageBytes);
		
		ByteArrayInputStream inStream = new ByteArrayInputStream(imageBytes);
		BufferedImage original = ImageIO.read(inStream);
		
		Raster data = original.getData();
		int dataType = data.getDataBuffer().getDataType();
		int bytes;
		switch(dataType)
		{
		case DataBuffer.TYPE_BYTE:
			bytes = Byte.SIZE / 8;
			break;
		case DataBuffer.TYPE_DOUBLE:
			bytes = Double.SIZE / 8;
			break;
		case DataBuffer.TYPE_FLOAT:
			bytes = Float.SIZE / 8;
			break;
		case DataBuffer.TYPE_INT:
			bytes = Integer.SIZE / 8;
			break;
		case DataBuffer.TYPE_SHORT:
			bytes = Short.SIZE / 8;
			break;
		case DataBuffer.TYPE_USHORT:
			bytes = Short.SIZE / 8;
			break;
		case DataBuffer.TYPE_UNDEFINED:
			bytes = 0;
			break;
		default:
			bytes = 0;
		}
		
		int channels = data.getNumBands();

		int pixels = data.getWidth() * data.getHeight();
		System.out.println("byte length: " + pixels * channels * bytes);
		
		
		Deflater compresser = new Deflater();
		compresser.setInput(imageBytes);
		compresser.finish();
		
		byte[] compressed = new byte[(int)length * 2];
		int compressedDataLength = compresser.deflate(compressed);
		System.out.println("comp length: " + compressedDataLength);
	}
}
