package thread;
import model.Odunc;
import service.KutuphaneDeposu;
import java.time.LocalDate;
import java.util.*;

public class IadeHatirlatici extends Thread{
    private final long kontrolAraligiMs;
    private volatile boolean calisiyor = true;

    public IadeHatirlatici(long kontrolAraligiMs) {
        this.kontrolAraligiMs = kontrolAraligiMs;
        setDaemon(true);
        setName("IadeHatirlatici-Thread");
    }

    public void durdur(){
        this.calisiyor = false;
        this.interrupt(); // Uyuyorsa beklemeden sonlandırıyoruz
    }

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

    private void gecikenleriKontrolEt(){
        KutuphaneDeposu depo =  KutuphaneDeposu.getInstance();
        LocalDate bugun = LocalDate.now();
        SortedMap<LocalDate, List<Odunc>> gecikenler = depo.getTeslimTakvimi().headMap(bugun);
        if(!gecikenler.isEmpty()){
            for(Map.Entry<LocalDate, List<Odunc>> kayit : gecikenler.entrySet()){
                for(Odunc o : kayit.getValue()){
                    if(!o.iadeEdildiMi()){
                        System.out.println("\n[HATIRLATMA] Geciken odunc -> Uye: " +  o.getUye().getAd() + " " + o.getUye().getSoyad() + " | Kaynak: " + o.getKaynak().getAd() + " | Son Teslim: " + kayit.getKey());
                    }
                }
            }
        }
    }
}