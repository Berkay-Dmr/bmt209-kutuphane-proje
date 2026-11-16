package model;
public class OgrenciUye extends Uye{

    private static final char TUR_KODU = 'O';
    private static final float GECIKME_CARPANI = 1.0f;
    private static final int MAX_LIMIT = 3;
// sırası ile tur kodunu gecikmesini ve max limitini yazdık
    public OgrenciUye(int uyeNo, String ad, String soyad, String eposta, long telefon, double borc){
        super(uyeNo, ad, soyad, eposta, telefon, borc, TUR_KODU, GECIKME_CARPANI);
    }
    public OgrenciUye(String ad, String soyad, String eposta, long telefon){
        super (ad ,soyad , eposta ,telefon , 0.0, TUR_KODU , GECIKME_CARPANI);
    } // iki adet yapıcı tanımladık ve içlerini doldurduk

    @Override
    public  int getMaxOduncLimiti(){
        return MAX_LIMIT;
    }// bu metodu ezdik ve max kitap sayısını döndürdük
}