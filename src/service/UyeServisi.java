package service;

import model.Uye;
import exception.GecersizVeriException;
import exception.KayitBulunamadiException;
import java.util.*;

/**
 *Kütüphane üyelerinin eklenmesi, listelenmesi, aranması ve gecikme borcu işlemlerini yöneten servis sınıfıdır.
 * Tüm işlemler Singleton yapıdaki KutuphaneDeposu üzerindeki koleksiyonlar üzerinden yürütülür.
 * Kullanılan koleksiyonlar: uyeler, uyeOduncleri, acikOduncSayilari.
 */
public class UyeServisi{
    //Singleton KutuphaneDeposu örneğinden getInstance ile erişim sağlanıyor
    private final KutuphaneDeposu depo = KutuphaneDeposu.getInstance();

    /**
     * Yeni üyeyi sisteme kaydeder. Üye nesnesi null ise veya uyeNo zaten uyeler mapinde kayıtlıysa GecersizVeriException fırlatır.
     * Geçerliyse üyeyi uyeler mapine ekler, o üye için boş ödünç listesi ile 0 başlangıçlı açık ödünç sayacı başlatır.
     * @param uye (sisteme eklenecek üye nesnesi)
     * @throws GecersizVeriException (üye null veya  zaten kayıtlı ise fırlatılır)
     */
    public void uyeEkle(Uye uye){
        if(uye == null || depo.getUyeler().containsKey(uye.getUyeNo())){
            throw new GecersizVeriException("Gecersiz veya mevcut numarali uye!");
        }
        depo.getUyeler().put(uye.getUyeNo(), uye);
        depo.getUyeOduncleri().put(uye.getUyeNo(), new ArrayList<>());
        depo.getAcikOduncSayilari().putIfAbsent(uye.getUyeNo(), 0);
    }

    /**
     * uyeler mapindeki tüm değerleri yeni bir ArrayList kopyası olarak döndürür.
     * Bu sayede dışarıdan doğrudan müdahale edilmesi engellenir
     * @return (kayıtlı tüm üyeleri içeren liste)
     */
    public List<Uye> listele(){
        return new ArrayList<>(depo.getUyeler().values());
    }

    /**
     * Verilen üye numarasına göre arama yapar.
     * Arka planda uyeGetir() metodunu çağırır ve üye yoksa KayitBulunamadiException fırlatır.
     * @param uyeNo (aranacak üyenin numarası)
     * @return (bulunan üye nesnesi)
     * @throws KayitBulunamadiException (üye bulunamazsa fırlatılır)
     */
    public Uye bul(int uyeNo) throws KayitBulunamadiException{
        return uyeGetir(uyeNo);
    }

    /**
     * uyeler haritasından üye numarasıyla Map.get ile doğrudan kayıt getirir.
     * Sonuç null ise açıklayıcı mesajla KayitBulunamadiException fırlatır.
     * @param uyeNo (getirilecek üyenin numarası)
     * @return (bulunan uye nesnesi)
     * @throws KayitBulunamadiException (belirtilen üye yoksa)
     */
    public Uye uyeGetir(int uyeNo) throws KayitBulunamadiException{
        Uye u = depo.getUyeler().get(uyeNo);
        if(u == null){
            throw new KayitBulunamadiException("Uye No: " + uyeNo + " olan uye bulunamadi!");
        }
        else{
            return u;
        }
    }

    /**
     * Kayıtlı üyeler arasında dolaşarak u.isAktifMi() değeri true olan aktif üyeleri filtreler ve yeni bir liste halinde döndürür.
     * @return (aktif durumdaki üyelerin listesi)
     */

    public List<Uye> aktifUyeler(){
        List<Uye> sonuc = new ArrayList<>();
        for(Uye u : depo.getUyeler().values()){
            if(u.isAktifMi()){
                sonuc.add(u);
            }
        }
        return sonuc;
    }

    /**
     * Önce uyeGetir() ile üyeyi bulur (bulunmazsa exception fırlatır), ardından bulunan üye nesnesi üzerinden u.borcode ile borç bakiyesini ödeme tutarından düşer.
     * @param uyeNo (borç ödemesi yapacak üyenin numarası)
     * @param tutar (ödenecek borç tutarı)
     * @throws KayitBulunamadiException (üye sistemde bulunmazsa)
     */
    public void borcOde(int uyeNo, double tutar) throws KayitBulunamadiException{
        Uye u = uyeGetir(uyeNo);
        u.borcode(tutar);
    }
}