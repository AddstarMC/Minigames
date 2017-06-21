package au.com.mineauz.minigames;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.StreamReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 21/06/2017.
 */
public class TestUtilities {

    public TestUtilities (){}

    public FileConfiguration createTestConfig(){
        FileConfiguration config = YamlConfiguration.loadConfiguration(
                new InputStreamReader(this.getClass().getResourceAsStream("/config.yml")));
        return config;
    }
    public Logger getLogger(){
        return Logger.getAnonymousLogger();
    }
}
