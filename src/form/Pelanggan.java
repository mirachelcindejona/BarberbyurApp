/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package form;

import component.ThemeColor;
import dialog.TambahPelangganDialog;
import java.awt.Color;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author marwa
 */
public class Pelanggan extends javax.swing.JPanel {

    /**
     * Creates new form Pelanggan
     */
    public Pelanggan() {
        initComponents();
        
        setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 40, 40));
        
        //active search 
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                refreshTable();
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                refreshTable();
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                refreshTable();
            }
        });
        
        //block edit double click
        tablePelanggan.setModel(new javax.swing.table.DefaultTableModel(
        new Object[][] {},
        new String[] {
            "Pelanggan", "No. HP", "Total Kunjungan", "Poin", "Tier", "Kunjungan Terakhir", "Aksi"
        }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        });
        //custom search bar
        txtSearch.setPreferredSize(new java.awt.Dimension(250, 40));
        txtSearch.setHint("Cari pelanggan...");
        
        //custom header tabel
        tablePelanggan.getTableHeader().setOpaque(false);

        tablePelanggan.getTableHeader().setBackground(
        ThemeColor.SURFACE_2
        );

        tablePelanggan.getTableHeader().setForeground(
        ThemeColor.TEXT_MUTED
        );

        tablePelanggan.getTableHeader().setFont(
        new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13)
        );

        tablePelanggan.getTableHeader().setBorder(null);
        
        //custom scrollbar
        scrollTable.getVerticalScrollBar().setUI(
        new component.ModernScrollBarUI()
        );

        scrollTable.getVerticalScrollBar().setPreferredSize(
        new java.awt.Dimension(10,0)
        );
        
        // costum color
        cbTier.setBackground(
        ThemeColor.SURFACE_2
        );
        tableContainer.setBackground(
        ThemeColor.SURFACE
        );
        
        //set radius tabel
        tableContainer.setRadius(20);
        
        //padding pada table scroll bar
        scrollTable.setBorder(
        javax.swing.BorderFactory.createEmptyBorder()
        );

        tableContainer.setBorder(
        javax.swing.BorderFactory.createEmptyBorder(
                15, 15, 15, 15
        )
        );
        
        //menghapus border tabel
        tablePelanggan.setShowGrid(false);
        
        tablePelanggan.setRowHeight(56);
        tablePelanggan.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tablePelanggan.setSelectionBackground(ThemeColor.SURFACE);
        tablePelanggan.setSelectionForeground(ThemeColor.TEXT);
        tablePelanggan.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13));
        tablePelanggan.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 40));
        
        
        //custom dropdown
        cbTier.setFocusable(false);

        cbTier.setBorder(null);

        cbTier.setBackground(
        ThemeColor.BACKGROUND
        );

        cbTier.setForeground(
        ThemeColor.TEXT
        );
        
        //scrollbar transparan dan berwarna hitam
        scrollTable.getViewport().setBackground(
        ThemeColor.SURFACE
        );

        scrollTable.setBackground(
        ThemeColor.SURFACE
        );
        
        //menghilangkan border default dari scroll pane
        scrollTable.setBorder(null);
        
        //load data
        refreshTable();
        
        //off switch kolom
        tablePelanggan.getTableHeader().setReorderingAllowed(false);
        
        //fix width kolom
        tablePelanggan.getTableHeader().setResizingAllowed(false);
        tablePelanggan.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablePelanggan.getColumnModel().getColumn(6).setMinWidth(200);
        tablePelanggan.getColumnModel().getColumn(6).setPreferredWidth(200);
        
        //tier dan aksi
        tablePelanggan.getColumnModel()
        .getColumn(4)
        .setCellRenderer(
                new table.TierTableCellRenderer()
        );

        tablePelanggan.getColumnModel()
        .getColumn(6)
        .setCellRenderer(
                new table.ActionCellRenderer()
        );

        tablePelanggan.getColumnModel()
        .getColumn(6)
        .setCellEditor(
                new table.ActionCellEditor()
        );
        
        cbTier.setRenderer(new javax.swing.DefaultListCellRenderer() {

        @Override
        public java.awt.Component getListCellRendererComponent(
                javax.swing.JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {

        java.awt.Component c =
                super.getListCellRendererComponent(
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus
                );

        if (isSelected) {

            c.setBackground(ThemeColor.GOLD);

            c.setForeground(Color.BLACK);

        } else {

            c.setBackground(ThemeColor.SURFACE_2);

            c.setForeground(ThemeColor.TEXT);
        }

        return c;
        }
    });

    }  
    
    private void loadTable(String keyword, String tier) {
    DefaultTableModel model = (DefaultTableModel) tablePelanggan.getModel();
    model.setRowCount(0);

    try {
        Connection conn = Koneksi.getKoneksi();
        
        StringBuilder sql = new StringBuilder(
            "SELECT nama, no_hp, total_kunjungan, point, tier, tanggal_kunjungan_terakhir FROM pelanggan WHERE 1=1"
        );

        // filter keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (nama LIKE ? OR no_hp LIKE ?)");
        }

        // filter tier
        if (tier != null && !tier.equals("Semua Tier")) {
            sql.append(" AND tier = ?");
        }

        java.sql.PreparedStatement ps = conn.prepareStatement(sql.toString());

        int paramIndex = 1;

        if (keyword != null && !keyword.trim().isEmpty()) {
            ps.setString(paramIndex++, "%" + keyword.trim() + "%");
            ps.setString(paramIndex++, "%" + keyword.trim() + "%");
        }

        if (tier != null && !tier.equals("Semua Tier")) {
            ps.setString(paramIndex++, tier);
        }

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            java.sql.Date tglKunjungan = rs.getDate("tanggal_kunjungan_terakhir");
            String kunjunganTerakhir = (tglKunjungan != null) ? tglKunjungan.toString() : "-";

            model.addRow(new Object[]{
                rs.getString("nama"),
                rs.getString("no_hp"),
                rs.getInt("total_kunjungan"),
                rs.getInt("point"),
                rs.getString("tier") != null ? rs.getString("tier") : "Bronze",
                kunjunganTerakhir,
                ""
            });
        }

        rs.close();
        ps.close();

        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(
                this,
                "Gagal load data: " + e.getMessage()
            );
        }
    }
    
    public void refreshTable() {
        String keyword = txtSearch.getText().trim();
        String tier = (String) cbTier.getSelectedItem();
        loadTable(keyword, tier);
    }
    
     
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtSearch = new component.ModernTextField();
        cbTier = new javax.swing.JComboBox<>();
        btnTambah = new component.ModernButton();
        tableContainer = new component.RoundedPanel();
        scrollTable = new javax.swing.JScrollPane();
        tablePelanggan = new component.ModernTable();

        setBackground(new java.awt.Color(10, 10, 11));

        txtSearch.setPreferredSize(new java.awt.Dimension(250, 40));
        txtSearch.addActionListener(this::txtSearchActionPerformed);

        cbTier.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        cbTier.setForeground(new java.awt.Color(255, 255, 255));
        cbTier.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semua Tier", "Bronze", "Silver", "Gold" }));
        cbTier.setPreferredSize(new java.awt.Dimension(140, 40));
        cbTier.addActionListener(this::cbTierActionPerformed);

        btnTambah.setText("+ Tambah Pelanggan");
        btnTambah.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnTambahMouseClicked(evt);
            }
        });
        btnTambah.addActionListener(this::btnTambahActionPerformed);

        tablePelanggan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Pelanggan", "No. HP", "Total Kunjungan", "Poin", "Tier", "Kunjungan Terakhir", "Aksi"
            }
        ));
        scrollTable.setViewportView(tablePelanggan);

        javax.swing.GroupLayout tableContainerLayout = new javax.swing.GroupLayout(tableContainer);
        tableContainer.setLayout(tableContainerLayout);
        tableContainerLayout.setHorizontalGroup(
            tableContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollTable)
        );
        tableContainerLayout.setVerticalGroup(
            tableContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollTable, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
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
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cbTier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(99, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbTier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(tableContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        refreshTable();
    }//GEN-LAST:event_txtSearchActionPerformed

    private void cbTierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTierActionPerformed
        refreshTable();

    }//GEN-LAST:event_cbTierActionPerformed

    private void btnTambahMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTambahMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahMouseClicked

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        // TODO add your handling code here:
        TambahPelangganDialog dialog =
        new TambahPelangganDialog(
                null,
                true
        );
        
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        
        // setelah dialog ditutup, refresh tabel otomatis
        refreshTable();
    }//GEN-LAST:event_btnTambahActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.ModernButton btnTambah;
    private javax.swing.JComboBox<String> cbTier;
    private javax.swing.JScrollPane scrollTable;
    private component.RoundedPanel tableContainer;
    private component.ModernTable tablePelanggan;
    private component.ModernTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
