package ex20.pyrmont.standardbeantest1;

import javax.management.*;
import javax.management.modelmbean.*;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-18 18:09
 */
public class ModelAgent {

    private String MANAGED_CLASS_NAME = "ex20.pyrmont.modelmbeantest1.Car";
    private MBeanServer mBeanServer = null;

    public ModelAgent() {
        mBeanServer = MBeanServerFactory.createMBeanServer();
    }

    public MBeanServer getMBeanServer() {
        return mBeanServer;
    }

    private ObjectName createObjectName(String name) {
        ObjectName objectName = null;
        try {
            objectName = new ObjectName(name);
        }
        catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
        return objectName;
    }

    private ModelMBean createMBean(ObjectName objectName, String mbeanName) {
        ModelMBeanInfo mBeanInfo = createModelMBeanInfo(objectName, mbeanName);
        RequiredModelMBean modelMBean = null;
        try {
            modelMBean = new RequiredModelMBean(mBeanInfo);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return modelMBean;
    }

    private ModelMBeanInfo createModelMBeanInfo(ObjectName inMbeanObjectName, String inMbeanName) {
        ModelMBeanInfo mBeanInfo = null;
        ModelMBeanAttributeInfo[] attributes = new ModelMBeanAttributeInfo[1];
        ModelMBeanOperationInfo[] operations = new ModelMBeanOperationInfo[3];
        try {
            attributes[0] = new ModelMBeanAttributeInfo("Color", "java.lang.String",
                    "the color.", true, true, false, null);
            operations[0] = new ModelMBeanOperationInfo("drive", "the drive method",
                    null, "void", MBeanOperationInfo.ACTION, null);
            operations[1] = new ModelMBeanOperationInfo("getColor", "get color attribute",
                    null, "java.lang.String", MBeanOperationInfo.ACTION, null);

            Descriptor setColorDesc = new DescriptorSupport("name=setColor", "descriptorType=operation",
                    "class=" + MANAGED_CLASS_NAME, "role=operation");
            MBeanParameterInfo[] setColorParams = new MBeanParameterInfo[] {
                    (new MBeanParameterInfo("new color", "java.lang.String",
                            "new Color value") )} ;
            operations[2] = new ModelMBeanOperationInfo("setColor",
                    "set Color attribute", setColorParams, "void",
                    MBeanOperationInfo.ACTION, setColorDesc);

            mBeanInfo  = new ModelMBeanInfoSupport(MANAGED_CLASS_NAME,
                    null, attributes, null, operations, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return mBeanInfo;
    }

    public static void main(String[] args) throws MBeanException, InstanceNotFoundException, InvalidTargetObjectTypeException, InstanceAlreadyExistsException, NotCompliantMBeanException, AttributeNotFoundException, ReflectionException, InvalidAttributeValueException {
        ModelAgent agent = new ModelAgent();
        MBeanServer mBeanServer = agent.getMBeanServer();

        String domain = mBeanServer.getDefaultDomain();
        ObjectName objectName = agent.createObjectName(domain + ":type=MyCar");
        String mBeanName = "myMBean";
        ModelMBean modelMBean = agent.createMBean(objectName, mBeanName);
        Car car = new Car();
        modelMBean.setManagedResource(car, "ObjectReference");
        mBeanServer.registerMBean(modelMBean, objectName);

        Attribute attribute = new Attribute("Color", "green");
        mBeanServer.setAttribute(objectName, attribute);
        String color = (String) mBeanServer.getAttribute(objectName, "Color");
        System.out.println(color);

        attribute = new Attribute("Color", "blue");
        mBeanServer.setAttribute(objectName, attribute);
        color = (String) mBeanServer.getAttribute(objectName, "Color");
        System.out.println(color);

        mBeanServer.invoke(objectName, "drive", null, null);

    }

}
