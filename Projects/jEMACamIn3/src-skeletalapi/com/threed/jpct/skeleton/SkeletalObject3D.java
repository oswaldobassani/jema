package com.threed.jpct.skeleton;

import com.threed.jpct.IVertexController;
import com.threed.jpct.Mesh;
import com.threed.jpct.Object3D;

/**
 * An implementation of an object3D that supports a skeleton.
 * 
 * @author mcgreevyj
 */

public class SkeletalObject3D extends Object3D
{
	protected SkeletalVertexController vertexController = new SkeletalVertexController(this);
	protected SimpleSkeleton           skeleton;
	protected VertexAssignment[]       vertexAssignments;
	
	public SkeletalObject3D(int arg0)
	{
		super(arg0);
	}
	/**
	 * Sets the skeleton of the object.
	 * This cannot be changed easily, and should only
	 * really be called from a 3d file loader.
	 * 
	 * @param skeleton
	 */
	public void setSkeleton(SimpleSkeleton skeleton)
	{
		this.skeleton = skeleton;
	}
	/**
	 * Creates mesh-keyframe animations from the attached
	 * skeleton.
	 * Animation can be used as with all mesh animations in jPCT.
	 * 
	 * TODO: Make this work again. Doesn't work!
	 */
	public void createAnimations()
	{
//		vertexController.obj = this;
//		vertexController.skeleton = skeleton;
//		//vertexController.assignedVertexes = assignedVertexes;
//		
//		Hashtable<String,SkeletalAnimation> animations = skeleton.animations;
//		//Make room for the animations.
//		Animation meshAnimation = null;
//		
//		Mesh srcMesh = getMesh();
//		
//		
//		for(Enumeration<SkeletalAnimation> e = animations.elements(); e.hasMoreElements();)
//		{
//			SkeletalAnimation animation = e.nextElement();
//			
//			if(meshAnimation == null)
//			{
//				meshAnimation = new Animation(
//						animation.tracks.get(0).keyframes.size() * animations.size());
//				meshAnimation.setClampingMode(Animation.USE_CLAMPING);
//			}
//			//Create subsequence in mesh animation.
//			meshAnimation.createSubSequence(animation.name);
//			
//			Object3D tmpObj = new Object3D(this);
//			vertexController.animationName = animation.name;
//			int i = 0,num = animation.tracks.get(0).keyframes.size();//dirty hack?
//			while(i<num)
//			{
//				vertexController.keyframeCtr = i;
//				
//				tmpObj.setMesh(srcMesh.cloneMesh(Mesh.DONT_COMPRESS));
//				tmpObj.build();
//				Mesh mesh = tmpObj.getMesh();
//				
//				mesh.setVertexController(vertexController, IVertexController.ALTER_SOURCE_MESH);
//				
//				mesh.applyVertexController();
//				
//				mesh.removeVertexController();
//				
//				mesh.compress();
//				mesh.strip();
//				
//				meshAnimation.addKeyFrame(mesh);
//				i++;
//			}
//			vertexController.keyframeCtr = 0;
//		}
//		setAnimationSequence(meshAnimation);
	}

	/**
	 * Sets the vertex assignments of this object.
	 * These assignments are used by the skeleton to warp
	 * in response to movements to the bones they are bound to.
	 * 
	 * @param vertexAssignments Array of vertex assignments.
	 */
	public void setVertexAssignments(VertexAssignment[] vertexAssignments)
	{
		this.vertexAssignments = vertexAssignments;
	}
	/**
	 * Returns the skeleton this object is connected to.
	 * 
	 * @return Skeleton connected to object.
	 */

	public SimpleSkeleton getSkeleton()
	{
		return skeleton;
	}
	/**
	 * Undo's the "rest position" of the vertices, to remove the requirement
	 * of doing it every keyframe.
	 */
	public void applyBoneInverseMatrices()
	{
		VertexBoneInverter vertexBoneInverter = new VertexBoneInverter(this);
		Mesh mesh = getMesh();
		
		mesh.setVertexController(vertexBoneInverter, 
				                 IVertexController.ALTER_SOURCE_MESH);
		mesh.applyVertexController();
		mesh.removeVertexController();
		//Set vertex controller to our skeletal animator controller:)
		mesh.setVertexController(vertexController,IVertexController.PRESERVE_SOURCE_MESH);
		
	}
	/**
	 * Restarts the current skeletal animation sequence.
	 */
	public void restartAnimation()
	{
		skeleton.restartAnimation();
	}
	/**
	 * Advances the animation of the model.
	 * 
	 * @see SimpleSkeleton.advanceAnimation();
	 */
	public void advanceAnimation()
	{
		skeleton.advanceAnimation();
		Mesh mesh = getMesh();
		//mesh.setVertexController(vertexController,IVertexController.ALTER_SOURCE_MESH);
		mesh.applyVertexController();
	}
}