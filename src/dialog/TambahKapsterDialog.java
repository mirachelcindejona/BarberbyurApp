/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package dialog;

import component.ThemeColor;
import javax.swing.JOptionPane;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import form.Koneksi;
import form.Kasir;
/**
 *
 * @author rhmnsae
 */
public class TambahKapsterDialog extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TambahKapsterDialog.class.getName());
    
    /**
     * Creates new form TambahPelangganDialog
     */
    public TambahKapsterDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        mainPanel.setRadius(20);
        mainPanel.setBackground(ThemeColor.BACKGROUND);

        labelJudul.setForeground(ThemeColor.TEXT);

        btnBatal.setOutlineVariant();

        // placeholder
        namaField.setHint("Nama lengkap");
        namaField1.setHint("Contoh: 081xxxx");
        spealisasiField.setHint("Contoh: Gunting Rambut, Cukur Jenggot");
        komisiKapsterField.setHint("Contoh: 40");

        // komisiOwnerField read-only
        komisiOwnerField.setEditable(false);
        komisiOwnerField.setFocusable(false);
        komisiOwnerField.setText("100");

        // auto-hitung owner saat kapster diketik
        komisiKapsterField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                String text = komisiKapsterField.getText().trim();
                try {
                    int kapster = Integer.parseInt(text);
                    if (kapster < 0) {
                        kapster = 0;
                    }
                    if (kapster > 100) {
                        kapster = 100;
                    }
                    komisiOwnerField.setText(String.valueOf(100 - kapster));
                } catch (NumberFormatException e) {
                    komisiOwnerField.setText("");
                }
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }
        });

        styleComboBox(jComboBoxStatus);
    }
    
    class RoundedBorder extends AbstractBorder {

        private final int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g,
                int x, int y, int width, int height) {

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(new Color(45, 45, 55));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 12);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 12;
            insets.top = insets.bottom = 8;
            return insets;
        }
    }
    
    private void styleComboBox(javax.swing.JComboBox<String> comboBox) {
                
        comboBox.setOpaque(false);

        comboBox.setBackground(new Color(30, 30, 36));
        comboBox.setForeground(Color.WHITE);

        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 12));

        comboBox.setBorder(new RoundedBorder(20));

        comboBox.setFocusable(false);

        comboBox.setRenderer(new javax.swing.DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    javax.swing.JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                Component c = super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                list.setBackground(new Color(30, 30, 36));
                list.setForeground(Color.WHITE);

                setBorder(javax.swing.BorderFactory.createEmptyBorder(
                        5, 10, 5, 10));

                return c;
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new component.RoundedPanel();
        labelJudul = new javax.swing.JLabel();
        labelNama = new javax.swing.JLabel();
        namaField = new component.ModernTextField();
        btnSimpan = new component.ModernButton();
        btnBatal = new component.ModernButton();
        labelNama3 = new javax.swing.JLabel();
        komisiKapsterField = new component.ModernTextField();
        labelNama4 = new javax.swing.JLabel();
        spealisasiField = new component.ModernTextField();
        labelNama5 = new javax.swing.JLabel();
        jComboBoxStatus = new javax.swing.JComboBox<>();
        labelNama1 = new javax.swing.JLabel();
        namaField1 = new component.ModernTextField();
        komisiOwnerField = new component.ModernTextField();
        labelNama6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tambah Kapster");
        setResizable(false);

        labelJudul.setFont(new java.awt.Font("SansSerif", 1, 28)); // NOI18N
        labelJudul.setText("Tambah Kapster");

        labelNama.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        labelNama.setForeground(new java.awt.Color(240, 237, 232));
        labelNama.setText("Nama Lengkap*");

        namaField.addActionListener(this::namaFieldActionPerformed);

        btnSimpan.setText("Simpan");
        btnSimpan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSimpanMouseClicked(evt);
            }
        });
        btnSimpan.addActionListener(this::btnSimpanActionPerformed);

        btnBatal.setText("Batal");
        btnBatal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBatalMouseClicked(evt);
            }
        });
        btnBatal.addActionListener(this::btnBatalActionPerformed);

        labelNama3.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        labelNama3.setForeground(new java.awt.Color(240, 237, 232));
        labelNama3.setText("Komisi Kapster (%)*");

        labelNama4.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        labelNama4.setForeground(new java.awt.Color(240, 237, 232));
        labelNama4.setText("Spesialisasi*");

        labelNama5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        labelNama5.setForeground(new java.awt.Color(240, 237, 232));
        labelNama5.setText("Status*");

        jComboBoxStatus.setBackground(new java.awt.Color(30, 30, 36));
        jComboBoxStatus.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jComboBoxStatus.setForeground(new java.awt.Color(240, 237, 232));
        jComboBoxStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Aktif", "Nonaktif" }));
        jComboBoxStatus.addActionListener(this::jComboBoxStatusActionPerformed);

        labelNama1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        labelNama1.setForeground(new java.awt.Color(240, 237, 232));
        labelNama1.setText("No. HP*");

        namaField1.addActionListener(this::namaField1ActionPerformed);

        labelNama6.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        labelNama6.setForeground(new java.awt.Color(240, 237, 232));
        labelNama6.setText("Estimasi Bagi Hasil Owner (%)");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelJudul, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                            .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(komisiKapsterField, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelNama3, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBoxStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelNama5, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(spealisasiField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelNama)
                                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                            .addComponent(labelNama4)
                                            .addGap(127, 127, 127))
                                        .addComponent(namaField, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelNama1)
                                    .addComponent(namaField1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(komisiOwnerField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelNama6, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(labelJudul)
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(labelNama)
                        .addGap(43, 43, 43))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(labelNama1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(namaField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(namaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(labelNama4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spealisasiField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelNama3)
                    .addComponent(labelNama5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(komisiKapsterField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(labelNama6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(komisiOwnerField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpanMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSimpanMouseClicked

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        String nama = namaField.getText().trim();
        String telepon = namaField1.getText().trim();
        String spesialisasi = spealisasiField.getText().trim();
        String komisiStr = komisiKapsterField.getText().trim();
        String statusUI = jComboBoxStatus.getSelectedItem().toString();

        // Validasi kosong
        if (nama.isEmpty() || telepon.isEmpty() || spesialisasi.isEmpty() || komisiStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi!");
            return;
        }

        // Validasi angka
        int komisiPersen;
        try {
            komisiPersen = Integer.parseInt(komisiStr);
            if (komisiPersen < 0 || komisiPersen > 100) {
                JOptionPane.showMessageDialog(this, "Komisi harus antara 0 – 100!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Komisi harus berupa angka!");
            return;
        }

        String statusDB = statusUI.equals("Aktif") ? "Aktif" : "Nonaktif";

        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "INSERT INTO kapster (nama, no_hp, spesialisasi, komisi_persen, status) "
                    + "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nama);
            ps.setString(2, telepon);
            ps.setString(3, spesialisasi);
            ps.setInt(4, komisiPersen);
            ps.setString(5, statusDB);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Kapster berhasil ditambahkan!");
            dispose();
            Kasir.refreshKapster();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + e.getMessage());
        }   
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnBatalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBatalMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBatalMouseClicked

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        dispose();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void namaFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namaFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namaFieldActionPerformed

    private void jComboBoxStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxStatusActionPerformed

    private void namaField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namaField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namaField1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.ModernButton btnBatal;
    private component.ModernButton btnSimpan;
    private javax.swing.JComboBox<String> jComboBoxStatus;
    private component.ModernTextField komisiKapsterField;
    private component.ModernTextField komisiOwnerField;
    private javax.swing.JLabel labelJudul;
    private javax.swing.JLabel labelNama;
    private javax.swing.JLabel labelNama1;
    private javax.swing.JLabel labelNama3;
    private javax.swing.JLabel labelNama4;
    private javax.swing.JLabel labelNama5;
    private javax.swing.JLabel labelNama6;
    private component.RoundedPanel mainPanel;
    private component.ModernTextField namaField;
    private component.ModernTextField namaField1;
    private component.ModernTextField spealisasiField;
    // End of variables declaration//GEN-END:variables
}
