package uk.ac.ebi.pride.toolsuite.gui.component.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.SideToolBarPanel;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.OpenDatabaseAction;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.OpenFileAction;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.OpenMyProjectAction;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * MainDataVisualizer is the main display area of the pride inspector.
 * It initializes the welcome pane, data source browser and data content browser.
 * In addition, it listens to the property change event from data access monitor, which
 * triggers the switching between the welcome pane and the data content panes.
 * <p/>
 * <p/>
 * User: rwang
 * Date: 19-Sep-2010
 * Time: 09:18:43
 */
public class MainDataVisualizer extends JPanel implements PropertyChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(MainDataVisualizer.class);

    /**
     * Main content display area
     */
    private SideToolBarPanel mainDisplayPane;

    /**
     * Reference to pride inspector context
     */
    private PrideInspectorContext context;

    private LeftControlPane dataSourceBrowser;

    private LaunchMenuViewer launchMenuViewer;

    public MainDataVisualizer() {
        // setup the main pane
        setupMainPane();

        // add the rest of components
        addComponents();
    }

    /**
     * Setup the main pane
     */
    private void setupMainPane() {
        this.setLayout(new BorderLayout());

        // get context
        context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
        context.addPropertyChangeListener(this);
    }

    /**
     * Initialize data source browser and data content browser
     */
    private void addComponents() {

        // data source browser
        dataSourceBrowser = new LeftControlPane();

        // open Source Browser
        launchMenuViewer = new LaunchMenuViewer();

        // central pane
        CentralContentPane dataContentBrowser = new CentralContentPane();

        // the main display area
        mainDisplayPane = new SideToolBarPanel(dataContentBrowser, SideToolBarPanel.WEST);

        // get icon for data source browser
        Icon dataSourceIcon = GUIUtilities.loadIcon(context.getProperty("data.source.small.icon"));
        String dataSourceDesc = context.getProperty("data.source.title");
        String dataSourceTooltip = context.getProperty("data.source.tooltip");


        // add all the component
        mainDisplayPane.addGap(5);
        mainDisplayPane.addComponent(dataSourceIcon, null, dataSourceTooltip, dataSourceDesc, dataSourceBrowser);

        // add open file action
        Icon openFileIcon = GUIUtilities.loadIcon(context.getProperty("open.file.icon.small"));
        String openFileTooltip = context.getProperty("open.file.title");
        PrideAction openFileAction = new OpenFileAction(null, openFileIcon);
        openFileAction.putValue(Action.SHORT_DESCRIPTION, openFileTooltip);
        // add all the component
        mainDisplayPane.addGap(5);
        mainDisplayPane.addAction(openFileAction, false);

        // add database action
        Icon prideIcon = GUIUtilities.loadIcon(context.getProperty("open.database.icon.small"));
        String prideTooltip = context.getProperty("open.database.title");
        PrideAction prideFileAction = new OpenDatabaseAction(null, prideIcon);
        prideFileAction.putValue(Action.SHORT_DESCRIPTION, prideTooltip);
        // add all the component
        mainDisplayPane.addGap(5);
        mainDisplayPane.addAction(prideFileAction, false);

        // add download Action
        Icon downloadIcon = GUIUtilities.loadIcon(context.getProperty("reviewer.download.icon.small"));
        String downloadTooltip = context.getProperty("reviewer.download.title");
        PrideAction downloadAction = new OpenMyProjectAction(null, downloadIcon);
        downloadAction.putValue(Action.SHORT_DESCRIPTION, downloadTooltip);
        // add all the component
        mainDisplayPane.addGap(5);
        mainDisplayPane.addAction(downloadAction, false);


        this.add(mainDisplayPane, BorderLayout.CENTER);
    }

    /**
     * Listens to events which change the visibility of the data source browser
     *
     * @param evt property change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();

        if (PrideInspectorContext.LEFT_CONTROL_PANE_VISIBILITY.equals(evtName)) {
            // set the visibility of data source browser
            logger.info("Data source browser's visibility has changed to: {}", evt.getNewValue());
            String dataSourceDesc = context.getProperty("data.source.title");
            if (!mainDisplayPane.isToggled(dataSourceDesc)) {
                mainDisplayPane.invokeAction(dataSourceDesc);
            }
        }
    }
}
