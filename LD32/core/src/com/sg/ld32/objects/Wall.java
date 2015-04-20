package com.sg.ld32.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import box2dLight.PointLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.sg.ld32.LD32Main;
import com.sg.ld32.screens.GameScreen;
import com.sg.ld32.utilities.Values;

public class Wall implements Drawable, PhysicsObject, Comparable<Drawable>, Swappable, Updatable{
	
	public float x;
	public float y;
	public Sprite sprite;
	public Sprite ledgeSprite;
	public Sprite lightSprite;
	public Body body;
	public Color pointLightColor;
	public PointLight pointLight;
	
	private GameScreen gameScreen;
	private Random rng;
	
	private boolean swapWaiting = false;
	private Vector2 swapLocation;
	
	private int lightSide = 0;
	
	@Override
	public int compareTo(Drawable o) {
		if (this.getY() == o.getY() ){
			return Float.compare(this.getX(), o.getX());
		} else {
			return -Float.compare(this.getY(), o.getY());
		}
	}
	
	public Wall(GameScreen gameScreen, int x, int y){
		this.x = x;
		this.y = y;
		this.gameScreen = gameScreen;
		
		rng = new Random();
		
		BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
       
        if (!gameScreen.world.isLocked()) body = gameScreen.world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        Fixture fixture = getBody().createFixture(fixtureDef);
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put("type", "Wall");
        userData.put("parent", this);
        fixture.setUserData(userData);

        shape.dispose();
        
        sprite = new Sprite(LD32Main.assetManager.get("wall.png", Texture.class));
		sprite.setSize(1, 1);
		sprite.setPosition(getBody().getPosition().x - 0.5f, getBody().getPosition().y);
		
		ledgeSprite = new Sprite(LD32Main.assetManager.get("wallLedge.png", Texture.class));
		ledgeSprite.setSize(1, 0.5f);
		ledgeSprite.setPosition(getBody().getPosition().x - 0.5f, getBody().getPosition().y - 0.5f);
	}

	@Override
	public void draw(SpriteBatch spriteBatch) {
		if (pointLight != null){
			float distanceMod = -0.5f + rng.nextFloat() * 1f;
			if (pointLight.getDistance() < Values.LIGHT_MIN){
				distanceMod = Math.abs(distanceMod);
			} else if (pointLight.getDistance() > Values.LIGHT_MAX){
				distanceMod = Math.abs(distanceMod) * -1;
			}
			pointLight.setDistance(pointLight.getDistance() + distanceMod);
			if (lightSide == 1){
				pointLight.setPosition(getBody().getPosition().x + 0.75f, getBody().getPosition().y);
			} else if (lightSide == 3){
				pointLight.setPosition(getBody().getPosition().x - 0.75f, getBody().getPosition().y);
			} else {
				pointLight.setPosition(getBody().getPosition().x, getBody().getPosition().y - 0.75f);
			}
		}
		
		sprite.setPosition(getBody().getPosition().x - 0.5f, getBody().getPosition().y);
		sprite.draw(spriteBatch);
		drawLedge(spriteBatch);
	}
	
	public void drawLedge(SpriteBatch spriteBatch) {
		ledgeSprite.setPosition(getBody().getPosition().x - 0.5f, getBody().getPosition().y - 0.5f);
		ledgeSprite.draw(spriteBatch);
		
		if (lightSide > 0 && lightSprite != null){
			if (lightSide == 1){
				lightSprite.setPosition(getBody().getPosition().x + 0.5f, getBody().getPosition().y - 0.5f);
			} else if (lightSide == 2){
			} else if (lightSide == 3){
				lightSprite.setPosition(getBody().getPosition().x - 1.5f, getBody().getPosition().y - 0.5f);
			} else {
				lightSprite.setPosition(getBody().getPosition().x - 0.5f, getBody().getPosition().y - 1.0f);
			}
			lightSprite.draw(spriteBatch);
		}
	}

	@Override
	public float getX() {
		return getBody().getPosition().x;
	}

	@Override
	public float getY() {
		return getBody().getPosition().y;
	}
	
	@Override
	public Body getBody() {
		return body;
	}
	
	public void putLight(int side){
		if (side > 0){
			if (side == 1){
				lightSide = side;
				pointLightColor = new Color(0.8f,0.6f,0.1f,0.6f);
				pointLight = new PointLight(gameScreen.rayHandler, 50, pointLightColor, 10, x + 0.75f, y);
				lightSprite = new Sprite(LD32Main.assetManager.get("light_right.png", Texture.class));
				lightSprite.setSize(1, 1);
				lightSprite.setPosition(getBody().getPosition().x + 0.5f, getBody().getPosition().y - 0.5f);
			} else if (side == 2){
				lightSide = side;
				//pointLight = new PointLight(gameScreen.rayHandler, 50, new Color(0.8f,0.1f,0.5f,0.8f), 10, x, y + 1);
				//weird bug, I dont have the time to fix it...
			} else if (side == 3){
				lightSide = side;
				pointLightColor = new Color(0.8f,0.6f,0.1f,0.6f);
				pointLight = new PointLight(gameScreen.rayHandler, 50, pointLightColor, 10, x - 0.75f, y);
				lightSprite = new Sprite(LD32Main.assetManager.get("light_left.png", Texture.class));
				lightSprite.setSize(1, 1);
				lightSprite.setPosition(getBody().getPosition().x - 1.5f, getBody().getPosition().y - 0.5f);
			} else {
				lightSide = side;
				pointLightColor = new Color(0.8f,0.6f,0.1f,0.6f);
				pointLight = new PointLight(gameScreen.rayHandler, 50, pointLightColor, 10, x, y - 0.75f);
				lightSprite = new Sprite(LD32Main.assetManager.get("light_down.png", Texture.class));
				lightSprite.setSize(1, 1);
				lightSprite.setPosition(getBody().getPosition().x - 0.5f, getBody().getPosition().y - 1.0f);
			}
		}
	}
	
	@Override
	public void update() {
		if (swapWaiting){
			getBody().setTransform(swapLocation, getBody().getAngle());
			swapWaiting = false;
		}
	}
	
	public void disposeLight(){
		if (pointLight != null){
			pointLight.remove();
		}
	}

	@Override
	public void swapTo(Vector2 position) {
		swapWaiting = true;
		swapLocation = position;
	}

	@Override
	public Vector2 getPosition() {
		return getBody().getPosition();
	}
	
	public void setType(int type){
		if (type == 1){
			sprite.setTexture(LD32Main.assetManager.get("wall_h.png", Texture.class));
		} else if (type == 2){
			sprite.setTexture(LD32Main.assetManager.get("wall_v.png", Texture.class));
		}
	}
	
	public void darken(){
		if (pointLight != null){
			pointLightColor.lerp(0,0,0, 0, 0.01f);
			pointLight.setColor(pointLightColor);
		}
	}
}
