package model;

import exception.KaynakMevcutDegilException; // sırası ile hangi pakatte olduğunu ve import edilmesi gereken diğer paketi yazdık

public interface OduncVerilebilir {

    public boolean oduncVerilebilirMi(); // sırası ile gerekli metotları ve imzalarını yazdık
    public void oduncVer () throws KaynakMevcutDegilException; // bu kısımda hata fırlatabilmesi için throws yazdık
    public void iadeAl();
    public int getOduncSuresiGun();

}