package ex15.pyrmont.digestertest;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-16 19:38
 */
public class EmployeeRuleSet extends RuleSetBase {
    @Override
    public void addRuleInstances(Digester digester) {
        digester.addObjectCreate("employee", "ex15.pyrmont.digestertest.Employee");
        digester.addSetProperties("employee");
        digester.addObjectCreate("employee/office", "ex15.pyrmont.digestertest.Office");
        digester.addSetProperties("employee/office");
        digester.addSetNext("employee/office", "addOffice");
        digester.addObjectCreate("employee/office/address", "ex15.pyrmont.digestertest.Address");
        digester.addSetProperties("employee/office/address");
        digester.addSetNext("employee/office/address", "setAddress");
    }
}
