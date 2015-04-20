package com.sg.ld32.objects;

public interface Disposable {
	public abstract boolean isDisposed();
	public abstract void disposeCallback();
}
