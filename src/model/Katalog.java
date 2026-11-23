package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** kaynak turundeki nesneleri tutan generic katalog sinifi acildi */
public class Katalog<T extends Kaynak>{

    private final List<T> kayitlar;

    /** bos liste olusturmak icin yapici metot yazildi */
    public Katalog(){
        this.kayitlar = new ArrayList<>();
    }

    /** kataloga yeni kaynak eklemek icin metot yazildi */
    public void ekle(T kaynak){
        if(kaynak != null){
            kayitlar.add(kaynak);
        }
    }

    /** kaynak numarasina gore silme islemi yapar */
    public boolean sil(int kaynakNo){
        return kayitlar.removeIf(k -> k.getKaynakNo() == kaynakNo);
    }

    /** kaynak numarasina gore arama yapar ve bulursa dondurur */
    public T bul(int kaynakNo){
        for(T k : kayitlar){
            if(k.getKaynakNo() == kaynakNo){
                return k;
            }
        }
        return null;
    }

    /** baslik icinde gecen kelimeye gore arama yapar */
    public List<T> basligaGoreAra(String kelime){
        List<T> sonuc = new ArrayList<>();
        if(kelime != null){
            for(T k : kayitlar){
                if(k.getBaslik().toLowerCase().contains(kelime.toLowerCase())){
                    sonuc.add(k);
                }
            }
        }
        return sonuc;
    }

    /** varsayilan siralama kuralina gore listeyi siralar */
    public void sirala(){
        Collections.sort(kayitlar);
    }

    /** disaridan verilen karsilastiriciya gore listeyi siralar */
    public void sirala(Comparator<? super T> karsilastirici){
        kayitlar.sort(karsilastirici);
    }

    /** kayitlarin kopya listesini dondurur */
    public List<T> getKayitlar(){
        return new ArrayList<>(kayitlar);
    }

    /** katalogdaki toplam kayit sayisini dondurur */
    public int boyut(){
        return kayitlar.size();
    }

    /** verilen listedeki kaynaklarin mevcut adetlerini toplar */
    public static int toplamMevcutAdet(List<? extends Kaynak> liste){
        int toplam = 0;
        for(Kaynak k : liste){
            toplam += k.getMevcutAdet();
        }
        return toplam;
    }

    /** verilen listedeki tum kaynaklarin bilgisini ekrana yazdirir */
    public static void listeyiYazdir(List<? extends Kaynak> liste){
        for(Kaynak k : liste){
            k.bilgiYazdir();
        }
    }

}