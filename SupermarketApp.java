import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class SupermarketApp {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/supermarket_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "riandi123";

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean loginSuccess = false;

        // Proses login dengan validasi
        while (!loginSuccess) {
            System.out.println("+-----------------------------------------------------+");
            System.out.print("Username : ");
            String username = scanner.nextLine().trim();
            System.out.print("Password : ");
            String password = scanner.nextLine().trim();
            System.out.print("Captcha (ketik '1234') : ");
            String captcha = scanner.nextLine().trim();

            if (username.equalsIgnoreCase("akurian") && password.equals("bro123") && captcha.equals("1234")) {
                loginSuccess = true;
                System.out.println("Login berhasil!");
            } else {
                System.out.println("Login gagal, silakan ulangi.\n");
            }
        }

        System.out.println("+-----------------------------------------------------+");
        System.out.println("Selamat Datang di Supermarket Budiyan!");

        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss");
        System.out.println("Tanggal dan Waktu : " + formatter.format(now));
        System.out.println("+-----------------------------------------------------+");

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Koneksi ke database berhasil!");

            boolean exit = false;
            while (!exit) {
                // Menu utama
                System.out.println("\n+------------------- MENU -------------------+");
                System.out.println("1. Create Data");
                System.out.println("2. Read Data");
                System.out.println("3. Update Data");
                System.out.println("4. Delete Data");
                System.out.println("5. Exit");
                System.out.print("Pilih opsi: ");

                int pilihan = Integer.parseInt(scanner.nextLine().trim());

                switch (pilihan) {
                    case 1:
                        createDataInteractive(connection, scanner);
                        break;
                    case 2:
                        readData(connection);
                        break;
                    case 3:
                        updateDataInteractive(connection, scanner);
                        break;
                    case 4:
                        deleteDataInteractive(connection, scanner);
                        break;
                    case 5:
                        exit = true;
                        System.out.println("Terima kasih telah menggunakan aplikasi ini!");
                        break;
                    default:
                        System.out.println("Opsi tidak valid, coba lagi.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Koneksi ke database gagal: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Input salah, harap masukkan angka!");
        }
    }

    // Create Data Interactive
    private static void createDataInteractive(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("\n+--------- CREATE DATA ---------+");

        System.out.print("No. Faktur      : ");
        String noFaktur = scanner.nextLine().trim();

        System.out.print("Kode Barang     : ");
        String kodeBarang = scanner.nextLine().trim().toUpperCase();

        System.out.print("Nama Barang     : ");
        String namaBarang = scanner.nextLine().trim();

        System.out.print("Harga Barang    : ");
        double hargaBarang = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Jumlah Beli     : ");
        int jumlahBeli = Integer.parseInt(scanner.nextLine().trim());

        double total = hargaBarang * jumlahBeli;

        String insertSQL = "INSERT INTO transaksi (no_faktur, kode_barang, nama_barang, harga_barang, jumlah_beli, total) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, noFaktur);
           preparedStatement.setString(2, kodeBarang);
            preparedStatement.setString(3, namaBarang);
            preparedStatement.setDouble(4, hargaBarang);
            preparedStatement.setInt(5, jumlahBeli);
            preparedStatement.setDouble(6, total);
            preparedStatement.executeUpdate();
            System.out.println("Data berhasil ditambahkan!");
        } 
    }

    // Read Data (Output dalam bentuk tabel)
private static void readData(Connection connection) throws SQLException {
    System.out.println("\n+--------- READ DATA ---------+");
    String selectSQL = "SELECT * FROM transaksi";

    try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
        ResultSet resultSet = preparedStatement.executeQuery();

        // Header tabel
        System.out.println("+--------------------------------------------------------------------------------+");
        System.out.printf("| %-12s | %-12s | %-20s | %-10s | %-10s | %-12s |\n",
                "No. Faktur", "Kode Barang", "Nama Barang", "Harga", "Jumlah", "Total");
        System.out.println("+--------------------------------------------------------------------------------+");

        // Isi tabel
        while (resultSet.next()) {
            System.out.printf("| %-12s | %-12s | %-20s | %-10.2f | %-10d | %-12.2f |\n",
                    resultSet.getString("no_faktur"),
                    resultSet.getString("kode_barang"),
                    resultSet.getString("nama_barang"),
                    resultSet.getDouble("harga_barang"),
                    resultSet.getInt("jumlah_beli"),
                    resultSet.getDouble("total"));
        }

        // Penutup tabel
        System.out.println("+--------------------------------------------------------------------------------+");
    }
}

    // Update Data Interactive
    private static void updateDataInteractive(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("\n+--------- UPDATE DATA ---------+");

        System.out.print("Masukkan No. Faktur yang ingin diupdate: ");
        String noFaktur = scanner.nextLine().trim();

        System.out.print("Nama Barang Baru     : ");
        String namaBarang = scanner.nextLine().trim();

        System.out.print("Harga Barang Baru    : ");
        double hargaBarang = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Jumlah Beli Baru     : ");
        int jumlahBeli = Integer.parseInt(scanner.nextLine().trim());

        double total = hargaBarang * jumlahBeli;

        String updateSQL = "UPDATE transaksi SET nama_barang = ?, harga_barang = ?, jumlah_beli = ?, total = ? WHERE no_faktur = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, namaBarang);
            preparedStatement.setDouble(2, hargaBarang);
            preparedStatement.setInt(3, jumlahBeli);
            preparedStatement.setDouble(4, total);
            preparedStatement.setString(5, noFaktur);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Data berhasil diupdate!");
            } else {
                System.out.println("Data tidak ditemukan.");
            }
        }
    }

    // Delete Data Interactive
    private static void deleteDataInteractive(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("\n+--------- DELETE DATA ---------+");

        System.out.print("Masukkan No. Faktur yang ingin dihapus: ");
        String noFaktur = scanner.nextLine().trim();

        String deleteSQL = "DELETE FROM transaksi WHERE no_faktur = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setString(1, noFaktur);
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Data berhasil dihapus!");
            } else {
                System.out.println("Data tidak ditemukan.");
            }
        }
    }
}
