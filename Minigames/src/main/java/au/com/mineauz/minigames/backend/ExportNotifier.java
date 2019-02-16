package au.com.mineauz.minigames.backend;

public interface ExportNotifier {
    void onProgress(String state, int count);

    void onComplete();

    void onError(Throwable e, String state, int count);
}
