package uk.ac.ebi.pride.toolsuite.gui.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * A list of static methods to alter the state of graphics
 * <p/>
 * User: rwang
 * Date: 05/07/2011
 * Time: 15:53
 */
public class GraphicsUtils {

    /**
     * Create an smooth drop shadow for a given image
     *
     * @param image given image
     * @param size  size of shadow
     * @return BufferedImage   new image with drop shadows
     */
    public static BufferedImage createDropShadow(BufferedImage image, int size) {
        BufferedImage shadow = new BufferedImage(image.getWidth() + 4 * size, image.getHeight() + 4 * size, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = shadow.createGraphics();
        g2.drawImage(image, size * 2, size * 2, null);

        // composite
        g2.setComposite(AlphaComposite.SrcIn);
        g2.setColor(Color.gray);
        g2.fillRoundRect(0, 0, shadow.getWidth(), shadow.getHeight(), 30, 30);


        g2.dispose();

        shadow = getGaussianBlurFilter(size, true).filter(shadow, null);
        shadow = getGaussianBlurFilter(size, false).filter(shadow, null);

        return shadow;

    }

    /**
     * Create an gaussian blur filter
     *
     * @param radius     radius of the filter
     * @param horizontal whether it is horizontal blur
     * @return ConvolveOp  filter
     */
    public static ConvolveOp getGaussianBlurFilter(int radius,
                                                   boolean horizontal) {
        if (radius < 1) {
            throw new IllegalArgumentException(
                    "Radius must be >= 1");
        }
        int size = radius * 2 + 1;
        float[] data = new float[size];
        float sigma = radius / 3.0f;
        float twoSigmaSquare = 2.0f * sigma * sigma;
        float sigmaRoot = (float)
                Math.sqrt(twoSigmaSquare * Math.PI);
        float total = 0.0f;
        for (int i = -radius; i <= radius; i++) {
            float distance = i * i;
            int index = i + radius;
            data[index] = (float) Math.exp(-distance / twoSigmaSquare)
                    / sigmaRoot;
            total += data[index];
        }
        for (int i = 0; i < data.length; i++) {
            data[i] /= total;
        }
        Kernel kernel;
        if (horizontal) {
            kernel = new Kernel(size, 1, data);
        } else {
            kernel = new Kernel(1, size, data);
        }
        return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    }

}
