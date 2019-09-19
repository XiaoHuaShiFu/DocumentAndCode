package ex20.pyrmont.standardbeantest;

import javax.management.*;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.RequiredModelMBean;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-18 18:09
 */
public class StandardAgent {

    public static void main(String[] args) throws AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, InvalidAttributeValueException {
        StandardAgent agent = new StandardAgent();
        MBeanServer mBeanServer = agent.getMBeanServer();
        String domain = mBeanServer.getDefaultDomain();
        String managedResourceClassName = "ex20.pyrmont.standardbeantest.Car";
        ObjectName objectName = agent.createObjectName(domain + ":type=" + managedResourceClassName);
        agent.createStandardBean(objectName, managedResourceClassName);

        Attribute colorAttribute = new Attribute("Color", "blue");
        mBeanServer.setAttribute(objectName, colorAttribute);
        System.out.println(mBeanServer.getAttribute(objectName, "Color"));
        mBeanServer.invoke(objectName, "drive", null, null);
    }

    private MBeanServer mBeanServer;

    public StandardAgent() {
        this.mBeanServer = MBeanServerFactory.createMBeanServer();
    }

    public MBeanServer getMBeanServer() {
        return mBeanServer;
    }

    public ObjectName createObjectName(String name) {
        ObjectName objectName = null;
        try {
            objectName = new ObjectName(name);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
        return objectName;
    }

    private void createStandardBean(ObjectName objectName, String managedResourceClassName) {
        try {
            mBeanServer.createMBean(managedResourceClassName, objectName);
        } catch (Exception ignored) {
        }
    }

}
