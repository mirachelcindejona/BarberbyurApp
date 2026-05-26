/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package form;

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
public class Proses_Pembayaran extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Proses_Pembayaran.class.getName());
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
    private int poinAwalDB;

    /**
     * Creates new form Proses_Pembayaran
     */
    public Proses_Pembayaran(List<model.CartItemModel> cart, int idMember, String namaPelanggan, 
                             String kapster, int subtotal, int diskon, int total, 
                             String metode, int poinAwal, int poin, Kasir kasirAsal) {
        initComponents();
        this.poinAwalDB = poinAwal;
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        this.cartData = cart;
        this.idMember = idMember;
        this.totalBayar = total;
        this.diskonDB = diskon;
        this.subtotalDB = subtotal;
        this.poinDidapat = idMember == 0 ? 0 : poin; // Poin hanya untuk member terdaftar
        this.namaKapster = kapster;
        this.formKasirAsal = kasirAsal;
        
        // Mapping Metode Pembayaran ke ENUM Database yang baru
        if (metode.equals("Tunai")) {
            this.metodeDb = "cash";
        } else if (metode.equals("QRIS")) {
            this.metodeDb = "qris";
        } else if (metode.equals("Debit")) {
            this.metodeDb = "debit";
        } else if (metode.equals("Transfer")) {
            this.metodeDb = "transfer";
        } else {
            this.metodeDb = "cash"; // Fallback/Default
        }
        
        this.noNota = generateNoNota(); // Generate nomor nota saat form dibuka

        populateDataStruk(namaPelanggan, kapster, subtotal, diskon, total, metode);
    }

    private void populateDataStruk(String pelanggan, String kapster, int subtotal, int diskon, int total, String metode) {
        lblWaktuTransaksi.setText(new SimpleDateFormat("dd MMM yyyy, HH.mm").format(new Date()));
        lblNomorNota.setText("No. " + this.noNota);
        lblNamaPelanggan.setText(pelanggan);
        lblNamaKapster.setText(kapster);
        
        lblHargaSubtotal.setText("Rp " + String.format("%,d", subtotal).replace(',', '.'));
        lblHargaSubtotal1.setText("- Rp " + String.format("%,d", diskon).replace(',', '.'));
        lblHargaSubtotal2.setText("Rp " + String.format("%,d", total).replace(',', '.'));
        lblMetodeBayar.setText(metode);
        
        if (idMember == 0) {
            lblPoinDidapat.setText("+0 poin (Bukan Member)");
            lblPoinDidapat.setForeground(new java.awt.Color(204, 204, 204));
        } else {
            lblPoinDidapat.setText("+" + poinDidapat + " poin");
        }

        // Loop Rendering List Item pada Struk
        PanelItem.setLayout(new javax.swing.BoxLayout(PanelItem, javax.swing.BoxLayout.Y_AXIS));
        PanelItem.removeAll();
        
        for (model.CartItemModel c : cartData) {
            // Container utama per item (kiri-kanan)
            javax.swing.JPanel baris = new javax.swing.JPanel(new java.awt.BorderLayout());
            baris.setBackground(java.awt.Color.WHITE);
            
            // Container kiri untuk Nama Item (atas) dan Detail Qty (bawah)
            javax.swing.JPanel panelKiri = new javax.swing.JPanel();
            panelKiri.setLayout(new javax.swing.BoxLayout(panelKiri, javax.swing.BoxLayout.Y_AXIS));
            panelKiri.setBackground(java.awt.Color.WHITE);
            
            // Label Nama Item (Dibuat agak tebal/Bold)
            javax.swing.JLabel lNama = new javax.swing.JLabel(c.produk.nama);
            lNama.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
            
            // Label Harga Satuan x Qty (Warna abu-abu/Muted)
            String formatSatuan = String.format("%,d", c.produk.harga).replace(',', '.');
            javax.swing.JLabel lDetail = new javax.swing.JLabel("Rp " + formatSatuan + "  x  " + c.qty);
            lDetail.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
            lDetail.setForeground(new java.awt.Color(102, 102, 102)); // Warna abu-abu
            
            // Masukkan nama dan detail ke panel kiri
            panelKiri.add(lNama);
            panelKiri.add(lDetail);
            
            // 3. Label Harga Total dari Item tersebut (Kanan)
            String formatTotalItem = String.format("%,d", c.produk.harga * c.qty).replace(',', '.');
            javax.swing.JLabel lHargaTotal = new javax.swing.JLabel("Rp " + formatTotalItem);
            lHargaTotal.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
            
            // Gabungkan panel kiri dan label harga total ke container utama
            baris.add(panelKiri, java.awt.BorderLayout.CENTER);
            baris.add(lHargaTotal, java.awt.BorderLayout.EAST);
            
            // Masukkan ke area Item di struk
            PanelItem.add(baris);
            
            // Gap antar item di dalam struk
            PanelItem.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10))); 
        }
        
        PanelItem.revalidate();
        PanelItem.repaint();
        
        // Kemas ulang (pack) frame agar tingginya memanjang pas mengikuti isi struk saat ini
        this.pack();

        // Ambil ukuran resolusi layar monitor perangkat yang sedang digunakan
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        
        // Tentukan batas tinggi maksimal frame (dikurangi 100px agar tidak tertutup Taskbar Windows)
        int maxHeight = screenSize.height - 100;
        
        // Jika setelah di-pack ternyata tinggi frame melebihi tinggi maksimal layar:
        if (this.getHeight() > maxHeight) {
            // Batasi tinggi frame ke maxHeight. 
            // Karena dibatasi, JScrollPane otomatis akan memunculkan Scrollbar.
            this.setSize(this.getWidth(), maxHeight);
        }
        
        // posisikan kembali frame persis di tengah layar
        this.setLocationRelativeTo(null);
        
        // (Opsional tapi penting) Mempercepat laju scroll pada mouse, karena default Swing sangat lambat
        jScrollPane3.getVerticalScrollBar().setUnitIncrement(16);
    }
    
    private String generateNoNota() {
        String prefix = "INV-" + new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + "-";
        String noUrut = "001"; // Default jika belum ada transaksi hari ini
        
        try {
            java.sql.Connection conn = Koneksi.getKoneksi(); // Sesuaikan dengan class koneksimu
            // Hitung jumlah transaksi pada hari ini
            String sql = "SELECT COUNT(*) AS total FROM transaksi WHERE DATE(tanggal) = CURDATE()";
            java.sql.Statement st = conn.createStatement();
            java.sql.ResultSet rs = st.executeQuery(sql);
            
            if (rs.next()) {
                int urutanBerikutnya = rs.getInt("total") + 1;
                // Format angka menjadi 3 digit (contoh: 1 menjadi 001, 15 menjadi 015)
                noUrut = String.format("%03d", urutanBerikutnya); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback (cadangan) jika database error, gunakan 3 digit acak dari waktu
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
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnKonfirmasiPembayaran = new javax.swing.JButton();
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

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Proses Pembayaran");

        btnKonfirmasiPembayaran.setBackground(new java.awt.Color(201, 168, 76));
        btnKonfirmasiPembayaran.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnKonfirmasiPembayaran.setText("Konfirmasi Pembayaran");
        btnKonfirmasiPembayaran.addActionListener(this::btnKonfirmasiPembayaranActionPerformed);

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
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jScrollPane3.setViewportView(jPanel2);

        jPanel3.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnKonfirmasiPembayaran))
                .addGap(22, 22, 22))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnKonfirmasiPembayaran, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
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

    private void btnKonfirmasiPembayaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKonfirmasiPembayaranActionPerformed
        // TODO add your handling code here:
        try {
            Connection conn = Koneksi.getKoneksi();
            conn.setAutoCommit(false);
            
            // Ambil ID & Komisi Kapster
            int idKapsterDB = 0;
            int persenKomisi = 0;
            if (!namaKapster.equals("Tanpa Kapster")) {
                
                PreparedStatement psK = conn.prepareStatement(
                    "SELECT id, komisi FROM kapster WHERE nama = ?"
                );
                psK.setString(1, namaKapster);
                ResultSet rsK = psK.executeQuery();

                if (rsK.next()) {
                    idKapsterDB = rsK.getInt("id");
                    persenKomisi = rsK.getInt("komisi");
                }

                rsK.close();
                psK.close();
            }
            
            // Hitung komisi kapster (Misal: Persentase komisi dari Total Bayar)
            int nominalKomisi = (totalBayar * persenKomisi) / 100;

            // Insert ke Tabel Transaksi
            String sqlTrans = "INSERT INTO transaksi (nomor_nota, id_pelanggan, id_kapster, subtotal, diskon, total, komisi, metode_pembayaran, tanggal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
            PreparedStatement psTrans = conn.prepareStatement(sqlTrans, Statement.RETURN_GENERATED_KEYS);
            psTrans.setString(1, this.noNota);
            
            if (idMember == 0) psTrans.setNull(2, java.sql.Types.INTEGER);
            else psTrans.setInt(2, idMember);
            
            if (idKapsterDB == 0) psTrans.setNull(3, java.sql.Types.INTEGER);
            else psTrans.setInt(3, idKapsterDB);
            
            psTrans.setInt(4, subtotalDB);
            psTrans.setInt(5, diskonDB);
            psTrans.setInt(6, totalBayar);
            psTrans.setInt(7, nominalKomisi);
            psTrans.setString(8, metodeDb);
            
            psTrans.executeUpdate();
            
            // Ambil id_transaksi yang baru saja terbuat
            ResultSet rsTrans = psTrans.getGeneratedKeys();
            int idTransaksiBaru = 0;
            if (rsTrans.next()) idTransaksiBaru = rsTrans.getInt(1);

            // Insert ke Tabel Detail Transaksi & Kurangi Durasi/Stok Produk
            String sqlDetail = "INSERT INTO detail_transaksi (id_transaksi, id_item, jumlah, harga_saat_ini) VALUES (?, ?, ?, ?)";
            PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
            
            String sqlStok = "UPDATE item SET status = CASE WHEN (stok - ?) <= 0 THEN 'non-aktif' ELSE status END, stok = stok - ? WHERE id = ? AND tipe = 'product'";
            PreparedStatement psUpdateStok = conn.prepareStatement(sqlStok);

            for (model.CartItemModel c : cartData) {
                psDetail.setInt(1, idTransaksiBaru);
                psDetail.setInt(2, c.produk.id);
                psDetail.setInt(3, c.qty);
                psDetail.setInt(4, c.produk.harga);
                psDetail.addBatch();
                
                // Kurangi stok jika item adalah product
                if (c.produk.tipe.equalsIgnoreCase("product")) {
                    psUpdateStok.setInt(1, c.qty);       // Tanda tanya pertama (untuk cek case when)
                    psUpdateStok.setInt(2, c.qty);       // Tanda tanya kedua (untuk pengurangan stok)
                    psUpdateStok.setInt(3, c.produk.id); // Tanda tanya ketiga (untuk id)
                    psUpdateStok.addBatch();
                }
            }
            psDetail.executeBatch();
            psUpdateStok.executeBatch();

            // Update Poin Pelanggan (Jika Member)
            if (idMember != 0 && poinDidapat > 0) {
                int poinBaru = poinAwalDB + poinDidapat;
                String tierBaru = "Bronze";
                
                // Kalkulasi Tier Baru berdasarkan Total Poin Akhir
                if (poinBaru >= 1000) {
                    tierBaru = "Gold";
                } else if (poinBaru >= 400) {
                    tierBaru = "Silver";
                }
                
                PreparedStatement psPoin = conn.prepareStatement("UPDATE pelanggan SET point = ?, tier = ? WHERE id = ?");
                psPoin.setInt(1, poinBaru);
                psPoin.setString(2, tierBaru);
                psPoin.setInt(3, idMember);
                psPoin.executeUpdate();
            }

            conn.commit(); // Eksekusi semua secara permanen
            
            // Update pesan sukses agar menampilkan nomor yang benar
            JOptionPane.showMessageDialog(this, "Transaksi Berhasil!\nNomor Nota: " + this.noNota);
            formKasirAsal.kosongkanKeranjang();
            
            this.dispose();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan transaksi!\n" + e.getMessage());
        }
    }//GEN-LAST:event_btnKonfirmasiPembayaranActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelItem;
    private javax.swing.JButton btnKonfirmasiPembayaran;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
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
