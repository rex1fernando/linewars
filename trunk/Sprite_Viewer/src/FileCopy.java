import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 
 * This class was taken from 
 * http://www.java2s.com/Code/Java/File-Input-Output/CopyfilesusingJavaIOAPI.htm
 *
 */
public class FileCopy {
  public static void main(String[] args) {
    try {
      copy("fromFile.txt", "toFile.txt");
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  public static void copy(String fromFileName, String toFileName)
      throws IOException {
    File fromFile = new File(fromFileName);
    File toFile = new File(toFileName);

    if (!fromFile.canRead())
      throw new IOException("FileCopy: " + "source file is unreadable: "
          + fromFileName);

    

    FileInputStream from = null;
    FileOutputStream to = null;
    try {
      from = new FileInputStream(fromFile);
      to = new FileOutputStream(toFile);
      byte[] buffer = new byte[4096];
      int bytesRead;

      while ((bytesRead = from.read(buffer)) != -1)
        to.write(buffer, 0, bytesRead); // write
    } finally {
      if (from != null)
        try {
          from.close();
        } catch (IOException e) {
          ;
        }
      if (to != null)
        try {
          to.close();
        } catch (IOException e) {
          ;
        }
    }
  }
}