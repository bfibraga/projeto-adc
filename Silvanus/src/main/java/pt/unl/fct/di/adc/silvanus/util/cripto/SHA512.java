package pt.unl.fct.di.adc.silvanus.util.cripto;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;

import java.util.Arrays;

public class SHA512 implements CRIPTO{
    @Override
    public String execute(String arg) {
        return Arrays.toString(DigestUtils.sha512(arg));
    }

    //TODO Change this
    @Override
    public String name() {
        return "SHA512";
    }
}
