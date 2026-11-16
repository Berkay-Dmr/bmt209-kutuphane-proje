package util;

public class Dogrulayici {
    private Dogrulayici(){}
    public static boolean bosMu(String metin){

        if( metin  == null || metin.trim().isEmpty() == true){

            return true ;

        }

        else return false;

    }
    public static boolean epostaGecerliMi(String eposta ) {

        if (bosMu(eposta) == true) {

            return false;

        } else {

            String temiz = eposta.trim();
            int index = temiz.indexOf('@');

            return index > 0 && temiz.indexOf('.', index) > index + 1 && temiz.lastIndexOf('.') < temiz.length() - 1;
        }


    }


    public static  boolean aralikta (int deger , int min , int max){
        return deger >= min && deger <= max;
    }
}