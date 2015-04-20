package com.sg.ld32.objects;

import java.util.HashMap;
import java.util.Map;

import box2dLight.PointLight;

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

public class Arrow implements PhysicsObject, Updatable, Disposable, Drawable, Comparable<Drawable>, Swappable{
	
	private Body body;
	private Vector2 velocity;
	private Sprite sprite;
	
	private float angle;
	
	private int life = 0;
	private boolean disposed = false;
	
	private GameScreen gameScreen;
	
	private boolean swapWaiting = false;
	private Vector2 swapLocation;
	
	@Override
	public int compareTo(Drawable o) {
		if (this.getY() == o.getY() ){
			return Float.compare(this.getX(), o.getX());
		} else {
			return -Float.compare(this.getY(), o.getY());
		}
	}
	
	public Arrow(GameScreen gameScreen, float x, float y, float angle){
		this.gameScreen = gameScreen;
		this.angle = angle;
		
		sprite = new Sprite(LD32Main.assetManager.get("arrow.png", Texture.class));
		sprite.setSize(1f, 0.2f);
		
		velocity = new Vector2(1,1);
		velocity.setLength(Values.ARROW_SPEED);
		velocity.setAngle(angle);
		
		BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.angle =  (float) Math.toRadians(angle);
        bodyDef.fixedRotation = true;
       
        if (!gameScreen.world.isLocked()) body = gameScreen.world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.1f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 100;

        Fixture fixture = getBody().createFixture(fixtureDef);
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put("type", "Arrow");
        userData.put("parent", this);
        fixture.setUserData(userData);
        
        shape.dispose();
	}

	@Override
	public void update() {
		if (swapWaiting){
			getBody().setTransform(swapLocation, getBody().getAngle());
			swapWaiting = false;
		}
		
		getBody().setLinearVelocity(velocity);
		life++;
		if (life > Values.ARROW_LIFE_MAX) dispose();
	}

	@Override
	public Body getBody() {
		return body;
	}
	
	public int getLife(){
		return life;
	}
	
	public void dispose(){
		disposed = true;
	}
	
	@Override
	public boolean isDisposed(){
		return disposed;
	}
	

	@Override
	public void disposeCallback() {
		if(!gameScreen.world.isLocked() && body != null) gameScreen.world.destroyBody(body);
	}

	@Override
	public void draw(SpriteBatch spriteBatch) {
		sprite.setPosition(getBody().getPosition().x - 0.5f, getBody().getPosition().y - 0.1f);
		sprite.setOriginCenter();
		sprite.setRotation(angle);
		sprite.draw(spriteBatch);
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
	public void swapTo(Vector2 position) {
		swapWaiting = true;
		swapLocation = position;
	}

	@Override
	public Vector2 getPosition() {
		return getBody().getPosition();
	}
}