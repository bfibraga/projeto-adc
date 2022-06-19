package pt.unl.fct.di.adc.silvanus.util.cripto;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;

import java.util.Arrays;

public class SHA256 implements CRIPTO{
    @Override
    public String execute(String arg) {
        return Arrays.toString(DigestUtils.sha256(arg));
    }

    @Override
    public String name() {
        return "SHA256";
    }
}
