package model; // paketi import ettik
import java.time.LocalDate; //  tarihe erişmek için zaman sınıfını import ettik
public interface Ucretlendirilebilir { // arayüzü yazdık ve gerekli metotları ekledik

    public double getGunlukCeza();
    public boolean cezaVarMi(LocalDate sonTeslim , LocalDate bugun );

    public double cezaHesapla(LocalDate sonTeslim , LocalDate iade ); // cezalandırma işlemini gerçekleştirmek için bu metotları kullanacağız

}