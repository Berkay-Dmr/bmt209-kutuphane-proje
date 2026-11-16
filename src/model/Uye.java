package model;
import exception.GecersizVeriException;
import util.Dogrulayici;
// sırası ile egrekli olan sınıfları import ettik ve paketi tanımladık
public abstract class Uye{

    private static int  sonUyeNo = 0 ;
    private final int uyeNo;

    private final char uyeTuruKodu ;
    private String ad;
    private String soyad ;
     private String eposta;
     private long telefon;
     private double borc;
     private float gecikmeCarpani ;
     private boolean aktifMi; // gerekli olan alanları verdik ve mümkün olanlara ilk değerleri verildi

    public char getUyeTuruKodu() {
        return uyeTuruKodu;
    }

    public double getBorc() {
        return borc;
    }
    public float getGecikmeCarpani() {
        return gecikmeCarpani;
    }


    public int getUyeNo() {
        return uyeNo;
    }

    public long getTelefon() {
        return telefon;
    }

    public String getAd() {
        return ad;
    }

    public String getEposta() {
        return eposta;
    }

    public String getSoyad() {
        return soyad;
    }

    public boolean isAktifMi(){
        return aktifMi;
    }

    public void setAd(String ad) {

        if(Dogrulayici.bosMu(ad)){
            throw  new GecersizVeriException("isim Boş olamaz");
        }
        else{
            this.ad = ad;
        }
    }

    public void setSoyad(String soyad) {
        if(Dogrulayici.bosMu(soyad)){
            throw new GecersizVeriException("Soyad Boş olamaz");
        }
        else{
            this.soyad = soyad;
        }
    }

    public void setAktifMi(boolean aktifMi) {
        this.aktifMi = aktifMi;
    }

    public void setEposta(String eposta) {
        if(!Dogrulayici.epostaGecerliMi(eposta)){
            throw new GecersizVeriException("Geçersiz e-posta adresi");
        }
        else{
            this.eposta =eposta;
        }
    }
    // sırası ile getter ve setter metotları yazdık ve bu metotlarda gerekenelre if koşulu ile atama yaptık
    protected Uye (int uyeNo, String ad, String soyad, String eposta, long telefon, double borc, char uyeTuruKodu, float gecikmeCarpani) {
         this.uyeNo = uyeNo;
         setAd(ad);
         setSoyad(soyad);
         setEposta(eposta);
         this.telefon = telefon;
         this.uyeTuruKodu = uyeTuruKodu;
         this.gecikmeCarpani = gecikmeCarpani;

         if(borc<0){
             throw new GecersizVeriException("Borç negatif olamaz !! ");
         }
         else {
             this.borc = borc;
         }
         this.aktifMi = true;
         if(uyeNo > sonUyeNo){
             sonUyeNo = uyeNo;
         }


// 8 parametreli yapıcıyı yazdık ve içindeki gerekli atamaları yaptık
     }

    protected Uye(String ad, String soyad, String eposta, long telefon, double borc, char uyeTuruKodu, float gecikmeCarpani){
        this( ++sonUyeNo ,  ad , soyad,  eposta , telefon , borc ,  uyeTuruKodu , gecikmeCarpani);

    }//burada da 7 parametreli yapıcıyı yaptık

    public void borcEkle(double tutar){
        if(tutar<= 0){
            throw new GecersizVeriException("tutar sıfırdan küçük ya da eşit olamaz");
        }
        else {
            this.borc+=tutar; // sırası ile metotları ekledik ve konrol koyduk
        }
    }

    public void borcOde(double tutar){
        if(tutar <= 0 || tutar > this.borc){
            throw new GecersizVeriException("tutar sıfırdan küçük ya da eşit olamaz");
        }
        else{
            this.borc -= tutar;
        }
    }

    public abstract int getMaxOduncLimiti(); // soyut metıt yazıldı

    @Override
    public String toString(){
        return "üye no : " + uyeNo + " Ad : " + ad + " Soyad " + soyad + " Borç : " + borc ; // sırası ile toString metodu Override edildi
    }

}