/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package component;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;

/**
 *
 * @author marwa
 */
public class RoundedImageLabel extends JLabel {

    private BufferedImage image;
    private int radius;

    public RoundedImageLabel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    public void setImage(java.net.URL imageUrl, int width, int height) {
        try {
            java.awt.image.BufferedImage original =
                javax.imageio.ImageIO.read(imageUrl);

            // Scale gambar ke ukuran yang diinginkan
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(original, 0, 0, width, height, null);
            g2.dispose();

            setPreferredSize(new Dimension(width, height));
            repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (image == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Clip berbentuk bulat penuh (full rounded)
        Ellipse2D.Float circle = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
        g2.setClip(circle);

        // Gambar image di dalam clip
        g2.drawImage(image, 0, 0, getWidth(), getHeight(), null);

        g2.dispose();
    }
}