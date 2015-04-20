package com.sg.ld32.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.sg.ld32.LD32Main;

public class TitleScreen extends GenericScreen {
	
	private Sprite titleSprite;
	private SpriteBatch spriteBatch;
	
	private BitmapFont font;
	
	public TitleScreen(){
		titleSprite = new Sprite(LD32Main.assetManager.get("title.png", Texture.class));
		titleSprite.setSize(720, 720);
		
		spriteBatch = new SpriteBatch();
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/minimeek.ttf"));
    	generator.scaleForPixelHeight(16);
		font = generator.generateFont(16); // font size 12 pixels
		font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		font.setScale(1);
	}

	@Override
	public void update() {
		if (Gdx.input.justTouched()){
			LD32Main.screenController.setCurrentScreen(new GameScreen());
		}
	}

	@Override
	public void draw() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		spriteBatch.setProjectionMatrix(LD32Main.screenController.getUICamera().combined);
		titleSprite.draw(spriteBatch);
		
		font.draw(spriteBatch, "We await your descent.", 500, 70);
		font.draw(spriteBatch, "Click to begin...", 580, 50);
		
		spriteBatch.end();
	}

}
