package uk.ac.ebi.pride.toolsuite.gui.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.utils.GUIBlocker;
import uk.ac.ebi.pride.toolsuite.gui.utils.PropertyChangeHelper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TaskManager acts as a thread pool, it does the followings:
 * <p/>
 * 1. maintain a list of Tasks
 * <p/>
 * 2. manage a queue of Tasks
 * <p/>
 * User: rwang
 * Date: 22-Jan-2010
 * Time: 11:34:32
 */
public class TaskManager extends PropertyChangeHelper {
    private final static Logger logger = LoggerFactory.getLogger(TaskManager.class);

    /**
     * property change event name, this is fired when a new task is added
     */
    public final static String ADD_TASK_PROP = "add_new_task";

    /**
     * property change event name, this is fired when a task is removed
     */
    public final static String REMOVE_TASK_PROP = "remove_new_task";

    /**
     * This number defines the number threads can be running at the same time
     */
    private final static int CORE_POOL_SIZE = 10;

    /**
     * This number defines the number threads can be running and waiting at the same time
     */
    private final static int MAXIMUM_POOL_SIZE = 20;

    /**
     * Threshold pool executor, it is responsible to running all the tasks
     */
    private final ExecutorService executor;

    /**
     * A list of current ongoing tasks
     */
    private final List<Task> tasks;

    /**
     * property change listener
     */
    private final PropertyChangeListener taskPropListener;

    /**
     * Constructor
     * <p/>
     * This will create a thread pool with default configurations.
     * <p/>
     * 1. the number of allowed threads are 20.
     * <p/>
     * 2. the number of max running threads are 10.
     */
    public TaskManager() {
        this(new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                1L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()));
    }

    /**
     * Constructor
     *
     * @param executor provide an implementation of the thread pool.
     */
    public TaskManager(ExecutorService executor) {

        // thread pool
        this.executor = executor;

        // a list of tasks
        this.tasks = Collections.synchronizedList(new ArrayList<Task>());

        // internal property change listener
        this.taskPropListener = new TaskPropertyListener();
    }

    /**
     * Add a new task to the task manager, hence the thread pool.
     * <p/>
     * Notify any listeners listen to the task manager
     *
     * @param task new task
     */
    public void addTask(Task task) {
        addTask(task, true);
    }

    /**
     * Add a new task to the task manager, you can choose whether to notify
     * the task manager listeners, for example, if false, the status bar will
     * not change.
     *
     * @param task   new task
     * @param notify choose whether to notify
     */
    public void addTask(Task task, boolean notify) {
        // add task the task list
        List<Task> oldTasks, newTasks;
        synchronized (tasks) {
            oldTasks = new ArrayList<Task>(tasks);
            tasks.add(task);
            newTasks = new ArrayList<Task>(tasks);
            task.addPropertyChangeListener(taskPropListener);
        }

        // notify the status bar
        if (notify) {
            firePropertyChange(ADD_TASK_PROP, oldTasks, newTasks);
        }

        // block gui
        // ToDo: this might need a separate thread
        GUIBlocker blocker = task.getGUIBlocker();
        if (blocker != null)
            blocker.block();

        // execute the task
        executor.execute(task);
    }

    /**
     * Return a list of Tasks which has the specified TaskListener.
     *
     * @param listener Task listener.
     * @return List<Task>   a list of tasks.
     */
    @SuppressWarnings("unchecked")
    public List<Task> getTasks(TaskListener listener) {
        List<Task> ts = new ArrayList<Task>();
        synchronized (tasks) {
            for (Task task : tasks) {
                if (task.hasTaskListener(listener)) {
                    ts.add(task);
                }
            }
        }
        return ts;
    }

    /**
     * Return a list of tasks which has the specified property change listener
     *
     * @param listener property change listener
     * @return List<Task>   a list of tasks
     */
    public List<Task> getTasks(PropertyChangeListener listener) {
        List<Task> ts = new ArrayList<Task>();
        synchronized (tasks) {
            for (Task task : tasks) {
                if (task.hasPropertyChangeListener(listener)) {
                    ts.add(task);
                }
            }
        }
        return ts;
    }

    /**
     * Return as list of tasks which is the specified task class type.
     *
     * @param taskClass task class type
     * @return List<Task>   a list of matching tasks
     */
    public List<Task> getTasks(Class<? extends Task> taskClass) {
        List<Task> ts = new ArrayList<Task>();
        synchronized (tasks) {
            for (Task task : tasks) {
                if (task.getClass().equals(taskClass)) {
                    ts.add(task);
                }
            }
        }
        return ts;
    }

    /**
     * Check whether the task is already registered with task manager
     *
     * @param task task
     * @return boolean  true if the task is in task manager
     */
    public boolean hasTask(Task task) {
        synchronized (tasks) {
            return tasks.contains(task);
        }
    }

    /**
     * Remove a task listener from all the ongoing tasks.
     *
     * @param listener task listener
     */
    @SuppressWarnings("unchecked")
    public void removeTaskListener(TaskListener listener) {
        synchronized (tasks) {
            for (Task task : tasks) {
                task.removeTaskListener(listener);
            }
        }
    }

    /**
     * Stop task. If it is in task manager then it will be removed from the TaskManager,
     * all the TaskListeners assigned to this Task will also be deleted.
     * all the property change listeners assigned to thia task will be deleted
     *
     * @param task      task
     * @param interrupt whether to notify
     * @return boolean  true is the cancel has been finished.
     */
    @SuppressWarnings("unchecked")
    public boolean cancelTask(Task task, boolean interrupt) {
        boolean canceled = false;

        // remove task from task manager
        boolean hasTask = hasTask(task);
        if (hasTask) {
            // cancel all the children tasks first
            cancelTasksByOwner(task);

            List<Task> oldTasks, newTasks;
            synchronized (tasks) {
                oldTasks = new ArrayList<Task>(tasks);
                tasks.remove(task);
                canceled = task.cancel(interrupt);
                newTasks = new ArrayList<Task>(tasks);
                task.removePropertyChangeListener(taskPropListener);
            }
            firePropertyChange(REMOVE_TASK_PROP, oldTasks, newTasks);
        }

        return canceled;
    }


    /**
     * Cancel all the tasks owned by the owner
     *
     * @param owner owner of the tasks
     */
    public void cancelTasksByOwner(Object owner) {
        for (Task task : tasks) {
            task.removeByOwner(owner);
        }
    }

    /**
     * orderly shutdown, all existing tasks are allowed to finish
     * no task is submitted.
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * attempt to stop all running tasks at once
     */
    public void shutdownNow() {
        executor.shutdownNow();
    }

    /**
     * Internal task listener, listens to any completion of the task,
     * if yes, then remove the task from task list and fire a remove task property change event.
     */
    private class TaskPropertyListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String propName = evt.getPropertyName();
            if (Task.COMPLETED_PROP.equals(propName)) {
                Task task = (Task) evt.getSource();
                List<Task> oldTasks, newTasks;
                synchronized (tasks) {
                    oldTasks = new ArrayList<Task>(tasks);
                    tasks.remove(task);
                    // remove all the children tasks too
                    TaskManager.this.cancelTasksByOwner(task);
                    newTasks = new ArrayList<Task>(tasks);
                    task.removePropertyChangeListener(taskPropListener);
                }
                firePropertyChange(REMOVE_TASK_PROP, oldTasks, newTasks);

                // unblock gui
                GUIBlocker blocker = task.getGUIBlocker();
                if (blocker != null)
                    blocker.unblock();
            }
        }
    }
}
