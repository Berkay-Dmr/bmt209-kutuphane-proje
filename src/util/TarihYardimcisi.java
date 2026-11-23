package util;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Tarih biçimlendirme, metinden tarihe çevirme ve gün/gecikme hesaplama işlemlerini yürüten yardımcı bir sınıftır.
 * Yalnızca static metotlar bulundurur, nesnesi üretilemez ve kalıtımla davranışı değiştirilemesin diye final tanımlanmıştır.
 */

public final class TarihYardimcisi {
    private static final DateTimeFormatter EKRAN = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * private erişim belirteci ile gereksiz nesne üretimi engellenen constructor metot
     */

    private TarihYardimcisi() {}

    /**
     *LocalDate nesnesini konsol ekranında okunabilir dd.MM.yyyy formatına çevirir, tarih null ise hata vermemesi için - yapılır
     * @param date (biçimlendirilecek tarih)
     * @return (dd.MM.yyyy biçiminde metin veya -)
     */
    public static String yazi(LocalDate date) {
        return date ==  null ? "-" : date.format(EKRAN);
    }

    /**
     * Tarihi .txt dosyalarına kaydedecek standart formata (yyyy-MM-dd) biçimine dönüştürür
     * Henüz iade edilmemiş ödünçlerde tarih null olacağı için hata vermemesi adına - yapılır.
     * @param date (dosyaya yazılacak tarih)
     * @return (yyyy-MM-dd biçiminde ya da -)
     */
    public static String dosyayaYaz(LocalDate date) {
        return date == null ? "-" : date.toString();
    }

    /**
     * Veri dosyalarında yazan yyyy-MM-dd formatındaki metni geri LocalDate nesnesine çevirir.
     * - değerde null döndürülür.
     * @param date (dosyadan okunan tarih metni)
     * @return (LocalDate nesnesi veya null)
     */
    public static LocalDate dosyadanOku(String date) {
        return date.equals("-") ? null : LocalDate.parse(date);
    }

    /**
     * Kullanıcının klavyeden dd.MM.yyyy formatında girdiği metni LocalDate nesnesine parse ile ayrıştırır.
     * Biçim hatalıysa DateTimeParseException fırlatır.
     * @param date (kullanıcının klavyeden girdiği)
     * @return (LocalDate nesnesi)
     * @throws java.time.format.DateTimeParseException (Geçersiz tarih biçimi girilirse fırlatılan)
     */
    public static LocalDate ekrandanOku(String date) {
        return LocalDate.parse(date, EKRAN);
    }

    /**
     * Ödünç alış tarihine izin verilen gün sayısını plusDays ile ekleyerek son teslim tarihini hesaplar.
     * @param alis (ödünç alma tarihi)
     * @param gun (izin verilen ödünç süresi)
     * @return (hesaplanan son teslim tarihi)
     */
    public static LocalDate sonTeslim(LocalDate alis, int gun){
        return alis.plusDays(gun);
    }

    /**
     * Son teslim tarihi ile bugün arasındaki gün farkını ChronoUnit.DAYS.between ile hesaplar.
     * Zamanında veya erken iadelerde negatif değer çıkmaması için Math.max(0,...) ile en az 0 döndürülür.
     * @param sonTeslim (beklenen iade tarihi)
     * @param bugun (karşılaştırılan iade tarihi)
     * @return (gecikilen gün sayısı / min 0)
     */
    public static long gecikmeGunu(LocalDate sonTeslim, LocalDate bugun){
        return Math.max(0,ChronoUnit.DAYS.between(sonTeslim, bugun));
    }
}