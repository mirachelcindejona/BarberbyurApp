/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package form;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import javax.swing.BoxLayout;
import form.Kasir;
import model.Pelanggan;

/**
 *
 * @author asus_
 */
public class Pilih_Member extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Pilih_Member.class.getName());

    // Variabel untuk menyimpan form Kasir asal
    private Kasir formKasir;

    // Constructor diubah agar menerima form Kasir
    public Pilih_Member(Kasir kasir) {
        this.formKasir = kasir;
        initComponents();
        
        // Atur agar form ini tertutup tanpa mematikan seluruh aplikasi
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        // Atur posisi pop up ke tengah layar
        this.setLocationRelativeTo(null); 
        
        setupUI();
        loadDataMember("");
    }

    private void setupUI() {
        // Layout menurun untuk list member
        jPanelMember.setLayout(new BoxLayout(jPanelMember, BoxLayout.Y_AXIS));
        
        // SETTING PLACEHOLDER UNTUK TXTSEARCHMEMBER
        txtSearchMember.setCaretColor(new java.awt.Color(201, 168, 76));
        txtSearchMember.setText(""); 
        
        txtSearchMember.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override
            protected void paintSafely(java.awt.Graphics g) {
                super.paintSafely(g);
                javax.swing.text.JTextComponent comp = getComponent();
                if (comp.getText().isEmpty() && !comp.hasFocus()) {
                    java.awt.Graphics2D g2d = (java.awt.Graphics2D) g.create();
                    g2d.setColor(new java.awt.Color(136, 136, 152)); 
                    g2d.setFont(comp.getFont().deriveFont(java.awt.Font.ITALIC));
                    int y = (comp.getHeight() + comp.getFontMetrics(comp.getFont()).getAscent()) / 2 - 2;
                    g2d.drawString("Cari nama atau no hp...", 10, y);
                    g2d.dispose();
                }
            }
        });
        
        txtSearchMember.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) { txtSearchMember.repaint(); }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) { txtSearchMember.repaint(); }
        });

        // PENCARIAN REALTIME
        txtSearchMember.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { loadDataMember(txtSearchMember.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { loadDataMember(txtSearchMember.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadDataMember(txtSearchMember.getText()); }
        });
    }

    private void loadDataMember(String keyword) {
        jPanelMember.removeAll();

        try {
            java.sql.Connection conn = Koneksi.getKoneksi();
            
            String sql = "SELECT p.id, p.nama, p.no_hp, p.point AS poin, p.tier, "
                       + "(SELECT COUNT(*) FROM transaksi t WHERE t.id_pelanggan = p.id) AS kunjungan "
                       + "FROM pelanggan p "
                       + "WHERE p.nama LIKE ? OR p.no_hp LIKE ?";
                       
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");
            java.sql.ResultSet rs = pst.executeQuery();

            int jumlahData = 0;

            while (rs.next()) {
                jumlahData++;
                
                model.Pelanggan p = new model.Pelanggan(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("no_hp"),
                    rs.getInt("kunjungan"),
                    rs.getInt("poin"), 
                    rs.getString("tier")
                );

                System.out.println("Memuat member: " + p.nama);

                swing.ListMember item = new swing.ListMember();
                // Passing data ke GUI List Member
                item.setData(p.nama, p.no_hp, p.kunjungan, p.poin, p.tier);

                // FIX LAYOUT SWING
                item.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 60));
                item.setMinimumSize(new java.awt.Dimension(0, 60));
                item.setPreferredSize(new java.awt.Dimension(400, 60));
                item.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

                item.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        formKasir.setMemberTerpilih(p.id, p.nama, p.poin, p.tier);
                        dispose(); 
                    }
                });

                jPanelMember.add(item);
                
                jPanelMember.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));
            }
            
            System.out.println("Total data ditemukan: " + jumlahData);
            
            jPanelMember.revalidate();
            jPanelMember.repaint();

        } catch (Exception e) {
            System.err.println("Error memuat data member: ");
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
        txtSearchMember = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnTanpaMember = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jScrollPaneMember = new javax.swing.JScrollPane();
        jPanelMember = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Pilih Member");

        jPanel1.setBackground(new java.awt.Color(10, 10, 11));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        txtSearchMember.setBackground(new java.awt.Color(22, 22, 26));
        txtSearchMember.setForeground(new java.awt.Color(136, 136, 152));
        txtSearchMember.setToolTipText("");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Pilih Member / Pelanggan");

        btnTanpaMember.setBackground(new java.awt.Color(22, 22, 26));
        btnTanpaMember.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTanpaMember.setForeground(new java.awt.Color(255, 255, 255));
        btnTanpaMember.setText("Tanpa Member");
        btnTanpaMember.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTanpaMember.addActionListener(this::btnTanpaMemberActionPerformed);

        jPanel2.setBackground(new java.awt.Color(10, 10, 11));
        jPanel2.setPreferredSize(new java.awt.Dimension(400, 300));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPaneMember.setBorder(null);
        jScrollPaneMember.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanelMember.setBackground(new java.awt.Color(10, 10, 11));

        javax.swing.GroupLayout jPanelMemberLayout = new javax.swing.GroupLayout(jPanelMember);
        jPanelMember.setLayout(jPanelMemberLayout);
        jPanelMemberLayout.setHorizontalGroup(
            jPanelMemberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 405, Short.MAX_VALUE)
        );
        jPanelMemberLayout.setVerticalGroup(
            jPanelMemberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 294, Short.MAX_VALUE)
        );

        jScrollPaneMember.setViewportView(jPanelMember);

        jPanel2.add(jScrollPaneMember, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnTanpaMember))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtSearchMember))))
                .addGap(16, 16, 16))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtSearchMember, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTanpaMember, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTanpaMemberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTanpaMemberActionPerformed
        // TODO add your handling code here:
        formKasir.setMemberTerpilih(0, "Pilih Member", 0, "Bronze"); // Set default
        dispose(); // Tutup pop up
    }//GEN-LAST:event_btnTanpaMemberActionPerformed

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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Pilih_Member(null).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnTanpaMember;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelMember;
    private javax.swing.JScrollPane jScrollPaneMember;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField txtSearchMember;
    // End of variables declaration//GEN-END:variables
}
