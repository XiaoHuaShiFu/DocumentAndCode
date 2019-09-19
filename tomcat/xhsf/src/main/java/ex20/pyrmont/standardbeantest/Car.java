package ex20.pyrmont.standardbeantest;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-18 18:06
 */
public class Car implements CarMBean{

    private String color = "red";

    public void drive() {
        System.out.println("HHX you can drive my car.");
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
