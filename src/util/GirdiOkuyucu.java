package  util;
import java.util.Scanner;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;

/**
 * Kullanıcının klavyeden güvenli veri girmesini sağlayan yardımcı sınıftır (System.in)
 * \n veren nextInt() metotlarının aksine nextLine() ile girdileri satır olarak okunup dönüştürülür.
 * Bu sayede hata verip programın çökmesi engellenir, tekrar veri isteğinde bulunulur.
 */
public class GirdiOkuyucu {
    private static final Scanner giris = new Scanner(System.in);

    /**
     * Ekrana bilgi mesajını yazdırır, kullanıcının girdiği satırı nextLine() ile okur
     * Başındaki ve sonundaki boşlukları gereksiz boşlukları trim() ile temizleyip döndürür
     * @param mesaj (kullanıcıya gösterilecek istem metni)
     * @return (boşlukları kırpılmış ham metin)
     */
    public static String okuSatir(String mesaj){
        System.out.print(mesaj);
        return giris.nextLine().trim();
    }

    /**
     * Kullanıcı geçerli bir tamsayı girene kadar sonsuz döngüde veri alır.
     * Okunan satırı parseInt ile tamsayıya çevirir ve sayı dışında karakter girilirse gelen NumberFormatException hatasını yakalayıp çökmeyi engeller ve uyarı verir.
     * @param mesaj (kullanıcıya gösterilecek istem metni)
     * @return (geçerli tamsayı değeri)
     */
    public static int okuInt(String mesaj){
        while(true){
            System.out.print(mesaj);
            try{
                return Integer.parseInt(giris.nextLine().trim());
            }
            catch(NumberFormatException e){
                System.out.println("Gecersiz sayi, tekrar deneyin.");
            }
        }
    }

    /**
     * Ondalıklı sayı girişleri için okuInt metodu ile aynı mantıkta çalışır.
     * .parseDouble ile dönüşüm yapar ve NumberFormatException yakalar, kullanıcıdan tekrar giriş ister.
     * @param mesaj (kullanıcıya gösterilecek istem metni)
     * @return (geçerli ondalıklı sayı değeri)
     */
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
    }

    /**
     * Kullanıcıdan dd.MM.yyyy formatında tarih ister ve TarihYardimcisi.ekrandanOku() metodu ile LocalDate nesnesine çevrilir
     * Biçim hatalıysa DateTimeParseException yakalar, doğru formatı hatırlatarak tekrar sordurur.
     * @param mesaj (Kullanıcıya gösterilecek istem metni)
     * @return (LocalDate türünde geçerli tarih)
     */
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
    }

    /**
     *Kullanıcıya onay sorusu gönderir. okuSatir ile satırları okur.
     *equalsIgnoreCase ile büyük/küçük harf duyarsız cevabı kontrol eder.
     *@param mesaj (onay sorusu metni)
     *@return (e veya evet girişinde true, diğer tüm durumlar false)
     */
    public static boolean evetMi(String mesaj){
        String cevap = okuSatir(mesaj);
        return cevap.equalsIgnoreCase("e") || cevap.equalsIgnoreCase("evet");
    }
}