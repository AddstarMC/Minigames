package au.com.mineauz.minigames;

import be.seeseemelk.mockbukkit.ServerMock;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Enumeration;

public class MockBootstrap {
    public static @NotNull Minigames createPluginWithTestContext(ServerMock server) {
        return new MinigameBootstrap().createPlugin(new BootstrapContext() {
            @Override
            public @NotNull PluginMeta getConfiguration() {
                Enumeration<URL> resources = null;
                try {
                    resources = Minigames.class.getClassLoader().getResources("plugin.yml"); //todo use paper plugin yml

                    while (resources.hasMoreElements()) {
                        URL url = resources.nextElement();
                        PluginDescriptionFile description = new PluginDescriptionFile(url.openStream());

                        String mainClass = description.getMain();
                        if (Minigames.class.getClassLoader().getName().equals(mainClass))
                            return description;
                    }
                } catch (InvalidDescriptionException | IOException e) {
                    throw new RuntimeException(e);
                }

                throw new RuntimeException(new FileNotFoundException("plugin.yml"));
            }

            @Override
            public @NotNull Path getDataDirectory() {
                PluginMeta description = getConfiguration();

                try {
                    return Path.of(server.getPluginManager().createTemporaryDirectory(description.getName() + "-" + description.getVersion()).getPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public @NotNull ComponentLogger getLogger() {
                return ComponentLogger.logger(Minigames.class);
            }

            @Override
            public @NotNull Path getPluginSource() {
                return Path.of(Minigames.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            }
        });
    }
}
