<%@ page contentType="text/plain" %><jsp:useBean id="bean" class="org.apache.tester.unshared.UnsharedSessionBean"/>JspBeans03 PASSED
lifecycle = <%= bean.getLifecycle() %>
stringProperty= <%= bean.getStringProperty() %>
toString = <%= bean.toString() %>
