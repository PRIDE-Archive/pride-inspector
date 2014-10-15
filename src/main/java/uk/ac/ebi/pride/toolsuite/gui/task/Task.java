package uk.ac.ebi.pride.toolsuite.gui.task;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Task represents a background running threshold, this is not the same as EDT
 * <p/>
 * User: rwang
 * Date: 25-Jan-2010
 * Time: 11:03:14
 */
@ThreadSafe
public abstract class Task<T, V> extends SwingWorker<T, V> {

    private static final Logger logger = LoggerFactory.getLogger(Task.class);

    public static final String NAME_PROP = "name";
    public static final String DESCRIPTION_PROP = "description";
    public static final String COMPLETED_PROP = "completed";
    public static final String GUI_BLOCKER_PROP = "blocker";

    // task name
    @GuardedBy("this")
    private String name;

    // task description
    @GuardedBy("this")
    private String description;

    // lock for owners collection
    private final Object ownersLock = new Object();
    @GuardedBy("ownersLock")
    private final Collection<Object> owners;

    // lock for task listeners collection
    private final Object taskListenersLock = new Object();
    @GuardedBy("taskListenersLock")
    private final Collection<TaskListener<T, V>> taskListeners;

    // GUI blocker for the task for blocking GUI components
    @GuardedBy("this")
    private GUIBlocker blocker;

    public Task() {
        addPropertyChangeListener(new TaskStateMonitor());
        owners = Collections.synchronizedList(new ArrayList<Object>());
        taskListeners = Collections.synchronizedList(new ArrayList<TaskListener<T, V>>());
    }

    public synchronized String getName() {
        return name;
    }

    public void setName(String n) {
        if (n == null) {
            throw new IllegalArgumentException("Null Task Name");
        } else {
            String oldName, newName;
            synchronized (this) {
                oldName = this.name;
                this.name = n;
                newName = n;
                firePropertyChange(NAME_PROP, oldName, newName);
            }
        }
    }

    public synchronized String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        if (desc == null) {
            throw new IllegalArgumentException("Null Task Description");
        } else {
            String oldDesc, newDesc;
            synchronized (this) {
                oldDesc = this.description;
                this.description = desc;
                newDesc = desc;
                firePropertyChange(DESCRIPTION_PROP, oldDesc, newDesc);
            }
        }
    }

    public synchronized GUIBlocker getGUIBlocker() {
        return blocker;
    }

    public void setGUIBlocker(GUIBlocker bl) {
        if (bl == null) {
            throw new IllegalArgumentException("Null GUI Blocker");
        } else {
            GUIBlocker oldBlocker, newBlocker;
            synchronized (this) {
                oldBlocker = this.blocker;
                this.blocker = bl;
                newBlocker = bl;
                firePropertyChange(GUI_BLOCKER_PROP, oldBlocker, newBlocker);
            }
        }
    }

    /**
     * Add a new owner to the task
     *
     * @param owner owner object could be tasks as well, but it can not be itself.
     */
    public final void addOwner(Object owner) {
        synchronized (ownersLock) {
            if (!this.equals(owner) && !owners.contains(owner)) {
                owners.add(owner);
            }
        }
    }

    /**
     * Get a representative list of owners.
     *
     * @return List<Object> a list of owners
     */
    public final List<Object> getOwners() {
        synchronized (ownersLock) {
            return new ArrayList<Object>(owners);
        }
    }

    /**
     * Default implementation of this method is to cancel the task whenever a owner has been
     * removed.
     *
     * @param owner owner of the task
     */
    public void removeByOwner(Object owner) {
        synchronized (ownersLock) {
            if (!isDone() && owners.contains(owner)) {
                this.cancel(true);
            }
            owners.remove(owner);
        }
    }

    /**
     * Return true if the state of the task is pending.
     *
     * @return boolean  true means pending
     */
    public final boolean isPending() {
        return getState() == StateValue.PENDING;
    }

    /**
     * Return true if the state of the task is started
     *
     * @return boolean  true means pending
     */
    public final boolean isStarted() {
        return getState() == StateValue.STARTED;
    }

    /**
     * Check whether task has a property change listener
     *
     * @param listener property change listener
     * @return boolean true mean exist
     */
    public synchronized boolean hasPropertyChangeListener(PropertyChangeListener listener) {
        for (PropertyChangeListener propertyChangeListener : this.getPropertyChangeSupport().getPropertyChangeListeners()) {
            if (propertyChangeListener.equals(listener)) {
                return true;
            }
        }
        return false;
    }

    public void addTaskListener(TaskListener<T, V> listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null Task Listener");
        }

        synchronized (taskListenersLock) {
            taskListeners.add(listener);
        }
    }

    public void removeTaskListener(TaskListener<T, V> listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null Task Listener");
        }

        synchronized (taskListenersLock) {
            taskListeners.remove(listener);
        }
    }

    public boolean hasTaskListener(TaskListener<T, V> listener) {
        synchronized (taskListenersLock) {
            return taskListeners.contains(listener);
        }
    }

    public Collection<TaskListener<T, V>> getTaskListeners() {
        synchronized (taskListenersLock) {
            return new ArrayList<TaskListener<T, V>>(taskListeners);
        }
    }

    protected void process(List<V> values) {
        fireProcessListeners(values);
    }

    //ToDo: Interrupted() and failed() does not cover exceptions during doinbackground(), this is not ideal!

    protected final void done() {
    }

    /**
     * finished method is called by SwingWorker's done method
     */
    protected abstract void finished();

    /**
     * failed method is called by done method from SwingWorker when the task has failed.
     *
     * @param error Throwable generated by failed task
     */
    protected void failed(Throwable error) {
        String msg = String.format("%s failed on : %s", this, error);
        logger.error(msg, error);
    }

    /**
     * succeed method is called by done method from SwingWorker when the task has succeed.
     *
     * @param results
     */
    protected abstract void succeed(T results);

    protected abstract void cancelled();

    protected abstract void interrupted(InterruptedException iex);

    private void fireStartedListeners() {
        TaskEvent<Void> event = new TaskEvent<Void>(this, null);
        synchronized (taskListenersLock) {
            for (TaskListener listener : taskListeners) {
                listener.started(event);
            }
        }
    }

    private void fireProcessListeners(List<V> values) {
        TaskEvent<List<V>> event = new TaskEvent<List<V>>(this, values);
        synchronized (taskListenersLock) {
            for (TaskListener<T, V> listener : taskListeners) {
                listener.process(event);
            }
        }
    }

    private void fireCompletionListeners() {
        try {
            if (isCancelled())
                fireCancelledListeners();
            else
                fireSucceedListeners(get());
        } catch (InterruptedException iex) {
            fireInterruptedListeners(iex);
        } catch (ExecutionException eex) {
            fireFailedListeners(eex.getCause());
        } finally {
            fireFinishedListeners();
        }
    }

    private void fireCancelledListeners() {
        TaskEvent<Void> event = new TaskEvent<Void>(this, null);
        synchronized (taskListenersLock) {
            for (TaskListener listener : taskListeners) {
                listener.cancelled(event);
            }
        }
    }

    private void fireInterruptedListeners(InterruptedException iex) {
        TaskEvent<InterruptedException> event = new TaskEvent<InterruptedException>(this, iex);
        synchronized (taskListenersLock) {
            for (TaskListener listener : taskListeners) {
                listener.interrupted(event);
            }
        }
    }

    private void fireSucceedListeners(T result) {
        TaskEvent<T> event = new TaskEvent<T>(this, result);
        synchronized (taskListenersLock) {
            for (TaskListener<T, V> listener : taskListeners) {
                listener.succeed(event);
            }
        }
    }

    private void fireFailedListeners(Throwable error) {
        TaskEvent<Throwable> event = new TaskEvent<Throwable>(this, error);
        synchronized (taskListenersLock) {
            for (TaskListener listener : taskListeners) {
                listener.failed(event);
            }
        }
    }

    private void fireFinishedListeners() {
        TaskEvent<Void> event = new TaskEvent<Void>(this, null);
        synchronized (taskListenersLock) {
            for (TaskListener listener : taskListeners) {
                listener.finished(event);
            }
        }
    }

    private void fireProgressListeners(int progress) {
        TaskEvent<Integer> event = new TaskEvent<Integer>(this, progress);
        synchronized (taskListenersLock) {
            for (TaskListener listener : taskListeners) {
                listener.progress(event);
            }
        }
    }

    private class TaskStateMonitor implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String propName = evt.getPropertyName();

            if ("state".equals(propName)) {
                StateValue state = (StateValue) (evt.getNewValue());
                switch (state) {
                    case STARTED:
                        taskStarted();
                        break;
                    case DONE:
                        taskDone();
                        break;
                }
            } else if ("progress".equals(propName)) {
                fireProgressListeners(getProgress());
            }
        }

        /**
         * Called when task started
         */
        private void taskStarted() {
            fireStartedListeners();
        }

        /**
         * Called when task is done
         */
        private void taskDone() {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (isCancelled())
                            cancelled();
                        else
                            succeed(get());
                    } catch (InterruptedException iex) {
                        interrupted(iex);
                    } catch (ExecutionException eex) {
                        failed(eex.getCause());
                    } finally {
                        finished();
                        try {
                            fireCompletionListeners();
                        } finally {
                            firePropertyChange(COMPLETED_PROP, false, true);
                        }
                    }
                }
            });
        }
    }
}