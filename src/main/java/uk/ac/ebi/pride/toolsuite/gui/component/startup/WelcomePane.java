package uk.ac.ebi.pride.toolsuite.gui.component.startup;

import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.*;
import uk.ac.ebi.pride.toolsuite.gui.component.PrideInspectorPanel;
import uk.ac.ebi.pride.toolsuite.gui.listener.MouseOverListener;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.CheckUpdateTask;
import uk.ac.ebi.pride.toolsuite.gui.utils.UpdateChecker;
import uk.ac.ebi.pride.util.IOUtilities;

import javax.help.CSH;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * WelcomePane provides the initial welcome screen.
 * <p/>
 * User: rwang
 * Date: 24-Aug-2010
 * Time: 17:40:29
 */
public class WelcomePane extends JPanel implements TaskListener<Object, Object> {

    /**
     * Quick start label title
     */
    private static final String QUICK_START = "Quick Start";

    /**
     * Open file label
     */
    private static final String OPEN_FILE = "Open Identification or Peak Files";

    /**
     * Connect to pride database
     */
    private static final String OPEN_DB = "Search PRIDE Database";

    /**
     * Reviewer download
     */
    private static final String REVIEWER_DOWNLOAD = "Review Project";

    /**
     * getting help
     */
    private static final String GETTING_HELP = "Help";

    /**
     * Learn more about
     */
    private static final String LEARN_MORE_ABOUT = "Learn More About";

    /**
     * Try sample
     */
    private static final String TRY_SAMPLE = "Try Examples";

    /**
     * Feed back
     */
    private static final String FEED_BACK = "Feedback";

    /**
     * PDF
     */
    private static final String PDF = "[PDF File]";

    /**
     * PUBMED
     */
    private static final String PUBMED = "[PubMed record]";

    private static final String PRIDE_INSPECTOR_CITE_MESSAGE = "When use PRIDE Inspector, please cite: ";

    /**
     * give feedback
     */
    private static final String GIVE_US_FEEDBACK = "Give Us Your Feedback";
    /**
     * large title color
     */
    private static final Color LARGE_TITLE_COLOR = new Color(58, 45, 123);

    /**
     * large title font
     */
    private static final Font LARGE_TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 22);

    /**
     * quick start font
     */
    private static final Font BUTTON_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 15);


    /**
     * quick start font color
     */
    private static final Color BUTTON_FONT_COLOR = new Color(0, 60, 200, 200);

    /**
     * pride inspector context
     */
    private PrideInspectorContext context;

    /**
     * Constructor
     */
    public WelcomePane() {
        setupMainPane();
        addComponents();
    }

    /**
     * Set up the main component
     */
    private void setupMainPane() {
        this.setBorder(BorderFactory.createEmptyBorder());

        this.setLayout(new BorderLayout());

        // pride inspector context
        context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
    }

    /**
     * Add the rest components
     */
    private void addComponents() {
        // quick start panel
        JPanel quickStartPanel = createQuickStartPane();

        // try samples panel
        JPanel trySamplePanel = createTrySamplePane();

        // learn more about panel
//        JPanel learnMoreAboutPanel = createLearnMoreAboutPane();

        // feedback panel
        JPanel feedbackPanel = createFeedBackPane();

        // publication panel
        JPanel publicationPanel = createPublicationPanel();

        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, feedbackPanel, publicationPanel);
        bottomSplitPane.setDividerLocation(0.5);
        bottomSplitPane.setResizeWeight(0.5);
        bottomSplitPane.setOpaque(false);
        bottomSplitPane.setBorder(BorderFactory.createEmptyBorder());
        bottomSplitPane.setDividerSize(0);

//        JSplitPane bottomLeftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, learnMoreAboutPanel, bottomLeftBottomSplitPane);
//        bottomLeftSplitPane.setDividerLocation(0.5);
//        bottomLeftSplitPane.setResizeWeight(0.5);
//        bottomLeftSplitPane.setOpaque(false);
//        bottomLeftSplitPane.setBorder(BorderFactory.createEmptyBorder());
//        bottomLeftSplitPane.setDividerSize(0);
//
//        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, bottomLeftBottomSplitPane, messagePanel);
//        bottomSplitPane.setDividerLocation(0.5);
//        bottomSplitPane.setResizeWeight(0.5);
//        bottomSplitPane.setOpaque(false);
//        bottomSplitPane.setBorder(BorderFactory.createEmptyBorder());
//        bottomSplitPane.setDividerSize(0);

        JSplitPane topSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, quickStartPanel, trySamplePanel);
        topSplitPane.setResizeWeight(0.5);
        topSplitPane.setOpaque(false);
        topSplitPane.setBorder(BorderFactory.createEmptyBorder());
        topSplitPane.setDividerSize(1);

        JSplitPane outerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplitPane, bottomSplitPane);
        outerSplitPane.setResizeWeight(0.8);
        outerSplitPane.setOpaque(false);
        outerSplitPane.setBorder(BorderFactory.createEmptyBorder());
        outerSplitPane.setDividerSize(1);

        JPanel panel = new PrideInspectorPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.white);
        panel.add(outerSplitPane, BorderLayout.CENTER);

        // scroll pane for screen size
        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        this.add(scrollPane, BorderLayout.CENTER);

        // check for update
        checkForUpdate();
    }

    /**
     * Create a panel which contains all the quick start components
     *
     * @return JPanel quick start pane
     */
    private JPanel createQuickStartPane() {
        // quick start pane
        JPanel quickStartPanel = new JPanel(new BorderLayout());
        quickStartPanel.setOpaque(false);

        // add title label
        JLabel title = new JLabel(QUICK_START, JLabel.LEFT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        title.setFont(LARGE_TITLE_FONT);
        title.setForeground(LARGE_TITLE_COLOR);
        quickStartPanel.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        buttonPanel.setOpaque(false);

        // set general configs
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(40, 10, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;


        // open file button
        c.gridy = 0;
        Icon openFileIcon = GUIUtilities.loadIcon(context.getProperty("open.file.icon.large"));
        Action openFileAction = new OpenFileAction(OPEN_FILE, openFileIcon);
        JButton openFileButton = createLabelLikeButton(openFileAction);
        buttonPanel.add(openFileButton, c);

        // open database button
        c.gridx = 1;
        Icon openDBIcon = GUIUtilities.loadIcon(context.getProperty("open.database.icon.large"));
        Action openDBAction = new OpenDatabaseAction(OPEN_DB, openDBIcon);
        JButton openDBButton = createLabelLikeButton(openDBAction);
        buttonPanel.add(openDBButton, c);


        // open reviewer download window button
        c.gridx = 0;
        c.gridy = 1;
        Icon openReviewerIcon = GUIUtilities.loadIcon(context.getProperty("reviewer.download.icon.large"));
        Action reviewerAction = new OpenMyProjectAction(REVIEWER_DOWNLOAD, openReviewerIcon);
        JButton reviewerButton = createLabelLikeButton(reviewerAction);
        buttonPanel.add(reviewerButton, c);

        // open help button
        c.gridx = 1;
        Icon openHelpIcon = GUIUtilities.loadIcon(context.getProperty("help.icon.large"));
        Action openHelpAction = new OpenHelpAction(GETTING_HELP, openHelpIcon, "help.index");
        JButton openHelpButton = createLabelLikeButton(openHelpAction);
        buttonPanel.add(openHelpButton, c);


        JPanel positionalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        positionalPanel.setOpaque(false);
        positionalPanel.add(buttonPanel);

        quickStartPanel.add(positionalPanel, BorderLayout.CENTER);
        return quickStartPanel;
    }

    /**
     * Create a panel which contains try a sample components
     *
     * @return JPanel try a sample panel
     */
    private JPanel createTrySamplePane() {
        JPanel trySamplePane = new JPanel(new BorderLayout());

        // set transparency
        trySamplePane.setOpaque(false);

        // add title label
        JLabel title = new JLabel(TRY_SAMPLE, JLabel.LEFT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        title.setFont(LARGE_TITLE_FONT);
        title.setForeground(LARGE_TITLE_COLOR);
        trySamplePane.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        buttonPanel.setOpaque(false);

        // set general configs
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(40, 10, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;


        Icon circleIcon = GUIUtilities.loadIcon(context.getProperty("puzzle.bulletin.icon.medium"));

        // add try mzML sample
        c.gridy = 0;
        File mzMLExampleFile = getExampleFiles(context.getProperty("pride.inspector.mzml.example.file"));
        List<File> mzMLFiles = new ArrayList<File>();
        if (mzMLExampleFile != null) {
            mzMLFiles.add(mzMLExampleFile);
        }
        Action openMzMLExampleAction = new OpenFileAction(context.getProperty("open.mzml.example.title"), circleIcon, mzMLFiles);
        openMzMLExampleAction.setEnabled(mzMLExampleFile != null && mzMLExampleFile.exists());
        JButton openMzMLExampleButton = createLabelLikeButton(openMzMLExampleAction);
        buttonPanel.add(openMzMLExampleButton, c);

        c.gridy = 1;
        File prideXMLExampleFile = getExampleFiles(context.getProperty("pride.inspector.pride.example.file"));
        List<File> prideXmlFiles = new ArrayList<File>();
        if (prideXMLExampleFile != null) {
            prideXmlFiles.add(prideXMLExampleFile);
        }
        Action openPrideExampleAction = new OpenFileAction(context.getProperty("open.pride.xml.example.title"), circleIcon, prideXmlFiles);
        openPrideExampleAction.setEnabled(prideXMLExampleFile != null && prideXMLExampleFile.exists());
        JButton openPrideExampleButton = createLabelLikeButton(openPrideExampleAction);
        openPrideExampleButton.setAction(openPrideExampleAction);
        buttonPanel.add(openPrideExampleButton, c);

        c.gridy = 2;
        File mzIdentMlExampleFile = getExampleFiles(context.getProperty("pride.inspector.mzidentml.example.file"));
        List<File> mzIdentMlFiles = new ArrayList<File>();
        if (mzIdentMlExampleFile != null) {
            mzIdentMlFiles.add(mzIdentMlExampleFile);
        }
        Action openMzIdentMlExampleAction = new OpenFileAction(context.getProperty("open.mzidentml.example.title"), circleIcon, mzIdentMlFiles);
        openMzIdentMlExampleAction.setEnabled(mzIdentMlExampleFile != null && mzIdentMlExampleFile.exists());
        JButton mzIdentMlExampleButton = createLabelLikeButton(openMzIdentMlExampleAction);
        mzIdentMlExampleButton.setAction(openMzIdentMlExampleAction);
        buttonPanel.add(mzIdentMlExampleButton, c);

        JPanel positionalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        positionalPanel.setOpaque(false);
        positionalPanel.add(buttonPanel);

        trySamplePane.add(positionalPanel, BorderLayout.CENTER);

        JPanel moreSamplePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        moreSamplePanel.setOpaque(false);
        Action openMoreExampleAction = new OpenUrlAction(context.getProperty("open.more.example.title"), null,
                context.getProperty("pride.inspector.example.dataset.website"));
        JButton openMoreExampleButton = createLabelLikeButton(openMoreExampleAction);
        openMoreExampleButton.setAction(openMoreExampleAction);

        moreSamplePanel.add(openMoreExampleButton);
        trySamplePane.add(moreSamplePanel, BorderLayout.SOUTH);


        return trySamplePane;
    }

    /**
     * Get examples file based on the sub path
     *
     * @param subPath sub path to the example file
     * @return File   an array of example file
     */
    private File getExampleFiles(String subPath) {
        URL path = IOUtilities.getFullPath(this.getClass(), subPath);
        File file;
        try {
            file = IOUtilities.convertURLToFile(path);
        } catch (IllegalArgumentException ex) {
            return null;
        }
        return file;
    }

    /**
     * Create a panel which contains learn more about components
     *
     * @return JPanel   learn more about panel
     */
    private JPanel createLearnMoreAboutPane() {
        JPanel learnMoreAboutPane = new JPanel(new BorderLayout());

        // set transparency
        learnMoreAboutPane.setOpaque(false);

        // add title label
        JLabel title = new JLabel(LEARN_MORE_ABOUT, JLabel.LEFT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        title.setFont(LARGE_TITLE_FONT);
        title.setForeground(LARGE_TITLE_COLOR);
        learnMoreAboutPane.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        buttonPanel.setOpaque(false);

        // set general configs
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(40, 10, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        c.gridx = 0;


        Icon bulletinIcon = GUIUtilities.loadIcon(context.getProperty("puzzle.bulletin.icon.small"));

        c.gridy = 0;
        JButton prideButton = createLabelLikeButton(bulletinIcon, "PRIDE");
        CSH.setHelpIDString(prideButton, "help.faq.pride");
        prideButton.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        buttonPanel.add(prideButton, c);

        c.gridx = 1;
        JButton prideDBButton = createLabelLikeButton(bulletinIcon, "PRIDE Public Database");
        CSH.setHelpIDString(prideDBButton, "help.faq.pridedb");
        prideDBButton.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        buttonPanel.add(prideDBButton, c);

        c.gridx = 2;
        JButton downloadButton = createLabelLikeButton(bulletinIcon, "Private Download");
        CSH.setHelpIDString(downloadButton, "help.download");
        downloadButton.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        buttonPanel.add(downloadButton, c);

        c.gridx = 3;
        JButton peptideButton = createLabelLikeButton(bulletinIcon, "Shared Peptides");
        CSH.setHelpIDString(peptideButton, "help.faq.shared.peptide");
        peptideButton.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        buttonPanel.add(peptideButton, c);

        c.gridy = 1;
        c.gridx = 0;
        JButton prideXMLButton = createLabelLikeButton(bulletinIcon, "PRIDE XML");
        CSH.setHelpIDString(prideXMLButton, "help.faq.pridexml");
        prideXMLButton.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        buttonPanel.add(prideXMLButton, c);

        c.gridx = 1;
        JButton mzIndetMLButton = createLabelLikeButton(bulletinIcon, "mzIdentML");
        CSH.setHelpIDString(mzIndetMLButton, "help.faq.mzidentml");
        mzIndetMLButton.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        mzIndetMLButton.addMouseListener(new MouseOverListener(mzIndetMLButton));
        buttonPanel.add(mzIndetMLButton, c);


        c.gridx = 2;
        JButton mzMLButton = createLabelLikeButton(bulletinIcon, "mzML");
        CSH.setHelpIDString(mzMLButton, "help.faq.mzml");
        mzMLButton.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        mzMLButton.addMouseListener(new MouseOverListener(mzMLButton));
        buttonPanel.add(mzMLButton, c);


        c.gridx = 3;
        JButton chartButton = createLabelLikeButton(bulletinIcon, "Summary Charts");
        CSH.setHelpIDString(chartButton, "help.chart");
        chartButton.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        buttonPanel.add(chartButton, c);

        JPanel positionalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        positionalPanel.setOpaque(false);
        positionalPanel.add(buttonPanel);

        // add reviewer download
        learnMoreAboutPane.add(positionalPanel, BorderLayout.CENTER);

        return learnMoreAboutPane;
    }

    private JPanel createFeedBackPane() {
        JPanel feedbackPane = new JPanel(new BorderLayout());
        // set transparency
        feedbackPane.setOpaque(false);

        // add title label
        JLabel title = new JLabel(FEED_BACK, JLabel.LEFT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        title.setFont(LARGE_TITLE_FONT);
        title.setForeground(LARGE_TITLE_COLOR);
        feedbackPane.add(title, BorderLayout.NORTH);


        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        buttonPanel.setOpaque(false);

        // set general configs
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(40, 10, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        c.gridx = 0;

        Icon bulletinIcon = GUIUtilities.loadIcon(context.getProperty("feedback.icon.small"));

        c.gridy = 0;
        // add button
        Action feedBackAction = new FeedbackAction(GIVE_US_FEEDBACK, bulletinIcon);
        JButton feedbackButton = createLabelLikeButton(feedBackAction);
        buttonPanel.add(feedbackButton, c);

        JPanel positionalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        positionalPanel.setOpaque(false);
        positionalPanel.add(buttonPanel);

        feedbackPane.add(positionalPanel, BorderLayout.CENTER);

        return feedbackPane;
    }

    private JPanel createPublicationPanel() {
        JPanel publicationPanel = new JPanel(new BorderLayout());

        // set transparency
        publicationPanel.setOpaque(false);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        buttonPanel.setOpaque(false);

        // set general configs
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(20, 10, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;

        c.gridx = 0;
        c.gridy = 0;

        // add pride inspector
        JLabel inspectorDescLabel = new JLabel(PRIDE_INSPECTOR_CITE_MESSAGE, JLabel.LEFT);
        inspectorDescLabel.setFont(BUTTON_FONT);
        inspectorDescLabel.setFont(inspectorDescLabel.getFont().deriveFont(12f));
        inspectorDescLabel.setForeground(LARGE_TITLE_COLOR);
        buttonPanel.add(inspectorDescLabel, c);

        c.insets = new Insets(5, 10, 5, 10);
        c.gridx = 0;
        c.gridy = 1;

        // add pride inspector
        JPanel inspectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inspectorPanel.setOpaque(false);

        JButton inspectorPublicationButton = createLabelLikeButton(new OpenUrlAction(context.getProperty("pride.inspector.publication.title"), null, context.getProperty("pride.inspector.publication.url")));
        inspectorPublicationButton.setFont(inspectorDescLabel.getFont().deriveFont(12f));
        inspectorPanel.add(inspectorPublicationButton);

        JButton inspectorPDFButton = createLabelLikeButton(new OpenUrlAction(PDF, null, context.getProperty("pride.inspector.publication.pdf.url")));
        inspectorPDFButton.setFont(inspectorDescLabel.getFont().deriveFont(12f));
        inspectorPanel.add(inspectorPDFButton);

        JButton inspectorPubMedButton = createLabelLikeButton(new OpenUrlAction(PUBMED, null, context.getProperty("pride.inspector.publication.pubmed.url")));
        inspectorPubMedButton.setFont(inspectorDescLabel.getFont().deriveFont(12f));
        inspectorPanel.add(inspectorPubMedButton);

        buttonPanel.add(inspectorPanel, c);

        JPanel positionalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        positionalPanel.setOpaque(false);
        positionalPanel.add(buttonPanel);

        publicationPanel.add(positionalPanel, BorderLayout.CENTER);

        return publicationPanel;
    }

    private void checkForUpdate() {
        // start a update checking task
        Task newTask = new CheckUpdateTask();
        newTask.addTaskListener(this);
        TaskUtil.startBackgroundTask(newTask);
    }

    /**
     * Create a label like button using a action
     *
     * @param action action which contains a icon and title
     * @return JButton  button
     */
    private JButton createLabelLikeButton(Action action) {
        JButton button = GUIUtilities.createLabelLikeButton(action);
        button.setFont(BUTTON_FONT);
        button.setForeground(BUTTON_FONT_COLOR);
        return button;
    }

    /**
     * Create a label like button using a icon and a title.
     *
     * @param icon  button icon
     * @param title button title
     * @return JButton  button
     */
    private JButton createLabelLikeButton(Icon icon, String title) {
        JButton button = GUIUtilities.createLabelLikeButton(icon, title);
        button.setFont(BUTTON_FONT);
        button.setForeground(BUTTON_FONT_COLOR);
        return button;
    }

    @Override
    public void succeed(TaskEvent<Object> objectTaskEvent) {
        Object obj = objectTaskEvent.getValue();
        if (obj instanceof Boolean && (Boolean) obj) {
//            messageBoard.showMessage(MessageBoard.Type.WARNING, context.getProperty("new.update.message"));
            UpdateChecker.showUpdateDialog();
        }
    }

    @Override
    public void started(TaskEvent<Void> event) {
    }

    @Override
    public void process(TaskEvent<java.util.List<Object>> listTaskEvent) {
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
    }
}
