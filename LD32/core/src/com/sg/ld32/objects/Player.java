package com.sg.ld32.objects;

import java.util.HashMap;
import java.util.Map;

import box2dLight.ConeLight;
import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.sg.ld32.LD32Main;
import com.sg.ld32.screens.GameScreen;
import com.sg.ld32.utilities.Values;

public class Player implements Drawable, Updatable, PhysicsObject, Swappable, Comparable<Drawable>, Killable{
	
	public Sprite sprite;
	public Body body;
	public float angle;
	ConeLight coneLight;
	PointLight pointLight;
	Color coneLightColor;
	
	public Sprite sprite_0;
	public Sprite sprite_1;
	public Sprite sprite_2;
	public Sprite sprite_3;
	public Sprite sprite_4;
	public Sprite sprite_5;
	public Sprite sprite_6;
	
	private Vector2 unitVector;
	private Vector2 staffLightVector;
	private Vector2 pointerPosition;
	private Vector3 pointerPosition3d;
	
	private GameScreen gameScreen;
	
	boolean alreadyClicked = false;
	private boolean swapWaiting = false;
	private Vector2 swapLocation;
	
	boolean dyingFire = false;
	boolean dyingFall = false;
	boolean dyingBleed = false;
	int deathTimer = 0;
	
	public int cooldownTimer = 0;
	public int charges = 3;
	
	@Override
	public int compareTo(Drawable o) {
		if (this.getY() == o.getY() ){
			return Float.compare(this.getX(), o.getX());
		} else {
			return -Float.compare(this.getY(), o.getY());
		}
	}
	
	public Player(GameScreen gameScreen){
		this.gameScreen = gameScreen;
		
		unitVector = new Vector2(0.3f, 0.3f);
		staffLightVector = new Vector2(0.15f, 0.15f);
		pointerPosition = new Vector2();
		pointerPosition3d = new Vector3();
		
		BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(0, 0);
       
        if (!gameScreen.world.isLocked()) body = gameScreen.world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.25f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        Fixture fixture = getBody().createFixture(fixtureDef);
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put("type", "Player");
        userData.put("parent", this);
        fixture.setUserData(userData);

        shape.dispose();
        
        getBody().setFixedRotation(true);
        
        sprite = new Sprite(LD32Main.assetManager.get("player_0.png", Texture.class));
		sprite.setSize(0.5f, 0.5f);
		
		sprite_0 = new Sprite(LD32Main.assetManager.get("player_0.png", Texture.class));
		sprite_0.setSize(0.5f, 0.5f);
		sprite_1 = new Sprite(LD32Main.assetManager.get("player_1.png", Texture.class));
		sprite_1.setSize(0.5f, 0.5f);
		sprite_2 = new Sprite(LD32Main.assetManager.get("player_2.png", Texture.class));
		sprite_2.setSize(0.5f, 0.5f);
		sprite_3 = new Sprite(LD32Main.assetManager.get("player_3.png", Texture.class));
		sprite_3.setSize(0.5f, 0.5f);
		sprite_4 = new Sprite(LD32Main.assetManager.get("player_4.png", Texture.class));
		sprite_4.setSize(0.5f, 0.5f);
		sprite_5 = new Sprite(LD32Main.assetManager.get("player_5.png", Texture.class));
		sprite_5.setSize(0.5f, 0.5f);
		sprite_6 = new Sprite(LD32Main.assetManager.get("player_6.png", Texture.class));
		sprite_6.setSize(0.5f, 0.5f);
		
		
		coneLightColor = new Color(0.1f,0.7f,0.1f,0.8f);
		coneLight = new ConeLight(gameScreen.rayHandler, 50, coneLightColor, 10, getBody().getPosition().x, getBody().getPosition().x, 0, 20);
		coneLight.setSoftnessLength(5);
		
		pointLight = new PointLight(gameScreen.rayHandler, 50, new Color(0.1f,1.0f,0.4f,1.0f), 0.8f, getBody().getPosition().x, getBody().getPosition().x);
		pointLight.setXray(true);
	}
	

	@Override
	public void draw(SpriteBatch spriteBatch) {
		unitVector.setAngle(angle + 45);
		staffLightVector.setAngle(angle + 45);
		coneLight.setPosition(getBody().getPosition().add(unitVector).add(0, 0.4f));
		coneLight.setDirection(angle);
		
		pointLight.setPosition(getBody().getPosition().add(staffLightVector).add(0, 0.4f));
		
		sprite_0.setOriginCenter();
		sprite_0.setRotation(angle + 90);
		sprite_1.setOriginCenter();
		sprite_1.setRotation(angle + 90);
		sprite_2.setOriginCenter();
		sprite_2.setRotation(angle + 90);
		sprite_3.setOriginCenter();
		sprite_3.setRotation(angle + 90);
		sprite_4.setOriginCenter();
		sprite_4.setRotation(angle + 90);
		sprite_5.setOriginCenter();
		sprite_5.setRotation(angle + 90);
		sprite_6.setOriginCenter();
		sprite_6.setRotation(angle + 90);
		
		sprite_0.setPosition(getBody().getPosition().x - 0.25f, getBody().getPosition().y - 0.25f - (1-sprite_0.getScaleX()));
		sprite_0.draw(spriteBatch);
		
		sprite_1.setPosition(getBody().getPosition().x - 0.25f, getBody().getPosition().y - 0.25f + 0.1f - (1-sprite_0.getScaleX()));
		sprite_1.draw(spriteBatch);
		
		sprite_2.setPosition(getBody().getPosition().x - 0.25f, getBody().getPosition().y - 0.25f + 0.2f - (1-sprite_0.getScaleX()));
		sprite_2.draw(spriteBatch);
		
		sprite_3.setPosition(getBody().getPosition().x - 0.25f, getBody().getPosition().y - 0.25f + 0.25f - (1-sprite_0.getScaleX()));
		sprite_3.draw(spriteBatch);
		
		sprite_4.setPosition(getBody().getPosition().x - 0.25f, getBody().getPosition().y - 0.25f + 0.3f - (1-sprite_0.getScaleX()));
		sprite_4.draw(spriteBatch);
		
		sprite_5.setPosition(getBody().getPosition().x - 0.25f, getBody().getPosition().y - 0.25f + 0.35f - (1-sprite_0.getScaleX()));
		sprite_5.draw(spriteBatch);
		
		sprite_6.setPosition(getBody().getPosition().x - 0.25f, getBody().getPosition().y - 0.25f + 0.4f - (1-sprite_0.getScaleX()));
		sprite_6.draw(spriteBatch);
	}


	@Override
	public void update() {
		if (dyingFall || dyingBleed || dyingFire){
			MassData deadMassData = new MassData();
			getBody().setLinearVelocity(0, 0);
			getBody().setMassData(deadMassData);
			deathTimer++;
			if (isDeathComplete()){
				gameScreen.endGame();
			}
			LD32Main.screenController.zoomCameraTo(LD32Main.screenController.getCamera().zoom - 0.02f);
			gameScreen.darken();
			coneLightColor.lerp(0,0,0,0,0.01f);
			coneLight.setColor(coneLightColor);
			
			if (dyingFall){
				float spriteScale = -0.01f;
				if (sprite_0.getScaleX() > 0){
					sprite_0.scale(spriteScale);
					sprite_1.scale(spriteScale);
					sprite_2.scale(spriteScale);
					sprite_3.scale(spriteScale);
					sprite_4.scale(spriteScale);
					sprite_5.scale(spriteScale);
					sprite_6.scale(spriteScale);
					
					if (sprite_0.getColor().a > 0.1f){
						sprite_0.setColor(1, 1, 1, sprite_0.getColor().a - 0.02f);
						sprite_1.setColor(1, 1, 1, sprite_0.getColor().a - 0.02f);
						sprite_2.setColor(1, 1, 1, sprite_0.getColor().a - 0.02f);
						sprite_3.setColor(1, 1, 1, sprite_0.getColor().a - 0.02f);
						sprite_4.setColor(1, 1, 1, sprite_0.getColor().a - 0.02f);
						sprite_5.setColor(1, 1, 1, sprite_0.getColor().a - 0.02f);
						sprite_6.setColor(1, 1, 1, sprite_0.getColor().a - 0.02f);
					} else {
						sprite_0.setColor(1, 1, 1, 0);
						sprite_1.setColor(1, 1, 1, 0);
						sprite_2.setColor(1, 1, 1, 0);
						sprite_3.setColor(1, 1, 1, 0);
						sprite_4.setColor(1, 1, 1, 0);
						sprite_5.setColor(1, 1, 1, 0);
						sprite_6.setColor(1, 1, 1, 0);
					}
				}
			}
		} else {
			if (swapWaiting){
				getBody().setTransform(swapLocation, getBody().getAngle());
				swapWaiting = false;
			}
			
			if (cooldownTimer < Values.PLAYER_BULLET_COOLDOWN){
				//increment, we know we aren't doing anything here
				cooldownTimer++;
			} else if (cooldownTimer >= Values.PLAYER_BULLET_COOLDOWN && charges < Values.PLAYER_BULLET_CHARGES) {
				cooldownTimer = 0;
				charges++;
			}
			
			
			pointerPosition3d.x = Gdx.input.getX();
			pointerPosition3d.y = Gdx.input.getY();
			LD32Main.screenController.getCamera().unproject(pointerPosition3d);
			pointerPosition.x = pointerPosition3d.x;
			pointerPosition.y = pointerPosition3d.y;
			angle = pointerPosition.sub(getBody().getPosition()).angle();
			
			if (Gdx.input.justTouched()){
				if (charges > 0){
					fireTeleporterBullet(angle);
					charges--;
				}
			}
			
			if (Gdx.input.isKeyPressed(Input.Keys.W) ||
				Gdx.input.isKeyPressed(Input.Keys.A) ||
				Gdx.input.isKeyPressed(Input.Keys.S) ||
				Gdx.input.isKeyPressed(Input.Keys.D)){
				
				if(Gdx.input.isKeyPressed(Input.Keys.W)){
					getBody().applyForceToCenter(0, Values.PLAYER_ACCELERATION, true);
				}
				if(Gdx.input.isKeyPressed(Input.Keys.A)){
					getBody().applyForceToCenter(-Values.PLAYER_ACCELERATION, 0, true);		
					}
				if(Gdx.input.isKeyPressed(Input.Keys.S)){
					getBody().applyForceToCenter(0, -Values.PLAYER_ACCELERATION, true);
				}
				if(Gdx.input.isKeyPressed(Input.Keys.D)){
					getBody().applyForceToCenter(Values.PLAYER_ACCELERATION, 0, true);
				}
			} else {
				if (getBody().getLinearVelocity().len() > 0){
					getBody().setLinearVelocity(getBody().getLinearVelocity().scl(Values.PLAYER_DECELERATION));
				}
			}
			
			
			if (getBody().getLinearVelocity().len() > Values.PLAYER_MAX_SPEED){
				getBody().setLinearVelocity(getBody().getLinearVelocity().nor().scl(Values.PLAYER_MAX_SPEED));
			}
		}
	}

	public void fireTeleporterBullet(float angle){
		Vector2 location = getBody().getPosition().cpy();
		unitVector.setAngle(angle);
		location.add(unitVector);
		gameScreen.updatables.add(new TeleporterBullet(gameScreen, location.x, location.y, angle));
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


	@Override
	public void swapTo(Vector2 position) {
		swapWaiting = true;
		swapLocation = position;
	}


	@Override
	public Vector2 getPosition() {
		return getBody().getPosition();
	}

	@Override
	public void killFire() {
		dyingFire = true;
	}

	@Override
	public void killFall() {
		dyingFall = true;
	}

	@Override
	public void killBleed() {
		dyingBleed = true;
	}
	
	@Override
	public void checkFall(){
		boolean grounded = false;
		
		Vector2 bottomLeft = getPosition().cpy().add(-0.25f, -0.25f);
		bottomLeft.x = (int) Math.round(bottomLeft.x);
		bottomLeft.y = (int) Math.round(bottomLeft.y);
		if (gameScreen.floors.containsKey(bottomLeft)) grounded = true;
		
		Vector2 bottomRight = getPosition().cpy().add(0.25f, -0.25f);
		bottomRight.x = (int) Math.round(bottomRight.x);
		bottomRight.y = (int) Math.round(bottomRight.y);
		if (gameScreen.floors.containsKey(bottomRight)) grounded = true;
		
		Vector2 topLeft = getPosition().cpy().add(-0.25f, 0.25f);
		topLeft.x = (int) Math.round(topLeft.x);
		topLeft.y = (int) Math.round(topLeft.y);
		if (gameScreen.floors.containsKey(topLeft)) grounded = true;
		
		Vector2 topRight = getPosition().cpy().add(0.25f, 0.25f);
		topRight.x = (int) Math.round(topRight.x);
		topRight.y = (int) Math.round(topRight.y);
		if (gameScreen.floors.containsKey(topRight)) grounded = true;
		
		if (!grounded){
			killFall();
		}
	}
	
	@Override
	public boolean isDeathComplete(){
		return deathTimer > Values.PLAYER_DEATH_TIME;
	}
}
