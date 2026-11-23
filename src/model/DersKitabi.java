package model;
import exception.GecersizVeriException ;
import util.Dogrulayici;
/**kitap sınıfndan miras aldı ve yeni sınıf açıldı*/

public class DersKitabi extends Kitap{
    private static final double GUNLUK_CEZA = 1.5;

    private static final int ODUNC_SURESI = 30;

    private String dersKodu;

    private int donem;
/**ders kodu için getter yazıldı*/
    public String getDersKodu() {
        return dersKodu;
    }
/**setter derskodu yazıldı*/
    public void setDersKodu(String dersKodu) {
        if(Dogrulayici.bosMu(dersKodu)){
            throw new GecersizVeriException("ders kdou boş olamaz");
        }
        else{
            this.dersKodu =dersKodu.trim();
        }
    }/**getter yazıldı dönem için*/

    public int getDonem() {
        return donem;
    }
/**setter yazdılıd donem için ve kullanıldı*/
    public void setDonem(int donem) {
        if(donem < 1 || donem > 8){
            throw new GecersizVeriException("donem 1 den küçük 8 den büyük olamaz");
        }
        else{
            this.donem = donem;
        }

    }/**8 parametreli yapıcı çağrıldı*/
    public DersKitabi(int kaynakNo, String baslik, String yazar, int yayinYili, int toplamAdet, int mevcutAdet, int sayfaSayisi, String isbn, String dersKodu, int donem){
        super(kaynakNo, baslik, yazar, yayinYili, toplamAdet, mevcutAdet, sayfaSayisi, isbn);
        setDersKodu(dersKodu);
        setDonem(donem);
    }
/**6 parametreli yapıcı yazıldı*/
    public DersKitabi(String baslik, String yazar, int yayinYili, int toplamAdet, int sayfaSayisi, String isbn, String dersKodu, int donem){
        super(baslik, yazar, yayinYili, toplamAdet, sayfaSayisi, isbn);
        setDersKodu(dersKodu);
        setDonem(donem);
    }
    /**override edildi ve kullanıldı*/
    @Override
    public String kaynakTuru(){
        return "Ders Kitabı";
    }
    /**odunc süresi override edildi ve return ile döndürüldü*/
    @Override
    public int maxOduncSuresi() {
        return ODUNC_SURESI ;
    }
    /**gunlukceza için overrie yazıdlı ve return ile döndürüldü */
    @Override
    public double getGunlukCeza(){
        return GUNLUK_CEZA ;
    }
    /**diğer ksımlarda da yapıldığı gibi toString override edildi ve kullanıldı*/
    @Override
    public String toString(){
        return super.toString() + " | Ders Kodu: " + dersKodu + " | Dönem: " + donem;
    }
}

