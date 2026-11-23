package model;
import exception.GecersizVeriException ;
import util.Dogrulayici;

/**ana sınıfımızı açtık ve kaynaktan miras aldık */
public class Kitap extends Kaynak{
    private static final double GUNLUK_CEZA = 2.5;

    private static final int ODUNC_SURESI = 15;

    private int sayfaSayisi;

    private String isbn;
/**get sayfasayisi yazıldı*/
    public int getSayfaSayisi() {
        return sayfaSayisi;
    }
    /**setter yazıldı ve gerekli kontroller yapıldı*/
    public void setSayfaSayisi(int sayfaSayisi){
        if(sayfaSayisi<=0){
            throw new GecersizVeriException("Sayfa sayısı sıfırdan az olamaz");
        }
        else{
            this.sayfaSayisi = sayfaSayisi;
        }
    }

    /**İsbn kontrolü yapıldı*/
    public String getIsbn() {
        return isbn;
    }
/**atama için setetr yazıldı ve kullanıldı*/
    public void setIsbn(String isbn) {
        if(Dogrulayici.bosMu(isbn)){
            throw new GecersizVeriException("isbn boş olamaz");
        }
        else{
            this.isbn= isbn.trim();
        }
    }
    /**Kitap sınıfı  için yapıcı yazıldı ve super() ile de üst sınıfın Kaynak yapıcısına erişildi*/
    public Kitap(int kaynakNo, String baslik, String yazar, int yayinYili, int toplamAdet, int mevcutAdet, int sayfaSayisi, String isbn){
        super(kaynakNo, baslik, yazar, yayinYili, toplamAdet, mevcutAdet);
        setSayfaSayisi(sayfaSayisi);
        setIsbn(isbn);
    }
    /**aynı yapıcı ama bu seferki 6 parametreli*/
    public Kitap(String baslik, String yazar, int yayinYili, int toplamAdet, int sayfaSayisi, String isbn){
        super(baslik, yazar, yayinYili, toplamAdet);
        setIsbn(isbn);
        setSayfaSayisi(sayfaSayisi);
    }
    /**override edildi ve kullanıldı*/
    @Override
    public String kaynakTuru(){
        return "Kitap";
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
        return super.toString() + " | Tür: Kitap | Sayfa: " + sayfaSayisi + " | ISBN: " + isbn;
    }
}