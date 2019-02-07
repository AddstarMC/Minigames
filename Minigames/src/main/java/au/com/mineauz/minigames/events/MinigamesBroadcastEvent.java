package au.com.mineauz.minigames.events;

import au.com.mineauz.minigames.minigame.Minigame;

public class MinigamesBroadcastEvent extends AbstractMinigameEvent{

    private String message;
    private String prefix;

    public MinigamesBroadcastEvent(String prefix, String message, Minigame minigame){
        super(minigame);
        this.message = message;
        this.prefix = prefix;
    }
    
    public String getMessage(){
        return message;
    }
    
    public String getMessageWithPrefix(){
        return prefix + " " + message;
    }
    
    public void setMessage(String message){
        this.message = message;
    }

    @Override
    public boolean isCancelled(){return false;}

    @Override
    public void setCancelled(boolean b){
        throw new UnsupportedOperationException("Cannot cancel a  Minigames Broadcast Event");
    }



}
