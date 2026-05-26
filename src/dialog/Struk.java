/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package dialog;

import form.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author asus_
 */
    public class Struk extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(Struk.class.getName());

    private List<model.CartItemModel> cartData;
    private int idMember;
    private int totalBayar;
    private int diskonDB;
    private int subtotalDB;
    private String metodeDb;
    private int poinDidapat;
    private String namaKapster;
    private Kasir formKasirAsal;
    private String noNota;

    public Struk(List<model.CartItemModel> cart, int idMember, String namaPelanggan,
                 String kapster, int subtotal, int diskon, int total,
                 String metode, int poin, Kasir kasirAsal, String tanggalTransaksi) {

        initComponents();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        this.cartData      = cart;
        this.idMember      = idMember;
        this.totalBayar    = total;
        this.diskonDB      = diskon;
        this.subtotalDB    = subtotal;
        this.poinDidapat   = idMember == 0 ? 0 : poin;
        this.namaKapster   = kapster;
        this.formKasirAsal = kasirAsal;

        // Mapping metode ke ENUM DB
        switch (metode) {
            case "Tunai":    this.metodeDb = "cash";     break;
            case "QRIS":     this.metodeDb = "qris";     break;
            case "Debit":    this.metodeDb = "debit";    break;
            case "Transfer": this.metodeDb = "transfer"; break;
            default:         this.metodeDb = "cash";     break;
        }

        this.noNota = generateNoNota();

        populateDataStruk(namaPelanggan, kapster, subtotal, diskon, total, metode, tanggalTransaksi);
    }

    private void populateDataStruk(String pelanggan, String kapster,
            int subtotal, int diskon, int total, String metode,
            String tanggalTransaksi) {

        // Format tanggal dari DB — bukan new Date()
        try {
            Date tgl = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tanggalTransaksi);
            lblWaktuTransaksi.setText(
                new SimpleDateFormat("dd MMM yyyy, HH.mm").format(tgl)
            );
        } catch (Exception ex) {
            lblWaktuTransaksi.setText(tanggalTransaksi);
        }

        lblNomorNota.setText("No. " + this.noNota);
        lblNamaPelanggan.setText(pelanggan);
        lblNamaKapster.setText(kapster);

        lblHargaSubtotal.setText("Rp " + String.format("%,d", subtotal).replace(',', '.'));
        lblHargaSubtotal1.setText("- Rp " + String.format("%,d", diskon).replace(',', '.'));
        lblHargaSubtotal2.setText("Rp " + String.format("%,d", total).replace(',', '.'));
        lblMetodeBayar.setText(metode);

        // Tampilan poin
        if (idMember == 0) {
            lblPoinDidapat.setText("+0 poin (Bukan Member)");
            lblPoinDidapat.setForeground(new java.awt.Color(204, 204, 204));
        } else {
            lblPoinDidapat.setText("+" + poinDidapat + " poin");
            lblPoinDidapat.setForeground(new java.awt.Color(255, 204, 0));
        }

        // Render list item di struk
        PanelItem.setLayout(new javax.swing.BoxLayout(PanelItem, javax.swing.BoxLayout.Y_AXIS));
        PanelItem.removeAll();

        for (model.CartItemModel c : cartData) {
            javax.swing.JPanel baris = new javax.swing.JPanel(new java.awt.BorderLayout());
            baris.setBackground(java.awt.Color.WHITE);

            javax.swing.JPanel panelKiri = new javax.swing.JPanel();
            panelKiri.setLayout(new javax.swing.BoxLayout(panelKiri, javax.swing.BoxLayout.Y_AXIS));
            panelKiri.setBackground(java.awt.Color.WHITE);

            // Nama item
            javax.swing.JLabel lNama = new javax.swing.JLabel(c.produk.nama);
            lNama.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));

            // Harga satuan x qty
            String formatSatuan = String.format("%,d", c.produk.harga).replace(',', '.');
            javax.swing.JLabel lDetail = new javax.swing.JLabel(
                "Rp " + formatSatuan + "  x  " + c.qty
            );
            lDetail.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
            lDetail.setForeground(new java.awt.Color(102, 102, 102));

            panelKiri.add(lNama);
            panelKiri.add(lDetail);

            // Total harga per item
            String formatTotalItem = String.format("%,d", c.produk.harga * c.qty).replace(',', '.');
            javax.swing.JLabel lHargaTotal = new javax.swing.JLabel("Rp " + formatTotalItem);
            lHargaTotal.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));

            baris.add(panelKiri, java.awt.BorderLayout.CENTER);
            baris.add(lHargaTotal, java.awt.BorderLayout.EAST);

            PanelItem.add(baris);
            PanelItem.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));
        }

        PanelItem.revalidate();
        PanelItem.repaint();

        // Pack frame mengikuti isi struk
        this.pack();

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int maxHeight = screenSize.height - 100;
        if (this.getHeight() > maxHeight) {
            this.setSize(this.getWidth(), maxHeight);
        }

        this.setLocationRelativeTo(null);
        jScrollPane3.getVerticalScrollBar().setUnitIncrement(16);
    }

    private String generateNoNota() {
        String prefix = "INV-" + new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + "-";
        String noUrut = "001";

        try {
            java.sql.Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT COUNT(*) AS total FROM transaksi WHERE DATE(tanggal) = CURDATE()";
            java.sql.Statement st = conn.createStatement();
            java.sql.ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                int urutanBerikutnya = rs.getInt("total") + 1;
                noUrut = String.format("%03d", urutanBerikutnya);
            }
        } catch (Exception e) {
            e.printStackTrace();
            noUrut = String.format("%03d", (System.currentTimeMillis() % 1000));
        }

        return prefix + noUrut;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        lblBarberbyur = new javax.swing.JLabel();
        lblSubTitle = new javax.swing.JLabel();
        lblWaktuTransaksi = new javax.swing.JLabel();
        txtPelanggan = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        lblNomorNota = new javax.swing.JLabel();
        lblNamaPelanggan = new javax.swing.JLabel();
        txtKapster = new javax.swing.JLabel();
        lblNamaKapster = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        PanelItem = new javax.swing.JPanel();
        lblNamaItem = new javax.swing.JLabel();
        lblHargaItem = new javax.swing.JLabel();
        lblHargaSatuan = new javax.swing.JLabel();
        txtKali = new javax.swing.JLabel();
        lblQtyItem = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        txtSubtotal = new javax.swing.JLabel();
        lblHargaSubtotal = new javax.swing.JLabel();
        txtDiskon = new javax.swing.JLabel();
        lblHargaSubtotal1 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        txtTotal = new javax.swing.JLabel();
        lblHargaSubtotal2 = new javax.swing.JLabel();
        txtPembayaran = new javax.swing.JLabel();
        lblMetodeBayar = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        txtPoinDidapat = new javax.swing.JLabel();
        lblPoinDidapat = new javax.swing.JLabel();
        txtTerimakasih = new javax.swing.JLabel();
        txtPowered = new javax.swing.JLabel();

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jList2.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Proses Pembayaran");

        jPanel1.setBackground(new java.awt.Color(10, 10, 11));
        jPanel1.setPreferredSize(new java.awt.Dimension(435, 573));

        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setBorder(null);
        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        lblBarberbyur.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblBarberbyur.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBarberbyur.setText("BARBERBYUR");
        lblBarberbyur.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        lblSubTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSubTitle.setText("Professional Barbershop Suite");

        lblWaktuTransaksi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWaktuTransaksi.setText("22 Mei 2026, 14.18");

        txtPelanggan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtPelanggan.setText("Pelanggan");

        lblNomorNota.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNomorNota.setText("No. #0002");

        lblNamaPelanggan.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNamaPelanggan.setText("Nama Pelanggan");

        txtKapster.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtKapster.setText("Kapster");

        lblNamaKapster.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNamaKapster.setText("Nama Kapster");

        PanelItem.setBackground(new java.awt.Color(255, 255, 255));

        lblNamaItem.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblNamaItem.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNamaItem.setText("Nama Item");

        lblHargaItem.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblHargaItem.setText("Harga Item");

        lblHargaSatuan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblHargaSatuan.setText("Harga Satuan");

        txtKali.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtKali.setText("x");

        lblQtyItem.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblQtyItem.setText("qty");

        javax.swing.GroupLayout PanelItemLayout = new javax.swing.GroupLayout(PanelItem);
        PanelItem.setLayout(PanelItemLayout);
        PanelItemLayout.setHorizontalGroup(
            PanelItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelItemLayout.createSequentialGroup()
                .addComponent(lblNamaItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblHargaItem, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(PanelItemLayout.createSequentialGroup()
                .addComponent(lblHargaSatuan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtKali)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblQtyItem)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        PanelItemLayout.setVerticalGroup(
            PanelItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelItemLayout.createSequentialGroup()
                .addGroup(PanelItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNamaItem)
                    .addComponent(lblHargaItem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PanelItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHargaSatuan)
                    .addComponent(txtKali)
                    .addComponent(lblQtyItem)))
        );

        txtSubtotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtSubtotal.setText("Subtotal");

        lblHargaSubtotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblHargaSubtotal.setText("Harga Subtotal");

        txtDiskon.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtDiskon.setText("Diskon");

        lblHargaSubtotal1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblHargaSubtotal1.setText("Harga Diskon");

        txtTotal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtTotal.setText("Total");

        lblHargaSubtotal2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblHargaSubtotal2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblHargaSubtotal2.setText("Harga Total");

        txtPembayaran.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtPembayaran.setText("Pembayaran");

        lblMetodeBayar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMetodeBayar.setText("Metode Bayar");

        txtPoinDidapat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtPoinDidapat.setForeground(new java.awt.Color(255, 204, 0));
        txtPoinDidapat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtPoinDidapat.setText("Poin Didapat");

        lblPoinDidapat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblPoinDidapat.setForeground(new java.awt.Color(255, 204, 0));
        lblPoinDidapat.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPoinDidapat.setText("+200 poin");

        txtTerimakasih.setForeground(new java.awt.Color(102, 102, 102));
        txtTerimakasih.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtTerimakasih.setText("Terimakasih atas kunjungan Anda!");

        txtPowered.setForeground(new java.awt.Color(102, 102, 102));
        txtPowered.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtPowered.setText("Powered by Barberbyur");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txtKapster)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblNamaKapster, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtPelanggan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblNamaPelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(1, 1, 1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(txtPoinDidapat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblPoinDidapat))
                    .addComponent(jSeparator6)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtTotal)
                        .addGap(18, 18, 18)
                        .addComponent(lblHargaSubtotal2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jSeparator5)
                    .addComponent(jSeparator3)
                    .addComponent(PanelItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator4)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtPembayaran)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMetodeBayar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSubtotal)
                            .addComponent(txtDiskon))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblHargaSubtotal1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblHargaSubtotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(31, 31, 31))
            .addComponent(txtTerimakasih, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSubTitle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPowered, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblBarberbyur, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblWaktuTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblNomorNota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblBarberbyur)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblWaktuTransaksi)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNomorNota)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPelanggan)
                    .addComponent(lblNamaPelanggan))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtKapster)
                    .addComponent(lblNamaKapster))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHargaSubtotal)
                    .addComponent(txtSubtotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDiskon)
                    .addComponent(lblHargaSubtotal1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHargaSubtotal2)
                    .addComponent(txtTotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMetodeBayar)
                    .addComponent(txtPembayaran))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPoinDidapat)
                    .addComponent(txtPoinDidapat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTerimakasih)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPowered)
                .addContainerGap(165, Short.MAX_VALUE))
        );

        jScrollPane3.setViewportView(jPanel2);

        jPanel3.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelItem;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JLabel lblBarberbyur;
    private javax.swing.JLabel lblHargaItem;
    private javax.swing.JLabel lblHargaSatuan;
    private javax.swing.JLabel lblHargaSubtotal;
    private javax.swing.JLabel lblHargaSubtotal1;
    private javax.swing.JLabel lblHargaSubtotal2;
    private javax.swing.JLabel lblMetodeBayar;
    private javax.swing.JLabel lblNamaItem;
    private javax.swing.JLabel lblNamaKapster;
    private javax.swing.JLabel lblNamaPelanggan;
    private javax.swing.JLabel lblNomorNota;
    private javax.swing.JLabel lblPoinDidapat;
    private javax.swing.JLabel lblQtyItem;
    private javax.swing.JLabel lblSubTitle;
    private javax.swing.JLabel lblWaktuTransaksi;
    private javax.swing.JLabel txtDiskon;
    private javax.swing.JLabel txtKali;
    private javax.swing.JLabel txtKapster;
    private javax.swing.JLabel txtPelanggan;
    private javax.swing.JLabel txtPembayaran;
    private javax.swing.JLabel txtPoinDidapat;
    private javax.swing.JLabel txtPowered;
    private javax.swing.JLabel txtSubtotal;
    private javax.swing.JLabel txtTerimakasih;
    private javax.swing.JLabel txtTotal;
    // End of variables declaration//GEN-END:variables
}
