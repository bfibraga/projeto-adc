package pt.unl.fct.di.adc.silvanus.data.terrain.chunks.exceptions;

public class OutOfChunkBounds extends Exception{
    private static final String message = "Selected chunk %s is out of bounds";
    public OutOfChunkBounds(String reason){
        super(String.format(message, reason));
    }
}
