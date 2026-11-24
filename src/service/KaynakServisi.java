package service;
import model.Kaynak;
import model.Odunc;
import exception.GecersizVeriException;
import exception.KayitBulunamadiException;
import java.util.*;

/**
 * Kaynakların eklenmesi, silinmesi, numaraya göre bulunması, sıralanması ve aşırı yüklenmiş listele/ara metotlarıyla aranması işlemlerini,
 * Singleton KutuphaneDeposu kataloğu üzerinden yürüten servis sınıfıdır.
 */
public class KaynakServisi {
    private final KutuphaneDeposu depo = KutuphaneDeposu.getInstance();

    /**
     * Kaynak null ise veya aynı kaynakNo katalogda zaten varsa GecersizVeriException fırlatır.
     * Eğer yoksa kataloğa kaydeder.
     * @param kaynak (girilen kaynak)
     * @throws GecersizVeriException (Katalogda varolan veya null girilen kaynakta fırlatılır)
     */
    public void kaynakEkle(Kaynak kaynak) throws GecersizVeriException{
        if(kaynak == null){
            throw new GecersizVeriException("Gecersiz veya mevcut ID'li kaynak!");
        }
        for(Kaynak k : depo.getKaynaklar().getKayitlar()){
            if(k.getKaynakNo() == kaynak.getKaynakNo()){
                throw new GecersizVeriException("Gecersiz veya mevcut ID'li kaynak!");
            }
        }
        depo.getKaynaklar().ekle(kaynak);
    }

    /**
     * Verilen ID'ye göre katalogda arama yaptığını, sonuç null ise KayitBulunamadiException fırlatır.
     * Eğer varolan ID ise kaynağı döndürür
     * @param id (aranacak kaynak IDsi)
     * @return (Bulunan kaynak)
     * @throws KayitBulunamadiException (Kayıt bulunamazsa fırlatılır)
     */
    public Kaynak idIleGetir(int id) throws KayitBulunamadiException{
        Kaynak k = depo.getKaynaklar().bul(id);
        if (k == null){
            throw new KayitBulunamadiException("ID: " + id + " olan kaynak bulunamadi!");
        }
        else{
            return k;
        }
    }

    /**OVERLOAD - 1
     * Orijinal listeyi bozmamak için kopya ArrayList oluşturup Collections.sort ile doğal sıraya (başlığa göre alfabetik) göre sıralar.
     * @return (sıralı listeyi döndürür.)
     */
    public List<Kaynak> listele(){
        List<Kaynak> kopya = new ArrayList<>(depo.getKaynaklar().getKayitlar());
        Collections.sort(kopya);
        return kopya;
    }

    /**OVERLOAD - 2
     * Kopya listeyi parametre olarak verilen Comparator ölçütüne göre sıralar.
     * @param olcut (YIL_SIRALI, YAZAR_SIRALI ölçütüne göre sıralama yapmak için girilen liste)
     * @return (ölçüte uygun yeni kopya listeyi döndürür)
     */
    public List<Kaynak> listele(Comparator<Kaynak> olcut){
        List<Kaynak> kopya = new ArrayList<>(depo.getKaynaklar().getKayitlar());
        kopya.sort(olcut);
        return kopya;
    }

    /**OVERLOAD - 1
     * Başlık veya Yazar anahtar kelimesine göre arama yapar
     * @param anahtar (Başlık/Yazar anahtar kelimesi)
     * @return (filtrelenmiş listeyi döndürür)
     */
    public List<Kaynak> ara(String anahtar){
        List<Kaynak> sonuc = new ArrayList<>();
        if(anahtar == null || anahtar.isBlank()){
            return sonuc;
        }
        String aranan = anahtar.toLowerCase(Locale.ROOT).trim();
        for(Kaynak k : depo.getKaynaklar().getKayitlar()){
            if(k.getBaslik().toLowerCase(Locale.ROOT).contains(aranan) || k.getYazar().toLowerCase(Locale.ROOT).contains(aranan)){
                sonuc.add(k);
            }
        }
        return sonuc;
    }

    /**OVERLOAD - 2
     * Yıla göre arama yapar.
     * @param yil (Girilen yıl)
     * @return (yıla göre filtrelenmiş liste)
     */
    public List<Kaynak> ara(int yil){
        List<Kaynak> sonuc = new ArrayList<>();
        for(Kaynak k : depo.getKaynaklar().getKayitlar()){
            if(k.getYayinYili() == yil){
                sonuc.add(k);
            }
        }
        return sonuc;
    }

    /**OVERLOAD - 3
     * Yazar ve yıla göre arama yapar. Her 3 aşırı yüklemede de büyük/küçük harf duyarsızlığına (Locale.ROOT) göre bakılır.
     * @param yazar (girilen yazar anahtar kelimesi)
     * @param yil (girilen yıl)
     * @return (filtrelenen sonuç listesi)
     */
    public List<Kaynak> ara(String yazar, int yil){
        List<Kaynak> sonuc = new ArrayList<>();
        if(yazar == null || yazar.isBlank()){
            return sonuc;
        }
        String aranan = yazar.toLowerCase(Locale.ROOT).trim();
        for(Kaynak k : depo.getKaynaklar().getKayitlar()){
            if(k.getYazar().toLowerCase(Locale.ROOT).contains(aranan) && k.getYayinYili() == yil){
                sonuc.add(k);
            }
        }
        return sonuc;
    }

    /**
     *Yalnızca başlığa göre arama yapar. Locale.ROOT ile büyük/küçük harf duyarsızlığına göre filtrelenir.
     * @param kelime (başlık anahtar kelimesi)
     * @return (filtrelenmiş sonuç listesi)
     */
    public List<Kaynak> basligaGoreAra(String kelime){
        List<Kaynak> sonuc = new ArrayList<>();
        if(kelime == null || kelime.isBlank()){
            return sonuc;
        }
        String aranan = kelime.toLowerCase(Locale.ROOT).trim();
        for(Kaynak k : depo.getKaynaklar().getKayitlar()){
            if(k.getBaslik().toLowerCase(Locale.ROOT).contains(aranan)){
                sonuc.add(k);
            }
        }
        return sonuc;
    }

    /**
     * Yalnızca yazara göre arama yapar. Locale.ROOT ile büyük/küçük harf duyarsızlığına göre filtrelenir.
     * @param kelime (yazar adı anahtar kelimesi)
     * @return (filtrelenmiş sonuç listesi)
     */
    public List<Kaynak> yazaraGoreAra(String kelime){
        List<Kaynak> sonuc = new ArrayList<>();
        if(kelime == null || kelime.isBlank()){
            return sonuc;
        }
        String aranan = kelime.toLowerCase(Locale.ROOT).trim();
        for(Kaynak k : depo.getKaynaklar().getKayitlar()){
            if(k.getYazar().toLowerCase(Locale.ROOT).contains(aranan)){
                sonuc.add(k);
            }
        }
        return sonuc;
    }

    /**
     * Kaynağın varlığını doğruladıktan sonra aktif ödünç kayıtlarını kontrol eder, henüz iade edilmemiş bir kopyası varsa GecersizVeriException fırlatır.
     * İade edilmemiş kopyası yoksa katalogdan silinir.
     * @param id (girilen kaynak ID)
     * @throws KayitBulunamadiException (ID ile getirilemezse fırlatılır)
     * @throws GecersizVeriException (Ödünçte olan kaynak varsa fırlatılır)
     */
    public void kaynakSil(int id) throws KayitBulunamadiException, GecersizVeriException{
        idIleGetir(id);
        for(Odunc o : depo.getOduncler()){
            if(o.getKaynak().getKaynakNo() == id && !o.iadeEdildiMi()){
                throw new GecersizVeriException("Oduncte olan kaynak silinemez!");
            }
        }
        depo.getKaynaklar().sil(id);
    }
}