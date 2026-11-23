package util;

import java.io.BufferedReader;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Uygulamanın veri dosyalarını okuma ve yazma işlevlerini yürüten yardımcı sınıftır.
 * Dosya akışında hata olması durumunda bellek sızıntısı yapmadan otomatik kapanması için try-with-resources kullanılmıştır.
 * Platform bağımsızlığı için File separator ile göreli yol oluşturulur.
 * IOException hataların throws ile çağıran üst sınıflara iletilir.
 */
public class DosyaIslemleri {
    //Veri dosyalarının saklandığı ana klasör adını tutar.
    public static final String KLASOR = "data";

    /**
     *KLASOR adını işletim sistemine uygun dizin ayırıcısını File.separator ile dosya adını birleştirerek göreli dosya yolunu üretir.
     * @param dosyaAdi (erişilecek dosyanın adı)
     * @return (data/dosyaYolu biçimindeki göreli dosya yolu)
     */
    public static String yol(String dosyaAdi){
        return KLASOR + File.separator + dosyaAdi;
    }

    /**
     * Belirtilen dosyayı BufferedReader ve FileReader kullanarak try-with-resources bloğu içinde satır satır okur.
     * Boş satırları .trim().isEmpty() ve # ile başlayan açıklama satırlarını atlar.
     * Geçerli satırları ; ayırıcısını parçalayarak dizi listesi olarak döndürür
     * @param dosyaAdi (okunacak dosyanın adı)
     * @return (; ile ayrılmış sütun dizilerinden oluşan liste)
     * @throws IOException (dosya bulunamazsa/okuma hatası olursa Input/Output Exception fırlatılır)
     */
    public static List<String[]> satirlariOku(String dosyaAdi) throws IOException {
        List<String[]> sonuc = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(yol(dosyaAdi)))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                if (satir.trim().isEmpty() || satir.startsWith("#")) continue;
                sonuc.add(satir.split(";"));
            }
        }
        return sonuc;
    }

    /**
     * Dosyayı Scanner(File) ile okuyarak çoklu dosya okuma yapar.
     * Dosya sonunda NoSuchElementException oluşmasını önlemek için while(sc.hasNextLine()) kontrolü yapar.
     * Boş ve # ile başlayan satırları atlayıp ; işaretine göre parçalar.
     * @param dosyaAdi (okunacak dosya adı)
     * @return (; ile ayrılmış sütun dizilerinden oluşan liste)
     * @throws IOException (Dosya bulunamazsa/okunamazsa veya giriş çıkış hataları olursa)
     */
    public static List<String[]> scannerIleOku(String dosyaAdi) throws IOException {
        List<String[]> sonuc = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(yol(dosyaAdi)))) {
            while (sc.hasNextLine()) {
                String satir = sc.nextLine();
                if (satir.trim().isEmpty() || satir.startsWith("#")) continue;
                sonuc.add(satir.split(";"));
            }
        }
        return sonuc;
    }

    /**
     * Verilen metin listesini BufferedWriter ve FileWriter kullanarak try-with-resources bloğu içinde hedef dosyaya satır satır yazar
     * İşlemi bitince dosyayı oto kapatır.
     * @param dosyaAdi (yazılacak hedef dosyanın adı)
     * @param satirlar (dosyayı alt alta kaydedilecek biçimlendirilmiş satır listesi)
     * @throws IOException (dosya yazma sırasında giriş/çıkış hatası oluşursa fırlatılır)
     */
    public static void satirlariYaz(String dosyaAdi, List<String> satirlar) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(yol(dosyaAdi)))) {
            for(String s : satirlar){
                bw.write(s);
                bw.newLine();
            }
        }
    }
}