package util;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class TarihYardimcisi {
    private static final DateTimeFormatter EKRAN = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    //formatımızı belirledik sabit bir değerle
    private TarihYardimcisi() {}

    public static String yazi(LocalDate date) {
        return date ==  null ? "-" : date.format(EKRAN);
        //null tarih kontrolü yapıyoruz NullPointerException için.
    }

    public static String dosyayaYaz(LocalDate date) {
        return date == null ? "-" : date.toString();
        //Tarih yazdıracı
    }

    public static LocalDate dosyadanOku(String date) {
        return date.equals("-") ? null : LocalDate.parse(date);
        //String gelen ifadeleri üzerinde işlem yapılabilecek LocalDate formatına çeviriyoruz.
    }

    public static LocalDate ekrandanOku(String date) {
        return LocalDate.parse(date, EKRAN);
        //xx.xx.xxxx formatında tarihleri parse metodu localdate formatında xx.xx.xxxx şeklinde okusun diye yazdık
    }

    public static LocalDate sonTeslim(LocalDate alis, int gun){
        return alis.plusDays(gun);
        //ödünç verirken alış tarihine izin verilen günü ekleyip son teslim tarihini buluyoruz
    }

    public static long gecikmeGunu(LocalDate sonTeslim, LocalDate bugun){
        return Math.max(0,ChronoUnit.DAYS.between(sonTeslim, bugun));
        //iade alırken son teslim tarihi ile alış tarihi arasındaki farkı hesaplayacağız.
    }
}