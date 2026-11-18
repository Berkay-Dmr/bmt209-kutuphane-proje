package service;

import model.Uye;
import exception.GecersizVeriException;
import exception.KayitBulunamadiException;
import java.util.*;


public class UyeServisi{
    private final  KutuphaneDeposu depo = KutuphaneDeposu.getInstance();
    public void uyeEkle(Uye uye){
        if(uye == null || depo.getUyeler().containsKey(uye.getUyeNo())){
            throw new GecersizVeriException("Gecersiz veya mevcut numarali uye!");
        }
        depo.getUyeler().put(uye.getUyeNo(), uye);
        depo.getUyeOduncleri().put(uye.getUyeNo(), new ArrayList<>());
    }

    public Uye uyeGetir(int uyeNo) throws KayitBulunamadiException{
        Uye u = depo.getUyeler().get(uyeNo);
        if(u == null){
            throw new KayitBulunamadiException("Uye No: " + uyeNo + " olan uye bulunamadi!");
        }
        else{
            return u;
        }
    }
    public List<Uye> aktifUyeler(){
        List<Uye> sonuc = new ArrayList<>();
        for(Uye u : depo.getUyeler().values()){
            if(u.isAktifMi()){
                sonuc.add(u);
            }
        }
        return sonuc;
    }
    public void borcOde(int uyeNo, double tutar) throws KayitBulunamadiException{
        Uye u = uyeGetir(uyeNo);
        u.borcOde(tutar);
        //Üye numarasından üyeyi bulur yoksa exception fırlatır. bulunan üye için u.borcOde(tutar) metodunu tetikler
    }
}