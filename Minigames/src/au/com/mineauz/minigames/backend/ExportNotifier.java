package au.com.mineauz.minigames.backend;

public interface ExportNotifier {
	public void onProgress(String state, int count);
	public void onComplete();
	public void onError(Throwable e, String state, int count);
}
