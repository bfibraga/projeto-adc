package pt.unl.fct.di.adc.silvanus.util.cripto;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;

public class MD5HEX implements CRIPTO{
    @Override
    public String execute(String arg) {
        return DigestUtils.md5Hex(arg);
    }

    //TODO Change this
    @Override
    public String name() {
        return "MD5HEX";
    }
}
