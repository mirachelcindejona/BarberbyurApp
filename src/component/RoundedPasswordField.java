/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package component;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author marwa
 */

public class RoundedPasswordField extends JPanel {

    private JPasswordField passwordField;
    private JButton btnToggle;
    private boolean isVisible = false;
    private int radius = 12;

    private Color bgColor     = new Color(30, 30, 36);
    private Color borderColor = new Color(60, 60, 70);
    private Color textColor   = Color.WHITE;
    private Color hintColor   = new Color(120, 120, 130);

    private String hintText = "";
    private boolean focused = false;

    public RoundedPasswordField() {
        init();
    }

    public RoundedPasswordField(int radius) {
        this.radius = radius;
        init();
    }

    private void init() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(0, 14, 0, 8));

        // Password field
        passwordField = new JPasswordField();
        passwordField.setOpaque(false);
        passwordField.setBackground(new Color(0, 0, 0, 0));
        passwordField.setBorder(null);
        passwordField.setForeground(textColor);
        passwordField.setCaretColor(textColor);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setEchoChar('●');

        // Load icon
        ImageIcon iconHide = loadIcon("/icon/hide-pw.png", 35, 35);

        // Tombol toggle
        if (iconHide != null) {
            btnToggle = new JButton(iconHide);
        } else {
            btnToggle = new JButton("O"); // fallback
        }
        btnToggle.setOpaque(false);
        btnToggle.setContentAreaFilled(false);
        btnToggle.setBorderPainted(false);
        btnToggle.setFocusPainted(false);
        btnToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnToggle.setForeground(hintColor);
        btnToggle.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnToggle.setPreferredSize(new Dimension(36, 36));
        btnToggle.addActionListener(e -> toggleVisibility());

        // Focus listener
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focused = true;
                repaint();
            }
            @Override
            public void focusLost(FocusEvent e) {
                focused = false;
                repaint();
            }
        });

        add(passwordField, BorderLayout.CENTER);
        add(btnToggle, BorderLayout.EAST);
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) {
                java.awt.image.BufferedImage img =
                    javax.imageio.ImageIO.read(url);
                java.awt.Image scaled = img.getScaledInstance(
                    width, height, java.awt.Image.SCALE_SMOOTH
                );
                return new ImageIcon(scaled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void toggleVisibility() {
        isVisible = !isVisible;
        passwordField.setEchoChar(isVisible ? (char) 0 : '●');

        // Ganti icon sesuai state
        ImageIcon icon = loadIcon(
            isVisible ? "/icon/unhide-pw.png" : "/icon/hide-pw.png",
            35, 35
        );
        if (icon != null) {
            btnToggle.setIcon(icon);
            btnToggle.setText("");
        } else {
            // Fallback teks jika icon tidak ditemukan
            btnToggle.setIcon(null);
            btnToggle.setText(isVisible ? "X" : "O");
        }

        passwordField.requestFocus();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Background rounded
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        // Border — selalu abu
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        // Hint/placeholder — tampil jika kosong dan tidak fokus
        if (!focused && passwordField.getPassword().length == 0
                && !hintText.isEmpty()) {
            g2.setColor(hintColor);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            FontMetrics fm = g2.getFontMetrics();
            int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(hintText, 14, textY);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintChildren(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setClip(new java.awt.geom.RoundRectangle2D.Float(
            0, 0, getWidth(), getHeight(), radius, radius
        ));
        super.paintChildren(g2);
        g2.dispose();
    }

    // ── Public API ──────────────────────────────────────

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public String getPasswordText() {
        return String.valueOf(passwordField.getPassword());
    }

    public void setHint(String hint) {
        this.hintText = hint;
        repaint();
    }

    public void setRadius(int r) {
        this.radius = r;
        repaint();
    }

    public void setBgColor(Color color) {
        this.bgColor = color;
        repaint();
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    public void setHintColor(Color color) {
        this.hintColor = color;
        repaint();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        btnToggle.setEnabled(enabled);
    }
}