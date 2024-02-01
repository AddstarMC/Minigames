package au.com.mineauz.minigames.backend;

public interface Notifier {
    void onProgress(String state, int count);

    void onComplete();

    void onError(Exception e, String state, int count);
}
