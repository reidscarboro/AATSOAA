package com.sg.ld32.objects;

import java.util.HashMap;
import java.util.Map;

import box2dLight.PointLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.sg.ld32.screens.GameScreen;
import com.sg.ld32.utilities.Values;

public class TeleporterBullet implements PhysicsObject, Updatable, Disposable{
	
	private Body body;
	private PointLight pointLight;
	
	private Vector2 velocity;
	
	private int life = 0;
	private boolean disposed = false;
	
	private GameScreen gameScreen;
	
	public TeleporterBullet(GameScreen gameScreen, float x, float y, float angle){
		this.gameScreen = gameScreen;
		
		velocity = new Vector2(1,1);
		velocity.setLength(Values.PLAYER_BULLET_SPEED);
		velocity.setAngle(angle);
		
		BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
       
        if (!gameScreen.world.isLocked()) body = gameScreen.world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.05f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        Fixture fixture = getBody().createFixture(fixtureDef);
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put("type", "TeleporterBullet");
        userData.put("parent", this);
        fixture.setUserData(userData);
        
        shape.dispose();
        getBody().setFixedRotation(true);
        
        pointLight = new PointLight(gameScreen.rayHandler, 50, new Color(0.1f,1.0f,0.6f,1.0f), 0, x, y);
	}

	@Override
	public void update() {
		getBody().setLinearVelocity(velocity);
		if (pointLight != null){
			if (pointLight.getDistance() < 3){
				pointLight.setDistance(pointLight.getDistance() + 0.1f);
			}
			pointLight.setPosition(getBody().getPosition());
		}
		life++;
		if (life > Values.PLAYER_BULLET_LIFE_MAX) dispose();
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
		if (pointLight != null){
			pointLight.remove();
		}
		if(!gameScreen.world.isLocked() && body != null) gameScreen.world.destroyBody(body);
	}
}
