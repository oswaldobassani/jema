package com.threed.jpct.skeleton;

import java.util.ArrayList;
import java.util.List;

import com.threed.jpct.skeleton.util.Timer;

/**
 * A simple implementation of a skeleton.
 * The skeleton references, and controls the bones,
 * and their animation keyframes.
 * 
 * @author mcgreevyj
 */

public class SimpleSkeleton
{
	protected SimpleBone[]        bones;
	protected SkeletalAnimation[] animations;
	protected Timer timer = new Timer();
	//The index of the current animation.
	protected int currentAnimation = 0;
	
	/**
	 * Sets the current animation sequence directly,
	 * by its index.
	 * 
	 * @param animation Index of animation sequence.
	 */
	public void setCurrentAnimation(int animation)
	{
		this.currentAnimation = animation;
	}
	/**
	 * Sets current animation sequence.
	 * Searches for animation index to use from
	 * the name.
	 * 
	 * @param name Name of animation to set.
	 */
	public void setCurrentAnimation(String name)
	{
		int i = 0;
		for(SkeletalAnimation animation : animations)
		{
			if(animation.name.equals(name))
				this.currentAnimation = i;
			i++;
		}
	}
	/**
	 * Sets the bone array of this skeleton.
	 * 
	 * TODO: Sort bones here! Sort by parent hierarchy.
	 * 
	 * @param bones	Array of bones.
	 */
	public void setBones(SimpleBone[] bones)
	{
		this.bones = bones;
	}
	/**
	 * Set the animation array.
	 * The animation array does not contain animation keyframes,
	 * but provides a way to convert from name to index,
	 * and stores the animation lengths.
	 * 
	 * @param animations	Array of animation definitions
	 */
	public void setAnimations(SkeletalAnimation[] animations)
	{
		this.animations = animations;
	}
	
	/*
	 * Extra - faltava!
	 */
	public SkeletalAnimation[] getAnimations() {
		return animations;
	}
	
	/**
	 * Returns a bone with the same name, or 
	 * null if nothing is found.
	 * 
	 * @param name	Name of bone to search for
	 * @return	Bone with the same name
	 */
	public SimpleBone getBone(String name)
	{
		for(SimpleBone bone : bones)
		{
			if(bone.name.equals(name))
				return bone;
		}
		return null;
	}
	/**
	 * Returns a bone with the same id, or
	 * null if nothing is found.
	 * 
	 * @param id	Id of bone to search for
	 * @return	Bone with the same id
	 */
	public SimpleBone getBone(int id)
	{
		for(SimpleBone bone : bones)
		{
			if(bone.id == id)
				return bone;
		}
		return null;
	}
	/**
	 * Initializes the skeleton. Must be called before use.
	 */
	public void build()
	{
		setupBones();
	}
	private void sortBones()
	{
		//-----------------------------------------------------------------
		// Put bones into an arraylist.
		//-----------------------------------------------------------------
		List<SimpleBone> boneList = new ArrayList<SimpleBone>(bones.length);
		int i = 0;
		for(SimpleBone bone : bones)
		{
			boneList.add(bone);
			
			i++;
		}
		//-----------------------------------------------------------------
		// Loop through bones, sorting them into parent first order;)
		//-----------------------------------------------------------------
		sortBonesPass(boneList);
		
		for(i = 0; i < bones.length; i++)
		{
			SimpleBone bone = boneList.get(i);
			System.out.println(bone.name + "->" + ((bone.parent!=null)?bone.parent.name:""));
		}
		bones = boneList.toArray(bones);
	}
	private void sortBonesPass(List<SimpleBone> boneList)
	{
		for(int i = 0; i < bones.length; i++)
		{
			SimpleBone bone = boneList.get(i);
			if(bone.parent != null)
			{
				int parentIndex = getBoneParentIndex(bone.parent,boneList);
				
				if(parentIndex > i)
				{
					boneList.remove(parentIndex);
					boneList.add(i,bone.parent);
					
					sortBonesPass(boneList);
					break;
				}
			}
		}
	}
	private int getBoneParentIndex(SimpleBone parent,List<SimpleBone> boneList)
	{
		for(int i = 0; i < bones.length; i++)
		{
			SimpleBone bone = boneList.get(i);
			if(bone == parent)
			{
				return i;
			}
		}
		return -1; //Never happen. Please;)
	}
	/**
	 * Initializes the skeleton's bones, by setting up their
	 * transformation matrices.
	 */
	private void setupBones()
	{
		sortBones();
		
		for (SimpleBone bone : bones)
		{
			bone.matrixRelative.setRotationQuaternion(bone.localRotation);
			bone.matrixRelative.setTranslation(bone.localTranslation);
			
			if (bone.parent != null)
		    {
				//=========================================================
				// If bone has a parent, apply its rotation matrix to
				// the child bone.
				//=========================================================
				bone.matrixAbsolute.set(bone.parent.matrixAbsolute.matrixData);
				//---------------------------------------------------------
				// Inherit the absolute transformation from the parent 
				// bone, adding it to this child bone's relative 
				// transformation, to get this bone's absolute 
				// transformation.
				//---------------------------------------------------------
		    	bone.matrixAbsolute.postMultiply(bone.matrixRelative);
		    }
		    else
		    {
		    	//========================================================
		    	// This is a parent bone.
		    	// We do not inherit any transformations,
		    	// and do not have any parent rotation matrices to apply.
		    	//========================================================
		        //bone.matrixAbsolute = new SimpleVector(bone.matrixRelative);
		    	bone.matrixAbsolute.set(bone.matrixRelative.matrixData);
		    }
		}
	}
	/**
	 * Restarts the animation sequence.
	 * Bone keyframe counters are reset, in addition
	 * to the timer.
	 */
	
	public void restartAnimation()
	{
		for(SimpleBone bone : bones)
		{
		      bone.currentRotationKeyframe    = 0;
		      bone.currentTranslationKeyframe = 0;
		      bone.matrixFinal.set(bone.matrixAbsolute.matrixData);
		}
		//Resynchronize the timestamp.
		timer.reset();
	}
	/**
	 * Advances the animation sequence.
	 * The faster this method is called, the higher the 
	 * fidelity of the animation.
	 * 
	 * The keyframes are self timed, and will not be
	 * made faster by more calls to this method.
	 */
	public void advanceAnimation()
	{
		if(currentAnimation>=animations.length || currentAnimation<0) return;

		SkeletalAnimation animation = animations[currentAnimation];
	
		//=================================================================
		// Ensure that the animation time has not been exceeded.
		// If so, restart animation sequence, or clip to end time,
		// if the animation is non-looping.
		//================================================================
		double time = timer.getTime();

		if (time > animation.length)
		{
			if (animation.looping)
			{
				restartAnimation();
				time = 0;
			}
			else
			{
		        time = animation.length;
			}
		}
		
		if(false) System.out.println(" Anima Time: "+time+"/"+animation.length);
		
		//================================================================
		// Calculate the final transform of all bones at this time
		// in the animation.
		//
		// Inbetween keyframes are interpolated for smoother transition.
		//================================================================
		for( int i = 0; i < bones.length; i++)
		{
			float[] transVec = new float[3];
		    SimpleBone   bone     = bones[i];
		    Matrix transform = new Matrix();
		    int frame;
		    
		    if (bone.rotationKeyframes == null && 
			        bone.translationKeyframes == null)
		    {
		    	continue;
		    }
		    
		    if (currentAnimation>=bone.rotationKeyframes.length && 
		    		currentAnimation>=bone.translationKeyframes.length)
			{
		    	continue;
			}
		    
		    //------------------------------------------------------------
		    // If the bone has no keyframes, then skip it.
		    //------------------------------------------------------------
		    if (bone.rotationKeyframes[currentAnimation] == null && 
		        bone.translationKeyframes[currentAnimation] == null)
		    {
		    	bone.matrixFinal.set(bone.matrixAbsolute.matrixData);
		        continue;
		    }
		    //------------------------------------------------------------ 
		    // Ensure we are at the correct keyframe for this time
		    // in the animation.
		    //------------------------------------------------------------
		    frame = bone.currentTranslationKeyframe;
		    while(frame < bone.translationKeyframes[currentAnimation].length && 
		         bone.translationKeyframes[currentAnimation][frame].time < time)
		    {
		    	frame++;
		    }
		    bone.currentTranslationKeyframe = frame;
		    //------------------------------------------------------------
		    // Find the correct translation vector for this time in the
		    // animation, for this bone.
		    //
		    // If the frame is at the start, or the end, then the
		    // vector is taken directly from the keyframes.
		    // However, if it is neither, the vector is interpolated
		    // from the current keyframe, and the previous keyframe.
		    //------------------------------------------------------------
		    if (frame == 0)
		    {
		    	float[] vec = bone.translationKeyframes[currentAnimation][0].translation;
		    	transVec[0] = vec[0];
		    	transVec[1] = vec[1];
		    	transVec[2] = vec[2];
		    }
		    else if (frame == bone.translationKeyframes[currentAnimation].length)
		    {
		    	float[] vec = bone.translationKeyframes[currentAnimation][frame-1].translation;
		    	transVec[0] = vec[0];
		    	transVec[1] = vec[1];
		    	transVec[2] = vec[2];
		    }
		    else
		    {
		    	if(frame>=bone.translationKeyframes[currentAnimation].length){
		    		// Caso a nova animacao seja menor.
		    		continue;
		    	}
		    	TranslationKeyframe curFrame  = bone.translationKeyframes[currentAnimation][frame];
		    	TranslationKeyframe prevFrame = bone.translationKeyframes[currentAnimation][frame-1];
		    	float timeDelta = (curFrame.time)-(prevFrame.time);
		        float interpValue = (float)(time-(prevFrame.time))/timeDelta;

		        float[] curTranslation  = curFrame.translation;
		        float[] prevTranslation = prevFrame.translation;
		          
		        transVec[0] = prevTranslation[0] + (curTranslation[0] - prevTranslation[0]) * interpValue;
		        transVec[1] = prevTranslation[1] + (curTranslation[1] - prevTranslation[1]) * interpValue;
		        transVec[2] = prevTranslation[2] + (curTranslation[2] - prevTranslation[2]) * interpValue; 
		    }
		    //-------------------------------------------------------------
		    // Ensure we are at the correct rotational keyframe for this
		    // time in the animation sequence.
		    //-------------------------------------------------------------
		    frame = bone.currentRotationKeyframe;
		    while(frame < bone.rotationKeyframes[currentAnimation].length && 
		         bone.rotationKeyframes[currentAnimation][frame].time < time)
		    {
		    	frame++;
		    }
		    bone.currentRotationKeyframe = frame;
		    //-------------------------------------------------------------
		    // Same as above, but for the rotational keyframes instead.
		    //-------------------------------------------------------------
		    if (frame == 0)
		    {
		    	transform.setRotationQuaternion(bone.rotationKeyframes[currentAnimation][0].quat);
		    }
		    else if (frame == bone.rotationKeyframes[currentAnimation].length)
		    {
		         transform.setRotationQuaternion(bone.rotationKeyframes[currentAnimation][frame-1].quat);
		    }
		    else
		    {
		    	RotationKeyframe curFrame  = bone.rotationKeyframes[currentAnimation][frame];
		    	RotationKeyframe prevFrame = bone.rotationKeyframes[currentAnimation][frame-1];
		      
		    	float timeDelta   = (curFrame.time)-(prevFrame.time);
		        float interpValue = (float)(time-(prevFrame.time))/timeDelta;

				Quaternion qPrev  = prevFrame.quat;
		        Quaternion qCur   = curFrame.quat;
		        Quaternion qFinal = new Quaternion(qPrev,qCur,interpValue);
		        transform.setRotationQuaternion(qFinal);
		    }
		    //-------------------------------------------------------------
		    // Apply the transformation vector to the relativeFinal
		    // transformation of this bone.
		    //-------------------------------------------------------------
		    transform.setTranslation(transVec);
		    Matrix relativeFinal = new Matrix();
		    relativeFinal.set(bone.matrixRelative.matrixData);
		    relativeFinal.postMultiply(transform);
		    
		    if(bone.parent == null)
		    {
		    	//---------------------------------------------------------
		    	// We are a parent bone, so just use the relative final
		    	// as the final transform.
		    	// There are no rotations, or transformations to inherit.
		    	//---------------------------------------------------------
		    	bone.matrixFinal.set(relativeFinal.getMatrix());
		    }
		    else
		    {
		    	//---------------------------------------------------------
		    	// We are a child bone, so inherit any rotations,
		    	// and inherit the parent bone's final transformation
		    	// to get our own.
		    	//---------------------------------------------------------
		    	bone.matrixFinal.set(bone.parent.matrixFinal.getMatrix());
		    	bone.matrixFinal.postMultiply(relativeFinal);
		    }
		}
	}
	/**
	 * Return skeleton's bone array.
	 * @return	Array of bones.
	 */
	public SimpleBone[] getBones()
	{
		return bones;
	}
}