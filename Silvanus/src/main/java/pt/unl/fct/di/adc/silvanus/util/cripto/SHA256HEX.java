package pt.unl.fct.di.adc.silvanus.util.cripto;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;

public class SHA256HEX implements CRIPTO{
    @Override
    public String execute(String arg) {
        return DigestUtils.sha256Hex(arg);
    }

    //TODO Change this later
    @Override
    public String name() {
        return "SHA256HEX";
    }
}
