package model;
import exception.GecersizVeriException;
import util.Dogrulayici;
// sırası ile egrekli olan sınıfları import ettik ve paketi tanımladık
public abstract class Uye{

    private static int sonNo = 0 ;
    public static final int MAKS_AD_UZUNLUGU = 40;


    public void bilgiYazdir(){
        System.out.println(toString());
    }
    @Override
    public boolean equals (Object o){
        if(this == o){
            return true;
        }
        else if (o == null || getClass() != o.getClass()){
            return false;
        }
        Uye diger = (Uye) o;
        return this.uyeNo == diger.uyeNo;
    }

    @Override
    public int hashCode(){
        return Integer.hashCode(uyeNo);
    }
    public static void sayaciAyarla(int enBuyukNo){
        if(enBuyukNo > sonNo){
            sonNo = enBuyukNo;
        }
    }
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
    /** bu kısmda @throws ile beraber hata fırlattık */

    public void setAd(String ad) {

        if (Dogrulayici.bosMu(ad) || ad.trim().length() > MAKS_AD_UZUNLUGU){
            throw new GecersizVeriException("İsim boş veya 40 karakterden uzun olamaz");
        }
        else{
            this.ad = ad;
        }
    }

    public void setTelefon(long telefon) {
        this.telefon = telefon;
    }

    /** bu kısımda da yukarıda yaptığımız gibi @throws ile bereaber hatayı fırlattık*/
    public void setBorc(double borc) {
        if(borc<0){
            throw new GecersizVeriException("Borç eksi olamaz...");
        }
        else{
            this.borc = borc;
        }
    }

    public void setGecikmeCarpani(float gecikmeCarpani) {
        this.gecikmeCarpani = gecikmeCarpani;
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

         setBorc(borc);
         this.aktifMi = true;
         sayaciAyarla(uyeNo);


// 8 parametreli yapıcıyı yazdık ve içindeki gerekli atamaları yaptık
     }

    protected Uye(String ad, String soyad, String eposta, long telefon, double borc, char uyeTuruKodu, float gecikmeCarpani){
        this( ++sonNo,  ad , soyad,  eposta , telefon , borc ,  uyeTuruKodu , gecikmeCarpani);

    }//burada da 7 parametreli yapıcıyı yaptık

    public void borcEkle(double tutar){
        if(tutar<= 0){
            throw new GecersizVeriException("tutar sıfırdan küçük ya da eşit olamaz");
        }
        else {
            this.borc+=tutar; // sırası ile metotları ekledik ve konrol koyduk
        }
    }

    public void borcode(double tutar){
        if(tutar <= 0 || tutar > this.borc){
            throw new GecersizVeriException("tutar sıfırdan küçük ya da eşit olamaz");
        }
        else{
            this.borc -= tutar;
        }
    }

    /** Üyenin aynı anda ödünç alabileceği maksimum kaynak sayısını döndürür. */
    public abstract int maxOduncAdedi();

    /** Üyenin bir kaynağı ödünç alabileceği maksimum gün sayısını döndürmesi sağlandı.. */
    public abstract int oduncSuresiGun();

    /** to string override ettik ve kullanılacak şekilde ayarladnı*/
    @Override
    public String toString(){
        return "üye no : " + uyeNo + " Ad : " + ad + " Soyad " + soyad + " Borç : " + borc ; // sırası ile toString metodu Override edildi
    }

}