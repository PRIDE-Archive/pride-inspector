package uk.ac.ebi.pride.toolsuite.gui.component.table.renderer;

import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Cell renderer to draw bar chart
 * <p/>
 * User: rwang
 * Date: 21/08/2011
 * Time: 10:03
 */
public class BarChartRenderer extends JLabel implements TableCellRenderer {
    private static final float DEFAULT_REFERENCE_VALUE = 0;
    private static final float DEFAULT_MAXIMUM_VALUE = 10;
    private static final float DEFAULT_MINIMUM_VALUE = 0;
    private static final Color DEFAULT_POSITIVE_VALUE_COLOUR = new Color(27, 106, 165);
    private static final Color DEFAULT_NEGATIVE_VALUE_COLOUR = new Color(215, 39, 41);
    private static final Color DEFAULT_WARNING_VALUE_COLOUR = Color.red;
    private static final boolean DEFAULT_NUMBER_AND_CHART = true;

    private float maximumValue;
    private float referenceValue;
    private float minimumValue;
    private Paint positiveValuePaint;
    private Paint negativeValuePaint;
    private Paint warningValuePaint;
    private boolean numberAndChart;
    private boolean warningVisible;


    private Object value;

    public BarChartRenderer() {
        this(DEFAULT_MAXIMUM_VALUE, DEFAULT_MINIMUM_VALUE, DEFAULT_REFERENCE_VALUE);
    }

    public BarChartRenderer(float maxValue) {
        this(maxValue, DEFAULT_MINIMUM_VALUE, DEFAULT_REFERENCE_VALUE);
    }

    public BarChartRenderer(float maxValue, float minValue) {
        this(maxValue, minValue, DEFAULT_REFERENCE_VALUE);
    }

    public BarChartRenderer(float maxValue, float minValue, float refValue) {
        this.maximumValue = maxValue;
        this.minimumValue = minValue;
        this.referenceValue = refValue;
        this.positiveValuePaint = DEFAULT_POSITIVE_VALUE_COLOUR;
        this.negativeValuePaint = DEFAULT_NEGATIVE_VALUE_COLOUR;
        this.warningValuePaint = DEFAULT_WARNING_VALUE_COLOUR;
        this.warningVisible = false;
        this.numberAndChart = DEFAULT_NUMBER_AND_CHART;
        this.setOpaque(true);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        this.value = value;
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (value != null && value instanceof Number) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // get size
            int width = this.getWidth() - 1;
            int height = this.getHeight();
            int xPos = 0;
            int yPos = -1;


            // font metrics
            FontMetrics fontMetrics = g2.getFontMetrics();
            int fontDescent = fontMetrics.getDescent();
            int fontAscent = fontMetrics.getAscent();


            // draw text
            if (numberAndChart) {
                String str;
                if (((Number) value).doubleValue() > Constants.MAX_NON_SCIENTIFIC_NUMBER
                        || ((Number) value).doubleValue() < Constants.MIN_MON_SCIENTIFIC_NUMBER) {
                    str = Constants.LARGE_DECIMAL_NUMBER_FORMATTER.format(value);
                } else {
                    str = Constants.DECIMAL_FORMATTER.format(value);
                }
                g2.drawString(str, xPos, height - fontDescent);

            }

            // draw bar chart
            int barWidth = -1;
            int barHeight = fontAscent;
            // calculate x position
            xPos = width / 2;
            // calculate referent point if exist
            int referenceWidth = -1;
            if (referenceValue != minimumValue && referenceValue != maximumValue) {
                referenceWidth = convertFloatToInt((width - xPos) / 2);
            }

            // calculate y position
            yPos = height - fontAscent - fontDescent;

            // float value
            float floatValue = ((Number) value).floatValue();

            // calculate x position
            if (referenceWidth > -1) {
                float relativeValue = floatValue - referenceValue;
                if (relativeValue >= 0) {
                    // greater or equal than reference value
                    xPos += referenceWidth;
                    float percentage = relativeValue / (maximumValue - referenceValue);
                    if (percentage > 1) {
                        percentage = 1;
                    }
                    barWidth = convertFloatToInt((width - xPos) * percentage);
                    // set a minimum width
                    if (barWidth == 0) {
                        barWidth = 1;
                    }
                    g2.setPaint(positiveValuePaint);
                } else {
                    // less than reference value
                    float percentage = relativeValue / (minimumValue - referenceValue);
                    if (percentage > 1) {
                        percentage = 1;
                    }
                    barWidth = convertFloatToInt(referenceWidth * percentage);
                    xPos += referenceWidth - barWidth;
                    // set a minimum width
                    if (barWidth == 0) {
                        barWidth = 1;
                        xPos--;
                    }
                    g2.setPaint((negativeValuePaint));
                }
            } else {
                // no reference point
                barWidth = convertFloatToInt((width - xPos) * (((Number) value).floatValue() / (maximumValue - minimumValue)));
                g2.setPaint(positiveValuePaint);
            }

            if (warningVisible && (floatValue > maximumValue || floatValue < minimumValue)) {
                g2.setPaint(warningValuePaint);
            }

            g2.fillRect(xPos, yPos, barWidth, barHeight);
            g2.dispose();
        }
    }

    private int convertFloatToInt(double val) {
        return new Float(val).intValue();
    }

    public double getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(float maximumValue) {
        this.maximumValue = maximumValue;
    }

    public double getReferenceValue() {
        return referenceValue;
    }

    public void setReferenceValue(float referenceValue) {
        this.referenceValue = referenceValue;
    }

    public double getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(float minimumValue) {
        this.minimumValue = minimumValue;
    }

    public Paint getPositiveValuePaint() {
        return positiveValuePaint;
    }

    public void setPositiveValuePaint(Paint positiveValuePaint) {
        this.positiveValuePaint = positiveValuePaint;
    }

    public Paint getNegativeValuePaint() {
        return negativeValuePaint;
    }

    public void setNegativeValuePaint(Paint negativeValuePaint) {
        this.negativeValuePaint = negativeValuePaint;
    }

    public Paint getWarningValuePaint() {
        return warningValuePaint;
    }

    public void setWarningValuePaint(Paint warningValuePaint) {
        this.warningValuePaint = warningValuePaint;
    }

    public boolean isNumberAndChart() {
        return numberAndChart;
    }

    public void setNumberAndChart(boolean numberAndChart) {
        this.numberAndChart = numberAndChart;
    }

    public boolean isWarningVisible() {
        return warningVisible;
    }

    public void setWarningVisible(boolean warningVisible) {
        this.warningVisible = warningVisible;
    }
}
