package thread;
import model.Odunc;
import service.KutuphaneDeposu;
import java.time.LocalDate;
import java.util.*;

/**
 *Thread sınıfından kalıtılarak arka planda belirli gecikmiş ödünç kayıtlarını tarayan ve konsola hatırlatma uyarısı basan iş parçacığı sınıfıdır.
 * Ana program kapandığında uygulamanın açık kalmasını önlemek amaçlı daemon thread olarak yapılandırıldı.
 * Eş zamanlı erişim hatalarını önlemek için koleksiyonların kopyaları üzerinden işlem yapılır.
 */
public class IadeHatirlatici extends Thread{
    //Kontrol döngüsünün milisaniye cinsinden bekleme süresini tutar.
    private final long kontrolAraligiMs;
    //Ana threadden durdur() çağrıldığında değişikliğin CPU önbelleğine takılmadan arkaplan threadi tarafından anında görülmesini sağlayan volatile bayraktır.
    private volatile boolean calisiyor = true;

    /**
     *Tarama aralığını ayarlar, setDaemon(true) çağrısı ile ana program sonlandığında bu threadin de oto kapanmasını sağlar.
     * Hata ayıklamayı kolaylaştırmak için threade özel bir isimi setName ile atar.
     * @param kontrolAraligiMs (kontrol gecikme süresi, milisaniye)
     */
    public IadeHatirlatici(long kontrolAraligiMs) {
        this.kontrolAraligiMs = kontrolAraligiMs;
        setDaemon(true);
        setName("IadeHatirlatici-Thread");
    }

    /**
     * Döngü koşulunu (calisiyor) false yapar. Thread o sırada uyuyorsa interrupt ile kesme uygulanır.
     *Kesme (interrupt) threadi uyandırıp sonlandırmak işini yapar.
     */

    public void durdur(){
        this.calisiyor = false;
        this.interrupt();
    }

    /**
     * Thread sınıfından @Override edilen ana çalışma metotudur. calisiyor true olduğu sürece kontrolAraligiMs kadar sessiz kalır.
     * Ardından gecikenleriKontrolEt() metodunu tetikler. durdur() çağrıldığında InterruptedException hatasını yakalar
     * Kesinti bayrağını koruyarak döngüden güvenli ayrılır.
     */
    @Override public void run(){
        while(calisiyor){
            try{
                Thread.sleep(kontrolAraligiMs);
                if(!calisiyor) break;
                gecikenleriKontrolEt();
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Singleton KutuphaneDeposu örneğine erişir ve TreeMap olan teslimTakvimi üzerinde headMap(bugun) metodunu kullanarak tüm liste yerine,
     * yalnızca son teslim tarihi bugünden önce olan kayıtları çeker. Ana thread aynı anda ödünç veya iade işlemi yaparken ConcurrentModificationException,
     * oluşmasını engellemek için kayıtların kopyası (ArrayList<>()) üzerinde dolaşır.
     * Henüz iade edilmemiş kayıtlar için ekrana uyarı yazdırır.
     */
    private void gecikenleriKontrolEt(){
        KutuphaneDeposu depo =  KutuphaneDeposu.getInstance();
        LocalDate bugun = LocalDate.now();
        SortedMap<LocalDate, List<Odunc>> gecikenler = depo.getTeslimTakvimi().headMap(bugun);
        if(!gecikenler.isEmpty()){
            for(Map.Entry<LocalDate, List<Odunc>> kayit : new ArrayList<>(gecikenler.entrySet())){
                for(Odunc o : new ArrayList<>(kayit.getValue())){
                    if(!o.iadeEdildiMi()){
                        System.out.println("\n[HATIRLATMA] Geciken odunc -> Uye: " +  o.getUye().getAd() + " " + o.getUye().getSoyad() + " | Kaynak: " + o.getKaynak().getBaslik() + " | Son Teslim: " + kayit.getKey());
                    }
                }
            }
        }
    }
}