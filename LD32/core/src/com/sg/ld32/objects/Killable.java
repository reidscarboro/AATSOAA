package com.sg.ld32.objects;

public interface Killable {
	
	public abstract void killFire();
	public abstract void killFall();
	public abstract void killBleed();
	public abstract void checkFall();
	public abstract boolean isDeathComplete();
}
