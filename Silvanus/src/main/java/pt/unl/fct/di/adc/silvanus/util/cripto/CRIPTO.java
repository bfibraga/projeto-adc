package pt.unl.fct.di.adc.silvanus.util.cripto;

/**
 * @author GreenTeam
 */
public interface CRIPTO {

    /**
     * Execute the ecription algorithm of this paramater
     * @param arg Parameter to encript
     * @return Encripted version of given parameter
     */
    public String execute(String arg);

    public String name();
}
