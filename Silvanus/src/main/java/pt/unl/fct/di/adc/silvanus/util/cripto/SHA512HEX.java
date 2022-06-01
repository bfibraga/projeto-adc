package pt.unl.fct.di.adc.silvanus.util.cripto;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;

public class SHA512HEX implements CRIPTO{

    @Override
    public String execute(String arg) {
        return DigestUtils.sha512Hex(arg);
    }

    @Override
    public String name() {
        return "SHA512HEX";
    }
}
