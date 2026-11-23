package model;
import exception.GecersizVeriException;

import exception.KaynakMevcutDegilException;

import util.Dogrulayici;

import java.time.LocalDate ;

import java.time.temporal.ChronoUnit;

import java.util.Comparator;

/**  Sınıf açıldı ve içi dolduruldu @author */

public abstract class Kaynak implements OduncVerilebilir, Ucretlendirilebilir, Comparable<Kaynak>{

    public static final Comparator<Kaynak> YIL_SIRALI = Comparator.comparingInt(Kaynak::getYayinYili);
    public static final Comparator<Kaynak> YAZAR_SIRALI = Comparator.comparing(Kaynak::getYazar, String.CASE_INSENSITIVE_ORDER);
    public abstract String kaynakTuru();

    public abstract int maxOduncSuresi();
    /**mevcuMu override edildi ve kullnıldı*/
    @Override
    public boolean mevcutMu(){
    return mevcutAdet>0 && !rezerveMi;
    }
    /**oduncver synchronized ile beraber override edildi */
    @Override
    public synchronized void oduncVer() throws KaynakMevcutDegilException{
        if(!mevcutMu()){
            throw new KaynakMevcutDegilException("Kaynak ödünç almaya uygun değil.");
        }
        else{
            this.mevcutAdet--;
            durumGuncelle();
        }
    }
    /**bu şekilde @throws ile birlikte tanımlandı ve kullanıldı*/
    public void oduncVerGuvensiz() throws KaynakMevcutDegilException{
        if(mevcutAdet<=0){
            throw new KaynakMevcutDegilException("Stokta kaynak kalmadı.");
        }
        else{
            try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            this.mevcutAdet--;
            durumGuncelle();
        }
    }
/**iadeAl metodu override edildi ve kullanıldı*/
    @Override
    public void iadeAl(){
        if(mevcutAdet<toplamAdet){
            this.mevcutAdet++;
            durumGuncelle();
        }
    }
    /** gecikme gunune gore ceza tutarini hesaplar */
    public double cezaHesapla(int gecikmeGunu){
        if(gecikmeGunu <= 0){
            return 0.0;
        }
        else{
            return gecikmeGunu * getGunlukCeza();
        }
    }

    /** son teslim tarihine gore ceza olusup olusmadigini kontrol eder */
    @Override
    public boolean cezaVarMi(LocalDate sonTeslim, LocalDate bugun){
        return cezaHesapla(sonTeslim, bugun) > 0;
    }
    public double cezaHesapla(LocalDate teslimTarihi, LocalDate iadeTarihi){
        long gun = ChronoUnit.DAYS.between(teslimTarihi, iadeTarihi);
        return cezaHesapla((int) gun);
    }
    public void bilgiYazdir(){
        System.out.println(toString());
    }

    public void bilgiYazdir(boolean detayli){
        if (detayli == true) {
            System.out.println(toString() + " | Mevcut: " + mevcutAdet + "/" + toplamAdet + " | Durum: " + durum);
        }
        else{
            bilgiYazdir();
        }
    }
/**bu ksımda equals metodu gerekli şekilde ezildi */
    @Override
    public boolean equals (Object o){
        if(this == o){
            return true;
        }
        else if (o == null || getClass() != o.getClass()){
            return false;
        }
        Kaynak diger = (Kaynak) o;
        return this.kaynakNo == diger.kaynakNo;
    }

    /**kendi kullanımımız için toString metodu ezildi */
    @Override
    public String toString(){
        return "[" + kaynakNo + "] " + baslik + " - " + yazar + " (" + yayinYili + ")" ;
    }
/**karşılaştırmaları daha düzgün yapmak için compareto ezildi (override)*/
    @Override
    public int compareTo(Kaynak diger){
        return this.baslik.compareToIgnoreCase(diger.baslik);
    }

    /**equals override edilidği için hashCode da override edildi */
    @Override
    public int hashCode(){
        return Integer.hashCode(kaynakNo);
    }

    private static int sonNo = 0;
    private static int toplamKaynakSayisi = 0;
    public static final int MIN_YIL = 1450;
    public static final int MAKS_BASLIK_UZUNLUGU = 120;

    private final int kaynakNo;
    private String baslik;
    private String yazar;
    private short yayinYili;
    private int toplamAdet;
    private int mevcutAdet;
    private KaynakDurumu durum;
    private boolean rezerveMi;

    /**kaynak no okunması için getter yazıldı*/
    public int getKaynakNo() {
        return kaynakNo;
    }
/**baslik için getter eklendi*/
    public String getBaslik() {
        return baslik;
    }
/**yazar için getter eklendi*/
    public String getYazar() {
        return yazar;
    }
/**yayinyili için getter eklendş ve kullanıldı*/
    public short getYayinYili() {
        return yayinYili;
    }
/**toplam adet için getter metodu ekledni ve toplamAdet döndürmesi sağlandı*/
    public int getToplamAdet() {
        return toplamAdet;
    }
/**mevcutadet için getter eklendi*/
    public int getMevcutAdet() {
        return mevcutAdet;
    }

    /**kaynakdurumu için getter eklendi*/

    public KaynakDurumu getDurum() {
        return durum;
    }
/**izrezervemi eklendi */
    public boolean isRezerveMi() {
        return rezerveMi;
    }

    void durumGuncelle (){
        if(mevcutAdet == 0 ){
            this.durum = KaynakDurumu.ODUNC_VERILDI;
        }
        else if (rezerveMi) {
            this.durum = KaynakDurumu.REZERVE;
        }
        else{
            this.durum = KaynakDurumu.MEVCUT;
        }
    }
/**baslik için kontroller ile birlikte setter eklendi*/
    public void setBaslik(String baslik) {
        if(Dogrulayici.bosMu(baslik) || baslik.trim().length()> MAKS_BASLIK_UZUNLUGU){
            throw new GecersizVeriException("Başlık 120 karakterden uzun ya da boş olamaz");
        }
        else{
            this.baslik = baslik;
        }
    }
/**yazar için setter eklendi ve gerekli kontroller yapıldı hata için mesaj @throws eklendi*/
    public void setYazar(String yazar) {
        if(Dogrulayici.bosMu(yazar)){
            throw new GecersizVeriException("Yazar boş olamaz");
        }
        else{
            this.yazar = yazar;
        }
    }
/**yayinyili için setter eklendi gerekli kontrolller ile birlikte */
    public void setYayinYili(int yayinYili) {
            if(yayinYili < MIN_YIL || yayinYili > LocalDate.now().getYear()){
                throw new GecersizVeriException("yayın yılı Min yıldan küçük ve şimdiki zamandan büyük olamaz");
            }
            else{
                this.yayinYili = (short) yayinYili;
            }
    }
/**mevcutadet için setter ekledni*/
    public void setMevcutAdet(int mevcutAdet) {
        if(mevcutAdet < 0 || mevcutAdet > this.toplamAdet){
            throw new GecersizVeriException("Mevcut adet 0 dan küçük ve toplam adetten de büyük olamaz");
        }
        else{
            this.mevcutAdet = mevcutAdet;
            durumGuncelle();
        }
    }
/**rezerve kontrolü için rezervemi eklendi*/
    public void setRezerveMi(boolean rezerveMi) {
        this.rezerveMi = rezerveMi;
        durumGuncelle();
    }
/**kaynakdurumu için setter eklendi*/
    public void setDurum(KaynakDurumu durum) {
        if(durum == null){
            throw new GecersizVeriException("Durum null olamaz");
        }
        else{
            this.durum = durum;
        }
    }
/**toplamader için setter yazıldı */
    public void setToplamAdet(int toplamAdet) {
        if(toplamAdet<1 || toplamAdet < this.mevcutAdet){
            throw new GecersizVeriException("Toplam Adet 1 den Küçük Olamaz");
        }
        else{
            this.toplamAdet = toplamAdet;
        }
    }



    /** sayacayarla yazıdlı ev kontrol yapıldı*/
    public static void sayaciAyarla(int no){
        if(no > sonNo){
            sonNo = no;
        }
    }
    /** getToplamKyankSayisi yazıldı ve atamasını yaptık*/
    public static int getToplamKaynakSayisi(){
        return toplamKaynakSayisi;
    }
/**protected şeklinde Kaynak sınıfının yapıcısı (6 parametreli @param) yazıldı ve atamaları gerçekleştirildi*/
    protected Kaynak(int kaynakNo, String baslik, String yazar, int yayinYili, int toplamAdet, int mevcutAdet){
        this.kaynakNo = kaynakNo;
        setBaslik(baslik);
        setYazar(yazar);
        setYayinYili(yayinYili);
        setToplamAdet(toplamAdet);
        setMevcutAdet(mevcutAdet);
        this.rezerveMi = false;
        sayaciAyarla(kaynakNo);
        toplamKaynakSayisi++;
    }
/**4 parametreli yapıcı olan Kaynak sınıfının yapıcısı yazıldı ve atamaları yapıldı */
    protected Kaynak(String baslik, String yazar, int yayinYili, int toplamAdet){
        this(++sonNo, baslik, yazar, yayinYili, toplamAdet, toplamAdet);
    }

}