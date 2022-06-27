package pt.unl.fct.di.adc.silvanus.util.cripto;

public class PASSWORD {

    private static final CRIPTO[] parameters = {
      new SHA256HEX(),
      new SHA512HEX()
    };

    //TODO Testing
    public static String digest(String arg){
        String result = arg;
        for (CRIPTO cripto: parameters) {
            result = cripto.execute(result);
        }
        return result;
    }
}
