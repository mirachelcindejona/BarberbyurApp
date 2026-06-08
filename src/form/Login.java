/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package form;

import component.ThemeColor;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import main.Main;
/**
 *
 * @author marwa
 */
public class Login extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(Login.class.getName());

    private component.RoundedPasswordField passwordField;
    private component.RoundedImageLabel    logoLabel;
    private javax.swing.JLabel             lblAppName;

    public Login() {
        initComponents();

        loginContainer.setRadius(20);
        txtUsername.setHint("Masukkan username");

        // Set layout null pada loginContainer
        loginContainer.setLayout(null);

        // Inisialisasi logo
        logoLabel = new component.RoundedImageLabel(80);
        logoLabel.setImage(
            getClass().getResource("/icon/logo.png"),
            80, 80
        );
        loginContainer.add(logoLabel);

        // Inisialisasi label nama app
        lblAppName = new javax.swing.JLabel("Login ke BARBERBYUR");
        lblAppName.setForeground(ThemeColor.TEXT_MUTED);
        lblAppName.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 22));
        lblAppName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loginContainer.add(lblAppName);

        // Inisialisasi passwordField
        passwordField = new component.RoundedPasswordField(12);
        passwordField.setHint("Masukkan password");
        loginContainer.add(passwordField);

        // Set layout null contentPane agar setBounds bekerja
        getContentPane().setLayout(null);

        // Listener resize — semua komponen ikut responsive
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateLayout();
            }
        });

        // Trigger layout pertama kali setelah frame siap
        SwingUtilities.invokeLater(() -> {
            jPanel1.setBounds(0, 0,
                getContentPane().getWidth(),
                getContentPane().getHeight()
            );
            loginContainer.setBounds(
                56, 71,
                getContentPane().getWidth() - 112,
                getContentPane().getHeight() - 142
            );
            updateLayout();
        });

        setLocationRelativeTo(null);
    }

    private void updateLayout() {
        // jPanel1 mengikuti contentPane
        jPanel1.setBounds(0, 0,
            getContentPane().getWidth(),
            getContentPane().getHeight()
        );

        // loginContainer mengikuti jPanel1 dengan margin
        int margin = 56;
        loginContainer.setBounds(
            margin, 71,
            jPanel1.getWidth() - (margin * 2),
            jPanel1.getHeight() - 142
        );

        int containerW  = loginContainer.getWidth();
        int containerH  = loginContainer.getHeight();

        int fieldW      = 350;  // lebar field & button
        int fieldH      = 45;
        int btnW        = 350;
        int btnH        = 40;
        int logoSize    = 80;
        int gapLogo     = 10;   // jarak logo ke label
        int labelH      = 30;   // tinggi label nama app
        int gapLabel    = 24;   // jarak label ke username
        int gapField    = 18;   // jarak antar field
        int gapBtn      = 30;   // jarak password ke button

        // Total tinggi semua komponen
        int totalH = logoSize + gapLogo + labelH + gapLabel
                   + fieldH + gapField + fieldH + gapBtn + btnH;

        int startY      = (containerH - totalH) / 2;
        int centerX     = (containerW - fieldW) / 2;
        int centerXBtn  = (containerW - btnW)   / 2;
        int centerXLogo = (containerW - logoSize) / 2;

        // Logo
        logoLabel.setBounds(centerXLogo, startY, logoSize, logoSize);

        // Label nama app
        int labelY = startY + logoSize + gapLogo;
        lblAppName.setBounds(0, labelY, containerW, labelH);

        // Field username
        int afterLabel = labelY + labelH + gapLabel;
        txtUsername.setBounds(centerX, afterLabel, fieldW, fieldH);

        // Field password
        passwordField.setBounds(centerX, afterLabel + fieldH + gapField, fieldW, fieldH);

        // Button login
        btnLogin.setBounds(
            centerXBtn,
            afterLabel + fieldH + gapField + fieldH + gapBtn,
            btnW, btnH
        );

        revalidate();
        repaint();
    }

    private void login() {
    String username = txtUsername.getText().trim();
    String password = passwordField.getPasswordText();

    if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(
            this,
            "Username dan password tidak boleh kosong",
            "Peringatan",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    try {
        java.sql.Connection conn = Koneksi.getKoneksi();

        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        java.sql.PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);

        java.sql.ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            JOptionPane.showMessageDialog(this, "Login berhasil");
            Main main = new Main();
            main.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Username atau password salah",
                "Login Gagal",
                JOptionPane.ERROR_MESSAGE
            );
        }

        rs.close();
        ps.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Koneksi database gagal: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        loginContainer = new component.RoundedPanel();
        txtUsername = new component.ModernTextField();
        btnLogin = new component.ModernButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(10, 10, 11));

        jPanel1.setBackground(new java.awt.Color(10, 10, 11));

        loginContainer.setBackground(new java.awt.Color(26, 26, 27));

        txtUsername.setPreferredSize(new java.awt.Dimension(250, 40));
        txtUsername.addActionListener(this::txtUsernameActionPerformed);

        btnLogin.setText("Login");
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnLoginMouseClicked(evt);
            }
        });
        btnLogin.addActionListener(this::btnLoginActionPerformed);

        javax.swing.GroupLayout loginContainerLayout = new javax.swing.GroupLayout(loginContainer);
        loginContainer.setLayout(loginContainerLayout);
        loginContainerLayout.setHorizontalGroup(
            loginContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginContainerLayout.createSequentialGroup()
                .addContainerGap(186, Short.MAX_VALUE)
                .addGroup(loginContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginContainerLayout.createSequentialGroup()
                        .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(96, 96, 96))
                    .addComponent(txtUsername, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(177, 177, 177))
        );
        loginContainerLayout.setVerticalGroup(
            loginContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginContainerLayout.createSequentialGroup()
                .addContainerGap(218, Short.MAX_VALUE)
                .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(120, 120, 120)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(74, 74, 74))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(loginContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(57, 57, 57))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addComponent(loginContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(71, 71, 71))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        passwordField.requestFocus();

    }//GEN-LAST:event_txtUsernameActionPerformed

    private void btnLoginMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLoginMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLoginMouseClicked

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
      login();
    }//GEN-LAST:event_btnLoginActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info
                    : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (
            ReflectiveOperationException |
            javax.swing.UnsupportedLookAndFeelException ex
        ) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.ModernButton btnLogin;
    private javax.swing.JPanel jPanel1;
    private component.RoundedPanel loginContainer;
    private component.ModernTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
