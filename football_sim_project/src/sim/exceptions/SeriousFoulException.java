package sim.exceptions;

public class SeriousFoulException extends Exception {
    private final String details;
    public SeriousFoulException(String details) { 
        this.details = details; 
    }

    @Override
    public String getMessage() { 
        return "Serious foul: " + details; 
    }
}
