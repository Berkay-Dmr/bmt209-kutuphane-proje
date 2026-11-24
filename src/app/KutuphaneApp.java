package app;

import service.KutuphaneDeposu;
import service.MenuYoneticisi;
import util.DosyaIslemleri;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Kütüphane Ödünç ve Gecikme Takip Sistemi konsol uygulamasının giriş (main metodu) noktasıdır.
 * Program açılışında merkezi KutuphaneDeposu üzerinden kayıtları yükleyip kullanıcı etkileşimini yöneten MenuYoneticisi sınıfını başlatır.
 */
public class KutuphaneApp {
    /**
     * Singleton KutuphaneDeposu örneğini alarak verileriYukle() ile .txt dosyalarındaki kayıtları belleğe aktarır.
     * İlk çalıştırmada dosya yoksa FileNotFoundException yakalayıp data klasörünü oluşturur.
     * Özelden genele sıralı catch blokları ile hataları ham printStackTrace() basmadan yönetir ve ana menüyü başlatır.
     * @param args (Komut satırı argümanları)
     */
    public static void main(String[] args) {
        KutuphaneDeposu depo = KutuphaneDeposu.getInstance();
        try {
            depo.verileriYukle();
        } catch (FileNotFoundException e) {
            System.out.println("Veri dosyasi bulunamadi, bos baslaniyor.");
            new File(DosyaIslemleri.KLASOR).mkdirs();
        } catch (IOException e) {
            System.out.println("Dosya okuma hatasi: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Beklenmeyen yukleme hatasi: " + e.getMessage());
        }

        new MenuYoneticisi().baslat();
    }
}