package com.threed.jpct.skeleton.loader;

/**
 * Encapsulates a vertex buffer as described in the 
 * Ogre XML Format.
 * 
 * @author mcgreevyj
 */

public class OGREVertexBuffer
{
	//List of vertices.
	public OGREVertex[] vertices;
	public boolean      positions,normals;
	public int          textureCoords,textureCoordDimensions;
}
