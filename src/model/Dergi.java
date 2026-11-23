package model;
import exception.GecersizVeriException ;
import util.Dogrulayici;

/**sınıf kaynaktan extends aldı vekullanıldı*/
public class Dergi extends Kaynak{
    private static final double GUNLUK_CEZA = 1.0;

    private static final int ODUNC_SURESI = 7;

    private int sayiNo;

    private String yayinAyi;
/**getsayino ile getter eklendi */
    public int getSayiNo() {
        return sayiNo;
    }
/**sayiNo kontrol ile setter yazıldı*/
    public void setSayiNo(int sayiNo) {
        if(sayiNo<= 0){
            throw new GecersizVeriException("sayiNo 0 dan küçük olamaz");
        }
        else{
            this.sayiNo = sayiNo;
        }
    }
/**yayinayi getter yazıldı*/
    public String getYayinAyi() {
        return yayinAyi;
    }
/**yayınayi setter yazıldı*/
    public void setYayinAyi(String yayinAyi) {
        if(Dogrulayici.bosMu(yayinAyi)){
            throw new GecersizVeriException("yayın yılı boş olamaz");
        }
        else{
            this.yayinAyi = yayinAyi.trim();
        }
    }
    /**8n parametreli yapıcı yazıldı @param*/
    public Dergi(int kaynakNo, String baslik, String yazar, int yayinYili, int toplamAdet, int mevcutAdet, int sayiNo, String yayinAyi){
        super(kaynakNo, baslik, yazar, yayinYili, toplamAdet, mevcutAdet);
        setSayiNo(sayiNo);
        setYayinAyi(yayinAyi);
    }
    /**6 parametreli yapıcı çağrıldı @param*/
    public Dergi(String baslik, String yazar, int yayinYili, int toplamAdet, int sayiNo, String yayinAyi){
        super(baslik, yazar, yayinYili, toplamAdet);
        setSayiNo(sayiNo);
        setYayinAyi(yayinAyi);
    }
    /**override edildi ve kullanıldı*/
    @Override
    public String kaynakTuru(){
        return "Dergi";
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
        return super.toString() + " | Tür: Dergi | Sayı: " + sayiNo + " | Ay: " + yayinAyi;
    }
}