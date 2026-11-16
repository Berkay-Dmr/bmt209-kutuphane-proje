package util;

import java.io.BufferedReader;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DosyaIslemleri {
    public static final String KLASOR = "data";
    public static String yol(String dosyaAdi){
        return KLASOR + File.separator + dosyaAdi;
        //tam göreli yolu döndürüyoruz her cihaz için kendine özel
    }
    public static List<String[]> satirlariOku(String dosyaAdi) throws IOException {
        List<String[]> sonuc = new ArrayList<>();
        //try-with-resources bloğu açıyoruz
        try (BufferedReader br = new BufferedReader(new FileReader(yol(dosyaAdi)))) {
            String satir;
            //satır satır okuyacağız sırayla
            while ((satir = br.readLine()) != null) {
                if (satir.trim().isEmpty() || satir.startsWith("#")) continue;
                sonuc.add(satir.split(";"));
            }
            //Bu bloğun amacı, hata durumunda bile bellekte sızıntı yapmadan otomatik kapanmayı sağlamaktır
        }
        return sonuc;
        // Dosyadaki geçerli satırları ';' işaretinden parçalayıp dizi listesi olarak döndürüyoruz
    }
    public static List<String[]> scannerIleOku(String dosyaAdi) throws IOException {
        List<String[]> sonuc = new ArrayList<>();
        try (Scanner sc = new Scanner(new FileReader(yol(dosyaAdi)))) {
            while (sc.hasNextLine()) {
                String satir = sc.nextLine();
                if (satir.trim().isEmpty() || satir.startsWith("#")) continue;
                sonuc.add(satir.split(";"));
            }
        }
        return sonuc;
        // Çeşitlilik için uyeler.txt dosyasını Scanner ile satır satır okuyup parçalıyoruz
    }
    public static void satirlariYaz(String dosyaAdi, List<String> satirlar) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(yol(dosyaAdi)))) {
            for(String s : satirlar){
                bw.write(s);
                bw.newLine();
            }
        }
        // Hazırlanan metin satırlarını dosyaya alt alta yazdırıp try-with-resources ile dosyayı otomatik kapatıyoruz
    }
}