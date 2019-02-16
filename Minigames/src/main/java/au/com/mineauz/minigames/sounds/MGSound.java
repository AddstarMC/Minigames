package au.com.mineauz.minigames.sounds;

import org.bukkit.Sound;

public class MGSound {
    private float volume = 10f;
    private float pitch = 1f;
    private Sound sound;
    private int count = 1;
    private long delay = 20L;
    private int timesPlayed = 0;

    public MGSound(Sound sound) {
        this.sound = sound;
    }

    public MGSound(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.pitch = pitch;
        this.volume = volume;
    }

    public MGSound(Sound sound, int count, long delay) {
        this.sound = sound;
        this.count = count;
        this.delay = delay;
    }

    public MGSound(Sound sound, float volume, float pitch, int count, long delay) {
        this.sound = sound;
        this.pitch = pitch;
        this.volume = volume;
        this.count = count;
        this.delay = delay;
    }

    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public int getCount() {
        return count;
    }

    public long getDelay() {
        return delay;
    }

    public int getTimesPlayed() {
        return timesPlayed;
    }

    public void setTimesPlayed(int count) {
        timesPlayed = count;
    }

    public MGSound clone() {
        MGSound s = new MGSound(sound, volume, pitch, count, delay);
        s.setTimesPlayed(getTimesPlayed());
        return s;
    }
}
