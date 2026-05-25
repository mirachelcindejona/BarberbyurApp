/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package form;

import component.RoundedPanel;
import component.RoundedProgressBar;
import component.ThemeColor;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import form.Koneksi;
import java.awt.FontMetrics;
import javax.swing.table.DefaultTableModel;
import main.Main;
/**
 *
 * @author rhmnsae
 */
public class Dashboard extends javax.swing.JPanel {

    /**
     * Creates new form Dashboard
     */
    
    private javax.swing.Timer autoRefresh;
    
    public Dashboard() {
        initComponents();
        
        setBorder(
                javax.swing.BorderFactory.createEmptyBorder(0, 0, 40, 40)
        );
        
        cardCapster.setBackground(ThemeColor.SURFACE);
        cardLayanan.setBackground(ThemeColor.SURFACE);
        cardTransaksi.setBackground(ThemeColor.SURFACE);
        panelListKapster.setBackground(ThemeColor.SURFACE);
        panelListLayanan.setBackground(ThemeColor.SURFACE);
        separatorTop.setBackground(ThemeColor.BORDER);
        separatorTop1.setBackground(ThemeColor.BORDER);
        separatorTop2.setBackground(ThemeColor.BORDER);
        scrollKapster.getViewport().setBackground(ThemeColor.SURFACE);
        scrollLayanan.getViewport().setBackground(ThemeColor.SURFACE);

        btnAll.setSurfaceVariant();

        panelListKapster.setLayout(
            new javax.swing.BoxLayout(panelListKapster, javax.swing.BoxLayout.Y_AXIS)
        );
        panelListLayanan.setLayout(
            new javax.swing.BoxLayout(panelListLayanan, javax.swing.BoxLayout.Y_AXIS)
        );

        panelListKapster.setBorder(null);
        panelListLayanan.setBorder(null);

        scrollKapster.getVerticalScrollBar().setUI(new component.ModernScrollBarUI());
        scrollKapster.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        scrollKapster.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollKapster.getHorizontalScrollBar().setUI(new component.ModernScrollBarUI());
        scrollKapster.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 6));

        scrollLayanan.getVerticalScrollBar().setUI(new component.ModernScrollBarUI());
        scrollLayanan.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        scrollLayanan.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollLayanan.getHorizontalScrollBar().setUI(new component.ModernScrollBarUI());
        scrollLayanan.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 6));

        scrollKapster.getViewport().setBackground(ThemeColor.SURFACE);
        scrollKapster.setBackground(ThemeColor.SURFACE);
        scrollLayanan.getViewport().setBackground(ThemeColor.SURFACE);
        scrollLayanan.setBackground(ThemeColor.SURFACE);
        scrollKapster.setBorder(null);
        scrollKapster.setViewportBorder(null);
        scrollLayanan.setBorder(null);
        scrollLayanan.setViewportBorder(null);

        panelListKapster.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 2));
        panelListLayanan.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 2));

        tableTransaksi.getTableHeader().setOpaque(false);
        tableTransaksi.getTableHeader().setBackground(ThemeColor.SURFACE_2);
        tableTransaksi.getTableHeader().setForeground(ThemeColor.TEXT_MUTED);
        tableTransaksi.getTableHeader().setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13));
        tableTransaksi.getTableHeader().setBorder(null);

        scrollTransaksi.getVerticalScrollBar().setUI(new component.ModernScrollBarUI());
        scrollTransaksi.getVerticalScrollBar().setPreferredSize(new java.awt.Dimension(10, 0));
        scrollTransaksi.getViewport().setBackground(ThemeColor.SURFACE);
        scrollTransaksi.setBackground(ThemeColor.SURFACE);
        scrollTransaksi.setBorder(null);

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

        loadDashboard();
        autoRefresh = new javax.swing.Timer(2000, e -> refreshDashboard());
        autoRefresh.start();
    
    }
    
    public void refreshDashboard() {
        loadDashboard();
    }
    
    private JPanel createKapsterItem(
        String nama,
        int value,
        int maxValue,
        String total,
        java.awt.Color progressColor
    ) {
        JPanel item = new JPanel(new BorderLayout(8, 0));
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel lbNama = new JLabel();
        lbNama.setForeground(ThemeColor.TEXT_MUTED);
        lbNama.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbNama.setPreferredSize(new Dimension(90, 24));
        lbNama.setMinimumSize(new Dimension(90, 24));
        lbNama.setMaximumSize(new Dimension(90, 24));
        lbNama.setToolTipText(nama);

        FontMetrics fm = lbNama.getFontMetrics(lbNama.getFont());
        String text = nama;
        if (fm.stringWidth(nama) > 100) {
            while (fm.stringWidth(text + "...") > 100 && text.length() > 0) {
                text = text.substring(0, text.length() - 1);
            }
            text = text + "...";
        }
        lbNama.setText(text);


        component.KapsterProgressBar progress = new component.KapsterProgressBar(
            value, maxValue, progressColor
        );
        progress.setPreferredSize(new Dimension(10, 10)); 
        progress.setMinimumSize(new Dimension(10, 10));     
        progress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));

        JLabel lbTotal = new JLabel(total);
        lbTotal.setForeground(ThemeColor.TEXT);
        lbTotal.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbTotal.setPreferredSize(new Dimension(80, 24));
        lbTotal.setMinimumSize(new Dimension(80, 24));
        lbTotal.setMaximumSize(new Dimension(80, 24));
        lbTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        item.add(lbNama, BorderLayout.WEST);
        item.add(progress, BorderLayout.CENTER);
        item.add(lbTotal, BorderLayout.EAST);
        item.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        
        item.setMaximumSize(new Dimension(540, 40));
        item.setPreferredSize(new Dimension(540, 40));
        item.setMinimumSize(new Dimension(100, 40)); 

        return item;
    }

    private void loadDashboard() {
        try {
            Connection conn = Koneksi.getKoneksi();

            // omzet bulan ini
            PreparedStatement ps1 = conn.prepareStatement(
                "SELECT COALESCE(SUM(total), 0) as total FROM transaksi " +
                "WHERE MONTH(tanggal) = MONTH(CURDATE()) AND YEAR(tanggal) = YEAR(CURDATE())"
            );
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                int omzet = rs1.getInt("total");
                labelOmzet.setText(String.format("Rp%,d", omzet).replace(",", "."));
            }
            rs1.close();
            ps1.close();

            // komisi bulan ini
            PreparedStatement ps2 = conn.prepareStatement(
                "SELECT COALESCE(SUM(komisi), 0) as total FROM transaksi " +
                "WHERE MONTH(tanggal) = MONTH(CURDATE()) AND YEAR(tanggal) = YEAR(CURDATE())"
            );
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                int komisi = rs2.getInt("total");
                labelKomisi.setText(String.format("Rp%,d", komisi).replace(",", "."));
            }
            rs2.close();
            ps2.close();

            // total pelanggan
            PreparedStatement ps3 = conn.prepareStatement(
                "SELECT COUNT(*) as total FROM pelanggan"
            );
            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) {
                labelPelanggan.setText(String.valueOf(rs3.getInt("total")));
            }
            rs3.close();
            ps3.close();

            // stok menipis
            PreparedStatement ps4 = conn.prepareStatement(
                "SELECT COUNT(*) as total " +
                "FROM item " +
                "WHERE tipe = 'product' " +
                "AND stok <= 2"
            );
            ResultSet rs4 = ps4.executeQuery();
            if (rs4.next()) {
                labelStok.setText(String.valueOf(rs4.getInt("total")));
            }
            rs4.close();
            ps4.close();

            loadTabelTransaksi(conn);
            loadKapsterTerbaik(conn);
            loadLayananTerlaris(conn);

        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(
                this, "Gagal load dashboard: " + e.getMessage(),
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadTabelTransaksi(Connection conn) throws SQLException {
        DefaultTableModel model = (DefaultTableModel) tableTransaksi.getModel();
        model.setRowCount(0);

        PreparedStatement ps = conn.prepareStatement(
            "SELECT t.tanggal, p.nama as pelanggan, k.nama as kapster, " +
            "t.total, t.metode_pembayaran " +
            "FROM transaksi t " +
            "LEFT JOIN pelanggan p ON t.id_pelanggan = p.id " +
            "LEFT JOIN kapster k ON t.id_kapster = k.id " +
            "ORDER BY t.tanggal DESC LIMIT 10"
        );
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getTimestamp("tanggal"),
                rs.getString("pelanggan") != null ? rs.getString("pelanggan") : "Umum",
                rs.getString("kapster"),
                String.format("Rp%,d", rs.getInt("total")).replace(",", "."),
                rs.getString("metode_pembayaran").toUpperCase()
            });
        }
        rs.close();
        ps.close();
    }

    private void loadKapsterTerbaik(Connection conn) throws SQLException {
        panelListKapster.removeAll();

        PreparedStatement ps = conn.prepareStatement(
            "SELECT k.nama, COUNT(t.id) as jumlah, " +
            "COALESCE(SUM(t.komisi), 0) as total_komisi " +
            "FROM kapster k " +
            "JOIN transaksi t ON k.id = t.id_kapster " +
            "GROUP BY k.id, k.nama " +
            "ORDER BY jumlah DESC LIMIT 10"
        );
        ResultSet rs = ps.executeQuery();

        int maxJumlah = 1;
        java.util.List<Object[]> rows = new java.util.ArrayList<>();

        while (rs.next()) {
            int jumlah = rs.getInt("jumlah");
            if (jumlah > maxJumlah) maxJumlah = jumlah;
            rows.add(new Object[]{
                rs.getString("nama"),
                jumlah,
                rs.getLong("total_komisi")
            });
        }
        rs.close();
        ps.close();

        for (Object[] row : rows) {
            String nama = (String) row[0];
            int jumlah = (int) row[1];
            long totalKomisi = (long) row[2];

            // pass jumlah dan maxJumlah langsung ke createKapsterItem
            panelListKapster.add(createKapsterItem(
                nama,
                jumlah,        // value aktual
                maxJumlah,     // max untuk kalkulasi bar
                String.format("Rp%,d", totalKomisi).replace(",", "."),
                ThemeColor.GOLD
            ));
        }

        panelListKapster.revalidate();
        panelListKapster.repaint();
    }

    private void loadLayananTerlaris(Connection conn) throws SQLException {
        panelListLayanan.removeAll();

        PreparedStatement ps = conn.prepareStatement(
            "SELECT i.nama, SUM(dt.jumlah) as total_terjual " +
            "FROM item i " +
            "JOIN detail_transaksi dt ON i.id = dt.id_item " +
            "GROUP BY i.id " +
            "ORDER BY total_terjual DESC LIMIT 10"
        );
        ResultSet rs = ps.executeQuery();

        int maxTerjual = 1;
        java.util.List<Object[]> rows = new java.util.ArrayList<>();

        while (rs.next()) {
            int terjual = rs.getInt("total_terjual");
            if (terjual > maxTerjual) maxTerjual = terjual;
            rows.add(new Object[]{
                rs.getString("nama"),
                terjual
            });
        }
        rs.close();
        ps.close();

        for (Object[] row : rows) {
            String nama = (String) row[0];
            int terjual = (int) row[1];

            // pass terjual dan maxTerjual langsung ke createKapsterItem
            panelListLayanan.add(createKapsterItem(
                nama,
                terjual,       // value aktual
                maxTerjual,    // max untuk kalkulasi bar
                terjual + "x",
                new java.awt.Color(85, 153, 221)
            ));
        }

        panelListLayanan.revalidate();
        panelListLayanan.repaint();
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cardOmzet = new RoundedPanel();
        totalPoin = new javax.swing.JLabel();
        labelOmzet = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        cardKunjungan = new RoundedPanel();
        komisiKapster = new javax.swing.JLabel();
        labelKomisi = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        cardBelanja = new RoundedPanel();
        itemStok = new javax.swing.JLabel();
        labelStok = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        cardStok = new RoundedPanel();
        pelangganAktif = new javax.swing.JLabel();
        labelPelanggan = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        cardCapster = new RoundedPanel();
        labelJudul = new javax.swing.JLabel();
        separatorTop = new javax.swing.JSeparator();
        scrollKapster = new javax.swing.JScrollPane();
        panelListKapster = new javax.swing.JPanel();
        cardLayanan = new RoundedPanel();
        labelJudul1 = new javax.swing.JLabel();
        separatorTop1 = new javax.swing.JSeparator();
        scrollLayanan = new javax.swing.JScrollPane();
        panelListLayanan = new javax.swing.JPanel();
        cardTransaksi = new component.RoundedPanel();
        labelJudul2 = new javax.swing.JLabel();
        btnAll = new component.ModernButton();
        separatorTop2 = new javax.swing.JSeparator();
        scrollTransaksi = new javax.swing.JScrollPane();
        tableTransaksi = new component.ModernTable();

        setBackground(new java.awt.Color(10, 10, 11));

        cardOmzet.setBackground(new java.awt.Color(22, 22, 26));

        totalPoin.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        totalPoin.setForeground(new java.awt.Color(136, 136, 152));
        totalPoin.setText("Omzet Bulan Ini ");

        labelOmzet.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        labelOmzet.setForeground(new java.awt.Color(201, 168, 76));
        labelOmzet.setText("jLabel1");

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

        javax.swing.GroupLayout cardOmzetLayout = new javax.swing.GroupLayout(cardOmzet);
        cardOmzet.setLayout(cardOmzetLayout);
        cardOmzetLayout.setHorizontalGroup(
            cardOmzetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardOmzetLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(cardOmzetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelOmzet, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalPoin, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(128, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        cardOmzetLayout.setVerticalGroup(
            cardOmzetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardOmzetLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(totalPoin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelOmzet)
                .addGap(27, 27, 27))
        );

        cardKunjungan.setBackground(new java.awt.Color(22, 22, 26));

        komisiKapster.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        komisiKapster.setForeground(new java.awt.Color(136, 136, 152));
        komisiKapster.setText("Komisi Kapster");

        labelKomisi.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        labelKomisi.setForeground(new java.awt.Color(255, 255, 255));
        labelKomisi.setText("jLabel1");

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

        javax.swing.GroupLayout cardKunjunganLayout = new javax.swing.GroupLayout(cardKunjungan);
        cardKunjungan.setLayout(cardKunjunganLayout);
        cardKunjunganLayout.setHorizontalGroup(
            cardKunjunganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardKunjunganLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(cardKunjunganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelKomisi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(komisiKapster, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(128, Short.MAX_VALUE))
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        cardKunjunganLayout.setVerticalGroup(
            cardKunjunganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardKunjunganLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(komisiKapster)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelKomisi)
                .addGap(27, 27, 27))
        );

        cardBelanja.setBackground(new java.awt.Color(22, 22, 26));

        itemStok.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        itemStok.setForeground(new java.awt.Color(136, 136, 152));
        itemStok.setText("Item Stok Menipis");

        labelStok.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        labelStok.setForeground(new java.awt.Color(255, 255, 255));
        labelStok.setText("jLabel1");

        jPanel4.setBackground(new java.awt.Color(224, 85, 85));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout cardBelanjaLayout = new javax.swing.GroupLayout(cardBelanja);
        cardBelanja.setLayout(cardBelanjaLayout);
        cardBelanjaLayout.setHorizontalGroup(
            cardBelanjaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardBelanjaLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(cardBelanjaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelStok, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(itemStok))
                .addContainerGap(119, Short.MAX_VALUE))
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        cardBelanjaLayout.setVerticalGroup(
            cardBelanjaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardBelanjaLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(itemStok)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelStok)
                .addGap(27, 27, 27))
        );

        cardStok.setBackground(new java.awt.Color(22, 22, 26));

        pelangganAktif.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        pelangganAktif.setForeground(new java.awt.Color(136, 136, 152));
        pelangganAktif.setText("Pelanggan Aktif");

        labelPelanggan.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        labelPelanggan.setForeground(new java.awt.Color(255, 255, 255));
        labelPelanggan.setText("jLabel1");

        jPanel5.setBackground(new java.awt.Color(85, 153, 221));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout cardStokLayout = new javax.swing.GroupLayout(cardStok);
        cardStok.setLayout(cardStokLayout);
        cardStokLayout.setHorizontalGroup(
            cardStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardStokLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(cardStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelPelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pelangganAktif, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(119, Short.MAX_VALUE))
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        cardStokLayout.setVerticalGroup(
            cardStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardStokLayout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(pelangganAktif)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelPelanggan)
                .addGap(27, 27, 27))
        );

        labelJudul.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        labelJudul.setForeground(new java.awt.Color(255, 255, 255));
        labelJudul.setText("Kapster Terbaik ");

        panelListKapster.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelListKapsterLayout = new javax.swing.GroupLayout(panelListKapster);
        panelListKapster.setLayout(panelListKapsterLayout);
        panelListKapsterLayout.setHorizontalGroup(
            panelListKapsterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 548, Short.MAX_VALUE)
        );
        panelListKapsterLayout.setVerticalGroup(
            panelListKapsterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 182, Short.MAX_VALUE)
        );

        scrollKapster.setViewportView(panelListKapster);

        javax.swing.GroupLayout cardCapsterLayout = new javax.swing.GroupLayout(cardCapster);
        cardCapster.setLayout(cardCapsterLayout);
        cardCapsterLayout.setHorizontalGroup(
            cardCapsterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(separatorTop)
            .addGroup(cardCapsterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cardCapsterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollKapster, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(cardCapsterLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(labelJudul, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        cardCapsterLayout.setVerticalGroup(
            cardCapsterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardCapsterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelJudul)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separatorTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollKapster, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        labelJudul1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        labelJudul1.setForeground(new java.awt.Color(255, 255, 255));
        labelJudul1.setText("Layanan Terlaris ");

        panelListLayanan.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelListLayananLayout = new javax.swing.GroupLayout(panelListLayanan);
        panelListLayanan.setLayout(panelListLayananLayout);
        panelListLayananLayout.setHorizontalGroup(
            panelListLayananLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1256, Short.MAX_VALUE)
        );
        panelListLayananLayout.setVerticalGroup(
            panelListLayananLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 375, Short.MAX_VALUE)
        );

        scrollLayanan.setViewportView(panelListLayanan);

        javax.swing.GroupLayout cardLayananLayout = new javax.swing.GroupLayout(cardLayanan);
        cardLayanan.setLayout(cardLayananLayout);
        cardLayananLayout.setHorizontalGroup(
            cardLayananLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(separatorTop1)
            .addGroup(cardLayananLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollLayanan, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(cardLayananLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(labelJudul1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        cardLayananLayout.setVerticalGroup(
            cardLayananLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardLayananLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelJudul1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separatorTop1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollLayanan, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        labelJudul2.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        labelJudul2.setForeground(new java.awt.Color(255, 255, 255));
        labelJudul2.setText("Transaksi Terbaru ");

        btnAll.setText("Lihat semua →");
        btnAll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAllMouseClicked(evt);
            }
        });
        btnAll.addActionListener(this::btnAllActionPerformed);

        tableTransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Waktu", "Pelanggan", "Kapster", "Total", "Bayar"
            }
        ));
        scrollTransaksi.setViewportView(tableTransaksi);

        javax.swing.GroupLayout cardTransaksiLayout = new javax.swing.GroupLayout(cardTransaksi);
        cardTransaksi.setLayout(cardTransaksiLayout);
        cardTransaksiLayout.setHorizontalGroup(
            cardTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardTransaksiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cardTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cardTransaksiLayout.createSequentialGroup()
                        .addComponent(labelJudul2, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardTransaksiLayout.createSequentialGroup()
                        .addComponent(scrollTransaksi)
                        .addContainerGap())))
            .addComponent(separatorTop2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        cardTransaksiLayout.setVerticalGroup(
            cardTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardTransaksiLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(cardTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelJudul2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(separatorTop2, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cardTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(cardOmzet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(40, 40, 40)
                                .addComponent(cardKunjungan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(cardCapster, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(40, 40, 40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cardStok, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(40, 40, 40)
                                .addComponent(cardBelanja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(cardLayanan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cardBelanja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cardKunjungan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cardOmzet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cardStok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cardCapster, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cardLayanan, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(cardTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAllMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAllMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAllMouseClicked

    private void btnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllActionPerformed
        java.awt.Container parent = this.getParent();
        while (parent != null && !(parent instanceof Main)) {
            parent = parent.getParent();
        }
        if (parent instanceof Main) {
            ((Main) parent).navigateTo(new form.Riwayat_Transaksi());
        }
    }//GEN-LAST:event_btnAllActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.ModernButton btnAll;
    private javax.swing.JPanel cardBelanja;
    private javax.swing.JPanel cardCapster;
    private javax.swing.JPanel cardKunjungan;
    private javax.swing.JPanel cardLayanan;
    private javax.swing.JPanel cardOmzet;
    private javax.swing.JPanel cardStok;
    private component.RoundedPanel cardTransaksi;
    private javax.swing.JLabel itemStok;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel komisiKapster;
    private javax.swing.JLabel labelJudul;
    private javax.swing.JLabel labelJudul1;
    private javax.swing.JLabel labelJudul2;
    private javax.swing.JLabel labelKomisi;
    private javax.swing.JLabel labelOmzet;
    private javax.swing.JLabel labelPelanggan;
    private javax.swing.JLabel labelStok;
    private javax.swing.JPanel panelListKapster;
    private javax.swing.JPanel panelListLayanan;
    private javax.swing.JLabel pelangganAktif;
    private javax.swing.JScrollPane scrollKapster;
    private javax.swing.JScrollPane scrollLayanan;
    private javax.swing.JScrollPane scrollTransaksi;
    private javax.swing.JSeparator separatorTop;
    private javax.swing.JSeparator separatorTop1;
    private javax.swing.JSeparator separatorTop2;
    private component.ModernTable tableTransaksi;
    private javax.swing.JLabel totalPoin;
    // End of variables declaration//GEN-END:variables
}
