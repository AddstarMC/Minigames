package au.com.mineauz.minigames.objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import au.com.mineauz.minigames.Minigames;

import au.com.mineauz.minigames.managers.ResourcePackManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Created for the AddstarMC Project. Created by Narimm on 12/02/2019.
 */
public class ResourcePack {
    
    private final String name;
    private final String ext = "resourcepack";
    public String getName() {
        return this.name;
    }
    
    private final URL url;
    private File local;
    private byte[] hash;

    public boolean isValid() {
        return valid;
    }

    private boolean valid;
    
    public String getDescription() {
        return this.description;
    }
    
    private String description;
    
    public ResourcePack(final String name, final @NotNull URL url) {
        this(name,url,null);
    }
    
    public ResourcePack(final String name, final @NotNull URL url, final File file) {
        this(name,url,file,null);
    }

    
    public ResourcePack(final String name, final @NotNull URL url, final File file, final String description ) {
        this.name = name;
        this.local = file;
        this.url = url;
        this.description = description;
        this.validateExternal();
    }
    
    
    private byte[] getSH1Hash(final File file)  {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            if (file != null && file.exists()) {
                try {
                    InputStream fis = new FileInputStream(file);
                    int n = 0;
                    byte[] buffer = new byte[8192];
                    while (n != -1) {
                        n = fis.read(buffer);
                        if (n > 0) {
                            digest.update(buffer, 0, n);
                        }
                    }
                }catch (IOException e) {
                    Minigames.log().warning(e.getMessage());
                    return null;
                }
            }else{
                Minigames.log().warning("File defined by Resource "+ name
                        +" does not exist and the resource is invalid");
                return null;
            }
            return digest.digest();
        }catch (NoSuchAlgorithmException e){
            Minigames.log().severe(e.getMessage());
            return null;
        }
    }
    
    private void validateExternal(){
        Bukkit.getScheduler().runTaskAsynchronously(Minigames.getPlugin(), () -> {
            Path path = ResourcePackManager.getResourceDir();
            if(local != null ){
                if(!local.exists()) {
                    download(local);
                } else {
                    try (InputStream in = url.openStream()) {
                        File temp = File.createTempFile(name,ext);
                        Files.copy(in, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        byte[] has = getSH1Hash(temp);
                        if(has.equals(hash)) {
                            Minigames.log().info("Resource Pack: " +name+ " passed external validation");
                            valid = true;
                        } else {
                            Files.copy(new FileInputStream(temp),local.toPath(),StandardCopyOption.REPLACE_EXISTING);
                            Files.delete(temp.toPath());
                            Minigames.log().warning("Resource Pack: " +name+ " the local resource did not match the external"
                                    + " and has been updated");
                            byte[] newHash  = getSH1Hash(local);
                            if(newHash == hash){
                                valid = true;
                            }
                        }
                    }catch (IOException e) {
                        Minigames.log().warning(e.getMessage());
                        e.printStackTrace();
                        valid = false;
                    }
                }
            } else {
                local = new File(path.toFile(),name+"."+ext);
                download(local);
            }
        }
        );
    }
    
    public void download(File file){
        try (InputStream in = url.openStream()) {
            if(!file.exists())file.createNewFile();
            Files.copy(in, file.toPath(),StandardCopyOption.REPLACE_EXISTING);
            hash = getSH1Hash(local);
            valid = true;
        } catch (IOException e) {
            Minigames.log().warning(e.getMessage());
            e.printStackTrace();
            valid = false;
        }
    }
    
    public byte[] getSH1Hash() {
        return hash;
    }
    
    /**
     * Gets the Publicly available URL
     * @return
     */
    public URL getUrl() {
        return this.url;
    }
}
