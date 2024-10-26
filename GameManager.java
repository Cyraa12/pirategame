import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.awt.event.*;

public class GameManager {
   UI ui;
   public JTextArea messageText;
   public JPanel[] bgPanel = new JPanel[10]; // Panel latar belakang
   public JLabel[] bglabel = new JLabel[10]; // Label latar belakang
   private int currentSlide = 0; // Mengatur slide saat ini ke 0 (slide kosong)

   public static void main(String[] args) {
      EventQueue.invokeLater(() -> new GameManager());
   }

   public GameManager() {
      // Inisialisasi UI
      ui = new UI(this);

   }

   // Kelas UI dalam
   class UI {
      GameManager gm;
      JFrame window;
      Timer transitionTimer; // Timer untuk transisi
      float opacity = 1.0f; // Opacity awal
      JPanel startPanel; // Panel layar awal
      JPanel mapPanel; // Panel untuk peta Indonesia
      JPanel worldMapPanel; // Panel untuk peta dunia

      public UI(GameManager gm) {
         this.gm = gm;
         createMainField();
         generateScreen();
         window.setVisible(true);
      }

      public void createMainField() {
         window = new JFrame("Game");
         int windowWidth = 1200;
         int windowHeight = 800;
         Dimension size = new Dimension(windowWidth, windowHeight); // Ukuran jendela 1200x800
         window.setSize(size);

         // Mendapatkan ukuran layar
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

         // Menempatkan jendela di tengah layar
         window.setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height) / 2);

         // Mengatur pengaturan jendela
         window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         window.getContentPane().setBackground(Color.black);
         window.setLayout(new BorderLayout()); // Mengatur manajer layout ke BorderLayout
      }

      public void createBackground(int bgNum, String bgFileName) {
         // Buat panel latar belakang
         bgPanel[bgNum] = new JPanel();
         bgPanel[bgNum].setLayout(new GridBagLayout()); // Mengatur layout untuk panel latar belakang
         bgPanel[bgNum].setBackground(Color.black); // Mengatur latar belakang panel agar terlihat

         // Tambahkan panel latar belakang ke jendela utama
         window.add(bgPanel[bgNum], BorderLayout.CENTER);

         // Membuat label latar belakang dengan gambar
         bglabel[bgNum] = new JLabel();
         updateBackgroundImage(bgNum, bgFileName); // Memperbarui gambar berdasarkan ukuran jendela

         // Tambahkan label ke panel latar belakang
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.gridx = 0; // Kolom
         gbc.gridy = 0; // Baris
         gbc.fill = GridBagConstraints.BOTH; // Mengisi ruang
         bgPanel[bgNum].add(bglabel[bgNum], gbc);

         // Buat JTextArea di bawah gambar
         createMessageText(); // Mengatur posisi JTextArea
         gbc.gridy = 1; // Baris kedua
         bgPanel[bgNum].add(messageText, gbc);

         // Tambahkan listener untuk mengatur ulang gambar saat jendela diubah ukurannya
         window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
               updateBackgroundImage(bgNum, bgFileName); // Memperbarui gambar saat ukuran jendela berubah
            }
         });

      }

      public void createMessageText() {
         // Membuat JTextArea
         messageText = new JTextArea("Selamat datang di permainan! Klik untuk memulai."); // Ubah pesan default
         messageText.setForeground(Color.white);
         messageText.setEditable(false);
         messageText.setLineWrap(true);
         messageText.setWrapStyleWord(true);
         messageText.setFont(new Font("Book Antiqua", Font.PLAIN, 26));
         messageText.setBackground(Color.DARK_GRAY); // Mengatur latar belakang agar terlihat jelas

         // Mengatur ukuran JTextArea agar mengambil ruang yang sesuai
         messageText.setPreferredSize(new Dimension(1200, 1000)); // Tinggi tetap, lebar sesuai jendela
      }

      private void updateBackgroundImage(int bgNum, String bgFileName) {
         // Mengambil ukuran jendela
         int windowWidth = window.getWidth();
         int windowHeight = window.getHeight();

         // Load image using File instead of getResource()
         File imageFile = new File(bgFileName);
         if (imageFile.exists()) {
            ImageIcon bgIcon = new ImageIcon(imageFile.getAbsolutePath());
            Image image = bgIcon.getImage().getScaledInstance(windowWidth, windowHeight - 100, Image.SCALE_SMOOTH);
            bglabel[bgNum].setIcon(new ImageIcon(image));
         } else {
            System.out.println("File not found: " + bgFileName);
         }
      }

      public void createTextLabel(String text) {
         JLabel messageLabel = new JLabel(text);
         messageLabel.setFont(new Font("Arial", Font.PLAIN, 20));
         messageLabel.setForeground(Color.WHITE); // Mengatur warna teks menjadi putih agar terlihat jelas
         messageLabel.setHorizontalAlignment(SwingConstants.CENTER); // Teks di tengah

         GridBagConstraints gbc = new GridBagConstraints();
         gbc.gridx = 0; // Posisi di kolom pertama
         gbc.gridy = 2; // Menempatkan label di bawah JTextArea
         gbc.insets = new Insets(10, 0, 0, 0); // Margin
         bgPanel[currentSlide].add(messageLabel, gbc); // Tambahkan label ke panel
         bgPanel[currentSlide].revalidate();
         bgPanel[currentSlide].repaint();
      }

      public void generateScreen() {
         // Memanggil createBackground dengan slide kosong
         createStartScreen(); // Menampilkan layar awal dengan tombol "Start"
      }

      private void createStartScreen() {
         class RoundedButton extends JButton {
            private int radius; // Radius untuk sudut

            public RoundedButton(String text, int radius) {
               super(text);
               this.radius = radius;
               setContentAreaFilled(false); // Agar latar belakang tidak diisi oleh default
               setFocusPainted(false); // Menghilangkan efek fokus
               setBorderPainted(false); // Menghilangkan border default
            }

            @Override
            protected void paintComponent(Graphics g) {
               Graphics2D g2d = (Graphics2D) g;
               g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

               // Menggambar tombol dengan sudut membulat
               g2d.setColor(getBackground());
               g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));

               // Menggambar teks di atas tombol
               super.paintComponent(g);
            }
         }

         startPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
               super.paintComponent(g);
               // Load the background image
               ImageIcon backgroundImage = new ImageIcon(
                     "startScreenBackground.m4");
               g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
         };

         startPanel.setLayout(new GridBagLayout());
         startPanel.setBackground(Color.black);

         // Mengganti JButton dengan RoundedButton
         RoundedButton startButton = new RoundedButton("START", 20); // Radius 20
         startButton.setFont(new Font("Book Antiqua", Font.BOLD, 40)); // Mengatur font untuk tombol
         startButton.setForeground(Color.BLACK); // Mengatur warna teks tombol
         startButton.setBackground(Color.YELLOW); // Mengatur warna latar belakang tombol
         startButton.setFocusable(false); // Menghapus fokus tombol
         startButton.setPreferredSize(new Dimension(350, 80));

         // Menambahkan ActionListener untuk tombol "Start"
         startButton.addActionListener(e -> showMapScreen());

         // Menambahkan beberapa panel kosong sebagai spacer
         GridBagConstraints gbcButton = new GridBagConstraints();
         gbcButton.gridx = 0; // Kolom
         gbcButton.gridy = 1; // Baris
         gbcButton.insets = new java.awt.Insets(300, 0, 0, 0);
         gbcButton.anchor = GridBagConstraints.CENTER; // Menempatkan tombol di tengah
         startPanel.add(startButton, gbcButton);

         // Menambahkan panel ke jendela
         window.add(startPanel, BorderLayout.CENTER);
         window.revalidate();
         window.repaint();
      }

      // Variabel global untuk menyimpan username
      private String username;

      private void showMapScreen() {
         // Hapus panel layar awal
         window.remove(startPanel);

         // Membuat panel baru untuk peta Indonesia
         mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
               super.paintComponent(g);
               // Load background image of Indonesia map
               ImageIcon backgroundImage = new ImageIcon(
                     "globe.jpg");
               g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
         };

         mapPanel.setLayout(new GridBagLayout());
         GridBagConstraints gbc = new GridBagConstraints();

         // Tambahkan label di atas TextField
         JLabel inputLabel = new JLabel("Masukkan Username:");
         gbc.gridx = 0;
         gbc.gridy = 0;
         gbc.insets = new Insets(20, 20, 5, 20); // Margin antara label dan TextField
         mapPanel.add(inputLabel, gbc);

         // Tambahkan TextField untuk input
         JTextField textBox = new JTextField();
         textBox.setPreferredSize(new Dimension(200, 30)); // Ukuran kotak input
         gbc.gridx = 0;
         gbc.gridy = 1;
         gbc.insets = new Insets(5, 20, 20, 20); // Margin untuk tata letak
         mapPanel.add(textBox, gbc);

         // Tambahkan tombol Submit
         JButton submitButton = new JButton("Submit");
         gbc.gridx = 0;
         gbc.gridy = 2;
         mapPanel.add(submitButton, gbc);

         // Tambahkan panel ke jendela
         window.add(mapPanel, BorderLayout.CENTER);
         window.revalidate();
         window.repaint();

         // Tambahkan ActionListener untuk tombol "Submit"
         submitButton.addActionListener(e -> {
            String inputText = textBox.getText().trim(); // Ambil teks input dan hapus spasi kosong di awal/akhir
            if (inputText.isEmpty()) {
               // Tampilkan pesan kesalahan jika input kosong
               JOptionPane.showMessageDialog(window, "Username harus diisi sebelum Submit!", "Input Error",
                     JOptionPane.ERROR_MESSAGE);
            } else {
               username = inputText; // Simpan username yang diinput ke variabel global
               showWorldMapScreen(); // Panggil layar selanjutnya
            }
         });
      }

      private void showWorldMapScreen() {
         // Hapus panel peta Indonesia
         window.remove(mapPanel);

         // Membuat panel baru untuk peta dunia
         worldMapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
               super.paintComponent(g);
               // Load background image of world map
               ImageIcon worldMapImage = new ImageIcon(
                     "vintage.jpg");
               g.drawImage(worldMapImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
         };

         worldMapPanel.setLayout(new GridBagLayout());
         GridBagConstraints gbc = new GridBagConstraints();

         // Tambahkan JLabel untuk menampilkan username
         JLabel usernameLabel = new JLabel("Selamat datang, " + username + "!" + "Yuk Simak Sejarah Indonesia");
         usernameLabel.setFont(new Font("Arial", Font.BOLD, 24));
         usernameLabel.setForeground(Color.YELLOW); // Teks warna kuning
         usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);

         gbc.gridx = 0;
         gbc.gridy = 0;
         gbc.insets = new Insets(20, 20, 5, 20);
         worldMapPanel.add(usernameLabel, gbc);

         // Tambahkan JLabel dengan teks sejarah Indonesia
         JLabel historyLabel = new JLabel("<html><div style='text-align: center;'>"
               + "Sejarah Indonesia dimulai dari peradaban Nusantara yang beragam. <br>"
               + "Pada tanggal 17 Agustus 1945, Indonesia memproklamirkan kemerdekaannya...</div></html>");
         historyLabel.setFont(new Font("Arial", Font.PLAIN, 24));
         historyLabel.setForeground(Color.WHITE); // Mengatur warna teks menjadi putih agar terlihat jelas
         historyLabel.setHorizontalAlignment(SwingConstants.CENTER);

         gbc.gridx = 0;
         gbc.gridy = 1;
         gbc.insets = new Insets(20, 20, 20, 20);
         worldMapPanel.add(historyLabel, gbc);

         // Tambahkan tombol "Next"
         JButton nextButton = new JButton("Next");
         gbc.gridx = 0;
         gbc.gridy = 2;
         worldMapPanel.add(nextButton, gbc);

         // Tambahkan panel peta dunia ke jendela
         window.add(worldMapPanel, BorderLayout.CENTER);
         window.revalidate();
         window.repaint();

         // Tambahkan ActionListener untuk tombol "Next"
         nextButton.addActionListener(e -> {
            window.remove(worldMapPanel); // Hapus panel world map
            showPetaIndonesiaScreen(); // Lanjutkan ke layar berikutnya
            window.revalidate();
            window.repaint();
         });
      }

      private boolean hoverSumatera = false;
      private boolean hoverJawa = false;
      private boolean hoverKalimantan = false;
      private boolean hoverSulawesi = false;
      private boolean hoverNusaTenggara = false;
      private boolean hoverPapua = false;

      // Variabel global untuk menyimpan HP pemain
      private int playerHP = 100; // Misalnya pemain mulai dengan 100 HP
      private JLabel hpLabel;

      private void showPetaIndonesiaScreen() {
         // Hapus panel world map
         window.remove(worldMapPanel);

         // Membuat panel baru untuk peta Indonesia
         JPanel petaIndonesiaPanel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
               super.paintComponent(g);
               // Load background image of Indonesia map
               ImageIcon indonesiaMapImage = new ImageIcon("petaindonesia.jpg");
               g.drawImage(indonesiaMapImage.getImage(), 0, 0, getWidth(), getHeight(), this);
               g.setColor(new Color(255, 0, 0, 100)); // Merah transparan

               // Area hover untuk pulau-pulau
               if (hoverSumatera)
                  g.fillRect(150, 350, 200, 280); // Gambar area Sumatera
               if (hoverJawa)
                  g.fillRect(380, 600, 200, 101); // Gambar area Jawa
               if (hoverKalimantan)
                  g.fillRect(390, 250, 201, 300); // Gambar area Kalimantan
               if (hoverSulawesi)
                  g.fillRect(600, 400, 100, 200); // Gambar area Sulawesi
               if (hoverNusaTenggara)
                  g.fillRect(600, 650, 150, 50); // Gambar area Nusa Tenggara
               if (hoverPapua)
                  g.fillRect(850, 430, 300, 300); // Gambar area Papua
            }
         };

         petaIndonesiaPanel.setLayout(new GridBagLayout());
         GridBagConstraints gbc = new GridBagConstraints();

         gbc.gridx = 0;
         gbc.gridy = 0;
         gbc.insets = new Insets(10, 10, 10, 10);

         // Tambahkan JLabel untuk menampilkan HP pemain
         hpLabel = new JLabel("HP: " + playerHP);
         hpLabel.setFont(new Font("Arial", Font.BOLD, 18));
         hpLabel.setForeground(Color.RED); // Warna merah untuk HP
         hpLabel.setHorizontalAlignment(SwingConstants.CENTER);

         gbc.gridx = 0;
         gbc.gridy = 1;
         petaIndonesiaPanel.add(hpLabel, gbc);

         // Tambahkan panel peta Indonesia ke jendela
         window.add(petaIndonesiaPanel, BorderLayout.CENTER);
         window.revalidate();
         window.repaint();

         // Tambahkan interaktivitas untuk klik di area pulau
         addIslandClickListener(petaIndonesiaPanel);

         // Tambahkan interaksi hover dengan mouse
         petaIndonesiaPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
               Point cursorPoint = e.getPoint();
               int x = cursorPoint.x;
               int y = cursorPoint.y;

               // Perbarui status hover berdasarkan posisi mouse
               hoverSumatera = isClickedSumatera(x, y);
               hoverJawa = isClickedJawa(x, y);
               hoverKalimantan = isClickedKalimantan(x, y);
               hoverSulawesi = isClickedSulawesi(x, y);
               hoverNusaTenggara = isClickedNusaTenggara(x, y);
               hoverPapua = isClickedPapua(x, y);

               // Meminta panel untuk digambar ulang
               petaIndonesiaPanel.repaint();
            }
         });
      }

      // Fungsi untuk memperbarui nilai HP pemain dan label HP
      // Fungsi untuk memperbarui nilai HP pemain dan label HP
      private void updatePlayerHP(int newHP) {
         playerHP = newHP;
         hpLabel.setText("HP: " + playerHP);

         // Jika HP pemain 0 atau kurang, tampilkan layar Game Over
         if (playerHP <= 0) {
            showGameOverScreen();
         }

         window.revalidate();
         window.repaint();
      }

      // Fungsi untuk menampilkan layar Game Over
      private void showGameOverScreen() {
         // Membuat panel untuk menampilkan gambar dan deskripsi kemenangan
         JPanel gameoverPanel = new JPanel();
         gameoverPanel.setLayout(new BorderLayout());

         // Menambahkan gambar kemenangan
         ImageIcon gameoverIcon = new ImageIcon("gameover.jpg"); // Gambar kemenangan
         Image gameoverImg = gameoverIcon.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH);
         gameoverIcon = new ImageIcon(gameoverImg);

         JLabel gameoverImageLabel = new JLabel(gameoverIcon);
         gameoverImageLabel.setHorizontalAlignment(JLabel.CENTER);
         gameoverPanel.add(gameoverImageLabel, BorderLayout.CENTER);

         // Menambahkan deskripsi kemenangan
         JLabel gameoverTextLabel = new JLabel(
               "<html><center>Game Over!.Your journey has ended...</center></html>");
         gameoverTextLabel.setHorizontalAlignment(JLabel.CENTER);
         gameoverPanel.add(gameoverTextLabel, BorderLayout.SOUTH);

         // Tampilkan panel kemenangan dalam dialog
         JOptionPane.showMessageDialog(window, gameoverPanel, "GAME OVER", JOptionPane.PLAIN_MESSAGE);

         // Nonaktifkan semua elemen lainnya (atau akhiri permainan)
         disableGameFunctions(); // Memanggil method untuk mematikan semua fungsi game
      }

      private void addIslandClickListener(JPanel panel) {
         panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               Point clickPoint = e.getPoint();
               int x = clickPoint.x;
               int y = clickPoint.y;

               // Cek posisi klik untuk masing-masing pulau
               if (isClickedSumatera(x, y)) {
                  showSumateraInfo();
               } else if (isClickedJawa(x, y)) {
                  showJawaInfo();
               } else if (isClickedKalimantan(x, y)) {
                  showKalimantanInfo();
               } else if (isClickedSulawesi(x, y)) {
                  showSulawesiInfo();
               } else if (isClickedNusaTenggara(x, y)) {
                  showNusaTenggaraInfo();
               } else if (isClickedPapua(x, y)) {
                  showPapuaInfo();
               }
            }
         });
      }

      // Fungsi untuk menentukan apakah klik berada di area pulau Sumatera
      private boolean isClickedSumatera(int x, int y) {
         // Sesuaikan dengan koordinat baru untuk Sumatera
         return x >= 150 && x <= 350 && y >= 350 && y <= 630; // 650 adalah tinggi 350 + 300
      }

      private boolean isClickedJawa(int x, int y) {
         return x >= 380 && x <= 580 && y >= 600 && y <= 701; // 701 adalah tinggi 600 + 101
      }

      private boolean isClickedKalimantan(int x, int y) {
         return x >= 390 && x <= 591 && y >= 250 && y <= 550;
      }

      private boolean isClickedSulawesi(int x, int y) {
         return x >= 600 && x <= 700 && y >= 400 && y <= 600; // 600 adalah tinggi 400 + 200
      }

      private boolean isClickedNusaTenggara(int x, int y) {
         return x >= 600 && x <= 750 && y >= 650 && y <= 700; // 650 adalah tinggi 650 + 50
      }

      private boolean isClickedPapua(int x, int y) {
         return x >= 850 && x <= 1150 && y >= 430 && y <= 730; // Area Papua
      }

      // Fungsi untuk menampilkan Sumatera dengan ImageIcon dan Deskripsi
      private void showSumateraInfo() {
         int response = JOptionPane.showConfirmDialog(window,
               "Apakah kamu yakin ingin menuju Sumatera ?",
               "Konfirmasi",
               JOptionPane.YES_NO_OPTION);

         if (response == JOptionPane.YES_OPTION) {
            // Menampilkan gambar dan deskripsi Sumatera
            ImageIcon sumateraIcon = new ImageIcon("sumatra.jpg");
            Image sumateraImage = sumateraIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH); // Sesuaikan
                                                                                                           // ukuran
            sumateraIcon = new ImageIcon(sumateraImage);

            JLabel label = new JLabel(
                  "<html>Pulau Sumatera, salah satu pulau terbesar di Indonesia, memiliki sejarah panjang yang kaya akan budaya dan perdagangan. <br> Pada masa kuno, Sumatera dikenal sebagai Swarnadwipa, atau \"Pulau Emas,\" karena kekayaan emas dan rempah-rempah yang melimpah. <br> Sumatera juga menjadi pusat kerajaan-kerajaan besar seperti Sriwijaya, sebuah kekuatan maritim yang dominan di Asia Tenggara pada abad ke-7 hingga ke-13, yang berperan penting dalam jalur perdagangan laut antara India dan Tiongkok. <br> Pengaruh Islam mulai masuk ke Sumatera pada abad ke-13, yang kemudian berkembang pesat melalui Kesultanan Aceh. <br> Pulau ini juga menjadi lokasi penting selama masa kolonial, terutama dalam eksploitasi sumber daya alam oleh Belanda, hingga akhirnya berperan dalam perjuangan kemerdekaan Indonesia.</html>",
                  sumateraIcon, JLabel.CENTER);
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.BOTTOM); // Posisikan teks di bawah gambar
            JOptionPane.showMessageDialog(window, label, "Informasi Sumatera", JOptionPane.INFORMATION_MESSAGE);

            // Tambahkan opsi "Go Back" dan "Go to Challenge"
            Object[] options = { "Go to Challenge" };
            int choice = JOptionPane.showOptionDialog(window,
                  "Pilih aksi selanjutnya:",
                  "Pilihan Aksi",
                  JOptionPane.DEFAULT_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null,
                  options,
                  options[0]);

            if (choice == 0) {
               showSumateraChallenge();
            }
         } else {
            JOptionPane.showMessageDialog(window, "Kamu batal menuju Sumatera.");
         }
      }

      // Fungsi untuk menampilkan Jawa dengan ImageIcon dan Deskripsi
      private void showJawaInfo() {
         int response = JOptionPane.showConfirmDialog(window,
               "Apakah kamu yakin ingin menuju Jawa ?",
               "Konfirmasi",
               JOptionPane.YES_NO_OPTION);

         if (response == JOptionPane.YES_OPTION) {
            // Menampilkan gambar dan deskripsi Jawa
            ImageIcon jawaIcon = new ImageIcon("jawa.jpg");
            Image jawaImage = jawaIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            jawaIcon = new ImageIcon(jawaImage);

            JLabel label = new JLabel(
                  "<html>Pulau Jawa, salah satu yang terpadat di dunia, memiliki sejarah penting bagi Nusantara. <br> Sejak abad ke-4 M, Jawa menjadi pusat kerajaan besar seperti Tarumanegara, Mataram Hindu yang membangun Candi Borobudur, hingga Majapahit di abad ke-13 yang menyatukan Nusantara. <br> Islam mulai berkembang di abad ke-15 dengan Kesultanan Demak sebagai kerajaan Islam pertama. <br> Pada abad ke-17, Belanda, melalui VOC, menguasai Jawa dan menjadikan Batavia pusat administrasi. <br> Jawa berperan besar dalam perjuangan kemerdekaan Indonesia, dengan tokoh penting seperti Soekarno lahir di sana, menjadikannya pusat politik dan ekonomi hingga kini.</html>",
                  jawaIcon, JLabel.CENTER);
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.BOTTOM);
            JOptionPane.showMessageDialog(window, label, "Informasi Jawa", JOptionPane.INFORMATION_MESSAGE);

            // Tambahkan opsi "Go Back" dan "Go to Challenge"
            Object[] options = { "Go to Challenge" };
            int choice = JOptionPane.showOptionDialog(window,
                  "Pilih aksi selanjutnya:",
                  "Pilihan Aksi",
                  JOptionPane.DEFAULT_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null,
                  options,
                  options[0]);

            if (choice == 0) {
               showJawaChallenge();
            }
         } else {
            JOptionPane.showMessageDialog(window, "Kamu batal menuju Jawa.");
         }
      }

      // Fungsi untuk menampilkan Kalimantan dengan ImageIcon dan Deskripsi
      private void showKalimantanInfo() {
         int response = JOptionPane.showConfirmDialog(window,
               "Apakah kamu yakin ingin menuju Kalimantan ?",
               "Konfirmasi",
               JOptionPane.YES_NO_OPTION);

         if (response == JOptionPane.YES_OPTION) {
            // Menampilkan gambar dan deskripsi Kalimantan
            ImageIcon kalimantanIcon = new ImageIcon("kalimantan.jpg");
            Image kalimantanImage = kalimantanIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            kalimantanIcon = new ImageIcon(kalimantanImage);

            JLabel label = new JLabel(
                  "<html>Pulau Kalimantan, pulau terbesar ketiga di dunia, memiliki sejarah yang berakar pada kerajaan-kerajaan kuno seperti Kerajaan Kutai, yang merupakan kerajaan Hindu tertua di Indonesia sejak abad ke-4 M. <br> Selain Kutai, Kesultanan Banjar di selatan Kalimantan juga memiliki peran penting dalam sejarah perdagangan dan penyebaran Islam pada abad ke-16. <br> Kalimantan dikenal dengan kekayaan alamnya, terutama hutan tropis dan tambang yang menjadi incaran penjajah Belanda sejak abad ke-17. <br> Pulau ini juga menjadi saksi penting dalam perjuangan melawan kolonialisme hingga kemerdekaan Indonesia. <br> Saat ini, Kalimantan sedang mengalami perubahan besar dengan rencana pemindahan ibu kota Indonesia dari Jakarta ke wilayah Kalimantan Timur, sebagai bagian dari upaya pemerataan pembangunan.</html>",
                  kalimantanIcon, JLabel.CENTER);
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.BOTTOM);
            JOptionPane.showMessageDialog(window, label, "Informasi Kalimantan", JOptionPane.INFORMATION_MESSAGE);

            // Tambahkan opsi "Go Back" dan "Go to Challenge"
            Object[] options = { "Go to Challenge" };
            int choice = JOptionPane.showOptionDialog(window,
                  "Pilih aksi selanjutnya:",
                  "Pilihan Aksi",
                  JOptionPane.DEFAULT_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null,
                  options,
                  options[0]);

            if (choice == 0) {
               showKalimantanChallenge();
            }
         } else {
            JOptionPane.showMessageDialog(window, "Kamu batal menuju Kalimantan.");
         }
      }

      // Fungsi untuk menampilkan Sulawesi dengan ImageIcon dan Deskripsi
      private void showSulawesiInfo() {
         int response = JOptionPane.showConfirmDialog(window,
               "Apakah kamu yakin ingin menuju Sulawesi ?",
               "Konfirmasi",
               JOptionPane.YES_NO_OPTION);

         if (response == JOptionPane.YES_OPTION) {
            // Menampilkan gambar dan deskripsi Sulawesi
            ImageIcon sulawesiIcon = new ImageIcon("sulawesi.jpg");
            Image sulawesiImage = sulawesiIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            sulawesiIcon = new ImageIcon(sulawesiImage);

            JLabel label = new JLabel(
                  "<html>Pulau Sulawesi, dengan bentuk unik dan budaya kaya, memiliki sejarah panjang sejak abad ke-14 sebagai pusat kerajaan maritim seperti Gowa dan Tallo. <br> Islam mulai berkembang pada abad ke-16, menyebar melalui kerajaan-kerajaan lokal. <br> Pada abad ke-17, Belanda melalui VOC berupaya menguasai perdagangan rempah-rempah di wilayah ini. <br> Tokoh seperti Sultan Hasanuddin memimpin perlawanan terhadap kolonialisme. <br> Kini, Sulawesi dikenal karena kekayaan alam dan keanekaragaman hayatinya yang luar biasa.</html>",
                  sulawesiIcon, JLabel.CENTER);
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.BOTTOM);
            JOptionPane.showMessageDialog(window, label, "Informasi Sulawesi", JOptionPane.INFORMATION_MESSAGE);

            // Tambahkan opsi "Go Back" dan "Go to Challenge"
            Object[] options = { "Go to Challenge" };
            int choice = JOptionPane.showOptionDialog(window,
                  "Pilih aksi selanjutnya:",
                  "Pilihan Aksi",
                  JOptionPane.DEFAULT_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null,
                  options,
                  options[0]);

            if (choice == 0) {
               showSulawesiChallenge();
            }
         } else {
            JOptionPane.showMessageDialog(window, "Kamu batal menuju Sulawesi.");
         }
      }

      // Fungsi untuk menampilkan Nusa Tenggara dengan ImageIcon dan Deskripsi
      private void showNusaTenggaraInfo() {
         int response = JOptionPane.showConfirmDialog(window,
               "Apakah kamu yakin ingin menuju Nusa Tenggara ?",
               "Konfirmasi",
               JOptionPane.YES_NO_OPTION);

         if (response == JOptionPane.YES_OPTION) {
            // Menampilkan gambar dan deskripsi Nusa Tenggara
            ImageIcon nusaTenggaraIcon = new ImageIcon(
                  "nusatenggara.jpg");
            Image nusaTenggaraImage = nusaTenggaraIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            nusaTenggaraIcon = new ImageIcon(nusaTenggaraImage);

            JLabel label = new JLabel(
                  "<html>Nusa Tenggara, terdiri dari Nusa Tenggara Barat (NTB) dan Nusa Tenggara Timur (NTT), memiliki sejarah kaya yang terkait dengan budaya dan kerajaan lokal. <br> Sejak abad ke-8, wilayah ini menjadi jalur perdagangan maritim utama, terutama untuk kayu cendana dan rempah-rempah. <br> Kerajaan Bima di NTB dan Wehali di NTT berperan dalam penyebaran Islam dan Katolik. <br> Pada abad ke-16, Portugis dan Belanda berusaha menguasai perdagangan di daerah ini. <br> Nusa Tenggara dihuni berbagai suku dengan tradisi unik, seperti suku Sasak di Lombok dan suku Flores di NTT. <br> Setelah kemerdekaan Indonesia, wilayah ini berkembang sebagai destinasi wisata alam dan budaya, terkenal dengan Gunung Rinjani dan Taman Nasional Komodo.</html>",
                  nusaTenggaraIcon, JLabel.CENTER);
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.BOTTOM);
            JOptionPane.showMessageDialog(window, label, "Informasi Nusa Tenggara", JOptionPane.INFORMATION_MESSAGE);

            // Tambahkan opsi "Go Back" dan "Go to Challenge"
            Object[] options = { "Go to Challenge" };
            int choice = JOptionPane.showOptionDialog(window,
                  "Pilih aksi selanjutnya:",
                  "Pilihan Aksi",
                  JOptionPane.DEFAULT_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null,
                  options,
                  options[0]);

            if (choice == 0) {
               showNusaTenggaraChallenge();
            }
         } else {
            JOptionPane.showMessageDialog(window, "Kamu batal menuju Nusa Tenggara.");
         }
      }

      // Fungsi untuk menampilkan Papua dengan ImageIcon dan Deskripsi
      private void showPapuaInfo() {
         int response = JOptionPane.showConfirmDialog(window,
               "Apakah kamu yakin ingin menuju Papua ?",
               "Konfirmasi",
               JOptionPane.YES_NO_OPTION);

         if (response == JOptionPane.YES_OPTION) {
            // Menampilkan gambar dan deskripsi Papua
            ImageIcon papuaIcon = new ImageIcon("papua.jpg");
            Image papuaImage = papuaIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            papuaIcon = new ImageIcon(papuaImage);

            JLabel label = new JLabel(
                  "<html>Pulau Papua, pulau terbesar kedua di dunia, memiliki sejarah kaya yang dimulai dari masyarakat adat yang telah mendiami wilayah ini selama ribuan tahun. <br> Sebelum penjajahan, Papua dikenal dengan berbagai suku dan budaya unik. <br> Penjelajah Eropa tiba pada abad ke-16, dan pada abad ke-19, Belanda dan Inggris menguasai sebagian besar pulau. <br> Papua menjadi bagian dari Hindia Belanda hingga Proklamasi Kemerdekaan Indonesia pada tahun 1945, namun statusnya diperdebatkan setelah integrasi ke dalam Indonesia pada tahun 1969. <br> Sejak saat itu, Papua menjadi sorotan terkait isu hak asasi manusia, keberagaman budaya, dan kekayaan sumber daya alam, menjadikannya salah satu wilayah paling kompleks di Indonesia.</html>",
                  papuaIcon, JLabel.CENTER);
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.BOTTOM);
            JOptionPane.showMessageDialog(window, label, "Informasi Papua", JOptionPane.INFORMATION_MESSAGE);

            // Tambahkan opsi "Go Back" dan "Go to Challenge"
            Object[] options = { "Go to Challenge" };
            int choice = JOptionPane.showOptionDialog(window,
                  "Pilih aksi selanjutnya:",
                  "Pilihan Aksi",
                  JOptionPane.DEFAULT_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null,
                  options,
                  options[0]);

            if (choice == 0) {
               showPapuaChallenge();
            }
         } else {
            JOptionPane.showMessageDialog(window, "Kamu batal menuju Papua.");
         }
      }

      private void showSumateraChallenge() {
         // Menampilkan gambar dan deskripsi tantangan
         ImageIcon challengeIcon = new ImageIcon("bunga.jpg");
         Image challengeImage = challengeIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
         challengeIcon = new ImageIcon(challengeImage);

         JLabel label = new JLabel(
               "<html>The Rafflesia flower, native to the rainforests of Sumatra, Indonesia, is known as the largest individual flower in the world. <br> It can grow up to 100 centimeters in diameter and weigh over 10 kilograms. <br> This flower is famous not only for its size but also for its strong smell, which resembles rotting flesh. <br> The scent helps attract insects like flies that aid in pollination. <br> The Rafflesia is a parasitic plant, meaning it cannot make its own food and relies on a host plant to survive, usually by attaching to the roots of vines. <br><h2> Why does the Rafflesia flower produce a strong smell? </h2></html>",
               challengeIcon, JLabel.CENTER);
         label.setHorizontalTextPosition(JLabel.CENTER);
         label.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 3 tombol aksi
         Object[] options = { "To scare away animals", "To attract insects for pollination",
               "To protect itself from predators" };
         int choice = JOptionPane.showOptionDialog(window,
               label,
               "Tantangan di Sumatera",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               options,
               options[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (choice == 0) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
            // Logika untuk mencari petunjuk
         } else if (choice == 1) {
            showNextChallenge();
            // Logika untuk melanjutkan tantangan
         } else if (choice == 2) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         }
      }

      // Fungsi untuk menampilkan tantangan di Sumatera
      private boolean sumateraChallenge2Completed = false;

      private void showSumateraChallenge2() {
         // Menampilkan gambar dan deskripsi tantangan
         ImageIcon challengeIcon = new ImageIcon("rumah.jpg");
         Image challengeImage = challengeIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
         challengeIcon = new ImageIcon(challengeImage);

         JLabel label = new JLabel(
               "<html>The Rumah Gadang is a traditional Minangkabau house that can also be found in Palembang, known for its distinctive architecture. <br> This large wooden house is built using natural materials such as timber, bamboo, and thatch for the roof. <br> Its shape is unique, featuring a high, curved roof that resembles buffalo horns, a symbol of Minangkabau culture. <br> The house is typically raised on stilts and has a rectangular structure with large open spaces inside to accommodate extended families. <br> The design is not only aesthetically beautiful but also functional, reflecting local wisdom in adapting to the tropical environment. <br> Rumah Gadang symbolizes the cultural richness and architectural ingenuity of the people in Palembang and beyond. <br><h2>What is one characteristic feature of the Rumah Gadang's architecture? </h2></html>",
               challengeIcon, JLabel.CENTER);
         label.setHorizontalTextPosition(JLabel.CENTER);
         label.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 3 tombol aksi
         Object[] options = { "A flat roof with modern materials", "A round structure made of concrete",
               "A curved roof resembling buffalo horns" };
         int choice = JOptionPane.showOptionDialog(window,
               label,
               "Tantangan ke 2 di Sumatera",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               options,
               options[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (choice == 0) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         } else if (choice == 1) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         } else if (choice == 2) {
            sumateraChallenge2Completed = true;
            showNext();
         }
      }

      // Fungsi baru untuk menampilkan koleksi dengan 6 ImageIcon
      private void showCollection() {
         // Membuat panel untuk menampung gambar-gambar koleksi
         JPanel collectionPanel = new JPanel();
         collectionPanel.setLayout(new GridLayout(2, 3, 10, 10)); // Layout grid 2 baris 3 kolom

         // Daftar path gambar koleksi
         String[] challengePaths = {
               "item1.jpg", // Gambar koleksi pertama
               "item2.jpg", // Gambar koleksi kedua
               "item3.jpg", // Gambar koleksi ketiga
               "item4.jpg", // Gambar koleksi keempat
               "item5.jpg", // Gambar koleksi kelima
               "item6.jpg" // Gambar koleksi keenam
         };
         boolean[] challengeStatus = {
               sumateraChallenge2Completed,
               jawaChallenge2Completed,
               kalimantanChallengeCompleted,
               sulawesiChallenge2Completed,
               nusaChallengeCompleted,
               papuaChallenge2Completed
               // Status tantangan di Sumatera
               // Tambahkan status untuk tantangan lain jika ada
         };

         boolean allCollected = true; // Menandai apakah semua item sudah terkumpul

         for (int i = 0; i < challengePaths.length; i++) {
            if (challengeStatus[i]) { // Hanya tambahkan gambar yang sudah diselesaikan
               ImageIcon icon = new ImageIcon(challengePaths[i]);
               Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Atur ukuran gambar
               icon = new ImageIcon(img);

               JLabel imageLabel = new JLabel(icon);
               collectionPanel.add(imageLabel); // Tambahkan label dengan gambar ke panel
            } else {
               allCollected = false; // Jika ada tantangan yang belum diselesaikan
            }
         }
         // Jika semua koleksi terkumpul, tampilkan panel victory
         if (allCollected) {
            showVictoryPanel();
         } else {
            // Tampilkan panel koleksi dalam dialog
            JOptionPane.showMessageDialog(window, collectionPanel, "Your Collection", JOptionPane.PLAIN_MESSAGE);
         }
      }

      // Fungsi untuk menampilkan panel kemenangan setelah semua koleksi terkumpul
      private void showVictoryPanel() {
         // Membuat panel untuk menampilkan gambar dan deskripsi kemenangan
         JPanel victoryPanel = new JPanel();
         victoryPanel.setLayout(new BorderLayout());

         // Menambahkan gambar kemenangan
         ImageIcon victoryIcon = new ImageIcon("victory.jpg"); // Gambar kemenangan
         Image victoryImg = victoryIcon.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH);
         victoryIcon = new ImageIcon(victoryImg);

         JLabel victoryImageLabel = new JLabel(victoryIcon);
         victoryImageLabel.setHorizontalAlignment(JLabel.CENTER);
         victoryPanel.add(victoryImageLabel, BorderLayout.CENTER);

         // Menambahkan deskripsi kemenangan
         JLabel victoryTextLabel = new JLabel(
               "<html><center>Congratulations!<br>You have collected all hidden treasures!</center></html>");
         victoryTextLabel.setHorizontalAlignment(JLabel.CENTER);
         victoryPanel.add(victoryTextLabel, BorderLayout.SOUTH);

         // Tampilkan panel kemenangan dalam dialog
         JOptionPane.showMessageDialog(window, victoryPanel, "Victory", JOptionPane.PLAIN_MESSAGE);

         // Nonaktifkan semua elemen lainnya (atau akhiri permainan)
         disableGameFunctions(); // Memanggil method untuk mematikan semua fungsi game
      }

      // Method untuk mematikan semua fungsi game (misal, menonaktifkan tombol dan
      // panel lain)
      private void disableGameFunctions() {
         // Menonaktifkan semua elemen interaktif (misalnya, tombol atau panel interaksi
         // lainnya)
         window.setEnabled(false); // Nonaktifkan seluruh window
         // Tambahkan logika lain di sini jika ada elemen tertentu yang perlu dimatikan

         // Atau, jika game berakhir, Anda bisa menutup aplikasi sepenuhnya
         System.exit(0); // Tutup aplikasi setelah dialog ditutup
      }

      // Fungsi baru untuk tantangan selanjutnya dengan deskripsi dan tombol tambahan
      private void showNextChallenge() {

         JLabel nextLabel = new JLabel(
               "<html> Move on to the next challenge </html>",
               JLabel.CENTER);
         nextLabel.setHorizontalTextPosition(JLabel.CENTER);
         nextLabel.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 2 tombol aksi
         Object[] nextOptions = { "Go to Next Challenge" };
         int nextChoice = JOptionPane.showOptionDialog(window,
               nextLabel,
               "Sumatra Challenge",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               nextOptions,
               nextOptions[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (nextChoice == 0) {
            showSumateraChallenge2();
            // Logika untuk memeriksa koleksi
         }
      }

      private void showNext() {
         // Menampilkan gambar baru dan deskripsi tantangan berikutnya
         ImageIcon nextIcon = new ImageIcon("item1.jpg"); // Gambar koleksi atau gambar lain
         Image nextImage = nextIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
         nextIcon = new ImageIcon(nextImage);

         JLabel nextLabel = new JLabel(
               "<html>Congratulations on completing this challenge ! <br>You have obtained a collection of rare items that have been hidden ! <br> Do you want to check your collection or continue playing ?</html>",
               nextIcon, JLabel.CENTER);
         nextLabel.setHorizontalTextPosition(JLabel.CENTER);
         nextLabel.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 2 tombol aksi
         Object[] nextOptions = { "Check Collection", "Continue Play" };
         int nextChoice = JOptionPane.showOptionDialog(window,
               nextLabel,
               "Your Reward",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               nextOptions,
               nextOptions[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (nextChoice == 0) {
            showCollection();
            // Logika untuk memeriksa koleksi
         } else if (nextChoice == 1) {
            showPetaIndonesiaScreen();
            // Logika untuk melanjutkan permainan
         }
      }

      private void showJawaChallenge() {
         // Menampilkan gambar dan deskripsi tantangan
         ImageIcon challengeIcon = new ImageIcon("sapi.jpg");
         Image challengeImage = challengeIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
         challengeIcon = new ImageIcon(challengeImage);

         JLabel label = new JLabel(
               "<html>Karapan Sapi is a traditional bull race held in Madura, an island in East Java, Indonesia. <br> This event typically takes place between August and October every year, during the dry season. <br> The race involves pairs of bulls pulling a wooden sled, with their riders standing on it as they race down a 100-meter track. <br> Karapan Sapi is not only a thrilling spectacle but also a way for local farmers to showcase the strength and speed of their bulls. <br> The race has deep cultural significance and is seen as a symbol of pride and prestige in Madura. <br> It is often held to celebrate the harvest season and to strengthen community bonds. <br><h2>What is the main purpose of holding the Karapan Sapi race in Madura?</h2></html>",
               challengeIcon, JLabel.CENTER);
         label.setHorizontalTextPosition(JLabel.CENTER);
         label.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 3 tombol aksi
         Object[] options = { "To attract tourists during the rainy season",
               "To train bulls for farming activities",
               "To celebrate the harvest season and showcase the bulls' strength" };
         int choice = JOptionPane.showOptionDialog(window,
               label,
               "Tantangan di Jawa",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               options,
               options[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (choice == 0) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         } else if (choice == 1) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         } else if (choice == 2) {
            showNextChallenge2();

         }
      }

      // Fungsi untuk menampilkan tantangan di Sumatera
      private boolean jawaChallenge2Completed = false;

      private void showJawaChallenge2() {
         // Menampilkan gambar dan deskripsi tantangan
         ImageIcon challengeIcon = new ImageIcon("hotel.jpg");
         Image challengeImage = challengeIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
         challengeIcon = new ImageIcon(challengeImage);

         JLabel label = new JLabel(
               "<html>The Battle of Surabaya is one of the most important events in Indonesia’s fight for independence. <br> It took place in November 1945, when the people of Surabaya, known as \"arek-arek Suroboyo,\" fought against British forces led by Brigadier General A.W.S. Mallaby. <br> The battle started after Mallaby was killed, escalating tensions between Indonesian fighters and British forces. <br> One significant moment occurred on September 19, 1945, when a group of Indonesians tore down the Dutch flag at Hotel Yamato in Surabaya. <br> They removed the blue part of the flag, leaving only the red and white, symbolizing the Indonesian national flag. <br> This act became a powerful symbol of resistance and patriotism during the battle. <br><h2> What did the arek-arek Suroboyo do to the Dutch flag at Hotel Yamato? </h2></html>",
               challengeIcon, JLabel.CENTER);
         label.setHorizontalTextPosition(JLabel.CENTER);
         label.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 3 tombol aksi
         Object[] options = { "They tore it down and replaced it with the Indonesian flag.", "They raised it higher.",
               "They burned it." };
         int choice = JOptionPane.showOptionDialog(window,
               label,
               "Tantangan ke 2 di Jawa",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               options,
               options[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (choice == 0) {
            jawaChallenge2Completed = true;
            showNext2();
            // Logika untuk mencari petunjuk
         } else if (choice == 1) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         } else if (choice == 2) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         }
      }

      // Fungsi baru untuk tantangan selanjutnya dengan deskripsi dan tombol tambahan
      private void showNextChallenge2() {

         JLabel nextLabel = new JLabel(
               "<html> Move on to the next challenge </html>",
               JLabel.CENTER);
         nextLabel.setHorizontalTextPosition(JLabel.CENTER);
         nextLabel.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 2 tombol aksi
         Object[] nextOptions = { "Go to Next Challenge" };
         int nextChoice = JOptionPane.showOptionDialog(window,
               nextLabel,
               "Jawa Challenge",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               nextOptions,
               nextOptions[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (nextChoice == 0) {
            showJawaChallenge2();
            // Logika untuk memeriksa koleksi
         }
      }

      private void showNext2() {
         // Menampilkan gambar baru dan deskripsi tantangan berikutnya
         ImageIcon nextIcon = new ImageIcon("item2.jpg"); // Gambar koleksi atau gambar lain
         Image nextImage = nextIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
         nextIcon = new ImageIcon(nextImage);

         JLabel nextLabel = new JLabel(
               "<html>Congratulations on completing this challenge ! <br>You have obtained a collection of rare items that have been hidden ! <br> Do you want to check your collection or continue playing ?</html>",
               nextIcon, JLabel.CENTER);
         nextLabel.setHorizontalTextPosition(JLabel.CENTER);
         nextLabel.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 2 tombol aksi
         Object[] nextOptions = { "Check Collection", "Continue Play" };
         int nextChoice = JOptionPane.showOptionDialog(window,
               nextLabel,
               "Your Reward",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               nextOptions,
               nextOptions[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (nextChoice == 0) {
            showCollection();
            // Logika untuk memeriksa koleksi
         } else if (nextChoice == 1) {
            showPetaIndonesiaScreen();
            // Logika untuk melanjutkan permainan
         }
      }

      private boolean kalimantanChallengeCompleted = false;

      private void showKalimantanChallenge() {
         // Menampilkan gambar dan deskripsi tantangan
         ImageIcon challengeIcon = new ImageIcon("ikn.jpg");
         Image challengeImage = challengeIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
         challengeIcon = new ImageIcon(challengeImage);

         JLabel label = new JLabel(
               "<html>Indonesia is currently building a new capital city called Ibu Kota Nusantara (IKN) in East Kalimantan. <br> This project was initiated by President Joko Widodo (Jokowi) as part of his vision to create a more balanced development across the country. <br> The current capital, Jakarta, faces overpopulation, traffic congestion, and environmental issues like sinking land. <br> The new capital aims to address these problems by relocating the government and creating a sustainable city with modern infrastructure. <br> IKN will incorporate eco-friendly technologies and is expected to boost economic growth in the region. <br> Construction began in 2022, and the first phase is planned to be completed by 2024. <br><h2> Why did President Joko Widodo decide to build a new capital city in Kalimantan? </h2></html>",
               challengeIcon, JLabel.CENTER);
         label.setHorizontalTextPosition(JLabel.CENTER);
         label.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 3 tombol aksi
         Object[] options = { "To reduce traffic congestion in Kalimantan",
               "To create a more balanced development across Indonesia",
               "To replace Kalimantan as Indonesia's economic center" };
         int choice = JOptionPane.showOptionDialog(window,
               label,
               "Tantangan di Kalimantan",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               options,
               options[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (choice == 0) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         } else if (choice == 1) {
            kalimantanChallengeCompleted = true;
            showNextChallenge3();
            // Logika untuk melanjutkan tantangan
         } else if (choice == 2) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         }
      }

      private void showNextChallenge3() {
         // Menampilkan gambar baru dan deskripsi tantangan berikutnya
         ImageIcon nextIcon = new ImageIcon("item3.jpg"); // Gambar koleksi atau gambar lain
         Image nextImage = nextIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
         nextIcon = new ImageIcon(nextImage);

         JLabel nextLabel = new JLabel(
               "<html>Congratulations on completing this challenge ! <br>You have obtained a collection of rare items that have been hidden ! <br> Do you want to check your collection or continue playing ?</html>",
               nextIcon, JLabel.CENTER);
         nextLabel.setHorizontalTextPosition(JLabel.CENTER);
         nextLabel.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 2 tombol aksi
         Object[] nextOptions = { "Check Collection", "Continue Play" };
         int nextChoice = JOptionPane.showOptionDialog(window,
               nextLabel,
               "Your Reward",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               nextOptions,
               nextOptions[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (nextChoice == 0) {
            showCollection();
            // Logika untuk memeriksa koleksi
         } else if (nextChoice == 1) {
            showPetaIndonesiaScreen();
            // Logika untuk melanjutkan permainan
         }
      }

      private void showSulawesiChallenge() {
         // Menampilkan gambar dan deskripsi tantangan
         ImageIcon challengeIcon = new ImageIcon("rambu.jpg");
         Image challengeImage = challengeIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
         challengeIcon = new ImageIcon(challengeImage);

         JLabel label = new JLabel(
               "<html>Rambu Solo' is a traditional funeral ceremony practiced by the Toraja people in South Sulawesi. <br> This elaborate ceremony is a significant part of Toraja culture and reflects their deep beliefs about life, death, and the afterlife. <br> According to Toraja tradition, death is not seen as the end of life but rather as a transition to the next stage. <br> The Rambu Solo' ceremony involves large gatherings, animal sacrifices (such as buffalo), and ritual processions, which are believed to help guide the soul of the deceased to the afterlife. <br> The ceremony can last several days and is a way for the Toraja people to honor their ancestors while maintaining their cultural and spiritual values. <br><h2>Why is the Rambu Solo' ceremony important to the Toraja people?</h2></html>",
               challengeIcon, JLabel.CENTER);
         label.setHorizontalTextPosition(JLabel.CENTER);
         label.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 3 tombol aksi
         Object[] options = { "It helps the deceased's soul transition to the afterlife",
               "It is a form of entertainment for the community", "It reduces funeral costs in Toraja" };
         int choice = JOptionPane.showOptionDialog(window,
               label,
               "Tantangan di Sulawesi",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               options,
               options[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (choice == 0) {
            showNextChallenge4();
            // Logika untuk mencari petunjuk
         } else if (choice == 1) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         } else if (choice == 2) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         }
      }

      // Fungsi untuk menampilkan tantangan di Sumatera
      private boolean sulawesiChallenge2Completed = false;

      private void showSulawesiChallenge2() {
         // Menampilkan gambar dan deskripsi tantangan
         ImageIcon challengeIcon = new ImageIcon("benteng.jpg");
         Image challengeImage = challengeIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
         challengeIcon = new ImageIcon(challengeImage);

         JLabel label = new JLabel(
               "<html>Fort Van der Wijck, more commonly known as Fort Rotterdam, is a historical landmark located in Makassar, South Sulawesi. <br> Built by the Dutch in the 17th century, the fort is a well-preserved example of colonial architecture, with its sturdy stone walls and distinctive European design. <br> Positioned by the coast, it offers visitors not only a glimpse into Indonesia’s colonial past but also stunning views of the surrounding sea and landscape. <br> Today, the fort attracts both local and international tourists who come to explore its history and enjoy the natural beauty of the area. <br> Fort Rotterdam serves as a reminder of Indonesia’s rich cultural heritage and the importance of preserving historical sites. <br><h2> Why do many foreign tourists visit Fort Van der Wijck (Fort Rotterdam)? </h2></html>",
               challengeIcon, JLabel.CENTER);
         label.setHorizontalTextPosition(JLabel.CENTER);
         label.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 3 tombol aksi
         Object[] options = { "To see modern architecture",
               "To explore Indonesia’s colonial history and enjoy the natural beauty",
               "To participate in adventure sports" };
         int choice = JOptionPane.showOptionDialog(window,
               label,
               "Tantangan ke 2 di Sulawesi",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               options,
               options[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (choice == 0) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         } else if (choice == 1) {
            sulawesiChallenge2Completed = true;
            showNext3();
         } else if (choice == 2) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         }
      }

      // Fungsi baru untuk tantangan selanjutnya dengan deskripsi dan tombol tambahan
      private void showNextChallenge4() {

         JLabel nextLabel = new JLabel(
               "<html> Move on to the next challenge </html>",
               JLabel.CENTER);
         nextLabel.setHorizontalTextPosition(JLabel.CENTER);
         nextLabel.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 2 tombol aksi
         Object[] nextOptions = { "Go to Next Challenge" };
         int nextChoice = JOptionPane.showOptionDialog(window,
               nextLabel,
               "Sulawesi Challenge",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               nextOptions,
               nextOptions[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (nextChoice == 0) {
            showSulawesiChallenge2();
            // Logika untuk memeriksa koleksi
         }
      }

      private void showNext3() {
         // Menampilkan gambar baru dan deskripsi tantangan berikutnya
         ImageIcon nextIcon = new ImageIcon("item4.jpg"); // Gambar koleksi atau gambar lain
         Image nextImage = nextIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
         nextIcon = new ImageIcon(nextImage);

         JLabel nextLabel = new JLabel(
               "<html>Congratulations on completing this challenge ! <br>You have obtained a collection of rare items that have been hidden ! <br> Do you want to check your collection or continue playing ?</html>",
               nextIcon, JLabel.CENTER);
         nextLabel.setHorizontalTextPosition(JLabel.CENTER);
         nextLabel.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 2 tombol aksi
         Object[] nextOptions = { "Check Collection", "Continue Play" };
         int nextChoice = JOptionPane.showOptionDialog(window,
               nextLabel,
               "Your Reward",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               nextOptions,
               nextOptions[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (nextChoice == 0) {
            showCollection();
            // Logika untuk memeriksa koleksi
         } else if (nextChoice == 1) {
            showPetaIndonesiaScreen();
            // Logika untuk melanjutkan permainan
         }
      }

      private boolean nusaChallengeCompleted = false;

      private void showNusaTenggaraChallenge() {
         // Menampilkan gambar dan deskripsi tantangan
         ImageIcon challengeIcon = new ImageIcon("mandalika.jpg");
         Image challengeImage = challengeIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
         challengeIcon = new ImageIcon(challengeImage);

         JLabel label = new JLabel(
               "<html>The Mandalika Circuit, located in Nusa Tenggara Timur (NTT), was developed under the leadership of President Joko Widodo as part of Indonesia's efforts to boost its global image through world-class sporting events. <br> Officially opened in 2021, the circuit is often referred to as one of the most beautiful in the world due to its stunning location by the sea. <br> With breathtaking views of the coastline and modern infrastructure, the Mandalika Circuit hosts international racing events like MotoGP. <br> This project not only promotes tourism in the region but also highlights Indonesia’s capability to host large-scale international events, enhancing the country’s reputation on the world stage. <br><h2>Why is the Mandalika Circuit often considered one of the most beautiful circuits in the world?</h2></html>",
               challengeIcon, JLabel.CENTER);
         label.setHorizontalTextPosition(JLabel.CENTER);
         label.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 3 tombol aksi
         Object[] options = { "Because it is located next to the sea", "Because it is the longest circuit in the world",
               "Because it has the largest seating capacity" };
         int choice = JOptionPane.showOptionDialog(window,
               label,
               "Tantangan di Nusa Tenggara",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               options,
               options[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (choice == 0) {
            nusaChallengeCompleted = true;
            showNextChallenge5();
            // Logika untuk mencari petunjuk
         } else if (choice == 1) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         } else if (choice == 2) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         }
      }

      private void showNextChallenge5() {
         // Menampilkan gambar baru dan deskripsi tantangan berikutnya
         ImageIcon nextIcon = new ImageIcon("item5.jpg"); // Gambar koleksi atau gambar lain
         Image nextImage = nextIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
         nextIcon = new ImageIcon(nextImage);

         JLabel nextLabel = new JLabel(
               "<html>Congratulations on completing this challenge ! <br>You have obtained a collection of rare items that have been hidden ! <br> Do you want to check your collection or continue playing ?</html>",
               nextIcon, JLabel.CENTER);
         nextLabel.setHorizontalTextPosition(JLabel.CENTER);
         nextLabel.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 2 tombol aksi
         Object[] nextOptions = { "Check Collection", "Continue Play" };
         int nextChoice = JOptionPane.showOptionDialog(window,
               nextLabel,
               "Your Reward",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               nextOptions,
               nextOptions[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (nextChoice == 0) {
            showCollection();
            // Logika untuk memeriksa koleksi
         } else if (nextChoice == 1) {
            showPetaIndonesiaScreen();
            // Logika untuk melanjutkan permainan
         }
      }

      private void showPapuaChallenge() {
         // Menampilkan gambar dan deskripsi tantangan
         ImageIcon challengeIcon = new ImageIcon("freeport.jpg");
         Image challengeImage = challengeIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
         challengeIcon = new ImageIcon(challengeImage);

         JLabel label = new JLabel(
               "<html>The Freeport mine, located in Papua, Indonesia, is one of the largest gold and copper mines in the world. <br> For many years, it was operated by the American company Freeport-McMoRan. <br> However, in 2018, a significant change occurred when Indonesia's state-owned enterprises (BUMN) took a 51% ownership stake in the mine. <br> This marked a major shift in control, giving Indonesia a majority share in its own natural resources. <br> The Indonesian government now plays a larger role in managing the wealth produced by the mine, contributing to the country’s economic growth while also addressing local environmental and social issues. <br><h2>What was one of the key impacts of Indonesia gaining a 51% ownership stake in the Freeport mine?</h2></html>",
               challengeIcon, JLabel.CENTER);
         label.setHorizontalTextPosition(JLabel.CENTER);
         label.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 3 tombol aksi
         Object[] options = { "Increased foreign investment in Freeport-McMoRan",
               "Greater control over the management of the country’s natural resources",
               "reduction in the mine's production of gold and copper" };
         int choice = JOptionPane.showOptionDialog(window,
               label,
               "Tantangan di Papua",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               options,
               options[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (choice == 0) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         } else if (choice == 1) {
            showNextChallenge6();
            // Logika untuk melanjutkan tantangan
         } else if (choice == 2) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         }
      }

      // Fungsi untuk menampilkan tantangan di Sumatera
      private boolean papuaChallenge2Completed = false;

      private void showPapuaChallenge2() {
         // Menampilkan gambar dan deskripsi tantangan
         ImageIcon challengeIcon = new ImageIcon("raja.jpg");
         Image challengeImage = challengeIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
         challengeIcon = new ImageIcon(challengeImage);

         JLabel label = new JLabel(
               "<html>Raja Ampat, located in West Papua, is known for its breathtaking natural beauty and rich marine biodiversity. <br> This archipelago consists of more than 1,500 small islands, coral reefs, and clear blue waters, making it one of the world’s top destinations for diving and snorkeling. <br> Its stunning landscapes and underwater life attract both local and international tourists, especially those seeking adventure and tranquility. <br> The increasing number of foreign visitors contributes significantly to the local economy and helps promote Indonesia as a global eco-tourism destination. <br> However, there are also challenges in preserving the pristine environment as tourism continues to grow. <br><h2> What is one of the main reasons foreign tourists visit Raja Ampat? </h2></html>",
               challengeIcon, JLabel.CENTER);
         label.setHorizontalTextPosition(JLabel.CENTER);
         label.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 3 tombol aksi
         Object[] options = { "To experience the rich marine biodiversity",
               "To explore historical landmarks",
               "To attend large festivals" };
         int choice = JOptionPane.showOptionDialog(window,
               label,
               "Tantangan ke 2 di Papua",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               options,
               options[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (choice == 0) {
            papuaChallenge2Completed = true;
            showNext4();
         } else if (choice == 1) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         } else if (choice == 2) {
            updatePlayerHP(playerHP - 15);
            JOptionPane.showMessageDialog(window, "Kamu gagal menjawab challenge !. HP anda berkurang 15");
         }
      }

      // Fungsi baru untuk tantangan selanjutnya dengan deskripsi dan tombol tambahan
      private void showNextChallenge6() {

         JLabel nextLabel = new JLabel(
               "<html> Move on to the next challenge </html>",
               JLabel.CENTER);
         nextLabel.setHorizontalTextPosition(JLabel.CENTER);
         nextLabel.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 2 tombol aksi
         Object[] nextOptions = { "Go to Next Challenge" };
         int nextChoice = JOptionPane.showOptionDialog(window,
               nextLabel,
               "Papua Challenge",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               nextOptions,
               nextOptions[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (nextChoice == 0) {
            showPapuaChallenge2();
            // Logika untuk memeriksa koleksi
         }
      }

      private void showNext4() {
         // Menampilkan gambar baru dan deskripsi tantangan berikutnya
         ImageIcon nextIcon = new ImageIcon("item6.jpg"); // Gambar koleksi atau gambar lain
         Image nextImage = nextIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
         nextIcon = new ImageIcon(nextImage);

         JLabel nextLabel = new JLabel(
               "<html>Congratulations on completing this challenge ! <br>You have obtained a collection of rare items that have been hidden ! <br> Do you want to check your collection or continue playing ?</html>",
               nextIcon, JLabel.CENTER);
         nextLabel.setHorizontalTextPosition(JLabel.CENTER);
         nextLabel.setVerticalTextPosition(JLabel.BOTTOM);

         // Menampilkan deskripsi tantangan dengan 2 tombol aksi
         Object[] nextOptions = { "Check Collection", "Continue Play" };
         int nextChoice = JOptionPane.showOptionDialog(window,
               nextLabel,
               "Your Reward",
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               nextOptions,
               nextOptions[0]);

         // Tindak lanjut berdasarkan pilihan tombol
         if (nextChoice == 0) {
            showCollection();
            // Logika untuk memeriksa koleksi
         } else if (nextChoice == 1) {
            showPetaIndonesiaScreen();
            // Logika untuk melanjutkan permainan
         }
      }

   }
}