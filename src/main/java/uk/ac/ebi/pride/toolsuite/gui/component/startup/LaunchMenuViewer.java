package uk.ac.ebi.pride.toolsuite.gui.component.startup;

import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.OpenDatabaseAction;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.OpenFileAction;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.OpenHelpAction;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.OpenMyProjectAction;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;

import javax.swing.*;
import java.awt.*;

/**
 * @user rwang
 * @user ypriverol
 *
 * Date: 25/05/11
 * Time: 11:21
 */
public class LaunchMenuViewer extends JPanel {

    public LaunchMenuViewer() {
        setupMainPane();
        addComponents();
    }

    private void setupMainPane() {
        this.setLayout(new BorderLayout());
    }

    public void addComponents() {
        // create a scroll pane
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createLineBorder(Color.gray));
        container.setBackground(Color.white);
        container.setLayout(new GridLayout(4, 1));

        // get application context
        PrideInspectorContext context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();

        // open  files
        ImageIcon openFileIcon = GUIUtilities.loadImageIcon(context.getProperty("open.file.icon.medium"));
        String openFileText = context.getProperty("open.file.title");
        String openFileTooltip = context.getProperty("open.file.tooltip");
        Action openFileAction = new OpenFileAction(openFileText, openFileIcon);
        JButton openFileButton = GUIUtilities.createLabelLikeButton(openFileAction);
        openFileButton.setToolTipText(openFileTooltip);
        openFileButton.setHorizontalAlignment(SwingConstants.LEFT);
        container.add(openFileButton);

        // search database
        ImageIcon searchDBIcon = GUIUtilities.loadImageIcon(context.getProperty("open.database.icon.medium"));
        String searchDBText = context.getProperty("open.database.title");
        String searchDBTooltip = context.getProperty("open.database.tooltip");
        Action searchDBAction = new OpenDatabaseAction(searchDBText, searchDBIcon);
        JButton searchDBButton = GUIUtilities.createLabelLikeButton(searchDBAction);
        searchDBButton.setToolTipText(searchDBTooltip);
        searchDBButton.setHorizontalAlignment(SwingConstants.LEFT);
        container.add(searchDBButton);

        // reviewer download
        ImageIcon reviewerIcon = GUIUtilities.loadImageIcon(context.getProperty("reviewer.download.icon.medium"));
        String reviewerText = context.getProperty("reviewer.download.title");
        String reviewerTooltip = context.getProperty("reviewer.download.tooltip");
        Action reviewerAction = new OpenMyProjectAction(reviewerText, reviewerIcon);
        JButton reviewerButton = GUIUtilities.createLabelLikeButton(reviewerAction);
        reviewerButton.setToolTipText(reviewerTooltip);
        reviewerButton.setHorizontalAlignment(SwingConstants.LEFT);
        container.add(reviewerButton);

        // help
        ImageIcon helpIcon = GUIUtilities.loadImageIcon(context.getProperty("help.icon.medium"));
        String helpText = context.getProperty("help.title");
        Action helpAction = new OpenHelpAction(helpText, helpIcon, "help.index");
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpAction);
        helpButton.setHorizontalAlignment(SwingConstants.LEFT);
        container.add(helpButton);

        // add scroll pane
        this.add(container, BorderLayout.CENTER);
    }
}
