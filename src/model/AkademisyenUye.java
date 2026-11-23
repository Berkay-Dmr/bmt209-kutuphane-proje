package model;
import util.Dogrulayici;
import exception.GecersizVeriException;
public class AkademisyenUye extends Uye{

    private static final char TUR_KODU = 'A';
    private static final float GECIKME_CARPANI = 0.5f;
    private static final int MAX_LIMIT = 10; // Ogrenciüyede yaptığımızın aynısını yaptık

    private static final int ODUNC_SURESI = 30;



    private String unvan ;
    private String birim;

    public String getUnvan() {
        return unvan;
    }

    public String getBirim() {
        return birim;
    }

    public void setUnvan(String unvan) {
        if(Dogrulayici.bosMu(unvan)){
            throw new GecersizVeriException("unvan boş olamaz");
        }
        else{
            this.unvan = unvan;
        }
    }

    public void setBirim(String birim) {
        if(Dogrulayici.bosMu(birim)){
            throw new GecersizVeriException("birim boş olamaz");
        }
        else{
            this.birim = birim;
        }
    }

    public AkademisyenUye(int uyeNo, String ad, String soyad, String eposta, long telefon, double borc, String unvan, String birim){
        super(uyeNo, ad, soyad, eposta, telefon, borc, TUR_KODU, GECIKME_CARPANI);
        setUnvan(unvan);
        setBirim(birim);
    }
    public AkademisyenUye(String ad, String soyad, String eposta, long telefon, String unvan, String birim){
        super (ad ,soyad , eposta ,telefon , 0.0, TUR_KODU , GECIKME_CARPANI);
        setBirim(birim);
        setUnvan(unvan);
    }

    @Override
    public int maxOduncAdedi(){
        return MAX_LIMIT;
    } // sadece akademisyenlere daha fazla hak tanınmasını sağladık

    @Override
    public  int  oduncSuresiGun (){
        return ODUNC_SURESI;
    }

    @Override
    public String toString(){
        return super.toString() + " birim : "  + birim + " unvan : " + unvan;
    }

}