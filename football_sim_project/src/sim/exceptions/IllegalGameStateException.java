package sim.exceptions;

public class IllegalGameStateException extends RuntimeException {
    public IllegalGameStateException(String msg){ 
        super(msg); 
    }
}
