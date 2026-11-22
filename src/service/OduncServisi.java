package service;

import model.*;
import exception.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class OduncServisi{
    private final KutuphaneDeposu depo = KutuphaneDeposu.getInstance();
    //KutuphaneDeposu sınıfı Singleton olduğu için getInstance yaptık.
    private final KaynakServisi kaynakServisi = new KaynakServisi();
    private final UyeServisi uyeServisi = new UyeServisi();

    public Odunc oduncVer(int uyeNo, int kaynakId) throws KayitBulunamadiException, KaynakMevcutDegilException{
        Uye uye = uyeServisi.uyeGetir(uyeNo);
        Kaynak kaynak = kaynakServisi.idIleGetir(kaynakId);
        if(!uye.isAktifMi() || uye.getBorc() > 0){
            throw new GecersizVeriException("Uye aktif degil veya odenmemis borcu bulunuyor!");
        }
        List<Odunc> uyeninOduncleri = depo.getUyeOduncleri().computeIfAbsent(uyeNo, k -> new ArrayList<>());
        long aktifSayi = uyeninOduncleri.stream().filter(o -> !o.iadeEdildiMi()).count();
        if(aktifSayi >= uye.getMaxOduncLimiti()){
            throw new OduncLimitiAsildiException("Uye maksimum odunc limitine ulasti!");
        }
        if(!kaynak.oduncVerilebilirMi()){
            throw new KaynakMevcutDegilException("Kaynak su anda odunc verilebilir degil!");
        }
        LocalDate bugun = LocalDate.now();
        LocalDate sonTeslim = bugun.plusDays(kaynak.getOduncSuresiGun());

        kaynak.oduncVer(); // Stok ve durum güncellemesini arayüz metoduyla yapıyor
        Odunc yeniOdunc = new Odunc(kaynak, uye, bugun, sonTeslim);

        depo.getOduncler().add(yeniOdunc);
        uyeninOduncleri.add(yeniOdunc);
        depo.getTeslimTakvimi().computeIfAbsent(sonTeslim, k -> new ArrayList<>()).add(yeniOdunc);

        return yeniOdunc;
    }

    public double iadeEt(int uyeNo, int kaynakId) throws KayitBulunamadiException {
        List<Odunc> liste = depo.getUyeOduncleri().get(uyeNo);
        if (liste == null) {
            throw new KayitBulunamadiException("Uyeye ait odunc kayit bulunamadi!");
        }

        Odunc hedef = null;
        for (Odunc o : liste){
            if(o.getKaynak().getId() == kaynakId && !o.iadeEdildiMi()){
                hedef = o;
                break;
            }
        }

        if (hedef == null){
            throw new KayitBulunamadiException("Bu uyede aktif olarak bu kaynak bulunmuyor!");
        }

        return iadeEt(hedef);
    }

    public double iadeEt(Odunc hedef){
        if (hedef == null || hedef.iadeEdildiMi()){
            throw new GecersizVeriException("Gecersiz veya daha once iade edilmis odunc kaydi!");
        }

        LocalDate bugun = LocalDate.now();
        hedef.setIadeTarihi(bugun);

        long gecikmeGunu = ChronoUnit.DAYS.between(hedef.getSonTeslimTarihi(), bugun);
        double ceza = 0.0;

        if(gecikmeGunu > 0){
            int uyeIndex = (hedef.getUye() instanceof OgrenciUye) ? 0 : 1;
            int kaynakIndex = (hedef.getKaynak() instanceof DersKitabi) ? 1 : (hedef.getKaynak() instanceof Dergi ? 2 : 0);
            double gunlukTarife = KutuphaneDeposu.CEZA_TARIFESI[uyeIndex][kaynakIndex];

            ceza = gecikmeGunu*gunlukTarife*hedef.getUye().getGecikmeCarpani();
            hedef.setCeza(ceza);
            hedef.getUye().borcEkle(ceza);
        }

        hedef.getKaynak().iadeAl();

        List<Odunc> gunlukListe = depo.getTeslimTakvimi().get(hedef.getSonTeslimTarihi());
        if (gunlukListe != null){
            gunlukListe.remove(hedef);
            if(gunlukListe.isEmpty()){
                depo.getTeslimTakvimi().remove(hedef.getSonTeslimTarihi());
            }
        }

        return ceza;
    }

    public List<Odunc> uyeOduncleri(int uyeNo){
        return depo.getUyeOduncleri().getOrDefault(uyeNo, new ArrayList<>());
    }

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