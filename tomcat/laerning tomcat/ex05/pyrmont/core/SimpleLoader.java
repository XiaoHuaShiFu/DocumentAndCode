package ex05.pyrmont.core;

import org.apache.catalina.Container;
import org.apache.catalina.DefaultContext;
import org.apache.catalina.Loader;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-12 0:23
 */
public class SimpleLoader implements Loader {

    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator  + "webroot";

    private ClassLoader classLoader;

    private Container container;

    public SimpleLoader() {
        URLStreamHandler streamHandler = null;
        File classPath = new File(WEB_ROOT);
        try {
            String repository =
                    new URL("file", null,classPath.getCanonicalPath() + File.separator).toString();
            URL url = new URL(null, repository, streamHandler);
            classLoader = new URLClassLoader(new URL[] {url});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public DefaultContext getDefaultContext() {
        return null;
    }

    public void setDefaultContext(DefaultContext defaultContext) {

    }

    public boolean getDelegate() {
        return false;
    }

    public void setDelegate(boolean delegate) {

    }

    public String getInfo() {
        return "A simple loader";
    }

    public boolean getReloadable() {
        return false;
    }

    public void setReloadable(boolean reloadable) {

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {

    }

    public void addRepository(String repository) {

    }

    public String[] findRepositories() {
        return new String[0];
    }

    public boolean modified() {
        return false;
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {

    }
}
