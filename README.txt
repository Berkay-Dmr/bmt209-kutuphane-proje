=====================================================================
BMT-209 NESNE YONELIMLI PROGRAMLAMA - DONEM PROJESI
KUTUPHANE ODUNC VE GECIKME TAKIP SISTEMI
=====================================================================

Ders      : BMT-209 Nesne Yonelimli Programlama
Universite: <UNIVERSITE / FAKULTE / BOLUM>
Donem     : 2026-2027 Guz
Sube      : <SUBE>

Grup uyeleri:
  Ogrenci 1: Berkay Demir - 25181616071
  Ogrenci 2: Berat Atak   - 25181616006

GitHub deposu (varsa): <depo adresi>


---------------------------------------------------------------------
1. PROJENIN AMACI
---------------------------------------------------------------------
Kutuphanelerde kaynaklarin gec iade edilmesi, gecikme cezalarinin elle
hesaplanmasi ve uye turune gore degisen odunc haklarinin takip
edilememesi gercek bir sorundur. Bu proje; kitap, ders kitabi ve dergi
gibi kaynaklari, ogrenci ve akademisyen uyeleri nesne yonelimli olarak
modeller ve su islemleri yapar:

  - Kaynak ve uye ekleme, listeleme, arama, guncelleme, silme
  - Uye turune gore odunc limiti ve odunc suresi belirleme
  - Odunc verme ve iade alma
  - Gecikme cezasini otomatik hesaplama
  - Iadesi yaklasan / geciken oduncleri arka planda tarama (thread)
  - Ayni kaynagin es zamanli odunc verilmesinde veri tutarliligini
    koruma (synchronized) ve bunu demo ile gosterme (race condition)
  - Tum verileri dosyada saklama

Hedef kullanici: kutuphane gorevlisi.
Uygulama turu : Java konsol uygulamasi (menulu).


---------------------------------------------------------------------
2. GEREKSINIMLER
---------------------------------------------------------------------
  - Java surumu : <JDK SURUMU, ornegin 17>
  - IDE         : IntelliJ IDEA (veya herhangi bir IDE / komut satiri)
  - Harici kutuphane gerekmez. (JUnit testleri icin JUnit 5 gerekir;
    testler sadece bonus kapsamindadir ve uygulamanin calismasi icin
    gerekli degildir.)


---------------------------------------------------------------------
3. KLASOR YAPISI
---------------------------------------------------------------------
  src/
    app/        KutuphaneApp (main)
    model/      Kaynak, Kitap, DersKitabi, Dergi, Uye, OgrenciUye,
                AkademisyenUye, Odunc, OduncVerilebilir,
                Ucretlendirilebilir, KaynakDurumu (enum), Katalog<T>
    exception/  GecersizVeriException, OduncLimitiAsildiException,
                KaynakMevcutDegilException, KayitBulunamadiException
    service/    KutuphaneDeposu (Singleton), KaynakServisi,
                UyeServisi, OduncServisi, MenuYoneticisi
    util/       Dogrulayici, DosyaIslemleri, TarihYardimcisi,
                GirdiOkuyucu
    thread/     IadeHatirlatici (Thread), OduncSimulasyonGorevi (Runnable)
  test/         JUnit testleri (bonus)
  data/         kaynaklar.txt, uyeler.txt, oduncler.txt
  README.txt
  Rapor, sunum ve video ayri dosyalar olarak teslim paketindedir.


---------------------------------------------------------------------
4. DERLEME VE CALISTIRMA
---------------------------------------------------------------------
ONEMLI: Program proje KOK klasorunden calistirilmalidir. Veri dosyalari
"data" klasorunden goreli yolla okunur (data/kaynaklar.txt vb.).

A) Komut satiri (Windows, proje kok klasorunde):
     dir /s /b src\*.java > kaynaklar_listesi.txt
     javac -encoding UTF-8 -d out @kaynaklar_listesi.txt
     java -cp out app.KutuphaneApp

B) Komut satiri (Linux / macOS, proje kok klasorunde):
     find src -name "*.java" > kaynaklar_listesi.txt
     javac -encoding UTF-8 -d out @kaynaklar_listesi.txt
     java -cp out app.KutuphaneApp

C) IntelliJ IDEA:
     1. Projeyi acin; "src" klasoru Sources Root olmalidir.
     2. Run > Edit Configurations > Working directory = proje kok klasoru.
     3. app.KutuphaneApp sinifini calistirin.

Not: Teslim paketinde .class dosyalari bulunmaz; yukaridaki komutlarla
derlenir.


---------------------------------------------------------------------
5. MENU
---------------------------------------------------------------------
   1) Kaynak ekle (kitap / ders kitabi / dergi)
   2) Kaynaklari listele (baslik / yil / yazar siralamasi secilebilir)
   3) Kaynak ara (baslik-yazar / yil / yazar+yil)
   4) Kaynak guncelle / sil
   5) Uye ekle (ogrenci / akademisyen)
   6) Uyeleri listele
   7) Odunc ver
   8) Iade al
   9) Geciken oduncler ve ceza raporu
  10) Uye odunc gecmisi
  11) Es zamanli odunc simulasyonu (thread / race condition demosu)
  12) Iade hatirlaticiyi baslat / durdur
   0) Cikis (veriler dosyaya kaydedilir)

Hatali girislerde program kapanmaz, uyari verip tekrar sorar.


---------------------------------------------------------------------
6. ISLEYIS KURALLARI
---------------------------------------------------------------------
Uye turleri:
  Ogrenci      : en fazla 3 kaynak, 15 gun, gecikme carpani 1.0
  Akademisyen  : en fazla 10 kaynak, 30 gun, gecikme carpani 0.5

Kaynak turleri:
  Kitap        : en fazla 14 gun, gunluk ceza 2.0 TL
  Ders kitabi  : en fazla 7 gun,  gunluk ceza 3.0 TL
  Dergi        : en fazla 3 gun,  gunluk ceza 1.0 TL

Odunc suresi = kaynagin en fazla gunu ile uyenin suresinin kucugu.
Gecikme cezasi = gecikme gunu x gunluk ceza x uyenin gecikme carpani.
Ceza, iade sirasinda uyenin borcuna eklenir.


---------------------------------------------------------------------
7. VERI DOSYALARI (data/ klasoru)
---------------------------------------------------------------------
Ayirac ";" karakteridir. Tarih bicimi yyyy-MM-dd, bos deger "-" ile
gosterilir. "#" ile baslayan satirlar yorumdur.

kaynaklar.txt
  tur;no;baslik;yazar;yil;toplam;mevcut;ek1;ek2;ek3;ek4
  KITAP     : ek1=sayfa, ek2=isbn
  DERSKITABI: ek1=sayfa, ek2=isbn, ek3=dersKodu, ek4=donem
  DERGI     : ek1=sayiNo, ek2=yayinAyi

uyeler.txt
  tur;no;ad;soyad;eposta;telefon;borc;ek1;ek2
  O (ogrenci)     : ek1=bolum, ek2=sinif
  A (akademisyen) : ek1=unvan, ek2=birim

oduncler.txt
  kaynakNo;uyeNo;alis;sonTeslim;iade;ceza

Program acilista bu dosyalari okur, her degisiklikte ve cikista
dosyalara yazar. Dosyalar bulunamazsa program bos veriyle baslar.


---------------------------------------------------------------------
8. KULLANILAN ONEMLI KAVRAMLAR
---------------------------------------------------------------------
  - Kalitim   : Kaynak > Kitap > DersKitabi, Kaynak > Dergi,
                Uye > OgrenciUye / AkademisyenUye
  - Abstract  : Kaynak, Uye
  - Interface : OduncVerilebilir, Ucretlendirilebilir
  - Cok bicimlilik, instanceof, downcasting, ic/anonim sinif
  - Kapsulleme, equals/hashCode, Comparable, Comparator
  - Generics  : Katalog<T extends Kaynak>, generic metotlar, wildcard
  - Collections: ArrayList, HashMap, HashSet, TreeMap, LinkedList,
                 ic ice koleksiyon
  - Exception : 2 checked + 2 unchecked ozel exception, try-with-
                resources, multi-catch, throws, finally
  - Thread    : Thread (IadeHatirlatici), Runnable
                (OduncSimulasyonGorevi), synchronized, join
  - Tarih/saat: LocalDate, DateTimeFormatter, ChronoUnit
  - Dosya I/O : BufferedReader/Writer, Scanner(File), File.separator
  - Singleton : KutuphaneDeposu


---------------------------------------------------------------------
9. EK TEKNOLOJILER (BONUS)
---------------------------------------------------------------------
(Asagidaki listeyi proje bilgi formunda bildirdiginiz ve gercekten
yaptiginiz maddelere gore duzenleyin; yapmadiklarinizi silin.)

  [ ] Enum (KaynakDurumu)
  [ ] Lambda ve Stream API (raporlar)
  [ ] ExecutorService (thread havuzu ile simulasyon)
  [ ] JUnit 5 birim testleri (test/ klasoru)
  [ ] Git / GitHub (surum kontrolu)
  [ ] Renkli konsol ciktilari
  [ ] Swing arayuzu (yapildiysa)


---------------------------------------------------------------------
10. THREAD DEMOSU (MENU 11)
---------------------------------------------------------------------
Stogu 1 olan bir kaynak icin 5 thread ayni anda odunc almaya calisir.
  - Korumasiz (synchronized yok): birden fazla thread basarili olur,
    stok eksiye duser (race condition).
  - Korumali (synchronized)     : yalnizca 1 thread basarili olur,
    stok 0 olur.
Ciktilar raporda ve videoda gosterilmistir.


---------------------------------------------------------------------
11. GOREV DAGILIMI
---------------------------------------------------------------------
Ogrenci 1: model ve exception paketleri, util.Dogrulayici,
           thread.OduncSimulasyonGorevi, JUnit testleri, Git deposu
Ogrenci 2: service ve app paketleri, util.TarihYardimcisi,
           util.DosyaIslemleri, util.GirdiOkuyucu,
           thread.IadeHatirlatici, veri dosyalari, teslim paketi
Ortak    : rapor, UML diyagrami, sunum, video
(Ayrintili tablo raporda yer almaktadir.)


---------------------------------------------------------------------
12. BILINEN SINIRLAMALAR
---------------------------------------------------------------------
  - Basliklarda ";" karakteri kullanilmamalidir (dosya ayiraci).
  - Uygulama tek kullanicili konsol uygulamasidir; thread demosu
    haricinde es zamanli kullanici desteklemez.
  - <varsa eklediginiz diger sinirlamalari yazin>
