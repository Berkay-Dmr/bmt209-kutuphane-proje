package  util;
import java.util.Scanner;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;

public class GirdiOkuyucu {
    private static final Scanner giris = new Scanner(System.in);
    public static String okuSatir(String mesaj){
        System.out.print(mesaj);
        return giris.nextLine().trim();
        //Boşlukları kırpıp satır okuma metodu yazdık
    }

    public static int okuInt(String mesaj){
        while(true){
            System.out.print(mesaj);
            try{
                return Integer.parseInt(giris.nextLine().trim());
                //eğer kullanıcı harf girerse exception yakala
            }
            catch(NumberFormatException e){
                System.out.println("Gecersiz sayi, tekrar deneyin.");
            }
        }
        //Bu döngü böyle devam eder geçerli giriş alana kadar
    }
    public static double okuDouble(String mesaj){
        while(true){
            System.out.print(mesaj);
            try{
                return Double.parseDouble(giris.nextLine().trim());
            }
            catch(NumberFormatException e){
                System.out.println("Gecersiz sayi, tekrar deneyin.");
            }
        }
        //Aynı fonksiyonun double türü için giriş versiyonu
    }
    public static LocalDate okuTarih(String mesaj){
        while(true){
            System.out.print(mesaj);
            try{
                return TarihYardimcisi.ekrandanOku(giris.nextLine().trim());
            }
            catch (DateTimeParseException e){
                System.out.println("Gecersiz tarih (gg.aa.yyyy formatinda girin).");
            }
        }
        //tarih okutarak girilen formatı test ediyoruz
    }
    public static boolean evetMi(String mesaj){
        String cevap = okuSatir(mesaj);
        return cevap.equalsIgnoreCase("e") || cevap.equalsIgnoreCase("evet");
        //aldığımız cevabı kontrol ediyoruz
    }
}