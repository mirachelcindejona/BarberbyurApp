/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package form;

import model.Item;
import model.CartItemModel;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import swing.RoundedPanel;

/**
 *
 * @author rhmnsae
 */
public class Kasir extends javax.swing.JPanel {
    
    private List<CartItemModel> listKeranjang = new ArrayList<>();
    private int idMemberTerpilih = 0;
    private String namaMemberTerpilih = "";
    private int poinMemberTerpilih = 0;
    private String tierMemberTerpilih = "Bronze";
    
    private void terapkanGayaTombolBayar(javax.swing.JToggleButton btn) {
        java.awt.Color bgAktif = new java.awt.Color(201, 168, 76);
        java.awt.Color fgAktif = new java.awt.Color(0, 0, 0);
        java.awt.Color bgPasif = new java.awt.Color(34, 34, 40);
        java.awt.Color fgPasif = new java.awt.Color(136, 136, 152);

        // Fungsi update warna
        Runnable updateWarna = () -> {
            if (btn.isSelected()) {
                btn.setBackground(bgAktif); btn.setForeground(fgAktif);
            } else {
                btn.setBackground(bgPasif); btn.setForeground(fgPasif);
            }
        };



        updateWarna.run(); // Set warna awal
        btn.addItemListener(evt -> updateWarna.run()); // Ganti warna tiap diklik
    }
    
    public Kasir() {
        initComponents();
        
        panelMemberInfo.setVisible(false);
        jScrollPane1.getViewport().setBackground(new java.awt.Color(22, 22, 26));

        // Hapus border putih/abu-abu bawaan Scroll Pane agar lebih bersih
        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        
        jScrollPane2.getViewport().setBackground(new java.awt.Color(10, 10, 11));

        // Hapus border putih/abu-abu bawaan Scroll Pane agar lebih bersih
        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        
        terapkanGayaTombolBayar(btnTunai);
        terapkanGayaTombolBayar(btnQris);
        terapkanGayaTombolBayar(btnTransfer);
        terapkanGayaTombolBayar(btnDebit);

        // Biar Tunai otomatis terpilih saat awal dibuka
        btnTunai.setSelected(true);
        
        terapkanGayaTombolBayar(btnCatAll);
        terapkanGayaTombolBayar(btnCatService);
        terapkanGayaTombolBayar(btnCatProduct);
        
        btnCatAll.setSelected(true);    // Mengeset nilai awal Rp 0
        
        btnCatAll.setSelected(true);

        // SETTING KEDIP GARIS (CARET) EMAS DI SEARCH BAR
        txtSearch.setCaretColor(new java.awt.Color(201, 168, 76));

        // SETTING PLACEHOLDER MURNI UNTUK TXTSEARCH
        // Mengosongkan text awal bawaan NetBeans
        txtSearch.setText(""); 
        
        // Memanipulasi UI untuk menampilkan placeholder yang tidak bisa diblok/diedit
        txtSearch.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override
            protected void paintSafely(java.awt.Graphics g) {
                super.paintSafely(g);
                javax.swing.text.JTextComponent comp = getComponent();
                // Jika text kosong dan tidak sedang difokuskan, gambar tulisan placeholder
                if (comp.getText().isEmpty() && !comp.hasFocus()) {
                    java.awt.Graphics2D g2d = (java.awt.Graphics2D) g.create();
                    g2d.setColor(new java.awt.Color(136, 136, 152)); // Warna abu-abu placeholder
                    g2d.setFont(comp.getFont().deriveFont(java.awt.Font.ITALIC));
                    
                    // Mengatur posisi text placeholder agak ke tengah secara vertikal
                    int y = (comp.getHeight() + comp.getFontMetrics(comp.getFont()).getAscent()) / 2 - 2;
                    g2d.drawString("Cari layanan atau produk...", 10, y);
                    g2d.dispose();
                }
            }
        });
        
        // Agar placeholder hilang saat diklik/fokus dan muncul lagi saat fokus hilang
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) { txtSearch.repaint(); }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) { txtSearch.repaint(); }
        });

        // FUNGSI PENCARIAN REAL-TIME SAAT MENGETIK
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { jalankanFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { jalankanFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { jalankanFilter(); }
        });

        // FUNGSI KLIK TOMBOL KATEGORI
        btnCatAll.addActionListener(e -> jalankanFilter());
        btnCatService.addActionListener(e -> jalankanFilter());
        btnCatProduct.addActionListener(e -> jalankanFilter());

        loadKapsterToCombo();
        // Load data pertama kali saat form dibuka (Semua data, tanpa pencarian)
        loadItemKeKatalog("", "Semua");
        renderKeranjang();
        
        // Event klik tombol Pilih Member
        btnPilihMember.addActionListener(e -> {
            // Kita pass "this" agar pop up tahu siapa yang memanggilnya
            Pilih_Member popUp = new Pilih_Member(this); 
            popUp.setVisible(true);
        });
        
        btnProsesPembayaran.addActionListener(e -> {
            if (listKeranjang.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Keranjang masih kosong!");
                return;
            }
            
            // Mengambil Kapster Terpilih
            String kapsterCombo = PilihKapster.getSelectedItem().toString();
            String kapsterTerpilih = "Tanpa Kapster";
            
            if (!kapsterCombo.equals("-- Pilih Kapster --")) {
                // Memecah "Nama Kapster (20%)" untuk mengambil "Nama Kapster" saja
                kapsterTerpilih = kapsterCombo.split(" \\(")[0];
            }

            // Mengambil Metode Pembayaran (Cek ButtonGroup payGroup)
            String metode = "Tunai";
            if (btnQris.isSelected()) metode = "QRIS";
            else if (btnDebit.isSelected()) metode = "Debit";
            else if (btnTransfer.isSelected()) metode = "Transfer";

            // Mengambil nilai kalkulasi
            int subtotal = Integer.parseInt(lblSubtotal.getText().replaceAll("[^0-9]", ""));
            int diskon = Integer.parseInt(lblTotal.getText().replaceAll("[^0-9]", ""));
            int total = Integer.parseInt(TotalBayarKeranjang.getText().replaceAll("[^0-9]", ""));
            
            // Hitung poin didapat: Rp 1000 = 1 poin
            int poinDidapat = total / 1000;

            // Buka Jframe Proses Pembayaran dan passing datanya
            new Proses_Pembayaran(
                listKeranjang, idMemberTerpilih, 
                namaMemberTerpilih.isEmpty() ? "Umum" : namaMemberTerpilih, 
                kapsterTerpilih, subtotal, diskon, total, 
                metode, poinMemberTerpilih, poinDidapat, this
            ).setVisible(true);
        });
    }
    
    private void jalankanFilter() {
        String keyword = txtSearch.getText();
        String filterTipe = "Semua";

        // Cek toggle button mana yang sedang aktif
        if (btnCatService.isSelected()) {
            filterTipe = "service";
        } else if (btnCatProduct.isSelected()) {
            filterTipe = "product";
        }

        // Panggil method load data dengan keyword dan tipe yang sedang aktif
        loadItemKeKatalog(keyword, filterTipe);
    }
    
    // METHOD UNTUK LOAD DATABASE KE JSCROLLPANE KIRI
    private void loadItemKeKatalog(String keyword, String filterTipe) {
        panelGridItem.setLayout(new java.awt.GridLayout(0, 3, 10, 10)); 
        panelGridItem.removeAll(); 

        try {
            java.sql.Connection conn = Koneksi.getKoneksi();
            
            // Buat query dinamis
            String sql = "SELECT * FROM item WHERE nama LIKE ? AND status = 'aktif'";
            
            // Jika filter bukan "Semua", tambahkan kondisi tipe
            if (!filterTipe.equals("Semua")) {
                sql += " AND tipe = ?";
            }
            
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, "%" + keyword + "%"); // Pencarian nama
            
            if (!filterTipe.equals("Semua")) {
                pst.setString(2, filterTipe); // 'service' atau 'product'
            }
            
            java.sql.ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.Item p = new model.Item(
                    rs.getInt("id"), 
                    rs.getString("nama"), 
                    rs.getInt("harga"),
                    rs.getString("tipe"),
                    rs.getString("deskripsi"),
                    rs.getInt("durasi"),
                    rs.getInt("stok")
                );

                swing.CardItem card = new swing.CardItem();
                // Cek tipe: Jika service pakai angka durasi, jika product pakai angka stok
                String angkaTampil = p.tipe.equalsIgnoreCase("service") ? String.valueOf(p.durasi) : String.valueOf(p.stok);
                card.setData(p.nama, p.harga, p.tipe, p.deskripsi, angkaTampil); 
                // card.setOpaque(false); 

                card.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        tambahKeKeranjang(p);
                    }
                });

                panelGridItem.add(card);
            }
            
            panelGridItem.revalidate();
            panelGridItem.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // LOGIKA MENAMBAH ITEM KE KERANJANG
    private void tambahKeKeranjang(Item p) {
        // 1. Cek dari awal, kalau stok sudah 0 jangan diizinkan masuk
        if (p.tipe.equalsIgnoreCase("product") && p.stok <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Maaf, stok " + p.nama + " sudah habis!");
            return;
        }

        boolean sudahAda = false;
        for (CartItemModel cart : listKeranjang) {
            if (cart.produk.id == p.id) {
                // 2. Validasi Limit Stok (Maksimal qty = stok)
                if (cart.produk.tipe.equalsIgnoreCase("product")) {
                    if (cart.qty + 1 > cart.produk.stok) {
                        javax.swing.JOptionPane.showMessageDialog(this, "Maksimal stok untuk " + cart.produk.nama + " adalah " + cart.produk.stok);
                        return;
                    }
                }
                
                cart.qty += 1; 
                sudahAda = true;
                break;
            }
        }
        
        if (!sudahAda) {
            listKeranjang.add(new CartItemModel(p, 1));
        }
        renderKeranjang();
    }

    // RENDER KERANJANG KE JSCROLLPANE KANAN
    private void renderKeranjang() {
        // Atur layout panelKeranjangItem agar berjejer ke bawah
        panelKeranjangItem.setLayout(new javax.swing.BoxLayout(panelKeranjangItem, javax.swing.BoxLayout.Y_AXIS));
        panelKeranjangItem.removeAll();

        if (listKeranjang.isEmpty()) {
            javax.swing.JLabel labelKosong = new javax.swing.JLabel("Keranjang masih kosong");
            labelKosong.setForeground(java.awt.Color.WHITE);
            labelKosong.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            labelKosong.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            panelKeranjangItem.add(labelKosong);
        } else {
            for (int i = 0; i < listKeranjang.size(); i++) {
                // Ambil data dari list
                model.CartItemModel cartData = listKeranjang.get(i);
                final int index = i;
                
                // Panggil class CartItem milikmu
                swing.CartItem itemCart = new swing.CartItem();
                
                // Masukkan data ke fungsi setData yang baru dibuat
                itemCart.setData(cartData.produk.nama, cartData.produk.harga, cartData.qty);
                
                // Logika jika tombol (+) diklik
                itemCart.getBtnTambah().addActionListener(e -> {
                    // Validasi Limit Stok saat dipencet dari dalam keranjang
                    if (cartData.produk.tipe.equalsIgnoreCase("product")) {
                        if (cartData.qty + 1 > cartData.produk.stok) {
                            javax.swing.JOptionPane.showMessageDialog(this, "Maksimal stok untuk " + cartData.produk.nama + " adalah " + cartData.produk.stok);
                            return;
                        }
                    }
                    
                    cartData.qty++;
                    renderKeranjang();
                });
                
                // 4. Logika jika tombol (-) diklik
                itemCart.getBtnKurang().addActionListener(e -> {
                    if (cartData.qty > 1) {
                        cartData.qty--;
                    } else {
                        // Kalau qty = 1 lalu dikurangi, hapus dari keranjang
                        listKeranjang.remove(index);
                    }
                    renderKeranjang();
                });

                // Logika jika tombol Hapus diklik
                itemCart.getBtnHapus().addActionListener(e -> {
                    listKeranjang.remove(index);
                    renderKeranjang();
                });

                panelKeranjangItem.add(itemCart);
            }
        }

        panelKeranjangItem.revalidate();
        panelKeranjangItem.repaint();
        
        // Panggil fungsi update harga di Kasir (lblSubtotal, lblTotal, dll)
        updateTotalHarga();
    }
    
    public void kosongkanKeranjang() {
        // 1. Bersihkan list item di keranjang
        listKeranjang.clear();
        
        // 2. Reset Kapster ke default "-- Pilih Kapster --"
        PilihKapster.setSelectedIndex(0);
        
        // 3. Reset Pelanggan / Member ke posisi "Tanpa Member"
        setMemberTerpilih(0, "Pilih Member", 0, "Bronze");
        
        // 4. Render ulang tampilan keranjang (otomatis hitung ulang total harga jadi Rp 0)
        renderKeranjang();
        
        jalankanFilter();
    }
    
    // Method ini akan dipanggil dari pop up Pilih_Member
    public void setMemberTerpilih(int id, String nama, int poin, String tier) {
        this.idMemberTerpilih = id;
        this.namaMemberTerpilih = nama;
        this.poinMemberTerpilih = poin;
        this.tierMemberTerpilih = tier;
        
        btnPilihMember.setText(id == 0 ? "Pilih Member" : nama);
        btnPilihMember.setForeground(id == 0 ? java.awt.Color.WHITE : new java.awt.Color(201, 168, 76));
        
        if (id != 0) {
            // Isi data nama dan poin ke panel
            panelMemberInfo.setData(nama, poin, tier);
            // Munculkan panelnya
            panelMemberInfo.setVisible(true);
        } else {
            // Jika "Tanpa Member", sembunyikan kembali panelnya
            panelMemberInfo.setVisible(false);
        }
        
        renderKeranjang();
    }

    // KALKULASI TOTAL HARGA & QTY MENGGUNAKAN LABEL KAMU
    private void updateTotalHarga() {
        int subtotal = 0;
        int totalQty = 0;
        for (model.CartItemModel cart : listKeranjang) {
            subtotal += (cart.produk.harga * cart.qty);
            totalQty += cart.qty;
        }
        
        // Cek diskon berdasarkan poin
        int persenDiskon = 0;
        if (idMemberTerpilih != 0) {
            if (tierMemberTerpilih.equalsIgnoreCase("Gold")) persenDiskon = 10;
            else if (tierMemberTerpilih.equalsIgnoreCase("Silver")) persenDiskon = 6;
            else if (tierMemberTerpilih.equalsIgnoreCase("Bronze")) persenDiskon = 3;
        }

        int nominalDiskon = (subtotal * persenDiskon) / 100;
        int totalBayar = subtotal - nominalDiskon;

        lblSubtotal.setText("Rp " + String.format("%,d", subtotal).replace(',', '.'));
        lblTotal.setText("- Rp " + String.format("%,d", nominalDiskon).replace(',', '.'));
        TotalBayarKeranjang.setText("Rp " + String.format("%,d", totalBayar).replace(',', '.'));
        qtyTotalItemDikeranjang.setText(String.valueOf(totalQty));
    }
    
    // METHOD UNTUK MENGISI JCOMBOBOX KAPSTER DARI DATABASE
    private void loadKapsterToCombo() {
        // Bersihkan item bawaan NetBeans dahulu
        PilihKapster.removeAllItems();
        
        // Tambahkan pilihan default di baris pertama
        PilihKapster.addItem("-- Pilih Kapster --");
        
        try {
            java.sql.Connection conn = Koneksi.getKoneksi();
            java.sql.Statement st = conn.createStatement();
            
            // Mengambil nama kapster yang statusnya aktif berdasarkan database SQL kamu
            java.sql.ResultSet rs = st.executeQuery("SELECT nama, komisi_persen FROM kapster WHERE LOWER(status) = 'aktif'");
            
            while (rs.next()) {
                PilihKapster.addItem(rs.getString("nama") + " (" + rs.getInt("komisi_persen") + "%)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        payGroup = new javax.swing.ButtonGroup();
        kategoriGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        PilihKapster = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new RoundedPanel();
        qtyTotalItemDikeranjang = new javax.swing.JLabel();
        panelMemberInfo = new swing.MemberInCart();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblSubtotal = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        TotalBayarKeranjang = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        btnTunai = new javax.swing.JToggleButton();
        btnQris = new javax.swing.JToggleButton();
        btnDebit = new javax.swing.JToggleButton();
        btnTransfer = new javax.swing.JToggleButton();
        btnProsesPembayaran = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        panelKeranjangItem = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        btnPilihMember = new javax.swing.JButton();
        btnCatAll = new javax.swing.JToggleButton();
        btnCatService = new javax.swing.JToggleButton();
        btnCatProduct = new javax.swing.JToggleButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        panelGridItem = new javax.swing.JPanel();

        setBackground(new java.awt.Color(10, 10, 11));

        jPanel1.setBackground(new java.awt.Color(22, 22, 26));

        jPanel3.setBackground(new java.awt.Color(22, 22, 26));
        jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(255, 255, 255)));

        PilihKapster.setBackground(new java.awt.Color(26, 26, 30));
        PilihKapster.setForeground(new java.awt.Color(255, 255, 255));
        PilihKapster.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Pilih Kapster --", " " }));
        PilihKapster.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        PilihKapster.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        PilihKapster.addActionListener(this::PilihKapsterActionPerformed);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 237, 232));
        jLabel1.setText("Keranjang Transaksi");

        jPanel2.setBackground(new java.awt.Color(204, 204, 0));

        qtyTotalItemDikeranjang.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        qtyTotalItemDikeranjang.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        qtyTotalItemDikeranjang.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(qtyTotalItemDikeranjang, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(qtyTotalItemDikeranjang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelMemberInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PilihKapster, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(PilihKapster, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelMemberInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(22, 22, 26));
        jPanel4.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(255, 255, 255)));

        jPanel5.setBackground(new java.awt.Color(22, 22, 26));
        jPanel5.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(255, 255, 255)));

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Subtotal");

        lblSubtotal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSubtotal.setForeground(new java.awt.Color(255, 255, 255));
        lblSubtotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSubtotal.setText("Rp 0");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Diskon");

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(51, 204, 0));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setText("- Rp 0");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSubtotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lblSubtotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblTotal))
                .addGap(16, 16, 16))
        );

        TotalBayarKeranjang.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        TotalBayarKeranjang.setForeground(new java.awt.Color(204, 204, 0));
        TotalBayarKeranjang.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        TotalBayarKeranjang.setText("RP 0");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Total Bayar");

        jPanel6.setBackground(new java.awt.Color(22, 22, 26));
        jPanel6.setLayout(new java.awt.GridLayout(2, 2, 6, 6));

        btnTunai.setBackground(new java.awt.Color(22, 22, 26));
        payGroup.add(btnTunai);
        btnTunai.setForeground(new java.awt.Color(255, 255, 255));
        btnTunai.setText("Tunai");
        btnTunai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTunai.setFocusable(false);
        jPanel6.add(btnTunai);

        btnQris.setBackground(new java.awt.Color(22, 22, 26));
        payGroup.add(btnQris);
        btnQris.setForeground(new java.awt.Color(255, 255, 255));
        btnQris.setText("QRIS");
        btnQris.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQris.setFocusable(false);
        jPanel6.add(btnQris);

        btnDebit.setBackground(new java.awt.Color(22, 22, 26));
        payGroup.add(btnDebit);
        btnDebit.setForeground(new java.awt.Color(255, 255, 255));
        btnDebit.setText("Debit");
        btnDebit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDebit.setFocusable(false);
        jPanel6.add(btnDebit);

        btnTransfer.setBackground(new java.awt.Color(22, 22, 26));
        payGroup.add(btnTransfer);
        btnTransfer.setForeground(new java.awt.Color(255, 255, 255));
        btnTransfer.setText("Transfer");
        btnTransfer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTransfer.setFocusable(false);
        jPanel6.add(btnTransfer);

        btnProsesPembayaran.setBackground(new java.awt.Color(201, 168, 76));
        btnProsesPembayaran.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnProsesPembayaran.setText("Proses Pembayaran");
        btnProsesPembayaran.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TotalBayarKeranjang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnProsesPembayaran, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(TotalBayarKeranjang))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnProsesPembayaran, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel11.setBackground(new java.awt.Color(22, 22, 26));
        jPanel11.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBackground(new java.awt.Color(22, 22, 26));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panelKeranjangItem.setBackground(new java.awt.Color(22, 22, 26));
        panelKeranjangItem.setMaximumSize(new java.awt.Dimension(358, 32767));

        javax.swing.GroupLayout panelKeranjangItemLayout = new javax.swing.GroupLayout(panelKeranjangItem);
        panelKeranjangItem.setLayout(panelKeranjangItemLayout);
        panelKeranjangItemLayout.setHorizontalGroup(
            panelKeranjangItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 358, Short.MAX_VALUE)
        );
        panelKeranjangItemLayout.setVerticalGroup(
            panelKeranjangItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 363, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(panelKeranjangItem);

        jPanel11.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel7.setBackground(new java.awt.Color(10, 10, 11));
        jPanel7.setLayout(new java.awt.BorderLayout());

        jPanel10.setBackground(new java.awt.Color(10, 10, 11));

        txtSearch.setBackground(new java.awt.Color(22, 22, 26));
        txtSearch.setForeground(new java.awt.Color(136, 136, 152));
        txtSearch.setToolTipText("");

        btnPilihMember.setBackground(new java.awt.Color(22, 22, 26));
        btnPilihMember.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnPilihMember.setForeground(new java.awt.Color(255, 255, 255));
        btnPilihMember.setText("Pilih Member");
        btnPilihMember.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnCatAll.setBackground(new java.awt.Color(22, 22, 26));
        kategoriGroup.add(btnCatAll);
        btnCatAll.setForeground(new java.awt.Color(255, 255, 255));
        btnCatAll.setText("Semua");
        btnCatAll.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnCatService.setBackground(new java.awt.Color(22, 22, 26));
        kategoriGroup.add(btnCatService);
        btnCatService.setForeground(new java.awt.Color(255, 255, 255));
        btnCatService.setText("Layanan");
        btnCatService.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnCatProduct.setBackground(new java.awt.Color(22, 22, 26));
        kategoriGroup.add(btnCatProduct);
        btnCatProduct.setForeground(new java.awt.Color(255, 255, 255));
        btnCatProduct.setText("Produk");
        btnCatProduct.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(btnCatAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCatService)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCatProduct)
                        .addContainerGap())
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(txtSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPilihMember))))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPilihMember, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCatAll, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCatService, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCatProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel10, java.awt.BorderLayout.PAGE_START);

        jScrollPane2.setBorder(null);
        jScrollPane2.setToolTipText("");

        panelGridItem.setBackground(new java.awt.Color(10, 10, 11));

        javax.swing.GroupLayout panelGridItemLayout = new javax.swing.GroupLayout(panelGridItem);
        panelGridItem.setLayout(panelGridItemLayout);
        panelGridItemLayout.setHorizontalGroup(
            panelGridItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 871, Short.MAX_VALUE)
        );
        panelGridItemLayout.setVerticalGroup(
            panelGridItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 804, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(panelGridItem);

        jPanel7.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 865, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void PilihKapsterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PilihKapsterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PilihKapsterActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> PilihKapster;
    private javax.swing.JLabel TotalBayarKeranjang;
    private javax.swing.JToggleButton btnCatAll;
    private javax.swing.JToggleButton btnCatProduct;
    private javax.swing.JToggleButton btnCatService;
    private javax.swing.JToggleButton btnDebit;
    private javax.swing.JButton btnPilihMember;
    private javax.swing.JButton btnProsesPembayaran;
    private javax.swing.JToggleButton btnQris;
    private javax.swing.JToggleButton btnTransfer;
    private javax.swing.JToggleButton btnTunai;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.ButtonGroup kategoriGroup;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel panelGridItem;
    private javax.swing.JPanel panelKeranjangItem;
    private swing.MemberInCart panelMemberInfo;
    private javax.swing.ButtonGroup payGroup;
    private javax.swing.JLabel qtyTotalItemDikeranjang;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}



