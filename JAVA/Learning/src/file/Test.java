package file;

import java.awt.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;

public class Test {
	
	public static void main(String[] args) throws IOException {
		String path = "C:\\Users\\lenovo\\Desktop\\Github\\DocumentAndCode\\JAVA\\Learning\\file";
		//Paths.get方法获取路径
		Path absolute = Paths.get(path);
		System.out.println(absolute);
		Path p = Paths.get(path);
		//Paths.resolve方法拼接或获取路径
		p = p.resolve("work");
		System.out.println(p);
		//Paths.resolveSibling方法产生兄弟路径
		Path q = p.resolveSibling("brother\\test.dat");
		System.out.println(q);
		//Paths.relativize方法,会产生一个相对路径，（两个路径不同的地方）
		Path r = p.relativize(q);
		System.out.println(r);
		//Paths.normalize方法将移除所有.和..
		Path n = r.normalize();
		System.out.println(n);
		//Paths.toAbsolutePath方法将返回从根部路径开始的绝对路径
		Path tap = r.toAbsolutePath();
		System.out.println(tap);
		//Path类的getParent方法
		Path parent = q.getParent();
		System.out.println(parent);
		//Path类的getFileName()方法将返回文件名
		Path file = q.getFileName();
		System.out.println(file);
		//Path类的root方法将返回根路径
		Path root = q.getRoot();
		System.out.println(root);
		
		
		////////////////////////////////////////////////////////////////////////////////////
		Path filePath = absolute.resolve("destFile.dat");
		//读取文件的所有内容
		byte[] bytes = Files.readAllBytes(filePath);
		//转换为字符串
		String content = new String(bytes);
		System.out.println(content);
		//一行一行读取数据
        List<String> lines = Files.readAllLines(filePath);
		System.out.println(lines);
		//写出一个字符串到文件中
		String str = "lalaawjx...123abc";
 		Files.write(filePath, str.getBytes());
 		//向文件追加内容
		Files.write(filePath, str.getBytes(), StandardOpenOption.APPEND);
		
		//////////////////////////////////////////////////////////////////////////////
		//创建目录,只能创建一层
		Path p1 = absolute.resolve("aFile");
		Path p2 = absolute.resolve("bFile\\b.dat");
		Path p3 = absolute.resolve("aFile\\a.dat");
//		Files.createDirectory(p1);
		//创建目录，可创建多层
		
//		Files.createDirectories(p2);
		//创建文件
//		Files.createFile(p2);
		//创建临时文件
//		Path newPath1 = Files.createTempFile(p1, "abc", ".txt");
//		System.out.println(newPath1);
//		Path newPath2 = Files.createTempFile(null, ".txt");
//		System.out.println(newPath2);
		//创建临时目录
//		Path newPath3 = Files.createTempDirectory(p1, "abc");
//		System.out.println(newPath3);
//		Path newPath4 = Files.createTempDirectory(null);
//		System.out.println(newPath4);
		
		//复制文件
//		Files.copy(p2, p3);
		//移动文件
//		Files.move(p2, p3);
		//删除文件
//		Files.delete(p2);
		
		//exists判断文件是否存在
		System.out.println(Files.exists(p3));
		//size方法返回文件的字节数
		System.out.println(Files.size(p3));
		//getOwner方法将返回文件的拥有者
		System.out.println(Files.getOwner(p3));
		
		////////////////////////////////////////////////////////////////////////
		//内存映射文件
		FileChannel channel = FileChannel.open(p3);
		MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		CRC32 crc = new CRC32();
		
		while(buffer.hasRemaining()) {
			//顺序访问
			byte b = buffer.get();
			crc.update(b);
			System.out.print(b + " ");
		} System.out.println();
		System.out.println(Long.toHexString(crc.getValue()));
		
		ByteBuffer bb = ByteBuffer.allocate((int)channel.size());
		channel.read(bb);
		while(bb.hasRemaining()) {
			byte b = bb.get();
			System.out.print(b + " ");
		} System.out.println();
		
		
//		for(int i = 0; i < buffer.limit(); i++) {
//			//随机访问
//			byte b = buffer.get(i);
//			System.out.print(b + " ");
//		} System.out.println();
		
		//字节数组方式访问
//		byte[] by = new byte[buffer.limit()];
//		buffer.get(by, 0, buffer.limit());
//		System.out.println(Arrays.toString(by));
		
		//int，long，short，char，float，double等类型访问
//		int a = buffer.getInt();
//		System.out.println(a);
		
		//获取buffer的当前位置，长度等信息
//		System.out.println(buffer.order(ByteOrder.LITTLE_ENDIAN));
		//获取buffer的二进制处理方式
//		buffer.order();
		
		//向缓冲区写数字
//		buffer.putInt(12);
//		buffer.putChar('a');
//		buffer.putDouble(3.14);
		
		///////////////////////////////////////////////////////////////////////////////////////
		//获取一个锁
		FileLock lock = channel.lock();
		//释放一个锁
		lock.release();
		
		
	}
	
}
