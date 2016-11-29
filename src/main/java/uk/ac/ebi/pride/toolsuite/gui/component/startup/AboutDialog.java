package uk.ac.ebi.pride.toolsuite.gui.component.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * AboutDialog displays a animation of PRIDE Inspector contributor.
 *
 * User: rwang
 * Date: 14-Oct-2010
 * Time: 09:02:36
 */
public class AboutDialog extends JDialog implements ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(AboutDialog.class);

    private static final String ABOUT_PANEL_TITLE = "About";
    private static final String CLOSE_BUTTON = "Close";

    public AboutDialog(Frame owner) {
        super(owner, ABOUT_PANEL_TITLE, true);
        setResizable(false);

        initializeComponent();
    }

    private void initializeComponent() {
        // close button
        JButton closeButton = new JButton(CLOSE_BUTTON);
        closeButton.addActionListener(this);
        getRootPane().setDefaultButton(closeButton);

        // close panel
        JPanel closePane = new JPanel(new FlowLayout());
        closePane.add(closeButton);
        closePane.add(Box.createRigidArea(new Dimension(40, 40)));

        // about panel
        AboutPanel aboutPane = new AboutPanel();

        // main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(closePane, BorderLayout.SOUTH);
        mainPanel.add(aboutPane, BorderLayout.CENTER);

        // set content pane
        setContentPane(mainPanel);
        pack();

        // set display location
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((d.width - getWidth())/2, (d.height - getHeight())/2);

        // add window listener
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                closeDialog();
            }
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        closeDialog();
    }

    /**
     * Close this dialog also shutdown the thread
     */
    private void closeDialog() {
        AboutPanel.stopThread();
        dispose();
    }

    private static class AboutPanel extends JComponent implements Runnable {

        private static final long SLEEP_TIME = 30;
        private static final int BOTTOM_PADDING = 36;
        private static final int TOP_PADDING = 250;
        private static final Font DEFAULT_FONT = UIManager.getFont("Label.font");
        private static final Font BOTTOM_LINE_FONT = DEFAULT_FONT.deriveFont(9.8f);
        private static boolean doWork;
        private final FontMetrics fontMetrics;

        private Thread thread;

        private java.util.List<String> aboutText;

        private String bottomLineText;

        private ImageIcon backgroundImage;
        private BufferedImage bufImage;
        private Graphics2D graphics;
        private Rectangle2D.Float rectangle;
        private GradientPaint gradientPaint;

        private PrideInspectorContext context;

        private int lineHeight = 0;
        private int listHeight = 0;
        private int lineCount = 0;
        private int bottomLineXOffset = 0;
        private int bottomLineYOffset = 0;
        private int pipeLineCount = 0;
        private int width = 0;
        private int height = 0;
        private int y = 0;


        private AboutPanel() {
            // get Pride Inspector context
            context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();

            // set font
            setFont(DEFAULT_FONT);

            // font metrics
            fontMetrics = getFontMetrics(DEFAULT_FONT);
            // bottom line
            FontMetrics bottomFontMetrics = getFontMetrics(BOTTOM_LINE_FONT);
            lineHeight = bottomFontMetrics.getHeight();

            // set the bottom line string
            Object[] args = {context.getProperty("pride.inspector.version"), System.getProperty("java.version")};
            bottomLineText = context.getProperty("about.bottom.line.text", args);

            // load background image
            loadBackgroundImage();

            // set the dimension
            Dimension dim = new Dimension(backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
            setSize(dim);
            setPreferredSize(dim);

            // store the dimension
            width = dim.width;
            height = dim.height;

            // calculate offset for bottom line
            bottomLineXOffset = (width - bottomFontMetrics.stringWidth(bottomLineText))/2;
            bottomLineYOffset = height - lineHeight/2;

            // load about panel text
            loadAboutPaneText();
            lineCount = aboutText.size();
            listHeight = lineCount * lineHeight;

            // start thread
            startThread();
            updateUI();

        }

        /**
         * Load the background logo image
         */
        private void loadBackgroundImage() {
            backgroundImage = (ImageIcon) GUIUtilities.loadIcon(context.getProperty("about.pride.main.logo"));
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(backgroundImage.getImage(), 0);

            // wait for the loading to finish
            try {
                tracker.waitForID(0);
            } catch(Exception ex) {
                logger.error("Failed to load pride main logo for about panel", ex);
            }
        }

        /**
         * Load the about panel text
         */
        private void loadAboutPaneText() {
            // list to store the text to be displayed by about panel
            aboutText = new ArrayList<>();

            StringTokenizer st = new StringTokenizer(context.getProperty("about.text"), "\n");
            while(st.hasMoreTokens()) {
                aboutText.add(st.nextToken());
            }
        }

        private void drawImage() {
            if (bufImage == null) {
                // first time create buffer image
                Dimension dim = getSize();
                bufImage = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
                // set graphics
                graphics = bufImage.createGraphics();
                // updating area
                rectangle = new Rectangle2D.Float(0, TOP_PADDING, dim.width, dim.height-BOTTOM_PADDING-TOP_PADDING);
                // pipe line count
                pipeLineCount = 1 + (int)Math.ceil(rectangle.height/lineHeight);
                //
                y = dim.height + BOTTOM_PADDING;
                // gradienPaint
                gradientPaint = new GradientPaint(
					rectangle.width/2, TOP_PADDING+80, new Color(80, 80, 80),
					rectangle.width/2, TOP_PADDING, new Color(205, 205, 205)
					);
                // set anti-alias
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }

            // draw background image
            graphics.drawImage(backgroundImage.getImage(), 0, 0, width, height, this);

            // draw bottom line text
            graphics.setFont(BOTTOM_LINE_FONT);
            graphics.setPaint(new Color(55, 55, 55));
            graphics.drawString(bottomLineText, bottomLineXOffset, bottomLineYOffset);

            // draw highlight effect
            graphics.setPaint(new Color(255, 255, 255, 50));
            graphics.drawString(bottomLineText, bottomLineXOffset + 1, bottomLineYOffset + 1);

            // draw about text
            graphics.setFont(DEFAULT_FONT);
            graphics.setPaint(Color.black);
            // draw border
            graphics.drawRect(0, 0, width - 1, height -1);
            // draw update area
            graphics.clip(rectangle);
            graphics.setPaint(gradientPaint);

            int drawnLineCount = 0;
            int yCoordinate;

            for (int i = 0; i < lineCount; i++) {
                // check whether the text line is above the canvas
                yCoordinate = y + i*lineHeight;
                if (yCoordinate < TOP_PADDING) {
                    continue;
                }

                String str = aboutText.get(i);
                int x = (width - fontMetrics.stringWidth(str))/2;
                graphics.drawString(str, x, yCoordinate);
                if (++ drawnLineCount >= pipeLineCount) {
                    break;
                }
            }

            y--;
            paint(getGraphics());

            // check if the end of list has been reached
            if ((y + listHeight) < TOP_PADDING) {
                y = height + BOTTOM_PADDING;
            }
        }

        @Override
        public void update(Graphics g) {
            paint(g);
        }

        /**
         * Paint the whole component as a buffered image
         * @param g
         */
        @Override
        public void paint(Graphics g) {
            if (g != null && bufImage != null) {
                g.drawImage(bufImage, 0, 0, width, height, this);
            }
        }

        private void startThread() {
            if (thread == null) {
                thread = new Thread(this);
                doWork= true;
                thread.start();
            }
        }

        public static void stopThread() {
            doWork = false;
        }

        @Override
        public void run() {
            try {
                while(doWork) {
                    drawImage();
                    Thread.sleep(SLEEP_TIME);
                }
            } catch(Exception ex) {
                logger.error("Failed to excute AboutDialog", ex);
            }
        }
    }

}
