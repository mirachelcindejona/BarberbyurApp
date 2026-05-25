/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package form;

import component.ThemeColor;
import javax.swing.JTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author rhmnsae
 */
public class Riwayat_Transaksi extends javax.swing.JPanel {

    /**
     * Creates new form Riwayat_Transaksi
     */
    public Riwayat_Transaksi() {

            initComponents();
            setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 40, 40));

            tableContainer.setBackground(ThemeColor.SURFACE);
            tableContainer.setRadius(20);

            txtSearch.setPreferredSize(
                new java.awt.Dimension(250, 40)
            );

            txtSearch.setHint("Cari transaksi...");

            // realtime search
            txtSearch.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
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
                }
            );

            scrollTransaksi.setBorder(
                javax.swing.BorderFactory.createEmptyBorder()
            );

            tableContainer.setBorder(
                javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)
            );

            // custom header
            tableTransaksi.getTableHeader().setOpaque(false);
            tableTransaksi.getTableHeader().setBackground(ThemeColor.SURFACE_2);
            tableTransaksi.getTableHeader().setForeground(ThemeColor.TEXT_MUTED);
            tableTransaksi.getTableHeader().setFont(
                new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13)
            );
            tableTransaksi.getTableHeader().setBorder(null);

            // scrollbar
            scrollTransaksi.getVerticalScrollBar().setUI(
                new component.ModernScrollBarUI()
            );
            scrollTransaksi.getVerticalScrollBar().setPreferredSize(
                new java.awt.Dimension(10, 0)
            );
            scrollTransaksi.getViewport().setBackground(ThemeColor.SURFACE);
            scrollTransaksi.setBackground(ThemeColor.SURFACE);
            scrollTransaksi.setBorder(null);

            // table
            tableTransaksi.setShowGrid(false);
            tableTransaksi.getTableHeader().setReorderingAllowed(false);
            tableTransaksi.getTableHeader().setResizingAllowed(false);
            tableTransaksi.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            
            tableTransaksi.setRowHeight(56);
            tableTransaksi.setIntercellSpacing(new java.awt.Dimension(0, 0));
            tableTransaksi.setSelectionBackground(ThemeColor.SURFACE);
            tableTransaksi.setSelectionForeground(ThemeColor.TEXT);
            tableTransaksi.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13));
            tableTransaksi.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 40));

            tableTransaksi.setModel(
                new javax.swing.table.DefaultTableModel(
                    new Object[][]{},
                    new String[]{
                        "ID", "Waktu", "Pelanggan", "Kapster",
                        "Item", "Total", "Bayar", "Aksi"
                    }
                ) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return column == 7;
                    }
                }
            );

            // width kolom
            int[] widths = { 0, 152, 178, 178, 178, 150, 150, 150 };
            for (int i = 0; i < widths.length; i++) {
                tableTransaksi.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            }

            // hide kolom ID
            tableTransaksi.getColumnModel().getColumn(0).setMinWidth(0);
            tableTransaksi.getColumnModel().getColumn(0).setMaxWidth(0);
            tableTransaksi.getColumnModel().getColumn(0).setWidth(0);

            // renderer & editor aksi
            tableTransaksi.getColumnModel().getColumn(7).setCellRenderer(
                new table.ActionTransaksiRenderer()
            );
            tableTransaksi.getColumnModel().getColumn(7).setCellEditor(
                new table.ActionTransaksiEditor()
            );

            loadData("");
        }

        private void loadData(String keyword) {

            DefaultTableModel model =
                (DefaultTableModel) tableTransaksi.getModel();
            model.setRowCount(0);

            try {
                java.sql.Connection conn = Koneksi.getKoneksi();

                StringBuilder sql = new StringBuilder(
                    "SELECT " +
                    "t.id, " +
                    "t.tanggal, " +
                    // LEFT JOIN agar transaksi non-member tetap muncul
                    "COALESCE(p.nama, 'Non-Member') AS pelanggan, " +
                    "k.nama AS kapster, " +
                    "GROUP_CONCAT(i.nama SEPARATOR ', ') AS item, " +
                    "t.total, " +
                    "t.metode_pembayaran " +
                    "FROM transaksi t " +
                    // Pakai LEFT JOIN agar non-member tidak hilang
                    "LEFT JOIN pelanggan p ON t.id_pelanggan = p.id " +
                    "JOIN kapster k ON t.id_kapster = k.id " +
                    "JOIN detail_transaksi dt ON t.id = dt.id_transaksi " +
                    "JOIN item i ON dt.id_item = i.id " +
                    "WHERE 1=1"
                );

                if (keyword != null && !keyword.trim().isEmpty()) {
                    sql.append(
                        " AND (" +
                        "p.nama LIKE ? OR " +
                        "k.nama LIKE ? OR " +
                        "i.nama LIKE ?" +
                        ")"
                    );
                }

                sql.append(" GROUP BY t.id ORDER BY t.tanggal DESC");

                java.sql.PreparedStatement ps = conn.prepareStatement(sql.toString());

                int index = 1;
                if (keyword != null && !keyword.trim().isEmpty()) {
                    String cari = "%" + keyword.trim() + "%";
                    ps.setString(index++, cari);
                    ps.setString(index++, cari);
                    ps.setString(index++, cari);
                }

                java.sql.ResultSet rs = ps.executeQuery();

                int jumlahTransaksi = 0;
                int grandTotal      = 0;

                while (rs.next()) {
                    int total = rs.getInt("total");
                    jumlahTransaksi++;
                    grandTotal += total;

                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("tanggal"),
                        rs.getString("pelanggan"),
                        rs.getString("kapster"),
                        rs.getString("item"),
                        "Rp " + String.format("%,d", total).replace(',', '.'),
                        rs.getString("metode_pembayaran"),
                        ""
                    });
                }

                rs.close();
                ps.close();

                // Update label total — dinamis dari hasil query
                totalTransaksi.setText(
                    "Total: Rp " +
                    String.format("%,d", grandTotal).replace(',', '.') +
                    " (" + jumlahTransaksi + " transaksi)"
                );

            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Gagal load data: " + e.getMessage()
                );
            }
        }

        public void refreshTable() {
            loadData(txtSearch.getText().trim());
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
        totalTransaksi = new javax.swing.JLabel();
        tableContainer = new component.RoundedPanel();
        scrollTransaksi = new javax.swing.JScrollPane();
        tableTransaksi = new component.ModernTable();

        setBackground(new java.awt.Color(10, 10, 11));

        txtSearch.setPreferredSize(new java.awt.Dimension(250, 40));
        txtSearch.addActionListener(this::txtSearchActionPerformed);

        totalTransaksi.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        totalTransaksi.setForeground(new java.awt.Color(160, 160, 160));
        totalTransaksi.setText("Total: Rp 240.000 (2 transaksi) ");

        tableTransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Waktu", "Pelanggan", "Kapster", "Item", "Bayar", "Total", "Aksi"
            }
        ));
        scrollTransaksi.setViewportView(tableTransaksi);

        javax.swing.GroupLayout tableContainerLayout = new javax.swing.GroupLayout(tableContainer);
        tableContainer.setLayout(tableContainerLayout);
        tableContainerLayout.setHorizontalGroup(
            tableContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(tableContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, 1136, Short.MAX_VALUE))
        );
        tableContainerLayout.setVerticalGroup(
            tableContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 509, Short.MAX_VALUE)
            .addGroup(tableContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(tableContainerLayout.createSequentialGroup()
                    .addComponent(scrollTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                    .addGap(0, 0, 0)))
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
                        .addGap(27, 27, 27)
                        .addComponent(totalTransaksi)
                        .addGap(0, 652, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalTransaksi))
                .addGap(18, 18, 18)
                .addComponent(tableContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        
    }//GEN-LAST:event_txtSearchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollTransaksi;
    private component.RoundedPanel tableContainer;
    private component.ModernTable tableTransaksi;
    private javax.swing.JLabel totalTransaksi;
    private component.ModernTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
