package com.sg.ld32;

import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.sg.ld32.controllers.ScreenController;
import com.sg.ld32.screens.GameScreen;
import com.sg.ld32.screens.TitleScreen;

public class LD32Main extends ApplicationAdapter {
	public static LD32Main ld32Main;
	public static ScreenController screenController;
	public static AssetManager assetManager;
	
	@Override
	public void create () {
		ld32Main = this;
		assetManager = new AssetManager();
		loadAssets();
		
		screenController = new ScreenController();
		screenController.setCurrentScreen(new TitleScreen());
	}
	

	@Override
	public void render () {
		screenController.getCurrentScreen().update();
		screenController.getCurrentScreen().draw();
	}
	
	public void loadAssets(){
		assetManager.setLoader(Texture.class, new TextureLoader(new InternalFileHandleResolver()));
		assetManager.load("title.png", Texture.class);
		
		assetManager.load("player_0.png", Texture.class);
		assetManager.load("player_1.png", Texture.class);
		assetManager.load("player_2.png", Texture.class);
		assetManager.load("player_3.png", Texture.class);
		assetManager.load("player_4.png", Texture.class);
		assetManager.load("player_5.png", Texture.class);
		assetManager.load("player_6.png", Texture.class);
		
		assetManager.load("zombie_0.png", Texture.class);
		assetManager.load("zombie_1.png", Texture.class);
		assetManager.load("zombie_2.png", Texture.class);
		
		assetManager.load("fastZombie_0.png", Texture.class);
		assetManager.load("fastZombie_1.png", Texture.class);
		assetManager.load("fastZombie_2.png", Texture.class);
		
		assetManager.load("archer_0.png", Texture.class);
		assetManager.load("archer_1.png", Texture.class);
		assetManager.load("archer_2.png", Texture.class);
		
		assetManager.load("wall.png", Texture.class);
		assetManager.load("wall_h.png", Texture.class);
		assetManager.load("wall_v.png", Texture.class);
		assetManager.load("wallLedge.png", Texture.class);
		assetManager.load("floor_1.png", Texture.class);
		assetManager.load("floor_2.png", Texture.class);
		assetManager.load("floor_3.png", Texture.class);
		assetManager.load("floor_4.png", Texture.class);
		assetManager.load("floor_5.png", Texture.class);
		assetManager.load("ledge.png", Texture.class);
		
		assetManager.load("light_down.png", Texture.class);
		assetManager.load("light_left.png", Texture.class);
		assetManager.load("light_right.png", Texture.class);
		
		assetManager.load("charge_orb.png", Texture.class);
		
		assetManager.load("arrow.png", Texture.class);
		
		while (!assetManager.update()) {
		}
	}
	
	public static Texture getFloorTexture(){
		Random rng = new Random();
		int floorVariant = rng.nextInt(5) + 1;
		Texture texture = LD32Main.assetManager.get("floor_"+floorVariant+".png", Texture.class);
		return texture;
	}
}
