package pt.unl.fct.di.adc.silvanus.util.result;

public class ResultMessage {
    private int code;
    private String message;

    public ResultMessage(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
