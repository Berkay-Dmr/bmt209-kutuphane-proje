package model;
import java.time.LocalDate;
import exception.GecersizVeriException;


public class Odunc{

    private Kaynak kaynak;
    private Uye uye ;
    private LocalDate alisTarihi;
    private LocalDate sonTeslimTarihi;
    private LocalDate iadeTarihi;
    private double ceza;


    public Odunc (Kaynak kaynak, Uye uye, LocalDate alisTarihi, LocalDate sonTeslimTarihi) {

        this.kaynak = kaynak;
        this.uye = uye ;
        this.alisTarihi = alisTarihi;
        setSonTeslimTarihi(sonTeslimTarihi);
        iadeTarihi = null;
        ceza = 0.0;
    }

    public Odunc (Kaynak kaynak, Uye uye, LocalDate alisTarihi, LocalDate sonTeslimTarihi, LocalDate iadeTarihi, double ceza){
        this(kaynak, uye, alisTarihi, sonTeslimTarihi);
        this.iadeTarihi = iadeTarihi;
        this.ceza = ceza;
    }

    public double getCeza() {
        return ceza;
    }

    public Kaynak getKaynak() {
        return kaynak;
    }

    public LocalDate getAlisTarihi() {
        return alisTarihi;
    }

    public LocalDate getIadeTarihi() {
        return iadeTarihi;
    }

    public LocalDate getSonTeslimTarihi() {
        return sonTeslimTarihi;
    }

    public Uye getUye() {
        return uye;
    }

    public void setCeza(double ceza) {
        this.ceza = ceza;
    }

    public void setIadeTarihi(LocalDate iadeTarihi) {
        this.iadeTarihi = iadeTarihi;
    }



    public void setSonTeslimTarihi(LocalDate sonTeslimTarihi) {
        if (alisTarihi != null && sonTeslimTarihi.isBefore(alisTarihi)) {
            throw new GecersizVeriException("Son teslim tarihi alis tarihinden once olamaz.");
        }

        this.sonTeslimTarihi = sonTeslimTarihi;


    }
public boolean iadeEdildiMi() {
    return iadeTarihi != null;
    }
@Override
    public String toString(){
    return "Kaynak: " + kaynak + " | Uye: " + uye + " | Alis: " + alisTarihi + " | Son Teslim: " + sonTeslimTarihi;
}


}
