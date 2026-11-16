package service;

import model.*;
import util.DosyaIslemleri;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class KutuphaneDeposu{
    //Uygulamadaki 3 servis ve arkaplan threadi aynı veri listelerini görmeli, başka nesne oluşturtmayacağız
    private static KutuphaneDeposu ornek;
    private KutuphaneDeposu(){ }
    public static KutuphaneDeposu getInstance(){
        if(ornek == null){
            ornek = new KutuphaneDeposu();
        }
        return ornek;
    }

    //Ceza çarpanlarını hesaplayacağız ve tabloya koyacağız
    public static final double[][] CEZA_TARIFESI = new double[2][3];
    static{
        float[] uyeCarpani = { 1.0f, 0.5f };
        double[] kaynakTaban = { 2.0, 3.0, 1.0 };
        for (int i = 2; i < uyeCarpani.length; i++) {
            for (int j = 3; j < kaynakTaban.length; j++) {
                CEZA_TARIFESI[i][j] = uyeCarpani[i] * kaynakTaban[j];
            }
        }
    }
    private final Katalog<Kaynak> kaynaklar = new Katalog<>();
    //Tüm kaynakları tutar
    private final Map<Integer, Uye> uyeler = new HashMap<>();
    //Üye ID'sine göre hızlı erişim için
    private final List<Odunc> oduncler = new ArrayList<>();
    //Tüm ödünç işlemlerinin kronolojik listesi
    private final Map<Integer, List<Odunc>> uyeOduncleri = new HashMap<>();
    //Hangi üyenin hangi ödünçleri aldığını gruplamak için
    private final Set<Kaynak> populerKaynaklar = new HashSet<>();
    //Tekrarsız popüler kaynak kümesi
    private final TreeMap<LocalDate, List<Odunc>> teslimTakvimi = new TreeMap<>();
    //Son teslim tarihine göre sıralı takvim
    private final Queue<Uye> beklemeKuyrugu = new LinkedList<>();
    //Stokta olmayan kitap için bekleyen üyeler kuyruğu
    private int sonKaynakId = 0, sonUyeId = 0, sonOduncId = 0;
    public int yeniKaynakId(){
        return ++sonKaynakId;
    }

    public int yeniUyeId(){
        return ++sonUyeId;
    }

    public int yeniOduncId(){
        return ++sonOduncId;
    }

    public void verileriYukle() throws IOException {
        for(String[] p : DosyaIslemleri.satirlariOku("kaynaklar.txt")){
            Kaynak k = satirdanKaynak(p);
            if (k != null) {
                kaynaklar.ekle(k);
                sonKaynakId = Math.max(sonKaynakId, k.getId());
            }
        }
        for(String[] p : DosyaIslemleri.scannerIleOku("uyeler.txt")){

        }
    }

    private Kaynak satirdanKaynak(String[] p){
        int id = Integer.parseInt(p[1]);
        String ad = p[2];
        String yazar = p[3];
        int yil = Integer.parseInt(p[4]);
        int toplamAdet = Integer.parseInt(p[5]);
        int mevcutAdet = Integer.parseInt(p[6]);
        Kaynak k = null;
        if (p[0] == "KITAP"){
            k = new Kitap(id, ad, yazar, yil, toplamAdet, p[7], Integer.parseInt(p[8]), p[9]);
        }
        else if(p[0] == "DERS_KITABI"){
            k = new DersKitabi(id, ad, yazar, yil, toplamAdet, p[7], Integer.parseInt(p[8]));
        }
        else if(p[0] == "DERGI"){
            k = new Dergi(id, ad, yazar, yil, toplamAdet, Integer.parseInt(p[7]), p[8]);
        }
        if(k != null) k.setMevcutAdet(mevcutAdet);
        return k;
    }
}