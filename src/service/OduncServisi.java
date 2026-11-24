package service;

import model.*;
import exception.*;
import util.TarihYardimcisi;
import java.time.LocalDate;
import java.util.*;

/**
 * Kütüphane kaynaklarının ödünç verilmesi, iade alınması, gecikme ile oluşan ceza hesaplamaları ve popüler kaynak analizlerini yöneten servis sınıfıdır.
 * Kritik işlemlerde (oduncVer, iadeEt gibi) veri tutarsızlığının önüne geçmek için synchronized kullandık.
 * Stok ve ücretlendirme işlemlerinde doğrudan alt sınıf yerine OduncVerilebilir ve Ucretlendirilebilir arayüz referansları kullandık.
 * Açık ödünç sayılarının Map.replace ile güncellendiği ve Integer, Long, Double Wrapper sınıflarının kullanıldığı bir sınıftır.
 */
public class OduncServisi{
    //Singleton örneğine ve üye/kaynak doğrulama servislerine erişim sağlamak için alttaki 3 satır yazıldı.
    private final KutuphaneDeposu depo = KutuphaneDeposu.getInstance();
    private final KaynakServisi kaynakServisi = new KaynakServisi();
    private final UyeServisi uyeServisi = new UyeServisi();

    /**
     * Üyeyi ve kaynağı getirir, üyenin ödenmemiş borcu varsa GecersizVeriException, açık ödünç sayısı üyenin limitine ulaşmışsa OduncLimitiAsildiException fırlatılır.
     * Stok düşümünü OduncVerilebilir arayüzü referansı üzerinden yapar. Kaynak ve üyenin izin verilen maksimum günlerinden küçük olanını seçerek son teslim tarihin hesaplar.
     * yeni Odunc kaydını ilgili koleksiyonlara ekler ve acikOduncSayilari mapini replace ile 1 artırır. Çoklu iş parçacığı güvenliği için synchronized tanımlanmıştır.
     * @param uyeNo (odunc alacak üyenin numarası)
     * @param kaynakId (ödünç verilecek kaynağın numarası)
     * @return (oluşturulan yeni Odunc işlem kaydı)
     * @throws KayitBulunamadiException (üye ve kaynak sistemde bulunmazsa fırlatılır)
     * @throws KaynakMevcutDegilException (kaynağın ödünç verilebilir stoğu kalmamışsa veya rezerveyse fırlatılır)
     */
    public synchronized Odunc oduncVer(int uyeNo, int kaynakId) throws KayitBulunamadiException, KaynakMevcutDegilException{
        Uye uye = uyeServisi.uyeGetir(uyeNo);
        Kaynak kaynak = kaynakServisi.idIleGetir(kaynakId);
        if(uye.getBorc() > 0){
            throw new GecersizVeriException("Uyenin odenmemis borcu bulunuyor!");
        }

        Integer aktif = depo.acikOduncSayisi(uyeNo);
        if(aktif >= uye.maxOduncAdedi()){
            throw new OduncLimitiAsildiException("Uye maksimum odunc limitine ulasti!");
        }

        OduncVerilebilir ov = kaynak;
        ov.oduncVer();

        LocalDate bugun = LocalDate.now();
        int sure = Math.min(kaynak.maxOduncSuresi(), uye.oduncSuresiGun());
        LocalDate sonTeslim = TarihYardimcisi.sonTeslim(bugun, sure);

        Odunc yeniOdunc = new Odunc(kaynak, uye, bugun, sonTeslim);

        List<Odunc> uyeninOduncleri = depo.getUyeOduncleri().computeIfAbsent(uyeNo, k -> new ArrayList<>());
        depo.getOduncler().add(yeniOdunc);
        uyeninOduncleri.add(yeniOdunc);
        depo.getTeslimTakvimi().computeIfAbsent(sonTeslim, k -> new ArrayList<>()).add(yeniOdunc);
        depo.getAcikOduncSayilari().putIfAbsent(uyeNo, 0);
        depo.getAcikOduncSayilari().replace(uyeNo, aktif + 1);

        return yeniOdunc;
    }

    /**OVERLOAD - 1
     * Üyenin ödünç geçmişi içinde belirtilen kaynağa ait henüz iade edilmemiş aktif Odunc kaydını arar, bulamazsa KayitBulunamadiException fırlatır.
     * Eğer exception fırlatılmazsa asıl iade işlemini yapan iadeEt() metoduna yönlendirir.
     * @param uyeNo (iade işlemini yapan üyenin numarası)
     * @param kaynakId (iade edilen kaynağın numarası)
     * @return (gecikme varsa hesaplanan ceza tutarı yoksa 0)
     * @throws KayitBulunamadiException (üyeye ait aktif ödünç kaydı bulunamazsa fırlatılır.)
     */
    public synchronized double iadeEt(int uyeNo, int kaynakId) throws KayitBulunamadiException {
        List<Odunc> liste = depo.getUyeOduncleri().get(uyeNo);
        if (liste == null) {
            throw new KayitBulunamadiException("Uyeye ait odunc kayit bulunamadi!");
        }

        Odunc hedef = null;
        for (Odunc o : liste){
            if(o.getKaynak().getKaynakNo() == kaynakId && !o.iadeEdildiMi()){
                hedef = o;
                break;
            }
        }

        if (hedef == null){
            throw new KayitBulunamadiException("Bu uyede aktif olarak bu kaynak bulunmuyor!");
        }

        return iadeEt(hedef);
    }

    /**OVERLOAD - 2
     * Kaydın geçerliliğini ve daha önce iade edilmediğini doğrular, doğrulanamazsa GecersizVeriException fırlatılır.
     * İade tarihini bugüne ayarlar, gecikme gününü Long wrapper ile hesaplar.
     * Gecikme varsa Ucretlendirilebilir arayüz referansını ve Double wrapperı kullanarak cezayı hesaplayıp üyenin borcuna işler.
     * OduncVerilebilir arayüzü sayesinde (ov.iadeAl() kullanarak) stoğu günceller.
     * acikOduncSayilari mapini replace ile 1 azaltır ve kaydı teslimTakvimi yapısından çıkartır.
     * @param hedef (iade alinacak Odunc nesnesi)
     * @return (Gecikme varsa tahakkuk eden ceza tutarı, yoksa 0.0)
     * @throws GecersizVeriException (Kayıt null veya daha önce iade edilmiş ise fırlatılır)
     */
    public synchronized double iadeEt(Odunc hedef) throws GecersizVeriException{
        if (hedef == null || hedef.iadeEdildiMi()){
            throw new GecersizVeriException("Gecersiz veya daha once iade edilmis odunc kaydi!");
        }

        LocalDate bugun = LocalDate.now();
        hedef.setIadeTarihi(bugun);

        Long gecikmeGunu = TarihYardimcisi.gecikmeGunu(hedef.getSonTeslimTarihi(), bugun);
        Double ceza = 0.0;

        if(gecikmeGunu > 0){
            Ucretlendirilebilir ucret = hedef.getKaynak();
            ceza = ucret.cezaHesapla(hedef.getSonTeslimTarihi(), bugun) * hedef.getUye().getGecikmeCarpani();
            hedef.setCeza(ceza);
            hedef.getUye().borcEkle(ceza);
        }

        OduncVerilebilir ov = hedef.getKaynak();
        ov.iadeAl();

        int uyeNo = hedef.getUye().getUyeNo();
        depo.getAcikOduncSayilari().replace(uyeNo, Math.max(0, depo.acikOduncSayisi(uyeNo) - 1));

        List<Odunc> gunlukListe = depo.getTeslimTakvimi().get(hedef.getSonTeslimTarihi());
        if (gunlukListe != null){
            gunlukListe.remove(hedef);
            if(gunlukListe.isEmpty()){
                depo.getTeslimTakvimi().remove(hedef.getSonTeslimTarihi());
            }
        }

        return ceza;
    }

    /**
     * İç içe tanımlanan uyeOduncleri mapinden ilgili üyenin tüm ödünç geçmişini getirir.
     * Kayıt yoksa boş liste (getOrDefault) döndürür.
     * @param uyeNo (geçmişi sorgulanacak üye numarası)
     * @return (üyeye ait ödünç kayıtlarının listesi)
     */
    public List<Odunc> uyeOduncleri(int uyeNo){
        return depo.getUyeOduncleri().getOrDefault(uyeNo, new ArrayList<>());
    }

    /**
     * Tüm ödünç kayıtlarını tarayarak henüz iade edilmemiş ve son teslim tarihi bugünden önce kalmış kayıtları filtreler.
     * Gecikme cezalarını Ucretlendirilebilir arayüzü ve Double listesi üzerinden hesaplayıp geciken kayıt listesine döndürür.
     * @return (teslim tarihi geçmiş aktif ödünç kayıtlarının listesi)
     */
    public List<Odunc> gecikenler(){
        LocalDate bugun = LocalDate.now();
        List<Odunc> sonuc = new ArrayList<>();
        List<Double> cezalar = new ArrayList<>();
        for (Odunc o : depo.getOduncler()){
            if (!o.iadeEdildiMi() && bugun.isAfter(o.getSonTeslimTarihi())){
                sonuc.add(o);
                Ucretlendirilebilir ucret = o.getKaynak();
                cezalar.add(ucret.cezaHesapla(o.getSonTeslimTarihi(), bugun) * o.getUye().getGecikmeCarpani());
            }
        }
        return sonuc;
    }

    /**
     * Tüm ödünç geçmişi üzerinden her kaynağın kaç kez ödünç alındığını sayar.
     * En çok tercih edilenden en aza doğru sıralar, KutuphaneDeposu içindeki tekrarsız populerKaynaklar kümesini günceller.
     * İlk limit adet kaynağı liste olarak döndürülür
     * @param limit (listelenecek maksimum popüler kaynak sayısı)
     * @return (en çok ödünç alınan kaynakların listesi)
     */
    public List<Kaynak> enPopulerKaynaklar(int limit) {
        Map<Kaynak, Integer> sayac = new HashMap<>();
        for (Odunc o : depo.getOduncler()) {
            Kaynak k = o.getKaynak();
            sayac.put(k, sayac.getOrDefault(k, 0) + 1);
        }

        List<Map.Entry<Kaynak, Integer>> sirali = new ArrayList<>(sayac.entrySet());
        sirali.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        depo.getPopulerKaynaklar().clear();
        List<Kaynak> sonuc = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, sirali.size()); i++) {
            Kaynak k = sirali.get(i).getKey();
            sonuc.add(k);
            depo.getPopulerKaynaklar().add(k);
        }
        return sonuc;
    }
}