/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package table;
import form.Koneksi;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
/**
 *
 * @author marwa
 */

public class ActionTransaksiEditor extends AbstractCellEditor implements TableCellEditor {

    @Override
    public Object getCellEditorValue() {
        return "";
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column
    ) {
        ActionTransaksiPanel panel = new ActionTransaksiPanel();
        panel.setBorder(javax.swing.BorderFactory.createMatteBorder(
            0, 0, 1, 0, component.ThemeColor.BORDER
        ));

        panel.getBtnStruk().addActionListener(e -> {
            fireEditingStopped();

            // Ambil ID transaksi dari kolom 0 (hidden)
            int idTransaksi = (int) table.getModel().getValueAt(row, 0);

            try {
                java.sql.Connection conn = Koneksi.getKoneksi();

                // Query utama
                String sql =
                    "SELECT t.id, t.tanggal, t.subtotal, t.diskon, t.total, " +
                    "t.metode_pembayaran, t.id_pelanggan, " +
                    "p.nama AS nama_pelanggan, p.point AS poin_pelanggan, " +
                    "k.nama AS nama_kapster " +
                    "FROM transaksi t " +
                    "LEFT JOIN pelanggan p ON t.id_pelanggan = p.id " +
                    "JOIN kapster k ON t.id_kapster = k.id " +
                    "WHERE t.id = ?";

                java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idTransaksi);
                java.sql.ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int idPelanggan      = rs.getInt("id_pelanggan");
                    boolean isMember     = !rs.wasNull() && idPelanggan != 0;
                    String namaPelanggan = isMember ? rs.getString("nama_pelanggan") : "Non-Member";
                    String namaKapster   = rs.getString("nama_kapster");
                    int subtotal         = rs.getInt("subtotal");
                    String metode        = rs.getString("metode_pembayaran");
                    String tanggalDB     = rs.getString("tanggal");
                    int poinPelanggan    = isMember ? rs.getInt("poin_pelanggan") : 0;

                    int diskon;
                    int total;

                    if (!isMember) {
                        // Non-member = tidak dapat diskon sama sekali
                        diskon = 0;
                        total  = subtotal;
                    } else {
                        // Member = dapat diskon berdasarkan poin terkini di tabel pelanggan
                        double pctDiskon;
                        if (poinPelanggan >= 1000) {
                            pctDiskon = 0.10; // 10%
                        } else if (poinPelanggan >= 400) {
                            pctDiskon = 0.06; // 6%
                        } else {
                            pctDiskon = 0.03; // 3%
                        }
                        diskon = (int) Math.round(subtotal * pctDiskon);
                        total  = subtotal - diskon;
                    }

                    // Poin didapat = total / 1000, hanya untuk member
                    int poinDidapat = isMember ? total / 1000 : 0;

                    // Ambil detail item dari detail_transaksi
                    java.util.List<model.CartItemModel> cartList = new java.util.ArrayList<>();

                    String sqlDetail =
                        "SELECT i.id, i.nama, dt.harga_saat_ini AS harga, dt.jumlah AS qty, " +
                        "i.tipe, i.deskripsi, i.durasi, i.stok " +
                        "FROM detail_transaksi dt " +
                        "JOIN item i ON dt.id_item = i.id " +
                        "WHERE dt.id_transaksi = ?";

                    java.sql.PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
                    psDetail.setInt(1, idTransaksi);
                    java.sql.ResultSet rsDetail = psDetail.executeQuery();

                    while (rsDetail.next()) {
                        // Buat model.Item sesuai constructor (id, nama, harga, tipe, deskripsi, durasi, stok)
                        model.Item itemProduk = new model.Item(
                            rsDetail.getInt("id"),
                            rsDetail.getString("nama"),
                            rsDetail.getInt("harga"),
                            rsDetail.getString("tipe"),
                            rsDetail.getString("deskripsi"),
                            rsDetail.getInt("durasi"),
                            rsDetail.getInt("stok")
                        );

                        // Buat CartItemModel sesuai constructor (produk, qty)
                        model.CartItemModel cartItem = new model.CartItemModel(
                            itemProduk,
                            rsDetail.getInt("qty")
                        );

                        cartList.add(cartItem);
                    }

                    rsDetail.close();
                    psDetail.close();
                    rs.close();
                    ps.close();

                    // Konversi metode pembayaran dari ENUM DB ke label tampilan
                    String metodeLabel;
                    switch (metode) {
                        case "cash":     metodeLabel = "Tunai";    break;
                        case "qris":     metodeLabel = "QRIS";     break;
                        case "debit":    metodeLabel = "Debit";    break;
                        case "transfer": metodeLabel = "Transfer"; break;
                        default:         metodeLabel = metode;     break;
                    }

                    // Buka Struk dengan data lengkap dari DB
                    dialog.Struk struk = new dialog.Struk(
                        cartList,
                        isMember ? idPelanggan : 0,
                        namaPelanggan,
                        namaKapster,
                        subtotal,
                        diskon,
                        total,
                        metodeLabel,
                        poinDidapat,
                        null,
                        tanggalDB
                    );
                    struk.setLocationRelativeTo(null);
                    struk.setVisible(true);
                    struk.setAlwaysOnTop(true);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                    "Gagal membuka struk: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        return panel;
    }
}