package InOutStream;

import java.io.*;

public class WRTest {

	public static void main(String[] args) throws IOException {
		String fileName = "file\\destFile.dat";
		File srcFile = new File(fileName);
		File destFile = new File("file\\WRFile.dat");
		WRUtils.printFileByCharArray(fileName);
		WRUtils.copyFileByCharArray(srcFile, destFile);
		
		File FWRFile = new File("fi'le\\FWRFile.dat");
		FileWRUtils.copyFile(srcFile,FWRFile);
		
		File BWRUFile = new File("file\\BWRUFile.dat");
		BWRUtils.copyFile(srcFile, BWRUFile);
	}
	

}
