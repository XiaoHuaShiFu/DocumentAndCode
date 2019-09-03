package top.xiaohuashifu.jvm.util;

/**
 * 描述: Bytes数组处理工具
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-03 20:42
 */
public class ByteUtils {

    /**
     * 将byte数组转换int类型
     *
     * @param b byte数组
     * @param start 开始
     * @param len 长度
     * @return int类型
     */
    public static int bytes2Int(byte[] b, int start, int len) {
        int sum = 0;
        int end = start + len;
        for (int i = start; i < end; i++) {
            int n = ((int) b[i]) & 0xff;
            n <<= (--len) * 8;
            sum = n + sum;
        }
        return sum;
    }

    /**
     * 整型转换成字节数组
     *
     * @param value 整型值
     * @param len 字节数组长度
     * @return 字节数组
     */
    public static byte[] int2Bytes(int value, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[len - i - 1] = (byte) ((value >> 8 * i) & 0xff);
        }
        return b;
    }

    /**
     * 字节数组转换成字符串
     *
     * @param b 字节数组
     * @param start 开始下标
     * @param len 长度
     * @return 字符串
     */
    public static String bytes2String(byte[] b, int start, int len) {
        return new String(b, start, len);
    }

    /**
     * 把字符串转换成字节数组
     *
     * @param str 字符串
     * @return 字节数组
     */
    public static byte[] string2Bytes(String str) {
        return str.getBytes();
    }

    /**
     * 替换某串字节
     *
     * @param originalBytes 原字节
     * @param offset 起始下标
     * @param len 长度
     * @param replaceBytes 替换的字节
     * @return 新字节
     */
    public static byte[] bytesReplace(byte[] originalBytes, int offset, int len, byte[] replaceBytes) {
        byte[] newBytes = new byte[originalBytes.length + (replaceBytes.length - len)];
        System.arraycopy(originalBytes, 0, newBytes, 0, offset);
        System.arraycopy(replaceBytes, 0, newBytes, offset, replaceBytes.length);
        System.arraycopy(originalBytes, offset + len, newBytes, offset + replaceBytes.length,
                originalBytes.length - offset -len);
        return newBytes;
    }

}
