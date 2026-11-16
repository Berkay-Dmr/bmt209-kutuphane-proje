package model;
public class AkademisyenUye extends Uye{

    private static final char TUR_KODU = 'A';
    private static final float GECIKME_CARPANI = 0.5f;
    private static final int MAX_LIMIT = 10; // Ogrenciüyede yaptığımızın aynısını yaptık
    public AkademisyenUye(int uyeNo, String ad, String soyad, String eposta, long telefon, double borc){
        super(uyeNo, ad, soyad, eposta, telefon, borc, TUR_KODU, GECIKME_CARPANI);
    }
    public AkademisyenUye(String ad, String soyad, String eposta, long telefon){
        super (ad ,soyad , eposta ,telefon , 0.0, TUR_KODU , GECIKME_CARPANI);
    }

    @Override
    public int getMaxOduncLimiti(){
        return MAX_LIMIT;
    } // sadece akademisyenlere daha fazla hak tanınmasını sağladık
}