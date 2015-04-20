package com.sg.ld32.objects;

import com.badlogic.gdx.math.Vector2;

public interface Swappable {
	public abstract void swapTo(Vector2 position);
	public abstract Vector2 getPosition();
}
