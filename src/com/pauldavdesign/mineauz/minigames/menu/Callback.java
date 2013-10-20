package com.pauldavdesign.mineauz.minigames.menu;

public interface Callback<T> {
	public void setValue(T value);
	
	public T getValue();
}
