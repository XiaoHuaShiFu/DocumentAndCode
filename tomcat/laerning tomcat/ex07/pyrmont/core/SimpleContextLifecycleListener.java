package ex07.pyrmont.core;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-12 15:33
 */
public class SimpleContextLifecycleListener implements LifecycleListener {
    public void lifecycleEvent(LifecycleEvent event) {
        System.out.println("SimpleContextLifecycleListener's event " + event.getType());
        if (Lifecycle.START_EVENT.equals(event.getType())) {
            System.out.println("Starting context.");
        } else if (Lifecycle.STOP_EVENT.equals(event.getType())) {
            System.out.println("Stopping context.");
        }
    }
}
