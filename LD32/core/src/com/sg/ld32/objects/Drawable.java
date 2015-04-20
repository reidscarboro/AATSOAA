package com.sg.ld32.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Drawable {
	public abstract void draw(SpriteBatch spriteBatch);
	public abstract float getX();
	public abstract float getY();
}
