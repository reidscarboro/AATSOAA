package com.sg.ld32.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.sg.ld32.screens.GenericScreen;
import com.sg.ld32.utilities.Values;

public class ScreenController {
	
	private OrthographicCamera camera;
	private OrthographicCamera uiCamera;
	
	private float width;
	private float height;
	
	public float finalWidth;
	public float finalHeight;
	
	private GenericScreen currentScreen;
	
	public ScreenController(){
		setUpCamera();
	}
	
	public void setUpCamera(){
		width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        
        finalWidth = Values.SCREEN_WIDTH;
        finalHeight = height/width * Values.SCREEN_WIDTH;
        
        camera = new OrthographicCamera(finalWidth, finalHeight);
        
        camera.zoom = Values.CAMERA_ZOOM;
        camera.position.x = 0;
        camera.position.y = 0;
        
        camera.update();
        
        uiCamera = new OrthographicCamera(width, height);
        camera.zoom = 1;
        uiCamera.position.x = width / 2;
        uiCamera.position.y = height / 2;
        
        uiCamera.update();
	}
	
	public void moveCameraTo(Vector2 position){
		float lerp = 0.05f;
		camera.position.x += (position.x - camera.position.x) * lerp;
		camera.position.y += (position.y - camera.position.y) * lerp;
	}
	
	public void zoomCameraTo(float zoom){
		if (zoom < 0.01f) zoom = 0.1f;
		
		float lerp = 0.05f;
		camera.zoom += (zoom - camera.zoom) * lerp;
	}
	
	public OrthographicCamera getUICamera(){
		return uiCamera;
	}
	
	public OrthographicCamera getCamera(){
		return camera;
	}
	
	public GenericScreen getCurrentScreen(){
		return currentScreen;
	}
	
	public void setCurrentScreen(GenericScreen currentScreen){
		this.currentScreen = currentScreen;
	}
}
