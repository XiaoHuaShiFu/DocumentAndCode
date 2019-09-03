package top.xiaohuashifu.jvm.util;

/**
 * 描述: 为了多次载入执行类而加入的加载器
 * 把defineClass方法开放出来，只有外部显式调用的时候才会使用到loadByte方法
 * 由虚拟机调用时，仍然按照原有的双亲委派规则使用loadClass方法进行类加载
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-03 20:30
 */
public class HotSwapClassLoader extends ClassLoader {

    public HotSwapClassLoader() {
        super(HotSwapClassLoader.class.getClassLoader());
    }

    /**
     * 调用父类的defineClass方法讲提交执行的Java类的byte[]数组转变为Class对象
     *
     * @param classByte class类的byte数组
     * @return class对象
     */
    public Class loadByte(byte[] classByte) {
        return defineClass(null, classByte, 0, classByte.length);
    }

}
