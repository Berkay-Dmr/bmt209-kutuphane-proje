package service;

import model.*;
import util.DosyaIslemleri;
import util.TarihYardimcisi;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class KutuphaneDeposu{
    //Uygulamadaki 3 servis ve arkaplan threadi aynı veri listelerini görmeli, başka nesne oluşturtmayacağız
    private static KutuphaneDeposu ornek;
    private KutuphaneDeposu(){ }
    public static synchronized KutuphaneDeposu getInstance(){
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
        for (int i = 0; i < uyeCarpani.length; i++) {
            for (int j = 0; j < kaynakTaban.length; j++) {
                CEZA_TARIFESI[i][j] = uyeCarpani[i] * kaynakTaban[j];
            }
        }
    }
    private final Katalog<Kaynak> kaynaklar = new Katalog<>();
    public Katalog<Kaynak> getKaynaklar() { return kaynaklar; }
    //Tüm kaynakları tutar
    private final Map<Integer, Uye> uyeler = new HashMap<>();
    public Map<Integer, Uye> getUyeler() { return uyeler; }
    //Üye ID'sine göre hızlı erişim için
    private final List<Odunc> oduncler = new ArrayList<>();
    public List<Odunc> getOduncler() { return oduncler; }
    //Tüm ödünç işlemlerinin kronolojik listesi
    private final Map<Integer, List<Odunc>> uyeOduncleri = new HashMap<>();
    public Map<Integer, List<Odunc>> getUyeOduncleri() { return uyeOduncleri; }
    //Hangi üyenin hangi ödünçleri aldığını gruplamak için
    private final Set<Kaynak> populerKaynaklar = new HashSet<>();
    public Set<Kaynak> getPopulerKaynaklar() { return populerKaynaklar; }
    //Tekrarsız popüler kaynak kümesi
    private final TreeMap<LocalDate, List<Odunc>> teslimTakvimi = new TreeMap<>();
    public TreeMap<LocalDate, List<Odunc>> getTeslimTakvimi() {return teslimTakvimi; }
    //Son teslim tarihine göre sıralı takvim
    private final Queue<Uye> beklemeKuyrugu = new LinkedList<>();
    public Queue<Uye> getBeklemeKuyrugu() { return  beklemeKuyrugu; }
    //Stokta olmayan kitap için bekleyen üyeler kuyruğu
    private int sonKaynakId = 0, sonUyeId = 0;
    public int yeniKaynakId(){
        return ++sonKaynakId;
    }

    public int yeniUyeId(){
        return ++sonUyeId;
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
            int uyeNo = Integer.parseInt(p[1]);
            String ad = p[2], soyad = p[3], eposta = p[4];
            long telefon = Long.parseLong(p[5]);
            double borc = Double.parseDouble(p[6]);
            boolean aktif = Boolean.parseBoolean(p[7]);
            //Dönüşümleri yapıyoruz
            Uye u = null;
            if(p[0].equals("OGRENCI")){
                u = new OgrenciUye(uyeNo, ad, soyad, eposta, telefon, borc);
            }
            else if (p[0].equals("AKADEMISYEN")){
                u = new AkademisyenUye(uyeNo, ad, soyad, eposta, telefon, borc);
            }
            if (u != null){
                u.setAktifMi(aktif);
                uyeler.put(u.getUyeNo(), u);
                uyeOduncleri.put(u.getUyeNo(), new ArrayList<>());
                sonUyeId = Math.max(sonUyeId, u.getUyeNo());
            }
        }
        for(String[] p : DosyaIslemleri.satirlariOku("oduncler.txt")){
            Kaynak kaynak = kaynaklar.idIleBul(Integer.parseInt(p[0]));
            Uye uye = uyeler.get(Integer.parseInt(p[1]));
            LocalDate alisTarihi = TarihYardimcisi.dosyadanOku(p[2]);
            LocalDate sonTeslimTarihi = TarihYardimcisi.dosyadanOku(p[3]);
            LocalDate iadeTarihi = TarihYardimcisi.dosyadanOku(p[4]);
            double odenenCeza = Double.parseDouble(p[5]);
            if (kaynak != null && uye != null) {
                Odunc o =  new Odunc(kaynak, uye, alisTarihi, sonTeslimTarihi, iadeTarihi, odenenCeza);
                oduncler.add(o);
                uyeOduncleri.get(uye.getUyeNo()).add(o);
                if (!o.iadeEdildiMi()){
                    teslimTakvimi.computeIfAbsent(sonTeslimTarihi, k -> new ArrayList<>()).add(o);
                }
            }
        }
    }

    public void verileriKaydet() throws IOException {
        List<String> kaynakSatirlari = new ArrayList<>();
        for(Kaynak k : kaynaklar.tumu()){
            kaynakSatirlari.add(k.dosyaSatiri());
        }
        DosyaIslemleri.satirlariYaz("kaynaklar.txt", kaynakSatirlari);
        List<String> uyeSatirlari = new ArrayList<>();
        for(Uye u : uyeler.values()) {
            String tur = (u instanceof OgrenciUye) ? "OGRENCI" : "AKADEMISYEN";
            uyeSatirlari.add(tur + ";" + u.getUyeNo() + ";" + u.getAd() + ";" + u.getSoyad() + ";" + u.getEposta() + ";" + u.getTelefon() + ";" + u.getBorc() + ";" + u.isAktifMi());
        }
        DosyaIslemleri.satirlariYaz("uyeler.txt", uyeSatirlari);
        List<String> odunclerSatirlari = new ArrayList<>();
        for(Odunc o : oduncler){
            String alisStr = TarihYardimcisi.dosyayaYaz(o.getAlisTarihi());
            String sonTeslimStr = TarihYardimcisi.dosyayaYaz(o.getSonTeslimTarihi());
            String iadeStr = TarihYardimcisi.dosyayaYaz(o.getIadeTarihi());
            odunclerSatirlari.add(o.getKaynak().getId() + ";" + o.getUye().getUyeNo() + ";" + alisStr + ";" + sonTeslimStr + ";" + iadeStr + ";" + o.getCeza());
        }
        DosyaIslemleri.satirlariYaz("oduncler.txt", odunclerSatirlari);
    }

    private Kaynak satirdanKaynak(String[] p){
        int id = Integer.parseInt(p[1]);
        String ad = p[2];
        String yazar = p[3];
        int yil = Integer.parseInt(p[4]);
        int toplamAdet = Integer.parseInt(p[5]);
        int mevcutAdet = Integer.parseInt(p[6]);
        Kaynak k = null;
        if (p[0].equals("KITAP")){
            k = new Kitap(id, ad, yazar, yil, toplamAdet, p[7], Integer.parseInt(p[8]), p[9]);
        }
        else if(p[0].equals("DERS_KITABI")){
            k = new DersKitabi(id, ad, yazar, yil, toplamAdet, p[7], Integer.parseInt(p[8]));
        }
        else if(p[0].equals("DERGI")){
            k = new Dergi(id, ad, yazar, yil, toplamAdet, Integer.parseInt(p[7]), p[8]);
        }
        if(k != null) k.setMevcutAdet(mevcutAdet);
        return k;
    }
}