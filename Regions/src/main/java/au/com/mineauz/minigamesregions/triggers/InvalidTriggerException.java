package au.com.mineauz.minigamesregions.triggers;

public class InvalidTriggerException extends RuntimeException{
    
    private static final long serialVersionUID = 1L;

    public InvalidTriggerException(){
        super();
    }
    
    public InvalidTriggerException(String message){
        super(message);
    }

}
