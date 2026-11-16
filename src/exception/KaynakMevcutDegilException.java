package exception;
// hangi pakette olduğunu bildirdik
public class KaynakMevcutDegilException extends Exception {

    public KaynakMevcutDegilException (String mesaj){

        super(mesaj); // yapıcıyı yazdık ve üst sınıfı olan exception sınıfına erişebilsin diye super kullanımı gerçekleştirdik

    }

}
