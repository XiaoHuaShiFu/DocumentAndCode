package InOutStream;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Raf {

	public static void main(String[] args) throws IOException {
	
		
		File file = new File("file");
		if (!file.exists()) {
			file.mkdirs();
		}
		File cfile = new File(file,"raf.dat");
		if (!cfile.exists()) {
			cfile.createNewFile();
		}
		
		/**
		 * RandomAccessFile支持随机读写操作
		 */
		RandomAccessFile raf = new RandomAccessFile(cfile,"rw");
		
		//获取当前指针位置
		System.out.println(raf.getFilePointer());
		//写一个字节
		raf.write('A');
		
		
		int i = 0x7fffffff;
		//右移24位获取高八位
		raf.write(i >>> 24);
		raf.write(i >>> 16);
		raf.write(i >>> 8);
		raf.write(i);
		
		//写一个字节数组
		raf.writeInt(i);
		
		//写一个中文
		String str = "中";
		byte[] bytes = str.getBytes();
		raf.write(bytes);
		
		
		//移动指针
		raf.seek(0);
		
		//一次性读取数据
		byte[] buf = new byte[(int)raf.length()];
		raf.read(buf);
		//Arrays.toString()把数组转换成字符串输出
		System.out.println(Arrays.toString(buf));
		//构造成字符串输出
		String str1 = new String(buf);
		System.out.println(str1);
		
		
		//关闭文件
		raf.close();
		
	}
	
}
