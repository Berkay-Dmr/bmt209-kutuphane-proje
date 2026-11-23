package model;
import exception.GecersizVeriException;
import util.Dogrulayici;

/**sınıfımızı açtık ve gerekenleri ekledik sınfımız uüyeden extends alınmıştır */
public class OgrenciUye extends Uye{

    private String bolum;

    private int sinif;
    private static final char TUR_KODU = 'O';
    private static final float GECIKME_CARPANI = 1.0f;
    private static final int MAX_LIMIT = 3;

    private static final int ODUNC_SURESI = 15 ;

    public String getBolum() {
        return bolum;
    }
/**set bölüm ile birlikte bölüm atamasını gerçekleştirdik @throws*/
    public void setBolum(String bolum) {
        if(Dogrulayici.bosMu(bolum)){
            throw new GecersizVeriException("Bölüm Boş Olamaz");
        }
        else{
            this.bolum = bolum;
        }
    }
/**get sınıfı ile sinifi dödürdük*/
    public int getSinif() {
        return sinif;
    }

    /**set sinif ile sınıf arasında kodntro yaptık ve duruma göre @throws yaptık*/

    public void setSinif(int sinif) {
        if(sinif < 1 || sinif > 6){
            throw new GecersizVeriException("Sınıf sınırların dışındadır.");
        }
        else{
            this.sinif = sinif;
        }
    }

    // sırası ile tur kodunu gecikmesini ve max limitini yazdık

    /** yapıcı metot yazıldı */
    public OgrenciUye(int uyeNo, String ad, String soyad, String eposta, long telefon, double borc, String bolum, int sinif){
        super(uyeNo, ad, soyad, eposta, telefon, borc, TUR_KODU, GECIKME_CARPANI);
        setBolum(bolum);
        setSinif(sinif);
    }

    /** yapıcı metot farklı parametreli yazıldı */
    public OgrenciUye(String ad, String soyad, String eposta, long telefon, String bolum, int sinif){
        super (ad ,soyad , eposta ,telefon , 0.0, TUR_KODU , GECIKME_CARPANI);
        setBolum(bolum);
        setSinif(sinif);
    } // iki adet yapıcı tanımladık ve içlerini doldurduk

    /**max limit override edildi*/
    @Override
    public  int maxOduncAdedi(){
        return MAX_LIMIT;
    }// bu metodu ezdik ve max kitap sayısını döndürdük

    /**Odunc süresü döndürüldü*/
    @Override
    public int oduncSuresiGun(){
        return ODUNC_SURESI;
    }
    /**toString ile birlikte override edildi*/
    @Override
    public String toString(){
        return super.toString() + " | Bölüm: " + bolum + " | Sınıf: " + sinif;
    }
}