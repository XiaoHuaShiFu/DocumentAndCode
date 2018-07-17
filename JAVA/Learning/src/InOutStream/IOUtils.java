package InOutStream;

import java.io.*;
/**
 * 字节流
 * FileInputStream类
 * FileOutputStream类
 * @author lenovo
 *
 */
public class IOUtils {

	/**
	 * 以16进制的方式打印文件，每10个字节换行
	 * @param fileName
	 * @throws IOException
	 */
	public static void printHex(String fileName) throws IOException{
		FileInputStream in = new FileInputStream(fileName);
		int b;
		int i = 1;
		while ((b = in.read()) != -1) {
			if (b <= 0xf) {
				System.out.print("0");
			}
			if (i == 10) {
				System.out.println(Integer.toHexString(b & 0xff) + "  ");
				i = 1;
			} else {
				System.out.print(Integer.toHexString(b & 0xff) + "  ");
				i++;
			}
		}
		in.close();
	}
	
	/**
	 * 以16进制的方式打印文件，每10个字节换行
	 * 一次读取整个数组
	 * @param fileName
	 * @throws IOException
	 */
	public static void printHexByByteArray(String fileName) throws IOException{
		FileInputStream in = new FileInputStream(fileName);
		byte[] buf = new byte[20 * 1024];
		int bytes;
		int j = 1;
		while ((bytes = in.read(buf, 0, buf.length)) != -1) {
			for (int i = 0; i < bytes; i++) {
				if (buf[i] <= 0xf) {
					System.out.print("0");
				}
				if (j == 10) {
					System.out.println(Integer.toHexString(buf[i] & 0xff) + "  ");
					j = 1;
				} else {
					System.out.print(Integer.toHexString(buf[i]  & 0xff) + "  ");
					j++;
				}
			}
		}
		in.close();
	}
	
	/**
	 * 文件拷贝，字节批量读取(快)
	 * @param srcFile
	 * @param destFile
	 * @throws IOException
	 */
	public static void copyFile(File srcFile, File destFile) throws IOException{
		if (!srcFile.exists()) {
			throw new IllegalArgumentException("文件:" + srcFile + "不存在.");
		}
		if (!srcFile.isFile()) {
			throw new IllegalArgumentException(srcFile + "不是文件.");
		}
		FileInputStream in = new FileInputStream(srcFile);
		FileOutputStream out = new FileOutputStream(destFile);
		byte[] buf = new byte[20 * 1024];
		int b;
		while((b = in.read(buf,0,buf.length)) != -1) {
			out.write(buf, 0, b);
			out.flush();//把流中的残余数据冲刷下去
		}
		in.close();
		out.close();
	}
	
	/**
	 * 文件拷贝，带缓冲区的单字节拷贝(特别慢中的比较快)
	 * @param srcFile
	 * @param destFile
	 * @throws IOException
	 */
	public static void copyFileByBuffer(File srcFile, File destFile) throws IOException{
		if (!srcFile.exists()) {
			throw new IllegalArgumentException("文件:" + srcFile + "不存在.");
		}
		if (!srcFile.isFile()) {
			throw new IllegalArgumentException(srcFile + "不是文件.");
		}
		FileInputStream in = new FileInputStream(srcFile);
		BufferedInputStream bis = new BufferedInputStream(in);
		FileOutputStream out = new FileOutputStream(srcFile);
		BufferedOutputStream bos = new BufferedOutputStream(out);
		int c;
		while((c = bis.read()) != -1) {
			bos.write(c);
			//刷新缓冲区
			bos.flush();
		}
		bis.close();
		bos.close();
	}
	
	/**
	 * 文件拷贝，单字节拷贝(特别慢)
	 * @param srcFile
	 * @param destFile
	 * @throws IOException
	 */
	public static void copyFileByte(File srcFile, File destFile) throws IOException{
		if (!srcFile.exists()) {
			throw new IllegalArgumentException("文件:" + srcFile + "不存在.");
		}
		if (!srcFile.isFile()) {
			throw new IllegalArgumentException(srcFile + "不是文件.");
		}
		FileInputStream in = new FileInputStream(srcFile);
		FileOutputStream out = new FileOutputStream(srcFile);
		int c;
		while((c = in.read()) != -1) {
			out.write(c);
			//刷新缓冲区
			out.flush();
		}
		in.close();
		out.close();
	}
	
}
