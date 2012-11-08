package com.threed.jpct.skeleton.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.threed.jpct.Logger;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.skeleton.Quaternion;
import com.threed.jpct.skeleton.RotationKeyframe;
import com.threed.jpct.skeleton.SimpleBone;
import com.threed.jpct.skeleton.SimpleSkeleton;
import com.threed.jpct.skeleton.SkeletalAnimation;
import com.threed.jpct.skeleton.SkeletalObject3D;
import com.threed.jpct.skeleton.TranslationKeyframe;
import com.threed.jpct.skeleton.VertexAssignment;
import com.threed.jpct.skeleton.util.XMLElement;
import com.threed.jpct.skeleton.util.XMLParserFactory;

/**
 * Classe do jPCT com modificações para melhor carregamento dos modelos.
 * @author jPCT Authors
 * @author Bassani
 */
public class OgreXMLLoader
{

	/**
	 * Loads a model, and it's skeleton from InputStreams.
	 * 
	 * Uses SkeletalObject3D, and its API to permit skeletal
	 * animations, and other effects.
	 * 
	 * @param in			InputStream of model file.
	 * @param skeletonIn	InputStream of skeleton file.
	 * @param newScale		Model scale factor.
	 * @param originalJPCTloader Use default or not default loader system.
	 * @return				array of SkeletalObject3Ds.
	 */
	public static SkeletalObject3D[] loadOgreXML(InputStream in, InputStream skeletonIn, float newScale, boolean originalJPCTloader){
		if(originalJPCTloader){
			return loadOgreXML(in, skeletonIn, newScale);
		}else{
			System.out.println("NOISSSS MANOOOOOO!");
			System.out.println("NOISSSS MANOOOOOO!");
			System.out.println("NOISSSS MANOOOOOO!");
			System.out.println("NOISSSS MANOOOOOO!");
			return loadOgreXMLmod(in, skeletonIn, newScale);
		}
	}
	
	/**
	 * Loads a model, and it's skeleton from InputStreams.
	 * 
	 * Uses SkeletalObject3D, and its API to permit skeletal
	 * animations, and other effects.
	 * 
	 * @param in			InputStream of model file.
	 * @param skeletonIn	InputStream of skeleton file.
	 * @param newScale		Model scale factor.
	 * @return				array of SkeletalObject3Ds.
	 */
	protected static SkeletalObject3D[] loadOgreXML(InputStream in, InputStream skeletonIn, float newScale)
	{
		Logger.log("Loading model(s) from OgreXML format...",Logger.MESSAGE);
		String input = getStringFromStream(in);
		
		XMLElement root   = XMLParserFactory.getInstance().parseXML(input);
    	
    	//-----------------------------------------------------------
    	// Load the skeleton first!
    	//-----------------------------------------------------------
    	SimpleSkeleton skeleton = loadSkeleton(skeletonIn);
    	//----------------------------------------------------------------------
    	// Load the submeshes, creating new SkeletalObject3Ds out of each one.
    	// 
    	// Each SkeletalObject3D will be linked to the same skeleton.
    	//----------------------------------------------------------------------
    	List<?> submeshes   = root.getChild("submeshes").getChildren("submesh");
    	
    	boolean addTexture = false;
    	String textureName = "";
    	
    	SkeletalObject3D[] meshes = new SkeletalObject3D[submeshes.size()];
    	int meshCtr = 0;
    	for(Object o : submeshes)
    	{
    		Logger.log("Found new submesh!",Logger.MESSAGE);
    		
    		XMLElement submesh = (XMLElement)o;
    		
    		//--------------------------------------------------------------
    		// Get some attributes.
    		//--------------------------------------------------------------
    		String  material          = submesh.getAttributeValue("material");
    		addTexture = false;
    		/*
    		 * Material - implementação de teste
    		 */
    		System.out.println("Material ->> "+material);
    		if(material!=null){
    			if(material.equals("Chocolate")){
    				
    				Texture tex=new Texture("textures"+File.separatorChar+"Chocolate.jpg");
    			    TextureManager.getInstance().addTexture("Chocolate", tex);
    				
    			    addTexture = true;
    			    textureName = "Chocolate";
    			}else if(material.equals("DadoTextura")){
    				
    				Texture tex=new Texture("textures"+File.separatorChar+"dado-numerado.png");
    			    TextureManager.getInstance().addTexture("DadoTextura", tex);
    				
    			    addTexture = true;
    			    textureName = "DadoTextura";
    			}
    		}
    		
    		boolean useSharedVertices = Boolean.parseBoolean(submesh.getAttributeValue("usesharedvertices"));
    		//--------------------------------------------------------------
    		// Load "per vertexbuffer" things.
    		//
    		// NOTE: loadFaces not required, because jPCT does this for us.
    		//--------------------------------------------------------------
    		//float[][]        faces        = loadFaces(submesh);
    		OGREVertexBuffer vertexBuffer = loadGeometry(submesh);
    		//--------------------------------------------------------------
    		// Create a new skeletal object to put vertex data into.
    		//--------------------------------------------------------------
    		SkeletalObject3D tmpObj   = new SkeletalObject3D(vertexBuffer.vertices.length);
    		OGREVertex[]     vertexes = vertexBuffer.vertices;
    		//--------------------------------------------------------------
    		// Fill temp vector array.
    		//
    		// Used to create triangles for the SkeletalObject3D.
    		//--------------------------------------------------------------
    		SimpleVector[] vs = new SimpleVector[3];
    	    for (int i = 0; i < 3; i++)
    	    	vs[i] = new SimpleVector();
    	    //--------------------------------------------------------------
    	    // If format doesn't use shared vertices, we don't either;)
    	    //--------------------------------------------------------------
    	    if(!useSharedVertices)
    	    {
    	    	Logger.log("Disabling vertex sharing.",Logger.MESSAGE);
        		tmpObj.disableVertexSharing();
    	    }
    	    //--------------------------------------------------------------
    	    // Loop through the vertices, making the triangles, and adding 
    	    // to skeletal object.
    	    //--------------------------------------------------------------
    	    Logger.log("Creating triangle data.",Logger.MESSAGE);
    		for(int i = 0; i < vertexes.length-2; i += 3)
    		{
    			for (int h = 0; h < 3; h++)
    			{
    				vs[h].x = vertexes[i + h].positionX;
    	            vs[h].y = vertexes[i + h].positionY;
    	            vs[h].z = vertexes[i + h].positionZ;
    	            vs[h].scalarMul(newScale);
    	        }

    			float u1 = vertexes[i].textureU;
    			float v1 = vertexes[i].textureV;
    			float u2 = vertexes[i + 1].textureU;
    			float v2 = vertexes[i + 1].textureV;
    			float u3 = vertexes[i + 2].textureU;
    			float v3 = vertexes[i + 2].textureV;

    	        tmpObj.addTriangle(vs[0], u1, v1, vs[1], u2, v2, vs[2], u3, v3);
    		}
    		tmpObj.build();
    		tmpObj.calcBoundingBox();
            tmpObj.calcNormals();
            
            //-------------------------------------------------------------
            // Rotate model to compensate for axis differences in format.
            //-------------------------------------------------------------
            //Rotate so its using Y down.
    		float radiansX = -(float)Math.toRadians(180f);
    		tmpObj.rotateX(radiansX);
    		//float radiansZ = (float)Math.toRadians(90f);
    		//tmpObj.rotateZ(radiansZ);
    		// - tmpObj.rotateMesh(); //Ensure the rotations are counted as default.
    	    //Resets rotation matrix, to avoid bugs;)
    		// - tmpObj.setRotationMatrix(new Matrix()); 
            //-------------------------------------------------------------
            // Assign skeleton to object, and load the vertex assignments.
            //-------------------------------------------------------------
            tmpObj.setSkeleton(skeleton);
            //tmpObj.setCulling(false);
            loadBoneVertexAssignments(vertexes.length,skeleton,tmpObj,submesh);
            tmpObj.applyBoneInverseMatrices();
            //-------------------------------------------------------------
            if(addTexture){
            	tmpObj.setTexture(textureName);
            }
    		meshes[meshCtr] = tmpObj;
    		meshCtr++;
    	}
    	Logger.log("OgreXML model(s) loading completed.",Logger.MESSAGE);
		return meshes;
	}
	
	/**
	 * Loads a model, and it's skeleton from InputStreams.
	 * 
	 * Uses SkeletalObject3D, and its API to permit skeletal
	 * animations, and other effects.
	 * 
	 * Modificação para realizar o carregamento utilizando os dados de faces.
	 * 
	 * @param in			InputStream of model file.
	 * @param skeletonIn	InputStream of skeleton file.
	 * @param newScale		Model scale factor.
	 * @return				array of SkeletalObject3Ds.
	 * @author Bassani
	 */
	protected static SkeletalObject3D[] loadOgreXMLmod(InputStream in, InputStream skeletonIn, float newScale)
	{
		Logger.log("Loading model(s) from OgreXML format...",Logger.MESSAGE);
		String input = getStringFromStream(in);
		
		XMLElement root   = XMLParserFactory.getInstance().parseXML(input);
    	
    	//-----------------------------------------------------------
    	// Load the skeleton first!
    	//-----------------------------------------------------------
    	SimpleSkeleton skeleton = loadSkeleton(skeletonIn);
    	//----------------------------------------------------------------------
    	// Load the submeshes, creating new SkeletalObject3Ds out of each one.
    	// 
    	// Each SkeletalObject3D will be linked to the same skeleton.
    	//----------------------------------------------------------------------
    	List<?> submeshes   = root.getChild("submeshes").getChildren("submesh");
    	
    	boolean addTexture = false;
    	String textureName = "";
    	
    	SkeletalObject3D[] meshes = new SkeletalObject3D[submeshes.size()];
    	int meshCtr = 0;
    	for(Object o : submeshes)
    	{
    		Logger.log("Found new submesh!",Logger.MESSAGE);
    		
    		XMLElement submesh = (XMLElement)o;
    		
    		//--------------------------------------------------------------
    		// Get some attributes.
    		//--------------------------------------------------------------
    		String  material          = submesh.getAttributeValue("material");
    		addTexture = false;
    		/*
    		 * Material - implementação de teste
    		 */
    		System.out.println("Material ->> "+material);
    		if(material!=null){
    			if(material.equals("Chocolate")){
    				
    				Texture tex=new Texture("textures"+File.separatorChar+"Chocolate.jpg");
    			    TextureManager.getInstance().addTexture("Chocolate", tex);
    				
    			    addTexture = true;
    			    textureName = "Chocolate";
    			}else if(material.equals("DadoTextura")){
    				
    				Texture tex=new Texture("textures"+File.separatorChar+"dado-numerado.png");
    			    TextureManager.getInstance().addTexture("DadoTextura", tex);
    				
    			    addTexture = true;
    			    textureName = "DadoTextura";
    			}
    		}
    		
    		boolean useSharedVertices = Boolean.parseBoolean(submesh.getAttributeValue("usesharedvertices"));
    		//--------------------------------------------------------------
    		// Load "per vertexbuffer" things.
    		//
    		// NOTE: loadFaces not required, because jPCT does this for us.
    		// Bassani: mas é melhor em caso de modelos 3d exportados não idealmente.
    		//--------------------------------------------------------------
    		int[][]        faces          = loadFaces(submesh);
    		OGREVertexBuffer vertexBuffer = loadGeometry(submesh);
    		//--------------------------------------------------------------
    		// Create a new skeletal object to put vertex data into.
    		//--------------------------------------------------------------
    		SkeletalObject3D tmpObj   = new SkeletalObject3D(faces.length*3);
    		OGREVertex[]     vertexes = vertexBuffer.vertices;
    		//--------------------------------------------------------------
    		// Fill temp vector array.
    		//
    		// Used to create triangles for the SkeletalObject3D.
    		//--------------------------------------------------------------
    		SimpleVector[] vs = new SimpleVector[3];
    	    for (int i = 0; i < 3; i++)
    	    	vs[i] = new SimpleVector();
    	    //--------------------------------------------------------------
    	    // If format doesn't use shared vertices, we don't either;)
    	    //--------------------------------------------------------------
    	    if(!useSharedVertices)
    	    {
    	    	Logger.log("Disabling vertex sharing.",Logger.MESSAGE);
        		tmpObj.disableVertexSharing();
    	    }
    	    //--------------------------------------------------------------
    	    // Loop through the vertices, making the triangles, and adding 
    	    // to skeletal object.
    	    //--------------------------------------------------------------
    	    Logger.log("Creating triangle data.",Logger.MESSAGE);
    	    // Bassani - remake utilizando as faces
    	    for(int i = 0; i < faces.length; i++)
    		{
    	    	// - Logger.log("Creating triangle data ... "+i+" de "+faces.length, Logger.MESSAGE);
    	    	// - Logger.log(" face Vertex "+faces[i][0]+" , "+faces[i][1]+" , "+faces[i][2], Logger.MESSAGE);
    	    	
    			for (int h = 0; h < 3; h++)
    			{
    				vs[h].x = vertexes[faces[i][h]].positionX;
    	            vs[h].y = vertexes[faces[i][h]].positionY;
    	            vs[h].z = vertexes[faces[i][h]].positionZ;
    	            vs[h].scalarMul(newScale);
    	        }

    			float u1 = vertexes[faces[i][0]].textureU;
    			float v1 = vertexes[faces[i][0]].textureV;
    			float u2 = vertexes[faces[i][1]].textureU;
    			float v2 = vertexes[faces[i][1]].textureV;
    			float u3 = vertexes[faces[i][2]].textureU;
    			float v3 = vertexes[faces[i][2]].textureV;

    	        tmpObj.addTriangle(vs[0], u1, v1, vs[1], u2, v2, vs[2], u3, v3);
    		}
    		tmpObj.build();
    		tmpObj.calcBoundingBox();
            tmpObj.calcNormals();
            
            //-------------------------------------------------------------
            // Rotate model to compensate for axis differences in format.
            //-------------------------------------------------------------
            //Rotate so its using Y down.
    		float radiansX = -(float)Math.toRadians(180f);
    		tmpObj.rotateX(radiansX);
            //-------------------------------------------------------------
            // Assign skeleton to object, and load the vertex assignments.
            //-------------------------------------------------------------
            tmpObj.setSkeleton(skeleton);
            // Bassani - todas as listas de vertices tem tamanho de 3 * o numero de faces
            loadBoneVertexAssignmentsMod(faces, vertexes, faces.length*3, skeleton, tmpObj, submesh);
            tmpObj.applyBoneInverseMatrices();
            //-------------------------------------------------------------
            if(addTexture){
            	tmpObj.setTexture(textureName);
            }
    		meshes[meshCtr] = tmpObj;
    		meshCtr++;
    	}
    	Logger.log("OgreXML model(s) loading completed.", Logger.MESSAGE);
		return meshes;
	}
	
	/**
	 * Loads the geometry of the OgreXML file.
	 * 
	 * This involves loading the vertex data from one or more
	 * vertex buffers.
	 * 
	 * @param submesh	the xml node to search.
	 * @return			vertex buffer with vertexes inside.
	 */
	private static OGREVertexBuffer loadGeometry(XMLElement submesh)
	{
		XMLElement geometryElement = submesh.getChild("geometry");
		//-----------------------------------------------------------------
		// Get some variables from geometry.
		//
		// NOTE: We do not use vertexcount, because we get the value from
		// the vertex array later on.
		//-----------------------------------------------------------------
		//int count = Integer.parseInt(geometryElement.getAttributeValue("vertexcount"));
		//-----------------------------------------------------------------
		// Begin loading vertex buffers.
		//
		// TODO: Permit loading of multiple vertex buffers!
		//-----------------------------------------------------------------
		XMLElement vertexBufferElement = geometryElement.getChild("vertexbuffer");
		//-----------------------------------------------------------------
		// Get vertex buffers capabilities - what if can give us:)
		//-----------------------------------------------------------------
		//Positional data?
		String  strPositions = vertexBufferElement.getAttributeValue("positions");
		boolean positions    = (strPositions != null) ? Boolean.parseBoolean(strPositions) : false;
		//Normal data?
		String  strNormals   = vertexBufferElement.getAttributeValue("normals");
		boolean normals      = (strNormals != null) ? Boolean.parseBoolean(strNormals) : false;
		//Texture coords?
		String  strTextureCoords = vertexBufferElement.getAttributeValue("texture_coords");
		int     textureCoords    = (strTextureCoords != null) ? Integer.parseInt(strTextureCoords) : 0;
		//If so, how many dimensions? I don't even know what that means;)
		String  strTextureCoordDimensions = vertexBufferElement.getAttributeValue("texture_coord_dimensions_0");
		int     textureCoordDimensions    = (strTextureCoordDimensions != null) ? Integer.parseInt(strTextureCoordDimensions) : 0;
		//-----------------------------------------------------------------
		// Create a vertex buffer to store vertex data.
		//-----------------------------------------------------------------
		OGREVertexBuffer vertexBuffer = new OGREVertexBuffer();
		//-----------------------------------------------------------------
		// Save the above capabilities into the vertex buffer class.
		//-----------------------------------------------------------------
		vertexBuffer.positions              = positions;
		vertexBuffer.normals                = normals;
		vertexBuffer.textureCoords          = textureCoords;
		vertexBuffer.textureCoordDimensions = textureCoordDimensions;
		//-----------------------------------------------------------------
		// Loop through the vertices.
		//
		// We only look for the data the variables above tell us about;)
		//-----------------------------------------------------------------
		int i = 0;
		List<?> vertexes = vertexBufferElement.getChildren("vertex");
		
		OGREVertex[] vertexArray = new OGREVertex[vertexes.size()];
		vertexBuffer.vertices    = vertexArray;
		
		for(Object o : vertexes)
    	{
			XMLElement vertex = (XMLElement)o;
    		
			XMLElement position = vertex.getChild("position");
			XMLElement normal   = vertex.getChild("normal");
			XMLElement texcoord = vertex.getChild("texcoord");
    		
    		vertexArray[i] = new OGREVertex();
    		
    		if(vertexBuffer.positions)
    		{
    		    float posX  = Float.parseFloat(position.getAttributeValue("x"));
    			float posY  = Float.parseFloat(position.getAttributeValue("y"));
    			float posZ  = Float.parseFloat(position.getAttributeValue("z"));
    		
    			vertexArray[i].positionX = posX;
    			vertexArray[i].positionY = posY;
    			vertexArray[i].positionZ = posZ;
    		}
    		if(vertexBuffer.normals)
    		{
    			float normX = Float.parseFloat(normal.getAttributeValue("x"));
    			float normY = Float.parseFloat(normal.getAttributeValue("y"));
    			float normZ = Float.parseFloat(normal.getAttributeValue("z"));
    		
    			vertexArray[i].normalX   = normX;
    			vertexArray[i].normalY   = normY;
    			vertexArray[i].normalZ   = normZ;
    		}
    		if(vertexBuffer.textureCoords>0)
    		{
    			float texU  = Float.parseFloat(texcoord.getAttributeValue("u"));
    			float texV  = Float.parseFloat(texcoord.getAttributeValue("v"));
    		
    			vertexArray[i].textureU  = texU;
    			vertexArray[i].textureV  = texV;
    		}
    		i++;
    	}
		return vertexBuffer;
	}

	/**
	 * Implementação não necessária mas que permite maior compatibilidade com modelos exportados.
	 * Caso as faces reusem vertices.
	 * 
	 * @param submesh
	 * @return
	 * 
	 * @author Bassani
	 */
	private static int[][] loadFaces(XMLElement submesh)
	{
		XMLElement facesElement = submesh.getChild("faces");
		int count = Integer.parseInt(facesElement.getAttributeValue("count"));
		int[][] facesArray = new int[count][3];
		
		int i = 0;
		List<?> faces = facesElement.getChildren("face");
		for(Object o : faces)
    	{
			XMLElement face = (XMLElement)o;
    		
    		int v1 = Integer.parseInt(face.getAttributeValue("v1"));
    		int v2 = Integer.parseInt(face.getAttributeValue("v2"));
    		int v3 = Integer.parseInt(face.getAttributeValue("v3"));
    		
    		facesArray[i][0] = v1;
    		facesArray[i][1] = v2;
    		facesArray[i][2] = v3;
    		
    		i++;
    	}
		return facesArray;
	}
	
	/**
	 * Loads skeleton data from input stream.
	 * 
	 * Returns a skeleton, with keyframes, and bone hierarchies,
	 * as specified by the OgreXML file specification.
	 */
	private static SimpleSkeleton loadSkeleton(InputStream in)
	{
		Logger.log("Loading skeleton data...",Logger.MESSAGE);
		String input = getStringFromStream(in);
		
		XMLElement root   = XMLParserFactory.getInstance().parseXML(input);
    	
    	//-----------------------------------------------------------------
    	// Create a new skeleton class to add bones to.
    	//-----------------------------------------------------------------
    	SimpleSkeleton skeleton = new SimpleSkeleton();
    	//-----------------------------------------------------------------
    	// Loop through bones, grabbing their data.
    	//-----------------------------------------------------------------
    	List<?> bones = root.getChild("bones").getChildren("bone");
    	SimpleBone[] bonesArray = new SimpleBone[bones.size()];
    	int i = 0;
    	for(Object o : bones)
    	{
    		XMLElement boneElement = (XMLElement)o;
    		
    		//-------------------------------------------------------------
    		// Unique bone identifiers.
    		//-------------------------------------------------------------
    		int    id   = Integer.parseInt(boneElement.getAttributeValue("id"));
    		String name = boneElement.getAttributeValue("name");
    		//-------------------------------------------------------------
    		// Positional data(resting position).
    		//-------------------------------------------------------------
    		XMLElement positionElement = boneElement.getChild("position");
    		float positionX = Float.parseFloat(positionElement.getAttributeValue("x"));
    		float positionY = Float.parseFloat(positionElement.getAttributeValue("y"));
    		float positionZ = Float.parseFloat(positionElement.getAttributeValue("z"));
    		//-------------------------------------------------------------
    		// Rotational pivot data.
    		//-------------------------------------------------------------
    		XMLElement rotationElement = boneElement.getChild("rotation");
    		float rotationAngle = Float.parseFloat(rotationElement.getAttributeValue("angle"));
    		XMLElement rotationAxisElement = rotationElement.getChild("axis");
    		float rotationX = Float.parseFloat(rotationAxisElement.getAttributeValue("x"));
    		float rotationY = Float.parseFloat(rotationAxisElement.getAttributeValue("y"));
    		float rotationZ = Float.parseFloat(rotationAxisElement.getAttributeValue("z"));
    		//-------------------------------------------------------------
    		// Feed this data into a SimpleBone class, for adding to 
    		// the skeleton.
    		//-------------------------------------------------------------
    		SimpleBone bone = new SimpleBone(id,name);
    		bone.setLocalTranslation(positionX,positionY,positionZ);
    		
    		//Matrix matrix = new Matrix();
    		//matrix.rotateAxis(new SimpleVector(rotationX,rotationY,rotationZ),rotationAngle);
    		//SimpleVector vec = SkeletalUtils.deriveAngles(matrix);
    		
    		bone.setLocalRotation(rotationX,rotationY,rotationZ,rotationAngle);
    		//bone.setLocalRotationAngle(rotationAngle);
    		//Add to bone array.
    		bonesArray[i] = bone;
    		Logger.log("New bone(" + name + ")",Logger.MESSAGE);
    		
    		i++;
    	}
    	skeleton.setBones(bonesArray); //Set bones in skeleton;)
    	//-----------------------------------------------------------------
    	// Load the bone hierarchies.
    	//
    	// These explain which bone is connected to the other bone:).
    	//-----------------------------------------------------------------
    	Logger.log("Loading bone hierarchies...",Logger.MESSAGE);
    	List<?> hierarchies = root.getChild("bonehierarchy").getChildren("boneparent");
    	for(Object o : hierarchies)
    	{
    		XMLElement boneParent = (XMLElement)o;
    		
    		String boneName       = boneParent.getAttributeValue("bone");
    		String boneParentName = boneParent.getAttributeValue("parent");
    		//Set parent/child relationship.
    		skeleton.getBone(boneParentName).addChild(skeleton.getBone(boneName));
    	}
    	Logger.log("Bone hierarchy loading completed.",Logger.MESSAGE);
    	//-----------------------------------------------------------------
    	// Load animations.
    	//
    	// Loads track data into the bones they represent, and separates
    	// the keyframes into "rotation" and "translation".
    	//
    	// NOTE: Scale keyframes not supported currently.
    	//-----------------------------------------------------------------
    	Logger.log("Loading animation sequences.",Logger.MESSAGE);
    	
    	XMLElement animationsInfoNode = root.getChild("animations");
    	
    	if(animationsInfoNode!=null){
	    	List<?> animations = animationsInfoNode.getChildren("animation");
	    	SkeletalAnimation[] animationsArray = new SkeletalAnimation[animations.size()];
	    	i = 0;
	    	for(Object o : animations)
	    	{
	    		XMLElement animationElement = (XMLElement)o;
	    		
	    		//-------------------------------------------------------------
	    		// Create animation reference.
	    		// 
	    		// This is just a small class with a name and length, used to
	    		// identify specific animations.
	    		//-------------------------------------------------------------
	    		SkeletalAnimation animation = new SkeletalAnimation();
	    		animation.setName(animationElement.getAttributeValue("name"));
	    		animation.setLength(Float.parseFloat(animationElement.getAttributeValue("length")));
	    		animationsArray[i] = animation;
	    		//-------------------------------------------------------------
	    		// Load animation tracks.
	    		//
	    		// The animation track data will be passed to the 
	    		// corresponding bone(includes keyframe data).
	    		//-------------------------------------------------------------
	    		List<?> animationTracks = animationElement.getChild("tracks").getChildren("track");
	        	for(Object o2 : animationTracks)
	        	{
	        		XMLElement animationTrackElement = (XMLElement)o2;
	        		
	        		String boneName = animationTrackElement.getAttributeValue("bone");
	        		SimpleBone bone = skeleton.getBone(boneName);
	        		
	        		//Load keyframe data into bone.
	        		bone.setMaxAnimations(animations.size());
	        		loadTrackKeyframes(i,bone,animationTrackElement);
	        		
	        		Logger.log("New animation track(" + bone + ")",Logger.MESSAGE);
	        	}
	        	i++;
	    	}
	    	skeleton.setAnimations(animationsArray);
    	}else{
    		Logger.log("No animation sequence in file!",Logger.MESSAGE);
    		SkeletalAnimation[] animationsArray = new SkeletalAnimation[0];
    		skeleton.setAnimations(animationsArray);
    	}
    	skeleton.build();
    	Logger.log("Animation sequence loading completed.",Logger.MESSAGE);
    	Logger.log("Skeletal data loading completed.",Logger.MESSAGE);
    	return skeleton;
	}
	/**
	 * Loads keyframe data into the selected bone.
	 * 
	 * Keyframe data is split into two groups - "rotation" and "translation".
	 * 
	 * @param animationTrack
	 * @param animationTrackElement
	 */
	private static void loadTrackKeyframes(int animationIndex,SimpleBone bone,
			XMLElement animationTrackElement)
	{
		Logger.log("Loading track keyframes..",Logger.MESSAGE);
		
		List<?> keyframes = animationTrackElement.getChild("keyframes").getChildren("keyframe");
		RotationKeyframe[]    rotationKeyframes    = new RotationKeyframe[keyframes.size()];
		TranslationKeyframe[] translationKeyframes = new TranslationKeyframe[keyframes.size()];
		int i = 0;
		for(Object o : keyframes)
    	{
			XMLElement keyframeElement = (XMLElement)o;
    		
    		//-------------------------------------------------------------
    		// Get the time.
    		//
    		// This is the exact time this keyframe was snapshotted in the
    		// sequence of animation.
    		//-------------------------------------------------------------
    		float time = Float.parseFloat(keyframeElement.getAttributeValue("time"));
    		//-------------------------------------------------------------
    		// Positional data.
    		// The translation at this keyframe.
    		//-------------------------------------------------------------
    		XMLElement translateElement = keyframeElement.getChild("translate");
    		float translateX = Float.parseFloat(translateElement.getAttributeValue("x"));
    		float translateY = Float.parseFloat(translateElement.getAttributeValue("y"));
    		float translateZ = Float.parseFloat(translateElement.getAttributeValue("z"));
    		//-------------------------------------------------------------
    		// Rotational pivot data.
    		//
    		// Converted to a matrix, before use as a keyframe:)
    		//-------------------------------------------------------------
    		XMLElement rotateElement = keyframeElement.getChild("rotate");
    		float rotateAngle = Float.parseFloat(rotateElement.getAttributeValue("angle"));
    		XMLElement rotateAxisElement = rotateElement.getChild("axis");
    		float rotateX = Float.parseFloat(rotateAxisElement.getAttributeValue("x"));
    		float rotateY = Float.parseFloat(rotateAxisElement.getAttributeValue("y"));
    		float rotateZ = Float.parseFloat(rotateAxisElement.getAttributeValue("z"));
    		//-------------------------------------------------------------
    		// Scale data.
    		//
    		// NOTE: We do not support this data as yet.
    		//-------------------------------------------------------------
    		//org.jdom.Element scaleElement = keyframeElement.getChild("scale");
    		//float scaleX = Float.parseFloat(scaleElement.getAttributeValue("x"));
    		//float scaleY = Float.parseFloat(scaleElement.getAttributeValue("y"));
    		//float scaleZ = Float.parseFloat(scaleElement.getAttributeValue("z"));
    		//-------------------------------------------------------------
    		RotationKeyframe    rotationKeyframe    = new RotationKeyframe();
    		TranslationKeyframe translationKeyframe = new TranslationKeyframe();
    		
    		rotationKeyframe.time = time;
    	//	Matrix matrix = new Matrix();
    		//-rotateY is to fix a stupid axis bug.
        	//matrix.rotateAxis(new SimpleVector(rotateX,rotateY,rotateZ), rotateAngle);
        	
        ///////	SimpleVector rot = SkeletalUtils.deriveAngles(matrix);
//        	matrix = new Matrix();
//        	matrix.rotateX(rot.x);
//        	matrix.rotateY(rot.y);
//        	matrix.rotateZ(rot.z);
        	
        	
        	//matrix.translate(bone.getLocalTranslation());
        	////rotationKeyframe.rotation[0] = rot.x;
        	//////rotationKeyframe.rotation[1] = rot.y;
        	//////rotationKeyframe.rotation[2] = rot.z;
        	rotationKeyframe.quat = new Quaternion(rotateX,rotateY,rotateZ,rotateAngle);
    		
        	translationKeyframe.time = time;
    		translationKeyframe.setTranslation(translateX,translateY,translateZ);
    		
    		rotationKeyframes[i]    = rotationKeyframe;
    		translationKeyframes[i] = translationKeyframe;
    		
    		i++;
    	}
    	bone.setKeyframes(animationIndex,rotationKeyframes, translationKeyframes);
    	
    	Logger.log("Track keyframe loading completed.",Logger.MESSAGE);
	}
	/**
	 * Loads the vertex->bone(s) assignments.
	 *  
	 * @param obj
	 * @param submesh
	 */
	private static void loadBoneVertexAssignments(int vertexNum, SimpleSkeleton skeleton, SkeletalObject3D obj, XMLElement submesh)
	{
		Logger.log("Loading vertex->bone assignments...",Logger.MESSAGE);
		
		VertexAssignment[] vertexAssignments = new VertexAssignment[vertexNum];
		//for(VertexAssignment v : vertexAssignments)
		//	v = new VertexAssignment();
		
		List<?> assignments = submesh.getChild("boneassignments").getChildren("vertexboneassignment");
		int i = 0;
		for(Object o : assignments)
		{
			XMLElement vertexAssignmentElement = (XMLElement)o;
			
			int  vertexIndex = Integer.parseInt(vertexAssignmentElement.getAttributeValue("vertexindex"));
			int  boneIndex   = Integer.parseInt(vertexAssignmentElement.getAttributeValue("boneindex"));
			float weight     = Float.parseFloat(vertexAssignmentElement.getAttributeValue("weight"));
			
			if(vertexAssignments[vertexIndex] == null)
				vertexAssignments[vertexIndex] = new VertexAssignment();
			
			VertexAssignment vertexAssignment = vertexAssignments[vertexIndex];
			vertexAssignment.addBone(skeleton.getBone(boneIndex),weight);
			
			i++;
		}
		obj.setVertexAssignments(vertexAssignments);
		Logger.log("Vertex->bone assignment completed.",Logger.MESSAGE);
	}
	
	/**
	 * Loads the vertex->bone(s) assignments.
	 *  
	 * @param obj
	 * @param submesh
	 */
	private static void loadBoneVertexAssignmentsMod(int[][] faces, OGREVertex[] vertexes, int vertexNum, SimpleSkeleton skeleton, SkeletalObject3D obj, XMLElement submesh)
	{
		Logger.log("Loading vertex->bone assignments...", Logger.MESSAGE);
		
		
		Logger.log("Loading vertex->bone assignments... faces: "+faces.length, Logger.MESSAGE);
		Logger.log("Loading vertex->bone assignments... vertexes: "+vertexes.length, Logger.MESSAGE);
		Logger.log("Loading vertex->bone assignments... faces geram vertexes: "+(faces.length*3), Logger.MESSAGE);
		Logger.log("Loading vertex->bone assignments... vertexNum: "+vertexNum, Logger.MESSAGE);
		
		VertexAssignment[] vertexAssignments = new VertexAssignment[vertexNum];
		//for(VertexAssignment v : vertexAssignments)
		//	v = new VertexAssignment();
		
		List<?> assignments = submesh.getChild("boneassignments").getChildren("vertexboneassignment");
		int i = 0;
		for(Object o : assignments)
		{
			XMLElement vertexAssignmentElement = (XMLElement)o;
			
			int  original_vertexIndex = Integer.parseInt(vertexAssignmentElement.getAttributeValue("vertexindex"));
			int  boneIndex   = Integer.parseInt(vertexAssignmentElement.getAttributeValue("boneindex"));
			float weight     = Float.parseFloat(vertexAssignmentElement.getAttributeValue("weight"));
			
			for(int j = 0; j < faces.length; j++)
    		{
    			for (int h = 0; h < 3; h++)
    			{
    				int indexV_h = faces[j][h];
    				
    				if(indexV_h == original_vertexIndex){
	    				int vertexIndex = j * 3 + h;
				
						if(vertexAssignments[vertexIndex] == null)
							vertexAssignments[vertexIndex] = new VertexAssignment();
						
						VertexAssignment vertexAssignment = vertexAssignments[vertexIndex];
						vertexAssignment.addBoneMod(skeleton.getBone(boneIndex), weight);
    				}
    			}
    		}
			
			i++;
		}
		obj.setVertexAssignments(vertexAssignments);
		Logger.log("Vertex->bone assignment completed.",Logger.MESSAGE);
	}
	
	private static String getStringFromStream(InputStream in)
	{
		String input = "";
		try
		{
			StringBuffer out = new StringBuffer();
	    	byte[] b         = new byte[4096];
	    
	    	for (int n; (n = in.read(b)) != -1;)
	    	{
	        	out.append(new String(b, 0, n));
	    	}
	    	input = out.toString();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
		Logger.log("Loaded from InputStream(" + input.length() + "bytes)",Logger.MESSAGE);
		return input;
	}
}
