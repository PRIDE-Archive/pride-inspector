package uk.ac.ebi.pride.toolsuite.gui.desktop;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.toolsuite.gui.EDTUtils;
import uk.ac.ebi.pride.toolsuite.gui.component.status.StatusBar;
import uk.ac.ebi.pride.toolsuite.gui.listeners.ExitListener;
import uk.ac.ebi.pride.toolsuite.gui.utils.PropertyChangeHelper;

import javax.swing.*;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashSet;

/**
 * @author rwang
 * @author ypriverol
 * Date: 21-Jan-2010
 * Time: 11:25:25
 */
public abstract class Desktop extends PropertyChangeHelper {

    private static final Logger logger = Logger.getLogger(Desktop.class.getName());

    private final Collection<ExitListener> exitListeners;
    private static DesktopContext desktopContext;
    private static Desktop desktop = null;

    /**
     * Constructs a <code>PropertyChangeSupport</code> object.
     */
    protected Desktop() {
        this.exitListeners = new HashSet<ExitListener>();
    }

    public static synchronized <T extends Desktop, V extends DesktopContext> void launch(final Class<T> appClass,
                                                                                         final Class<V> appContextClass,
                                                                                         final String[] args) {
        Runnable mainThread = new Runnable() {
            @Override
            public void run() {
                try {
                    desktop = buildApp(appClass);
                    desktopContext = buildAppContext(appContextClass);
                    desktop.init(args);
                    desktop.ready();
                    desktop.show();
                    desktop.postShow();
                } catch (Exception ex) {
                    String message = "Error occurred during building desktop instance";
                    logger.log(Level.FATAL, message, ex);
                    throw (new Error(message, ex));
                }
            }
        };
        EDTUtils.invokeLater(mainThread);
    }

    private static <V extends DesktopContext> V buildAppContext(Class<V> appContextClass) throws Exception {
        Constructor<V> cstruct = appContextClass.getDeclaredConstructor();
        V appContext = cstruct.newInstance();
        appContext.setDesktop(desktop);
        return appContext;
    }

    private static <T extends Desktop> T buildApp(final Class<T> appClass) throws Exception {
        // Get the declared constructor and create a new instance of the Desktop
        Constructor<T> cstruct = appClass.getDeclaredConstructor();
        return cstruct.newInstance();
    }

    public static synchronized <R extends Desktop> R getInstance(Class<R> appClass) {
        checkAppLaunchState();
        return appClass.cast(desktop);
    }

    public static synchronized Desktop getInstance() {
        checkAppLaunchState();
        return desktop;
    }

    private static void checkAppLaunchState() {
        if (desktop == null)
            throw new IllegalStateException("Desktop is not launched");
    }

    public abstract void init(String[] args);

    public abstract void ready();

    public abstract void show();

    public abstract void postShow();

    public abstract void finish();

    public final void shutdown(EventObject event) {
        Runnable exitThread = new Runnable() {

            @Override
            public void run() {
                // check whether all exit listeners are ready to shutdown
                for (ExitListener exitListener : exitListeners) {
                    if (!exitListener.isReady()) {
                        return;
                    }
                }
                // run shutdown sequence
                int exitCode = 0;
                try {
                    for (ExitListener exitListener : exitListeners) {
                        exitListener.performExit();
                    }
                    // call finish method before shutdown
                    finish();
                } catch (Exception ex) {
                    logger.log(Level.ERROR, "Error during shutdown process", ex);
                    exitCode = 1;
                } finally {
                    Runtime.getRuntime().exit(exitCode);
                }
            }
        };
        EDTUtils.invokeLater(exitThread);
    }

    public final DesktopContext getDesktopContext() {
        return desktopContext;
    }

    public final Collection<ExitListener> getExitListeners() {
        return exitListeners;
    }

    public final void addExitListeners(ExitListener listener) {
        exitListeners.add(listener);
    }

    public final void removeExitListener(ExitListener listener) {
        exitListeners.remove(listener);
    }

    public abstract JFrame getMainComponent();

    public abstract JMenuBar getMenuBar();

    public abstract StatusBar getStatusBar();
}
