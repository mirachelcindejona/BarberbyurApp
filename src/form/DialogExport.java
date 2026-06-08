package form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DialogExport extends JDialog {

    private JComboBox<String> cbFormat;
    private JComboBox<String> cbRentang;
    private JTextField txtMulai;
    private JTextField txtAkhir;
    private JButton btnProses;

    public DialogExport() {
        super((java.awt.Frame) null, "Export Riwayat Transaksi", true);
        setSize(450, 320);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); 

        // Setup UI Panel Tengah
        JPanel panelTengah = new JPanel(new GridLayout(5, 2, 10, 15));
        panelTengah.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelTengah.setBackground(new Color(22, 22, 26)); 

        JLabel lblFormat = new JLabel("Format Export:");
        lblFormat.setForeground(Color.WHITE);
        cbFormat = new JComboBox<>(new String[]{"Excel", "CSV"});
        
        JLabel lblRentang = new JLabel("Rentang Waktu:");
        lblRentang.setForeground(Color.WHITE);
        cbRentang = new JComboBox<>(new String[]{"Semua Waktu", "Hari Ini", "Bulan Ini", "Custom (YYYY-MM-DD)"});
        
        JLabel lblMulai = new JLabel("Tgl Mulai (YYYY-MM-DD):");
        lblMulai.setForeground(Color.WHITE);
        txtMulai = new JTextField();
        txtMulai.setEnabled(false);

        JLabel lblAkhir = new JLabel("Tgl Akhir (YYYY-MM-DD):");
        lblAkhir.setForeground(Color.WHITE);
        txtAkhir = new JTextField();
        txtAkhir.setEnabled(false);

        cbRentang.addActionListener(e -> {
            boolean isCustom = cbRentang.getSelectedItem().equals("Custom (YYYY-MM-DD)");
            txtMulai.setEnabled(isCustom);
            txtAkhir.setEnabled(isCustom);
        });

        panelTengah.add(lblFormat); panelTengah.add(cbFormat);
        panelTengah.add(lblRentang); panelTengah.add(cbRentang);
        panelTengah.add(lblMulai); panelTengah.add(txtMulai);
        panelTengah.add(lblAkhir); panelTengah.add(txtAkhir);

        btnProses = new JButton("Export Sekarang");
        btnProses.setBackground(new Color(201, 168, 76)); 
        btnProses.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProses.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel panelBawah = new JPanel();
        panelBawah.setBackground(new Color(22, 22, 26));
        panelBawah.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panelBawah.add(btnProses);

        add(panelTengah, BorderLayout.CENTER);
        add(panelBawah, BorderLayout.SOUTH);

        btnProses.addActionListener(e -> prosesPilihFile());
    }

    private void prosesPilihFile() {
        String format = cbFormat.getSelectedItem().toString();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan File Export");
        
        if (format.equals("CSV")) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));
        } else if (format.equals("Excel")) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel File (*.xlsx)", "xlsx"));
        }

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            if (format.equals("CSV") && !filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            } else if (format.equals("Excel") && !filePath.toLowerCase().endsWith(".xlsx")) {
                filePath += ".xlsx";
            }

            File finalFile = new File(filePath);

            try {
                ambilDataDariDatabase(finalFile, format);
                JOptionPane.showMessageDialog(this, "Berhasil! File di-export ke:\n" + filePath, "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose(); 
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal Export!\nError: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ambilDataDariDatabase(File finalFile, String format) throws Exception {
        String rentang = cbRentang.getSelectedItem().toString();
        Connection conn = Koneksi.getKoneksi(); 
        
        StringBuilder sql = new StringBuilder(
            "SELECT t.tanggal, " +
            "COALESCE(p.nama, 'Non-Member') AS pelanggan, " +
            "COALESCE(k.nama, 'Tanpa Kapster') AS kapster, " +
            "GROUP_CONCAT(i.nama SEPARATOR ', ') AS item, " +
            "t.metode_pembayaran, t.total " +
            "FROM transaksi t " +
            "LEFT JOIN pelanggan p ON t.id_pelanggan = p.id " +
            "LEFT JOIN kapster k ON t.id_kapster = k.id " +
            "LEFT JOIN detail_transaksi dt ON t.id = dt.id_transaksi " +
            "LEFT JOIN item i ON dt.id_item = i.id " +
            "WHERE 1=1 "
        );

        if (rentang.equals("Hari Ini")) {
            sql.append("AND DATE(t.tanggal) = CURDATE() ");
        } else if (rentang.equals("Bulan Ini")) {
            sql.append("AND MONTH(t.tanggal) = MONTH(CURDATE()) AND YEAR(t.tanggal) = YEAR(CURDATE()) ");
        } else if (rentang.equals("Custom (YYYY-MM-DD)")) {
            sql.append("AND DATE(t.tanggal) BETWEEN ? AND ? ");
        }

        sql.append("GROUP BY t.id ORDER BY t.tanggal ASC");

        PreparedStatement ps = conn.prepareStatement(sql.toString());
        if (rentang.equals("Custom (YYYY-MM-DD)")) {
            ps.setString(1, txtMulai.getText());
            ps.setString(2, txtAkhir.getText());
        }

        ResultSet rs = ps.executeQuery();

        if (format.equals("CSV")) {
            exportToCSV(finalFile, rs);
        } else if (format.equals("Excel")) {
            exportToExcel(finalFile, rs);
        }

        rs.close();
        ps.close();
    }

    private void exportToCSV(File file, ResultSet rs) throws Exception {
        FileWriter fw = new FileWriter(file);
        fw.append("Waktu,Pelanggan,Kapster,Item,Metode Bayar,Total\n");
        int grandTotal = 0;
        
        while (rs.next()) {
            fw.append("\"").append(rs.getString("tanggal")).append("\",");
            fw.append("\"").append(rs.getString("pelanggan")).append("\",");
            fw.append("\"").append(rs.getString("kapster")).append("\",");
            String item = rs.getString("item");
            if(item != null) item = item.replace("\"", "'"); 
            fw.append("\"").append(item).append("\",");
            fw.append("\"").append(rs.getString("metode_pembayaran")).append("\",");
            
            int total = rs.getInt("total");
            grandTotal += total;
            fw.append("\"").append(String.valueOf(total)).append("\"\n");
        }
        
        fw.append(",,,,,Grand Total: ").append(String.valueOf(grandTotal)).append("\n");
        fw.flush();
        fw.close();
    }

    private void exportToExcel(File file, ResultSet rs) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Riwayat Transaksi");
        
        // Buat style untuk header biar tebal
        CellStyle headerStyle = workbook.createCellStyle();
        
        org.apache.poi.ss.usermodel.Font excelFont = workbook.createFont();
        excelFont.setBold(true);
        headerStyle.setFont(excelFont);

        // Header Row
        Row header = sheet.createRow(0);
        String[] columns = {"Waktu", "Pelanggan", "Kapster", "Item", "Metode Bayar", "Total"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        int grandTotal = 0;
        
        while (rs.next()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rs.getString("tanggal"));
            row.createCell(1).setCellValue(rs.getString("pelanggan"));
            row.createCell(2).setCellValue(rs.getString("kapster"));
            row.createCell(3).setCellValue(rs.getString("item") != null ? rs.getString("item") : "-");
            row.createCell(4).setCellValue(rs.getString("metode_pembayaran"));
            
            int total = rs.getInt("total");
            grandTotal += total;
            row.createCell(5).setCellValue(total);
        }

        // Grand Total Row
        Row footer = sheet.createRow(rowNum + 1);
        Cell labelCell = footer.createCell(4);
        labelCell.setCellValue("Grand Total:");
        labelCell.setCellStyle(headerStyle);
        
        Cell totalCell = footer.createCell(5);
        totalCell.setCellValue(grandTotal);
        totalCell.setCellStyle(headerStyle);

        // Auto-size kolom agar rapi
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}