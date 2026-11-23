package thread;
import exception.KaynakMevcutDegilException;
import model.Kaynak;
import model.OduncVerilebilir;

/** Ayni kaynagi es zamanli odunc alma simulasyonu icin Runnable sinifi */
public class OduncSimulasyonGorevi implements Runnable{

    private static int basarili = 0;
    private final Kaynak kaynak;
    private final String ad;
    private final boolean guvenli;

    /** yapici metot yazildi ve alanlara atama yapildi */
    public OduncSimulasyonGorevi(Kaynak kaynak, String ad, boolean guvenli){
        this.kaynak = kaynak;
        this.ad = ad;
        this.guvenli = guvenli;
    }

    /** thread calistiginda guvenli veya guvensiz odunc alma islemini yapar */
    @Override
    public void run(){
        try{
            if(guvenli){
                OduncVerilebilir ov = kaynak;
                ov.oduncVer();
            }
            else{
                kaynak.oduncVerGuvensiz();
            }
            artir();
            System.out.println(ad + ": odunc aldi");
        }
        catch (KaynakMevcutDegilException e){
            System.out.println(ad + ": " + e.getMessage());
        }
    }

    /** basarili islem sayacini senkronize sekilde artirir */
    private static synchronized void artir(){
        basarili++;
    }

    /** basarili islem sayisini dondurur */
    public static synchronized int getBasarili(){
        return basarili;
    }

    /** basarili islem sayacini sifirlar */
    public static synchronized void sifirla(){
        basarili = 0;
    }

}