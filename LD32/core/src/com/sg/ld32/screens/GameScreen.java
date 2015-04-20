package com.sg.ld32.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.sg.ld32.LD32Main;
import com.sg.ld32.objects.Archer;
import com.sg.ld32.objects.Arrow;
import com.sg.ld32.objects.Disposable;
import com.sg.ld32.objects.Drawable;
import com.sg.ld32.objects.Floor;
import com.sg.ld32.objects.Killable;
import com.sg.ld32.objects.Player;
import com.sg.ld32.objects.Swappable;
import com.sg.ld32.objects.TeleporterBullet;
import com.sg.ld32.objects.Updatable;
import com.sg.ld32.objects.Wall;
import com.sg.ld32.objects.Zombie;
import com.sg.ld32.utilities.Values;


public class GameScreen extends GenericScreen{
	
	public Box2DDebugRenderer debugRenderer;
	public Matrix4 debugMatrix;
	
	public SpriteBatch spriteBatch;
	
	private BitmapFont scoreFont;
	private BitmapFont scoreFontBG;
	private BitmapFont standardFont;
	
	private Sprite chargeOrb;
	
	public World world;
	public RayHandler rayHandler;
	public Color ambientLight;
	public Player player;
	public Map<Vector2, Floor> floors;
	public Map<Vector2, Wall> walls;
	public Set<Drawable> drawables;
	public Set<Updatable> updatables;
	public Set<Updatable> pendingUpdatables;
	
	//level generation stuff
	public int currentMaxY = 0;
	public int currentCenterX = 0;
	public int currentWidth = 4;
	
	public int score = 0;
	public int relocations = 0;
	public int enemiesKilled = 0;
	
	public GameScreen(){
		loadFonts();
		
		debugRenderer=new Box2DDebugRenderer();
		chargeOrb = new Sprite(LD32Main.assetManager.get("charge_orb.png", Texture.class));
		chargeOrb.setSize(40, 40);
		
		spriteBatch = new SpriteBatch();
		drawables = new HashSet<Drawable>();
		updatables = new HashSet<Updatable>();
		pendingUpdatables = new HashSet<Updatable>();
		floors = new HashMap<Vector2, Floor>();
		walls = new HashMap<Vector2, Wall>();
		
		world = new World(new Vector2(0, 0), true);
		createCollisionListener();
		
		ambientLight = new Color(0.3f, 0.3f, 0.4f, 0.11f);
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(ambientLight);
		
		player = new Player(this);
		drawables.add(player);
		
		addSquare(0, 10, 10, 4, true);
		addSquare(0, 10, 5, 5, true);
		addSquare(0, 16, 4, 10, true);
		
		removeSquare(0, 0, 7, 8);
		addSquare(0, 0, 5, 5, false);
		
		Vector2 cursor = new Vector2();
		
		cursor.x = -8;
		cursor.y = 9;
		addWall((int)cursor.x, (int)cursor.y);
		walls.get(cursor).putLight(4);
		
		addWall(-7,9);
		addWall(-6,9);
		addWall(-5,9);
		addWall(-4,9);
		addWall(-3,9);
		
		cursor.x = -2;
		cursor.y = 9;
		addWall((int)cursor.x, (int)cursor.y);
		walls.get(cursor).putLight(4);
		
		cursor.x = 2;
		cursor.y = 9;
		addWall((int)cursor.x, (int)cursor.y);
		walls.get(cursor).putLight(4);
		
		addWall(3,9);
		addWall(4,9);
		addWall(5,9);
		
		cursor.x = 6;
		cursor.y = 9;
		addWall((int)cursor.x, (int)cursor.y);
		walls.get(cursor).putLight(4);
		
		LD32Main.screenController.getCamera().zoom = Values.CAMERA_ZOOM;
		
		updateRows();
	}

	@Override
	public void update() {
		
		world.step(1/60f, 4, 2);
		updateRows();
		purgeOldBodies();
		
		if (Gdx.input.isKeyJustPressed(Keys.L)){
			System.out.println("System Report");
			System.out.println(floors.values().size()+" floors");
			System.out.println(walls.values().size()+" walls");
			System.out.println(drawables.size()+" drawables");
			System.out.println(updatables.size()+" updatables");
		}
		
		updatables.addAll(pendingUpdatables);
		pendingUpdatables.clear();
		
		Set<Vector2> floorsPositionTrash = new HashSet<Vector2>();
		Set<Vector2> wallsPositionTrash = new HashSet<Vector2>();
		Set<Drawable> drawablesTrash = new HashSet<Drawable>();
		Set<Updatable> updatablesTrash = new HashSet<Updatable>();
		
		for (Vector2 floorPosition: floors.keySet()){
			if (player.getY() - floorPosition.y > Values.OBJECT_Y_DIFF_MAX){
				floorsPositionTrash.add(floorPosition);
			}
		}
		
		for (Vector2 wallPosition: walls.keySet()){
			if (player.getY() - wallPosition.y > Values.OBJECT_Y_DIFF_MAX){
				wallsPositionTrash.add(wallPosition);
			}
		}
		
		for (Drawable drawable: drawables){
			if (drawable instanceof Killable){
				if (((Killable)drawable).isDeathComplete()){
					drawablesTrash.add(drawable);
					continue;
				}
			}
			if (drawable instanceof Disposable && ((Disposable) drawable).isDisposed()){
				drawablesTrash.add(drawable);
				continue;
			}
			if (player.getY() - drawable.getY() > Values.OBJECT_Y_DIFF_MAX){
				drawablesTrash.add(drawable);
			}
		}
		
		for (Vector2 floorPosition: floorsPositionTrash){
			floors.remove(floorPosition);
		}
		for (Vector2 wallPosition: wallsPositionTrash){
			walls.get(wallPosition).disposeLight();
			updatables.remove(walls.get(wallPosition));
			walls.remove(wallPosition);
		}
		drawables.removeAll(drawablesTrash);
		
		for (Updatable updatable: updatables){
			if (updatable instanceof Killable){
				if (((Killable)updatable).isDeathComplete()){
					updatablesTrash.add(updatable);
					continue;
				}
			}
			if (updatable instanceof Disposable && ((Disposable) updatable).isDisposed()){
				((Disposable) updatable).disposeCallback();
				updatablesTrash.add(updatable);
			}
		}
		updatables.removeAll(updatablesTrash);
		
		for (Updatable updatable: updatables){
			if (updatable instanceof Killable) ((Killable) updatable).checkFall();
			updatable.update();
		}
		
		player.update();
		player.checkFall();
		
		LD32Main.screenController.moveCameraTo(player.getBody().getPosition());
		LD32Main.screenController.getCamera().update();
	}

	@Override
	public void draw() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Set<Drawable> drawableDrawSet = new TreeSet<Drawable>();
		Set<Floor> floorDrawSet = new TreeSet<Floor>();
		
		drawableDrawSet.addAll(walls.values());
		drawableDrawSet.addAll(drawables);
		floorDrawSet.addAll(floors.values());
		
		spriteBatch.begin();
		spriteBatch.setProjectionMatrix(LD32Main.screenController.getCamera().combined);
		for (Floor drawable: floorDrawSet){
			drawable.drawLedge(spriteBatch);
		}
		for (Floor drawable: floorDrawSet){
			drawable.draw(spriteBatch);
		}
		for (Drawable drawable: drawableDrawSet){
			drawable.draw(spriteBatch);
		}
		
		spriteBatch.end();
		rayHandler.setCombinedMatrix(LD32Main.screenController.getCamera().combined);
		rayHandler.updateAndRender();
		
		//TODO: update renderer
		//debugRenderer.render(world, LD32Main.screenController.getCamera().combined);
		
		spriteBatch.begin();
		spriteBatch.setProjectionMatrix(LD32Main.screenController.getUICamera().combined);
		
		scoreFontBG.draw(spriteBatch, String.valueOf(score), 50, 650);
		scoreFont.draw(spriteBatch, String.valueOf(score), 50, 650);

		for (int i = 0; i < player.charges; i++){
			chargeOrb.setPosition(650, 650 - i * 60);
			chargeOrb.setScale(1);
			chargeOrb.draw(spriteBatch);
		}
		
		if (player.charges < Values.PLAYER_BULLET_CHARGES){
			chargeOrb.setPosition(650, 650 - player.charges * 60);
			chargeOrb.setOriginCenter();
			chargeOrb.setScale((player.cooldownTimer/Values.PLAYER_BULLET_COOLDOWN) * 0.8f);
			chargeOrb.draw(spriteBatch);
		}
		
		spriteBatch.end();
		
	}
	
	public void updateRows(){
		int newMaxY = (int)(player.getY() + Values.OBJECT_Y_DIFF_MIN);
		
		if (Math.round(newMaxY / 5) - 6 > score){
			score = Math.round(newMaxY / 5) - 6;
		}
		
		if (newMaxY > currentMaxY){
			for (int i = currentMaxY + 1; i <= newMaxY; i++){
				createRow(i);
			}
			currentMaxY = newMaxY;
		}
	}
	
	public void createRow(int y){
		createFloors(y + 20);
		createWalls(y);
		createLights(y);
		updateWallSprites(y - 1);
		spawnEnemies(y);
	}
	
	public void createFloors(int y){
		//first lets draw the floors, we can come back to the walls
		Random rng = new Random();
		
		//choose the center x, we are starting by drawing what is basically a hallway
		currentCenterX += rng.nextInt(3) - 1;
		if (rng.nextInt(5) >= 4){
			currentWidth = rng.nextInt(2) + 1;
		} else if (rng.nextInt(7) >= 6){
			currentWidth = 2;
		}
		for (int i = -currentWidth; i <= currentWidth; i++){
			addFloor(currentCenterX - i, y);
		}
		
		//now we will see if we should add a random square
		if (rng.nextInt(10) > 7){
			int squareXPosition = currentCenterX + rng.nextInt(20) - 10;
			int squareSize = rng.nextInt(6) + 1;
			int squareWidth = squareSize + rng.nextInt(2);
			int squareHeight = squareSize + rng.nextInt(2);
			addSquare(squareXPosition, y, squareWidth, squareHeight, true);
		}
		
		//now we will see if we should remove a random square
		if (rng.nextInt(6) >= 5){
			int squareXPosition = currentCenterX + rng.nextInt(10) - 5;
			int squareSize = rng.nextInt(2) + 1;
			int squareWidth = squareSize + rng.nextInt(2);
			int squareHeight = squareSize + rng.nextInt(2);
			removeSquare(squareXPosition, y, squareWidth, squareHeight);
		}
	}
	
	public void createWalls(int y){
		Random rng = new Random();
		Vector2 cursor = new Vector2();
		
		//iterate first for adding our floors and walls
		for (int i = currentCenterX - 20; i <= currentCenterX + 20; i++){
			cursor.x = i;
			cursor.y = y;
			//if we have a floor at current cursor position
			if (y != 9 && floors.containsKey(cursor)){
				boolean topEdge = false;
				boolean bottomEdge = false;
				boolean leftEdge = false;
				boolean rightEdge = false;
				
				boolean topEdgeTrue = false;
				boolean bottomEdgeTrue = false;
				boolean leftEdgeTrue = false;
				boolean rightEdgeTrue = false;
				
				
				
				cursor.x -= 2;
				if (!floors.containsKey(cursor)) leftEdge = true;
				cursor.x += 1;
				if (!floors.containsKey(cursor)){
					leftEdge = true;
					leftEdgeTrue = true;
				}
				
				cursor.x += 3;
				if (!floors.containsKey(cursor)) rightEdge = true;
				cursor.x -= 1;
				if (!floors.containsKey(cursor)){
					rightEdge = true;
					rightEdgeTrue = true;
				}
				cursor.x -= 1;
				
				
				cursor.y -= 2;
				if (!floors.containsKey(cursor)) bottomEdge = true;
				cursor.y += 1;
				if (!floors.containsKey(cursor)){
					bottomEdge = true;
					bottomEdgeTrue = true;
				}
				
				cursor.y += 3;
				if (!floors.containsKey(cursor)) topEdge = true;
				cursor.y -= 1;
				if (!floors.containsKey(cursor)){
					topEdge = true;
					topEdgeTrue = true;
				}
				cursor.y -= 1;
				
				int numEdges = 0;
				
				if (leftEdgeTrue) numEdges++;
				if (rightEdgeTrue) numEdges++;
				if (topEdgeTrue) numEdges++;
				if (bottomEdgeTrue) numEdges++;
				
				
				boolean putWall = false;
				if (numEdges > 0){
					if (numEdges == 1){
						putWall = true;
					}
					
					putWall = true;
					if (topEdge && bottomEdge){
						putWall = false;
					}
					if (leftEdge && rightEdge){
						putWall = false;
					}
				}
				if (putWall){
					addWall(i, y);
				}
			}
		}
		
		//now we will see if we should remove a random square of walls
		if (rng.nextInt(6) >= 4){
			int squareXPosition = currentCenterX + rng.nextInt(20) - 10;
			int squareSize = rng.nextInt(3) + 2;
			int squareWidth = squareSize + rng.nextInt(2);
			int squareHeight = squareSize + rng.nextInt(2);
			removeSquareWalls(squareXPosition, y, squareWidth, squareHeight);
		}
	}
	
	public void createLights(int y){
		Random rng = new Random();
		Vector2 cursor = new Vector2();
		//place some lights
		for (int i = currentCenterX - 20; i <= currentCenterX + 20; i++){
			cursor.x = i;
			cursor.y = y;
			if (y != 9 && walls.containsKey(cursor)){
				//we're putting a light on this guy
				if (rng.nextInt(10) > 6){
					int side = 0;
					List<Integer> validSides = new ArrayList<Integer>();
					cursor.x += 1;
					if (!walls.containsKey(cursor) && floors.containsKey(cursor)) validSides.add(1);
					cursor.x -= 2;
					if (!walls.containsKey(cursor) && floors.containsKey(cursor)) validSides.add(3);
					cursor.x += 1;
					cursor.y += 1;
					if (!walls.containsKey(cursor) && floors.containsKey(cursor)) validSides.add(2);
					cursor.y -= 2;
					if (!walls.containsKey(cursor) && floors.containsKey(cursor)) validSides.add(4);
					cursor.y += 1;
					
					if (!validSides.isEmpty()){
						side = validSides.get(rng.nextInt(validSides.size()));
						walls.get(cursor).putLight(side);
					}
				}
			}
		}
	}
	
	public void updateWallSprites(int y){
		Vector2 cursor = new Vector2();
		Random rng = new Random();
		for (int i = currentCenterX - 30; i <= currentCenterX + 30; i++){
			cursor.x = i;
			cursor.y = y;
			if (walls.containsKey(cursor)){
				boolean leftEdge = false;
				boolean topEdge = false;
				boolean rightEdge = false;
				boolean bottomEdge = false;
				
				cursor.x += 1;
				if (!walls.containsKey(cursor)) rightEdge = true;
				cursor.x -= 2;
				if (!walls.containsKey(cursor)) leftEdge = true;
				cursor.x += 1;
				cursor.y += 1;
				if (!walls.containsKey(cursor)) topEdge = true;
				cursor.y -= 2;
				if (!walls.containsKey(cursor)) bottomEdge = true;
				cursor.y += 1;
				
				
				
				if (topEdge && bottomEdge && !leftEdge && !rightEdge){
					walls.get(cursor).setType(1);
				} else if (!topEdge && !bottomEdge && leftEdge && rightEdge){
					walls.get(cursor).setType(2);
				} else {
					if (rng.nextInt(3) < 1){
						if (topEdge && bottomEdge && leftEdge && !rightEdge){
							walls.get(cursor).setType(1);
						} else if (topEdge && bottomEdge && !leftEdge && rightEdge){
							walls.get(cursor).setType(1);
						} else if (!topEdge && bottomEdge && leftEdge && rightEdge){
							walls.get(cursor).setType(2);
						} else if (topEdge && !bottomEdge && leftEdge && rightEdge){
							walls.get(cursor).setType(2);
						}
					}
				}
			}
		}
	}
	
	public void spawnEnemies(int y){
		Vector2 cursor = new Vector2();
		Random rng = new Random();
		for (int i = currentCenterX - 30; i <= currentCenterX + 30; i++){
			cursor.x = i;
			cursor.y = y;
			if (floors.containsKey(cursor) && !walls.containsKey(cursor) && y > 10){
				if (rng.nextFloat() < ((float) y) / Values.DIFFICULTY_RAMP){
					float type = rng.nextFloat();
					if (type > 0.8f){
						addArcher(i, y);
					} else {
						addZombie(i, y);
					}
				}
			}
		}
	}
	
	public void addSquare(int x, int y, int width, int height, boolean hasSecondary){
		Random rng = new Random();
		for (int i = x - width; i <= x + width; i++){
			for (int j = y - height; j <= y + height; j++){
				addFloor(i, j);
				
				if (hasSecondary && rng.nextInt(10) >= 9){
					int squareXPosition = x + rng.nextInt(3) - 1;
					int squareWidth = rng.nextInt(2) + 1;
					int squareHeight = rng.nextInt(2) + 1;
					addSquare(squareXPosition, y, squareWidth, squareHeight, false);
				}
				
			}
		}
	}
	
	public void removeSquare(int x, int y, int width, int height){
		Vector2 cursor = new Vector2();
		for (int i = x - width; i <= x + width; i++){
			for (int j = y - height; j <= y + height; j++){
				cursor.x = i;
				cursor.y = j;
				if (floors.containsKey(cursor)){
					floors.remove(cursor);
				}
			}
		}
	}
	
	public void removeSquareWalls(int x, int y, int width, int height){
		Vector2 cursor = new Vector2();
		for (int i = x - width; i <= x + width; i++){
			for (int j = y - height; j <= y + height; j++){
				cursor.x = i;
				cursor.y = j;
				if (j != 9 && walls.containsKey(cursor)){
					walls.get(cursor).disposeLight();
					if (!world.isLocked() && walls.get(cursor).getBody() != null) world.destroyBody(walls.get(cursor).getBody());
					updatables.remove(walls.get(cursor));
					walls.remove(cursor);
				}
			}
		}
	}
	
	public void addWall(int x, int y){
		Vector2 cursor = new Vector2(x, y);
		if (!walls.containsKey(cursor)){
			Wall wall = new Wall(this, x, y);
			walls.put(cursor, wall);
			updatables.add(wall);
		}
	}
	
	public void addZombie(int x, int y){
		Zombie zombie = new Zombie(this, x, y);
		drawables.add(zombie);
		updatables.add(zombie);
	}
	
	public void addArcher(int x, int y){
		Archer archer = new Archer(this, x, y);
		drawables.add(archer);
		updatables.add(archer);
	}
	
	public void addArrow(Arrow arrow){
		pendingUpdatables.add(arrow);
		drawables.add(arrow);
	}
	
	public void addFloor(int x, int y){
		floors.put(new Vector2(x, y), new Floor(this, x, y));
	}
	
	private void createCollisionListener() {
        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                
                String typeA = (String) ((Map<String, Object>)fixtureA.getUserData()).get("type");
                String typeB = (String) ((Map<String, Object>)fixtureB.getUserData()).get("type");
                
                Object parentA = ((Map<String, Object>)fixtureA.getUserData()).get("parent");
                Object parentB = ((Map<String, Object>)fixtureB.getUserData()).get("parent");
                
                //Check collisions of our teleporter bullets
                if (typeA.equals("TeleporterBullet")){
                	if (parentB instanceof Swappable){
                		TeleporterBullet teleporterBullet = (TeleporterBullet)parentA;
                		teleporterBullet.dispose();
	                	
                		Swappable swappable = (Swappable)parentB;
                		Vector2 playerPosition = player.getBody().getPosition().cpy();
                		player.swapTo(swappable.getPosition().cpy());
                		swappable.swapTo(playerPosition);
                	}
                } else if (typeB.equals("TeleporterBullet")){
                	if (parentA instanceof Swappable){
                		TeleporterBullet teleporterBullet = (TeleporterBullet)parentB;
                		teleporterBullet.dispose();
                		
                		Swappable swappable = (Swappable)parentA;
                		Vector2 playerPosition = player.getBody().getPosition().cpy();
                		player.swapTo(swappable.getPosition().cpy());
                		swappable.swapTo(playerPosition);
                	}
                }
                
                //Check if player is colliding with a zombie, kill player
                if (typeA.equals("Zombie") && typeB.equals("Player")){
                	player.killBleed();
                } else if (typeA.equals("Player") && typeB.equals("Zombie")){
                	player.killBleed();
                }
                
                //if player colliding with arrow
                if (typeA.equals("Arrow") && parentB instanceof Killable){
                	((Arrow)parentA).dispose();
                	((Killable)parentB).killBleed();
                } else if (parentA instanceof Killable && typeB.equals("Arrow")){
                	((Arrow)parentB).dispose();
                	((Killable)parentA).killBleed();
                }
                
            }

            @Override
            public void endContact(Contact contact) {
            }

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}
        });
    }
	
	public void purgeOldBodies(){
		if (!world.isLocked()){
			int maxY = (int) (player.getY() - Values.PHYSICS_OBJECT_Y_DIFF_MAX);
			Array<Body> bodies = new Array<Body>();
			world.getBodies(bodies);
			
			for (Body body: bodies){
				if (body.getPosition().y < maxY){
					world.destroyBody(body);
				}
			}
		}
	}
	
	public void endGame(){
		LD32Main.screenController.setCurrentScreen(new GameScreen());
	}
	
	public void darken(){
		ambientLight.lerp(0,0,0, 0, 0.01f);
		rayHandler.setAmbientLight(ambientLight);
		for (Wall wall: walls.values()){
			wall.darken();
		}
	}
	
	public void loadFonts(){
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/scoreFont.ttf"));
    	generator.scaleForPixelHeight(16);
    	
    	scoreFont = generator.generateFont(16); // font size 12 pixels
    	scoreFont.setColor(0.0f, 0.0f, 0.0f, 1.0f);
    	scoreFont.setScale(5);
    	
    	generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/scoreFontBG.ttf"));
    	generator.scaleForPixelHeight(16);
    	scoreFontBG = generator.generateFont(16); // font size 12 pixels
    	scoreFontBG.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    	scoreFontBG.setScale(5);
    	
    	generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/minimeek.ttf"));
    	generator.scaleForPixelHeight(16);
    	standardFont = generator.generateFont(16); // font size 12 pixels
    	standardFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    	standardFont.setScale(1);
	}
}
