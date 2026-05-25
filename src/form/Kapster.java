/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package form;

import component.ThemeColor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import dialog.TambahKapsterDialog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import component.RoundedPanel;

/**
 *
 * @author rhmnsae
 */
public class Kapster extends javax.swing.JPanel {

    /**
     * Creates new form Kapster
     */
    
    private javax.swing.Timer autoRefresh;
    
    public Kapster() {
        initComponents();
        Connection conn = Koneksi.getKoneksi();
        
//        PreparedStatement ps3 = conn.prepareStatement(
//            "SELECT COUNT(*) as total FROM kapster"
//        );
//        ResultSet rs3 = ps3.executeQuery();
//        if (rs3.next()) {
//            totalKaps.setText(String.valueOf(rs3.getInt("totalKaps")));
//        }
//        rs3.close();
//        ps3.close();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 40, 40));

        // model
        tableKapster.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Nama", "Spesialisasi", "Komisi (%)", "Total TX", "Pendapatan", "Komisi Kapster", "Status", "Aksi"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // hanya kolom Aksi
            }
        });

        // Sembunyikan kolom ID
        tableKapster.getColumnModel().getColumn(0).setMinWidth(0);
        tableKapster.getColumnModel().getColumn(0).setMaxWidth(0);
        tableKapster.getColumnModel().getColumn(0).setWidth(0);

        // satu blok renderer — index sudah geser +1 karena ada kolom ID
        tableKapster.getColumnModel().getColumn(3).setCellRenderer(new table.KomisiCellRenderer());
        tableKapster.getColumnModel().getColumn(7).setCellRenderer(new table.StatusTableCellRenderer());
        tableKapster.getColumnModel().getColumn(8).setCellRenderer(new table.ItemActionCellRenderer());
        tableKapster.getColumnModel().getColumn(8).setCellEditor(new table.KapsterActionCellEditor());

        // lebar kolom
        tableKapster.getColumnModel().getColumn(1).setPreferredWidth(180);
        tableKapster.getColumnModel().getColumn(2).setPreferredWidth(160);
        tableKapster.getColumnModel().getColumn(3).setPreferredWidth(160);
        tableKapster.getColumnModel().getColumn(4).setPreferredWidth(80);
        tableKapster.getColumnModel().getColumn(5).setPreferredWidth(120);
        tableKapster.getColumnModel().getColumn(6).setPreferredWidth(120);
        tableKapster.getColumnModel().getColumn(7).setPreferredWidth(80);
        tableKapster.getColumnModel().getColumn(8).setPreferredWidth(100);

        // search
        txtSearch.setHint("Cari kapster...");
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                loadData();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                loadData();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
            }
        });

        // header
        tableKapster.getTableHeader().setOpaque(false);
        tableKapster.getTableHeader().setBackground(ThemeColor.SURFACE_2);
        tableKapster.getTableHeader().setForeground(ThemeColor.TEXT_MUTED);
        tableKapster.getTableHeader().setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13));
        tableKapster.getTableHeader().setBorder(null);
        tableKapster.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 40));

        // table
        tableKapster.setBackground(ThemeColor.SURFACE);
        tableKapster.setForeground(ThemeColor.TEXT);
        tableKapster.setRowHeight(60);
        tableKapster.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13));
        tableKapster.setSelectionBackground(ThemeColor.SURFACE);
        tableKapster.setSelectionForeground(ThemeColor.TEXT);
        tableKapster.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tableKapster.setShowGrid(false);
        tableKapster.getTableHeader().setReorderingAllowed(false);
        tableKapster.getTableHeader().setResizingAllowed(false);
        tableKapster.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // scroll
        scrollTable.getVerticalScrollBar().setUI(new component.ModernScrollBarUI());
        scrollTable.getVerticalScrollBar().setPreferredSize(new java.awt.Dimension(10, 0));
        scrollTable.getViewport().setBackground(ThemeColor.SURFACE);
        scrollTable.setBackground(ThemeColor.SURFACE);
        scrollTable.setBorder(null);

        scrollTable.setCorner(javax.swing.JScrollPane.LOWER_RIGHT_CORNER, new javax.swing.JPanel() {
            {
                setBackground(ThemeColor.SURFACE);
            }
        });
        scrollTable.setCorner(javax.swing.JScrollPane.LOWER_LEFT_CORNER, new javax.swing.JPanel() {
            {
                setBackground(ThemeColor.SURFACE);
            }
        });
        scrollTable.setCorner(javax.swing.JScrollPane.UPPER_RIGHT_CORNER, new javax.swing.JPanel() {
            {
                setBackground(ThemeColor.SURFACE);
            }
        });

        // container
        tableContainer.setBackground(ThemeColor.SURFACE);
        tableContainer.setRadius(20);
        tableContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        ((component.RoundedPanel) cardPoin).setRadius(20);
        ((component.RoundedPanel) cardPoin3).setRadius(20);
        ((component.RoundedPanel) cardPoin4).setRadius(20);

        loadData();

        loadStats();
        
        new javax.swing.Timer(2000, e -> refreshDashboard()).start();

    }
    
    public void refreshDashboard() {
        loadData();
    }

    public void loadData() {
     DefaultTableModel model = (DefaultTableModel) tableKapster.getModel();
     model.setRowCount(0);

     String keyword = txtSearch.getText().trim();

     try {
         Connection conn = Koneksi.getKoneksi();

         String sql = """
             SELECT 
                 k.id,
                 k.nama,
                 k.no_hp,
                 k.spesialisasi,
                 k.komisi_persen,
                 k.status,
                 COUNT(t.id) AS total_tx,
                 COALESCE(SUM(t.total), 0) AS pendapatan,
                 COALESCE(SUM(t.komisi), 0) AS komisi_kapster
             FROM kapster k
             LEFT JOIN transaksi t ON k.id = t.id_kapster
             WHERE 1=1
         """;

         // tambahkan filter keyword jika ada
         if (!keyword.isEmpty()) {
             sql += " AND (k.nama LIKE ? OR k.spesialisasi LIKE ?)";
         }

         sql += """
             GROUP BY k.id, k.nama, k.no_hp, k.spesialisasi, k.komisi_persen, k.status
             ORDER BY k.id ASC
         """;

         PreparedStatement ps = conn.prepareStatement(sql);

         // set parameter jika ada keyword
         if (!keyword.isEmpty()) {
             ps.setString(1, "%" + keyword + "%");
             ps.setString(2, "%" + keyword + "%");
         }

         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
             int id = rs.getInt("id");
             String nama = rs.getString("nama");
             String noHp = rs.getString("no_hp");
             String spesialisasi = rs.getString("spesialisasi");
             int komisi = rs.getInt("komisi_persen");
             String status = rs.getString("status");
             int totalTx = rs.getInt("total_tx");
             long pendapatan = rs.getLong("pendapatan");
             long komisiKapster = pendapatan * komisi / 100;

             String namaHtml = "<html><div style='text-align:center;'>"
                     + "<b>" + nama + "</b><br>"
                     + "<span style='color:gray;'>" + noHp + "</span>"
                     + "</div></html>";

             String pendapatanFmt = "Rp " + String.format("%,d", pendapatan).replace(',', '.');
             String komisiKapsterFmt = "Rp " + String.format("%,d", komisiKapster).replace(',', '.');

             model.addRow(new Object[]{
                 id,
                 namaHtml,
                 spesialisasi,
                 komisi + "%(" + komisiKapsterFmt + ")",
                 totalTx,
                 pendapatanFmt,
                 komisiKapsterFmt,
                 status,
                 "Aksi"
             });
         }

         rs.close();
         ps.close();

     } catch (Exception e) {
         JOptionPane.showMessageDialog(this, "Gagal memuat data kapster:\n" + e.getMessage());
     }
 }

    public void loadStats() {
        try {
            Connection conn = Koneksi.getKoneksi();

            // 1. Total Kapster
            String sql1 = "SELECT COUNT(*) AS total FROM kapster";
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ResultSet rs1 = ps1.executeQuery();

            if (rs1.next()) {
                totalKaps.setText(String.valueOf(rs1.getInt("total")));
            }

            rs1.close();
            ps1.close();

            // 2. Total Komisi Semua Kapster
            String sql2 = """
                SELECT COALESCE(SUM(t.total * k.komisi_persen / 100), 0) AS total_komisi
                FROM transaksi t
                JOIN kapster k ON t.id_kapster = k.id
            """;

            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ResultSet rs2 = ps2.executeQuery();

            if (rs2.next()) {
                long totalKomisi = rs2.getLong("total_komisi");
                komisiKaps.setText("Rp " + String.format("%,d", totalKomisi).replace(',', '.'));
            }

            rs2.close();
            ps2.close();

            // AVG Komisi Kapster
            PreparedStatement ps3 = conn.prepareStatement(
                    "SELECT COALESCE("
                    + "  (SELECT SUM(t.total * k.komisi_persen / 100) FROM transaksi t JOIN kapster k ON t.id_kapster = k.id) "
                    + "  / (SELECT COUNT(*) FROM kapster), 0) AS avg_komisi"
            );

            ResultSet rs3 = ps3.executeQuery();

            if (rs3.next()) {
                long avgKomisi = rs3.getLong("avg_komisi");
                jLabel3.setText("Rp " + String.format("%,d", avgKomisi).replace(',', '.'));
            }

            rs3.close();
            ps3.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load stats: " + e.getMessage());
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

        btnTambah = new component.ModernButton();
        tableContainer = new component.RoundedPanel();
        scrollTable = new javax.swing.JScrollPane();
        tableKapster = new component.ModernTable();
        txtSearch = new component.ModernTextField();
        cardPoin = new RoundedPanel();
        totalPoin = new javax.swing.JLabel();
        totalKaps = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        cardPoin3 = new RoundedPanel();
        totalPoin1 = new javax.swing.JLabel();
        komisiKaps = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        cardPoin4 = new RoundedPanel();
        totalPoin2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();

        setBackground(new java.awt.Color(10, 10, 11));

        btnTambah.setText("+ Tambah Kapster");
        btnTambah.addActionListener(this::btnTambahActionPerformed);

        tableContainer.setMaximumSize(new java.awt.Dimension(0, 0));

        scrollTable.setBorder(null);
        scrollTable.setToolTipText("");

        tableKapster.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama", "Spesialisasi", "Komisi (%)", "Total TX", "Pendapatan", "Komisi Kapster", "Status", "Aksi"
            }
        ));
        scrollTable.setViewportView(tableKapster);

        javax.swing.GroupLayout tableContainerLayout = new javax.swing.GroupLayout(tableContainer);
        tableContainer.setLayout(tableContainerLayout);
        tableContainerLayout.setHorizontalGroup(
            tableContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollTable)
        );
        tableContainerLayout.setVerticalGroup(
            tableContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollTable, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
        );

        txtSearch.setPreferredSize(new java.awt.Dimension(250, 40));
        txtSearch.addActionListener(this::txtSearchActionPerformed);

        cardPoin.setBackground(new java.awt.Color(22, 22, 26));

        totalPoin.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        totalPoin.setForeground(new java.awt.Color(136, 136, 152));
        totalPoin.setText("Total Kapster");

        totalKaps.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        totalKaps.setForeground(new java.awt.Color(201, 168, 76));
        totalKaps.setText("jLabel1");

        jPanel1.setBackground(new java.awt.Color(201, 168, 76));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout cardPoinLayout = new javax.swing.GroupLayout(cardPoin);
        cardPoin.setLayout(cardPoinLayout);
        cardPoinLayout.setHorizontalGroup(
            cardPoinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardPoinLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(cardPoinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(totalPoin, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalKaps, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(198, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        cardPoinLayout.setVerticalGroup(
            cardPoinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardPoinLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(totalPoin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalKaps)
                .addGap(27, 27, 27))
        );

        cardPoin3.setBackground(new java.awt.Color(22, 22, 26));

        totalPoin1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        totalPoin1.setForeground(new java.awt.Color(136, 136, 152));
        totalPoin1.setText("Komisi Kapster Bulan Ini");

        komisiKaps.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        komisiKaps.setForeground(new java.awt.Color(240, 239, 232));
        komisiKaps.setText("jLabel2");

        jPanel2.setBackground(new java.awt.Color(85, 153, 221));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout cardPoin3Layout = new javax.swing.GroupLayout(cardPoin3);
        cardPoin3.setLayout(cardPoin3Layout);
        cardPoin3Layout.setHorizontalGroup(
            cardPoin3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardPoin3Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(cardPoin3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(komisiKaps, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalPoin1, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(109, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        cardPoin3Layout.setVerticalGroup(
            cardPoin3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardPoin3Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addComponent(totalPoin1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(komisiKaps)
                .addGap(27, 27, 27))
        );

        cardPoin4.setBackground(new java.awt.Color(22, 22, 26));

        totalPoin2.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        totalPoin2.setForeground(new java.awt.Color(136, 136, 152));
        totalPoin2.setText("AVG Komisi Kapster");

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 239, 232));
        jLabel3.setText("jLabel3");

        jPanel3.setBackground(new java.awt.Color(76, 175, 130));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout cardPoin4Layout = new javax.swing.GroupLayout(cardPoin4);
        cardPoin4.setLayout(cardPoin4Layout);
        cardPoin4Layout.setHorizontalGroup(
            cardPoin4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardPoin4Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(cardPoin4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalPoin2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(125, Short.MAX_VALUE))
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        cardPoin4Layout.setVerticalGroup(
            cardPoin4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardPoin4Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(totalPoin2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(27, 27, 27))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cardPoin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(cardPoin3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(cardPoin4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cardPoin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cardPoin3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cardPoin4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(tableContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
        loadData();
    }//GEN-LAST:event_txtSearchActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        // TODO add your handling code here:       
        java.awt.Frame parent
                = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this);

        TambahKapsterDialog dialog = new TambahKapsterDialog(parent, true);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        loadData();
    }//GEN-LAST:event_btnTambahActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.ModernButton btnTambah;
    private javax.swing.JPanel cardPoin;
    private javax.swing.JPanel cardPoin3;
    private javax.swing.JPanel cardPoin4;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel komisiKaps;
    private javax.swing.JScrollPane scrollTable;
    private component.RoundedPanel tableContainer;
    private component.ModernTable tableKapster;
    private javax.swing.JLabel totalKaps;
    private javax.swing.JLabel totalPoin;
    private javax.swing.JLabel totalPoin1;
    private javax.swing.JLabel totalPoin2;
    private component.ModernTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
