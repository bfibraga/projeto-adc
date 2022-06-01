package pt.unl.fct.di.adc.silvanus.util.cripto;

public class CriptoManager {

    private static final CRIPTO[] cripto = {
            new SHA512HEX(),
            new MD5HEX()
    };

    public static CRIPTO get(){
        int random = (int) (cripto.length * Math.random());
        return cripto[random];
    }
}
