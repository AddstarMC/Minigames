package au.com.mineauz.minigames.managers;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Class will hold and store all messages that are required for minigames
 * Created for the Charlton IT Project.
 * Created by benjicharlton on 19/06/2020.
 */
public class MessageManager {
    /**
     * Stores each prop file with a identifier
     */
    private static final Hashtable<String, ResourceBundle> propertiesHashMap = new Hashtable<>();

    public static void registerCoreLanguage() {
        ResourceBundle minigames = ResourceBundle.getBundle("minigames", Locale.getDefault());
        registerMessageFile("minigames",minigames);
    }

    public static boolean registerMessageFile(String identifier, ResourceBundle properties) {
        if(propertiesHashMap.containsKey(identifier)) {
            return false;
        } else {
            return (propertiesHashMap.put(identifier,properties) == null);
        }
    }

    public static boolean deRegisterAll(String identifier) {
        return (propertiesHashMap.remove(identifier) != null);
    }

    /**
     * If the identifier is null this uses the core language file
     * @param identifier
     * @param key
     * @param args
     * @return
     */
    public static String getMessage(String identifier, @NotNull String key, String... args) throws  MissingResourceException {
        ResourceBundle bundle;
        if( identifier == null ) {
            bundle = propertiesHashMap.get("minigames");
        } else {
            bundle = propertiesHashMap.get(identifier);
        }
        if(bundle == null) {
            String err = (identifier == null) ? "NULL" : identifier;
            Collection<String> errArgs = new ArrayList<>();
            errArgs.add("Identifier was invalid: " + err);
            errArgs.add(key);
            Collections.addAll(errArgs,args);
            throw new MissingResourceException(err,"MessageManager",key);
        }
        return String.format(bundle.getString(key), args);

    }

    public static String getMinigamesMessage(String key, String... args) throws MissingResourceException{
        return getMessage(null,key,args);
    }
}

