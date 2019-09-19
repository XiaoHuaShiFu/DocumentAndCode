package ex03.pyrmont.connector.http;

import java.io.File;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-10 17:11
 */
public class Constants {

    /** WEB_ROOT is the directory where our HTML and other files reside.
     *  For this package, WEB_ROOT is the "webroot" directory under the working
     *  directory.
     *  The working directory is the location in the file system
     *  from where the java command was invoked.
     */
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator  + "webroot";

    /**
     * package path
     */
    public static final String PACKAGE = "ex03.pyrmont.connector.http";

    /**
     * default connection timeout
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT = 60000;

    /**
     * processor idle
     */
    public static final int PROCESSOR_IDLE = 0;

    /**
     * processor active
     */
    public static final int PROCESSOR_ACTIVE = 1;

}
