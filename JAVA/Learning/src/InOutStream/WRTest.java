package InOutStream;

import java.io.*;

public class WRTest {

	public static void main(String[] args) throws IOException {
		String fileName = "file\\dosFile.dat";
		File srcFile = new File(fileName);
		File destFile = new File("file\\WRFile.dat");
		WRUtils.printFileByCharArray(fileName);
		WRUtils.copyFileByCharArray(srcFile, destFile);
	}

}
