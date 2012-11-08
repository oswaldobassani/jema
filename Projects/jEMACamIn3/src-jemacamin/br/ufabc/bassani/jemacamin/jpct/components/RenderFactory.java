package br.ufabc.bassani.jemacamin.jpct.components;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import raft.jpct.bones.Animated3D;
import raft.jpct.bones.AnimatedGroup;
import raft.jpct.bones.BonesImporter;
import raft.jpct.bones.Quaternion;

import com.jmex.model.ModelFormatException;
import com.jmex.model.ogrexml.OgreEntityNode;
import com.jmex.model.ogrexml.OgreLoader;
import com.threed.jpct.Camera;
import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.IRenderer;
import com.threed.jpct.Lights;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.skeleton.loader.OgreXMLLoader;

public class RenderFactory
{

	protected World world;
	protected Camera camera;
	protected FrameBuffer frameBuffer;
	protected Dimension size;
	
	public RenderFactory(int w, int h)
    {
		Config.saveMemory = true;
		
		size = new Dimension(w, h);
		
    	//Set up the world for rendering onto:).
        world = new World();
         
        world.getLights().setOverbrightLighting(Lights.OVERBRIGHT_LIGHTING_DISABLED);
        world.getLights().setRGBScale(Lights.RGB_SCALE_2X);
        int colorAmbient = 255;//80
        world.setAmbientLight(colorAmbient, colorAmbient, colorAmbient);
        
        int colorSpot = 255;//40
        world.addLight(new SimpleVector(-30,-30,-30), new Color(colorSpot,colorSpot,colorSpot));
        world.addLight(new SimpleVector(-30,-30,30), new Color(colorSpot,colorSpot,colorSpot));
        //Get some kind of angle to start with:)
        camera = world.getCamera();
        camera.setPosition(new SimpleVector(0,0,0));
	    camera.moveCamera(new SimpleVector(0,0,-40),1f);
	    camera.lookAt(new SimpleVector(0,0,0));
	    
	    frameBuffer = new FrameBuffer(
				size.width,size.height,
				FrameBuffer.SAMPLINGMODE_NORMAL);

	    frameBuffer.enableRenderer(
				IRenderer.RENDERER_SOFTWARE,
				IRenderer.MODE_OPENGL);
    }

	/**
	 * Returns world instance, shared between all RenderPanes.
	 * 
	 * @return
	 */
	public World getWorld()
	{
		return world;
	}

	/**
	 * Loads a single 3d model.
	 * 
	 * @param filename
	 * @return
	 */
	public Object3D loadModel(String filename, InputStream inputStream, InputStream skeletonIn, float scale, boolean originalJPCTloader)
	{
		Object3D obj = null;
		try
		{
			obj = OgreXMLLoader.loadOgreXML(inputStream,skeletonIn, scale, originalJPCTloader)[0];
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		obj.build();
	    world.addObject(obj);
	    
	    System.out.println("Model Loaded..");
	    
	    return obj;
	}
	
	/**
	 * Loads a single 3d model.
	 * 
	 * @param filename
	 * @return
	 */
	public Object3D[] loadModel_Full(String filename, InputStream inputStream, InputStream skeletonIn, float scale, boolean originalJPCTloader)
	{
		Object3D[] objs = null;
		try
		{
			objs = OgreXMLLoader.loadOgreXML(inputStream,skeletonIn, scale, originalJPCTloader);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		for(Object3D obj : objs){
			obj.build();
			world.addObject(obj);
		}
	    
	    System.out.println("Model Loaded..");
	    
	    return objs;
	}
	
	/**
	 * Loads a single 3d model.
	 * 
	 * @param filename
	 * @return
	 */
	public Object3D[] loadModel_BonesAPI_Ogre(String directory, String fileName, String textureName, String textureFileName)
	{
		Object3D[] objs = null;

		try {
			// we only specify the mesh file, skeleton file automatically loaded, and should be in same directory.  
			URL ninjaUrl = new File(directory + "/" + fileName).toURI().toURL();
			
			OgreLoader loader = new OgreLoader();
			OgreEntityNode node = loader.loadModel(ninjaUrl);

			// data in ogre file is upside down, so rotate around x axis
			AnimatedGroup animatedGroup = BonesImporter.importOgre(node, .1f, new Quaternion().rotateX((float)Math.PI));

			Texture texture = new Texture(directory + "/" + textureFileName);
			TextureManager.getInstance().addTexture(textureName, texture);
			
			objs = new Object3D[animatedGroup.getSize()];
			int count = 0;
			for (Animated3D o : animatedGroup) {
				o.setTexture("ninja");
				o.build();
				
				world.addObject(o);
				
				o.discardMeshData();
				objs[count] = o;
				count++;
			}
			
			System.out.println("Model Loaded..");
		} catch (MalformedURLException e) {
			System.out.println("Model NOT loaded ... Exception: "+e);
		} catch (IOException e) {
			System.out.println("Model NOT loaded ... Exception: "+e);
		} catch (ModelFormatException e) {
			System.out.println("Model NOT loaded ... Exception: "+e);
		}
	    
	    return objs;
	}

}
