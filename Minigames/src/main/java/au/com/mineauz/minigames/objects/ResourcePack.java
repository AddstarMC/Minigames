package au.com.mineauz.minigames.objects;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.ResourcePackManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class ResourcePack implements ConfigurationSerializable {
    private static final String ext = "resourcepack";
    private final String name;
    private final Component displayName;
    private final URL url;
    private final File local;
    private final String description;
    /**
     * Unique SH1 hash
     */
    private byte[] hash;
    private boolean valid = false;

    /**
     * Instantiates a new Resource pack.
     *
     * @param input the input
     */
    public ResourcePack(final Map<String, Object> input) {
        URL url1;
        this.name = MiniMessage.miniMessage().stripTags((String) input.get("name"));
        this.displayName = MiniMessage.miniMessage().deserialize((String) input.get("name"));
        this.description = (String) input.get("description");
        try {
            url1 = new URL((String) input.get("url"));
        } catch (final MalformedURLException e) {
            Minigames.getCmpnntLogger().warn("The URL defined in the configuration is malformed: ", e);
            url1 = null;
            this.valid = false;
        }
        this.url = url1;
        final Path path = ResourcePackManager.getResourceDir();
        this.local = new File(path.toFile(), name + '.' + ext);
        this.validate();
    }

    /**
     * Instantiates a new Resource pack.
     *
     * @param displayName the name
     * @param url         the url
     */
    public ResourcePack(final Component displayName, final @NotNull URL url) {
        this(displayName, url, null);
    }

    /**
     * Instantiates a new Resource pack.
     *
     * @param displayName the name
     * @param url         the url
     * @param file        the file
     */
    public ResourcePack(final Component displayName, final @NotNull URL url, final File file) {
        this(displayName, url, file, null);
    }

    /**
     * Instantiates a new Resource pack.
     *
     * @param displayName the name
     * @param url         the url
     * @param file        the file
     * @param description the description
     */
    public ResourcePack(final Component displayName, final @NotNull URL url, final File file, final String description) {
        this.name = PlainTextComponentSerializer.plainText().serialize(displayName);
        this.displayName = displayName;
        final Path path = ResourcePackManager.getResourceDir();
        this.local = file != null ? file : new File(path.toFile(), name + '.' + ext);
        this.url = url;
        this.description = description;
        this.validate();
    }

    /**
     * Statically create the class
     *
     * @param map A map of values
     * @return ResourcePack resource pack
     */
    public static ResourcePack valueOf(final Map<String, Object> map) {
        return deserialize(map);
    }

    /**
     * Statically create the class
     *
     * @param map A map of values
     * @return ResourcePack resource pack
     */
    public static ResourcePack deserialize(final Map<String, Object> map) {
        return new ResourcePack(map);
    }

    private void validate() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        try {
            scheduler.runTaskAsynchronously(Minigames.getPlugin(), () -> {
                synchronized (this.local) {
                    if (this.local.exists()) {
                        //set the local hash;
                        try (final FileInputStream fis = new FileInputStream(this.local)) {
                            this.hash = this.getSH1Hash(fis);
                        } catch (final IOException e) {
                            Minigames.getCmpnntLogger().error("", e);
                        }
                        //Validate the remote file hash = local.
                        final File temp;
                        try (final InputStream in = this.url.openStream()) {
                            temp = File.createTempFile(this.name, ext);
                            Files.copy(in, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } catch (final IOException e) {
                            Minigames.getCmpnntLogger().warn("", e);
                            this.valid = false;
                            return;
                        }
                        try (final FileInputStream fis = new FileInputStream(temp)) {
                            final byte[] has = this.getSH1Hash(fis);
                            if (Arrays.equals(has, this.hash)) {
                                Minigames.getCmpnntLogger().info(Component.text("Resource Pack: ").append(this.displayName).append(Component.text(" passed external validation")));
                                this.valid = true;
                                return;
                            }
                        } catch (final IOException e) {
                            Minigames.getCmpnntLogger().warn("", e);
                            this.valid = false;
                            return;
                        }
                        // Local did not match hash on remote so copy the remote over the local.
                        try (final FileInputStream fis = new FileInputStream(temp)) {
                            Files.copy(fis, this.local.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } catch (final IOException e) {
                            Minigames.getCmpnntLogger().error("", e);
                        }
                        //set the new hash as long as it's not null its valid
                        this.setLocalHash();
                    } else {
                        this.download(this.local);
                        this.setLocalHash();
                        this.valid = true;
                    }
                }
            });
        } catch (Exception e) {
            Minigames.getCmpnntLogger().error("", e);
            this.valid = false;
        }
    }

    private byte[] getSH1Hash(final InputStream fis) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-1");
            try {
                int n = 0;
                final byte[] buffer = new byte[8192];
                while (n != -1) {
                    n = fis.read(buffer);
                    if (n > 0) {
                        digest.update(buffer, 0, n);
                    }
                }
            } catch (final IOException e) {
                Minigames.getCmpnntLogger().warn("", e);
                return null;
            }
            return digest.digest();
        } catch (final NoSuchAlgorithmException e) {
            Minigames.getCmpnntLogger().error("", e);
            return null;
        }
    }

    /**
     * Generate the local SH1 hash
     */
    private void setLocalHash() {
        if (this.local != null && this.local.exists()) {
            try (final FileInputStream fis = new FileInputStream(this.local)) {
                this.hash = this.getSH1Hash(fis);
                this.valid = true;
                return;
            } catch (final IOException e) {
                Minigames.getCmpnntLogger().warn("", e);
                this.valid = false;
                return;
            }
        }
        this.valid = false;
    }

    /**
     * Download.
     *
     * @param file the file
     */
    public void download(final File file) {
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    this.valid = false;
                    return;
                }
            }
        }
        try (final InputStream in = this.url.openStream()) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            Minigames.getCmpnntLogger().error("", e);
            this.valid = false;
        }
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets Displayname.
     *
     * @return the name
     */
    public Component getDisplayName() {
        return this.displayName;
    }

    /**
     * True if the resource pack is validated.
     *
     * @return the boolean
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Get sh 1 hash byte [ ].
     *
     * @return the byte [ ]
     */
    @SuppressWarnings("syncronized")
    public byte[] getSH1Hash() {
        return this.hash;
    }

    /**
     * Gets the Publicly available URL
     *
     * @return url url
     */
    public URL getUrl() {
        return this.url;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        final Map<String, Object> result = new HashMap<>();
        result.put("name", this.displayName);
        result.put("url", this.url.toString());
        result.put("description", this.description);

        return result;
    }

}
