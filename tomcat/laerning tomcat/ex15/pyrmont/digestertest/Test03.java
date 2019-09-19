package ex15.pyrmont.digestertest;

import org.apache.catalina.startup.ContextConfig;
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
public class Test03 {

    public static void main(String[] args) throws IOException, SAXException {
        String path = System.getProperty("user.dir") + File.separator + "etc";
        File file = new File(path, "employee2.xml");
        Digester digester = new Digester();
        digester.addRuleSet(new EmployeeRuleSet());

        Employee employee = (Employee) digester.parse(file);
        System.out.println(employee);
    }

}
