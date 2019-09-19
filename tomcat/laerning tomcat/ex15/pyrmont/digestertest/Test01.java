package ex15.pyrmont.digestertest;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-16 19:15
 */
public class Test01 {

    public static void main(String[] args) throws IOException, SAXException {
        String path = System.getProperty("user.dir") + File.separator + "etc";
        File file = new File(path, "employee1.xml");
        Digester digester = new Digester();
        digester.addObjectCreate("employee", "ex15.pyrmont.digestertest.Employee");
        digester.addSetProperties("employee");
        digester.addCallMethod("employee", "printName");


        Employee employee = (Employee) digester.parse(file);
        System.out.println("First name: " + employee.getFirstName());
        System.out.println("Last name: " + employee.getLastName());
    }

}
