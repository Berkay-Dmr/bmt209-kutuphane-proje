package service;
import model.Kaynak;
import model.Odunc;
import exception.GecersizVeriException;
import exception.KayitBulunamadiException;
import java.util.*;


public class KaynakServisi {
    private final KutuphaneDeposu depo = KutuphaneDeposu.getInstance();

    public void kaynakEkle(Kaynak kaynak){
        if(kaynak == null || depo.getKaynaklar().idIleBul(kaynak.getId()) != null){
            throw new GecersizVeriException("Gecersiz veya mevcut ID'li kaynak!");
        }
        else{
            depo.getKaynaklar().ekle(kaynak);
        }
    }

    public Kaynak idIleGetir(int id) throws KayitBulunamadiException{
        Kaynak k = depo.getKaynaklar().idIleBul(id);
        if (k == null){
            throw new KayitBulunamadiException("ID: " + id + " olan kaynak bulunamadi!");
        }
        else{
            return k;
        }
    }

    public List<Kaynak> basligaGoreAra(String kelime){
        List<Kaynak> sonuc = new ArrayList<>();
        if(kelime == null || kelime.isBlank()){
            return sonuc;
        }
        String aranan = kelime.toLowerCase(Locale.ROOT).trim();
        for(Kaynak k : depo.getKaynaklar().tumu()){
            if(k.getAd().toLowerCase(Locale.ROOT).contains(aranan)){
                sonuc.add(k);
            }
        }
        return sonuc;
    }

    public List<Kaynak> yazaraGoreAra(String kelime){
        List<Kaynak> sonuc = new ArrayList<>();
        if(kelime == null || kelime.isBlank()){
            return sonuc;
        }
        String aranan = kelime.toLowerCase(Locale.ROOT).trim();
        for(Kaynak k : depo.getKaynaklar().tumu()){
            if(k.getYazar().toLowerCase(Locale.ROOT).contains(aranan)){
                sonuc.add(k);
            }
        }
        return sonuc;
    }

    public void kaynakSil(int id) throws KayitBulunamadiException{
        Kaynak hedef = idIleGetir(id);
        for(Odunc o : depo.getOduncler()){
            if(o.getKaynak().getId() == id && !o.iadeEdildiMi()){
                throw new GecersizVeriException("Oduncte olan kaynak silinemez!");
            }
        }
        depo.getKaynaklar().sil(hedef);
    }
}