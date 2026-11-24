package service;

import model.*;
import util.DosyaIslemleri;
import util.TarihYardimcisi;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Singleton tasarım deseni ile uygulama boyunca bellekte tek bir örnek üzerinden çalışan merkezi veri deposu sınıfıdır.
 * Hem servislerin hem de arka plandaki threadlerin aynı koleksiyonlar üzerinde çalışmasını sağlar ve .txt dosyaları ile  veri kalıcılığını yönetir.
 */
public class KutuphaneDeposu{
    private static KutuphaneDeposu ornek;
    private KutuphaneDeposu(){ }

    /**
     * getInstance(), KutuphaneDeposu sınıfının tek örneğini döndürür, ilk çağrılan nesneyi oluşturur.
     * Çoklu thread ortamında iki threadin aynı anda nesne üretmesini önlemek için synchronized tanımlıdır.
     * @return (uygulama genelindeki tek KutuphaneDeposu örneği)
     */
    public static synchronized KutuphaneDeposu getInstance(){
        if(ornek == null){
            ornek = new KutuphaneDeposu();
        }
        return ornek;
    }

    public static final double[][] CEZA_TARIFESI = new double[2][3];
    static{
        float[] uyeCarpani = { 1.0f, 0.5f };
        double[] kaynakTaban = { 2.0, 3.0, 1.0 };
        for (int i = 0; i < uyeCarpani.length; i++) {
            for (int j = 0; j < kaynakTaban.length; j++) {
                CEZA_TARIFESI[i][j] = uyeCarpani[i] * kaynakTaban[j];
            }
        }
    }
    private final Katalog<Kaynak> kaynaklar = new Katalog<>();
    /** Kayıtlı tüm kaynakları tutan kataloğu döndürür. @return Kaynak kataloğu*/
    public Katalog<Kaynak> getKaynaklar() { return kaynaklar; }
    private final Map<Integer, Uye> uyeler = new HashMap<>();
    /** Üye mapini döndürür @return Üye mapi */
    public Map<Integer, Uye> getUyeler() { return uyeler; }
    private final List<Odunc> oduncler = new ArrayList<>();
    /** Ödünç listesini döndürür. @return Ödünç listesi*/
    public List<Odunc> getOduncler() { return oduncler; }
    private final Map<Integer, List<Odunc>> uyeOduncleri = new HashMap<>();
    /** Üyelerin ödünç haritasını döndürür @return Üye-Ödünç haritası */
    public Map<Integer, List<Odunc>> getUyeOduncleri() { return uyeOduncleri; }
    private final Map<Integer, Integer> acikOduncSayilari = new HashMap<>();
    /** Açık ödünç sayılarının haritasını döndürür @return Açık ödünç haritası */
    public Map<Integer, Integer> getAcikOduncSayilari() { return acikOduncSayilari; }

    /**
     * Verilen üyenin üzerindeki aktif ödünç sayısını döndürür
     * @param uyeNo (ödünç sayısı döndürülecek üye numarası)
     * @return (ödünç sayısı, yoksa 0)
     */
    public int acikOduncSayisi(int uyeNo) { return acikOduncSayilari.getOrDefault(uyeNo, 0); }
    private final Set<Kaynak> populerKaynaklar = new HashSet<>();
    /** popüler kaynak koleksiyonunu döndürür @return Popüler kaynak koleksiyonu */
    public Set<Kaynak> getPopulerKaynaklar() { return populerKaynaklar; }
    private final TreeMap<LocalDate, List<Odunc>> teslimTakvimi = new TreeMap<>();
    /** Teslim takvimi ağacı haritasını döndürür @return Teslim takvimi ağacı haritası */
    public TreeMap<LocalDate, List<Odunc>> getTeslimTakvimi() {return teslimTakvimi; }
    private final Queue<Uye> beklemeKuyrugu = new LinkedList<>();
    /** Bekleme kuyruğu koleksiyonu döndürür. @return Bekleme kuyruğu koleksiyonu */
    public Queue<Uye> getBeklemeKuyrugu() { return  beklemeKuyrugu; }
    private int sonKaynakId = 0, sonUyeId = 0;

    /** İlgili sayacı 1 artırıp yeni benzersiz kaynak ID üretir @return Benzersiz ID*/
    public int yeniKaynakId(){
        return ++sonKaynakId;
    }
    /** İlgili sayacı 1 artırıp yeni benzersiz üye ID üretir @return Benzersiz ID*/
    public int yeniUyeId(){
        return ++sonUyeId;
    }

    /**
     * Uygulama açılışında data/ klasöründeki kaynaklar.txt (BufferedReader ile), uyeler.txt (Scanner ile) ve oduncler.txt (BufferedReader ile) dosyalarını sırasıyla okuyarak bellek içi koleksiyonları doldurur.
     * Ödünç kayıtları kaynak ve üye nesnelerine bağlandığı için yükleme sırası önemlidir.
     * En büyük numaralara göre Kaynak.sayaciAyarla ve Uye.sayaciAyarla metotlarının güncellendir.
     * Henüz iade edilmemiş kayıtların teslimTakvimi ile acikOduncSayilari yapılarına aktarılır.
     * @throws IOException (Veri dosyaları okunurken olası Input/Output hatalarında fırlatılır)
     */
    public void verileriYukle() throws IOException {
        for(String[] p : DosyaIslemleri.satirlariOku("kaynaklar.txt")){
            Kaynak k = satirdanKaynak(p);
            if (k != null) {
                kaynaklar.ekle(k);
                sonKaynakId = Math.max(sonKaynakId, k.getKaynakNo());
            }
        }
        Kaynak.sayaciAyarla(sonKaynakId);

        for(String[] p : DosyaIslemleri.scannerIleOku("uyeler.txt")){
            int uyeNo = Integer.parseInt(p[1]);
            String ad = p[2], soyad = p[3], eposta = p[4];
            long telefon = Long.parseLong(p[5]);
            double borc = Double.parseDouble(p[6]);
            Uye u = null;
            if(p[0].equals("O")){
                u = new OgrenciUye(uyeNo, ad, soyad, eposta, telefon, borc, p[7], Integer.parseInt(p[8]));
            }
            else if (p[0].equals("A")){
                u = new AkademisyenUye(uyeNo, ad, soyad, eposta, telefon, borc, p[7], p[8]);
            }
            if (u != null){
                uyeler.put(u.getUyeNo(), u);
                uyeOduncleri.put(u.getUyeNo(), new ArrayList<>());
                acikOduncSayilari.putIfAbsent(u.getUyeNo(), 0);
                sonUyeId = Math.max(sonUyeId, u.getUyeNo());
            }
        }
        Uye.sayaciAyarla(sonUyeId);

        for(String[] p : DosyaIslemleri.satirlariOku("oduncler.txt")){
            Kaynak kaynak = kaynaklar.bul(Integer.parseInt(p[0]));
            Uye uye = uyeler.get(Integer.parseInt(p[1]));
            LocalDate alisTarihi = TarihYardimcisi.dosyadanOku(p[2]);
            LocalDate sonTeslimTarihi = TarihYardimcisi.dosyadanOku(p[3]);
            LocalDate iadeTarihi = TarihYardimcisi.dosyadanOku(p[4]);
            double odenenCeza = Double.parseDouble(p[5]);
            if (kaynak != null && uye != null) {
                Odunc o = new Odunc(kaynak, uye, alisTarihi, sonTeslimTarihi, iadeTarihi, odenenCeza);
                oduncler.add(o);
                uyeOduncleri.get(uye.getUyeNo()).add(o);
                if (!o.iadeEdildiMi()){
                    teslimTakvimi.computeIfAbsent(sonTeslimTarihi, k -> new ArrayList<>()).add(o);
                    acikOduncSayilari.replace(uye.getUyeNo(), acikOduncSayisi(uye.getUyeNo()) + 1);
                }
            }
        }
    }

    /**
     * Bellekteki güncel kaynak, üye ve ödünç kayıtlarını ; ayırıcılı metin satırlarına dönüştürerek kaynaklar.txt, uyeler.txt, oduncler.txt dosyalarına yazar.
     * Alt sınıfları ayırt etmek için instanceof kalıp eşleştirme yaptık.
     * @throws IOException (Dosya yazma işlemlerinde olası Input/Output hatalarında fırlatılır.)
     */
    public void verileriKaydet() throws IOException {
        List<String> kaynakSatirlari = new ArrayList<>();
        for(Kaynak k : kaynaklar.getKayitlar()){
            String temel = k.getKaynakNo() + ";" + k.getBaslik() + ";" + k.getYazar() + ";"
                    + k.getYayinYili() + ";" + k.getToplamAdet() + ";" + k.getMevcutAdet();
            if (k instanceof DersKitabi dk) {
                kaynakSatirlari.add("DERSKITABI;" + temel + ";" + dk.getSayfaSayisi() + ";" + dk.getIsbn() + ";" + dk.getDersKodu() + ";" + dk.getDonem());
            } else if (k instanceof Kitap kitap) {
                kaynakSatirlari.add("KITAP;" + temel + ";" + kitap.getSayfaSayisi() + ";" + kitap.getIsbn() + ";-;-");
            } else if (k instanceof Dergi dergi) {
                kaynakSatirlari.add("DERGI;" + temel + ";" + dergi.getSayiNo() + ";" + dergi.getYayinAyi() + ";-;-");
            }
        }
        DosyaIslemleri.satirlariYaz("kaynaklar.txt", kaynakSatirlari);

        List<String> uyeSatirlari = new ArrayList<>();
        for(Uye u : uyeler.values()) {
            String temel = u.getUyeNo() + ";" + u.getAd() + ";" + u.getSoyad() + ";"
                    + u.getEposta() + ";" + u.getTelefon() + ";" + u.getBorc();
            if (u instanceof OgrenciUye ogr) {
                uyeSatirlari.add("O;" + temel + ";" + ogr.getBolum() + ";" + ogr.getSinif());
            } else if (u instanceof AkademisyenUye aka) {
                uyeSatirlari.add("A;" + temel + ";" + aka.getUnvan() + ";" + aka.getBirim());
            }
        }
        DosyaIslemleri.satirlariYaz("uyeler.txt", uyeSatirlari);

        List<String> odunclerSatirlari = new ArrayList<>();
        for(Odunc o : oduncler){
            String alisStr = TarihYardimcisi.dosyayaYaz(o.getAlisTarihi());
            String sonTeslimStr = TarihYardimcisi.dosyayaYaz(o.getSonTeslimTarihi());
            String iadeStr = TarihYardimcisi.dosyayaYaz(o.getIadeTarihi());
            odunclerSatirlari.add(o.getKaynak().getKaynakNo() + ";" + o.getUye().getUyeNo() + ";" + alisStr + ";" + sonTeslimStr + ";" + iadeStr + ";" + o.getCeza());
        }
        DosyaIslemleri.satirlariYaz("oduncler.txt", odunclerSatirlari);
    }

    /**
     *kaynaklar.txt dosyasından ; ile bölünmüş satır dizisini p[0] tür koduna (KITAP, DERSKITABI, DERGI) göre switch ifadesiyle değerlendirip çok biçimlilikle ilgili Kaynak alt sınıfı nesnesini üreten yardımcı metottur.
     * @param p (dosya satırından ayrıştırılmış sütun dizisi)
     * @return (oluşturulan Kaynak alt sınıfı nesnesi, geçersiz türse null)
     */
    private Kaynak satirdanKaynak(String[] p){
        int id = Integer.parseInt(p[1]);
        String baslik = p[2];
        String yazar = p[3];
        int yil = Integer.parseInt(p[4]);
        int toplamAdet = Integer.parseInt(p[5]);
        int mevcutAdet = Integer.parseInt(p[6]);
        return switch (p[0]) {
            case "KITAP" -> new Kitap(id, baslik, yazar, yil, toplamAdet, mevcutAdet, Integer.parseInt(p[7]), p[8]);
            case "DERSKITABI" -> new DersKitabi(id, baslik, yazar, yil, toplamAdet, mevcutAdet, Integer.parseInt(p[7]), p[8], p[9], Byte.parseByte(p[10]));
            case "DERGI" -> new Dergi(id, baslik, yazar, yil, toplamAdet, mevcutAdet, Integer.parseInt(p[7]), p[8]);
            default -> null;
        };
    }
}