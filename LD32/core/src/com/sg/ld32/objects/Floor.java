package com.sg.ld32.objects;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sg.ld32.LD32Main;
import com.sg.ld32.screens.GameScreen;

public class Floor implements Drawable, Comparable<Floor>{
	
	float x;
	float y;
	Sprite sprite;
	Sprite ledgeSprite;
	
	@Override
	public int compareTo(Floor o) {
		if (this.y == o.y){
			return Float.compare(this.x, o.x);
		} else {
			return -Float.compare(this.y, o.y);
		}
	}
	
	public Floor(GameScreen gameScreen, int x, int y){
		this.x = x - 0.5f;
		this.y = y - 0.5f;
		sprite = new Sprite(LD32Main.getFloorTexture());
		sprite.setPosition(this.x, this.y);
		sprite.setSize(1, 1);
		sprite.setOriginCenter();
		Random rng = new Random();
		int rotations = rng.nextInt(4);
		for (int i = 0; i < rotations; i++){
			sprite.rotate90(true);
		}
		
		ledgeSprite = new Sprite(LD32Main.assetManager.get("ledge.png", Texture.class));
		ledgeSprite.setPosition(this.x, this.y - 5);
		ledgeSprite.setSize(1, 5);
	}

	@Override
	public void draw(SpriteBatch spriteBatch) {
		sprite.draw(spriteBatch);
	}
	
	public void drawLedge(SpriteBatch spriteBatch) {
		ledgeSprite.draw(spriteBatch);
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}
}
